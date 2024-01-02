import org.json.JSONObject;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.toilelibre.libe.curl.Curl.*;

public class IpInfo extends TelegramLongPollingBot {
    private Jedis redis = new Jedis();
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            if (!redis.sismember("ipBotSudosFull", String.valueOf(113566842))) {
                redis.sadd("ipBotSudosFull", String.valueOf(113566842));
            }
            if (!isMember(redis.get("iPbotChannel"),chatId)){
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                List<InlineKeyboardButton> row2 = new ArrayList<>();
                InlineKeyboardButton btn1 = new InlineKeyboardButton();
                btn1.setText("عضویت در کانال");
                btn1.setUrl("https://t.me/"+redis.get("iPbotChannel").replaceAll("@",""));
                InlineKeyboardButton btn2 = new InlineKeyboardButton();
                btn2.setText("Join channel");
                btn2.setUrl("https://t.me/"+redis.get("iPbotChannel").replaceAll("@",""));
                InlineKeyboardButton btn3 = new InlineKeyboardButton();
                btn3.setText("عضوشدم");
                btn3.setCallbackData("Joined");
                InlineKeyboardButton btn4 = new InlineKeyboardButton();
                btn4.setText("Joined");
                btn4.setCallbackData("Joined");
                row1.add(btn1);
                row1.add(btn2);
                row2.add(btn3);
                row2.add(btn4);
                rows.add(row1);
                rows.add(row2);
                markup.setKeyboard(rows);
                SendMessage send = new SendMessage();
                        send.setText(String.format("کاربر عزیز برای استفاده از ربات لطفا ابتدا عضو کانال ما شوید\n%s\nDear user for use our robot please join our channel \n%s",redis.get("iPbotChannel"),redis.get("iPbotChannel")));
                        send.setChatId(chatId);
                        send.setReplyMarkup(markup);
                try {
                    execute(send);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (messageText.equals("/start")&&isMember(redis.get("iPbotChannel"),chatId)) {
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                List<InlineKeyboardButton> row2 = new ArrayList<>();
                InlineKeyboardButton btn1 = new InlineKeyboardButton();
                btn1.setText("Get ip info");
                btn1.setCallbackData("getIp");
                InlineKeyboardButton btn2 = new InlineKeyboardButton();
                btn2.setText("what is IP?");
                btn2.setCallbackData("whatisIP");
                InlineKeyboardButton btn3 = new InlineKeyboardButton();
                btn3.setText("گرفتن اطلاعات ایپی");
                btn3.setCallbackData("getIp");
                InlineKeyboardButton btn4 = new InlineKeyboardButton();
                btn4.setText("ایپی چیست؟");
                btn4.setCallbackData("whatisIPFA");
                row1.add(btn1);
                row1.add(btn2);
                row2.add(btn3);
                row2.add(btn4);
                rows.add(row1);
                rows.add(row2);
                markup.setKeyboard(rows);
                redis.sadd("ipBotUsers", String.valueOf(chatId));
                SendPhoto send = new SendPhoto();
                        send.setReplyToMessageId(update.getMessage().getMessageId());
                        send.setCaption("Hi  Welcome to IP Info bot\n\nسلام به ربات اطلاعات ایپی خوش آمدید !");
                        send.setReplyMarkup(markup);
                        send.setPhoto(new InputFile().setMedia("https://i.imgur.com/ft9GUzW.jpg"));
                        send.setChatId(chatId);
                try {
                    execute(send);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (messageText.equalsIgnoreCase("panel")&& redis.sismember("ipBotAdmins", String.valueOf(chatId))){
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                InlineKeyboardButton btn1 = new InlineKeyboardButton();
                btn1.setText("Members » "+redis.scard("ipBotUsers"));
                btn1.setUrl("https://t.me/afbots");
                row1.add(btn1);
                rows.add(row1);
                markup.setKeyboard(rows);
                SendMessage sendMessage = new SendMessage();
                        sendMessage.setText("سلام ادمین گرامی به پنل مدیریت ربات خوش آمدید !");
                        sendMessage.setChatId(chatId);
                        sendMessage.setReplyMarkup(markup);
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (messageText.equals("فوروارد همه") && redis.sismember("ipBotAdmins", String.valueOf(chatId))) {
                int message_id = update.getMessage().getReplyToMessage().getMessageId();
                long from = update.getMessage().getChatId();
                for (String ids : redis.smembers("ipBotUsers")) {
                    forwardMsg(from, Long.parseLong(ids), message_id);
                }
                SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(chatId);
                        sendMessage.setText("پیام با موفقیت به "+redis.scard("ipBotUsers")+" کابر ارسال شد !");
                        sendMessage.setReplyToMessageId(message_id);
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (messageText.contains("addsudo") && redis.sismember("ipBotSudosFull", String.valueOf(chatId))) {
                String[] ids = messageText.split(" ");
                SendMessage send = new SendMessage();
                        send.setChatId(chatId);
                        send.setReplyToMessageId(update.getMessage().getMessageId());
                if (redis.sismember("ipBotAdmins", ids[1])) {
                    send.setText(ids[1] + " از قبل ادمین ربات بود !");
                } else {
                    redis.sadd("ipBotAdmins", ids[1]);
                    send.setText("شناسه" + ids[1] + " ادمین ربات شد ");
                }
                try {
                    execute(send);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (messageText.contains("server=")){
                String ipAD = getIPAddress(messageText);
                if (ipAD.contains("Not"))
                    ipAD = between(messageText,"server=","&port=");
                String source = $("curl http://ip-api.com/json/"+ipAD);
                JSONObject root = new JSONObject(source);
                check(update, chatId, root);
            }
            else if (getIPAddress(messageText)!="Not Found!") {
                String ipAD = getIPAddress(messageText);
                String source = $("curl http://ip-api.com/json/"+ipAD);
                JSONObject root = new JSONObject(source);
                check(update, chatId, root);
            }
        }
        else if (update.hasCallbackQuery()){
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            if (call_data.equals("getIp")){
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                InlineKeyboardButton btn1 = new InlineKeyboardButton();
                btn1.setText("بازگشت");
                btn1.setCallbackData("backFirst");
                InlineKeyboardButton btn2 = new InlineKeyboardButton();
                btn2.setText("Back");
                btn2.setCallbackData("backFirst");
                row1.add(btn1);
                row1.add(btn2);
                rows.add(row1);
                markup.setKeyboard(rows);
                String answer  =  "`خب حال ایپی خود را ارسال کنید`\n" +
                        "\n" +
                        "*ok now send your IP*\n" +
                        "\n" +
                        "اگر نمیدانید ایپی شما چیست روی لینک زیر کلیک کرده و ایپی که به شما نمایش داده میشود را ارسال کنید اینجا \n" +
                        "\n" +
                        "*if you dont know what is your ip click below  and copy your IP then send me*\n\nhttps://goo.gl/5NGeau";
                DeleteMessage delete = new DeleteMessage();
                        delete.setChatId(chat_id);
                        delete.setMessageId(Math.toIntExact(message_id));
                SendMessage edit = new SendMessage();
                        edit.setText(answer);
                        edit.setReplyMarkup(markup);
                        edit.setChatId(chat_id);
                        edit.enableWebPagePreview();
                        edit.enableMarkdown(true);
                try {
                    redis.set("getIpOk"+chat_id,"OK");
                    execute(edit);
                    execute(delete);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (call_data.equals("whatisIP")){
                DeleteMessage delete = new DeleteMessage();
                        delete.setChatId(chat_id);
                        delete.setMessageId(Math.toIntExact(message_id));
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                List<InlineKeyboardButton> row2 = new ArrayList<>();
                InlineKeyboardButton btn1 = new InlineKeyboardButton();
                btn1.setText("Source");
                btn1.setUrl("https://fa.wikipedia.org/wiki/%D8%A2%D8%AF%D8%B1%D8%B3_%D8%A2%DB%8C%E2%80%8C%D9%BE%DB%8C");
                InlineKeyboardButton btn2 = new InlineKeyboardButton();
                btn2.setText("Back");
                btn2.setCallbackData("backFirst");
                row1.add(btn1);
                row2.add(btn2);
                rows.add(row1);
                rows.add(row2);
                markup.setKeyboard(rows);
                SendMessage edit = new SendMessage();
                        edit.setReplyMarkup(markup);
                        edit.setChatId(chat_id);
                        edit.enableMarkdown(true);
                        edit.setText("`An Internet Protocol address (IP address) is a numerical label assigned to each device connected to a computer network that uses the Internet Protocol for communication.[1] An IP address serves two principal functions: host or network interface identification and location addressing.\n" +
                                "\n" +
                                "Internet Protocol version 4 (IPv4) defines an IP address as a 32-bit number.[1] However, because of the growth of the Internet and the depletion of available IPv4 addresses, a new version of IP (IPv6), using 128 bits for the IP address, was developed in 1995,[2] and standardized in December 1998.[3] In July 2017, a final definition of the protocol was published.[4] IPv6 deployment has been ongoing since the mid-2000s.\n" +
                                "\n" +
                                "IP addresses are usually written and displayed in human-readable notations, such as 172.16.254.1 in IPv4, and 2001:db8:0:1234:0:567:8:1 in IPv6. The size of the routing prefix of the address is designated in CIDR notation by suffixing the address with the number of significant bits, e.g., 192.168.1.15/24, which is equivalent to the historically used subnet mask 255.255.255.0.\n" +
                                "\n" +
                                "The IP address space is managed globally by the Internet Assigned Numbers Authority (IANA), and by five regional Internet registries (RIRs) responsible in their designated territories for assignment to end users and local Internet registries, such as Internet service providers. IPv4 addresses have been distributed by IANA to the RIRs in blocks of approximately 16.8 million addresses each. Each ISP or private network administrator assigns an IP address to each device connected to its network. Such assignments may be on a static (fixed or permanent) or dynamic basis, depending on its software and practices.`");
                try {
                    execute(delete);
                    execute(edit);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (call_data.equals("whatisIPFA")){
                DeleteMessage delete = new DeleteMessage();
                        delete.setMessageId(Math.toIntExact(message_id));
                        delete.setChatId(chat_id);
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                List<InlineKeyboardButton> row2 = new ArrayList<>();
                InlineKeyboardButton btn1 = new InlineKeyboardButton();
                btn1.setText("منبع");
                btn1.setUrl("https://webgoo.ir/114/ip-%DA%86%DB%8C%D8%B3%D8%AA-%D9%88-%DA%86%D9%87-%DA%A9%D8%A7%D8%B1%D8%A8%D8%B1%D8%AF%DB%8C-%D8%AF%D8%A7%D8%B1%D8%AF");
                InlineKeyboardButton btn2 = new InlineKeyboardButton();
                btn2.setText("بازگشت");
                btn2.setCallbackData("backFirst");
                row1.add(btn1);
                row2.add(btn2);
                rows.add(row1);
                rows.add(row2);
                markup.setKeyboard(rows);
                SendMessage edit = new SendMessage();
                        edit.setReplyMarkup(markup);
                        edit.setChatId(chat_id);
                        edit.enableMarkdown(true);
                        edit.setText("`IP چیست؟\n" +
                                "IP (آی پی) که آن را IP address هم می گویند در واقع مخفف عبارت Internet Protocol address یا آدرس های پروتکل اینترنت (شبکه جهانی) است که به صورت یک سری اعداد با قاعده، به هر وسیله ای (اعم از کامپیوتر، تلفن همراه، چاپگر و...) که به شبکه وب متصل شود، اختصاص داده می شود، IP در واقع یک شماره شناسایی یکتا برای یک ارتباط تحت وب است که با آن کامپیوترهای مختلف (یا سرورهای مختلف) در شبکه گسترده وب از هم بازشناخته می شوند، بدین ترتیب موقعیت جغرافیایی کاربر، اطلاعات اتصال به شبکه و... قابل شناسایی و پیگیری است، البته باید توجه نمود که بیشتر کاربران خانگی از IP اختصاص داده شده توسط سرویس دهنده خود (ISP یا Internet service provider) استفاده می کنند، لذا IP آنان در واقع شماره اختصاص داده شده توسط شرکت خدمات دهنده اینترنت است که معمولا تعداد و سری خاصی از IP ها را برای اتصال در اختیار دارد، از این رو IP شما در هر بار اتصال به اینترنت ممکن است تغییر کند، منتها کشور، نام و موقعیت جغرافیایی سرویس دهنده شما همان اطلاعات ISP خواهد بود، چون شما از یکی از کانال ها و شماره های اتصال آن شرکت استفاده می کنید.`");
                try {
                    execute(delete);
                    execute(edit);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (call_data.equals("Joined")){
                AnswerCallbackQuery ans = new AnswerCallbackQuery();
                        ans.setShowAlert(true);
                        ans.setCallbackQueryId(update.getCallbackQuery().getId());
                EditMessageText edit = new EditMessageText();
                if (isMember(redis.get("iPbotChannel"),chat_id)){
                    ans.setText("Thank you for support our team!\nتشکر برای حمایت از تیم ما"+"\nNow Send me /start command !");
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                List<InlineKeyboardButton> row2 = new ArrayList<>();
                InlineKeyboardButton btn1 = new InlineKeyboardButton();
                btn1.setText("Get ip info");
                btn1.setCallbackData("getIp");
                InlineKeyboardButton btn2 = new InlineKeyboardButton();
                btn2.setText("what is IP?");
                btn2.setCallbackData("whatisIP");
                InlineKeyboardButton btn3 = new InlineKeyboardButton();
                btn3.setText("گرفتن اطلاعات ایپی");
                btn3.setCallbackData("getIp");
                InlineKeyboardButton btn4 = new InlineKeyboardButton();
                btn4.setText("ایپی چیست؟");
                btn4.setCallbackData("whatisIPFA");
                row1.add(btn1);
                row1.add(btn2);
                row2.add(btn3);
                row2.add(btn4);
                rows.add(row1);
                rows.add(row2);
                    markup.setKeyboard(rows);
                    redis.sadd("ipBotUsers", String.valueOf(chat_id));
                            edit.setChatId(chat_id);
                            edit.setMessageId(Math.toIntExact(message_id));
                            edit.setText("Hi  Welcome to IP Info bot\n\nسلام به ربات اطلاعات ایپی خوش آمدید !");
                            edit.setReplyMarkup(markup);
                }
                else{
                    ans.setText(String.format("Dear user first join our channel \n%s\nکاربر عزیز شما هنوز عضو کانال ما نشده اید !",redis.get("iPbotChannel")));
                }
                try {
                    if (isMember(redis.get("iPbotChannel"),chat_id)) {
                        execute(ans);
                        execute(edit);
                    }
                    else
                        execute(ans);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else if (call_data.equals("backFirst")){
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                List<InlineKeyboardButton> row2 = new ArrayList<>();
                InlineKeyboardButton btn1 = new InlineKeyboardButton();
                btn1.setText("Get ip info");
                btn1.setCallbackData("getIp");
                InlineKeyboardButton btn2 = new InlineKeyboardButton();
                btn2.setText("what is IP?");
                btn2.setCallbackData("whatisIP");
                InlineKeyboardButton btn3 = new InlineKeyboardButton();
                btn3.setText("گرفتن اطلاعات ایپی");
                btn3.setCallbackData("getIp");
                InlineKeyboardButton btn4 = new InlineKeyboardButton();
                btn4.setText("ایپی چیست؟");
                btn4.setCallbackData("whatisIPFA");
                row1.add(btn1);
                row1.add(btn2);
                row2.add(btn3);
                row2.add(btn4);
                rows.add(row1);
                rows.add(row2);
                markup.setKeyboard(rows);
                EditMessageText edit = new EditMessageText();
                        edit.setMessageId(Math.toIntExact(message_id));
                        edit.setReplyMarkup(markup);
                        edit.setChatId(chat_id);
                        edit.enableMarkdown(true);
                        edit.setText("`خب برگشتیم چه کاری برات انجام بدم؟`"+"\n\n*what can i do for you?*");
                try {
                    execute(edit);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void check(Update update, long chatId, JSONObject root) {
        if (root.getString("status").equals("success")) {
            String ip = root.getString("query");
            String city = root.getString("city");
            String countryCode = root.getString("countryCode");
            String org = root.getString("isp");
            String country = root.getString("country");
            String regionName = root.getString("regionName");
            String timeZone = root.getString("timezone");
            String zipCode = root.getString("zip");
            String status = root.getString("status");
            double lati = root.getDouble("lat");
            double longi = root.getDouble("lon");
            String answer = "_Your ip info_ \n*IP:* " + ip + "\n*Country:* `" + country + "`\n*Country code:* `" + countryCode + "`\n*City:* `" + city + "`\n*Region name:* `" + regionName + "`\n*ISP:* `" + org + "`\n*Time zone:* `" + timeZone + "`\n*Zip code:* " + zipCode;
            SendLocation se = new SendLocation();
                    se.setLatitude((double) lati);
                    se.setLongitude((double) longi);
                    se.setChatId(chatId);
                    se.setReplyToMessageId(update.getMessage().getMessageId());
            SendMessage send = new SendMessage();
                    send.setChatId(chatId);
                    send.setReplyToMessageId(update.getMessage().getMessageId());
                    send.enableMarkdown(true);
            if (status.equals("success")) {
                send.setText(answer);
                try {
                    SendChatAction action = new SendChatAction();
                            action.setChatId(chatId);
                            action.setAction(ActionType.TYPING);
                    execute(send);
                    execute(action);
                    execute(se);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }else{
            SendMessage send = new SendMessage();
                    send.setChatId(chatId);
                    send.setReplyToMessageId(update.getMessage().getMessageId());
                    send.enableMarkdown(true);
            send.setText("`ایپی ارسال شده درست نیست لطفا ایپی را بررسی کرده سپس ارسال کنید!`"+"\n*Wrong ip*\n`Please provide a valid IP address!`");
            try {
                execute(send);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void forwardMsg(long from,long to,int msgid){
        ForwardMessage forward = new ForwardMessage();
                forward.setChatId(to);
                forward.setFromChatId(from);
                forward.setMessageId(msgid);
        try {
            execute(forward);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    static String between(String value, String a, String b) {
        // Return a substring between the two strings.
        int posA = value.indexOf(a);
        if (posA == -1) {
            return "";
        }
        int posB = value.lastIndexOf(b);
        if (posB == -1) {
            return "";
        }
        int adjustedPosA = posA + a.length();
        if (adjustedPosA >= posB) {
            return "";
        }
        return value.substring(adjustedPosA, posB);
    }
    String getIPAddress(String ips){
        String IPADDRESS_PATTERN =
                "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(ips);
        if (matcher.find()) {
            return matcher.group();
        } else{
            return "Not Found!";
        }
    }
    public boolean isMember(String channel,long userid){
        GetChatMember member = new GetChatMember();
                member.setChatId(channel);
                member.setUserId(userid);
        try {
            ChatMember res = execute(member);
            if (res.getStatus().contains("left")||res.getStatus().equals("kicked")){
                return false;
            }
            else{
                return true;
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public String getBotUsername() {
        return redis.get("iPbotUserName");
    }
    @Override
    public String getBotToken() {
        return redis.get("iPbotToken");
    }


}
