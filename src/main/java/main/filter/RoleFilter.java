package main.filter;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Update;

import application.boilerplate.MessageSender;
import application.boilerplate.dto.InlineButton;
import application.context.annotation.Component;
import application.context.annotation.Filter;
import application.context.annotation.Inject;
import application.context.filter.FilterAdapter;
import application.exception.FilterException;
import application.routing.RouterManager;
import application.session.SessionManager;
import main.cases.CaptainMainMenuCase;
import main.cases.UserMainMenuCase;
import main.dto.Captain;
import main.dto.User;
import main.service.CaptainService;
import main.service.UserService;

@Filter(order=0)
@Component
public class RoleFilter implements FilterAdapter{

    @Inject
    private UserService userService;
    
    @Inject
    private CaptainService captainService;
    
    @Inject
    private application.boilerplate.UserService coreUserService;
    
    @Inject
    private RouterManager router;
    
    @Inject
    private MessageSender sender;
    
    @Inject
    private SessionManager session;
    
    @Override
    public Update filter(Update update) throws FilterException {
        int userid = -1;
        if(update.hasCallbackQuery()) {
            userid = update.getCallbackQuery().getFrom().getId();
        }else {
            userid = update.getMessage().getFrom().getId();
        }
        if(userid==-1) {
            throw new FilterException("User not found");
        }
        int userCase = this.coreUserService.getUserState(userid);
        if(userCase==0) {
            try {
                this.userService.getUserById(userid);
                proceedToUserMenu(userid);
            }catch(NullPointerException e) {
                e.printStackTrace();
                try {
                    this.captainService.getCaptainById(userid);
                    proceedToCaptainMenu(userid);
                }catch(NullPointerException e1) {
                    throw new FilterException("User not found");
                }
            }
            
        }
        return update;
    }

    private void proceedToCaptainMenu(int userid) {
       
        List<InlineButton> menuButtons = new ArrayList<>();
        menuButtons.add(new InlineButton("Add a boat", "captain_add_boat"));
        menuButtons.add(new InlineButton("View my boats", "captain_view_boats"));

        sender.setInlineButtons(menuButtons);
        
        sender.setChatId(userid);
        sender.setText("Choose what to do next");
        sender.sendMessage();
        router.routeCallbackToClass(userid, CaptainMainMenuCase.class);
    }

    private void proceedToUserMenu(int userid) {
       
        List<InlineButton> menuButtons = new ArrayList<>();
        menuButtons.add(new InlineButton("Create order", "client_create_order"));
        menuButtons.add(new InlineButton("View my orders", "client_view_orders"));

        sender.setInlineButtons(menuButtons);
        
        sender.setChatId(userid);
        sender.setText("Choose what to do next");
        sender.sendMessage();
        router.routeCallbackToClass(userid, UserMainMenuCase.class);
    }

    
    
}
