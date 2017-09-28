package com.example.driverdelight;

/**
 * Created by M-K on 25.9.2017.
 */

public class Contact implements Comparable<Contact>{
    private String name;
    private String phoneNumber;

    public Contact(String name, String phoneNumber){
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName(){
        return name;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }


    @Override
    public int compareTo(Contact contact) {
        return name.compareTo(contact.name);
    }
}
