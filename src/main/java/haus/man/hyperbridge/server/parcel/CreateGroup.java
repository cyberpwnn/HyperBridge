package haus.man.hyperbridge.server.parcel;

import haus.man.hyperbridge.api.ILightHouse;
import haus.man.hyperbridge.server.GroupData;
import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.ParcelRequest;
import ninja.bytecode.shuriken.web.Parcelable;

@ParcelRequest
public class CreateGroup extends Parcel {
    private String name;

    public CreateGroup() {
        super("creategroup");
    }

    @Override
    public Parcelable respond() {
        return new SendGroup(GroupData.from(ILightHouse.get().createGroup(name)));
    }
}
