package haus.man.hyperbridge.server.parcel;

import haus.man.hyperbridge.server.LightData;
import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.Parcelable;

public class SendLight extends Parcel {
    private LightData light;

    public SendLight() {
        super("sendlight");
    }

    public SendLight(LightData data)
    {
        this();
        this.light = data;
    }

    @Override
    public Parcelable respond() {
        return null;
    }
}
