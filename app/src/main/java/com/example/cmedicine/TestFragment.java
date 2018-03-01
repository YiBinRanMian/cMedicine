package com.example.cmedicine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.support.v4.app.Fragment;

/**
 * Created by 41850 on 2018/2/28.
 */

public class TestFragment extends Fragment {
    private static final String TAG = "TestFragment";

    private Button entrytestbutton ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.test, container, false);
        entrytestbutton = (Button) v.findViewById(R.id.entrybutton);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        entrytestbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), MedicineActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }
}
