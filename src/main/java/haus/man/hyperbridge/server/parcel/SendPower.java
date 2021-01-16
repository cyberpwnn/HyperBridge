package haus.man.hyperbridge.server.parcel;

import haus.man.hyperbridge.api.ILightHouse;
import ninja.bytecode.shuriken.collections.KList;
import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.ParcelResponse;
import ninja.bytecode.shuriken.web.Parcelable;

@ParcelResponse
public class SendPower extends Parcel {
    private double w;
    private double wh;

    public SendPower() {
        super("sendpower");
        this.w = ILightHouse.get().getWattage();
        this.wh = ILightHouse.get().getTotalWattHours();
    }

    @Override
    public Parcelable respond() {
        return null;
    }
}
