package aps;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class CorpusCollection {
	
	private PostgresHelper client;
	private String consumerKeyStr = "HlRwUgUaBmWfJsNzlHqIgC7Fp";
	private String consumerSecretStr = "SfXOv6mJhCDdJ6dGqb2cU4kSfuOwXArh54NsBnrRazlVratcFg";
	private String app_twitter_key = "2317121491-iiJ45yU2RMuro5uL3lm52MIvXSkCrQ2Ix8hJFni";
	private String app_twitter_secret = "vlkCMFkgS8ujG7bXriCUgbxR9ag05Doseo3RP0IDEF3FM";
	
	private twitter4j.Twitter twitter;
	
	public CorpusCollection () throws IOException, TwitterException {
		
		// Getting an instance of Twitter4J
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey(consumerKeyStr)
		  .setOAuthConsumerSecret(consumerSecretStr)
		  .setOAuthAccessToken(app_twitter_key)
		  .setOAuthAccessTokenSecret(app_twitter_secret);
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
		
		List<String> screenNames = new ArrayList<String>();
		List<String[]> output = new ArrayList<String[]>();
		
		FileReader fis = new FileReader("/Users/Mia/Desktop/depressed_users.csv");
		BufferedReader reader = new BufferedReader(fis);
		String line = reader.readLine();
		screenNames.add(line);
		while ((line = reader.readLine()) != null) {
			 screenNames.add(line);
		}
		reader.close();


		
		for (int i=0; i<screenNames.size(); i++) {
			for (int p=1; p<3; p++) {
				String screenName = screenNames.get(i);
				Paging page = new Paging(p, 20);
				ResponseList<Status> timeline = twitter.getUserTimeline(screenName, page);
				Long id = timeline.get(0).getUser().getId();
				String user_id = Long.toString(id);
				for (int j=0; j<timeline.size(); j++) {
					Long status_id = timeline.get(j).getId();
					String tweet_id = Long.toString(status_id);
					String[] record = new String[4];
					record[0] = screenName;
					record[1] = user_id;
					record[2] = tweet_id;
					record[3] = timeline.get(j).getText();
					output.add(record);
				}
			}
			
		}
		
		String write_file = "/Users/Mia/Desktop/depressed_statuses.csv";
		FileWriter writer = new FileWriter(write_file, true); 
		PrintWriter print_line = new PrintWriter(writer);
		
		for (int i=0; i<output.size(); i++) {
			print_line.write(output.get(i)[0] + ", " + output.get(i)[1] + ", " + output.get(i)[2] + ", " + output.get(i)[3] + "\n");
		}
		print_line.close();

	}
	


}
