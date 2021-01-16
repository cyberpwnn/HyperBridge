package haus.man.hyperbridge.server.parcel;

import haus.man.hyperbridge.api.ILight;
import haus.man.hyperbridge.api.ILightGroup;
import haus.man.hyperbridge.api.ILightHouse;
import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.ParcelRequest;
import ninja.bytecode.shuriken.web.Parcelable;

@ParcelRequest
public class RemoveLightFromGroup extends Parcel {
    private String light;
    private String group;

    public RemoveLightFromGroup() {
        super("removelight");
    }

    @Override
    public Parcelable respond() {
        ILight l = ILightHouse.get().getLight(light);

        if(l == null)
        {
            return new Error();
        }

        ILightGroup g = ILightHouse.get().getGroup(group);

        if(g == null)
        {
            return new Error();
        }

        g.removeLight(l);
        return new OK();
    }
}
