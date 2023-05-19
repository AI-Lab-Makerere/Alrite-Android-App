package com.ug.air.alrite.Fragments.Patient;

import static com.ug.air.alrite.Fragments.Patient.Assess.SEVERE_SYMPTOMS;
import static com.ug.air.alrite.Fragments.Patient.FTouch.TOUCH;
import static com.ug.air.alrite.Fragments.Patient.HIVCare.CHOICEHC;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ug.air.alrite.R;


public class Temperature extends Fragment {

    View view;
    EditText etDay;
    Button back, next, btnSkip, btnSave;
    String temp, diagnosis;
    public static final String TEMP = "temperature";
    public static final String TDIAGNOSIS = "diagnosis_fever";
    public static final String SHARED_PREFS = "sharedPrefs";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Dialog dialog;
    TextView txtMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_temperature, container, false);

        etDay = view.findViewById(R.id.days);
        next = view.findViewById(R.id.next);
        back = view.findViewById(R.id.back);
        btnSkip = view.findViewById(R.id.skip);

        etDay.requestFocus();

        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        loadData();
        updateViews();


        etDay.addTextChangedListener(textWatcher);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp = etDay.getText().toString();
                if (temp.isEmpty()){
                    Toast.makeText(getActivity(), "Please fill in the field before you continue", Toast.LENGTH_SHORT).show();
                }else{
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
                    fr.replace(R.id.fragment_container, new CoughD());
                }else{
                    if (care.isEmpty()){
                        fr.replace(R.id.fragment_container, new HIVStatus());
                    }else{
                        fr.replace(R.id.fragment_container, new HIVCare());
                    }
                }

                fr.commit();
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container, new FTouch());
                fr.addToBackStack(null);
                fr.commit();
            }
        });

        return view;
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            temp = etDay.getText().toString();
            if (!temp.isEmpty()){
                float dy = Float.parseFloat(temp);
                btnSkip.setEnabled(false);
                if (dy < 33.0){
                    etDay.setError("The minimum temperature is 33.0");
                    next.setEnabled(false);
                }else if (dy > 42.0){
                    etDay.setError("The maximum temperature is 42.0");
                    next.setEnabled(false);
                }else {
                    btnSkip.setEnabled(true);
                    next.setEnabled(true);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void saveData() {
        deleteSharedPreferences();

        String assess = sharedPreferences.getString(SEVERE_SYMPTOMS, "");
        editor.putString(TEMP, temp);
        editor.apply();
        float tp = Float.parseFloat(temp);
        if (tp >= 38.5 && assess.contains("None of these")){
            diagnosis = "Fever";
            editor.putString(TDIAGNOSIS, diagnosis);
            editor.apply();
            showDialog();
        }else if (tp >= 38.5){
            editor.remove(TDIAGNOSIS);
            editor.apply();
            showDialog();
        }else {
            editor.remove(TDIAGNOSIS);
            editor.apply();
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_container, new Oxygen());
            fr.addToBackStack(null);
            fr.commit();
        }

    }

    private void loadData() {
        temp = sharedPreferences.getString(TEMP, "");
    }

    private void updateViews() {
        if (!temp.isEmpty()){
            etDay.setText(temp);
            btnSkip.setEnabled(false);
        }else {
            btnSkip.setEnabled(true);
        }
    }

    private void deleteSharedPreferences() {
        editor.remove(TOUCH);
        editor.remove(TDIAGNOSIS);
        editor.apply();
    }

    private void showDialog() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.assess);
        dialog.setCancelable(true);

        txtMessage = dialog.findViewById(R.id.message);
        btnSave = dialog.findViewById(R.id.ContinueButton);

        btnSave.setText("Continue");

        txtMessage.setText("The Child has a fever");
        txtMessage.setTextColor(Color.parseColor("#FF0000"));
        txtMessage.setTypeface(Typeface.DEFAULT_BOLD);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container, new Oxygen());
                fr.addToBackStack(null);
                fr.commit();
            }
        });

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }
}