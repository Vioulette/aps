package aps;

import java.util.List;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class Validation {
	
	private String consumerKeyStr = "HlRwUgUaBmWfJsNzlHqIgC7Fp";
	private String consumerSecretStr = "SfXOv6mJhCDdJ6dGqb2cU4kSfuOwXArh54NsBnrRazlVratcFg";
	private String app_twitter_key = "2317121491-iiJ45yU2RMuro5uL3lm52MIvXSkCrQ2Ix8hJFni";
	private String app_twitter_secret = "vlkCMFkgS8ujG7bXriCUgbxR9ag05Doseo3RP0IDEF3FM";
	
	private twitter4j.Twitter twitter;
	//private long id_twitter;
	
	public Validation () {
		//System.out.println("validation method connected");
		// Getting an instance of Twitter4J
				ConfigurationBuilder cb = new ConfigurationBuilder();
				cb.setDebugEnabled(true)
				  .setOAuthConsumerKey(consumerKeyStr)
				  .setOAuthConsumerSecret(consumerSecretStr)
				  .setOAuthAccessToken(app_twitter_key)
				  .setOAuthAccessTokenSecret(app_twitter_secret);
				TwitterFactory tf = new TwitterFactory(cb.build());
				twitter = tf.getInstance();
	}
	
	public void getUser (Long tweet_id) throws TwitterException, IOException {
		//System.out.println("getUser method connected");
		//double tweet_id = tweetID;
		//id_twitter = twitter.getId();
		//User user2 = twitter.showUser(id_twitter);
		//List<Status> timeline = twitter.getUserTimeline(user2.getId());
		//for (int i=0; i<timeline.size();i++) {
			//System.out.println(timeline.get(i));
		//}
		//long id = 588362661819240448L;
		
		//String write_file = "/Users/Mia/Desktop/History_HD_messages.txt";
		//FileWriter writer = new FileWriter(write_file, true);
		//PrintWriter print_line = new PrintWriter(writer);
		
		Status status = twitter.showStatus(tweet_id);
		User user = status.getUser();
		System.out.println("Name: " + user.getName());
		//print_line.write("Name: " + user.getName() + "\n");
		System.out.println("screenName: " + user.getScreenName());
		System.out.println("id: " + user.getId());
		//print_line.write("screenName: " + user.getScreenName() + "\n");
		System.out.println(status.getCreatedAt());
		//print_line.write(status.getCreatedAt() + "\n");
		System.out.println("Root Tweet: " + status.getText());
		//print_line.write("Root Tweet: " + status.getText() + "\n");
		//print_line.write("---"+ "\n");
		List<Status> timeline = twitter.getUserTimeline(user.getId());
		
		if (timeline.size()<=20) {
			for (int i=0; i<timeline.size(); i++)
			{
				Status history = timeline.get(i);
				System.out.println(history.getCreatedAt());
				//print_line.write(history.getCreatedAt() + "\n");
				System.out.println(history.getText());
				//print_line.write(history.getText() + "\n");
				System.out.println("---");
				//print_line.write("---" + "\n");
			}
			System.out.println("======");
			//print_line.write("================");
			//print_line.close();
			}
		
		else {
			for (int i=timeline.size()-20; i<timeline.size(); i++)
			{
				Status history = timeline.get(i);
				System.out.println(history.getCreatedAt());
				//print_line.write(history.getCreatedAt() + "\n");
				System.out.println(history.getText());
				//print_line.write(history.getText() + "\n");
				System.out.println("---");
				//print_line.write("---" + "\n");
			}
			System.out.println("======");
			//print_line.write("================");
			//print_line.close();
			}	
	}
	
	public void findStatuses(String text) throws TwitterException {
		System.out.println("finding possible users");
		Query query = new Query(text);
		query.setLang("en");
		query.setCount(100);
		QueryResult result =twitter.search(query);
		List<Status> tweets = result.getTweets();
		for (Status tweet : tweets) {
			System.out.println(tweet.getId() + " " + tweet.getText());
		}
	}

	public void saveCases() throws TwitterException, IOException {
		
		String write_file = "/Users/Mia/Desktop/Validation_cases.json";
		FileWriter writer = new FileWriter(write_file, true);
		PrintWriter print_line = new PrintWriter(writer);
	
		ResponseList<Status> getUser = twitter.getUserTimeline("@Rcnaway");
		Status instance = getUser.get(1);
		User user = instance.getUser();
		print_line.write(user + "n");
		print_line.write("=========");
		
		for (int i=1; i<11; i++) {
			Paging page = new Paging(i,50);
			ResponseList<Status> trial = twitter.getUserTimeline("@Rcnaway", page);
			print_line.write("==========Page: " + i + "==========" + "\n");
			for (Status s: trial) {
				print_line.write(s + "\n");
				print_line.write("==========" + "\n");
			}
			
		}
		print_line.close();
	}

}
