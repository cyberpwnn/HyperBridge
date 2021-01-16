package haus.man.hyperbridge.server.parcel;

import ninja.bytecode.shuriken.collections.KList;
import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.ParcelResponse;
import ninja.bytecode.shuriken.web.Parcelable;

@ParcelResponse
public class SendGroups extends Parcel {
    private KList<String> groups;

    public SendGroups(KList<String> groups) {
        super("sendgroups");
        this.groups = groups;
    }

    public SendGroups() {
        this(new KList<>());
    }

    @Override
    public Parcelable respond() {
        return null;
    }
}
