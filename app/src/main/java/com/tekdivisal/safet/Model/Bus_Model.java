package com.tekdivisal.safet.Model;

public class Bus_Model {

    public String bus_id;
    public String bus_route;


    public Bus_Model(){

    }

    public Bus_Model(String bus_id, String bus_route) {

        this.bus_id = bus_id;
        this.bus_route = bus_route;
    }


    public String getBus_id(){return bus_id; }

    public String getBus_route(){return bus_route; }

}
