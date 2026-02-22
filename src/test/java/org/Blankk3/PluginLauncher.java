package org.Blankk3;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class PluginLauncher
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(SlayerAdvisorPlugin.class);
		RuneLite.main(args);
	}
}