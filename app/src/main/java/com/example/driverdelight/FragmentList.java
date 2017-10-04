package com.example.driverdelight;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.SensorEvent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentList extends ListFragment {
    private FragmentDetail fragmentDetail;
    private Activity activity;
    private CustomAdapter adapter;
    private View view;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.list_fragment, container, false);
        fragmentDetail = new FragmentDetail();

        LoadContactsTask loadContactsTask = new LoadContactsTask();
        loadContactsTask.execute();

        return view;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        //sends the contact from the list to mainActivity
        try {
            ((OnItemSelectedListener) activity).itemSelected(adapter.getItem(position));
        } catch (ClassCastException e) {
            Log.d("EXCEPTION", "ONLISTITEMSELECTED");
        }

        FragmentTransaction fragmentTransaction =
                getActivity()
                        .getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentHolder, fragmentDetail);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    private class LoadContactsTask extends AsyncTask<Void, Void, List<Contact>> {

        @Override
        protected List<Contact> doInBackground(Void... voids) {
            return fetchContacts();
        }

        @Override
        protected void onPostExecute(List<Contact> contacts) {
            Collections.sort(contacts);
            adapter = new CustomAdapter(
                    view.getContext(), contacts);
            setListAdapter(adapter);
        }

        private List<Contact> fetchContacts() {
            List<Contact> list = new ArrayList<>();
            int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && activity.checkSelfPermission(Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{Manifest.permission.READ_CONTACTS},
                        PERMISSIONS_REQUEST_READ_CONTACTS);

            } else {

                String phoneNumber;

                Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
                String _ID = ContactsContract.Contacts._ID;
                String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
                String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

                Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
                String NUMBER = ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER;

                ContentResolver contentResolver = getActivity().getContentResolver();
                Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

                // Loop for every contact in the phone
                if (cursor.getCount() > 0) {
                    List<String> phoneNumbers = new ArrayList<>();

                    while (cursor.moveToNext()) {
                        String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                        String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                        int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                        if (hasPhoneNumber > 0) {

                            // Query and loop for every phone number of the contact
                            Cursor phoneCursor = contentResolver.query(
                                    PhoneCONTENT_URI, null,
                                    Phone_CONTACT_ID + " = ?",
                                    new String[]{contact_id}, null);

                            while (phoneCursor.moveToNext()) {
                                phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));

                                if (phoneNumbers.contains(phoneNumber) == false) {
                                    phoneNumbers.add(phoneNumber);
                                    list.add(new Contact(name, phoneNumber, contact_id));
                                }

                            }
                            phoneCursor.close();
                        }
                    }
                }
                cursor.close();
            }
            return list;
        }
    }
}
