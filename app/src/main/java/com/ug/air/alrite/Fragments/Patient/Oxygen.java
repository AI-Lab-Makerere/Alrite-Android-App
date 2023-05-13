package com.ug.air.alrite.Fragments.Patient;

import static com.ug.air.alrite.Fragments.Patient.Assess.FINAL_DIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.Assess.SEVERE_SYMPTOMS;
import static com.ug.air.alrite.Fragments.Patient.FTouch.TOUCH;
import static com.ug.air.alrite.Fragments.Patient.Sex.AGE_IN_MONTHS;
import static com.ug.air.alrite.Fragments.Patient.Sex.KILO;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ug.air.alrite.Activities.DiagnosisActivity;
import com.ug.air.alrite.Adapters.AssessmentAdapter;
import com.ug.air.alrite.Models.Assessment;
import com.ug.air.alrite.R;
import com.ug.air.alrite.Utils.Calculations.Instructions;

import java.util.ArrayList;
import java.util.List;


public class Oxygen extends Fragment {

    View view;
    EditText etDay;
    Button back, next, btnSave, btSkip,btnContinue,btnContinue2;
    String oxy;
    public static final String OXY = "blood_oxygen_saturation";
    public static final String OXDIAGNOSIS = "oxDiagnosis";
    public static final String SHARED_PREFS = "sharedPrefs";
    SharedPreferences sharedPreferences, sharedPreferences1;
    SharedPreferences.Editor editor, editor1;
    long percent = 0;
    Dialog dialog, dialog2;
    RecyclerView recyclerView;
    LinearLayout linearLayout_instruction;
    TextView txtDiagnosis, txtOxygen;
    ArrayList<Assessment> assessments;
    AssessmentAdapter assessmentAdapter;
    String diagnosis;
    List messages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_oxygen, container, false);

        etDay = view.findViewById(R.id.days);
        next = view.findViewById(R.id.next);
        back = view.findViewById(R.id.back);
        btSkip = view.findViewById(R.id.skip);

        etDay.requestFocus();

        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        loadData();
        updateViews();

        etDay.addTextChangedListener(textWatcher);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oxy = etDay.getText().toString();
                if (oxy.isEmpty()){
                    Toast.makeText(getActivity(), "Please fill in the field before you continue", Toast.LENGTH_SHORT).show();
                }else{
                    saveData();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String touch = sharedPreferences.getString(TOUCH, "");
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                if (touch.isEmpty()){
                    fr.replace(R.id.fragment_container, new Temperature());
                }else {
                    fr.replace(R.id.fragment_container, new FTouch());
                }
                fr.commit();
            }
        });

        btSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.remove(OXDIAGNOSIS);
                editor.remove(OXY);
                editor.apply();
                nextInterface();
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
            oxy = etDay.getText().toString();
            if (!oxy.isEmpty()){
                long dy = Long.parseLong(oxy);
                if (dy < 50){
                    etDay.setError("The minimum accepted value is 50");
                    next.setEnabled(false);
                }else if (dy > 100){
                    etDay.setError("The maximum accepted value is 100");
                    next.setEnabled(false);
                }else {
                    next.setEnabled(true);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void saveData() {
        diagnosis = "";
        editor.putString(OXY, oxy);
        editor.apply();

        percent = Integer.parseInt(oxy);
        if (percent >= 92){
            editor.remove(OXDIAGNOSIS);
            editor.remove(FINAL_DIAGNOSIS);
            editor.apply();
            nextInterface();
        }else if (percent >= 90 && percent < 92){
            editor.remove(OXDIAGNOSIS);
            editor.remove(FINAL_DIAGNOSIS);
            editor.apply();
            showDialog2();
        }else if (percent >= 85 && percent < 90){
            showDialog3("The oxgyen saturation is below normal. Are you sure you entered the right value?");
        }else {
            editor.remove(OXDIAGNOSIS);
            editor.remove(FINAL_DIAGNOSIS);
            editor.apply();
            showDialog3("The child's oxygen levels are abnormally low, Are you sure you entered the right value?");
        }
    }

    private void showDialog3(String msg) {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.assess);
        dialog.setCancelable(true);

        TextView txtMessage = dialog.findViewById(R.id.message);
        Button btnSave = dialog.findViewById(R.id.ContinueButton);
        Button btnNo = dialog.findViewById(R.id.cancel);

        btnSave.setText("Yes");
        btnNo.setVisibility(View.VISIBLE);

        txtMessage.setText(msg);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showDialog();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                etDay.setText("");
                etDay.requestFocus();
                Toast.makeText(getActivity(), "Please check your pulse oximeter and enter the value again", Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }

    private void loadData() {
        oxy = sharedPreferences.getString(OXY, "");
    }

    private void updateViews() {
        etDay.setText(oxy);
    }

    private void showDialog() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.assessment_layout);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        linearLayout_instruction = dialog.findViewById(R.id.diagnosis);
        txtDiagnosis = dialog.findViewById(R.id.txtDiagnosis);
        recyclerView = dialog.findViewById(R.id.recyclerView1);
        btnSave = dialog.findViewById(R.id.btnSave);
        btnContinue = dialog.findViewById(R.id.btnContinue);

        linearLayout_instruction.setBackgroundColor(getResources().getColor(R.color.severeDiagnosisColor));
        txtDiagnosis.setText(R.string.severe);
        diagnosis = txtDiagnosis.getText().toString();
        diagnosis = diagnosis.replace("Diagnosis: ", "");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        assessments = new ArrayList<>();
        assessmentAdapter = new AssessmentAdapter(assessments);

        String age = sharedPreferences.getString(AGE_IN_MONTHS, "");
        String s = sharedPreferences.getString(SEVERE_SYMPTOMS, "");
        String weight = sharedPreferences.getString(KILO, "");
        int ag = Integer.parseInt(age);

        Instructions instructions = new Instructions();
        messages = instructions.GetInstructions(ag, weight, s);

        for (int i = 0; i < messages.size(); i++){
            Assessment assessment = new Assessment((Integer) messages.get(i));
            assessments.add(assessment);
        }
        recyclerView.setAdapter(assessmentAdapter);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalDiagnosis();
                editor.putString(OXDIAGNOSIS, diagnosis);
                editor.apply();
                dialog.dismiss();
                startActivity(new Intent(getActivity(), DiagnosisActivity.class));
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalDiagnosis();
                editor.putString(OXDIAGNOSIS, diagnosis);
                editor.apply();
                dialog.dismiss();
                nextInterface();
            }
        });

//        dialog.getWindow().setLayout(650, 800);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }

    private void finalDiagnosis() {
        editor.putString(FINAL_DIAGNOSIS, "Severe Pneumonia OR very Severe Disease");
        editor.apply();
    }

    private void showDialog2() {
        dialog2 = new Dialog(getActivity());
        dialog2.setContentView(R.layout.oxygen);
        dialog2.setCancelable(true);

        txtOxygen = dialog2.findViewById(R.id.oxygen);
        btnContinue2 = dialog2.findViewById(R.id.ContinueButton);

        txtOxygen.setText(oxy + "%");

        btnContinue2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.remove(OXDIAGNOSIS);
                editor.remove(FINAL_DIAGNOSIS);
                editor.apply();
                dialog2.dismiss();
                nextInterface();
            }
        });

        dialog2.show();

    }

    private void nextInterface(){
//        String assess = sharedPreferences.getString(S4, "");
//
        FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
//        if (!assess.equals("None of these")){
//            fr.replace(R.id.fragment_container, new Wheezing());
//        }else {
//            fr.replace(R.id.fragment_container, new RRCounter());
//        }
        fr.replace(R.id.fragment_container, new RRCounter());
        fr.addToBackStack(null);
        fr.commit();
    }


}