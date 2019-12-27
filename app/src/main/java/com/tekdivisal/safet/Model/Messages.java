package com.tekdivisal.safet.Model;

public class Messages {


    public String image;
    public String title;
    public String message;
    public String time;
    public String date;
    public String location;


    public Messages(){

    }

    public Messages(String title, String message,String location, String date,String time) {

        this.image = image;
        this.title = title;
        this.message = message;
        this.time = time;
        this.date = date;
        this.location = location;
    }


    public String getImage(){return image; }
    public String getMessage_title(){return title; }

    public String getMessage_message(){return message; }

    public String getTime(){return time; }

    public String getDate(){return date; }
    public String getLocation(){return location; }


}
