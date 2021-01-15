package haus.man.hyperbridge;
@FunctionalInterface
public interface CMD
{
	public boolean onCommand(String... params);
}
