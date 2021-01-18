package haus.man.hyperbridge.core;

import java.io.File;
import java.io.IOException;

import com.google.gson.Gson;

import lombok.Data;
import ninja.bytecode.shuriken.collections.KList;
import ninja.bytecode.shuriken.execution.J;
import ninja.bytecode.shuriken.io.IO;
import ninja.bytecode.shuriken.json.JSONObject;
import ninja.bytecode.shuriken.logging.L;

@Data
public class HyperState
{
	private KList<HyperBridgeState> bridges;
	private KList<HyperGroup> groups;
	private KList<HyperLight> lights;
	private boolean dirty;

	public HyperState()
	{
		dirty = false;
		bridges = new KList<>();
		groups = new KList<>();
	}

	public void saveIfDirty()
	{
		if(dirty)
		{
			dirty =  false;
			J.a(this::save);
		}
	}

	public void save()
	{
		File file = new File("state.json");
		try
		{
			file.getParentFile().mkdirs();
		}

		catch(Throwable e)
		{
			e.printStackTrace();
		}

		try
		{
			IO.writeAll(file, new JSONObject(new Gson().toJson(this)).toString(4));
		}

		catch(IOException e)
		{
			L.ex(e);
		}
		L.v("Saved HyperState");
	}

	public static HyperState load()
	{
		File file = new File("state.json");
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
				return new Gson().fromJson(IO.readAll(file), HyperState.class);
			}

			catch(Throwable e)
			{

			}
		}

		return new HyperState();
	}
}
