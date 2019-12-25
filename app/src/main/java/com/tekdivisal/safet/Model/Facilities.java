package com.tekdivisal.safet.Model;

public class Facilities {


    public String id;
    public String the_facility_name;
    public String imageUrl;

    public Facilities(){

    }

    public Facilities(String id, String name, String imageUrl) {

        this.id = id;
        the_facility_name = name;
        this.imageUrl = imageUrl;
    }

    public String getId(){return  id;}

    public String getFacility_name(){return the_facility_name; }

    public String getImageUrl(){return imageUrl; }

}
