package com.ddt.knoknok.Model;

public class Sended_Sms_Model {
    String id;
    String smsbody;
    String date;

    public Sended_Sms_Model() {}

    public Sended_Sms_Model(String id, String smsbody, String date) {
        this.id = id;
        this.smsbody = smsbody;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSmsbody() {
        return smsbody;
    }

    public void setSmsbody(String smsbody) {
        this.smsbody = smsbody;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
