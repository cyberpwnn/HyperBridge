package haus.man.hyperbridge.core;
@FunctionalInterface
public interface CMD
{
	public boolean onCommand(String... params);
}
