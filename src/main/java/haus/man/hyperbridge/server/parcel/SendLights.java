package haus.man.hyperbridge.server.parcel;

import ninja.bytecode.shuriken.collections.KList;
import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.Parcelable;

public class SendLights extends Parcel {
    private KList<String> lights;

    public SendLights(KList<String> lights) {
        super("sendlights");
        this.lights = lights;
    }

    public SendLights() {
        this(new KList<>());
    }

    @Override
    public Parcelable respond() {
        return null;
    }
}
