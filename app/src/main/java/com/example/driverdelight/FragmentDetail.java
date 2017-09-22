package com.example.driverdelight;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by M-K on 23.8.2017.
 */

public class FragmentDetail extends Fragment {

    public FragmentDetail(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);

        //TextView detailView = (TextView)view.findViewById(R.id.detailView);
        /*Bundle bundle = getArguments();
        if (bundle != null) {
            detailView.setText(bundle.getString("YourKey"));
        }*/

        return view;
    }
}
