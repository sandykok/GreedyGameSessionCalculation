import java.io.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class GameSess {
	
	//initializing variables for total and valid no. of sessions
	public static int total=0, valid=0;
	
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//HashMap for ggstart and ggstop
	public static Map<Long,Date> start_hashMap = new HashMap<Long,Date>();
	public static Map<Long,Date> stop_hashMap = new HashMap<Long,Date>();
	
	public static long valid_sessionTime =0;
	
	//'calculate_Sessions' will calculate total No. of sessions, valid No. of sessions, valid session time, Average valid session time and prints them on console
	void calculate_Sessions(String Event,String ts,String id) throws ParseException{
		
		Long ID_Value = Long.parseLong(id);
		Date TimeStampDate = format.parse(ts);
		
		if(Event.equals("ggstart"))
		{
			
							if(!stop_hashMap.containsKey(ID_Value) && !start_hashMap.containsKey(ID_Value))
							{
							//if both hashmaps does not contain the timestamp corresponding to given ID
							start_hashMap.put(ID_Value,TimeStampDate );
							total++;
							}
							else if(stop_hashMap.containsKey(ID_Value))
							{
								//already ggstop has come now check whether it is same id or new one
												    
												if(start_hashMap.containsKey(ID_Value))
												{	
													//starthashmap has timestamp value so check whether it is more than 30 seconds or not
													Date d1 = new Date();
												    Date d2 = new Date();
												    d1 =   stop_hashMap.get(ID_Value);
												    d2 =    TimeStampDate;
												    
												    long diff = d2.getTime() - d1.getTime();
												    long diffSeconds = diff / 1000 % 60;
											        long diffMinutes = diff / (60 * 1000) % 60;
											        long diffHours = diff / (60 * 60 * 1000);
											        
											        int diffInDays = (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
				
											                 if (diffInDays >=1 ||diffHours >= 1||diffMinutes >= 1||diffSeconds>30) 
											                   {//new session has started
											                	total++;
											                   }
												}
												//remove the value in stophashmap and insert new timestamp in starthashmap
												stop_hashMap.remove(ID_Value);
							                	 start_hashMap.put(ID_Value,TimeStampDate );
							  }
							else if(!stop_hashMap.containsKey(ID_Value) && start_hashMap.containsKey(ID_Value))
							{//ggstop may not delivered so replace new timestamp in start_hashMap
								start_hashMap.put(ID_Value,TimeStampDate );
							}
		}
		else if(Event.equals("ggstop"))
		{
			
			if(start_hashMap.containsKey(ID_Value)&&!stop_hashMap.containsKey(ID_Value))
			{
				//ID is present in starthashmap but not in stophashmap
				stop_hashMap.put(ID_Value,TimeStampDate );
			Date d3,d4;
			d3 = start_hashMap.get(ID_Value);
			d4 = TimeStampDate;
			long diff = d4.getTime() - d3.getTime();
	        long diffSeconds = diff / 1000 % 60;
	        long diffMinutes = diff / (60 * 1000) % 60;
	        long diffHours = diff / (60 * 60 * 1000);
	        
	        int diffInDays = (int) ((d4.getTime() - d3.getTime()) / (1000 * 60 * 60 * 24));

						        if (diffInDays >=1 ||diffHours >= 1||diffMinutes >= 1) 
						        {
						        	//if time is more than 60 Seconds then it is valid
						        	valid_sessionTime = (valid_sessionTime + diff);
									valid++;
								}
		     }
			
				
			else{
				//Considering 3 cases
				//1. ggstart may not delivered and stop_hashmap does not have corresponding ID
				//2. ggstart may not delivered and stop_hashmap have corresponding ID
				//3. ggstart  delivered and start_hashmap,stop_hashmap have corresponding ID which means first ggstart then ggstop then again ggstop of same ID(even here second ggstart may not delivered)
				//for all the cases add timestamp to corresponding key and increment total
			stop_hashMap.put(ID_Value,TimeStampDate );
			total++;
			    }
		}
		else{
			System.out.println("Event should be ggstart or ggstop");
			return;
		}

	}
	
	public static void main(String args[]) throws IOException, ParseException
	{
		FileInputStream fs = new FileInputStream("ggevent.log");
		BufferedReader bf = new BufferedReader(new InputStreamReader(fs));
		String str;

				while((str=bf.readLine())!=null)
				  {
				
					String[] st = str.split("event"); 
					
					String Secondpart = st[1];
					String[] Secondpart_split = Secondpart.split("\"");
					String Action = Secondpart_split[2];
					
					String[] Timestamp_temp = Secondpart.split("timestamp");
					String[] Timestamp_val = Timestamp_temp[1].split("\"");
					String Timestamp_final = Timestamp_val[2];
					
					String Game_id = Timestamp_val[6];
					
					ReadFile RF = new ReadFile();
					RF.calculate_Sessions(Action,Timestamp_final,Game_id);
					
				  }

				System.out.println("No. of Sessions (Total + Valid): "+total);
				System.out.println("No. of valid Sessions :"+valid);

			if(valid!=0)
				{
			valid_sessionTime = valid_sessionTime/valid;
			
		    long diffSeconds = valid_sessionTime / 1000 % 60;
		    long diffMinutes = valid_sessionTime / (60 * 1000) % 60;
		    long diffHours = valid_sessionTime / (60 * 60 * 1000);
		  
		    System.out.println("The Average Session Time is : "+ diffHours + " hours, " + diffMinutes + " minutes, "+ diffSeconds + " seconds.");
				}
			
			bf.close();
	}
}
