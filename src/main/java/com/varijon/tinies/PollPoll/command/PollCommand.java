package com.varijon.tinies.PollPoll.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.varijon.tinies.PollPoll.gui.GUIHelper;
import com.varijon.tinies.PollPoll.object.PollData;
import com.varijon.tinies.PollPoll.object.PollOption;
import com.varijon.tinies.PollPoll.storage.PollStorageManager;

import ca.landonjw.gooeylibs2.api.UIManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;

public class PollCommand implements ICommand {

	private List aliases;
	
	public PollCommand()
	{
	   this.aliases = new ArrayList();
	   this.aliases.add("poll");
	   
	}
	
	@Override
	public int compareTo(ICommand arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "poll";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "poll";
	}

	@Override
	public List<String> getAliases() {
		// TODO Auto-generated method stub
		return this.aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException 
	{
		if(sender.canUseCommand(4, "pollpoll.poll"))
		{
			if(sender instanceof EntityPlayerMP)
			{
				if(args.length == 0)
				{
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage /poll pollname"));
					return;
				}
				if(args.length == 1)
				{
					PollData pollData = PollStorageManager.getPollData(args[0]);
					if(pollData == null)
					{
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll not found!"));
						return;
					}
					if(!pollData.isPollActive() && !pollData.isAllowResults())
					{
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Poll not found!"));
						return;						
					}

					if(!pollData.isPollActive() && pollData.isAllowResults())
					{
						//show poll results
						sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Poll results for: " + TextFormatting.GOLD + pollData.getPollTitle()));
						int optionCount = 1;
						for(PollOption pollOption : pollData.getLstPollOptions())
						{
							sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Option " + optionCount +": " + TextFormatting.YELLOW + pollOption.getOptionName() + TextFormatting.GRAY + " - " + TextFormatting.GOLD + pollOption.getTotalVotes() + TextFormatting.AQUA + " vote(s)"));
							optionCount++;
						}
						return;						
					}
					UIManager.openUIForcefully((EntityPlayerMP) sender, GUIHelper.getPollVoteMenu(pollData, (EntityPlayerMP) sender));
					return;
				}
				
			}
			return;
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
			for(PollData pollData : PollStorageManager.getPollDataList())
			{
				if(pollData.isPollActive() || pollData.isAllowResults())
				{
					lstTabComplete.add(pollData.getPollID());
				}
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
	
}
