package com.ug.air.alrite.Fragments.Patient;

import static com.ug.air.alrite.Fragments.Patient.WheezY.DAY2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.ug.air.alrite.R;


public class Breathless extends Fragment {

   View view;
    Button back, next;
    CheckBox drink, vomit, resp, none;
    String s = "";
    Boolean check1, check2, check3, check4;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String CHECK11 = "check11";
    public static final String CHECK21 = "check21";
    public static final String CHECK31 = "check31";
    public static final String CHECK41 = "check41";
    public static final String S5 = "child_breathless";
    SharedPreferences sharedPreferences, sharedPreferences1;
    SharedPreferences.Editor editor, editor1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_breathless, container, false);

        back = view.findViewById(R.id.back);
        next = view.findViewById(R.id.next);
        drink = view.findViewById(R.id.drink);
        vomit = view.findViewById(R.id.vomit);
        none = view.findViewById(R.id.none);
        resp = view.findViewById(R.id.responsive);

        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        loadData();
        updateViews();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedList();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String day2 = sharedPreferences.getString(DAY2, "");
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                if (day2.isEmpty()){
                    fr.replace(R.id.fragment_container, new WheezD());
                }else {
                    fr.replace(R.id.fragment_container, new WheezY());
                }
                fr.commit();
            }
        });

        vomit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (vomit.isChecked() ){
                    none.setChecked(false);
                    none.setSelected(false);
                    none.setEnabled(false);
                }
                else if (!resp.isChecked() && !drink.isChecked() && !vomit.isChecked()){
                    none.setEnabled(true);
                }
            }
        });

        drink.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (drink.isChecked() ){
                    none.setChecked(false);
                    none.setSelected(false);
                    none.setEnabled(false);
                }
                else if (!resp.isChecked() && !drink.isChecked() && !vomit.isChecked()){
                    none.setEnabled(true);
                }
            }
        });

        resp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (resp.isChecked() ){
                    none.setChecked(false);
                    none.setSelected(false);
                    none.setEnabled(false);
                }
                else if (!resp.isChecked() && !drink.isChecked() && !vomit.isChecked()){
                    none.setEnabled(true);
                }
            }
        });

        none.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (none.isChecked()){
                    checked(vomit);
                    checked(drink);
                    checked(resp);
                }else {
                    vomit.setEnabled(true);
                    drink.setEnabled(true);
                    resp.setEnabled(true);
                }
            }
        });

        return view;
    }

    private void checked(CheckBox checkBox){
        checkBox.setChecked(false);
        checkBox.setSelected(false);
        checkBox.setEnabled(false);
    }

    private void checkedList() {
        s = "";

        if(drink.isChecked()){
            s += "At night, ";
        }
        if(vomit.isChecked()){
            s += "When playing, so they have to take a break, ";
        }
        if(resp.isChecked()){
            s += "When laughing/crying or exposed to smoke or strong smells, ";
        }
        if(none.isChecked()){
            s = "None of these, ";
        }
        s = s.replaceAll(", $", "");
        if (s.isEmpty()){
            Toast.makeText(getActivity(), "Choose at least one option", Toast.LENGTH_SHORT).show();
        }else {
            saveData(s);
        }

    }

    private void saveData(String s) {

        editor.putBoolean(CHECK11, drink.isChecked());
        editor.putBoolean(CHECK21, vomit.isChecked());
        editor.putBoolean(CHECK31, resp.isChecked());
        editor.putBoolean(CHECK41, none.isChecked());
        editor.putString(S5, s);

        editor.apply();

        FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
        fr.replace(R.id.fragment_container, new Eczema());
        fr.addToBackStack(null);
        fr.commit();
    }

    private void loadData() {
        check1 = sharedPreferences.getBoolean(CHECK11, false);
        check2 = sharedPreferences.getBoolean(CHECK21, false);
        check3 = sharedPreferences.getBoolean(CHECK31, false);
        check4 = sharedPreferences.getBoolean(CHECK41, false);
    }

    private void updateViews() {
        drink.setChecked(check1);
        vomit.setChecked(check2);
        resp.setChecked(check3);
        none.setChecked(check4);
    }

}