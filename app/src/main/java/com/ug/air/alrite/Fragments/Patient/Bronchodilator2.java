package com.ug.air.alrite.Fragments.Patient;

import static com.ug.air.alrite.Fragments.Patient.Cough.DIFFICULTY_BREATHING;
import static com.ug.air.alrite.Fragments.Patient.CoughD.DAY1;
import static com.ug.air.alrite.Fragments.Patient.Sex.AGE_IN_MONTHS;
import static com.ug.air.alrite.Fragments.Patient.Wheezing.WHEEZING_SOUNDS;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ug.air.alrite.Activities.DiagnosisActivity;
import com.ug.air.alrite.Adapters.AssessmentAdapter;
import com.ug.air.alrite.Models.Assessment;
import com.ug.air.alrite.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Bronchodilator2 extends Fragment {

    View view;
    Button next, back, btnSave, btnCancel, btnContinue;
    TextView txtMessage;
    Dialog dialog;
    CheckBox other, stock, refuse, need, enough;
    String s, reason;
    EditText etOther;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String REASON = "bronchodilator_not_given_reason";
    public static final String BDIAGNOSIS = "bronchodilator_diagnosis";
    SharedPreferences sharedPreferences, sharedPreferences1;
    SharedPreferences.Editor editor, editor1;
    String diagnosis;
    TextView txtDisease, txtDefinition, txtOk, txtDiagnosis;
    LinearLayout linearLayoutDisease;
    LinearLayout linearLayout_instruction;
    RecyclerView recyclerView;
    long day;
    ArrayList<Assessment> assessments;
    AssessmentAdapter assessmentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bronchodilator_2, container, false);

        back = view.findViewById(R.id.back);
        next = view.findViewById(R.id.next);
        etOther = view.findViewById(R.id.otherText);
        other = view.findViewById(R.id.other);
        stock = view.findViewById(R.id.stock);
        refuse = view.findViewById(R.id.refuse);
        need = view.findViewById(R.id.need);
        enough = view.findViewById(R.id.enough);

        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        other.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (other.isChecked()){
                    etOther.setVisibility(View.VISIBLE);
                }else {
                    etOther.setVisibility(View.GONE);
                    etOther.setText("");
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container, new Bronchodilator());
                fr.commit();
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reason = etOther.getText().toString();
                if (etOther.getVisibility() == View.VISIBLE && reason.isEmpty()){
                    Toast.makeText(getActivity(), "Please fill in the missing field", Toast.LENGTH_SHORT).show();
                    etOther.requestFocus();
                }else {
                    checkedList();
                }

            }
        });

        return view;
    }

    private void checkedList() {
        s = "";

        if(stock.isChecked()){
            s += "Bronchodilator out of stock, ";
        }
        if(enough.isChecked()){
            s += "Not enough time, ";
        }
        if(refuse.isChecked()){
            s += "Patient or family refused, ";
        }
        if(need.isChecked()){
            s += "Bronchodilator not needed by clinical assessment, ";
        }
        if (!reason.isEmpty()){
            s += reason + ", ";
        }
        s = s.replaceAll(", $", "");
        if (s.isEmpty()){
            Toast.makeText(getActivity(), "Choose at least one option", Toast.LENGTH_SHORT).show();
        }else {
//            Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
            saveData(s);
        }
    }

    private void saveData(String s) {
        editor.putString(REASON, s);
        editor.apply();

        String cough = sharedPreferences.getString(DIFFICULTY_BREATHING, "");
        String wheezing = sharedPreferences.getString(WHEEZING_SOUNDS, "");
        String days = sharedPreferences.getString(DAY1, "");
        day = Long.parseLong(days);

        if (cough.equals("Yes") && wheezing.equals("Wheezing")){
//            showDialog2();
            editor.putString(BDIAGNOSIS, diagnosis);
        }else {
            editor.remove(BDIAGNOSIS);
        }
        editor.apply();

        if (day >= 10){
            showDialog();
        }else {
            startActivity(new Intent(getActivity(), DiagnosisActivity.class));
        }
    }

    private void showDialog() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.assess);
        dialog.setCancelable(true);

        txtMessage = dialog.findViewById(R.id.message);
        btnSave = dialog.findViewById(R.id.ContinueButton);
        btnCancel= dialog.findViewById(R.id.cancel);

        btnCancel.setVisibility(View.VISIBLE);

        txtMessage.setText("Would you like to complete the Asthma Assessment");
        btnSave.setText("YES");


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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
                startActivity(new Intent(getActivity(), DiagnosisActivity.class));
            }
        });

        dialog.show();
    }

    private void showDialog2() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.assessment_layout);
        dialog.setCancelable(true);

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

        linearLayout_instruction.setBackgroundColor(getResources().getColor(R.color.moderateDiagnosisColor));
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

        for (int i = 0; i < messages.size(); i++){
            Assessment assessment = new Assessment(messages.get(i));
            assessments.add(assessment);
        }
        recyclerView.setAdapter(assessmentAdapter);

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, 1300);

        diagnosis = txtDiagnosis.getText().toString();
        diagnosis = diagnosis.replace("Diagnosis: ", "");

        btnSave.setVisibility(View.GONE);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString(BDIAGNOSIS, diagnosis);
                editor.apply();
                dialog.dismiss();
                if (day >= 10){
                    showDialog();
                }else {
                    startActivity(new Intent(getActivity(), DiagnosisActivity.class));
                }
            }
        });

        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }

}