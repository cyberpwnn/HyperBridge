package haus.man.hyperbridge.api;

import haus.man.hyperbridge.HyperBridge;
import ninja.bytecode.shuriken.collections.KList;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface ILightHouse {
    public static ILightHouse get()
    {
        return HyperBridge.server;
    }

    public KList<ILight> getAllLights();

    public int getBridgeCount();

    public default double getWattage()
    {
        return (9.5 * getBridgeCount()) + stream().mapToDouble(ILight::getWattage).sum();
    }

    public default double getTotalWattHours()
    {
        return stream().mapToDouble(ILight::getTotalWattHours).sum();
    }

    public default Stream<ILight> stream()
    {
        return getAllLights().stream();
    }

    public default void forEachLight(Consumer<ILight> v)
    {
        getAllLights().forEach(v);
    }

    public default void forEachLight(Predicate<ILight> p, Consumer<ILight> v)
    {
        for(ILight i : getAllLights())
        {
            if(p.test(i))
            {
                v.accept(i);
            }
        }
    }

    default ILight getLight(String id)
    {
        for(ILight i : getAllLights())
        {
            if(i.getId().equalsIgnoreCase(id))
            {
                return i;
            }
        }

        return null;
    }
}
