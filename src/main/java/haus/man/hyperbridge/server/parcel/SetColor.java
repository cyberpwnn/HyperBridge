package haus.man.hyperbridge.server.parcel;

import haus.man.hyperbridge.api.ILight;
import haus.man.hyperbridge.api.ILightHouse;
import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.Parcelable;

import java.awt.*;

public class SetColor extends Parcel {
    private String id;
    private int r;
    private int g;
    private int b;
    private int a;
    private int t;

    public SetColor() {
        super("setcolor");
    }

    @Override
    public Parcelable respond() {
        ILight light = ILightHouse.get().getLight(id);

        if(light == null)
        {
            return new Error();
        }

        light.setColor(new Color(r,g,b), a/255D, t);

        return new OK();
    }
}
