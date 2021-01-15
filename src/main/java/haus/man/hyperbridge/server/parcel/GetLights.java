package haus.man.hyperbridge.server.parcel;

import haus.man.hyperbridge.api.ILight;
import haus.man.hyperbridge.api.ILightHouse;
import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.ParcelRequest;
import ninja.bytecode.shuriken.web.Parcelable;

import java.lang.annotation.Annotation;

public class GetLights extends Parcel {
    public GetLights() {
        super("getlights");
    }

    @Override
    public Parcelable respond() {
        return new SendLights(ILightHouse.get().getAllLights().convert(ILight::getName));
    }
}
