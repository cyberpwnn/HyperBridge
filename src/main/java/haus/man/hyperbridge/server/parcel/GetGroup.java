package haus.man.hyperbridge.server.parcel;

import haus.man.hyperbridge.api.ILight;
import haus.man.hyperbridge.api.ILightGroup;
import haus.man.hyperbridge.api.ILightHouse;
import haus.man.hyperbridge.server.GroupData;
import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.ParcelRequest;
import ninja.bytecode.shuriken.web.Parcelable;

@ParcelRequest
public class GetGroup extends Parcel {
    private String id;

    public GetGroup() {
        super("getgroup");
    }

    @Override
    public Parcelable respond() {
        ILightGroup d = ILightHouse.get().getGroup(id);

        if(d == null)
        {
            return new Error();
        }

        return new SendGroup(GroupData.from(d));
    }
}
