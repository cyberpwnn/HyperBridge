package haus.man.hyperbridge;

import java.util.List;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;

import lombok.Data;
import ninja.bytecode.shuriken.collections.KMap;
import ninja.bytecode.shuriken.execution.J;
import ninja.bytecode.shuriken.execution.Looper;
import ninja.bytecode.shuriken.logging.L;
import ninja.bytecode.shuriken.math.M;

@Data
public class HyperBridgeServer implements PHSDKListener
{
	private PHHueSDK sdk;
	private ConsoleManager console;
	private KMap<String, HyperNode> nodes = new KMap<>();
	private KMap<String, HyperLight> lights = new KMap<>();
	private HyperState state = HyperState.load();
	private boolean dirtyState = false;
	private Looper saver;
	private boolean closed = false;

	public HyperBridgeServer()
	{
		sdk = PHHueSDK.create();
		sdk.getNotificationManager().registerSDKListener(this);
		saver = new Looper()
		{
			@Override
			protected long loop()
			{
				if(dirtyState)
				{
					dirtyState = false;
					state.save();
					L.v("Saved HyperState");
				}

				return 30000;
			}
		};

		saver.start();
		console = new ConsoleManager();
		L.i("Looking for new Bridges");
		connectToBridges();
	}

	public double getSystemWattage()
	{
		double w = 0;

		for(HyperLight i : getLights().v())
		{
			w += i.getWattage();
		}

		return w;
	}

	public double getSystemWattHours()
	{
		double wh = 0;

		for(HyperLight i : getLights().v())
		{
			wh += i.getWattHours();
		}

		return wh;
	}

	public void close()
	{
		if(closed)
		{
			return;
		}

		closed = true;

		if(dirtyState)
		{
			dirtyState = false;
			state.save();
		}

		for(HyperLight i : lights.v())
		{
			i.save(true);
		}

		sdk.getNotificationManager().unregisterSDKListener(this);
	}

	public void refresh()
	{
		KMap<String, HyperLight> oldLights = lights.copy();
		lights.clear();

		for(HyperNode i : nodes.v())
		{
			for(PHLight j : i.getBridge().getResourceCache().getAllLights())
			{
				HyperLight light = new HyperLight(i, j);
				lights.put(light.getHyperID(), light);
			}

			rememberBridge(i.getBridge());
		}

		L.flush();

		int removed = 0;
		int added = 0;

		for(String i : lights.k())
		{
			if(!oldLights.containsKey(i))
			{
				added++;
			}
		}

		for(String i : oldLights.k())
		{
			if(!lights.containsKey(i))
			{
				removed++;
			}
		}

		L.i("Lost " + removed + " lights (bridge disconnect)");
		L.i("Discovered " + added + " more lights (bridge connect)");

		if(added > 0 || removed > 0)
		{
			L.i("Connected to " + lights.size() + " lights across " + nodes.size() + " bridges");
		}
	}

	private void connectToBridges()
	{
		J.a(() ->
		{
			for(HyperBridgeState i : state.getBridges().copy())
			{
				L.v("Attempting to connect to previous bridge: " + i.getIpa());
				connectTo(new PHAccessPoint(i.getIpa(), i.getUsr(), i.getMac()));
				J.sleep(250);
			}

			L.v("Searching for new bridges in the background");
			PHBridgeSearchManager sm = (PHBridgeSearchManager) sdk.getSDKService(PHHueSDK.SEARCH_BRIDGE);
			sm.search(true, true, true);
		});
	}

	public void onAccessPointsFound(List<PHAccessPoint> aps)
	{
		for(PHAccessPoint i : aps)
		{
			L.v("Found Hue AP: " + i.getIpAddress() + " (" + i.getBridgeId() + ")");
			L.v("Connecting to AP: " + i.getIpAddress() + " (" + i.getBridgeId() + ")");
			connectTo(i);
		}

		sdk.getAccessPointsFound().clear();
		sdk.getAccessPointsFound().addAll(aps);
	}

	private void connectTo(PHAccessPoint i)
	{
		for(HyperNode f : getNodes().v())
		{
			if(f.getBridge().getResourceCache().getBridgeConfiguration().getIpAddress().equals(i.getIpAddress()))
			{
				L.v("Not connecting to " + i.getIpAddress() + ". We're already connected.");
				return;
			}
		}

		try
		{
			sdk.connect(i);
		}

		catch(Throwable e)
		{
			L.v("Ignoring double connect error. We are still connected.");
		}
	}

	public void onAuthenticationRequired(PHAccessPoint ap)
	{
		L.w("Authentication Required for Hue AP: " + ap.getIpAddress() + " (" + ap.getBridgeId() + ")");
		sdk.startPushlinkAuthentication(ap);
		L.flush();
		L.i("=======================================================================");
		L.i("| PUSH THE BIG WHITE BUTTON FOR " + ap.getIpAddress() + " (" + ap.getBridgeId() + ")");
		L.i("=---------------------------------------------------------------------=");
		L.flush();
	}

	public void onBridgeConnected(PHBridge bridge, String id)
	{
		L.i("Hue Bridge Connected: " + bridge.getResourceCache().getBridgeConfiguration().getIpAddress() + " (" + id + ")");
		addBridge(bridge, id);
	}

	public void onCacheUpdated(List<Integer> locs, PHBridge bridge)
	{

	}

	public void onConnectionLost(PHAccessPoint ap)
	{
		L.w("Hue AP Lost Connection: " + ap.getIpAddress() + " (" + ap.getBridgeId() + ")");
		dropBridge(ap);
	}

	public void onConnectionResumed(PHBridge bridge)
	{
		L.i("Hue Bridge Reconnected: " + bridge.getResourceCache().getBridgeConfiguration().getIpAddress() + " (" + bridge.getResourceCache().getBridgeConfiguration().getIdentifier() + ")");
		addBridge(bridge);
	}

	public void onError(int errcode, String message)
	{
		L.e("Hue Error: " + errcode + " (" + message + ")");
	}

	public void onParsingErrors(List<PHHueParsingError> errs)
	{
		for(PHHueParsingError i : errs)
		{
			L.e("Hue Parsing Error: " + i.getAddress() + ": " + i.getMessage());
		}
	}

	private void addBridge(PHBridge bridge, String id)
	{
		if(nodes.containsKey(id))
		{
			nodes.get(id).close();
		}

		nodes.put(id, new HyperNode(bridge));
		refresh();
	}

	private void addBridge(PHBridge bridge)
	{
		String id = bridge.getResourceCache().getBridgeConfiguration().getIdentifier();

		addBridge(bridge, id);
	}

	private void rememberBridge(PHBridge bridge)
	{
		try
		{
			HyperBridgeState b = new HyperBridgeState();
			b.setLast(M.ms());
			b.setIpa(bridge.getResourceCache().getBridgeConfiguration().getIpAddress());
			b.setMac(bridge.getResourceCache().getBridgeConfiguration().getMacAddress());
			b.setUsr(bridge.getResourceCache().getBridgeConfiguration().getUsername());

			if(b.getIpa() == null || b.getMac() == null || b.getUsr() == null)
			{
				return;
			}

			for(HyperBridgeState i : state.getBridges())
			{
				if(i.getMac().equals(b.getMac()) && i.getIpa().equals(b.getIpa()) && i.getUsr().equals(b.getUsr()))
				{
					dirtyState = true;
					i.setLast(M.ms());
					return;
				}
			}

			dirtyState = true;
			L.i("Saved Bridge Connection: " + b.getIpa() + " (" + b.getMac() + ")");
			state.getBridges().add(b);
		}

		catch(Throwable e)
		{

		}
	}

	private void dropBridge(PHAccessPoint ap)
	{
		String id = ap.getBridgeId();

		if(nodes.containsKey(id))
		{
			nodes.get(id).close();
		}

		refresh();
	}
}
