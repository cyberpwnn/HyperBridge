package haus.man.hyperbridge.core;

import java.awt.Color;
import java.io.File;

import com.google.gson.Gson;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLight.PHLightColorMode;
import com.philips.lighting.model.PHLightState;

import haus.man.hyperbridge.api.ILight;
import lombok.Data;
import ninja.bytecode.shuriken.execution.ChronoLatch;
import ninja.bytecode.shuriken.io.IO;
import ninja.bytecode.shuriken.json.JSONObject;
import ninja.bytecode.shuriken.logging.L;
import ninja.bytecode.shuriken.math.M;

@Data
public class HyperLight implements ILight
{
	private transient HyperNode bridge;
	private transient PHLight light;
	private transient boolean dirty = false;
	private transient ChronoLatch dirtyLatch;
	private String uuid;
	private long lastChange;
	private long nextTransition;
	private double wh;
	private double w;
	private int r;
	private int g;
	private int b;
	private int a;

	public HyperLight(HyperNode bridge, PHLight light)
	{
		this.bridge = bridge;
		this.light = light;
		uuid = light.getUniqueId();
		dirty = false;
		nextTransition = HyperConfig.get().getDefaultTransitionMS();
		dirtyLatch = new ChronoLatch(1250);
		HyperLight data = tryLoad();
		lastChange = data.lastChange;
		wh = data.wh;
		w = data.w;
		r = data.r;
		g = data.g;
		b = data.b;
		a = data.a;
	}

	public HyperLight()
	{

	}

	public boolean push()
	{
		if(!dirty)
		{
			return false;
		}

		Color c = new Color(r,g,b);
		double[] normalizedToOne = new double[3];
		normalizedToOne[0] = (c.getRed() / 255D);
		normalizedToOne[1] = (c.getGreen() / 255D);
		normalizedToOne[2] = (c.getBlue() / 255D);
		lastChange = M.ms();
		w = Math.max(0.5, Math.pow(a / 256D, 2) * 8.5);
		float red = normalizedToOne[0] > 0.04045 ? (float) Math.pow((normalizedToOne[0] + 0.055) / (1.0 + 0.055), 2.4) : (float) (normalizedToOne[0] / 12.92);
		float green = normalizedToOne[1] > 0.04045 ? (float) Math.pow((normalizedToOne[1] + 0.055) / (1.0 + 0.055), 2.4) : (float) (normalizedToOne[1] / 12.92);
		float blue = normalizedToOne[2] > 0.04045 ? (float) Math.pow((normalizedToOne[2] + 0.055) / (1.0 + 0.055), 2.4) : (float) (normalizedToOne[2] / 12.92);
		float X = (float) (red * 0.649926 + green * 0.103455 + blue * 0.197109);
		float Y = (float) (red * 0.234327 + green * 0.743075 + blue * 0.022598);
		float Z = (float) (red * 0.0000000 + green * 0.053077 + blue * 1.035763);
		float x = X / (X + Y + Z);
		float y = Y / (X + Y + Z);
		PHLightState state = new PHLightState();
		state.setColorMode(PHLightColorMode.COLORMODE_XY);
		state.setBrightness(a, true);
		state.setX(x, true);
		state.setY(y, true);
		state.setSaturation((int) Math.round(255 * (M.max((double) c.getRed(), (double) c.getBlue(), (double) c.getGreen()) - M.min((double) c.getRed(), (double) c.getBlue(), (double) c.getGreen())) / M.max((double) c.getRed(), (double) c.getBlue(), (double) c.getGreen())), true);
		state.setTransitionTime((int) Math.round((double) nextTransition / 100D));
		bridge.queue(() ->
		{
			bridge.getBridge().updateLightState(light, state);
			L.v("Set " + light.getUniqueId() + " to " + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + "+" + a + "@" + nextTransition + "ms");
		});
		save(true);
		dirty = false;
		return true;
	}

	private HyperLight tryLoad()
	{
		File file = new File("HyperData/lights/" + getHyperID() + ".json");

		try
		{
			file.getParentFile().mkdirs();
		}

		catch(Throwable e)
		{

		}

		try
		{
			HyperLight l = new Gson().fromJson(IO.readAll(file), getClass());
			L.v("Loaded Existing Light State for " + l.getUuid());
			return l;
		}

		catch(Throwable e)
		{

		}

		return new HyperLight();
	}

	public String getHyperID()
	{
		return (light.getUniqueId() + light.getModelNumber()).replaceAll("\\Q:\\E", "");
	}

	public int getPreceivedBrightness()
	{
		return (int) Math.round(Math.sqrt((0.299 * Math.pow(((double) r / 255D), 2)) + (0.587 * Math.pow(((double) g / 255D), 2)) + (0.114 * Math.pow(((double) b / 255D), 2))) * 255D);
	}

	public double getWattHours()
	{
		return wh + (w * ((double) (M.ms() - lastChange) / 1000D / 60D / 60D));
	}

	@Override
	public String getId() {
		return getHyperID();
	}

	@Override
	public Color getColor() {
		return new Color(r,g,b);
	}

	@Override
	public double getBrightness() {
		return a / 255D;
	}

	public double getWattage()
	{
		return w;
	}

	@Override
	public double getTotalWattHours() {
		return wh;
	}

	@Override
	public void forceUpdate() {
		push();
	}

	@Override
	public void setColor(Color color, double brightness, int transitionMS) {
		color(color, (int) (brightness * 255D), transitionMS);
	}

	@Override
	public String getName() {
		return getLight().getName();
	}

	public HyperLight color(Color c)
	{
		return color(c, HyperConfig.get().getDefaultBrightness());
	}

	public HyperLight color(Color c, int a)
	{
		return color(c, a, HyperConfig.get().getDefaultTransitionMS());
	}

	public HyperLight color(Color c, int a, long ms)
	{
		long lt = M.ms() - lastChange;
		wh += w * ((double) lt / 1000D / 60D / 60D);
		r = c.getRed();
		g = c.getGreen();
		b = c.getBlue();
		this.a = a;
		nextTransition = ms;
		dirty = true;

		return this;
	}

	public void save()
	{
		save(false);
	}

	public void save(boolean now)
	{
		if(!now)
		{
			return;
		}

		if(dirty)
		{
			doSave();
			dirty = false;
		}
	}

	private void doSave()
	{
		File file = new File("HyperData/lights/" + getHyperID() + ".json");

		try
		{
			file.getParentFile().mkdirs();
		}

		catch(Throwable e)
		{
			e.printStackTrace();
		}

		try
		{
			IO.writeAll(file, new JSONObject(new Gson().toJson(this)));
			L.v("Saved Light State " + uuid);
		}

		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
}
