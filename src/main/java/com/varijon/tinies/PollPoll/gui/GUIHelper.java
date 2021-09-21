package com.varijon.tinies.PollPoll.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang3.time.DurationFormatUtils;

import com.ibm.icu.text.Replaceable;
import com.varijon.tinies.PollPoll.object.PollData;
import com.varijon.tinies.PollPoll.object.PollOption;
import com.varijon.tinies.PollPoll.object.PollPlayer;
import com.varijon.tinies.PollPoll.storage.PollStorageManager;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraft.util.text.event.HoverEvent;

public class GUIHelper 
{
	public static ITextComponent getPollResultToolTip(PollOption pollOption)
	{
		StringBuilder sb = new StringBuilder();
		
		LinkedHashMap<String,Integer> tempMap = new LinkedHashMap<>();
		
		for(PollPlayer pollPlayer : pollOption.getLstPollPlayers())
		{
			if(tempMap.containsKey(pollPlayer.getPlayerName()))
			{
				tempMap.put(pollPlayer.getPlayerName(), tempMap.get(pollPlayer.getPlayerName()) + 1);
			}
			else
			{
				tempMap.put(pollPlayer.getPlayerName(),1);
			}
		}
		boolean firstEntry = true;
		for (Entry<String, Integer> set : tempMap.entrySet())
		{
			if(firstEntry)
			{
				sb.append(set.getKey() + " " + set.getValue() + "x");
				firstEntry = false;
			}
			else
			{
				sb.append("\n" + set.getKey() + " " + set.getValue() + "x");				
			}
		}
		
		TextComponentTranslation extraInfo = new TextComponentTranslation(pollOption.getOptionName(), new Object[0]);
		extraInfo.getStyle().setColor(TextFormatting.YELLOW).setHoverEvent(
        		new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
        				new TextComponentString(
        						sb.toString()
        						)
        				)
        		);
		return extraInfo;
	}
	public static ITextComponent getClickableCommand(PollData pollData)
	{
		TextComponentTranslation extraInfo = new TextComponentTranslation("/poll " + pollData.getPollID(), new Object[0]);
		extraInfo.getStyle().setColor(TextFormatting.GREEN).setHoverEvent(
        		new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
        				new TextComponentString(
        						"Click to run command " + "/poll " + pollData.getPollID()
        						)
        				)
        		).setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/poll " + pollData.getPollID()));
		return extraInfo;
	}
	public static GooeyPage getPollVoteMenu(PollData pollData, EntityPlayerMP player)
	{
		GooeyButton emptySlot = GooeyButton.builder()
                .display(new ItemStack(Blocks.STAINED_GLASS_PANE,1,0))
                .title("")
                .build();

		ArrayList<String> pollTitleLore = new ArrayList<>();
		pollTitleLore.add(TextFormatting.WHITE + "Time left: " + TextFormatting.DARK_GREEN + DurationFormatUtils.formatDuration(pollData.getPollTimeLeft(),"d' days 'H' hours 'm' minutes 's' seconds'", true));
		GooeyButton timeLeft = GooeyButton.builder()
                .display(new ItemStack(Items.CLOCK))
                .lore(pollTitleLore)
                .title("")
                .build();
		
		ArrayList<String> lstFormattedLore = new ArrayList<>();
		
		for(String string : pollData.getLstLore())
		{
			lstFormattedLore.add(replaceColorCodes(string));
		}
		
		GooeyButton pollTitle = GooeyButton.builder()
                .display(new ItemStack(Items.SIGN))
                .title(TextFormatting.GOLD + pollData.getPollTitle())
                .lore(lstFormattedLore)
                .build();
		
		ChestTemplate.Builder templateBuilder = ChestTemplate.builder(3)
        		.fill(emptySlot)
        		.set(0, 5,timeLeft)
        		.set(0, 4, pollTitle);
		

		ChestTemplate template = setPollButtons(pollData, templateBuilder, player)
                .build();
      
		GooeyPage pageBuilder = GooeyPage.builder()
                .title(TextFormatting.DARK_BLUE + "Votes Left: " + TextFormatting.DARK_GREEN + (pollData.getNumberVotes() - pollData.playerVotesPoll(player.getUniqueID().toString())))
                .template(template)
                .build();

        return pageBuilder;
	}
	
	static ChestTemplate.Builder setPollButtons(PollData pollData, ChestTemplate.Builder templateBuilder, EntityPlayerMP player)
	{
		ArrayList<GooeyButton> lstButton = new ArrayList<>();
		
		for(PollOption pollOption : pollData.getLstPollOptions())
		{
			Item pollItem = Item.getByNameOrId(pollOption.getItemName());
			ItemStack pollItemStack;
			if(pollOption.getItemMeta() == 0)
			{
				pollItemStack = new ItemStack(pollItem);
			}
			else
			{
				pollItemStack = new ItemStack(pollItem,1,pollOption.getItemMeta());
			}
			if(!pollOption.getItemNBT().equals(""))
			{
				try {
					pollItemStack.setTagCompound(JsonToNBT.getTagFromJson(pollOption.getItemNBT()));
				} catch (NBTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			int playerVotesOption = pollOption.getPlayerVotesOption(player.getUniqueID().toString());
			ArrayList<String> buttonLore = new ArrayList<>();
			if(playerVotesOption == 1)
			{
				buttonLore.add(TextFormatting.GRAY + "You voted for this option!");
			}
			if(playerVotesOption > 1)
			{
				buttonLore.add(TextFormatting.GRAY + "You voted for this option " + TextFormatting.GOLD + playerVotesOption + "x" + TextFormatting.GRAY + "!");
			}
			
			GooeyButton optionButton = GooeyButton.builder()
					.display(pollItemStack)
					.title(playerVotesOption > 0 ? TextFormatting.GREEN + pollOption.getOptionName() : TextFormatting.AQUA + pollOption.getOptionName())
					.lore(buttonLore)
					.onClick((action) -> 
					{
						if(!pollData.isPollActive())
						{
							UIManager.closeUI(player);
							action.getPlayer().sendMessage(new TextComponentString(TextFormatting.RED + "Poll is closed!"));
							return;
						}
						if(pollData.playerVotesPoll(action.getPlayer().getUniqueID().toString()) >= pollData.getNumberVotes())
						{
							UIManager.closeUI(player);
							action.getPlayer().sendMessage(new TextComponentString(TextFormatting.RED + "You already voted!"));
							return;
						}
						if(pollOption.getPlayerVotesOption(action.getPlayer().getUniqueID().toString()) == 1 && !pollData.isAllowSameOptionVote())
						{
							UIManager.closeUI(player);
							action.getPlayer().sendMessage(new TextComponentString(TextFormatting.RED + "You already voted for this option!"));
							return;
						}
						UIManager.closeUI(player);
						pollOption.addPollPlayer(new PollPlayer(action.getPlayer().getUniqueID().toString(), action.getPlayer().getName()));
						if(pollData.playerVotesPoll(action.getPlayer().getUniqueID().toString()) < pollData.getNumberVotes())
						{
							UIManager.openUIForcefully(action.getPlayer(), getPollVoteMenu(pollData, action.getPlayer()));
						}
						else
						{
							action.getPlayer().sendMessage(new TextComponentString(TextFormatting.GREEN + "Thank you for voting!"));							
						}
						PollStorageManager.writePollData(pollData);
					})
					.build();
			lstButton.add(optionButton);
		}

		int amountButtons = lstButton.size();
		
		if(amountButtons == 1)
		{
			templateBuilder.set(1, 4, lstButton.get(0));
		}
		if(amountButtons == 2)
		{
			templateBuilder.set(1, 3, lstButton.get(0));
			templateBuilder.set(1, 5, lstButton.get(1));
		}
		if(amountButtons == 3)
		{
			templateBuilder.set(1, 2, lstButton.get(0));
			templateBuilder.set(1, 4, lstButton.get(1));
			templateBuilder.set(1, 6, lstButton.get(2));
		}
		if(amountButtons == 4)
		{
			templateBuilder.set(1, 1, lstButton.get(0));
			templateBuilder.set(1, 3, lstButton.get(1));
			templateBuilder.set(1, 5, lstButton.get(2));
			templateBuilder.set(1, 7, lstButton.get(3));
		}
		if(amountButtons == 5)
		{
			templateBuilder.set(1, 0, lstButton.get(0));
			templateBuilder.set(1, 2, lstButton.get(1));
			templateBuilder.set(1, 4, lstButton.get(2));
			templateBuilder.set(1, 6, lstButton.get(3));
			templateBuilder.set(1, 8, lstButton.get(4));
		}
		if(amountButtons == 6)
		{
			templateBuilder.set(1, 1, lstButton.get(0));
			templateBuilder.set(1, 2, lstButton.get(1));
			templateBuilder.set(1, 3, lstButton.get(2));
			templateBuilder.set(1, 5, lstButton.get(3));
			templateBuilder.set(1, 6, lstButton.get(4));
			templateBuilder.set(1, 7, lstButton.get(5));
		}
		if(amountButtons == 7)
		{
			templateBuilder.set(1, 1, lstButton.get(0));
			templateBuilder.set(1, 2, lstButton.get(1));
			templateBuilder.set(1, 3, lstButton.get(2));
			templateBuilder.set(1, 4, lstButton.get(3));
			templateBuilder.set(1, 5, lstButton.get(4));
			templateBuilder.set(1, 6, lstButton.get(5));
			templateBuilder.set(1, 7, lstButton.get(6));
		}
		if(amountButtons == 8)
		{
			templateBuilder.set(1, 0, lstButton.get(0));
			templateBuilder.set(1, 1, lstButton.get(1));
			templateBuilder.set(1, 2, lstButton.get(2));
			templateBuilder.set(1, 3, lstButton.get(3));
			templateBuilder.set(1, 5, lstButton.get(4));
			templateBuilder.set(1, 6, lstButton.get(5));
			templateBuilder.set(1, 7, lstButton.get(6));
			templateBuilder.set(1, 8, lstButton.get(7));
		}
		if(amountButtons == 9)
		{
			templateBuilder.set(1, 0, lstButton.get(0));
			templateBuilder.set(1, 1, lstButton.get(1));
			templateBuilder.set(1, 2, lstButton.get(2));
			templateBuilder.set(1, 3, lstButton.get(3));
			templateBuilder.set(1, 4, lstButton.get(4));
			templateBuilder.set(1, 5, lstButton.get(5));
			templateBuilder.set(1, 6, lstButton.get(6));
			templateBuilder.set(1, 7, lstButton.get(7));
			templateBuilder.set(1, 8, lstButton.get(8));
		}
		if(amountButtons == 10)
		{
			templateBuilder.set(1, 0, lstButton.get(0));
			templateBuilder.set(1, 1, lstButton.get(1));
			templateBuilder.set(1, 2, lstButton.get(2));
			templateBuilder.set(1, 3, lstButton.get(3));
			templateBuilder.set(1, 4, lstButton.get(4));
			templateBuilder.set(1, 5, lstButton.get(5));
			templateBuilder.set(1, 6, lstButton.get(6));
			templateBuilder.set(1, 7, lstButton.get(7));
			templateBuilder.set(1, 8, lstButton.get(8));
			templateBuilder.set(2, 4, lstButton.get(9));
		}
		if(amountButtons == 11)
		{
			templateBuilder.set(1, 0, lstButton.get(0));
			templateBuilder.set(1, 1, lstButton.get(1));
			templateBuilder.set(1, 2, lstButton.get(2));
			templateBuilder.set(1, 3, lstButton.get(3));
			templateBuilder.set(1, 4, lstButton.get(4));
			templateBuilder.set(1, 5, lstButton.get(5));
			templateBuilder.set(1, 6, lstButton.get(6));
			templateBuilder.set(1, 7, lstButton.get(7));
			templateBuilder.set(1, 8, lstButton.get(8));
			templateBuilder.set(2, 3, lstButton.get(9));
			templateBuilder.set(2, 5, lstButton.get(10));
		}
		if(amountButtons == 12)
		{
			templateBuilder.set(1, 0, lstButton.get(0));
			templateBuilder.set(1, 1, lstButton.get(1));
			templateBuilder.set(1, 2, lstButton.get(2));
			templateBuilder.set(1, 3, lstButton.get(3));
			templateBuilder.set(1, 4, lstButton.get(4));
			templateBuilder.set(1, 5, lstButton.get(5));
			templateBuilder.set(1, 6, lstButton.get(6));
			templateBuilder.set(1, 7, lstButton.get(7));
			templateBuilder.set(1, 8, lstButton.get(8));
			templateBuilder.set(2, 3, lstButton.get(9));
			templateBuilder.set(2, 4, lstButton.get(10));
			templateBuilder.set(2, 5, lstButton.get(11));
		}
		if(amountButtons == 13)
		{
			templateBuilder.set(1, 0, lstButton.get(0));
			templateBuilder.set(1, 1, lstButton.get(1));
			templateBuilder.set(1, 2, lstButton.get(2));
			templateBuilder.set(1, 3, lstButton.get(3));
			templateBuilder.set(1, 4, lstButton.get(4));
			templateBuilder.set(1, 5, lstButton.get(5));
			templateBuilder.set(1, 6, lstButton.get(6));
			templateBuilder.set(1, 7, lstButton.get(7));
			templateBuilder.set(1, 8, lstButton.get(8));
			templateBuilder.set(2, 2, lstButton.get(9));
			templateBuilder.set(2, 3, lstButton.get(10));
			templateBuilder.set(2, 5, lstButton.get(11));
			templateBuilder.set(2, 6, lstButton.get(12));
		}
		if(amountButtons == 14)
		{
			templateBuilder.set(1, 0, lstButton.get(0));
			templateBuilder.set(1, 1, lstButton.get(1));
			templateBuilder.set(1, 2, lstButton.get(2));
			templateBuilder.set(1, 3, lstButton.get(3));
			templateBuilder.set(1, 4, lstButton.get(4));
			templateBuilder.set(1, 5, lstButton.get(5));
			templateBuilder.set(1, 6, lstButton.get(6));
			templateBuilder.set(1, 7, lstButton.get(7));
			templateBuilder.set(1, 8, lstButton.get(8));
			templateBuilder.set(2, 2, lstButton.get(9));
			templateBuilder.set(2, 3, lstButton.get(10));
			templateBuilder.set(2, 4, lstButton.get(11));
			templateBuilder.set(2, 5, lstButton.get(12));
			templateBuilder.set(2, 6, lstButton.get(13));
		}
		if(amountButtons == 15)
		{
			templateBuilder.set(1, 0, lstButton.get(0));
			templateBuilder.set(1, 1, lstButton.get(1));
			templateBuilder.set(1, 2, lstButton.get(2));
			templateBuilder.set(1, 3, lstButton.get(3));
			templateBuilder.set(1, 4, lstButton.get(4));
			templateBuilder.set(1, 5, lstButton.get(5));
			templateBuilder.set(1, 6, lstButton.get(6));
			templateBuilder.set(1, 7, lstButton.get(7));
			templateBuilder.set(1, 8, lstButton.get(8));
			templateBuilder.set(2, 1, lstButton.get(9));
			templateBuilder.set(2, 2, lstButton.get(10));
			templateBuilder.set(2, 3, lstButton.get(11));
			templateBuilder.set(2, 5, lstButton.get(12));
			templateBuilder.set(2, 6, lstButton.get(13));
			templateBuilder.set(2, 7, lstButton.get(14));
		}
		if(amountButtons == 16)
		{
			templateBuilder.set(1, 0, lstButton.get(0));
			templateBuilder.set(1, 1, lstButton.get(1));
			templateBuilder.set(1, 2, lstButton.get(2));
			templateBuilder.set(1, 3, lstButton.get(3));
			templateBuilder.set(1, 4, lstButton.get(4));
			templateBuilder.set(1, 5, lstButton.get(5));
			templateBuilder.set(1, 6, lstButton.get(6));
			templateBuilder.set(1, 7, lstButton.get(7));
			templateBuilder.set(1, 8, lstButton.get(8));
			templateBuilder.set(2, 1, lstButton.get(9));
			templateBuilder.set(2, 2, lstButton.get(10));
			templateBuilder.set(2, 3, lstButton.get(11));
			templateBuilder.set(2, 4, lstButton.get(12));
			templateBuilder.set(2, 5, lstButton.get(13));
			templateBuilder.set(2, 6, lstButton.get(14));
			templateBuilder.set(2, 7, lstButton.get(15));
		}
		if(amountButtons == 17)
		{
			templateBuilder.set(1, 0, lstButton.get(0));
			templateBuilder.set(1, 1, lstButton.get(1));
			templateBuilder.set(1, 2, lstButton.get(2));
			templateBuilder.set(1, 3, lstButton.get(3));
			templateBuilder.set(1, 4, lstButton.get(4));
			templateBuilder.set(1, 5, lstButton.get(5));
			templateBuilder.set(1, 6, lstButton.get(6));
			templateBuilder.set(1, 7, lstButton.get(7));
			templateBuilder.set(1, 8, lstButton.get(8));
			templateBuilder.set(2, 0, lstButton.get(9));
			templateBuilder.set(2, 1, lstButton.get(10));
			templateBuilder.set(2, 2, lstButton.get(11));
			templateBuilder.set(2, 3, lstButton.get(12));
			templateBuilder.set(2, 5, lstButton.get(13));
			templateBuilder.set(2, 6, lstButton.get(14));
			templateBuilder.set(2, 7, lstButton.get(15));
			templateBuilder.set(2, 8, lstButton.get(16));
		}
		if(amountButtons == 18)
		{
			templateBuilder.set(1, 0, lstButton.get(0));
			templateBuilder.set(1, 1, lstButton.get(1));
			templateBuilder.set(1, 2, lstButton.get(2));
			templateBuilder.set(1, 3, lstButton.get(3));
			templateBuilder.set(1, 4, lstButton.get(4));
			templateBuilder.set(1, 5, lstButton.get(5));
			templateBuilder.set(1, 6, lstButton.get(6));
			templateBuilder.set(1, 7, lstButton.get(7));
			templateBuilder.set(1, 8, lstButton.get(8));
			templateBuilder.set(2, 0, lstButton.get(9));
			templateBuilder.set(2, 1, lstButton.get(10));
			templateBuilder.set(2, 2, lstButton.get(11));
			templateBuilder.set(2, 3, lstButton.get(12));
			templateBuilder.set(2, 4, lstButton.get(13));
			templateBuilder.set(2, 5, lstButton.get(14));
			templateBuilder.set(2, 6, lstButton.get(15));
			templateBuilder.set(2, 7, lstButton.get(16));
			templateBuilder.set(2, 8, lstButton.get(17));
		}
		
		return templateBuilder;
	}
	private static String replaceColorCodes(String string)
	{
		String returnString = string.replace("&","\u00A7");
//		returnString = string.replace("&","\u00A7" + "1");
//		returnString = string.replace("&2","\u00A7" + "2");
//		returnString = string.replace("&3","\u00A7" + "3");
//		returnString = string.replace("&4","\u00A7" + "4");
//		returnString = string.replace("&5","\u00A7" + "5");
//		returnString = string.replace("&6","\u00A7" + "6");
//		returnString = string.replace("&7","\u00A7" + "7");
//		returnString = string.replace("&8","\u00A7" + "8");
//		returnString = string.replace("&9","\u00A7" + "9");
//		returnString = string.replace("&0","\u00A7" + "0");
//		returnString = string.replace("&a","\u00A7" + "a");
//		returnString = string.replace("&b","\u00A7" + "b");
//		returnString = string.replace("&c","\u00A7" + "c");
//		returnString = string.replace("&d","\u00A7" + "d");
//		returnString = string.replace("&e","\u00A7" + "e");
//		returnString = string.replace("&f","\u00A7" + "f");
//		returnString = string.replace("&k","\u00A7" + "k");
//		returnString = string.replace("&l","\u00A7" + "l");
//		returnString = string.replace("&o","\u00A7" + "o");
//		returnString = string.replace("&m","\u00A7" + "m");
//		returnString = string.replace("&n","\u00A7" + "n");
//		returnString = string.replace("&r","\u00A7" + "r");
		
		return returnString;
	}
}
