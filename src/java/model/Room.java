/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author Seka
 */
@Entity
public class Room {
    @Id
    private int id;
    private String name;
    private int price;
    private boolean isBooked;

    public Room() {
    }

    public Room(int id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public boolean isIsBooked() {
        return isBooked;
    }

    public void setIsBooked(boolean isBooked) {
        this.isBooked = isBooked;
    }
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    
}
