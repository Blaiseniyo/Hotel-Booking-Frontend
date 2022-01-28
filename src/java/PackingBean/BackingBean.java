/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PackingBean;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.ACCEPTED;
import model.Booking;
import model.Room;
import model.Users;

/**
 *
 * @author Seka
 */
@ManagedBean(name = "BB")
@SessionScoped
public class BackingBean implements Serializable {

//    private static final long serialVersionUID = 5174981910836317636L;
    private int room_Id;
    private Users user;
    private Room room;
    private Booking booking;
    public boolean isLogged = false;
    public boolean update = false;

    public int getRoom_Id() {
        return room_Id;
    }

    public void setRoom_Id(int room_Id) {
        this.room_Id = room_Id;
    }

//    for room
    public List<Room> getAllRoom() {
        return ClientBuilder.newClient()
                .target("http://localhost:8080/HotelBooking/api/room")
                .request().get(new GenericType<List<Room>>() {
                });
    }

    public String createRoom() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        List<Room> rooms = getAllRoom();
        for (Room r : rooms) {
            if (r.getName().equals(room.getName())) {
                facesContext.addMessage("room-frm", new FacesMessage("room already registered"));
            }
        }
        if (room.getName().trim().length() < 5) {
            facesContext.addMessage("room-frm", new FacesMessage("names must be more than five character large"));
        } else if (room.getPrice() <= 0) {
            facesContext.addMessage("room-frm", new FacesMessage("price can not be less or equal to zero"));
        } else {
            ClientBuilder.newClient().target("http://localhost:8080/HotelBooking/api/room")
                    .request().post(Entity.json(room));
        }
        return "Home.xhtml";
    }
//    for booking

    public List<Booking> getAllBookings() {
        return ClientBuilder.newClient()
                .target("http://localhost:8080/HotelBooking/api/booking")
                .request().get(new GenericType<List<Booking>>() {
                });
    }

    public Room getRoom(int id) {
        return ClientBuilder.newClient()
                .target("http://localhost:8080/HotelBooking/api/room/" + id)
                .request().get(new GenericType<Room>() {
                });
    }

    public String handleUpdate(String id) {
        booking = ClientBuilder.newClient()
                .target("http://localhost:8080/HotelBooking/api/booking/" + id)
                .request().get(new GenericType<Booking>() {
                });
        room_Id = booking.getRoom().getId();
        update = true;
        return null;
    }

    public String update(String id) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage("update-frm", new FacesMessage(id));
        return "update.xhtml";
    }

    public String deleteBooking(int id) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        booking = ClientBuilder.newClient()
                .target("http://localhost:8080/HotelBooking/api/booking/" + id)
                .request().delete(Booking.class);
        System.out.println(booking.getFullnames());
        if (booking != null) {
            facesContext.addMessage("tbl", new FacesMessage("delete done"));
            return null;
        } else {
            facesContext.addMessage("data", new FacesMessage("delete failed"));
            return "register.xhtml";
        }
    }

    public String createBooking() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        room.setId(room_Id);
            booking.setRoom(room);
        if (update) {
            if (validate(booking)) {
                booking = new Booking();
                return "booking.xhtml";
            }
            facesContext.addMessage("booking-frm", new FacesMessage("booking updated"));
            ClientBuilder.newClient().target("http://localhost:8080/HotelBooking/api/booking")
                    .request().put(Entity.json(booking));
            update = false;
            return "register.xhtml";
        } else if (!update) {
            if (validate(booking)) {
                System.out.println("yes");
                return null;
            }
            facesContext.addMessage("booking-frm", new FacesMessage("booking saved"));
            ClientBuilder.newClient().target("http://localhost:8080/HotelBooking/api/booking")
                    .request().post(Entity.json(booking));
            return "register.xhtml";
        }
        return "register.xhtml";
    }

    public boolean validate(Booking booking) {
        boolean status = false;
        LocalDate now = LocalDate.now();

        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (booking.getFullnames().trim().length() < 5) {
            facesContext.addMessage("booking-frm", new FacesMessage("names must be more than five character large"));
            status = true;
        } else if (!isValid(booking.getEmail())) {
            facesContext.addMessage("booking-frm", new FacesMessage("your email is not valid"));
            status = true;
        } else if (booking.getCheckIn() == null) {
            facesContext.addMessage("booking-frm", new FacesMessage("chekin date can not be null"));
            status = true;
        } else if (booking.getCheckIn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(now)) {
            facesContext.addMessage("booking-frm", new FacesMessage("chekin date can not be in the past"));
            status = true;
        } else if (booking.getCheckout() == null) {
            facesContext.addMessage("booking-frm", new FacesMessage("chekout date can not be null"));
            status = true;
        } else if (booking.getCheckout().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(booking.getCheckIn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) {
            facesContext.addMessage("booking-frm", new FacesMessage("chekout date can not came before checkin date"));
            status = true;
        } else {
            if (!update) {
                status = availability(booking);
            } else {
                status = updateCheck(booking);
            }
        }
        return status;
    }

    public boolean updateCheck(Booking booking) {
        boolean status = false;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        List<Booking> bookings = getAllBookings();
        for (Booking b : bookings) {
            if (booking.getId() == b.getId()) {
                if (booking.getRoom().getId() == b.getRoom().getId()) {
                    if (booking.getCheckIn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(b.getCheckIn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) {
                        if (booking.getCheckout().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(b.getCheckout().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) {
                            status = false;
                        } else {
                            status = availability(booking);
                        }
                    } else {
                        status = availability(booking);
                    }
                } else {
                    status = availability(booking);
                }
            }
        }
        return status;
    }

    public boolean availability(Booking booking) {
        boolean status = false;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        room = getRoom(booking.getRoom().getId());
        //if room is  booked
        if (room.isIsBooked()) {
            List<Booking> bookings = getAllBookings();
            for (Booking b : bookings) {
                if (booking.getRoom().getId() == b.getRoom().getId()) {
                    //checks in all bookings the similar id
                    System.out.println(booking.getId());
                    System.out.println(b.getId());
                    // if entyered date is the same with a saved date
                    if (booking.getCheckIn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(b.getCheckout().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()) || booking.getCheckIn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(b.getCheckIn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()) || booking.getCheckout().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(b.getCheckout().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()) || booking.getCheckIn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isEqual(b.getCheckIn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) {
                        facesContext.addMessage("booking-frm", new FacesMessage("room already registered on that date"));
                        System.out.println(b.getCheckIn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                        status = true;
                    }
                    //if entered date is after saved date and 
                    if (booking.getCheckIn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(b.getCheckIn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) {
                        //if
                        if (booking.getCheckIn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(b.getCheckout().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) {
                            facesContext.addMessage("booking-frm", new FacesMessage("room already registered on that date"));
                            System.out.println(b.getCheckIn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                            status = true;
                        }
                    }
                    if (booking.getCheckIn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(b.getCheckIn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) {
                        if (booking.getCheckout().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(b.getCheckIn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) {
                            facesContext.addMessage("booking-frm", new FacesMessage("room already registered on that date"));
                            System.out.println(b.getCheckIn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                            status = true;
                        }
                    }
                }
            }
        }

        return status;
    }

    //    for auth
    public String aunthenticate() {
        Users result = ClientBuilder.newClient()
                .target("http://localhost:8080/HotelBooking/api/auth")
                .request().post(Entity.json(user), Users.class);
        if (result != null) {
            isLogged = true;
            return "Home.xhtml";
        } else {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.addMessage("auth-frm", new FacesMessage("Auth failed"));
            return null;
        }
    }

    public boolean isValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public BackingBean() {
        user = new Users();
        room = new Room();
        booking = new Booking();
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

}
