package haus.man.hyperbridge;

import java.io.File;
import java.io.IOException;

import com.google.gson.Gson;

import lombok.Data;
import ninja.bytecode.shuriken.io.IO;
import ninja.bytecode.shuriken.json.JSONObject;
import ninja.bytecode.shuriken.logging.L;

@Data
public class HyperConfig
{
	private static HyperConfig cfg;
	private int bridgeMaxLIOPS = 4;
	private int defaultTransitionMS = 10000;
	private int defaultBrightness = 255;

	public static HyperConfig get()
	{
		if(cfg == null)
		{
			cfg = HyperConfig.load();
		}

		return cfg;
	}

	public HyperConfig()
	{

	}

	public void save()
	{
		File file = new File("config.json");
		try
		{
			file.getParentFile().mkdirs();
		}

		catch(Throwable e)
		{

		}

		try
		{
			IO.writeAll(file, new JSONObject(new Gson().toJson(this)).toString(4));
		}

		catch(IOException e)
		{
			L.ex(e);
		}
	}

	public static HyperConfig load()
	{
		File file = new File("config.json");
		try
		{
			file.getParentFile().mkdirs();
		}

		catch(Throwable e)
		{

		}

		if(file.exists())
		{
			try
			{
				return new Gson().fromJson(IO.readAll(file), HyperConfig.class);
			}

			catch(Throwable e)
			{

			}
		}

		else
		{
			new HyperConfig().save();
		}

		return new HyperConfig();
	}
}
