package com.example.driverdelight;

/**
 * Created by M-K on 25.9.2017.
 */

public class Contact implements Comparable<Contact>{
    private String name;
    private String phoneNumber;
    private String id;

    public Contact(String name, String phoneNumber, String id){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.id =id;
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
