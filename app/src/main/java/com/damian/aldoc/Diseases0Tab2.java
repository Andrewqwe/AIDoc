package com.damian.aldoc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by dawid on 2017-04-04.
 */

public class Diseases0Tab2 extends Fragment {
    private ListView list ;
    private ArrayAdapter<String> adapter ;
    String wizytaString;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab2_diseases0, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        list = (ListView) getActivity().findViewById(R.id.list3);

        wizytaString = "Brak";
        String notes[] = {wizytaString};

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, notes);
        list.setAdapter(adapter);
    }
}
