package aps;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Testing {

	// Tokens for identification
	private String consumerKeyStr = "HlRwUgUaBmWfJsNzlHqIgC7Fp";
	private String consumerSecretStr = "SfXOv6mJhCDdJ6dGqb2cU4kSfuOwXArh54NsBnrRazlVratcFg";
	private String app_twitter_key = "2317121491-iiJ45yU2RMuro5uL3lm52MIvXSkCrQ2Ix8hJFni";
	private String app_twitter_secret = "vlkCMFkgS8ujG7bXriCUgbxR9ag05Doseo3RP0IDEF3FM";
	
	private twitter4j.Twitter twitter;
	
	List<String[]> input = new ArrayList<String[]>();
	List<String[]> output = new ArrayList<String[]>();	
	
	public Testing () {
		
		// Getting an instance of Twitter4J
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey(consumerKeyStr)
		  .setOAuthConsumerSecret(consumerSecretStr)
		  .setOAuthAccessToken(app_twitter_key)
		  .setOAuthAccessTokenSecret(app_twitter_secret);	
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
	
		FileReader read_file;
		BufferedReader reader;
		try {
			read_file = new FileReader("/Users/Mia/Desktop/data/data-from-article/annotations_id-class4.csv");
			reader = new BufferedReader(read_file);
			String line = reader.readLine();
			String[] row = line.split(",");
			for (int i=0; i<496; i++) {
				line = reader.readLine();
				row = line.split(",");
				input.add(row);
			}
			reader.close();
		} 
		catch (IOException e) {
			System.out.println("can't find file");
		}
	}
	
	public void collectMessages (int i) throws IOException, TwitterException {
	
				int p=0;
				Long tweetId = 0L;
				try{
					tweetId = Long.parseLong(input.get(i)[0]);
					Status status = twitter.showStatus(tweetId);
					String[] record = new String[2];
					record[0] = status.getText();
					record[1] = input.get(i)[1];
					output.add(record);
				}
				catch (Exception e) {
					p++;
				}
				
				System.out.println(tweetId + ", " + p);
			
		
	}
	
	public void writeMessages () throws IOException {
		String write_file = "/Users/Mia/Desktop/annotations_message-class.txt";
		FileWriter writer = new FileWriter(write_file, true);
		PrintWriter print_line = new PrintWriter(writer);
	
		for (int i=0; i<output.size(); i++) {
			print_line.print(output.get(i)[0] + "	" + output.get(i)[1] + "\n");
		}
	
		writer.close();
		output.clear();
	}
	
	public boolean rateLimit() throws TwitterException {
		Map<String ,RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus();
		int hitRemaining=0;
		boolean test = false;
		for (String endpoint : rateLimitStatus.keySet()) {
		    RateLimitStatus status = rateLimitStatus.get(endpoint);
		    hitRemaining=status.getRemaining();
		    //System.out.println("Hits remaining" + hitRemaining);
		    if(hitRemaining==0){
		    	//timeRemaining=status.getSecondsUntilReset()*1000;
		    	test = true;
		    }
		}
		return(test);
	}
	
}
