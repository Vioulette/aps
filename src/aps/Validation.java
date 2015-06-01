package aps;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private PostgresHelper client;
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
	
	public void showId(String screenName) throws TwitterException, IOException, SQLException {
		client = new PostgresHelper(
				DbContract.HOST, 
				DbContract.DB_NAME,
				DbContract.USERNAME,
				DbContract.PASSWORD);	
		try {
			if (client.connect()) {
				System.out.println("DB connected for saving validation cases");
			}	
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		ResponseList<Status> getUser = twitter.getUserTimeline(screenName);
		Status instance = getUser.get(1);
		User user = instance.getUser();
		System.out.println(user.getId());
		System.out.println(user.getStatusesCount());
		
	}

	public void saveCase(long id) throws TwitterException, IOException, SQLException {
		
		client = new PostgresHelper(
				DbContract.HOST, 
				DbContract.DB_NAME,
				DbContract.USERNAME,
				DbContract.PASSWORD);	
		try {
			if (client.connect()) {
				System.out.println("DB connected for saving validation cases");
			}	
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		

		User user = twitter.showUser(id);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", user.getId());
		map.put("user_id", null);
		map.put("name", user.getName());
		map.put("profile_image_url", user.getProfileImageURL());
		map.put("created_at", user.getCreatedAt().toString());
		map.put("location", user.getLocation());
		map.put("favourites_count", user.getFavouritesCount());
		map.put("listed_count", user.getListedCount());
		map.put("followers_count", user.getFollowersCount());
		map.put("verified", null);
		map.put("geo_enabled", false);
		map.put("time_zone", user.getTimeZone());
		map.put("description", null);
		map.put("statuses_count", user.getStatusesCount());
		map.put("friends_count", user.getFriendsCount());
		map.put("following", null);
		map.put("screen_name", user.getScreenName());
		map.put("created", user.getCreatedAt().toString());
		map.put("modified", null);
//		map.put("id", user.getId());
//		map.put("user_id", null);
//		map.put("name", user.getName());
//		map.put("profile_image_url", user.getProfileImageURL());
//		map.put("created_at", user.getCreatedAt().toString());
//		map.put("location", user.getLocation());
//		map.put("favourites_count", user.getFavouritesCount());
//		map.put("listed_count", user.getListedCount());
//		map.put("followers_count", user.getFollowersCount());
//		map.put("verified", null);
//		map.put("geo_enabled", false);
//		map.put("time_zone", user.getTimeZone());
//		map.put("description", user.getDescription());
//		map.put("statuses_count", user.getStatusesCount());
//		map.put("friends_count", user.getFriendsCount());
//		map.put("following", null);
//		map.put("screen_name", user.getScreenName());
//		map.put("created", user.getCreatedAt().toString());
//		map.put("modified", null);
		client.insert("validation_users", map);
		System.out.println("User saved");
		

		for (int p=1; p<11; p++) {
			Paging page = new Paging(p,50);
			ResponseList<Status> timeline = twitter.getUserTimeline(id, page);
			int i = 0;
			for (Status s : timeline) {
				Status post = timeline.get(i);
				Map<String, Object> map2 = new HashMap<String, Object>();
				map2.put("id", post.getId());
				map2.put("user_id", post.getUser().getId());
				map2.put("created_at", post.getCreatedAt().toString());
				map2.put("in_reply_to_user_id", post.getInReplyToUserId());
				map2.put("retweet_count", post.getRetweetCount());
				map2.put("in_reply_to_status_id", post.getInReplyToStatusId());
				map2.put("text_", post.getText().replace("'"," "));
				map2.put("in_reply_to_screen_name", post.getInReplyToScreenName());
				map2.put("place", post.getPlace());
				map2.put("source", post.getSource());
				map2.put("full_content", null);
				map2.put("created", post.getCreatedAt().toString());
				map2.put("modified", null);
				client.insert("validation_statuses", map2);
				i++;
				}
		}
		System.out.println("Timeline saved");
		}
		
		
		
//		String write_file = "/Users/Mia/Desktop/Validation_cases.json";
//		FileWriter writer = new FileWriter(write_file, true);
//		PrintWriter print_line = new PrintWriter(writer);
//	
//		ResponseList<Status> getUser = twitter.getUserTimeline("@suicidalquxxn");
//		Status instance = getUser.get(1);
//		User user = instance.getUser();
//		print_line.write(user + "n");
//		print_line.write("=========");
//		
//		for (int i=1; i<11; i++) {
//			Paging page = new Paging(i,50);
//			ResponseList<Status> trial = twitter.getUserTimeline("@suicidalquxxn", page);
//			print_line.write("==========Page: " + i + "==========" + "\n");
//			for (Status s: trial) {
//				print_line.write(s + "\n");
//				print_line.write("==========" + "\n");
//			}
//			
//		}
//		print_line.close();
	
	public void collectUsers(String text) throws TwitterException, IOException {
		
		ResponseList<User> users = twitter.searchUsers(text, 1);
		System.out.println(users.size());
		for (int i=0; i<users.size(); i++) {
			User user = users.get(i);
			System.out.println(user.getName());
			System.out.println(user.getScreenName());
			System.out.println("=====");
		}
		
	}

}
