package com.varijon.tinies.PollPoll.handler;

import com.varijon.tinies.PollPoll.gui.GUIHelper;
import com.varijon.tinies.PollPoll.object.PollData;
import com.varijon.tinies.PollPoll.storage.PollStorageManager;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class PollTicker 
{

	int tickCount = 0;
	MinecraftServer server;
	
	public PollTicker() 
	{
		server = FMLCommonHandler.instance().getMinecraftServerInstance();	
	}
	
	@SubscribeEvent
	public void onWorldTick (WorldTickEvent event)
	{
		try
		{
			if(event.phase != Phase.END)
			{
				return;
			}
			if(tickCount > 20)
			{
				for(PollData pollData : PollStorageManager.getPollDataList())
				{
					if(System.currentTimeMillis() >= pollData.getPollEndDate())
					{
						if(pollData.isPollActive())
						{
							for(EntityPlayerMP player : server.getPlayerList().getPlayers())
							{
								pollData.setPollActive(false);
								PollStorageManager.writePollData(pollData);
								player.sendMessage(new TextComponentString(TextFormatting.AQUA + "[Poll] " + TextFormatting.RED + "A poll has ended: " + TextFormatting.WHITE + pollData.getPollTitle()));
								continue;
							}
						}
						
					}
					if(pollData.getPollMessageInterval() != 0 && pollData.isPollActive())
					{
						if(System.currentTimeMillis() >= pollData.getPollNextMessageTime())
						{

							TextComponentTranslation chatTrans = new TextComponentTranslation("", new Object());
							chatTrans.appendSibling(new TextComponentString(TextFormatting.AQUA + "[Poll] " + TextFormatting.LIGHT_PURPLE + "Use or click: "));
							chatTrans.appendSibling(GUIHelper.getClickableCommand(pollData));
							chatTrans.appendSibling(new TextComponentString(TextFormatting.WHITE + " to vote!"));
							
							for(EntityPlayerMP player : server.getPlayerList().getPlayers())
							{
								if(pollData.playerVotesPoll(player.getUniqueID().toString()) == pollData.getNumberVotes())
								{
									continue;
								}
								player.sendMessage(new TextComponentString(TextFormatting.AQUA + "[Poll] " + TextFormatting.GREEN + "A poll is active: " + TextFormatting.WHITE + pollData.getPollTitle()));
								player.sendMessage(chatTrans);
							}

							pollData.setPollNextMessageTime(System.currentTimeMillis() + pollData.getPollMessageInterval());
						}						
					}
				}
				tickCount = 0;
			}
			tickCount++;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
