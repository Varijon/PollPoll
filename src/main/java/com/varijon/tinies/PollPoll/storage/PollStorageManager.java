package com.varijon.tinies.PollPoll.storage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.varijon.tinies.PollPoll.object.PollData;

public class PollStorageManager 
{
	static ArrayList<PollData> lstPollData = new ArrayList<PollData>();
	
	public static boolean loadStorage()
	{
		String basefolder = new File("").getAbsolutePath();
        String source = basefolder + "/config/PollPoll";
		try
		{
			Gson gson = new Gson();
			
			File dir = new File(source);
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			
			lstPollData.clear();
			
			for(File file : dir.listFiles())
			{
				FileReader reader = new FileReader(file);
				
				PollData poll = gson.fromJson(reader, PollData.class);
								
				lstPollData.add(poll);
				reader.close();
			}
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	public static void writePollData(PollData pollData)
	{
		String basefolder = new File("").getAbsolutePath();
        String source = basefolder + "/config/PollPoll";
		
		try
		{
			File dir = new File(source);
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
					
			FileWriter writer = new FileWriter(source + "/" + pollData.getPollID() + ".json");
			gson.toJson(pollData, writer);
			writer.close();
		}
			
		catch (Exception ex) 
		{
		    ex.printStackTrace();
		}
	}
	
	public static void deletePollData(PollData pollData)
	{
		String basefolder = new File("").getAbsolutePath();
        String source = basefolder + "/config/PollPoll";
		
		try
		{
			File dir = new File(source);
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			
			File deleteFile = new File(source + "/" + pollData.getPollID() + ".json");
			deleteFile.delete();
		}
			
		catch (Exception ex) 
		{
		    ex.printStackTrace();
		}
	}
	
	public static PollData getPollData(String pollID)
	{
		for(PollData pollData : lstPollData)
		{
			if(pollData.getPollID().equals(pollID))
			{
				return pollData;
			}
		}
		return null;
	}
	
	public static void removePollData(PollData pollData)
	{
		deletePollData(pollData);
		lstPollData.remove(pollData);
	}
	
	public static PollData addPollData(PollData pollData)
	{
		lstPollData.add(pollData);
		return pollData;
	}
	
	
//	public static void saveChangesToFile()
//	{
//		String basefolder = new File("").getAbsolutePath();
//        String source = basefolder + "/config/CatchEventReport";
//		
//		try
//		{
//			File dir = new File(source);
//			if(!dir.exists())
//			{
//				dir.mkdirs();
//			}
//			if(dir.listFiles().length == 0)
//			{
//				ArrayList<EventPokemon> lstEventPokemon = new ArrayList<EventPokemon>();
//				lstEventPokemon.add(new EventPokemon(EnumSpecies.Salandit, "winter", 10));
//				lstEventPokemon.add(new EventPokemon(EnumSpecies.Cutiefly, "winter", 20));
//				EventConfig event = new EventConfig("Example", "exampleTag", "Welcome to the Example event", lstEventPokemon);
//		
//				Gson gson = new GsonBuilder().setPrettyPrinting().create();
//					
//				FileWriter writer = new FileWriter(source + "/Example.json");
//				gson.toJson(event, writer);
//				writer.close();
//			}
//		}
//			
//		catch (Exception ex) 
//		{
//		    ex.printStackTrace();
//		}
//	}
	
	public static ArrayList<PollData> getPollDataList()
	{
		return lstPollData;
	}
}
