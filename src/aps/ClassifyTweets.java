package aps;

import java.sql.SQLException;

import twitter4j.TwitterException;

public class ClassifyTweets extends Thread{

	public ClassifyTweets () {
		this.start() ;
	}
	
	
	public void run () {   
		
		Twitter twitter = new Twitter();
		System.out.println("ClassifyTweets active");
		
		try {
			
			twitter.classifyTweets();
			// Pause du thread pendant 5 heures
			System.out.println("ClassifyTweets sleeping");
			ClassifyTweets.sleep(18000000);
			
		} catch (TwitterException | SQLException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		this.run();
	}
}