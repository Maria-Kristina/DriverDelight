package com.example.driverdelight;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by M-K on 23.8.2017.
 */

public class FragmentList extends ListFragment {
    private List<String> phoneList; //fragmentin oma presidenttilista
    private FragmentDetail fragmentDetail;

    public FragmentList(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_fragment, container, false);

        fragmentDetail = new FragmentDetail();
        phoneList = new ArrayList<>();
        makeList();

        CustomAdapter adapter = new CustomAdapter(
                view.getContext(), phoneList);
        setListAdapter(adapter);

        return view;
    }


    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        /*President itemValue = (President) listView.getItemAtPosition(position);
        String info = itemValue.lastName + ", "
                + itemValue.firstName + " "
                + itemValue.aloitusVuosi + " "
                + itemValue.lopetusVuosi + "\n"
                + itemValue.detail;*/


        FragmentTransaction fragmentTransaction =
                getActivity().getFragmentManager().beginTransaction().replace(R.id.fragmentHolder, fragmentDetail);

        /*Bundle args = new Bundle();
        args.putString("YourKey", info);
        fragmentDetail.setArguments(args);*/

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void makeList(){
        phoneList.add("Kekkonen");
        phoneList.add("Partanen");
        phoneList.add("Koivu");
        phoneList.add("Teppo");
        phoneList.add("Mikko Mallikas");
        phoneList.add("Olavi");
        phoneList.add("Kekkonen");
        phoneList.add("Partanen");
        phoneList.add("Koivu");
        phoneList.add("Teppo");
        phoneList.add("Mikko Mallikas");
        phoneList.add("Olavi");

    }
}
