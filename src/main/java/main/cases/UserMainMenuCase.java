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
import main.dto.Captain;
import main.dto.Order;
import main.dto.Seat;
import main.dto.Timetable;
import main.service.OrderService;
import main.service.TimetableService;
import main.util.CallbackUtils;

@Case(caseNumber=5)
@Component
public class UserMainMenuCase {

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
    
    @Callback(command="client_create_order")
    public void createOrder(Update update) {
        int userid = CallbackUtils.getUserIdFromCallback(update);
        int messageid = CallbackUtils.getMessageIdFromCallback(update);
        this.editor.setChatId(userid);
        this.editor.setText("Please choose trip parameters");
        
        List<InlineButton> menuButtons = new ArrayList<>();
        menuButtons.add(new InlineButton("Choose trip", "client_create_order_choose_trip"));
        this.editor.setInlineButtons(menuButtons);
        this.editor.setMessageId(messageid);
        this.editor.editMessage();
        this.router.routeCallbackToClass(userid, UserCreateOrderCase.class);
    }
    
    @Callback(command="client_view_orders")
    public void viewOrders(Update update) {
        int userid = update.getCallbackQuery().getFrom().getId();
        
        List<Order> userOrders = this.orderService.getAllByUserId(userid);
        
        
        for(Order order : userOrders) {
            
            String text = parseOrderData(order);
            
            
            this.sender.setText(textBuilder.toString());
            this.sender.setChatId(userid);
            this.sender.sendMessage();
        }
        
        if(userOrders.isEmpty()) {
            this.sender.setText("No orders yet");
            this.sender.setChatId(userid);
            this.sender.sendMessage();
        }
        
        int messageId = CallbackUtils.getMessageIdFromCallback(update);
        
        this.deleter.setMessageId(messageId);
        this.deleter.setChatId(userid);
        this.deleter.deleteMessage();
        
        this.sender.setChatId(userid);
        List<InlineButton> menuButtons = new ArrayList<>();
        menuButtons.add(new InlineButton("Create order", "client_create_order"));
        menuButtons.add(new InlineButton("View my orders", "client_view_orders"));
        this.sender.setInlineButtons(menuButtons);
        this.sender.setText("Main menu");
        this.sender.sendMessage();
        
    }

    private String parseOrderData(Order order) {
        StringBuilder textBuilder = new StringBuilder();
        Timetable timetable = order.getSeats().get(0).getTimetable();
        Boat boat = timetable.getBoat();
        Captain captain = boat.getCaptain();
        int vipSeatsCount = (int) order.getSeats().stream().filter(Seat::isSeatVip).count();
        
        textBuilder
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
            .append("\n")
            .append("\t> ")
            .append(vipSeatsCount)
            .append(" VIP seats\n")
            .append("\t> ")
            .append(order.getSeats().size()-vipSeatsCount)
            .append(" regular seats\n")
            .append("Order time: ")
            .append(order.getTimestamp().toString().replace("T", " "));
            
        return textBuilder.toString();
    }
    
}
