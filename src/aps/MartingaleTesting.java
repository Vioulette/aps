package aps;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

public class MartingaleTesting {
	
	private PostgresHelper client;
	private List<Float[]> stream;
	private List<Float[]> testingArray = new ArrayList<Float[]>();
	private List<Float[]> USM;
	private List<Float> randomNumber = new ArrayList<Float>();
	//indicate here the number of numerical (n) and categorical (c) dimensions
	int num = 2;
	int cat = 2;
	int total = num + cat;
	private Float[] USMrow = new Float[total];
	private Map<String, Object> map = new HashMap<String, Object>();
	
	
	public MartingaleTesting (double epsilon, double epsilon1, float lambda) throws SQLException, IOException {
		System.out.println("Martingale Testing connected");
		
		// Connection to DataBase
		client = new PostgresHelper(
				DbContract.HOST, 
				DbContract.DB_NAME,
				DbContract.USERNAME,
				DbContract.PASSWORD);	
		try {
			if (client.connect()) {
				System.out.println("DB connected for data stream");
			}	
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	
		//getting data stream
		ResultSet rs = client.execQuery("SELECT * FROM data_stream");
		stream = new ArrayList<Float[]>();
		while(rs.next()) {
			Float[] observation = new Float[4];
			observation[0] = rs.getFloat(2);
			observation[1] = rs.getFloat(3);
			observation[2] = (float) rs.getInt(4);
			observation[3] = (float) rs.getInt(5);
			stream.add(observation);
		}
		
		//setting up array of random number for p-value calculation
//		FileReader fis = new FileReader("/Users/Mia/Desktop/randomArray.csv");
//		BufferedReader reader = new BufferedReader(fis);
//		String line = reader.readLine();
//		Float rand = (float) 0;
//		rand = Float.parseFloat(line);
//		randomNumber.add(rand);
//		for (int i=0; i<1999; i++) {
//			line = reader.readLine();
//			//System.out.println(line);
//			rand = (float) 0;
//			rand = Float.parseFloat(line);
//			randomNumber.add(rand);
//		}
//		reader.close();
		
		Random rand = new Random();
		for (int i=0; i<stream.size(); i++) {
			Float randNum = rand.nextFloat();
			randomNumber.add(randNum);
		}
		
		//final analysis of data stream
		//initialization
		float MTprevious1=1;
		float MTprevious2=1;
		float MT = 1;
		float pValue;
		float pValueComp;
		USM = new ArrayList<Float[]>();
		Float[] x = new Float[1];
		x[0] = (float) 0;
		int firstPoint = 0;
		
		for (int a=0; a<stream.size(); a++) {
			testingArray.add(stream.get(a));
			if (testingArray.size()==1) {
				System.out.println("Setting first USM to 0");
				USM.add(x);
				firstPoint = 0;
			} 
			else {
				//System.out.println("USM being calculated");
				for (int i=a; i<testingArray.size(); i++) {
					USMrow = new Float[4];
					Float[] USMsum = new Float[1];
					USMsum[0] = (float) 0;
					for (int j=0; j<total; j++) {
						calculateUSM(i,j);	
				}
				for (int k=0; k<USMrow.length; k++) {
					USMsum[0] = USMsum[0] + USMrow[k];
				}
				USM.add(USMsum);
				}
				firstPoint++;
			}
			//initializing the first martingale value for the user
			if (a==0) {
				map.put("id", 1);
				map.put("user_id", 19775920);
				map.put("mt_value", 1);
				client.insert("mt_values", map);
				map.clear();
			}
			pValue = (float) calculatePvalue(firstPoint);
			//System.out.println(pValue);
			pValueComp = (float) (1-pValue);
			//choose epsilon here
			pValue = (float) Math.pow(pValue, epsilon1);
			pValue = pValue*((float) epsilon);
			//System.out.println("P value after calculations = " + pValue);
			MTprevious1=MTprevious1*pValue;
			//System.out.println("MTprevious1 = " + MTprevious1);
			pValueComp = (float) Math.pow(pValueComp, epsilon1);
			pValueComp = pValueComp*((float) epsilon);
			//System.out.println("P valueComp after calculations = " + pValueComp);
			MTprevious2=MTprevious2*pValueComp;
			//System.out.println("MTprevious2 = " + MTprevious2);
			MT = (MTprevious1 + MTprevious2)/2;
			//System.out.println("Martingale value is =" + MT);
			//System.out.println(MT);
			//inserting the most recent martingale value in the database
			client.updateMT(19775920, MT, 0);
//			map.put("id", 1);
//			map.put("user_id", 19775920);
//			map.put("mt_value", MT);
//			client.insert("mt_values", map);
//			map.clear();
			if (MT > lambda & testingArray.size()>100) {
				System.out.println("MT value = " + MT);
				System.out.println("Alarm at point: " + a);
				client.updateMT(19775920, MT, a);
				MTprevious1 = 1;
				MTprevious2 = 1;
				MT = 1;
				testingArray.clear();
			}
			}
		}

	
	//Calculating the USM for an individual data point - row n column m
	public void calculateUSM(int n, int m) {
		if (m<num) {
			Float z = testingArray.get(n)[m];
			Float sum = (float) 0;
			for (int i=0; i<=n; i++) {
				sum = sum + testingArray.get(i)[m];
			}
			Float avg = sum/(n+1);
			Float USM = Math.abs(z-avg);
			USMrow[m]=USM;
			//System.out.println(USMrow[m]);
		}
		if (m>=cat) {
			//keep this section of the code in case the calculation of the mode changes
//			Float z = testingArray.get(n)[m];
//			Float[] modeArray = new Float[n+1];
//			for (int i=0; i<modeArray.length; i++) {
//				modeArray[i] = testingArray.get(i)[m];
//			}
			//Float mode = mode(modeArray);
			//int z2 = Math.round(z);
			//int mode2 = Math.round(mode);
//			if (z2==mode2) {
//				USMrow[m] = (float) 0;
//			}
//			else USMrow[m] = (float) 1;
//			System.out.println(USMrow[m]);
			Float z = testingArray.get(n)[m];
			float[] modeArray = new float[n+1];
			for (int i=0; i<modeArray.length; i++) {
				modeArray[i] = testingArray.get(i)[m];
			}
			Float[] modes = mode3(modeArray);
			Integer[] mode2 = new Integer[modes.length];
			for (int i=0; i<modes.length; i++) {
				mode2[i] = Math.round(modes[i]);
			}
			int z2 = Math.round(z);
			if (!ArrayUtils.contains(mode2, z2)) {
					USMrow[m] = (float) 1;
			}
			else USMrow[m] = (float) 0;	
			//System.out.println(USMrow[m]);
		}
	}
	
	//calculating the mode for one dimension (used for categorical dimensions)
	//this method takes the highest number if there are multiple modes
	public static float mode(Float[] input) {
		//here 6 is the number of discrete categories used with the labels {1,2,3,4,5}
		//must have one extra space in the count array so that count = {0,1,2,3,4,5}
		int[] count = new int[6]; 
		for (int i=0; i<input.length; i++) {
			float x = input[i];
			int y = (int)x;
			count[y]++;
		}
		
		int index = 0;
		for (int i=1; i<count.length; i++) {
			if (count[i] > count[index])
				index = i;
		}
		Float index2 = (float) index;
		return index2;
	}
	
	//this method returns an array of modes if there are multiple
	public static Float[] mode3(float[] a){
		  List<Float> modes = new ArrayList<Float>(  );
		  int maxCount=0;   
		  for (int i = 0; i < a.length; ++i){
		    int count = 0;
		    for (int j = 0; j < a.length; ++j){
		      if (a[j] == a[i]) ++count;
		    }
		    if (count > maxCount){
		      maxCount = count;
		      modes.clear();
		      modes.add( a[i] );
		    } else if ( count == maxCount ){
		      modes.add( a[i] );
		    }
		  }
		  return modes.toArray(new Float[modes.size()] );
		}
	
	//calculating the p-value for a point
	public double calculatePvalue(int n) {
		int p1 = 0;
		Float[] point = USM.get(n);
		Float z = point[0];
		for (int i=0; i<=n; i++) {
			Float[] previousPoint = USM.get(i);
			Float y = previousPoint[0];
			if (y>z)
				p1++;
		}
		double pValue = 0;
		float theda = randomNumber.get(n);;
		pValue=(p1+theda)/(n+1);
		return pValue;
	}
	
	
}
