package com.example.driverdelight;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by User1 on 22.9.2017.
 */

public class PhoneActivity extends Activity implements OnItemSelectedListener{
    Contact mContact;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);
        if (savedInstanceState == null){
            getFragmentManager().beginTransaction().add(R.id.fragmentHolder, new FragmentList()).commit();
        }
    }

    @Override
    public void itemSelected(Contact contact) {
        this.mContact = contact;
        Log.d("ONITEMSELECTED", contact.getName());

    }

    @Override
    public Contact getItemSelected() {
        return mContact;
    }
}
