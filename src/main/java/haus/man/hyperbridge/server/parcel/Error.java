package haus.man.hyperbridge.server.parcel;

import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.ParcelResponse;
import ninja.bytecode.shuriken.web.ParcelResponseError;
import ninja.bytecode.shuriken.web.Parcelable;

@ParcelResponse
public class Error extends Parcel {
    public Error() {
        super("error");
    }

    @Override
    public Parcelable respond() {
        return null;
    }
}
