package com.varijon.tinies.PollPoll.command;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.soap.Text;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import com.varijon.tinies.PollPoll.gui.GUIHelper;
import com.varijon.tinies.PollPoll.object.PollData;
import com.varijon.tinies.PollPoll.object.PollOption;
import com.varijon.tinies.PollPoll.object.PollPlayer;
import com.varijon.tinies.PollPoll.storage.PollStorageManager;

import ca.landonjw.gooeylibs2.api.UIManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;

public class PollAdminCommand implements ICommand {

	private List aliases;
	private static final Pattern periodPattern = Pattern.compile("([0-9]+)([hdwmy])");
	
	public PollAdminCommand()
	{
	   this.aliases = new ArrayList();
	   this.aliases.add("polladmin");
	   
	}
	
	@Override
	public int compareTo(ICommand arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "polladmin";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "polladmin";
	}

	@Override
	public List<String> getAliases() {
		// TODO Auto-generated method stub
		return this.aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException 
	{
		if(sender.canUseCommand(4, "pollpoll.admin"))
		{
			if(args.length == 0)
			{
				sendCommandOptionHelp(sender);

				return;
			}
			if(args[0].equals("create"))
			{
				if(args.length < 3)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin create pollID duration"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Time format example: 10d10m for a 10 days and 10 minutes long poll"));						
					return;
				}
				else
				{
					PollData pollData = PollStorageManager.getPollData(args[1]);
					if(pollData != null)
					{
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll ID already exists!"));
						return;
					}
					
					PollData newPoll = new PollData(args[1], "", parsePeriod(args[2]), 1, 0, 0, 0, false, false, false, new ArrayList<PollOption>());
					PollStorageManager.addPollData(newPoll);
					PollStorageManager.writePollData(newPoll);
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Poll " + TextFormatting.GOLD + newPoll.getPollID() + TextFormatting.GREEN + " successfully created"));
					return;
				}
			}
			if(args[0].equals("title"))
			{
				if(args.length < 3)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin title pollID title"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Sets the title of the poll"));
					return;
				}
				PollData pollData = PollStorageManager.getPollData(args[1]);
				if(pollData == null)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll not found!"));
					return;
				}
				StringBuilder sb = new StringBuilder();
				for(int x = 2; x < args.length; x++)
				{
					sb.append(args[x]);
					if(x != args.length-1)
					{
						sb.append(" ");
					}
				}
				pollData.setPollTitle(sb.toString());
				PollStorageManager.writePollData(pollData);
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Poll title set to: " + TextFormatting.GOLD + pollData.getPollTitle()));
				return;
			}
			if(args[0].equals("description"))
			{
				if(args.length < 3)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin description pollID add/set/remove <(slot)/text>"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Modifies the description of the poll"));
					return;
				}
				PollData pollData = PollStorageManager.getPollData(args[1]);
				if(pollData == null)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll not found!"));
					return;
				}
				if(args[2].equals("add"))
				{
					if(args.length < 4)
					{
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin description pollID add <text>"));
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Adds description to the poll"));					
						return;							
					}
					StringBuilder sb = new StringBuilder();
					for(int x = 3; x < args.length; x++)
					{
						sb.append(args[x]);
						if(x != args.length-1)
						{
							sb.append(" ");
						}
					}
					
					pollData.addLoreItem(sb.toString());
					PollStorageManager.writePollData(pollData);
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Poll description line added: " + TextFormatting.GOLD + replaceColorCodes(sb.toString())));
					return;
				}
				if(args[2].equals("set"))
				{
					if(args.length < 5)
					{
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin description pollID set slot <text>"));
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Sets the description of the poll"));				
						return;						
					}
					
					if(!NumberUtils.isNumber(args[3]))
					{
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin description pollID set slot <text>"));
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Sets the description of the poll"));						
						return;
					}
					
					StringBuilder sb = new StringBuilder();
					for(int x = 4; x < args.length; x++)
					{
						sb.append(args[x]);
						if(x != args.length-1)
						{
							sb.append(" ");
						}
					}
					if(sb.toString().length() == 0)
					{
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin description pollID set slot <text>"));
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Sets the description of the poll"));			
						return;						
					}
					
					if(pollData.setLoreLine(Integer.parseInt(args[3]), sb.toString()))
					{
						PollStorageManager.writePollData(pollData);
						sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Poll description line set to: " + TextFormatting.GOLD + replaceColorCodes(sb.toString())));
						return;						
					}
					else
					{
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Description slot not found!"));		
						return;
						
					}
				}
				if(args[2].equals("remove"))
				{

					if(!NumberUtils.isNumber(args[3]))
					{
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin description pollID remove slot"));
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Removes the description line of the poll"));				
						return;
					}
					
					if(pollData.removeLoreLine(Integer.parseInt(args[3])))
					{					
						PollStorageManager.writePollData(pollData);
						sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Poll description line removed"));
						return;
					}
					else
					{
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Description slot not found!"));		
						return;							
					}
				}
			}
			if(args[0].equals("time"))
			{
				if(args.length < 3)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin time pollID time"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Time format example: 10d10m for a 10 days and 10 minutes long poll"));	
					return;
				}
				PollData pollData = PollStorageManager.getPollData(args[1]);
				if(pollData == null)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll not found!"));
					return;
				}
				long newTime = parsePeriod(args[2]);
				pollData.setPollTimeSpan(newTime);
				PollStorageManager.writePollData(pollData);
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Poll duration set to: " + TextFormatting.GOLD + DurationFormatUtils.formatDuration(pollData.getPollTimeSpan(), "d' days 'H' hours 'm' minutes 's' seconds'", true)));
				return;
			}
			if(args[0].equals("interval"))
			{
				if(args.length < 3)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin interval pollID interval"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Sets the poll broadcast interval, no broadcast if left 0"));	
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Time format example: 10d10m for a 10 days and 10 minutes long poll"));	
					return;
				}
				PollData pollData = PollStorageManager.getPollData(args[1]);
				if(pollData == null)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll not found!"));
					return;
				}
				long newTime = parsePeriod(args[2]);
				pollData.setPollMessageInterval(newTime);
				PollStorageManager.writePollData(pollData);
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Poll message interval set to: " + TextFormatting.GOLD + DurationFormatUtils.formatDuration(pollData.getPollMessageInterval(), "d' days 'H' hours 'm' minutes 's' seconds'", true)));
				return;
			}
			if(args[0].equals("addoption"))
			{
				if(args.length < 3)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin addoption pollID title"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Add option to poll, uses item in hand for icon, sign if nothing"));	
					return;
				}
				PollData pollData = PollStorageManager.getPollData(args[1]);
				if(pollData == null)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll not found!"));
					return;
				}
				StringBuilder sb = new StringBuilder();
				for(int x = 2; x < args.length; x++)
				{
					sb.append(args[x]);
					if(x != args.length-1)
					{
						sb.append(" ");
					}
				}
				String itemName = Items.SIGN.getRegistryName().toString();
				String itemNBT = "";
				int itemMeta = 0;
				if(sender instanceof EntityPlayerMP)
				{
					EntityPlayerMP player = (EntityPlayerMP) sender;
					if(player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.AIR)
					{
					}
					else
					{
						itemName = player.getHeldItem(EnumHand.MAIN_HAND).getItem().getRegistryName().toString();
						if(player.getHeldItem(EnumHand.MAIN_HAND).hasTagCompound())
						{
							itemNBT = player.getHeldItem(EnumHand.MAIN_HAND).getTagCompound().toString();							
						}
						if(player.getHeldItem(EnumHand.MAIN_HAND).getMetadata() != 0)
						{
							itemMeta = player.getHeldItem(EnumHand.MAIN_HAND).getMetadata();
						}
					}
				}
				pollData.addPollOption(new PollOption(sb.toString(), itemName, itemNBT, itemMeta, 0, new ArrayList<PollPlayer>()));
				PollStorageManager.writePollData(pollData);
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Option added: " + TextFormatting.GOLD + sb.toString() + " - " + itemName));
				return;
			}
			if(args[0].equals("removeoption"))
			{
				if(args.length < 3)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin removeoption pollID number"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Removes the poll option, counting left to right"));
					return;
				}
				PollData pollData = PollStorageManager.getPollData(args[1]);
				if(pollData == null)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll not found!"));
					return;
				}
				if(!NumberUtils.isNumber(args[2]))
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin removeoption pollID number"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Removes the poll option, counting left to right"));				
					return;
				}
				if(pollData.removePollOption(Integer.parseInt(args[2])))
				{
					PollStorageManager.writePollData(pollData);
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Option removed"));
					return;
				}
				else
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "No option found!"));		
					return;
				}
			}
			if(args[0].equals("numbervotes"))
			{
				if(args.length < 3)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin numbervotes pollID number"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Sets the number of votes allowed"));
					return;
				}
				PollData pollData = PollStorageManager.getPollData(args[1]);
				if(pollData == null)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll not found!"));
					return;
				}
				if(!NumberUtils.isNumber(args[2]))
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin numbervotes pollID number"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Sets the number of votes allowed"));		
					return;
				}
				pollData.setNumberVotes(Integer.parseInt(args[2]));
				PollStorageManager.writePollData(pollData);
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Number of votes set to: " + TextFormatting.GOLD + pollData.getNumberVotes()));
				return;
			}
			if(args[0].equals("preview"))
			{
				if(args.length < 2)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin preview pollID"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Previews the poll as if using /poll"));
					return;
				}
				PollData pollData = PollStorageManager.getPollData(args[1]);
				if(pollData == null)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll not found!"));
					return;
				}
				UIManager.openUIForcefully((EntityPlayerMP) sender, GUIHelper.getPollVoteMenu(pollData, (EntityPlayerMP) sender));
				return;
			}
			if(args[0].equals("results"))
			{
				if(args.length < 2)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin results pollID"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Shows poll results, hover for playernames"));	
					return;
				}
				PollData pollData = PollStorageManager.getPollData(args[1]);
				if(pollData == null)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll not found!"));
					return;
				}
				sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Poll results for: " + TextFormatting.GOLD + pollData.getPollTitle()));
				int optionCount = 1;
				for(PollOption pollOption : pollData.getLstPollOptions())
				{
					TextComponentTranslation chatTrans = new TextComponentTranslation("", new Object());
					chatTrans.appendSibling(new TextComponentString(TextFormatting.GREEN + "Option " + optionCount +": "));
					chatTrans.appendSibling(GUIHelper.getPollResultToolTip(pollOption));
					chatTrans.appendSibling(new TextComponentString(TextFormatting.GRAY + " - " + TextFormatting.GOLD + pollOption.getTotalVotes() + TextFormatting.AQUA + " vote(s)"));
					sender.sendMessage(chatTrans);
					optionCount++;
				}
				return;
			}
			if(args[0].equals("info"))
			{
				if(args.length < 2)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin info pollID"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Shows poll configuration"));	
					return;
				}
				PollData pollData = PollStorageManager.getPollData(args[1]);
				if(pollData == null)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll not found!"));
					return;
				}
				sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Current Poll Configuration:"));
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Poll ID: " + TextFormatting.RED + pollData.getPollID()));
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Title: " + TextFormatting.RED + pollData.getPollTitle()));
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Duration: " + TextFormatting.RED + DurationFormatUtils.formatDuration(pollData.getPollTimeSpan(), "d' days 'H' hours 'm' minutes 's' seconds'", true)));
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Time Left: " + TextFormatting.RED + DurationFormatUtils.formatDuration(pollData.getPollTimeLeft(),"d' days 'H' hours 'm' minutes 's' seconds'", true)));
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Message Interval: " + TextFormatting.RED + DurationFormatUtils.formatDuration(pollData.getPollMessageInterval(),"d' days 'H' hours 'm' minutes 's' seconds'", true)));
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Next Message: " + TextFormatting.RED + DurationFormatUtils.formatDuration(pollData.getNextMessageLeftTime(),"d' days 'H' hours 'm' minutes 's' seconds'", true)));
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Allowed Votes: " + TextFormatting.RED + pollData.getNumberVotes()));
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Same Option Allowed: " + TextFormatting.RED + pollData.isAllowSameOptionVote()));
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Results Allowed: " + TextFormatting.RED + pollData.isAllowResults()));
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Vote Total: " + TextFormatting.RED + pollData.getTotalVotes()));
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Poll Options: " + TextFormatting.RED + pollData.getLstPollOptions().size()));
				
				return;
			}
			if(args[0].equals("delete"))
			{
				if(args.length < 2)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin delete pollID"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Permanently deletes the poll and results"));	
					return;
				}
				PollData pollData = PollStorageManager.getPollData(args[1]);
				if(pollData == null)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll not found!"));
					return;
				}
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Poll deleted: " + TextFormatting.GOLD + pollData.getPollID()));
				PollStorageManager.removePollData(pollData);
				return;
			}
			if(args[0].equals("reload"))
			{
				if(args.length < 1)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin reload"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Reloads PollPoll storage"));	
					return;
				}
				PollStorageManager.loadStorage();
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "PollPoll storage reloaded!"));
				return;
			}
			if(args[0].equals("clearresults"))
			{
				if(args.length < 2)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin clearresults pollID"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Clears all votes from specified poll"));	
					return;
				}
				PollData pollData = PollStorageManager.getPollData(args[1]);
				if(pollData == null)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll not found!"));
					return;
				}
				for(PollOption pollOption : pollData.getLstPollOptions())
				{
					pollOption.getLstPollPlayers().clear();
					pollOption.setTotalVotes(0);
				}
				PollStorageManager.writePollData(pollData);
				sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Cleared results for: " + TextFormatting.GOLD + pollData.getPollID()));
				return;
			}
			if(args[0].equals("allowsamevote"))
			{
				if(args.length < 3)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin allowsamevote pollID true/false"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Set whether to allow a player to vote an option multiple times"));	
					return;
				}
				PollData pollData = PollStorageManager.getPollData(args[1]);
				if(pollData == null)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll not found!"));
					return;
				}
				if(args[2].equals("true") || args[2].equals("false"))
				{
					if(args[2].equals("true"))
					{
						pollData.setAllowSameOptionVote(true);						
					}
					else
					{
						pollData.setAllowSameOptionVote(false);
					}
					PollStorageManager.writePollData(pollData);
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Set allowsamevote to: " + TextFormatting.GOLD + pollData.isAllowSameOptionVote()));
					return;
				}
				else
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin allowsamevote pollID true/false"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Set whether to allow a player to vote an option multiple times"));	
					return;
				}
			}
			if(args[0].equals("allowresults"))
			{
				if(args.length < 3)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin allowresults pollID true/false"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Set whether to allow a player to check results"));	
					return;
				}
				PollData pollData = PollStorageManager.getPollData(args[1]);
				if(pollData == null)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll not found!"));
					return;
				}
				if(args[2].equals("true") || args[2].equals("false"))
				{
					if(args[2].equals("true"))
					{
						pollData.setAllowResults(true);						
					}
					else
					{
						pollData.setAllowResults(false);
					}
					PollStorageManager.writePollData(pollData);
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Set allowresults to: " + TextFormatting.GOLD + pollData.isAllowResults()));
					return;
				}
				else
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin allowresults pollID true/false"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Set whether to allow a player to check results"));	
					return;
				}
			}
			if(args[0].equals("startpoll"))
			{
				if(args.length < 2)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin startpoll pollID"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Starts poll, broadcasts and sets end time"));	
					return;
				}
				PollData pollData = PollStorageManager.getPollData(args[1]);
				if(pollData == null)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll not found!"));
					return;
				}
				if(pollData.isPollActive())
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll already active!"));
					return;					
				}
				pollData.setPollActive(true);
				pollData.setPollEndDate(System.currentTimeMillis() + pollData.getPollTimeSpan());
				if(pollData.getPollMessageInterval() > 0)
				{
					pollData.setPollNextMessageTime(System.currentTimeMillis() + pollData.getPollMessageInterval());
				}

				TextComponentTranslation chatTrans = new TextComponentTranslation("", new Object());
				chatTrans.appendSibling(new TextComponentString(TextFormatting.AQUA + "[Poll] " + TextFormatting.LIGHT_PURPLE + "Use or click: "));
				chatTrans.appendSibling(GUIHelper.getClickableCommand(pollData));
				chatTrans.appendSibling(new TextComponentString(TextFormatting.LIGHT_PURPLE + " to vote!"));
				
				for(EntityPlayerMP player : server.getPlayerList().getPlayers())
				{
//					if(pollData.playerVotesPoll(player.getUniqueID().toString()) == pollData.getNumberVotes())
//					{
//						continue;
//					}
					
					player.sendMessage(new TextComponentString(TextFormatting.AQUA + "[Poll] " + TextFormatting.LIGHT_PURPLE + "A poll was started: " + TextFormatting.WHITE + pollData.getPollTitle()));
					player.sendMessage(chatTrans);
				}
				PollStorageManager.writePollData(pollData);
				return;
			}
			if(args[0].equals("stoppoll"))
			{
				if(args.length < 2)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin stoppoll pollID"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Stops poll and broadcasts"));	
					return;
				}
				PollData pollData = PollStorageManager.getPollData(args[1]);
				if(pollData == null)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll not found!"));
					return;
				}
				if(!pollData.isPollActive())
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll is not active!"));
					return;					
				}
				pollData.setPollActive(false);
				pollData.setPollEndDate(0);
				if(pollData.getPollMessageInterval() > 0)
				{
					pollData.setPollNextMessageTime(0);
				}
				for(EntityPlayerMP player : server.getPlayerList().getPlayers())
				{
					player.sendMessage(new TextComponentString(TextFormatting.AQUA + "[Poll] " + TextFormatting.RED + "A poll has ended: " + TextFormatting.WHITE + pollData.getPollTitle()));
				}
				PollStorageManager.writePollData(pollData);
				return;
			}
			if(args[0].equals("broadcast"))
			{
				if(args.length < 2)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /polladmin broadcast pollID"));
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Broadcasts the specified poll if active"));	
					return;
				}
				PollData pollData = PollStorageManager.getPollData(args[1]);
				if(pollData == null)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll not found!"));
					return;
				}
				if(!pollData.isPollActive())
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll is not active!"));
					return;
				}

				TextComponentTranslation chatTrans = new TextComponentTranslation("", new Object());
				chatTrans.appendSibling(new TextComponentString(TextFormatting.AQUA + "[Poll] " + TextFormatting.LIGHT_PURPLE + "Use or click: "));
				chatTrans.appendSibling(GUIHelper.getClickableCommand(pollData));
				chatTrans.appendSibling(new TextComponentString(TextFormatting.LIGHT_PURPLE + " to vote!"));
				for(EntityPlayerMP player : server.getPlayerList().getPlayers())
				{
//					if(pollData.playerVotesPoll(player.getUniqueID().toString()) == pollData.getNumberVotes())
//					{
//						continue;
//					}
					player.sendMessage(new TextComponentString(TextFormatting.AQUA + "[Poll] " + TextFormatting.GREEN + "A poll is active: " + TextFormatting.WHITE + pollData.getPollTitle()));
					player.sendMessage(chatTrans);
				}
				return;
			}
			sendCommandOptionHelp(sender);
		}
		else
		{
			sender.sendMessage(new TextComponentString(TextFormatting.RED + "You don't have permission to use this command"));
			return;
		}

	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) 
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) 
	{	
		if(args.length == 1)
		{
			ArrayList<String> lstTabComplete = new ArrayList<>();
			lstTabComplete.add("create");
			lstTabComplete.add("title");
			lstTabComplete.add("description");
			lstTabComplete.add("time");
			lstTabComplete.add("interval");
			lstTabComplete.add("addoption");
			lstTabComplete.add("removeoption");
			lstTabComplete.add("numbervotes");
			lstTabComplete.add("allowsamevote");
			lstTabComplete.add("allowresults");
			lstTabComplete.add("preview");
			lstTabComplete.add("results");
			lstTabComplete.add("info");
			lstTabComplete.add("delete");
			lstTabComplete.add("reload");
			lstTabComplete.add("clearresults");
			lstTabComplete.add("startpoll");
			lstTabComplete.add("stoppoll");
			lstTabComplete.add("broadcast");
			return CommandBase.getListOfStringsMatchingLastWord(args, lstTabComplete);
		}
		
		if(args.length == 2)
		{
			ArrayList<String> lstTabComplete = new ArrayList<>();
			for(PollData pollData : PollStorageManager.getPollDataList())
			{
				lstTabComplete.add(pollData.getPollID());
			}
			return CommandBase.getListOfStringsMatchingLastWord(args, lstTabComplete);
		}
		return Collections.emptyList();
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static long parsePeriod(String period)
	{
	    period = period.toLowerCase(Locale.ENGLISH);
	    Matcher matcher = periodPattern.matcher(period);
	    Instant instant=Instant.EPOCH;
	    while(matcher.find()){
	        int num = Integer.parseInt(matcher.group(1));
	        String typ = matcher.group(2);
	        switch (typ) {
	        	case "m":
	        		instant=instant.plus(Duration.ofMinutes(num));
	        		break;
	            case "h":
	                instant=instant.plus(Duration.ofHours(num));
	                break;
	            case "d":
	                instant=instant.plus(Duration.ofDays(num));
	                break;
	            case "w":
	                instant=instant.plus(Period.ofWeeks(num));
	                break;
	        }
	    }
	    return instant.toEpochMilli();
	}

	private void sendCommandOptionHelp(ICommandSender sender)
	{

		sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "PollPoll Command Options:"));
		sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/polladmin create " + TextFormatting.GOLD + "- Create new poll"));
		sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/polladmin title " + TextFormatting.GOLD + "- Set poll title"));
		sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/polladmin lore " + TextFormatting.GOLD + "- Modify poll description"));
		sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/polladmin time " + TextFormatting.GOLD + "- Set poll duration"));
		sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/polladmin interval " + TextFormatting.GOLD + "- Set poll announce interval"));
		sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/polladmin addoption " + TextFormatting.GOLD + "- Add poll option"));
		sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/polladmin removeoption " + TextFormatting.GOLD + "- Remove poll option"));
		sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/polladmin numbervotes " + TextFormatting.GOLD + "- Set allowed number of votes"));
		sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/polladmin allowsamevote " + TextFormatting.GOLD + "- Allow multiple votes on same option"));
		sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/polladmin allowresults " + TextFormatting.GOLD + "- Allow players to check results"));
		sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/polladmin preview " + TextFormatting.GOLD + "- Preview poll"));
		sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/polladmin results " + TextFormatting.GOLD + "- Check poll results"));
		sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/polladmin info " + TextFormatting.GOLD + "- Check poll config"));
		sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/polladmin startpoll " + TextFormatting.GOLD + "- Start poll"));
		sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/polladmin stoppoll " + TextFormatting.GOLD + "- Stop poll"));
		sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/polladmin broadcast " + TextFormatting.GOLD + "- Broadcast poll manually"));
		sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/polladmin delete " + TextFormatting.GOLD + "- Delete poll completely"));
		sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/polladmin reload " + TextFormatting.GOLD + "- Reload PollPoll storage"));
		sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "/polladmin clearresults " + TextFormatting.GOLD + "- Remove all votes from poll"));
	}
	
	private String replaceColorCodes(String string)
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
