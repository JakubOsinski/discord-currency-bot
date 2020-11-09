package juice.juubot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BotCommands
{

	public static void checkUsers(ArrayList<CashUser> users, int fields) {
		System.out.println(users.size() +" users size in BotCommands.checkUsers(users, fields)");
		for(int i =0; i < users.size(); i++) {
			System.out.print(users.get(i).get_cash()+ " ");
			System.out.print(users.get(i).get_lastTimely()+ " ");
			System.out.print(users.get(i).get_ID()+ " ");
			System.out.print(users.get(i).get_mention()+ " ");
			System.out.print(users.get(i).get_tag()+ " " );
			System.out.println();
		}
	}
	
	/**
	 * users have to be filtered at this point
	 * @param users
	 * @param event
	 * @return
	 */
	public static CashUser getCommandUser(ArrayList<CashUser> users, MessageReceivedEvent event) {
		//	CashUser commandUser = new CashUser();
			long id = Long.parseLong(event.getMember().getUser().getId());
			for (CashUser u: users) {
			//	if(id.equals(u.get_ID())) {
				if(id==u.get_ID()) {
					return u;
				}
			}	
			return null;
		}
	
	public static CashUser createNewUser(MessageReceivedEvent event, Date date) {
		//int cash ,long lastTimely, long _ID, String _tag, String _mention
		long id = Long.parseLong(event.getMember().getUser().getId());
		String mention = event.getMember().getUser().getAsMention();
		String tag = event.getMember().getUser().getAsTag();
		CashUser commandUser = new CashUser(1000, date.getTime(), id, tag, mention); //cash, lasttimely, id, mention, tag
			return commandUser;
		}

	public static void fillUsers(ArrayList<CashUser> users, ArrayList<String> sortedFile, int fields) {
		int counter = 0;
		CashUser user = new CashUser();
for(int i =0; i < sortedFile.size(); i++)
{
	System.out.println(i + " " + sortedFile.get(i));
	user.set(counter, sortedFile.get(i));
	counter++;
	if(counter >= 5) {
	 	users.add(user);
	 	user = new CashUser();
	 	counter = 0;
	}
}	
		
	}

	public static void filterUsers(ArrayList<CashUser> users) { // get rid of duplicate users
	//	users.sort(CashUser::compareByCash);// after sort fighters.size()-1) = best
		Collections.sort(users, Comparator.comparingInt(p -> p.get_cash()));
	//users.sort(Collections.reverse(users););
	Collections.reverse(users); // index 0 = highest
	//delete duplicates
	ArrayList<String> mentions = new ArrayList<>();
	ArrayList<CashUser> newUsers = new ArrayList<>();
	for(CashUser u: users) 
	{
		if(!mentions.contains(u.get_mention())) {
		mentions.add(u.get_mention());
		newUsers.add(u);
		}
	}
//	System.out.println(newUsers.size());
	//System.out.println(users.size());
		users = newUsers;
//	System.out.println(newUsers.size());
	}

	public static String getTimeToNextTimely(long timelyCommandResult) {
		String timeToNextTimely = ""; // timelyCommandResult = milliseconds to next timely
		int seconds = (int) (timelyCommandResult / 1000) % 60 ; 
		int minutes = (int) ((timelyCommandResult / (1000*60)) % 60);
		int hours   = (int) ((timelyCommandResult / (1000*60*60)) % 24);
		return "" + hours +":" + minutes + ":" + seconds;
	}

	 /**
		  * helper method for heads/tails flip gamble, only used when userBet is not an numeric value
		  * @return -1 if the bet value is invalid and bet amount if userBet input is valid.
		  */
		public static int getBesttingAmount(String userBet, int userCash) {
			int betting = -1;
			switch(userBet) { // can delete percentages as it's done below.
			case "half": case "Half": case "HALF": case "HAlf": case "1/2": case "50%": 
				betting = userCash /2;
			break;
			case "all": case "All": case "ALL": case "ALl": case "1/1": case "100%": 
				betting = userCash;
			break;
			case "quater": case "Quater": case "QUATER": case "QUater": case "1/4": case "25%": 
				betting = userCash / 4;
			break;
			}
			if(betting != -1) {
				return betting;
			}

			//WORK OUT PERCENTAGES
			if(userBet.charAt(userBet.length()-1) == '%') {
				try {
					 String percentageBet = userBet.substring(0,userBet.length()-1);
					 System.out.println(percentageBet + " PERCENTAGE BET NEED NOT TO HAVE % AT THE END");
					 if(Integer.parseInt(percentageBet) <= 100 && Integer.parseInt(percentageBet) > 0) 
					 {
					     int betting2 = userCash * Integer.parseInt(percentageBet);
					     betting = betting2/100;
					 } else {
						 System.out.println("ABOVE 100% OR BELOW 0%, = ERROR");
					 }

				}catch(Exception e) {
					System.out.println("heres");
				}}
			return betting;
		}

		/**
		 * @param userBet
		 * @return -1 for invalid input, 0 for tails, 1 for heads
		 */
	public static int getUserChoice(String userBet)
	{
	//	String userBetChoice = "";
		int userBetChoice1 = -1;
		switch(userBet) { // can delete percentages as it's done below.
		case "h": case "heads": case "HEADS": case "Heads": case "HEads": case "H": 
		case "head": case "HEAD": case "Head": case "HEad":
			 userBetChoice1 = 1;
		break;
		case "t": case "tails": case "TAILS": case "Tails": case "TAils": case "T": 
	    case "tail": case "TAIL": case "Tail": case "TAil": 
			 userBetChoice1 = 0;
		break;
	}
		System.out.println("getUserChoice is returning... " + userBetChoice1);
		return userBetChoice1;
		
	}

		public static CashUser getCommandUser(ArrayList<CashUser> users, String getThisUserBalance) {
			for (CashUser u: users) {
				if(getThisUserBalance.equals(u.get_mention())) {
					return u;
				}
			}	
			return null;
		}

		/**
		 * //used in give cash
		 * @param users
		 * @param receiverTag
		 * @return
		 */
		public static CashUser getUserReceiving(ArrayList<CashUser> users, long receiverID) {
//			for (CashUser u: users) {
//				if(receiverTag.equals(u.get_tag())) {
//					return u;
//				}
//			}	
//			return null;
			for (CashUser u: users) {
				if(receiverID == u.get_ID()) {
					return u;
				}
			}	
			return null;
		}

		public static long getReceiverID(String receiverMention) {
//<@!481088743332380673> mention has to be trimmed into only numbers so it becomes ID
			String id = "";
			for(int i =0; i < receiverMention.length(); i++) {
				if(Character.isDigit(receiverMention.charAt(i))) {
					id = id+ receiverMention.charAt(i);
				}
			}
			System.out.println("receiverMention '" + receiverMention +"'");
			System.out.println("returning ID: '" + id +"'");
		//	int ID = Integer.parseInt(id);
			return Long.parseLong(id);
		}


	

}
