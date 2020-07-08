package haus.man.hyperbridge;

import java.awt.Color;

import com.philips.lighting.model.PHLight;

import ninja.bytecode.shuriken.collections.KList;
import ninja.bytecode.shuriken.collections.KMap;
import ninja.bytecode.shuriken.execution.J;
import ninja.bytecode.shuriken.format.Form;
import ninja.bytecode.shuriken.io.StreamSucker;
import ninja.bytecode.shuriken.logging.L;

public class ConsoleManager
{
	private static final String blank = Form.repeat("    ", 10);
	private KMap<String, CMD> commands;

	public ConsoleManager()
	{
		registerCommands();
		prompt();

		J.a(() ->
		{
			J.sleep(3000);
			// if(Icarus.eclipseDockerConsole)
			// {
			// L.w("Eclipse Console is being used. Expect Garbage.");
			// }
		});

		new StreamSucker(System.in, this::process);
	}

	private void registerCommands()
	{
		commands = new KMap<>();
		registerCommand("stop", (args) ->
		{
			HyperBridge.server.close();
			System.exit(0);
			return true;
		});

		registerCommand("list", (args) ->
		{
			if(args.length == 0)
			{
				L.i("Usage list [thing]");
				L.i("- bridges");
				L.i("- lights");
				L.i("- <bridge ip>");
				L.flush();
			}

			else if(args[0].equalsIgnoreCase("bridges") || args[0].equalsIgnoreCase("bridge"))
			{
				L.i("MAC Address\t\t\tIP Address\t\t\tLights\t\t\tQueue");
				L.flush();

				for(HyperNode i : HyperBridge.server.getNodes().v())
				{
					L.i(i.getBridge().getResourceCache().getBridgeConfiguration().getMacAddress() + "\t\t\t" + i.getBridge().getResourceCache().getBridgeConfiguration().getIpAddress() + "\t\t\t" + i.getBridge().getResourceCache().getAllLights().size() + "\t\t\t" + i.getQueue().size());
				}

				L.flush();
			}

			else if(args[0].equalsIgnoreCase("lights") || args[0].equalsIgnoreCase("light"))
			{
				L.i("Name\t\t\tUUID\t\t\tType\t\t\tState");
				L.flush();

				for(HyperLight i : HyperBridge.server.getLights().v())
				{
					Color c = Color.getHSBColor(i.getLight().getLastKnownLightState().getHue() / 255f, i.getLight().getLastKnownLightState().getSaturation() / 255f, i.getLight().getLastKnownLightState().getBrightness() / 255f);
					L.i(i.getLight().getName() + "\t\t\t" + i.getLight().getUniqueId() + "\t\t\t" + i.getLight().getLightType().name() + "\t\t\t" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue());
				}

				L.flush();
			}

			else
			{
				for(HyperNode i : HyperBridge.server.getNodes().v())
				{
					if(i.getBridge().getResourceCache().getBridgeConfiguration().getIpAddress().equals(args[0]))
					{
						L.i("Name\t\t\tUUID\t\t\tType\t\t\tState");
						L.flush();

						for(PHLight j : i.getBridge().getResourceCache().getAllLights())
						{
							Color c = Color.getHSBColor(j.getLastKnownLightState().getHue() / 255f, j.getLastKnownLightState().getSaturation() / 255f, j.getLastKnownLightState().getBrightness() / 255f);
							L.i(j.getName() + "\t\t\t" + j.getUniqueId() + "\t\t\t" + j.getLightType().name() + "\t\t\t" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue());
						}

						L.flush();
						return true;
					}
				}

				L.w("Cant find a bridge at " + args[0] + ". Try running 'list bridges'");
			}

			return true;
		});

		registerCommand("set", (args) ->
		{
			if(args.length < 2)
			{
				L.i("Usage: set <light-name | light-uuid | bridge-ip | all> <#RRGGBBAA> [transition ms]");
				L.i("If you are using light names with spaces, use underscores.");
				return true;
			}

			if(args[1].length() != 9)
			{
				L.w("Second parameter is '#RRGGBBAA'. R = Red, G = Green, B = Blue, A = Brightness (alpha)");
			}

			L.i("Color is " + args[1].substring(1).substring(0, 6));
			Color color = hex2Rgb("#" + args[1].substring(1).substring(0, 6));
			int brightness = Long.valueOf(args[1].substring(7), 16).intValue();
			L.i("Brightness is " + brightness);
			int ttime = args.length > 2 ? Integer.valueOf(args[2]) : 3000;
			L.i("Transition Time is " + ttime + "ms");

			if(color == null)
			{
				L.f("Color is invalid");
				return true;
			}

			if(args[0].equals("all"))
			{
				int m = 0;

				for(HyperLight i : HyperBridge.server.getLights().v())
				{
					m++;
					i.color(color, brightness, ttime);
				}

				L.i("Changed " + m + " light states");

				return true;
			}

			for(HyperLight i : HyperBridge.server.getLights().v())
			{
				int m = 0;
				if(i.getBridge().getBridge().getResourceCache().getBridgeConfiguration().getIpAddress().equals(args[0]))
				{
					m++;
					i.color(color, brightness, ttime);
				}

				if(m > 0)
				{
					L.i("Changed " + m + " light states");
					return true;
				}
			}

			for(HyperLight i : HyperBridge.server.getLights().v())
			{
				if(i.getLight().getName().replaceAll("\\Q \\E", "_").equals(args[0]))
				{
					i.color(color, brightness, ttime);
					L.i("Changed 1 light state");
					return true;
				}

				else if(i.getLight().getUniqueId().equals(args[0]))
				{
					i.color(color, brightness, ttime);
					L.i("Changed 1 light state");
					return true;
				}
			}

			L.w("Couldnt find a source.. Not a bridge, light or anything. Try listing.");

			return true;
		});

		registerCommand("power", (args) ->
		{
			L.i("Current System  Wattage: " + Form.f(HyperBridge.server.getSystemWattage(), 4));
			L.i("Total System Watt Hours: " + Form.f(HyperBridge.server.getSystemWattHours(), 4));
			return true;
		});

		registerCommand("help", (args) ->
		{
			for(String i : commands.k())
			{
				L.i("- " + i);
			}

			return true;
		});
	}

	private static Color hex2Rgb(String colorStr)
	{
		return new Color(Integer.valueOf(colorStr.substring(1, 3), 16), Integer.valueOf(colorStr.substring(3, 5), 16), Integer.valueOf(colorStr.substring(5, 7), 16));
	}

	private void registerCommand(String string, CMD cmd)
	{
		commands.put(string, cmd);
	}

	public void process(String s)
	{
		prompt();

		if(s.trim().isEmpty())
		{
			return;
		}

		KList<String> params = new KList<String>();
		String command = s;

		if(s.contains(" "))
		{
			params = new KList<String>(s.split("\\Q \\E"));
			command = params.pop();
		}

		processCommand(command, params.toArray(new String[params.size()]));
	}

	private void processCommand(String command, String... params)
	{
		try
		{
			for(String i : commands.k())
			{
				if(command.trim().toLowerCase().equals(i.toLowerCase()))
				{
					if(commands.get(i).onCommand(params))
					{
						L.flush();
						return;
					}
				}
			}

			L.i("Unknown Command");
			L.flush();
		}

		catch(Throwable e)
		{
			L.f("Failed to execute command");
			L.ex(e);
		}
	}

	public void log(String s)
	{
		rewrite(s.replaceAll("\\Q\n\\E", "").replaceAll("\\Q\r\\E", ""));
		ln();
		prompt();
	}

	public void prompt()
	{
		print("> ");
	}

	public void ln()
	{
		System.out.println();
	}

	public void print(String s)
	{
		System.out.print(s);
	}

	public void rewrite(String s)
	{
		System.out.print("\r" + s + blank);
	}
}
