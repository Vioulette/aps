package aps;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;

import com.opencsv.CSVReader;

public class Main {

	/**
	 * 
	 * APS Server
	 * 
	 * Collect Facebook and Twitter informations
	 * Store users, informations, posts and statuses in DataBase  
	 * @throws FileNotFoundException 
	 * @throws TwitterException 
	 * @throws SQLException 
	 *
	 */
	    
	public static void main(String[] args) throws FileNotFoundException, IOException, TwitterException, SQLException {
		

		
		//CollectFeed collectFeed = new CollectFeed();
		//CollectTweets collectTweets = new CollectTweets();
	   // ClassifyTweets classifyTweets = new ClassifyTweets();
		//ClassifyFeed classifyFeed = new ClassifyFeed();
		
		//Martingale test for a stream of data
		//parameters: MartingaleTesting(epsilon, (1-epsilon), threshold, testing size)
		//MartingaleTesting1 MartingaleTesting1 = new MartingaleTesting1(.9, -.1, 5, 50);
		//MartingaleTesting MartingaleTesting = new MartingaleTesting(.8, -.2, 10f);
		
		//Trying to find validation data 
		
		//Validation validation = new Validation ();
		//validation.collectUsers("megan");
		//validation.showId("@MindofMange");
		//validation.saveCase(312429642L);
		//validation.findStatuses("I'm going to kill myself");
		//validation.getUser(603077130872754177L);
		
		//This class used to collect and assemble the messages and annotations for the NYC database of tweets
		//TestingSuper testing = new TestingSuper();
		
		//This class was created to save the time postings of a regular user
		//Simulation simulation = new Simulation ();
		
		//This class is being used to create the database of messages for annotation
		CorpusCollection corpusCollection = new CorpusCollection();
		
		
//		String file = "/Users/Mia/Desktop/HD_messages.csv";
//		CSVReader reader = new CSVReader (new FileReader(file));
//		List<String[]> allrows = reader.readAll();
//		reader.close();
//		String update;
//		Long[] clean_ids = new Long[84];
//		for (int i=1; i<allrows.size(); i++) {
//			update = Arrays.toString(allrows.get(i));
//			update = update.replace('[',' ');
//			update = update.replace(']',' ');
//			update = update.replaceAll(" ","");
//			long clean = Long.parseLong(update);
//			clean_ids[i] = clean;
//		}
		

//		long tweet_id = 14995373255L;
//		for (int i=1; i<clean_ids.length; i++) {
//		try {
//			validation.getUser(clean_ids[i]);
//		} catch (TwitterException e) {
//		}
//		continue;
//		}
	}

}
