package haus.man.hyperbridge.server.parcel;

import haus.man.hyperbridge.api.ILight;
import haus.man.hyperbridge.api.ILightGroup;
import haus.man.hyperbridge.api.ILightHouse;
import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.ParcelRequest;
import ninja.bytecode.shuriken.web.Parcelable;

@ParcelRequest
public class GetGroups extends Parcel {
    public GetGroups() {
        super("getgroups");
    }

    @Override
    public Parcelable respond() {
        return new SendGroups(ILightHouse.get().getAllGroups().convert(ILightGroup::getId));
    }
}
