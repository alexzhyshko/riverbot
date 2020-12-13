package main.cases;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Update;

import application.boilerplate.MessageDeleter;
import application.boilerplate.MessageEditor;
import application.boilerplate.MessageSender;
import application.boilerplate.dto.InlineButton;
import application.context.annotation.Callback;
import application.context.annotation.Case;
import application.context.annotation.Component;
import application.context.annotation.Inject;
import application.routing.RouterManager;
import application.session.SessionManager;
import main.dto.Boat;
import main.service.BoatService;
import main.util.CallbackUtils;

@Case(caseNumber=2)
@Component
public class CaptainMainMenuCase {

    @Inject
    private MessageEditor editor;
    
    @Inject
    private MessageSender sender;
    
    @Inject
    private SessionManager session;
    
    @Inject
    private BoatService boatService;
    
    @Inject
    private MessageDeleter deleter;
    
    @Inject
    private RouterManager router;
    
    @Callback(command="captain_add_boat")
    public void captainAddBoat(Update update) {
        int userid = CallbackUtils.getUserIdFromCallback(update);
        int messageId = CallbackUtils.getMessageIdFromCallback(update);
        
        this.session.setProperty("mainMessageId", messageId);
        
        this.editor.setText("Input following parameters:");
        
        List<InlineButton> menuButtons = new ArrayList<>();
        menuButtons.add(new InlineButton("Name", "captain_add_boat_set_name"));
        menuButtons.add(new InlineButton("Regular places count", "captain_add_boat_set_regular_places_count"));
        menuButtons.add(new InlineButton("Vip places count", "captain_add_boat_set_vip_places_count"));
        menuButtons.add(new InlineButton("Finish", "captain_add_boat_finish"));

        this.editor.setInlineButtons(menuButtons);
        this.editor.setMessageId(messageId);
        this.editor.setChatId(userid);
        this.editor.editMessage();
        
        this.router.routeCallbackToClass(userid, CaptainAddBoatCase.class);
    }
    
    
    
    @Callback(command="captain_view_boats")
    public void captainViewBoats(Update update) {
        int userid = CallbackUtils.getUserIdFromCallback(update);
        int messageId = CallbackUtils.getMessageIdFromCallback(update);
        
        List<Boat> captainBoats = this.boatService.getAllByCaptainId(userid);
        
        if(captainBoats.isEmpty()) {
            this.sender.setText("No boats yet");
            this.sender.setChatId(userid);
            this.sender.sendMessage();
        }
        
        for(Boat boat : captainBoats) {
            StringBuilder textBuilder = new StringBuilder();
            textBuilder
                .append("Name: ")
                .append(boat.getName())
                .append("\nRegular places: ")
                .append(boat.getRegularPlacesCount())
                .append("\nVIP places: ")
                .append(boat.getVipPlacesCount())
                .append("\n---------\n");
            this.sender.setText(textBuilder.toString());
            this.sender.setChatId(userid);
            this.sender.sendMessage();
        }
        
        this.deleter.setMessageId(messageId);
        this.deleter.setChatId(userid);
        this.deleter.deleteMessage();
        
        this.sender.setChatId(userid);
        List<InlineButton> menuButtons = new ArrayList<>();
        menuButtons.add(new InlineButton("Add a boat", "captain_add_boat"));
        menuButtons.add(new InlineButton("View my boats", "captain_view_boats"));
        this.sender.setInlineButtons(menuButtons);
        this.sender.setText("Main menu");
        this.sender.sendMessage();
    }
    
}
