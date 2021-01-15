package haus.man.hyperbridge.server.parcel;

import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.Parcelable;

public class OK extends Parcel {
    public OK() {
        super("ok");
    }

    @Override
    public Parcelable respond() {
        return null;
    }
}
