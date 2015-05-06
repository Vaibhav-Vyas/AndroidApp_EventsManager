package com.socialapp.eventmanager.Models;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by sujith on 29/3/15.
 */
public class Event extends SugarRecord<Event>{
    public String event_id;

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

    public String event_type;

    public String status;

    //No-args constructor required by Sugar
    public Event() {
        created_at = new Date();
        updated_at = new Date();
    }

    public List<Invitee> getInvitees()
    {
        return Invitee.find(Invitee.class, "event = ?", String.valueOf(this.getId()));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\t id: " + event_id);
        sb.append("\n\t createdAt: " + created_at.toString());
        sb.append("\n\t updatedAt: " + updated_at.toString());
        sb.append("\n\t name: " + name);
        return sb.toString();
    }
}
