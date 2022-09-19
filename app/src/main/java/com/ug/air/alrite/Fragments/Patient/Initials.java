package com.ug.air.alrite.Fragments.Patient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ug.air.alrite.Activities.Dashboard;
import com.ug.air.alrite.Database.DatabaseHelper;
import com.ug.air.alrite.R;
import com.ug.air.alrite.Utils.Credentials;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Initials extends Fragment {

   View view;
   EditText etCin, etPin, etStudy, etCode;
   Button back, next;
   String cin, pin, formattedDate, studyId, code, h_code, counter;
   public static final String CIN = "patient_initials";
   public static final String PIN = "parent_initials";
   public static final String STUDY_ID = "study_id";
   public static final String INITIAL_DATE = "start_date";
   public static final String SHARED_PREFS = "sharedPrefs";
   SharedPreferences sharedPreferences;
   SharedPreferences.Editor editor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_initials, container, false);

        etCin = view.findViewById(R.id.cin);
        etCode = view.findViewById(R.id.code);
        etStudy = view.findViewById(R.id.studyId);
        etPin = view.findViewById(R.id.pin);
        next = view.findViewById(R.id.next);
        back = view.findViewById(R.id.back);

        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        loadData();
        updateViews();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cin = etCin.getText().toString();
                pin = etPin.getText().toString();
                studyId = etStudy.getText().toString();

                if (cin.isEmpty() || pin.isEmpty() || studyId.equals("0") || studyId.isEmpty()){
                    Toast.makeText(getActivity(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                }else {
                    saveData();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Dashboard.class));
            }
        });

        return view;
    }

    private void saveData() {

        editor.putString(CIN, cin);
        editor.putString(PIN, pin);
        editor.putString(STUDY_ID, studyId);
        if (formattedDate.isEmpty()){
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
            String formattedDate = df.format(currentTime);

            editor.putString(INITIAL_DATE, formattedDate);
        }
        editor.apply();

        int count = Integer.valueOf(counter);
        int count2 = Integer.valueOf(studyId);

//        Toast.makeText(getActivity(), count + " " + count2, Toast.LENGTH_SHORT).show();

        if (count == count2){
            count = count+1;
            String ct = String.valueOf(count);
            Credentials credentials = new Credentials();
            credentials.counting(getActivity(), ct);
        }

        FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
        fr.replace(R.id.fragment_container, new Sex());
        fr.addToBackStack(null);
        fr.commit();
    }

    private void loadData() {
        pin = sharedPreferences.getString(PIN, "");
        cin = sharedPreferences.getString(CIN, "");
        studyId = sharedPreferences.getString(STUDY_ID, "");
        formattedDate = sharedPreferences.getString(INITIAL_DATE, "");
    }

    private void updateViews() {
        etPin.setText(pin);
        etCin.setText(cin);

        Credentials credentials = new Credentials();
        credentials.creds2(getActivity());
        code = credentials.creds2(getActivity()).getCode();
        h_code = credentials.creds2(getActivity()).getH_code();
        counter = credentials.creds2(getActivity()).getCounter();
        etCode.setText("AL"+h_code + "" + code);
        etCode.setEnabled(false);

        if (studyId.isEmpty()){
            etStudy.setText(counter);
        }else{
            etStudy.setText(studyId);
        }

    }

}