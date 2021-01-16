package haus.man.hyperbridge.server.parcel;

import haus.man.hyperbridge.api.ILight;
import haus.man.hyperbridge.api.ILightGroup;
import haus.man.hyperbridge.api.ILightHouse;
import ninja.bytecode.shuriken.logging.L;
import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.ParcelRequest;
import ninja.bytecode.shuriken.web.Parcelable;

import java.awt.*;

@ParcelRequest
public class SetGroupName extends Parcel {
    private String id;
    private String name;

    public SetGroupName() {
        super("setgroupname");
    }

    @Override
    public Parcelable respond() {
        ILightGroup group = ILightHouse.get().getGroup(id);

        if(group == null)
        {
            return new Error();
        }

        group.setName(name);

        return new OK();
    }
}
