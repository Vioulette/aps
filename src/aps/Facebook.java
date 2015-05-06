package aps;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Friend;
import facebook4j.Link;
import facebook4j.Post;
import facebook4j.ResponseList;
import facebook4j.User;
import facebook4j.auth.AccessToken;

public class Facebook {

		// Tokens for identification
		private String app_id = "1597055770513413";
		private String app_secret = "d9e1589864612fac8190f2024710dbad";			
		private facebook4j.Facebook facebook;
		// Get an access token from: 
		// https://developers.facebook.com/tools/explorer
		// Copy and paste it below.
		private String accessTokenString = "CAACEdEose0cBAPVoYKQsi86ZC8xg4TZA3rnYaPLDS6g3T5d8EzTFYtwB8WdXHWz3z2DZBzyQmYlRoet1M6kfgqth0Rn1S28aOuB9Ai4Ik0DaLewKR96USwA71xeZCO3kmRXZAtHWAsMQFZClwFhA9bowCT35zS8Cn4YwZBCmXQXfdwp5vQ4vLRt55gAM0NZAPZAGiwJaUEZACXiw7dZALt2GaHZA";
		private PostgresHelper client;
		private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		
		public Facebook () {
			
			// Connection to DataBase
			client = new PostgresHelper(
					DbContract.HOST, 
					DbContract.DB_NAME,
					DbContract.USERNAME,
					DbContract.PASSWORD);	
			try {
				if (client.connect()) {
					System.out.println("DB connected");
				}	
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
			
			// Generate facebook instance.
			facebook = new FacebookFactory().getInstance();
			facebook.setOAuthAppId(app_id, app_secret);
			// Set access token.
			AccessToken token = new AccessToken(accessTokenString);
			facebook.setOAuthAccessToken(token);
		}
		   
		public void collectFeed () throws FacebookException, SQLException {
			User me = facebook.getMe();
			insertFacebookUser(me);
			
			// Collect friends
			ResponseList<Friend> friends_list = getListFriends (me);
			for (int i=0;i<friends_list.size();i++){
				Friend friend = friends_list.get(i);
				insertFacebookUser(friend);
				insertFacebookFriend(me,friend);
				
				// Collect messages for each friend
				collectLinks(friend);
				collectPosts(friend);
				collectStatuses(friend);
			}
		}
		
		public ResponseList<Friend> getListFriends (User user) throws FacebookException {
			ResponseList<Friend> liste = facebook.getFriends(user.getId());
			return liste;
		}
		
		public void collectLinks (User user) throws SQLException, FacebookException {
			ResponseList<Link> links_list = facebook.getLinks(user.getId());
			for (int i=0;i<links_list.size();i++){
				Link link = links_list.get(i);
				insertFacebookLink(link);
			}
		}
		
		public void collectPosts (User user) throws SQLException, FacebookException {
			ResponseList<Post> posts_list = facebook.getPosts(user.getId());
			for (int i=0;i<posts_list.size();i++){
				Post post = posts_list.get(i);
				insertFacebookPosts(post);
			}
		}

		public void collectStatuses (User user) throws SQLException, FacebookException {
			ResponseList<Post> statuses_list = facebook.getStatuses(user.getId());
			for (int i=0;i<statuses_list.size();i++){
				Post status = statuses_list.get(i);
				insertFacebookStatus(status);
			}
		}
		
						/*  INSERT METHODS  */
						  /*		      */
				
		public void insertFacebookUser(User user) throws SQLException {
			java.util.Date date= new java.util.Date();
			List<String> idUsers = selectFacebookUsersID();
			
			if(!idUsers.contains(user.getId())) {
				// Store user in database
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", user.getId());
				map.put("user_id", null);
				map.put("about", null);
				map.put("bio", user.getBio());
				map.put("birthday", user.getBirthday());
				map.put("cover", user.getCover());
				map.put("email", user.getEmail());
				map.put("first_name", user.getFirstName());
				map.put("gender", user.getGender());
				map.put("howntown", user.getHometown());
				map.put("is_verified", null);
				map.put("last_name", user.getLastName());
				if (user.getLink()!=null){
					map.put("link", user.getLink().getFile().replace(","," "));
				}
				else {
					map.put("link", null);
				}
				if (user.getLocale()!=null){
					map.put("locale", user.getLocale().getCountry().replace(","," "));
				}
				else {
					map.put("locale", null);
				}
				map.put("location", user.getLocation());
				map.put("middle_name", user.getMiddleName());
				map.put("name", user.getName());
				map.put("political", user.getPolitical());
				map.put("quotes", user.getQuotes());
				map.put("relationship_status", user.getRelationshipStatus());
				map.put("religion", user.getReligion());
				map.put("significant_other", user.getSignificantOther());
				map.put("timezone", user.getTimezone());
				map.put("third_party_id", user.getThirdPartyId());
				map.put("verified", null);
				map.put("website", user.getWebsite());
				map.put("created", format.format(date));
				map.put("modified", null);
				if (client.insert("facebook_users", map) == 1) {
					System.out.println("Record added");
				}
			}
		}
		
		public void insertFacebookFriend(User user, Friend friend) throws SQLException {
			java.util.Date date= new java.util.Date();
			List<String> idFriends = selectFacebookFriendID();
			
			if(!idFriends.contains(friend.getId()) && !idFriends.contains(user.getId())) {
				// Store user in database
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("user_id", user.getId());
				map.put("friend", friend.getId());
				map.put("still_friend", true);
				map.put("friendlist_id", null);
				map.put("created", format.format(date));
				map.put("modified", null);
				if (client.insert("facebook_friends", map) == 1) {
					System.out.println("Record added");
				}
			}
		}
		
		public void insertFacebookFeed(String userID, String linkID, String postID, String statusID) throws SQLException {
			
			java.util.Date date= new java.util.Date();
			
			// Store user in database
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("user_id", userID);
			map.put("link_id", linkID);
			map.put("post_id", postID);
			map.put("status_id", statusID);
			map.put("created", format.format(date));
			map.put("modified", null);
			if (client.insert("facebook_user_feeds", map) == 1) {
				System.out.println("Record added");
			}
		}
		
		public void insertFacebookLink(Link link) throws SQLException {
			java.util.Date date= new java.util.Date();
			List<String> idLinks = selectFacebookLinksID();
			
			if(!idLinks.contains(link.getId())) {
				// Store user in database
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", link.getId());
				map.put("created_time", format.format(link.getCreatedTime()));
				if(link.getDescription()==null){
					map.put("description", null);
				}
				else {
					map.put("description", link.getDescription().replace("'"," "));
				}
				map.put("user_id", link.getFrom().getId());
				map.put("icon", link.getIcon());
				if (link.getLink()!=null){
					map.put("link", link.getLink().replace(","," "));
				}
				else {
					map.put("link", null);
				}
				if(link.getMessage()==null){
					map.put("message", null);
				}
				else {
					map.put("message", link.getMessage().replace("'"," "));
				}
				if(link.getName()==null){
					map.put("name", null);
				}
				else {
					map.put("name", link.getName().replace("'"," "));
				}
				map.put("picture", null);
				map.put("created", format.format(date));
				map.put("modified", null);
				if (client.insert("facebook_links", map) == 1) {
					System.out.println("Record added");
					//insertFacebookFeed(link.getFrom().getId(),link.getId(),null,null);
				}
			}
		}
		
		public void insertFacebookPosts(Post post) throws SQLException {

			List<String> idPosts = selectFacebookPostsID();
			
			if(!idPosts.contains(post.getId())) {
				// Store user in database
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", post.getId());
				map.put("caption", post.getCaption());
				map.put("created_time", format.format(post.getCreatedTime()));
				if(post.getDescription()==null || post.getDescription().length()>30){
					map.put("description", null);
				}
				else { 
					map.put("description", post.getDescription().replace("'"," "));
				}
				if (post.getFrom()!=null){
					map.put("user_id", post.getFrom().getId());
				}
				else {
					map.put("user_id", null);
				}
				map.put("page_id", null);
				map.put("group_id", null);
				map.put("event_id", null);
				map.put("application_id", null);
				if (post.getIcon()!=null){
					map.put("icon", post.getIcon().getFile().replace(","," "));
				}
				else {
					map.put("icon", null);
				}
				map.put("is_hidden", null);
				if (post.getLink()!=null){
					map.put("link", post.getLink().getFile().replace(","," "));
				}
				else {
					map.put("link", null);
				}
				if(post.getMessage()==null){
					map.put("message", null);
				}
				else {
					map.put("message", post.getMessage().replace("'"," "));
				}
				if(post.getName()==null){
					map.put("name", null);
				}
				else {
					map.put("name", post.getName().replace("'"," "));
				}
				map.put("photo_id", null);
				map.put("video_id", null);
				if (post.getPicture()!=null){
					map.put("picture", post.getPicture().getFile().replace(","," "));
				}
				else {
					map.put("picture", null);
				}
				map.put("place", null);
				map.put("shares", post.getSharesCount());
				if (post.getSource()!=null){
					map.put("source", post.getSource().getFile().replace(","," "));
				}
				else {
					map.put("source", null);
				}
				map.put("status_type", post.getStatusType());
				map.put("story", null);
				map.put("type", post.getType());
				map.put("updated_time", format.format(post.getUpdatedTime()));
				map.put("created", format.format(post.getCreatedTime()));
				map.put("modified", null);
				if (client.insert("facebook_posts", map) == 1) {
					System.out.println("Record added");
					insertFacebookFeed(post.getFrom().getId(),null,post.getId(),null);
				}
			}
		}
		
		public void insertFacebookStatus(Post status) throws SQLException {
			
			List<String> idStatuses = selectFacebookStatusesID();
			
			if(!idStatuses.contains(status.getId())) {
				// Store user in database
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", status.getId());
				map.put("user_id", status.getFrom().getId());
				map.put("page_id", null);
				map.put("group_id", null);
				map.put("event_id", null);
				map.put("application_id", null);
				if(status.getMessage()==null){
					map.put("message", null);
				}
				else {
					map.put("message", status.getMessage().replace("'"," "));
				};
				if (status.getPlace()!=null){
					map.put("place", status.getPlace().getName());
				}
				else {
					map.put("place", null);
				}
				map.put("updated_time", null);
				map.put("created", status.getCreatedTime());
				map.put("modified", null);
				if (client.insert("facebook_statuses", map) == 1) {
					System.out.println("Record added");
					insertFacebookFeed(status.getFrom().getId(),null,null,status.getId());
				}
			}
		}
		
						/*  SELECT METHODS  */
		  				  /*		      */
		
		public List<String> selectFacebookUsersID() throws SQLException {
			ResultSet rs = client.execQuery("SELECT id FROM facebook_users");
			List<String> liste = new ArrayList<String>();
			while(rs.next()) {
				liste.add(rs.getString(1));
			}
			return liste;
		}
		
		public List<String> selectFacebookFriendID() throws SQLException {
			ResultSet rs = client.execQuery("SELECT friend FROM facebook_friends");
			List<String> liste = new ArrayList<String>();
			while(rs.next()) {
				liste.add(rs.getString(1));
			}
			return liste;
		}
		
		public List<String> selectFacebookPostsID() throws SQLException {
			ResultSet rs = client.execQuery("SELECT id FROM facebook_posts");
			List<String> liste = new ArrayList<String>();
			while(rs.next()) {
				liste.add(rs.getString(1));
			}
			return liste;
		}
		
		public List<String> selectFacebookLinksID() throws SQLException {
			ResultSet rs = client.execQuery("SELECT id FROM facebook_links");
			List<String> liste = new ArrayList<String>();
			while(rs.next()) {
				liste.add(rs.getString(1));
			}
			return liste;
		}
		
		public List<String> selectFacebookStatusesID() throws SQLException {
			ResultSet rs = client.execQuery("SELECT id FROM facebook_statuses");
			List<String> liste = new ArrayList<String>();
			while(rs.next()) {
				liste.add(rs.getString(1));
			}
			return liste;
		}
		
		public List<Map<String, Object>> selectFacebookRates() throws SQLException {
			ResultSet rs = client.execQuery("SELECT * FROM rates");
			List<Map<String, Object>> liste = new ArrayList<Map<String, Object>>();
			while(rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", rs.getLong(1));
				map.put("twitter_status_id", rs.getLong(2));
				map.put("facebook_post_id", rs.getLong(3));
				map.put("facebook_status_id", rs.getLong(4));
				map.put("facebook_link_id", rs.getLong(5));
				map.put("facebook_comment_id", rs.getLong(6));
				map.put("rate", rs.getInt(7));
				map.put("anorexia", rs.getInt(8));
				map.put("depression", rs.getInt(9));
				map.put("harassment", rs.getInt(10));
				map.put("uncategorized", rs.getInt(11));
				map.put("created", rs.getTimestamp(12));
				map.put("modified", rs.getTimestamp(13));
				liste.add(map);
			}
			return liste;
		}
		
		public void classifyFeed() {
			System.out.println("Feed classification OK");
		}
		
		public long rateLimit() {
			return 0;
		}
}
