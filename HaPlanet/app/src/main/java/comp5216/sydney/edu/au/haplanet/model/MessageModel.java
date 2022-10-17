package comp5216.sydney.edu.au.haplanet.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MessageModel {

    private String title;
    private String uid;
    private String reply;
    private Date date;

    public MessageModel(String title, String uid, String reply, Date date) {
        this.title = title;
        this.uid = uid;
        this.reply = reply;
        this.date = date;
    }

    public MessageModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
