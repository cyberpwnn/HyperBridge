package haus.man.hyperbridge.server.parcel;

import haus.man.hyperbridge.server.GroupData;
import haus.man.hyperbridge.server.LightData;
import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.ParcelResponse;
import ninja.bytecode.shuriken.web.Parcelable;

@ParcelResponse
public class SendGroup extends Parcel {
    private GroupData group;

    public SendGroup() {
        super("sendgroup");
    }

    public SendGroup(GroupData group)
    {
        this();
        this.group = group;
    }

    @Override
    public Parcelable respond() {
        return null;
    }
}
