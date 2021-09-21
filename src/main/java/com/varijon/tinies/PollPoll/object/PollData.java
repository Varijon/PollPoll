package com.varijon.tinies.PollPoll.object;

import java.util.ArrayList;

public class PollData 
{
	String pollID;
	String pollTitle;
	long pollTimeSpan;
	long pollEndDate;
	long pollMessageInterval;
	long pollNextMessageTime;
	boolean pollActive;
	boolean allowResults;
	boolean allowSameOptionVote;
	int numberVotes;
	ArrayList<PollOption> lstPollOptions;
	ArrayList<String> lstLore;
	
	public PollData(String pollID, String pollTitle, long pollTimeSpan, int numberVotes, long pollEndDate, long pollMessageInterval, long pollNextMessageTime, boolean pollActive, boolean allowSameOptionVote, boolean allowResults, ArrayList<PollOption> lstPollOptions, ArrayList<String> lstLore)
	{
		this.pollID = pollID;
		this.pollTitle = pollTitle;
		this.pollTimeSpan = pollTimeSpan;
		this.pollEndDate = pollEndDate;
		this.pollActive = pollActive;
		this.allowResults = allowResults;
		this.allowSameOptionVote = allowSameOptionVote;
		this.lstPollOptions = lstPollOptions;
		this.numberVotes = numberVotes;
		this.pollMessageInterval = pollMessageInterval;
		this.pollNextMessageTime = pollNextMessageTime;
		this.lstLore = lstLore;			
	}
	
	public PollData(String pollID, String pollTitle, long pollTimeSpan, int numberVotes, long pollEndDate, long pollMessageInterval, long pollNextMessageTime, boolean pollActive, boolean allowSameOptionVote, boolean allowResults, ArrayList<PollOption> lstPollOptions)
	{
		this.pollID = pollID;
		this.pollTitle = pollTitle;
		this.pollTimeSpan = pollTimeSpan;
		this.pollEndDate = pollEndDate;
		this.pollActive = pollActive;
		this.allowResults = allowResults;
		this.allowSameOptionVote = allowSameOptionVote;
		this.lstPollOptions = lstPollOptions;
		this.numberVotes = numberVotes;
		this.pollMessageInterval = pollMessageInterval;
		this.pollNextMessageTime = pollNextMessageTime;
		this.lstLore = new ArrayList<>();
	}

	public String getPollID() {
		return pollID;
	}

	public void setPollID(String pollID) {
		this.pollID = pollID;
	}

	public long getPollEndDate() {
		return pollEndDate;
	}

	public void setPollEndDate(long pollEndDate) {
		this.pollEndDate = pollEndDate;
	}

	public boolean isPollActive() {
		return pollActive;
	}

	public void setPollActive(boolean pollActive) {
		this.pollActive = pollActive;
	}

	public ArrayList<PollOption> getLstPollOptions() {
		return lstPollOptions;
	}

	public void setLstPollOptions(ArrayList<PollOption> lstPollOptions) {
		this.lstPollOptions = lstPollOptions;
	}
	
	public void addPollOption(PollOption pollOption)
	{
		lstPollOptions.add(pollOption);
	}
	

	public void addLoreItem(String lore)
	{
		if(lstLore == null)
		{
			this.lstLore = new ArrayList<>();
		}
		lstLore.add(lore);
	}
	
	public boolean removeLoreLine(int index)
	{
		if(lstLore == null)
		{
			return false;
		}
		if(index > lstLore.size())
		{
			return false;			
		}
		lstLore.remove(index-1);
		return true;
	}

	public boolean setLoreLine(int index, String lore)
	{
		if(lstLore == null)
		{
			return false;
		}
		if(index > lstLore.size())
		{
			return false;			
		}
		lstLore.set(index-1,lore);
		return true;
	}
	
	public boolean removePollOption(int index)
	{
		if(index > lstPollOptions.size())
		{
			return false;			
		}
		lstPollOptions.remove(index-1);
		return true;
	}

	public long getPollTimeSpan() {
		return pollTimeSpan;
	}

	public void setPollTimeSpan(long pollTimeSpan) {
		this.pollTimeSpan = pollTimeSpan;
	}

	public String getPollTitle() {
		return pollTitle;
	}

	public void setPollTitle(String pollTitle) {
		this.pollTitle = pollTitle;
	}

	public int getNumberVotes() {
		return numberVotes;
	}

	public void setNumberVotes(int numberVotes) {
		this.numberVotes = numberVotes;
	}

	public boolean isAllowSameOptionVote() {
		return allowSameOptionVote;
	}

	public void setAllowSameOptionVote(boolean allowSameOptionVote) {
		this.allowSameOptionVote = allowSameOptionVote;
	}
	
	public long getPollMessageInterval() {
		return pollMessageInterval;
	}

	public void setPollMessageInterval(long pollMessageInterval) {
		this.pollMessageInterval = pollMessageInterval;
	}

	public long getPollNextMessageTime() {
		return pollNextMessageTime;
	}

	public void setPollNextMessageTime(long pollNextMessageTime) {
		this.pollNextMessageTime = pollNextMessageTime;
	}
	
	

	public boolean isAllowResults() {
		return allowResults;
	}

	public void setAllowResults(boolean allowResults) {
		this.allowResults = allowResults;
	}

	public long getPollTimeLeft()
	{
		if(pollActive)
		{
			return pollEndDate - System.currentTimeMillis();
		}
		else
		{
			return pollTimeSpan;
		}
	}
	public long getNextMessageLeftTime()
	{
		if(pollMessageInterval == 0)
		{
			return pollMessageInterval;
		}
		if(pollActive)
		{
			return pollNextMessageTime - System.currentTimeMillis();
		}
		else
		{
			return pollMessageInterval;
		}
	}
	
	public int getTotalVotes()
	{
		int totalVotes = 0;
		
		for(PollOption option : lstPollOptions)
		{
			totalVotes += option.totalVotes;
		}
		
		return totalVotes;
	}
	
	public int playerVotesPoll(String uuid)
	{
		int playerVotes = 0;
		for(PollOption pollOption : lstPollOptions)
		{
			playerVotes += pollOption.getPlayerVotesOption(uuid);
		}
		return playerVotes;
	}

	public ArrayList<String> getLstLore() {
		return lstLore;
	}

	public void setLstLore(ArrayList<String> lstLore) {
		this.lstLore = lstLore;
	}
	
	
}
