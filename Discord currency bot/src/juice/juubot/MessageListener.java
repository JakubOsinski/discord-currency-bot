package juice.juubot;

import juice.juubot.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import textmanipulation.readfile.Read;

import javax.security.auth.login.LoginException;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MessageListener extends ListenerAdapter
{
	public static final String PREFIX = "$";
	private static final String FILENAME = "users.txt";
	private static final int FIELDS = 5;
	public ArrayList<CashUser> users = new ArrayList<>();
	private final static int LB_ID = 1;

	textmanipulation.readfile.Read r = new Read();
	
	public ArrayList<CashUser> users2 = new ArrayList<>();
	
    /**
     * This is the method where the program starts.
     */
    public static void main(String[] args)
    {
        //We construct a builder for a BOT account. If we wanted to use a CLIENT account
        // we would use AccountType.CLIENT
        try
        {
            JDA jda = JDABuilder.createDefault(Token.MY_TOKEN) // The token of the account that is logging in.
                    .addEventListeners(new MessageListener())   // An instance of a class that will handle events.
                    .build();
            jda.awaitReady(); // Blocking guarantees that JDA will be completely loaded.
                   
            System.out.println("Finished Building JDA!");
        }
        catch (LoginException e)
        {
            //If anything goes wrong in terms of authentication, this is the exception that will represent it
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            //Due to the fact that awaitReady is a blocking method, one which waits until JDA is fully loaded,
            // the waiting can be interrupted. This is the exception that would fire in that situation.
            //As a note: in this extremely simplified example this will never occur. In fact, this will never occur unless
            // you use awaitReady in a thread that has the possibility of being interrupted (async thread usage and interrupts)
            e.printStackTrace();
        }
    }
    public   MessageChannel channel;

    private  void displayWindow(int id) {
    
		switch(id) {
		case 1: showLB();
			break;
		case 2:
			break;
		}
	}
    
	private void showLB() {
	 EmbedBuilder eb = new EmbedBuilder();
	 final int MAX_LB_FIELDS = 3;

	 eb.setTitle("Cash Leaderboard");
	 eb.setColor(0xf45642);
	 System.out.println(users.size() + "users size");
	 
// for(int i = users.size()-1; i > -1; i--)
	 for(int i = 0; i < users.size(); i++)
 	{
		 if(i < MAX_LB_FIELDS) {
		 System.out.println(users.get(i).get_tag() + " : " + users.get(i).get_cash());
 		  eb.addField(users.get(i).get_tag(), users.get(i).get_cash() + "", true);
		 }
 	}
		 channel.sendMessage(eb.build()).queue();
		 BotCommands.checkUsers(users, FIELDS);
	}

	/**
     * NOTE THE @Override!
     * This method is actually overriding a method in the ListenerAdapter class! We place an @Override annotation
     *  right before any method that is overriding another to guarantee to ourselves that it is actually overriding
     *  a method from a super class properly. You should do this every time you override a method!
     *
     * As stated above, this method is overriding a hook method in the
     * {@link net.dv8tion.jda.api.hooks.ListenerAdapter ListenerAdapter} class. It has convenience methods for all JDA events!
     * Consider looking through the events it offers if you plan to use the ListenerAdapter.
     *
     * In this example, when a message is received it is printed to the console.
     *
     * @param event
     *          An event containing information about a {@link net.dv8tion.jda.api.entities.Message Message} that was
     *          sent in a channel.
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {//WHEN SOMETHING CHANGES WITHIN USERS, JUST CALL filterUsers() to update, then save()

    	 this.channel = event.getChannel(); 
    	loadUsers(); //filter users = also sort that index 0 = richest cash user
		BotCommands.filterUsers(users);   // filter users = user latest balance + get rid of duplicates	

    	     	  
   	 String[] messageContent = event.getMessage().getContentRaw().split("\\s+");
	 if(messageContent[0].equalsIgnoreCase(PREFIX +"info") || messageContent[0].equalsIgnoreCase(PREFIX +"help")) 
	 {
		 displayInfo(event);
		 return;
	 }

	 if(messageContent[0].equalsIgnoreCase(PREFIX +"t")) 
	 {
			String tag = event.getMember().getUser().getAsTag();
			System.out.println("TAG is doing timely : " + tag);
	  getTimely(event);
	 }
	 
	 if(messageContent[0].equalsIgnoreCase(PREFIX +"leaderboard") 	|| messageContent[0].equalsIgnoreCase(Main.prefix +"lb")) 
	 {//shows users, could or could not be filtered
		 displayWindow(LB_ID);
	 }
//	 
	 if(messageContent[0].equalsIgnoreCase(PREFIX +"bf")) {
		 System.out.println("gambling activated");
		 gamble(event, messageContent);
	 }
	 
	 if(messageContent[0].equalsIgnoreCase(PREFIX +"$") ||messageContent[0].equalsIgnoreCase(PREFIX +"cash")) {
		 System.out.println("Checking user balance...");
		 checkBalance(event, messageContent);
	 }
	 
	 if(messageContent[0].equalsIgnoreCase(PREFIX +"give")) {
		 System.out.println("Giving cash...");
			String mention = event.getMember().getUser().getAsMention();
			String tag = event.getMember().getUser().getAsTag();
		 System.out.println("the giver tag " + tag);
		 System.out.println("the gives mention : " + mention);
		 
		 giveCash(event, messageContent);
	 }
//		 for(int i =0; i < messageContent.length; i++) {
//			 System.out.println("msgContent: " + i + "   : " +  messageContent[i]);	 
//		 }
//	 }
        //These are provided with every event in JDA
        JDA jda = event.getJDA();                       //JDA, the core of the api.
        long responseNumber = event.getResponseNumber();//The amount of discord events that JDA has received since the last reconnect.

        //Event specific information
        User author = event.getAuthor();                //The user that sent the message
        Message message = event.getMessage();           //The message that was received.
        //MessageChannel channel = event.getChannel();    //This is the MessageChannel that the message was sent to.
                                                        //  This could be a TextChannel, PrivateChannel, or Group!

        String msg = message.getContentDisplay();              //This returns a human readable version of the Message. Similar to
                                                        // what you would see in the client.

        boolean bot = author.isBot();                    //This boolean is useful to determine if the User that
                                                        // sent the Message is a BOT or not!

        if (event.isFromType(ChannelType.TEXT))         //If this message was sent to a Guild TextChannel
        {
            //Because we now know that this message was sent in a Guild, we can do guild specific things
            // Note, if you don't check the ChannelType before using these methods, they might return null due
            // the message possibly not being from a Guild!

            Guild guild = event.getGuild();             //The Guild that this message was sent in. (note, in the API, Guilds are Servers)
            TextChannel textChannel = event.getTextChannel(); //The TextChannel that this message was sent to.
            Member member = event.getMember();          //This Member that sent the message. Contains Guild specific information about the User!

            String name;
            if (message.isWebhookMessage())
            {
                name = author.getName();                //If this is a Webhook message, then there is no Member associated
            }                                           // with the User, thus we default to the author for name.
            else
            {
                name = member.getEffectiveName();       //This will either use the Member's nickname if they have one,
            }                                           // otherwise it will default to their username. (User#getName())

            System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), name, msg);
        }
        else if (event.isFromType(ChannelType.PRIVATE)) //If this message was sent to a PrivateChannel
        {
            //The message was sent in a PrivateChannel.
            //In this example we don't directly use the privateChannel, however, be sure, there are uses for it!
            PrivateChannel privateChannel = event.getPrivateChannel();

            System.out.printf("[PRIV]<%s>: %s\n", author.getName(), msg);
        }

        //Now that you have a grasp on the things that you might see in an event, specifically MessageReceivedEvent,
        // we will look at sending / responding to messages!
        //This will be an extremely simplified example of command processing.

        //Remember, in all of these .equals checks it is actually comparing
        // message.getContentDisplay().equals, which is comparing a string to a string.
        // If you did message.equals() it will fail because you would be comparing a Message to a String!
        if (msg.equals("!ping"))
        {
            //This will send a message, "pong!", by constructing a RestAction and "queueing" the action with the Requester.
            // By calling queue(), we send the Request to the Requester which will send it to discord. Using queue() or any
            // of its different forms will handle ratelimiting for you automatically!

            channel.sendMessage("pong!").queue();
        }
        else if (msg.equals("!roll"))
        {
            //In this case, we have an example showing how to use the flatMap operator for a RestAction. The operator
            // will provide you with the object that results after you execute your RestAction. As a note, not all RestActions
            // have object returns and will instead have Void returns. You can still use the flatMap operator to run chain another RestAction!

            Random rand = ThreadLocalRandom.current();
            int roll = rand.nextInt(6) + 1; //This results in 1 - 6 (instead of 0 - 5)
            channel.sendMessage("Your roll: " + roll)
                   .flatMap(
                       (v) -> roll < 3, // This is called a lambda expression. If you don't know what they are or how they work, try google!
                       // Send another message if the roll was bad (less than 3)
                       sentMessage -> channel.sendMessage("The roll for messageId: " + sentMessage.getId() + " wasn't very good... Must be bad luck!\n")
                   )
                   .queue();
        }
        else if (msg.startsWith("!kick"))   //Note, I used "startsWith, not equals.
        {
            //This is an admin command. That means that it requires specific permissions to use it, in this case
            // it needs Permission.KICK_MEMBERS. We will have a check before we attempt to kick members to see
            // if the logged in account actually has the permission, but considering something could change after our
            // check we should also take into account the possibility that we don't have permission anymore, thus Discord
            // response with a permission failure!
            //We will use the error consumer, the second parameter in queue!

            //We only want to deal with message sent in a Guild.
            if (message.isFromType(ChannelType.TEXT))
            {
                //If no users are provided, we can't kick anyone!
                if (message.getMentionedUsers().isEmpty())
                {
                    channel.sendMessage("You must mention 1 or more Users to be kicked!").queue();
                }
                else
                {
                    Guild guild = event.getGuild();
                    Member selfMember = guild.getSelfMember();  //This is the currently logged in account's Member object.
                                                                // Very similar to JDA#getSelfUser()!

                    //Now, we the the logged in account doesn't have permission to kick members.. well.. we can't kick!
                    if (!selfMember.hasPermission(Permission.KICK_MEMBERS))
                    {
                        channel.sendMessage("Sorry! I don't have permission to kick members in this Guild!").queue();
                        return; //We jump out of the method instead of using cascading if/else
                    }

                    //Loop over all mentioned users, kicking them one at a time. Mwauahahah!
                    List<User> mentionedUsers = message.getMentionedUsers();
                    for (User user : mentionedUsers)
                    {
                        Member member = guild.getMember(user);  //We get the member object for each mentioned user to kick them!

                        //We need to make sure that we can interact with them. Interacting with a Member means you are higher
                        // in the Role hierarchy than they are. Remember, NO ONE is above the Guild's Owner. (Guild#getOwner())
                        if (!selfMember.canInteract(member))
                        {
                            // use the MessageAction to construct the content in StringBuilder syntax using append calls
                            channel.sendMessage("Cannot kick member: ")
                                   .append(member.getEffectiveName())
                                   .append(", they are higher in the hierarchy than I am!")
                                   .queue();
                            continue;   //Continue to the next mentioned user to be kicked.
                        }

                        //Remember, due to the fact that we're using queue we will never have to deal with RateLimits.
                        // JDA will do it all for you so long as you are using queue!
                        guild.kick(member).queue(
                            success -> channel.sendMessage("Kicked ").append(member.getEffectiveName()).append("! Cya!").queue(),
                            error ->
                            {
                                //The failure consumer provides a throwable. In this case we want to check for a PermissionException.
                                if (error instanceof PermissionException)
                                {
                                    PermissionException pe = (PermissionException) error;
                                    Permission missingPermission = pe.getPermission();  //If you want to know exactly what permission is missing, this is how.
                                                                                        //Note: some PermissionExceptions have no permission provided, only an error message!

                                    channel.sendMessage("PermissionError kicking [")
                                           .append(member.getEffectiveName()).append("]: ")
                                           .append(error.getMessage()).queue();
                                }
                                else
                                {
                                    channel.sendMessage("Unknown error while kicking [")
                                           .append(member.getEffectiveName())
                                           .append("]: <").append(error.getClass().getSimpleName()).append(">: ")
                                           .append(error.getMessage()).queue();
                                }
                            });
                    }
                }
            }
            else
            {
                channel.sendMessage("This is a Guild-Only command!").queue();
            }
        }
        else if (msg.equals("!block"))
        {
            //This is an example of how to use the complete() method on RestAction. The complete method acts similarly to how
            // JDABuilder's awaitReady() works, it waits until the request has been sent before continuing execution.
            //Most developers probably wont need this and can just use queue. If you use complete, JDA will still handle ratelimit
            // control, however if shouldQueue is false it won't queue the Request to be sent after the ratelimit retry after time is past. It
            // will instead fire a RateLimitException!
            //One of the major advantages of complete() is that it returns the object that queue's success consumer would have,
            // but it does it in the same execution context as when the request was made. This may be important for most developers,
            // but, honestly, queue is most likely what developers will want to use as it is faster.

            try
            {
                //Note the fact that complete returns the Message object!
                //The complete() overload queues the Message for execution and will return when the message was sent
                //It does handle rate limits automatically
                Message sentMessage = channel.sendMessage("I blocked and will return the message!").complete();
                //This should only be used if you are expecting to handle rate limits yourself
                //The completion will not succeed if a rate limit is breached and throw a RateLimitException
                Message sentRatelimitMessage = channel.sendMessage("I expect rate limitation and know how to handle it!").complete(false);

                System.out.println("Sent a message using blocking! Luckly I didn't get Ratelimited... MessageId: " + sentMessage.getId());
            }
            catch (RateLimitedException e)
            {
                System.out.println("Whoops! Got ratelimited when attempting to use a .complete() on a RestAction! RetryAfter: " + e.getRetryAfter());
            }
            //Note that RateLimitException is the only checked-exception thrown by .complete()
            catch (RuntimeException e)
            {
                System.out.println("Unfortunately something went wrong when we tried to send the Message and .complete() threw an Exception.");
                e.printStackTrace();
            }
        }
    }

		 private void addAllUsers(MessageReceivedEvent event) {
//			 System.out.println(guild.getMemberCount()+ " : size of members");
//			 System.out.println(guild.getMemberCache().size()+ " : size of members");
//			 System.out.println(guild.getMemberCache()+" : size of members");
//            System.out.println(Guild.BANNER_URL + " STATIC");
//            System.out.println(guild.BANNER_URL + " NON STATIC");
//            System.out.println();
//            System.out.println(Guild.ICON_URL + " STATIC");
//            System.out.println(guild.ICON_URL + " NON STATIC");
            
          //  guild.getRoles()
			  Guild guild = event.getGuild();
		        List<Member> users = guild.getMembers();
        System.out.println(users.size() + " : users size ");
//			 for (Member member : guild.getMembers()) 
//			 {
//				 if(newMember(member)) {
//				 member.getIdLong();
//				 member.getAsMention();
//				 member.getNickname();
//				 CashUser u = new CashUser(0,0, member.getIdLong(),  member.getAsMention(),  member.getNickname());
//			     users2.add(u);
//				 System.out.println("XXX");
//				 }
//			 }
//		BotCommands.checkUsers(users2, FIELDS);
//		 System.out.println("XXX");
	}

		private boolean newMember(Member member) {
			for (CashUser u: users2) { // if id not found member is new
				if(member.getIdLong()== u.get_ID()) {
					return false;
				}
			}
			return true;
		}

		private void giveCash(MessageReceivedEvent event, String[] msgContent) {
			  int amount = -1;
			  String receiverMention = "";
			 try {
		   amount = Integer.parseInt(msgContent[1]);
		   receiverMention = msgContent[2];
			 }catch(Exception e) {
				 System.out.println("invalid format in giveCash() '" + msgContent[1] + "' ::: " + "'"+msgContent[2]+"'");
				 System.out.println( e);
				 return;
			 }
			 //try to give cash
			 long receiverID = BotCommands.getReceiverID(receiverMention);
			 CashUser commandUser = BotCommands.getCommandUser(users, event);
			 CashUser cashReceiver = BotCommands.getUserReceiving(users, receiverID);
					 if(cashReceiver != null) {
						//cash Receiver has to exist at this point
						 //give cash
						 
						// divideUserCash(commandUser, null);
						 addUserCash(amount, cashReceiver , commandUser); // 
						 save(); // update users to file
					 } else {
						 //can't find the receiver msg
						 showGiveCash(0); // default
						 System.out.println("can't find the receiver with ID : '" + receiverID+"'");
					//	 BotCommands.checkUsers(users, FIELDS);
					 }
			  
		
	}

		private void addUserCash(int amount, CashUser cashReceiver, CashUser cashLoser) {
			//cash Receiver has to exist at this point
			if(amount != -1 && amount != 0) 
			{
				if(cashLoser.get_cash() >= amount) 
				{
					 showGiveCash(1); // success bot message
					cashLoser.set_cash(cashLoser.get_cash() - amount);
					cashReceiver.set_cash(cashReceiver.get_cash() + amount);
				} else {
					showGiveCash(2); // not enough cash bot message
				}
				
			}
			
		}

		private void showGiveCash(int showtype) {
			switch(showtype) {
			case 1: //success;
				break;
			case 2:		//not enough cash
				break;
			 default: //receiver doesn't exist/can't find him
				break;
				
			}
			
		}

		private void checkBalance(MessageReceivedEvent event, String[] messageContent) 
		 {
			 String getThisUserBalance = "";
			 try {	
				  getThisUserBalance = messageContent[1];
			 }catch(Exception e) {
				System.out.println("'"+getThisUserBalance + "' HEHJA") ;
			 }
			 CashUser getTheirBalance = null;
			 if(getThisUserBalance.equals("")) {
				  getTheirBalance = BotCommands.getCommandUser(users, event);
			 } else {
				  getTheirBalance = BotCommands.getCommandUser(users, getThisUserBalance);
			 }
			 if(getTheirBalance != null) {
				 showBalance(getTheirBalance);
			 }
		 }

		private void showBalance(CashUser user) {
			// moneybag
			  EmbedBuilder userBalance = new EmbedBuilder();
			  userBalance.setTitle(user.get_tag() + " has " + user.get_cash()+ ":moneybag:");
			  userBalance.setColor(0xf45642);
			  channel.sendMessage(userBalance.build()).queue();
		}

		private void gamble(MessageReceivedEvent event, String[] msgContent) 
		 { // 
			CashUser commandUser = BotCommands.getCommandUser(users, event);
			 int userCash = 0;
			 int betting = -1; 
			 int userChoice = -1; //1 for heads, 0 for tails, -1 for invalid input
			 boolean userVictory = false;
			 
				try {
					 betting = Integer.parseInt(msgContent[1]);
				}catch(Exception e) {
					System.out.println(e + " gamble error, possibly trying to bet string: " + msgContent);
					betting = BotCommands.getBesttingAmount(msgContent[1], commandUser.get_cash());
					if(betting == -1) {
						System.out.println("Invalid format entered for user BET choice : " + msgContent[1]); 
						return;
				} // else betting is now a valid representation of what the user is betting.
				}
				System.out.println("betting : should be good : " + betting);
			 userChoice = BotCommands.getUserChoice(msgContent[2]);
			if(userChoice == -1) {
				System.out.println("Invalid format entered for user FLIP choice : " + msgContent[2]); 
				return;
			}
			//user bet and flip choices are now validated.	
			final String [] HEADS_TAILS = {"tails", "heads"};
			 event.getChannel().sendMessage(commandUser.get_tag() + " is betting " + betting + " on " + HEADS_TAILS[userChoice] + "...").queue();		
			int coinflip = (int)(Math.random() * (1 - 0 + 1)) + 0;
			System.out.println(coinflip + " : coinflip" );//+ " 1 = heads, 0 = tails = check the image if works");
			if(coinflip <= -1 || coinflip > 1) { //test
				System.out.println("ERROR ERROR ERROR ERROR"); System.out.println("ERROR ERROR ERROR ERROR");
				return;
			}
			System.out.println(coinflip + " : COINFLIP" );
//			//valid format = $bf all h, $bf 300 t			
			if(coinflip == userChoice) {
//				//p vic
				System.out.println("victory");
				showGambleResult(true, userChoice, betting, coinflip, commandUser.get_tag());
				gambleResult(true, betting, commandUser); // get user cash after gambling add to user
//				//save
			} else {
				//display(false, userChoice, betting)
				System.out.println("defeat");
				showGambleResult(false, userChoice, betting, coinflip, commandUser.get_tag());
				gambleResult(false, betting, commandUser); // get user cash after gambling add to user
			}
			save();
		 }	
			
			
		 
		 private void showGambleResult(boolean victory, int userChoice, int betting, int flipResult, String userTag) 
		 { 
			final String [] HEADS_TAILS_IMAGE = {"https://nadeko-pictures.nyc3.digitaloceanspaces.com/other/coins/tails.png", "https://nadeko-pictures.nyc3.digitaloceanspaces.com/other/coins/heads.png"};
			 EmbedBuilder coinFlip = new EmbedBuilder();
			 if(victory) {
				  coinFlip.setTitle(userTag + " you've guessed it!"); 
				  coinFlip.setDescription("You've won " + (int)(betting * 0.95));
			 } else {
				 coinFlip.setTitle(userTag + " try again next time!");
			 }
	
				 coinFlip.setImage(HEADS_TAILS_IMAGE[flipResult]);
			//  coinFlip.setImage("https://nadeko-pictures.nyc3.digitaloceanspaces.com/other/coins/heads.png");
			  coinFlip.setColor(0xf45642);
			  channel.sendMessage(coinFlip.build()).queue();
			
		}

		public void gambleResult(boolean victory, int bet, CashUser user) {
			 if(victory) {
				 user.set_cash((int) (user.get_cash() + (bet * 0.95)));
			 } else {
				 user.set_cash(user.get_cash() - bet);
			 }
			 System.out.println(user.get_cash() + " USER KASH");
		 }

		public void save() {
			 Gson gson = new Gson();
//			 String json = gson.toJson(users);
//			 System.out.println(json);
		        try{
		            FileWriter writer = new FileWriter(FILENAME);
		            writer.write(gson.toJson(this.users));
		            writer.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }

		  public void loadUsers() {
		        Gson gson = new Gson();

		        String inFile = "";
		        try{
		            inFile = new String(Files.readAllBytes(Paths.get(FILENAME)));
		        }catch (IOException e) {
		e.printStackTrace();
		        }
		        if(!inFile.equals("")) { // if worked (not empty)
//		        	CashUser [] loadedUsers = gson.fromJson(inFile, CashUser[].class);
//		        	users = new ArrayList<>();
//		            for(int i =0; i < loadedUsers.length; i++) { 
//		             users.add(loadedUsers[i]);            	
//		            }    
		        	Type cashUserType = new TypeToken<ArrayList<CashUser>>(){}.getType();
		        	ArrayList<CashUser> loadedUsers = gson.fromJson(inFile, cashUserType);
		        	this.users = loadedUsers;
		        }
		    }


//		private void getTimely(MessageReceivedEvent event) 
//	{
//	//	String fileText = readFile(FILENAME);
////		textmanipulation.readfile.Read r = new Read(); // make it static
////		ArrayList<String> sortedFile = r.toDiscordBot(FILENAME);
////		//r.readSortedString(sortedFile, FIELDS);
////		BotCommands.fillUsers(users, sortedFile, FIELDS); // users become filled with info from file
////		BotCommands.filterUsers(users);   // filter users = user latest balance + get rid of duplicates
////		BotCommands.checkUsers(users, FIELDS);
//		timely(event);
//	//	writeFile(event, fileText);
//	}

	private void getTimely(MessageReceivedEvent event) {
		CashUser commandUser = BotCommands.getCommandUser(users, event);
		Date date = new Date();
		boolean timelySuccess = false;
		
		if(commandUser == null) {// if user doesn't exist
			System.out.println("user doesn't exist");
			//create user
			//BotCommands.checkUsers(users, FIELDS);
			commandUser = BotCommands.createNewUser(event, date);
			this.users.add(commandUser);
			BotCommands.checkUsers(users,FIELDS);
			//write to file
			 event.getChannel().sendMessage(commandUser.get_tag() + " You've claimed your 1000 cash." + " You can claim again in 1h.").queue();
			 timelySuccess = true;
		} else {
			
			if((commandUser.get_lastTimely() + 3600000) > date.getTime()) {
				System.out.println("not enough  time passed for new timely");
	event.getChannel().sendMessage(commandUser.get_tag() + " You've already claimed your timely reward. You can get it again in :"
	+ (BotCommands.getTimeToNextTimely(3600000 - (date.getTime() - commandUser.get_lastTimely())))).queue();
	 timelySuccess = false;
			} else {
				//update balance + timely
				commandUser.set_cash(commandUser.get_cash() + 1000);
				commandUser.set_lastTimely(date.getTime());
				//write to file
				 event.getChannel().sendMessage(commandUser.get_tag() + " You've claimed your 1000 cash." + " You can claim again in 1h.").queue(); 
				 timelySuccess = true;
			}	
		}
		if(timelySuccess) {
			save();
		}
	}

	private void displayInfo(MessageReceivedEvent event){
		System.out.println("display info called!!");
		 EmbedBuilder info = new EmbedBuilder();
		 info.setTitle("Title", null);
		 info.setColor(new Color(1,0,1,0));
		 info.setColor(new Color(1,1,1,1));
		 info.setColor(new Color(0, 0, 1,1));
		 info.setDescription("Text");
		 info.addField("Title of field", "test of field", false);
		 info.setFooter("Text", "https://github.com/zekroTJA/DiscordBot/blob/master/.websrc/zekroBot_Logo_-_round_small.png");
		  MessageChannel channel = event.getChannel();
		 channel.sendMessage(info.build()).queue();
		// info.setFooter("Created by Juice, wrong img", event.getMember().getUser().getAvatarUrl());
	}
}
