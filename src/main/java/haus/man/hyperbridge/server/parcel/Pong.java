package haus.man.hyperbridge.server.parcel;

import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.ParcelResponse;
import ninja.bytecode.shuriken.web.Parcelable;

@ParcelResponse
public class Pong extends Parcel {
    public Pong() {
        super("pong");
    }

    @Override
    public Parcelable respond() {
       return null;
    }
}
