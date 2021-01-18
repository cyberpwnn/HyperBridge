package haus.man.hyperbridge.server.parcel;

import haus.man.hyperbridge.api.ILight;
import haus.man.hyperbridge.api.ILightHouse;
import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.ParcelRequest;
import ninja.bytecode.shuriken.web.Parcelable;

@ParcelRequest
public class UnlockLight extends Parcel {
    private String id;

    public UnlockLight() {
        super("unlocklight");
    }

    @Override
    public Parcelable respond() {
        ILight light = ILightHouse.get().getLight(id);

        if(light == null)
        {
            return new Error();
        }

        light.setLocked(false);
        light.setDirty();

        return new OK();
    }
}
