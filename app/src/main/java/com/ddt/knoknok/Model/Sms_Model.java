package com.ddt.knoknok.Model;

public class Sms_Model {
    String id;
    String smsfrom;
    String smsbody;
    String date;

    public Sms_Model() {}

    public Sms_Model(String id,String smsfrom, String smsbody, String date) {
        this.id = id;
        this.smsfrom = smsfrom;
        this.smsbody = smsbody;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSmsfrom() {
        return smsfrom;
    }

    public void setSmsfrom(String smsfrom) {
        this.smsfrom = smsfrom;
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
