package com.media30.zglosproblem.mobileapp;

import java.util.ArrayList;
import java.util.List;

public class Phone {
    private String Description;
    private String Phone;

    Phone(String description, String phone){
        this.Description = description;
        this.Phone = phone;
    }

    public String getDescription() {
        return Description;
    }

    public String getPhone() {
        return Phone;
    }

    static public List<Phone> getPhones(){
        ArrayList<Phone> phones = new ArrayList<Phone>();
        phones.add(new Phone("Urząd Gminy i Miasta", "324295911"));
        phones.add(new Phone("Zakład Gospodarki Mieszkaniowej", "324311440"));
        phones.add(new Phone("Zarząd Dróg i Służby Komunalne", "324277543"));
        phones.add(new Phone("Straż Miejska", "324311928"));
        phones.add(new Phone("Komisariat Policji", "324391710"));
        return phones;
    }
}
