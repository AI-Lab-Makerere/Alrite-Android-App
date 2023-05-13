package com.ug.air.alrite.Fragments.Patient;

import static com.ug.air.alrite.Fragments.Patient.Assess.SEVERE_SYMPTOMS;
import static com.ug.air.alrite.Fragments.Patient.Sex.AGE_IN_MONTHS;
import static com.ug.air.alrite.Fragments.Patient.Sex.KILO;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ug.air.alrite.Adapters.AssessmentAdapter;
import com.ug.air.alrite.Models.Assessment;
import com.ug.air.alrite.R;
import com.ug.air.alrite.Utils.Calculations.Instructions;

import java.util.ArrayList;
import java.util.List;

public class FTouch extends Fragment {

    View view;
    Button back, next, btnExit, btnContinue;
    RadioGroup radioGroup1;
    RadioButton radioButton3, radioButton4;
    String value6 = "none";
    private static final int YES1 = 0;
    private static final int NO1 = 1;
    public static final String TOUCH = "febrile_to_touch";
    public static final String FTDIAGNOSIS = "diagnosis_5";
    public static final String SHARED_PREFS = "sharedPrefs";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Dialog dialog;
    List messages;
    RecyclerView recyclerView;
    LinearLayout linearLayout_instruction;
    TextView txtDiagnosis;
    ArrayList<Assessment> assessments;
    AssessmentAdapter assessmentAdapter;
    String diagnosis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_touch, container, false);

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

                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container, new Temperature());
                fr.commit();
            }
        });

        return view;
    }

    private void saveData() {

        editor.putString(TOUCH, value6);
        editor.apply();

        String assess = sharedPreferences.getString(SEVERE_SYMPTOMS, "");
        if (value6.equals("Yes") && !assess.equals("None of these")){
            showDialog();
        }else {
            editor.remove(FTDIAGNOSIS);
            editor.apply();
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_container, new Oxygen());
            fr.addToBackStack(null);
            fr.commit();
        }
    }

    private void loadData() {
        value6 = sharedPreferences.getString(TOUCH, "");
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

    private void showDialog() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.assessment_layout);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        linearLayout_instruction = dialog.findViewById(R.id.diagnosis);
        txtDiagnosis = dialog.findViewById(R.id.txtDiagnosis);
        recyclerView = dialog.findViewById(R.id.recyclerView1);
        btnExit = dialog.findViewById(R.id.btnSave);
        btnContinue = dialog.findViewById(R.id.btnContinue);

        linearLayout_instruction.setBackgroundColor(getResources().getColor(R.color.severeDiagnosisColor));
        txtDiagnosis.setText(R.string.febril);
        diagnosis = txtDiagnosis.getText().toString();
        diagnosis = diagnosis.replace("Diagnosis: ", "");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        assessments = new ArrayList<>();
        assessmentAdapter = new AssessmentAdapter(assessments);

        String age = sharedPreferences.getString(AGE_IN_MONTHS, "");
        String weight = sharedPreferences.getString(KILO, "");
        int ag = Integer.parseInt(age);

        Instructions instructions = new Instructions();
        messages = instructions.GetFebrilInstructions(ag, weight);

        for (int i = 0; i < messages.size(); i++){
            Assessment assessment = new Assessment((Integer) messages.get(i));
            assessments.add(assessment);
        }
        recyclerView.setAdapter(assessmentAdapter);

        btnExit.setVisibility(View.GONE);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(FTDIAGNOSIS, diagnosis);
                editor.apply();
                dialog.dismiss();
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container, new Oxygen());
                fr.addToBackStack(null);
                fr.commit();
            }
        });

//        dialog.getWindow().setLayout(650, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }
}