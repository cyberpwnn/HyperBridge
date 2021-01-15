package haus.man.hyperbridge.server;

import ninja.bytecode.shuriken.web.Parcel;
import ninja.bytecode.shuriken.web.ParcelWebServer;

public class HyperWebserver
{
    private ParcelWebServer pws;

    public HyperWebserver()
    {
        pws = new ParcelWebServer()
                .configure()
                    .http(true)
                    .https(false)
                    .httpPort(13369)
                    .applySettings()
                .addParcelables(getClass(), "haus.man.hyperbridge.server.parcel")
                .start();
    }
}
