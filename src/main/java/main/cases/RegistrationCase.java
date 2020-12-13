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
import main.util.MessageUtils;

@Case(caseNumber=0)
@Component
public class RegistrationCase {

    
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
    
    private static final String GREETING_TEXT_TEMPLATE = "Hello, 1. Choose your role using buttons below, please.\nMake a wise choice, you will not be able to change this later.";
    
    @Case(message="/start")
    public void registerUser(Update update) {
        int userid = MessageUtils.getUserIdFromMessage(update);
        String username = MessageUtils.getUsernameFromMessage(update);
        this.sender.setChatId(userid);
        this.sender.setText(GREETING_TEXT_TEMPLATE.replace("1", username));
        List<InlineButton> roleChooseButtons = new ArrayList<>();
        InlineButton btnClient = new InlineButton("Client", "role_client");
        InlineButton btnCaptain = new InlineButton("Captain", "role_captain");
        roleChooseButtons.add(btnClient);
        roleChooseButtons.add(btnCaptain);
        this.sender.setInlineButtons(roleChooseButtons);
        this.sender.sendMessage();
        
        this.router.routeCallbackToClass(userid, ChooseRoleCase.class);
    }
    
    
}
