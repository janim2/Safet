package com.tekdivisal.safet.Model;

public class School {

    public String school_code;
    public String school_name;
    public String school_email;
    public String school_location;
    public String school_phone;


    public School(){

    }

    public School(String code, String name, String email, String location, String phone) {

        this.school_code = code;
        this.school_name = name;
        this.school_email = email;
        this.school_location = location;
        this.school_phone = phone;
    }


    public String getSchool_code(){return school_code; }

    public String getSchool_name(){return school_name; }

    public String getSchool_email(){return school_email; }

    public String getSchool_location(){return school_location; }

    public String getSchool_phone(){return school_phone; }


}
