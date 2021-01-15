package haus.man.hyperbridge;

import haus.man.hyperbridge.core.HyperBridgeServer;
import ninja.bytecode.shuriken.logging.L;

public class HyperBridge
{
	public static HyperBridgeServer server;
	
	public static void main(String[] a)
	{	
		L.i("HyperBridge is Starting");
		server = new HyperBridgeServer();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			server.close();
		}));
	}
}
