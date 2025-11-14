package com.example;

import com.questnextaction.ObjectiveTrackerPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ObjectiveTrackerPlugin.class);
		RuneLite.main(args);
	}
}