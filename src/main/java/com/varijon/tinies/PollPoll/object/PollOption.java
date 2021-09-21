package com.varijon.tinies.PollPoll.object;

import java.util.ArrayList;

public class PollOption 
{
	String optionName;
	String itemName;
	String itemNBT;
	int itemMeta;
	int totalVotes;
	ArrayList<PollPlayer> lstPollPlayers;
	
	public PollOption(String optionName, String itemName, String itemNBT, int itemMeta, int totalVotes, ArrayList<PollPlayer> lstPollPlayers) 
	{
		this.optionName = optionName;
		this.itemName = itemName;
		this.itemNBT = itemNBT;
		this.itemMeta = itemMeta;
		this.lstPollPlayers = lstPollPlayers;
		this.totalVotes = totalVotes;
	}

	public String getOptionName() {
		return optionName;
	}

	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemNBT() {
		return itemNBT;
	}

	public void setItemNBT(String itemNBT) {
		this.itemNBT = itemNBT;
	}

	public ArrayList<PollPlayer> getLstPollPlayers() {
		return lstPollPlayers;
	}

	public void setLstPollPlayers(ArrayList<PollPlayer> lstPollPlayers) {
		this.lstPollPlayers = lstPollPlayers;
	}
	
	public void addPollPlayer(PollPlayer pollPlayer)
	{
		lstPollPlayers.add(pollPlayer);
		totalVotes = lstPollPlayers.size();
	}

	public int getTotalVotes() {
		return totalVotes;
	}

	public void setTotalVotes(int totalVotes) {
		this.totalVotes = totalVotes;
	}
	
	public int getItemMeta() {
		return itemMeta;
	}

	public void setItemMeta(int itemMeta) {
		this.itemMeta = itemMeta;
	}

	public int getPlayerVotesOption(String uuid)
	{
		int playerVotes = 0;
		for(PollPlayer pollPlayer : lstPollPlayers)
		{
			if(pollPlayer.playerUUID.equals(uuid))
			{
				playerVotes++;
			}
		}
		return playerVotes;
	}
	
}
