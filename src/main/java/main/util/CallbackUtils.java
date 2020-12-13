package main.util;

import org.telegram.telegrambots.meta.api.objects.Update;

public class CallbackUtils {

    private CallbackUtils() {}
    
    public static int getUserIdFromCallback(Update update) {
        return update.getCallbackQuery().getFrom().getId();
    }
    
    public static String getUsernameFromCallback(Update update) {
        return update.getCallbackQuery().getFrom().getUserName();
    }
    
    public static String getNameFromCallback(Update update) {
        return update.getCallbackQuery().getFrom().getFirstName();
    }
    
    public static String getSurnameFromCallback(Update update) {
        return update.getCallbackQuery().getFrom().getLastName();
    }
    
    public static int getMessageIdFromCallback(Update update) {
        return update.getCallbackQuery().getMessage().getMessageId();
    }
    
}
