package haus.man.hyperbridge.server.parcel;

import haus.man.hyperbridge.api.ILight;
import haus.man.hyperbridge.api.ILightHouse;
import haus.man.hyperbridge.server.LightData;
import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.ParcelRequest;
import ninja.bytecode.shuriken.web.Parcelable;

@ParcelRequest
public class GetLight extends Parcel {
    private String id;

    public GetLight() {
        super("getlight");
    }

    @Override
    public Parcelable respond() {
        ILight light = ILightHouse.get().getLight(id);

        if(light == null)
        {
            return new Error();
        }

        return new SendLight(LightData.from(light));
    }
}
