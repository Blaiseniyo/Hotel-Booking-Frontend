/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 *
 * @author Seka
 */
@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String fullnames;
    private String email;
    @OneToOne
    @JoinColumn(name = "room_id")
    private Room room;
    private Date checkIn;
    private Date checkout;

    public Booking() {
    }

    public Booking( String fullnames, String email, Room room, Date checkIn, Date checkout) {
        this.fullnames = fullnames;
        this.email = email;
        this.room = room;
        this.checkIn = checkIn;
        this.checkout = checkout;
    }

    public Date getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Date checkIn) {
        this.checkIn = checkIn;
    }

    public Date getCheckout() {
        return checkout;
    }

    public void setCheckout(Date checkout) {
        this.checkout = checkout;
    }

    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullnames() {
        return fullnames;
    }

    public void setFullnames(String fullnames) {
        this.fullnames = fullnames;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

}
