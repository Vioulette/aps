package aps;

import java.sql.SQLException;

import facebook4j.FacebookException;

public class CollectFeed extends Thread{

	public CollectFeed () {
		this.start() ;
	}
	
	
	public void run () {   
		Facebook facebook = new Facebook();
		System.out.println("CollectFeed active");
		
		try {
			facebook.collectFeed();
			// Wait 2 minutes before next call
			System.out.println("CollectFeed sleeping");
			CollectFeed.sleep(120000);
		} catch (FacebookException | SQLException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.run();
	}
}
