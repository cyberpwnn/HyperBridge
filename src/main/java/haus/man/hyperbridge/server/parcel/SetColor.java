package haus.man.hyperbridge.server.parcel;

import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.Parcelable;

public class GetLight extends Parcel {
    public GetLight() {
        super("getlight");
    }

    @Override
    public Parcelable respond() {
        return null;
    }
}
