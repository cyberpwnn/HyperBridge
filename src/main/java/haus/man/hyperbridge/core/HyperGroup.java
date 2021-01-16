package haus.man.hyperbridge.core;

import haus.man.hyperbridge.api.ILight;
import haus.man.hyperbridge.api.ILightGroup;
import haus.man.hyperbridge.api.ILightHouse;
import ninja.bytecode.shuriken.collections.KList;

import java.util.UUID;

public class HyperGroup implements ILightGroup {
    private KList<String> lights;
    private String name;
    private String id;

    public HyperGroup()
    {
        this.id = UUID.randomUUID().toString();
        name = "Unnamed Group";
        lights = new KList<>();
    }

    @Override
    public KList<ILight> getLights() {
        return lights.convert((i) -> ILightHouse.get().getLight(i));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void clearLights(ILight light) {
        lights.clear();
    }

    @Override
    public void addLight(ILight light) {
        lights.add(light.getId());
        lights.dedupe();
    }

    @Override
    public void removeLight(ILight light) {
        lights.remove(light.getId());
    }
}
