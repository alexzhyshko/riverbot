package main.util;

import org.telegram.telegrambots.meta.api.objects.Update;

public class MessageUtils {

    private MessageUtils() {}
    
    public static int getUserIdFromMessage(Update update) {
        return update.getMessage().getFrom().getId();
    }
    
    public static String getUsernameFromMessage(Update update) {
        return update.getMessage().getFrom().getUserName();
    }
    
    public static String getMessageContents(Update update) {
        return update.getMessage().getText();
    }
    
}
