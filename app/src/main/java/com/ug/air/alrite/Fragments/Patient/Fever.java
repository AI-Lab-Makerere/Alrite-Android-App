package com.ug.air.alrite.Fragments.Patient;

import static com.ug.air.alrite.Fragments.Patient.Assess.SEVERE_SYMPTOMS;
import static com.ug.air.alrite.Fragments.Patient.HIVCare.CHOICEHC;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ug.air.alrite.R;


public class Fever extends Fragment {

    View view;
    Button back, next;
    RadioGroup radioGroup1;
    RadioButton radioButton3, radioButton4;
    String value6 = "none";
    private static final int YES1 = 0;
    private static final int NO1 = 1;
    public static final String CHOICE5 = "choice5";
    public static final String SHARED_PREFS = "sharedPrefs";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fever, container, false);

        next = view.findViewById(R.id.next);
        back = view.findViewById(R.id.back);
        radioGroup1 = view.findViewById(R.id.radioGroup1);
        radioButton3 = view.findViewById(R.id.yes1);
        radioButton4 = view.findViewById(R.id.no1);

        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = radioGroup1.findViewById(checkedId);
                int index = radioGroup1.indexOfChild(radioButton);

                switch (index) {
                    case YES1:
                        value6 = "Yes";
                        break;
                    case NO1:
                        value6 = "No";
                        break;
                    default:
                        break;
                }
            }
        });

        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        loadData();
        updateViews();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (value6.isEmpty()){
                    Toast.makeText(getActivity(), "Please select at least one option", Toast.LENGTH_SHORT).show();
                }else {
                    saveData();
                }
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String assess = sharedPreferences.getString(SEVERE_SYMPTOMS, "");
                String care = sharedPreferences.getString(CHOICEHC, "");

                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                if (!assess.equals("None of these")){
                    fr.replace(R.id.fragment_container, new Assess());
                }else if (care.isEmpty()){
                    fr.replace(R.id.fragment_container, new HIVStatus());
                }else{
                    fr.replace(R.id.fragment_container, new HIVCare());
                }
                fr.commit();
            }
        });

        return  view;
    }

    private void saveData() {

        editor.putString(CHOICE5, value6);
        editor.apply();

        FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
        fr.replace(R.id.fragment_container, new Temperature());
        fr.addToBackStack(null);
        fr.commit();
    }

    private void loadData() {
        value6 = sharedPreferences.getString(CHOICE5, "");
    }

    private void updateViews() {

        if (value6.equals("Yes")){
            radioButton3.setChecked(true);
        }else if (value6.equals("No")){
            radioButton4.setChecked(true);
        }else {
            radioButton3.setChecked(false);
            radioButton4.setChecked(false);
        }

    }
}