package haus.man.hyperbridge.server.parcel;

import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.Parcelable;

public class SendLight extends Parcel {
    public SendLight() {
        super("sendlight");
    }

    @Override
    public Parcelable respond() {
        return null;
    }
}
