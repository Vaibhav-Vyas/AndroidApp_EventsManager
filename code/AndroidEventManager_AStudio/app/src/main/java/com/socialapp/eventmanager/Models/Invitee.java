package com.socialapp.eventmanager.Models;

import com.orm.SugarRecord;

/**
 * Created by sujith on 5/5/15.
 */
public class Invitee extends SugarRecord<Invitee> {
    public String name;
    public String status;
    public Event event;
}
