package main.cases;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import main.dto.Captain;
import main.dto.Timetable;
import main.service.OrderService;
import main.service.TimetableService;
import main.util.CallbackUtils;
import main.util.MessageUtils;

@Case(caseNumber=6)
@Component
public class UserCreateOrderCase {

    @Inject
    private MessageSender sender;
    
    @Inject
    private OrderService orderService;
    
    @Inject
    private TimetableService timetableService;
    
    @Inject
    private MessageDeleter deleter;
    
    @Inject
    private MessageEditor editor;
    
    @Inject
    private SessionManager session;
    
    @Inject
    private RouterManager router;
    
    @Callback(command="client_create_order_choose_trip")
    public void createOrderChooseTrip(Update update) {
        
        int userid = CallbackUtils.getUserIdFromCallback(update);
        int messageid = CallbackUtils.getMessageIdFromCallback(update);
        
        this.session.setProperty("mainMessageId", messageid);
        
        List<Timetable> availableTrips = this.timetableService.getAllAvailable(LocalDateTime.now());
        
        List<Integer> availableIds = availableTrips.stream().map(trip->trip.getId()).collect(Collectors.toList());
        this.session.setProperty("availableTripIds", availableIds);
        
        for(Timetable timetable : availableTrips) {
            String text = parseTimetableData(timetable);
            this.sender.setText(text);
            this.sender.setChatId(userid);
            this.sender.sendMessage();
        }
        this.sender.setText("Choose a trip and input its ID as a positive integer");
        this.sender.setChatId(userid);
        this.sender.sendMessage();
        
        this.session.setProperty("client_create_order_choosing_trip", true);
        this.session.setProperty("client_create_order_choosing_regular_places_count", false);
        this.session.setProperty("client_create_order_choosing_vip_places_count", false);
    }
    
    @Callback(command="client_create_order_choose_regular_places_count")
    public void createOrderChooseRegularPlacesCount(Update update) {
        int userid = CallbackUtils.getUserIdFromCallback(update);
        
        int messageid = CallbackUtils.getMessageIdFromCallback(update);
        this.session.setProperty("mainMessageId", messageid);
        
        this.session.setProperty("client_create_order_choosing_trip", false);
        this.session.setProperty("client_create_order_choosing_regular_places_count", true);
        this.session.setProperty("client_create_order_choosing_vip_places_count", false);

        this.sender.setText("Input desired regular places count");
        this.sender.setChatId(userid);
        this.sender.sendMessage();
    }
    
    @Callback(command="client_create_order_choose_vip_places_count")
    public void createOrderChooseVipPlacesCount(Update update) {
        int userid = CallbackUtils.getUserIdFromCallback(update);
        
        int messageid = CallbackUtils.getMessageIdFromCallback(update);
        this.session.setProperty("mainMessageId", messageid);
        
        this.session.setProperty("client_create_order_choosing_trip", false);
        this.session.setProperty("client_create_order_choosing_regular_places_count", false);
        this.session.setProperty("client_create_order_choosing_vip_places_count", true);

        this.sender.setText("Input desired VIP places count");
        this.sender.setChatId(userid);
        this.sender.sendMessage();
    }
    
    @Callback(command="client_create_order_finish")
    public void createOrderFinish(Update update) {
        int userid = CallbackUtils.getUserIdFromCallback(update);
        
        this.session.setProperty("client_create_order_choosing_trip", false);
        this.session.setProperty("client_create_order_choosing_regular_places_count", false);
        this.session.setProperty("client_create_order_choosing_vip_places_count", false);
        
        int timetableid = -1;
        int regularPlaces = -1;
        int vipPlaces = -1;
        
        try {
            timetableid = this.session.getProperty("client_create_order_trip_id", Integer.class);
        } catch (Exception e) {
            // do nothing here
        }
        try {
            regularPlaces = this.session.getProperty("client_create_order_trip_regular_places", Integer.class);
        } catch (Exception e) {
            // do nothing here
        }
        try {
            vipPlaces = this.session.getProperty("client_create_order_trip_vip_places", Integer.class);
        } catch (Exception e) {
            // do nothing here
        }
        
        
        if(timetableid==-1 ||(regularPlaces==-1 && vipPlaces==-1)) {
            this.sender.setText("Please input all parameters first");
            this.sender.setChatId(userid);
            this.sender.sendMessage();
            return;
        }
        
        boolean orderCreated = this.orderService.createOrder(userid, timetableid, regularPlaces, vipPlaces);
        
        this.session.setProperty("client_create_order_choosing_trip", null);
        this.session.setProperty("client_create_order_choosing_regular_places_count", null);
        this.session.setProperty("client_create_order_choosing_vip_places_count", null);
        
        this.session.setProperty("client_create_order_trip_id", null);
        this.session.setProperty("client_create_order_trip_regular_places", null);
        this.session.setProperty("client_create_order_trip_vip_places", null);
        
        this.sender.setText(orderCreated?"Your order was created":"Something ");
        this.sender.setChatId(userid);
        this.sender.sendMessage();

        this.deleter.setChatId(userid);
        this.deleter.setMessageId(this.session.getProperty("mainMessageId", Integer.class));
        this.deleter.deleteMessage();
        this.session.setProperty("mainMessageId", null);
        
        this.sender.setChatId(userid);
        List<InlineButton> menuButtons = new ArrayList<>();
        menuButtons.add(new InlineButton("Create order", "client_create_order"));
        menuButtons.add(new InlineButton("View my orders", "client_view_orders"));
        this.sender.setInlineButtons(menuButtons);
        this.sender.setText("Main menu");
        this.sender.sendMessage();
        
        this.router.routeCallbackToClass(userid, UserMainMenuCase.class);
    }
    
    @Case
    public void userInput(Update update) {
        int userid = MessageUtils.getUserIdFromMessage(update);
        
        if(this.session.getProperty("client_create_order_choosing_trip", Boolean.class)) {
            String message = MessageUtils.getMessageContents(update);
            try {
                int id = Integer.parseInt(message);
                List<Integer> availableIds = this.session.getProperty("availableTripIds", List.class);
                if(id<=0 || !availableIds.contains(id)) {
                    this.sender.setText("Incorrect input, try again");
                    this.sender.setChatId(userid);
                    this.sender.sendMessage(); 
                    return;
                }
                
                
                this.session.setProperty("client_create_order_trip_id", id);
                this.session.setProperty("availableTripIds", null);
                
                this.session.setProperty("client_create_order_choosing_trip", false);
                int mainMessageId = this.session.getProperty("mainMessageId", Integer.class);
                this.session.setProperty("mainMessageId", null);
                
                List<InlineButton> menuButtons = new ArrayList<>();
                menuButtons.add(new InlineButton("Choose trip", "client_create_order_choose_trip"));
                menuButtons.add(new InlineButton("Choose regular places count", "client_create_order_choose_regular_places_count"));
                menuButtons.add(new InlineButton("Choose VIP places count", "client_create_order_choose_vip_places_count"));
                menuButtons.add(new InlineButton("Finish", "client_create_order_finish"));
                
                this.deleter.setChatId(userid);
                this.deleter.setMessageId(mainMessageId);
                this.deleter.deleteMessage();
                
                this.sender.setChatId(userid);
                this.sender.setInlineButtons(menuButtons);
                
                Timetable timetable = this.timetableService.getTimetableById(id);
                String text = parseTimetableData(timetable);
                
                this.sender.setChatId(userid);
                this.sender.setText("Choose order parameters \n\n"+text);
                this.sender.sendMessage();
            }catch(Exception e) {
                this.sender.setText("Incorrect input, try again");
                this.sender.setChatId(userid);
                this.sender.sendMessage();
            }
        } else if(this.session.getProperty("client_create_order_choosing_regular_places_count", Boolean.class)) {
            String message = MessageUtils.getMessageContents(update);
            try {
                int number = Integer.parseInt(message);
                if (number <= 0) {
                    this.sender.setText("Incorrect input, try again");
                    this.sender.setChatId(userid);
                    this.sender.sendMessage();
                    return;
                }
                
                int timetableid = this.session.getProperty("client_create_order_trip_id", Integer.class);
                int availableRegularPlacesCount = this.timetableService.getAvailableRegularPlacesCountById(timetableid);
                
                if(availableRegularPlacesCount < number) {
                    this.sender.setText("There are not enough places to book.\nWe have only "+availableRegularPlacesCount+" regular places available");
                    this.sender.setChatId(userid);
                    this.sender.sendMessage();
                    return;
                }
                
                this.session.setProperty("client_create_order_trip_regular_places", number);
                this.session.setProperty("client_create_order_choosing_regular_places_count", false);
                
                this.sender.setText("OK, "+number+" regular places");
                this.sender.setChatId(userid);
                this.sender.sendMessage();
            } catch (Exception e) {
                this.sender.setText("Incorrect input, try again");
                this.sender.setChatId(userid);
                this.sender.sendMessage();
            }
        } else if(this.session.getProperty("client_create_order_choosing_vip_places_count", Boolean.class)) {
            String message = MessageUtils.getMessageContents(update);
            try {
                int number = Integer.parseInt(message);
                if (number <= 0) {
                    this.sender.setText("Incorrect input, try again");
                    this.sender.setChatId(userid);
                    this.sender.sendMessage();
                    return;
                }
                int timetableid = this.session.getProperty("client_create_order_trip_id", Integer.class);
                int availableVipPlacesCount = this.timetableService.getAvailableVipPlacesCountById(timetableid);
                
                if(availableVipPlacesCount < number) {
                    this.sender.setText("There are not enough places to book.\nWe have only "+availableVipPlacesCount+" VIP places available");
                    this.sender.setChatId(userid);
                    this.sender.sendMessage();
                    return;
                }
                this.session.setProperty("client_create_order_trip_vip_places", number);
                this.session.setProperty("client_create_order_choosing_vip_places_count", false);
                
                this.sender.setText("OK, "+number+" VIP places");
                this.sender.setChatId(userid);
                this.sender.sendMessage();
            } catch (Exception e) {
                this.sender.setText("Incorrect input, try again");
                this.sender.setChatId(userid);
                this.sender.sendMessage();
            }
        }
        
       
        
    }

    private String parseTimetableData(Timetable timetable) {
        StringBuilder textBuilder = new StringBuilder();
        Boat boat = timetable.getBoat();
        Captain captain = boat.getCaptain();
        textBuilder
            .append("Id: ")
            .append(timetable.getId())
            .append("\n")
            .append("Boat name: ")
            .append(boat.getName())
            .append("\n")
            .append("Captain: ");
        
            if(captain.getSurname()!=null) {
                textBuilder.append(captain.getSurname())
                .append(" ");
            }
           
            if(captain.getName()!=null) {
                textBuilder.append(captain.getName())
                .append(" ");
            }
            
            textBuilder
            .append("\nDeparts at ")
            .append(timetable.getDepartureDatetime().toString().replace("T", " "))
            .append("\n")
            .append("From ")
            .append(timetable.getDeparturePlace())
            .append(" to ")
            .append(timetable.getDestinationPlace())
            .append("\n")
            .append("Duration: ")
            .append(timetable.getDuration().toMinutes())
            .append(" minutes")
            .append("\n");
        return textBuilder.toString();
    }
    
}
