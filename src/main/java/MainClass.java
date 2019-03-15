import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import redis.clients.jedis.Jedis;

import java.util.Scanner;

public class MainClass {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Jedis redis = new Jedis();
        String token;
        String username;
		String channel;
		String sudo;
        if (redis.get("iPbotToken")==null){
            System.out.println("Enter bot token :");
            token = input.nextLine();
            redis.set("iPbotToken",token);
        }    
		if (redis.get("iPbotChannel")==null){
            System.out.println("Enter Channel username NOTE > Only username supported!EX : @AFBoTS :");
            channel = input.nextLine();
            redis.set("iPbotChannel",channel);
        }
		if(redis.get("iPIsFirstTime")==null){
			System.out.println("Enter sudo Id :");
			sudo = input.nextLine();
			redis.sadd("ipBotSudosFull",sudo);
			redis.set("iPIsFirstTime","No");
		}
        if (redis.get("iPbotUserName")==null){
            System.out.println("Enter bot username without @ :");
            username = input.nextLine();
            redis.set("iPbotUserName",username);
        }
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new IpInfo());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        System.out.println("Bots Started!\nType panel in your bot PV to get Members count!");
    }
}
