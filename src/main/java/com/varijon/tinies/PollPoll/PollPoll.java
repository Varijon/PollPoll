package com.varijon.tinies.PollPoll;


import com.varijon.tinies.PollPoll.command.PollAdminCommand;
import com.varijon.tinies.PollPoll.command.PollCommand;
import com.varijon.tinies.PollPoll.handler.PollTicker;
import com.varijon.tinies.PollPoll.storage.PollStorageManager;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid="pollpoll", version="1.0.7", acceptableRemoteVersions="*")
public class PollPoll
{
	public static String MODID = "modid";
	public static String VERSION = "version";

		
	@EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{

	}
	
	@EventHandler
	public void init(FMLInitializationEvent e)
	{
		MinecraftForge.EVENT_BUS.register(new PollTicker());
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		PollStorageManager.loadStorage();
	}

	 @EventHandler
	 public void serverLoad(FMLServerStartingEvent event)
	 {
		 event.registerServerCommand(new PollAdminCommand());
		 event.registerServerCommand(new PollCommand());
	 }
}