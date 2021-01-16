package haus.man.hyperbridge.server.parcel;

import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.ParcelRequest;
import ninja.bytecode.shuriken.web.Parcelable;

@ParcelRequest
public class Ping extends Parcel {
    public Ping() {
        super("ping");
    }

    @Override
    public Parcelable respond() {
        return new Pong();
    }
}
