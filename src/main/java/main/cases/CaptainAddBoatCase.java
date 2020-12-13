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
import main.service.BoatService;
import main.util.CallbackUtils;
import main.util.MessageUtils;

@Component
@Case(caseNumber = 3)
public class CaptainAddBoatCase {

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

    @Callback(command = "captain_add_boat_set_name")
    public void captainAddBoatSetName(Update update) {
        int userid = CallbackUtils.getUserIdFromCallback(update);
        this.session.setProperty("captainAddBoatSettingName", true);
        this.session.setProperty("captainAddBoatSettingRegularPlacesCount", false);
        this.session.setProperty("captainAddBoatSettingVipPlacesCount", false);

        this.sender.setText("Input a name for your boat");
        this.sender.setChatId(userid);
        this.sender.sendMessage();
    }

    @Callback(command = "captain_add_boat_set_regular_places_count")
    public void captainAddBoatRegularPlacesCount(Update update) {
        int userid = CallbackUtils.getUserIdFromCallback(update);
        this.session.setProperty("captainAddBoatSettingName", false);
        this.session.setProperty("captainAddBoatSettingRegularPlacesCount", true);
        this.session.setProperty("captainAddBoatSettingVipPlacesCount", false);

        this.sender.setText("Input regular places count for your boat (must be a positive integer)");
        this.sender.setChatId(userid);
        this.sender.sendMessage();
    }

    @Callback(command = "captain_add_boat_set_vip_places_count")
    public void captainAddBoatVipPlacesCount(Update update) {
        int userid = CallbackUtils.getUserIdFromCallback(update);
        this.session.setProperty("captainAddBoatSettingName", false);
        this.session.setProperty("captainAddBoatSettingRegularPlacesCount", false);
        this.session.setProperty("captainAddBoatSettingVipPlacesCount", true);

        this.sender.setText("Input VIP places count for your boat (must be a positive integer)");
        this.sender.setChatId(userid);
        this.sender.sendMessage();
    }

    @Case(message = "*")
    public void captainAddBoatInputParameter(Update update) {
        int userid = MessageUtils.getUserIdFromMessage(update);

        String message = MessageUtils.getMessageContents(update);

        boolean settingName = this.session.getProperty("captainAddBoatSettingName", Boolean.class);
        boolean settingRegularPlaces = this.session.getProperty("captainAddBoatSettingRegularPlacesCount",
                Boolean.class);
        boolean settingVipPlaces = this.session.getProperty("captainAddBoatSettingVipPlacesCount", Boolean.class);

        if (settingName) {
            this.session.setProperty("captainAddBoatName", message);
        }

        if (settingRegularPlaces) {
            try {
                int placesCount = Integer.parseInt(message);
                if(placesCount<=0) {
                    this.sender.setText("Incorrect input, try again");
                    this.sender.setChatId(userid);
                    this.sender.sendMessage();
                    return;
                }
                this.session.setProperty("captainAddBoatRegularPlacesCount", placesCount);
            } catch (Exception e) {
                this.sender.setText("Incorrect input, try again");
                this.sender.setChatId(userid);
                this.sender.sendMessage();
            }
        }

        if (settingVipPlaces) {
            try {
                int placesCount = Integer.parseInt(message);
                if(placesCount<=0) {
                    this.sender.setText("Incorrect input, try again");
                    this.sender.setChatId(userid);
                    this.sender.sendMessage();
                    return;
                }
                this.session.setProperty("captainAddBoatVipPlacesCount", placesCount);
            } catch (Exception e) {
                this.sender.setText("Incorrect input, try again");
                this.sender.setChatId(userid);
                this.sender.sendMessage();
            }
        }

    }

    @Callback(command = "captain_add_boat_finish")
    public void captainAddBoatFinish(Update update) {
        int userid = CallbackUtils.getUserIdFromCallback(update);

        this.session.setProperty("captainAddBoatSettingName", false);
        this.session.setProperty("captainAddBoatSettingRegularPlacesCount", false);
        this.session.setProperty("captainAddBoatSettingVipPlacesCount", false);

        String boatName = null;
        int boatRegularPlaces = -1;
        int boatVipPlaces = -1;

        try {
            boatName = this.session.getProperty("captainAddBoatName", String.class);
            boatRegularPlaces = this.session.getProperty("captainAddBoatRegularPlacesCount", Integer.class);
            boatVipPlaces = this.session.getProperty("captainAddBoatVipPlacesCount", Integer.class);
        } catch (Exception e) {
            this.sender.setText("Please input all parameters first");
            this.sender.setChatId(userid);
            this.sender.sendMessage();
            return;
        }

        this.boatService.createBoat(userid, boatName, boatRegularPlaces, boatVipPlaces);

        this.session.setProperty("captainAddBoatName", null);
        this.session.setProperty("captainAddBoatRegularPlacesCount", null);
        this.session.setProperty("captainAddBoatVipPlacesCount", null);
        
        this.sender.setText("Your boat was added");
        this.sender.setChatId(userid);
        this.sender.sendMessage();

        this.deleter.setChatId(userid);
        this.deleter.setMessageId(this.session.getProperty("mainMessageId", Integer.class));
        this.deleter.deleteMessage();

        this.sender.setChatId(userid);
        List<InlineButton> menuButtons = new ArrayList<>();
        menuButtons.add(new InlineButton("Add a boat", "captain_add_boat"));
        menuButtons.add(new InlineButton("View my boats", "captain_view_boats"));
        this.sender.setInlineButtons(menuButtons);
        this.sender.setText("Main menu");
        this.sender.sendMessage();
        
       
        
        this.router.routeCallbackToClass(userid, CaptainMainMenuCase.class);
    }

}
