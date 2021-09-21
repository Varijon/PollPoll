package com.varijon.tinies.PollPoll.object;

public class PollPlayer 
{
	String playerUUID;
	String playerName;
	
	public PollPlayer(String playerUUID, String playerName) 
	{
		this.playerUUID = playerUUID;
		this.playerName = playerName;
	}

	public String getPlayerUUID() {
		return playerUUID;
	}

	public void setPlayerUUID(String playerUUID) {
		this.playerUUID = playerUUID;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	
}
