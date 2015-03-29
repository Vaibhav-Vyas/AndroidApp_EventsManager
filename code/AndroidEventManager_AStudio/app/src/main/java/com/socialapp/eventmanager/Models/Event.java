package com.socialapp.eventmanager.Models;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by sujith on 29/3/15.
 */
public class Event extends SugarRecord<Event>{
    public String eventId;

    public Date created_at;
    public Date updated_at;

    public String name;
    public String location;
    public String description;

    /// Date and Time

    public long start_time;
    public long end_time;


    public String image_url;

    public boolean public_event;

    public String owner;

    public boolean invitation_allowed;

    public String organization;

    public ArrayList<String> event_type;


    //No-args constructor required by Sugar
    public Event() {
        created_at = new Date();
        updated_at = new Date();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\t id: " + eventId);
        sb.append("\n\t createdAt: " + created_at.toString());
        sb.append("\n\t updatedAt: " + updated_at.toString());
        sb.append("\n\t name: " + name);
        return sb.toString();
    }
}
