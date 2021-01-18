package haus.man.hyperbridge.api;

import haus.man.hyperbridge.core.HyperConfig;

import java.awt.*;

public interface ILight extends ILit {
    public String getId();

    public Color getColor();

    public double getBrightness();

    public double getWattage();

    public boolean isLocked();

    public void setLocked(boolean locked);

    public double getTotalWattHours();

    public default void changeHue(double percent)
    {
        Color c = getColor();
        float[] hsb = new float[3];
        Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
        double hue = hsb[0] * 360D;
        hue = (hue + (360 * percent)) % 360;
        setColor(Color.getHSBColor((float) (hue / 360D), hsb[1], hsb[2]), getBrightness());
    }

    public default void changeSaturation(double percent)
    {
        Color c = getColor();
        float[] hsb = new float[3];
        Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
        setColor(Color.getHSBColor(hsb[0], (float) Math.min(1D, Math.max(hsb[1] + percent, 0D)), hsb[2]), getBrightness());
    }

    public default void changeBrightness(double addPercent)
    {
        setBrightness(Math.min(1D, Math.max(getBrightness() + addPercent, 0D)));
    }

    public default void setBrightness(double brightness)
    {
        setColor(getColor(), brightness);
    }

    public default void setBrightness(double brightness, int transitionMS)
    {
        setColor(getColor(), brightness, transitionMS);
    }

    public void setColor(Color color, double brightness, int transitionMS);

    public default void setColor(Color color, double brightness)
    {
        setColor(color, brightness, HyperConfig.get().getDefaultTransitionMS());
    }

    public default void setColor(Color color)
    {
        setColor(color, HyperConfig.get().getDefaultBrightness());
    }

    String getName();

    void setDirty();
}
