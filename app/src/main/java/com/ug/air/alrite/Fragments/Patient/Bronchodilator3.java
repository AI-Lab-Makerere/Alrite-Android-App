package com.ug.air.alrite.Fragments.Patient;

import static com.ug.air.alrite.Fragments.Patient.ChestIndrawing.POINT;
import static com.ug.air.alrite.Fragments.Patient.ChestIndrawing.POINT2;
import static com.ug.air.alrite.Fragments.Patient.Cough.DIFFICULTY_BREATHING;
import static com.ug.air.alrite.Fragments.Patient.CoughD.DAY1;
import static com.ug.air.alrite.Fragments.Patient.Sex.AGE_IN_MONTHS;
import static com.ug.air.alrite.Fragments.Patient.Wheezing.CHOICE82;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ug.air.alrite.Activities.DiagnosisActivity;
import com.ug.air.alrite.Adapters.AssessmentAdapter;
import com.ug.air.alrite.Models.Assessment;
import com.ug.air.alrite.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Bronchodilator3 extends Fragment {

    View view;
    Button back, next, btnSave, btnContinue;
    RadioGroup radioGroup;
    CheckBox checkBox;
    RadioButton radioButton1, radioButton2, radioButton3;
    String value9 = "none";
    TextView txtDisease, txtDefinition, txtOk, txtDiagnosis;
    LinearLayout linearLayoutDisease;
    LinearLayout linearLayout_instruction;
    VideoView videoView;
    Dialog dialog;
    int val;
    long day;
    String diagnosis, s, assess, second;
    RecyclerView recyclerView;
    ArrayList<Assessment> assessments;
    AssessmentAdapter assessmentAdapter;
    private static final int YES = 0;
    private static final int NO = 1;
    private static final int NOT = 2;
    public static final String AFTER_BRONCHODILATOR = "after_bronchodilator";
    public static final String FINAL = "final";
    public static final String B3DIAGNOSIS = "diagnosis_9";
    public static final String SHARED_PREFS = "sharedPrefs";
    SharedPreferences sharedPreferences, sharedPreferences1;
    SharedPreferences.Editor editor, editor1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bronchodilator_3, container, false);

        next = view.findViewById(R.id.next);
        back = view.findViewById(R.id.back);
        radioGroup = view.findViewById(R.id.radioGroup);
        radioButton1 = view.findViewById(R.id.better);
        radioButton2 = view.findViewById(R.id.no_change);
        radioButton3 = view.findViewById(R.id.worse);

        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = radioGroup.findViewById(checkedId);
                int index = radioGroup.indexOfChild(radioButton);

                switch (index) {
                    case YES:
                        value9 = "Better";
                        break;
                    case NO:
                        value9 = "Same/ No change";
                        break;
                    case NOT:
                        value9 = "Worse";
                        break;
                    default:
                        break;
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (value9.isEmpty()){
                    Toast.makeText(getActivity(), "Please select at least one of the options", Toast.LENGTH_SHORT).show();
                }else {
                    saveData();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container, new ChestIndrawing());
                fr.commit();

            }
        });

        return view;
    }

    private void saveData() {
        editor.putString(AFTER_BRONCHODILATOR, value9);
        editor.apply();

        String cough = sharedPreferences.getString(DIFFICULTY_BREATHING, "");
        String wheezing = sharedPreferences.getString(CHOICE82, "");
        String days = sharedPreferences.getString(DAY1, "");
        String point1 = sharedPreferences.getString(POINT, "");
        String point2 = sharedPreferences.getString(POINT2, "");
        day = Long.parseLong(days);
//        respiratory score decreasing

        if (!point1.isEmpty() && !point2.isEmpty()){
            int p1 = Integer.parseInt(point1);
            int p2 = Integer.parseInt(point2);

            if (value9.equals("Worse")){
                val = 1;
                editor.remove(B3DIAGNOSIS);
            }else if (cough.equals("Yes") && ((p2 < p1) || value9.equals("Better"))){
                val = 2;
                editor.putString(B3DIAGNOSIS, "Wheezing illness (Bronchodilator response)");
            }else if (cough.equals("Yes") && wheezing.equals("Wheezing") && (p2 >= p1 || value9.equals("Same/ No change"))){
                val = 3;
                editor.putString(B3DIAGNOSIS, "Wheezing (not clear Bronchodilator response)");
            }else {
                editor.remove(B3DIAGNOSIS);
            }
        }else {
            if (value9.equals("Worse")){
                val = 1;
                editor.remove(B3DIAGNOSIS);
            }else if (cough.equals("Yes") && value9.equals("Better")){
                val = 2;
                editor.putString(B3DIAGNOSIS, String.valueOf(R.string.wheez1));
            }else if (cough.equals("Yes") && wheezing.equals("Wheezing") && value9.equals("Same/ No change")){
                val = 3;
                editor.putString(B3DIAGNOSIS, String.valueOf(R.string.wheez_ill));
            }else {
                editor.remove(B3DIAGNOSIS);

            }
        }
        editor.apply();

        if (day >= 10){
            showDialog2();
        }else {
            finalValue();
            startActivity(new Intent(getActivity(), DiagnosisActivity.class));
        }

    }

    private void showDialog(int value) {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.assessment_layout);
        dialog.setCancelable(true);
//        Window window = dialog.getWindow();
//        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        linearLayout_instruction = dialog.findViewById(R.id.diagnosis);
        txtDiagnosis = dialog.findViewById(R.id.txtDiagnosis);
        recyclerView = dialog.findViewById(R.id.recyclerView1);
        btnSave = dialog.findViewById(R.id.btnSave);
        btnContinue = dialog.findViewById(R.id.btnContinue);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        assessments = new ArrayList<>();
        assessmentAdapter = new AssessmentAdapter(assessments);

        List<Integer> messages = new ArrayList<>();

        if (value == 1){
            linearLayout_instruction.setBackgroundColor(getResources().getColor(R.color.severeDiagnosisColor));
            txtDiagnosis.setText(R.string.bronc1);
            messages = Collections.singletonList(R.string.bronc1x);
        }else if (value == 2){
            linearLayout_instruction.setBackgroundColor(getResources().getColor(R.color.mildDiagnosisColor));
            txtDiagnosis.setText(R.string.wheez1);
            messages = Arrays.asList(R.string.wheez_ill00, R.string.wheez_ill01, R.string.wheez_ill71,
                    R.string.wheez_ill72, R.string.wheez_ill8, R.string.wheez_ill9);
        }else if (value == 3){
            linearLayout_instruction.setBackgroundColor(getResources().getColor(R.color.mildDiagnosisColor));
            txtDiagnosis.setText(R.string.wheez_ill);
            String age = sharedPreferences.getString(AGE_IN_MONTHS, "");
            float ag = Float.parseFloat(age);
            if (ag < 2){
                messages = Arrays.asList(R.string.wheez_ill1, R.string.wheez_ill2, R.string.wheez_ill3,
                        R.string.wheez_ill4, R.string.wheez_ill5, R.string.wheez_ill6);
            }else {
                messages = Arrays.asList(R.string.wheez_ill1, R.string.wheez_ill2, R.string.wheez_ill7,
                        R.string.wheez_ill71, R.string.wheez_ill72, R.string.wheez_ill8, R.string.wheez_ill9);
            }
        }

        for (int i = 0; i < messages.size(); i++){
            Assessment assessment = new Assessment(messages.get(i));
            assessments.add(assessment);
        }
        recyclerView.setAdapter(assessmentAdapter);

        diagnosis = txtDiagnosis.getText().toString();
        diagnosis = diagnosis.replace("Diagnosis: ", "");

        btnSave.setVisibility(View.GONE);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString(B3DIAGNOSIS, diagnosis);
                editor.apply();
                dialog.dismiss();
                if (day >= 10){
                    showDialog2();
                }else {
                    finalValue();
                    startActivity(new Intent(getActivity(), DiagnosisActivity.class));
                }
            }
        });

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, 1300);
        dialog.show();
    }

    private void showDialog2() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.assess);
        dialog.setCancelable(true);

        TextView txtMessage = dialog.findViewById(R.id.message);
        btnSave = dialog.findViewById(R.id.ContinueButton);
        Button btnCancel= dialog.findViewById(R.id.cancel);

        btnCancel.setVisibility(View.VISIBLE);

        txtMessage.setText("Would you like to complete the Asthma Assessment");
        btnSave.setText("YES");


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finalValue();
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container, new WheezD());
                fr.addToBackStack(null);
                fr.commit();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finalValue();
                startActivity(new Intent(getActivity(), DiagnosisActivity.class));
            }
        });

        dialog.show();
    }

    private void finalValue(){
        editor.putString(FINAL, "final value");
        editor.apply();
    }
}