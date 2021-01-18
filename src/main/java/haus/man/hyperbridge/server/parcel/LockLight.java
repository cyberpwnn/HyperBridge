package haus.man.hyperbridge.server.parcel;

import haus.man.hyperbridge.api.ILight;
import haus.man.hyperbridge.api.ILightHouse;
import haus.man.hyperbridge.server.LightData;
import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.ParcelRequest;
import ninja.bytecode.shuriken.web.Parcelable;

@ParcelRequest
public class LockLight extends Parcel {
    private String id;

    public LockLight() {
        super("locklight");
    }

    @Override
    public Parcelable respond() {
        ILight light = ILightHouse.get().getLight(id);

        if(light == null)
        {
            return new Error();
        }

        light.setLocked(true);
        light.setDirty();

        return new OK();
    }
}
