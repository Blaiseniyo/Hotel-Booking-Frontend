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
public class Users {
    @Id
    private String email;
    private String fullnames;
    private String password;

    public Users() {
    }

    public Users(String email, String fullnames, String password) {
        this.email = email;
        this.fullnames = fullnames;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullnames() {
        return fullnames;
    }

    public void setFullnames(String fullnames) {
        this.fullnames = fullnames;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
