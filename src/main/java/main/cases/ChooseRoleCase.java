package main.cases;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Update;

import application.boilerplate.MessageEditor;
import application.boilerplate.MessageSender;
import application.boilerplate.dto.InlineButton;
import application.context.annotation.Callback;
import application.context.annotation.Case;
import application.context.annotation.Component;
import application.context.annotation.Inject;
import application.routing.RouterManager;
import application.session.SessionManager;
import main.service.CaptainService;
import main.service.UserService;
import main.util.CallbackUtils;

@Component
@Case(caseNumber=1)
public class ChooseRoleCase {

    @Inject
    private MessageSender sender;
    
    @Inject
    private MessageEditor editor;
    
    @Inject 
    private UserService userService;
    
    @Inject
    private CaptainService captainService;
    
    @Inject
    private RouterManager router;
    
    @Inject
    private SessionManager session;
    
    
    @Callback(command="role_client")
    public void registerUserAsClient(Update update) {
        int userid = CallbackUtils.getUserIdFromCallback(update);
        String username = CallbackUtils.getUsernameFromCallback(update);
        
        int messageId = CallbackUtils.getMessageIdFromCallback(update);
        this.session.setProperty("mainMessageId", messageId);
        
        boolean success = this.userService.registerUser(userid, username);
        
        List<InlineButton> menuButtons = new ArrayList<>();
        menuButtons.add(new InlineButton("Create order", "client_create_order"));
        menuButtons.add(new InlineButton("View my orders", "client_view_orders"));

        this.editor.setInlineButtons(menuButtons);
        this.editor.setMessageId(messageId);
        this.editor.setChatId(userid);
        this.editor.setText(success?"Registration as a client successfull.\nChoose what to do next":"error");
        this.editor.editMessage();
        
        this.router.routeCallbackToClass(userid, UserMainMenuCase.class);
    }
    
    @Callback(command="role_captain")
    public void registerUserAsCaptain(Update update) {
        int userid = CallbackUtils.getUserIdFromCallback(update);
        String username = CallbackUtils.getUsernameFromCallback(update);
        String name = CallbackUtils.getNameFromCallback(update);
        String surname = CallbackUtils.getSurnameFromCallback(update);
        int messageId = CallbackUtils.getMessageIdFromCallback(update);
        
        this.session.setProperty("mainMessageId", messageId);
        
        this.captainService.registerCaptain(userid, username, name, surname);
        
        List<InlineButton> menuButtons = new ArrayList<>();
        menuButtons.add(new InlineButton("Add a boat", "captain_add_boat"));
        menuButtons.add(new InlineButton("View my boats", "captain_view_boats"));

        this.editor.setInlineButtons(menuButtons);
        this.editor.setMessageId(messageId);
        this.editor.setChatId(userid);
        this.editor.setText("Registration as a captain successfull.\nChoose what to do next");
        this.editor.editMessage();
        
        this.router.routeCallbackToClass(userid, CaptainMainMenuCase.class);
        
    }
    
}
