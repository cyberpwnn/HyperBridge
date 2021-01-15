package haus.man.hyperbridge.server.parcel;

import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.Parcelable;

public class GetBridges extends Parcel {
    public GetBridges() {
        super("getbridges");
    }

    @Override
    public Parcelable respond() {
        return null;
    }
}
