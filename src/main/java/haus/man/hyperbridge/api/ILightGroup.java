package haus.man.hyperbridge.api;

import ninja.bytecode.shuriken.collections.KList;

import java.awt.*;

public interface ILightGroup extends ILit {
    public KList<ILight> getLights();

    public String getId();

    public void setName(String name);

    public String getName();

    @Override
    default Color getColor() {
        return null;
    }

    @Override
    default double getBrightness() {
        return getLights().stream().mapToDouble(ILight::getBrightness).sum()/getLights().size();
    }

    @Override
    default double getWattage() {
        return getLights().stream().mapToDouble(ILight::getWattage).sum();
    }

    @Override
    default double getTotalWattHours() {
        return getLights().stream().mapToDouble(ILight::getTotalWattHours).sum();
    }

    @Override
    default void setColor(Color color, double brightness, int transitionMS) {
        getLights().forEach((i) -> i.setColor(color, brightness, transitionMS));
    }

    public void clearLights(ILight light);

    public void addLight(ILight light);

    public void removeLight(ILight light);

    public default void forceUpdate()
    {
        getLights().forEach(ILight::forceUpdate);
    }

    public default void addLight(String id)
    {
        addLight(ILightHouse.get().getLight(id));
    }

    public default void removeLight(String id)
    {
        removeLight(ILightHouse.get().getLight(id));
    }

    public default void changeHue(double percent)
    {
        getLights().forEach((i) -> changeHue(percent));
    }

    public default void changeSaturation(double percent)
    {
        getLights().forEach((i) -> changeSaturation(percent));
    }

    public default void changeBrightness(double percent)
    {
        getLights().forEach((i) -> changeBrightness(percent));
    }
}
