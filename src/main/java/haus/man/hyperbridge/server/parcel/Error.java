package haus.man.hyperbridge.server.parcel;

import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.Parcelable;

public class SetColor extends Parcel {
    public SetColor() {
        super("setcolor");
    }

    @Override
    public Parcelable respond() {
        return null;
    }
}
