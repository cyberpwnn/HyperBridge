package haus.man.hyperbridge.core;

import java.util.List;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;

import haus.man.hyperbridge.api.ILight;
import haus.man.hyperbridge.api.ILightGroup;
import haus.man.hyperbridge.api.ILightHouse;
import haus.man.hyperbridge.server.HyperWebserver;
import lombok.Data;
import ninja.bytecode.shuriken.collections.KList;
import ninja.bytecode.shuriken.collections.KMap;
import ninja.bytecode.shuriken.execution.J;
import ninja.bytecode.shuriken.execution.Looper;
import ninja.bytecode.shuriken.logging.L;
import ninja.bytecode.shuriken.math.M;

@Data
public class HyperBridgeServer implements PHSDKListener, ILightHouse
{
	private PHHueSDK sdk;
	private ConsoleManager console;
	private KMap<String, HyperNode> nodes = new KMap<>();
	private final KMap<String, HyperLight> lights = new KMap<>();
	private HyperState state = HyperState.load();
	private KList<Runnable> queue = new KList<>();
	private Looper saver;
	private Looper ticker;
	private boolean closed = false;
	private HyperWebserver ws;

	public HyperBridgeServer()
	{
		J.a(() -> ws = new HyperWebserver());
		sdk = PHHueSDK.create();
		sdk.getNotificationManager().registerSDKListener(this);
		saver = new Looper()
		{
			@Override
			protected long loop()
			{
				state.saveIfDirty();
				return 10000;
			}
		};
		ticker = new Looper()
		{
			@Override
			protected long loop()
			{
				synchronized (lights)
				{
					int m = 0;

					for(HyperLight i : lights.v())
					{
						if(i.push())
						{
							m++;
						}

						if(m >= Math.max(1, HyperConfig.get().getBridgeMaxLIOPS()/4))
						{
							break;
						}
					}
				}

				return 250;
			}
		};

		saver.start();
		ticker.start();
		console = new ConsoleManager();
		L.i("Looking for new Bridges");
		connectToBridges();
	}

	@Override
	public ILightGroup createGroup(String name) {
		HyperGroup g = new HyperGroup();
		g.setName(name);
		state.getGroups().add(g);
		state.setDirty(true);
		return g;
	}

	@Override
	public void deleteGroup(String id) {
		state.getGroups().removeIf((i) -> i.getId().equals(id));
		state.setDirty(true);
	}

	@Override
	public KList<ILightGroup> getAllGroups() {
		return state.getGroups().convert((i) -> i);
	}

	@Override
	public ILightGroup getGroup(String id) {
		for(ILightGroup i : getAllGroups())
		{
			if(i.getId().equals(id))
			{
				state.setDirty(true);
				return i;
			}
		}

		return null;
	}

	public KList<ILight> getAllLights()
	{
		KList<ILight> k = new KList<>();
		k.addAll(getLights().values());
		return k;
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
		state.save();

		synchronized (lights)
		{
			for(HyperLight i : lights.v())
			{
				i.save(true);
			}
		}

		sdk.getNotificationManager().unregisterSDKListener(this);
	}

	public void refresh()
	{
		synchronized (lights)
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
					i.setLast(M.ms());
					return;
				}
			}

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

	@Override
	public int getBridgeCount() {
		return nodes.size();
	}
}
