package haus.man.hyperbridge.server;

import haus.man.hyperbridge.server.parcel.*;
import haus.man.hyperbridge.server.parcel.Error;
import lombok.Getter;
import ninja.bytecode.shuriken.web.ParcelWebServer;

public class HyperWebserver
{
    @Getter
    private ParcelWebServer pws;

    public HyperWebserver()
    {
        pws = new ParcelWebServer()
                .configure()
                    .http(true)
                    .https(false)
                    .httpPort(13369)
                    .applySettings()
                //.addParcelables(getClass(), "haus.man.hyperbridge.server.parcel")
                .addParcelables(GetLight.class, GetLights.class, GetGroups.class)
                .addParcelables(Error.class, OK.class, SetColor.class, GetGroup.class)
                .addParcelables(SendLights.class, SendLight.class, SendPower.class)
                .addParcelables(Ping.class, Pong.class, GetPower.class, CreateGroup.class)
                .addParcelables(AddLightToGroup.class, RemoveLightFromGroup.class)
                .addParcelables(RemoveGroup.class, SendGroup.class, SendGroups.class)
                .addParcelables(SetGroupName.class, LockLight.class, UnlockLight.class)
                .start();
    }
}
