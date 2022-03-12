package com.ug.air.alrite.Fragments.Patient;

import static com.ug.air.alrite.Fragments.Patient.Cough.CHOICE2;
import static com.ug.air.alrite.Fragments.Patient.Wheezing.CHOICE8;
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
    String diagnosis, s, assess, second;
    RecyclerView recyclerView;
    ArrayList<Assessment> assessments;
    AssessmentAdapter assessmentAdapter;
    private static final int YES = 0;
    private static final int NO = 1;
    private static final int NOT = 2;
    public static final String BRONC = "bron";
    public static final String B3DIAGNOSIS = "b3Diagnosis";
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
        editor.putString(BRONC, value9);
        editor.apply();

        String cough = sharedPreferences.getString(CHOICE2, "");
        String wheezing = sharedPreferences.getString(CHOICE82, "");
//        respiratory score decreasing


        if (value9.equals("Worse")){
            val = 1;
            showDialog(val);
        }else if (cough.equals("Yes") && value9.equals("Better")){
            val = 2;
            showDialog(val);
        }else if (cough.equals("Yes") && wheezing.equals("Wheezing") && value9.equals("Same/ No change")){
            val = 3;
            showDialog(val);
        }else {
            editor.remove(B3DIAGNOSIS);
            editor.apply();
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
            messages = Arrays.asList(R.string.bronc1, R.string.bronc1);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, 1300);
        }else if (value == 2){

        }else if (value == 3){

        }
    }
}