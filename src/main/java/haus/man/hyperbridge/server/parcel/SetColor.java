package haus.man.hyperbridge.server.parcel;

import haus.man.hyperbridge.api.ILight;
import haus.man.hyperbridge.api.ILightGroup;
import haus.man.hyperbridge.api.ILightHouse;
import haus.man.hyperbridge.core.HyperLight;
import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.ParcelRequest;
import ninja.bytecode.shuriken.web.Parcelable;

import java.awt.*;

@ParcelRequest
public class SetColor extends Parcel {
    private String id;
    private Integer r;
    private Integer g;
    private Integer b;
    private Integer a;
    private Integer t;
    private boolean now;

    public SetColor() {
        super("setcolor");
    }

    @Override
    public Parcelable respond() {
        ILight light = ILightHouse.get().getLight(id);

        if(light == null)
        {
            ILightGroup group = ILightHouse.get().getGroup(id);

            if(group == null)
            {
                return new Error();
            }

            group.setColor(new Color(r,g,b), a/255D, t);

            if(now)
            {
                group.forceUpdate();
            }

            return new OK();
        }

        light.setColor(new Color(r,g,b), a/255D, t);

        if(now)
        {
            light.forceUpdate();
        }

        return new OK();
    }
}
