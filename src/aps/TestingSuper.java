package aps;

import java.io.IOException;
import twitter4j.TwitterException;

public class TestingSuper extends Thread {

	public TestingSuper () {
		this.start() ;
	}
	
	
	public void run () {   
		Testing testing = new Testing();
		System.out.println("TestingSuper active");

		for (int i=0; i<496; i++) {
			try {
				boolean hitsRemaining = testing.rateLimit();
				//System.out.println(hitsRemaining);
				if(hitsRemaining == true) {
					System.out.println("sleeping");
					TestingSuper.sleep(900000);
				}
				else {
					testing.collectMessages(i);
				}
			} catch (TwitterException | InterruptedException | IOException e) {
				System.out.println("exception");
			}	
		}
		
		try {
			testing.writeMessages();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
