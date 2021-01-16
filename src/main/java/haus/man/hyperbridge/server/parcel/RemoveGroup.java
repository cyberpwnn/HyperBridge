package haus.man.hyperbridge.server.parcel;

import haus.man.hyperbridge.api.ILightHouse;
import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.ParcelRequest;
import ninja.bytecode.shuriken.web.Parcelable;

@ParcelRequest
public class RemoveGroup extends Parcel {
    private String id;

    public RemoveGroup() {
        super("removegroup");
    }

    @Override
    public Parcelable respond() {
        ILightHouse.get().deleteGroup(id);
        return new OK();
    }
}
