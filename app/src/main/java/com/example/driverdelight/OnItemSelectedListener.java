package com.example.driverdelight;

public interface OnItemSelectedListener {

    //Interface for the communication between PhoneActivity and it's fragments
    void itemSelected(Contact contact);
    Contact getItemSelected();
}
