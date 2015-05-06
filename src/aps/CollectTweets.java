package aps;

import java.sql.SQLException;
import twitter4j.TwitterException;

public class CollectTweets extends Thread {

	public CollectTweets () {
		this.start() ;
	}
	
	
	public void run () {   
		Twitter twitter = new Twitter();
		long timeRemaining=0;
		System.out.println("CollectTweets active");

		try {
			timeRemaining = twitter.rateLimit();
			if(timeRemaining==0){
				twitter.collectTweets();
				// Wait 1 minute before next call
				CollectTweets.sleep(60000);
			}
			else {
				System.out.println("CollectTweets sleeping");
				System.out.println("Twitter Time remaining = " + (timeRemaining * 1.66666667) / 100000 + " minutes");
				CollectTweets.sleep(timeRemaining);
			}
		} catch (TwitterException | SQLException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.run();
	}
}
