package com.example.driverdelight;

public class Contact implements Comparable<Contact>{
    private String name;
    private String phoneNumber;
    private String id;

    public Contact(String name, String phoneNumber, String id){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.id =id;
    }

    // Returns value "name"
    public String getName(){
        return name;
    }

    // Returns value "phoneNumber"
    public String getPhoneNumber(){
        return phoneNumber;
    }

    // Helps to arrange the contact list into alphabetical order
    @Override
    public int compareTo(Contact contact) {
        return name.compareTo(contact.name);
    }
}
