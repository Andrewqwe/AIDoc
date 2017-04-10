package com.damian.aldoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by dawid on 2017-04-04.
 */

public class Diseases0Tab1 extends Fragment {

    private ListView list ;
    private ArrayAdapter<String> adapter ;
    EditText disease1;
    String disease1String;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.tab1_diseases0, container, false);
        Button button = (Button) view.findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity().getApplicationContext(), Diseases1Activity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        list = (ListView) getActivity().findViewById(R.id.list2);

        disease1String = "Brak";
        String notes[] = {disease1String};

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, notes);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                Intent intent = new Intent(getActivity().getApplicationContext(), Diseases2Activity.class);

                intent.putExtra("date", "Brak");
                intent.putExtra("mood", "Brak");
                intent.putExtra("symptoms", "Brak");
                intent.putExtra("medicines", "Brak");
                intent.putExtra("reaction", "Brak");
                startActivity(intent);
            }
        });
    }
}
