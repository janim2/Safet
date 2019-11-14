package com.tekdivisal.safet.Model;

public class Children {

    public String child_fname;
    public String child_lname;
    public String child_class;
    public String gender;
    public String child_code;

    public Children(){

    }

    public Children(String child_fname, String child_lname,
                    String child_class, String gender,String child_code) {

        this.child_fname = child_fname;
        this.child_lname = child_lname;
        this.child_class = child_class;
        this.gender = gender;
        this.child_code = child_code;
    }


    public String getChild_fname(){return child_fname; }

    public String getChild_lname(){return child_lname; }

    public String getChild_class(){return child_class; }

    public String getGender(){return gender; }

    public String getChild_code(){return child_code; }



}
