package com.tekdivisal.safet.Model;

public class Notify {


    public String title;
    public String message;
    public String time;
    public String imagetype;

    public Notify(){

    }

    public Notify(String title, String message, String time, String imagetype) {

        this.title = title;
        this.message = message;
        this.time = time;
        this.imagetype = imagetype;
    }


    public String getTitle(){return title; }

    public String getMessage(){return message; }

    public String getTime(){return time; }

    public String getImageType(){return imagetype; }


}
