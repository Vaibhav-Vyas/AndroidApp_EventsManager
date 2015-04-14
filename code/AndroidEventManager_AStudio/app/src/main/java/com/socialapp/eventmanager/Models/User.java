package com.socialapp.eventmanager.Models;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sujith on 29/3/15.
 */
public class User extends SugarRecord<User>{
    public String name;
    public int login_status;

    //No-args constructor required by Sugar
    public User() {
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\t name: " + name);
        return sb.toString();
    }
}
