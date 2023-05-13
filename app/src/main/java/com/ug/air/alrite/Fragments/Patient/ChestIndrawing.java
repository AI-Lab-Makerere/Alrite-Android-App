package com.ug.air.alrite.Fragments.Patient;

import static com.ug.air.alrite.Activities.SplashActivity.CHESTINDRWAING_COUNT;
import static com.ug.air.alrite.Fragments.Patient.Assess.DIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.Assess.FINAL_DIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.Assess.SEVERE_SYMPTOMS;
import static com.ug.air.alrite.Fragments.Patient.Cough.DIFFICULTY_BREATHING;
import static com.ug.air.alrite.Fragments.Patient.HIVStatus.HDIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.Nasal.CHOICEGN;
import static com.ug.air.alrite.Fragments.Patient.Nasal.GNDIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.Oxygen.OXDIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.RRCounter.SECOND;
import static com.ug.air.alrite.Fragments.Patient.Stridor.STDIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.Wheezing.WHEEZING_SOUNDS;
import static com.ug.air.alrite.Fragments.Patient.CoughD.DAY1;
import static com.ug.air.alrite.Fragments.Patient.RRCounter.FASTBREATHING;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import com.ug.air.alrite.Utils.Calculations.Instructions;
import com.ug.air.alrite.Utils.Counter;

import java.util.ArrayList;

public class ChestIndrawing extends Fragment {

    View view;
    Button back, next, btnSave, btnChest, btnContinue;
    RadioGroup radioGroup;
    RadioButton radioButton1, radioButton2, radioButton3;
    String value8 = "none";
    String diagnosis, wheezing, second;
    long day;
    TextView txtDisease, txtDefinition, txtOk, txtDiagnosis;
    LinearLayout linearLayoutDisease;
    LinearLayout linearLayout_instruction;
    VideoView videoView;
    Dialog dialog;
    RecyclerView recyclerView;
    ArrayList<Assessment> assessments;
    AssessmentAdapter assessmentAdapter;
    private static final int YES = 1;
    private static final int NO = 0;
    private static final int NOT = 2;
    public static final String CHOICE7 = "chest_indrawing";
    public static final String POINT = "respiratory_rate_score";
    public static final String POINT2 = "respiratory_rate_score_2";
    public static final String CHOICE72 = "chest_indrawing_2";
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String CIDIAGNOSIS = "diagnosis_7";
    SharedPreferences sharedPreferences, sharedPreferences1;
    SharedPreferences.Editor editor, editor1;
    String fastBreathing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_indrawing, container, false);


        next = view.findViewById(R.id.next);
        back = view.findViewById(R.id.back);
        radioGroup = view.findViewById(R.id.radioGroup);
        radioButton1 = view.findViewById(R.id.no);
        radioButton2 = view.findViewById(R.id.yes);
        radioButton3 = view.findViewById(R.id.not);
        btnChest = view.findViewById(R.id.chest);

        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        second = sharedPreferences.getString(SECOND, "");

        if (second.isEmpty()){
            loadData();
            updateViews();
        }


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = radioGroup.findViewById(checkedId);
                int index = radioGroup.indexOfChild(radioButton);

                switch (index) {
                    case NO:
                        value8 = "No";
                        break;
                    case YES:
                        value8 = "Mild";
                        break;
                    case NOT:
                        value8 = "Moderate/Severe";
                        break;
                    default:
                        break;
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (value8.isEmpty()){
                    Toast.makeText(getActivity(), "Please select at least one of the options", Toast.LENGTH_SHORT).show();
                }else {
                    saveData();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String assess = sharedPreferences.getString(SEVERE_SYMPTOMS, "");
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();

                if (second.isEmpty()){
                    if (!assess.equals("None of these")){
                        fr.replace(R.id.fragment_container, new Wheezing());
                    }else{
                        fr.replace(R.id.fragment_container, new Nasal());
                    }

                }else {
                    fr.replace(R.id.fragment_container, new Wheezing());
                }
                fr.commit();
            }
        });

        btnChest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Counter counter = new Counter();
                counter.Count(requireActivity(), CHESTINDRWAING_COUNT);
                showDialog();
            }
        });

        return view;
    }

    private void saveData() {
        if (second.isEmpty()){
            editor.putString(CHOICE7, value8);
            editor.apply();
            String assess = sharedPreferences.getString(SEVERE_SYMPTOMS, "");

            if (!assess.equals("None of these")){
                startActivity(new Intent(getActivity(), DiagnosisActivity.class));
            }else {
                makeAssessment();
            }
        }else {
            calculatePoints();
            editor.putString(CHOICE72, value8);
            editor.apply();
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_container, new Bronchodilator3());
            fr.addToBackStack(null);
            fr.commit();
        }

    }

    private void loadData() {
        value8 = sharedPreferences.getString(CHOICE7, "");
    }

    private void updateViews() {
        if (value8.equals("Mild")){
            radioButton2.setChecked(true);
        }else if (value8.equals("No")){
            radioButton1.setChecked(true);
        }else if (value8.equals("Moderate/Severe")){
            radioButton3.setChecked(true);
        }else {
            radioButton1.setChecked(false);
            radioButton2.setChecked(false);
            radioButton3.setChecked(false);
        }
    }

    private void showDialog() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.learn_popup);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        txtDefinition = dialog.findViewById(R.id.definition);
        txtDisease = dialog.findViewById(R.id.diseaseName);
        txtOk = dialog.findViewById(R.id.ok);
        linearLayoutDisease = dialog.findViewById(R.id.disease);
        videoView = dialog.findViewById(R.id.video_view);
        recyclerView = dialog.findViewById(R.id.recyclerView2);
        CardView inst = dialog.findViewById(R.id.inst);

        inst.setVisibility(View.GONE);

        txtDisease.setText("Chest Indrawing");
        txtDefinition.setText(R.string.chest_in);
        linearLayoutDisease.setBackgroundColor(getResources().getColor(R.color.green_dark));

        String videoPath = "android.resource://" + requireActivity().getPackageName() + "/" + R.raw.chest_indrawing_glossary_video;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        ImageView imPlay = dialog.findViewById(R.id.play);
        imPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imPlay.setVisibility(View.GONE);
                videoView.start();
            }
        });

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imPlay.setVisibility(View.VISIBLE);
                videoView.pause();
            }
        });

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.stopPlayback();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void makeAssessment(){
        String cough = sharedPreferences.getString(DIFFICULTY_BREATHING, "");
        String fDiagnosis = sharedPreferences.getString(DIAGNOSIS, "");
        String oxDiagnosis = sharedPreferences.getString(OXDIAGNOSIS, "");
        String stDiagnosis = sharedPreferences.getString(STDIAGNOSIS, "");
        String gnDiagnosis = sharedPreferences.getString(GNDIAGNOSIS, "");
        String hDiagnosis = sharedPreferences.getString(HDIAGNOSIS, "");
        String oneDiagnosis = sharedPreferences.getString(FINAL_DIAGNOSIS, "");
        fastBreathing = sharedPreferences.getString(FASTBREATHING, "");
        wheezing = sharedPreferences.getString(WHEEZING_SOUNDS, "");
        String days = sharedPreferences.getString(DAY1, "");
        day = Long.parseLong(days);
        boolean cx = oneDiagnosis.isEmpty() && hDiagnosis.isEmpty();
        boolean cx2 = oneDiagnosis.isEmpty();
        boolean b = oxDiagnosis.isEmpty() && stDiagnosis.isEmpty() && gnDiagnosis.isEmpty() && fDiagnosis.isEmpty() && hDiagnosis.isEmpty();
        boolean b2 = oxDiagnosis.isEmpty() && stDiagnosis.isEmpty() && gnDiagnosis.isEmpty() && fDiagnosis.isEmpty();

        if (cx && (cough.equals("Yes") && fastBreathing.equals("Fast Breathing"))){
            editor.putString(CIDIAGNOSIS, "Pneumonia");
            editor.apply();
        }else if (cx && (cough.equals("Yes") && (value8.equals("Mild") || value8.equals("Moderate/Severe")))){
            editor.putString(CIDIAGNOSIS, "Pneumonia");
            editor.apply();
        }else if(cx2 && (cough.equals("Yes") && fastBreathing.equals("Normal Breathing") &&
                value8.equals("No") && (wheezing.equals("Other abnormal breath sounds") || wheezing.equals("Normal breath sounds")))){
            editor.putString(CIDIAGNOSIS, "Cough/Cold/No Pneumonia");
            editor.apply();
        }else{
            Log.d("TAG,", "nothing");
        }

        calculatePoints();

        if (wheezing.equals("Wheezing")  || day >= 10){
            calculatePoints();
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_container, new Bronchodilator());
            fr.addToBackStack(null);
            fr.commit();
        }else {
            startActivity(new Intent(getActivity(), DiagnosisActivity.class));
        }
    }

//    private void showDialog2(String disease) {
//        dialog = new Dialog(getActivity());
//        dialog.setContentView(R.layout.assessment_layout);
//        dialog.setCancelable(true);
////        Window window = dialog.getWindow();
////        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//
//        linearLayout_instruction = dialog.findViewById(R.id.diagnosis);
//        txtDiagnosis = dialog.findViewById(R.id.txtDiagnosis);
//        recyclerView = dialog.findViewById(R.id.recyclerView1);
//        btnSave = dialog.findViewById(R.id.btnSave);
//        btnContinue = dialog.findViewById(R.id.btnContinue);
//
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//
//        assessments = new ArrayList<>();
//        assessmentAdapter = new AssessmentAdapter(assessments);
//
//        List messages = new ArrayList<>();
//
//        if (disease.equals("pneumonia")){
//            linearLayout_instruction.setBackgroundColor(getResources().getColor(R.color.moderateDiagnosisColor));
//            txtDiagnosis.setText(R.string.pneumonia);
//
//            String age = sharedPreferences.getString(AGE, "");
//            String weight = sharedPreferences.getString(KILO, "");
//            int ag = Integer.parseInt(age);
//
//            Instructions instructions = new Instructions();
//            messages = instructions.GetPneumoniaInstructions(ag, weight);
//
//        }else {
//            linearLayout_instruction.setBackgroundColor(getResources().getColor(R.color.mildDiagnosisColor));
//            txtDiagnosis.setText(R.string.cold);
//            messages = Arrays.asList(R.string.cold1, R.string.cold2, R.string.cold3, R.string.cold4);
//        }
//
//        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, 1300);
//        diagnosis = txtDiagnosis.getText().toString();
//        diagnosis = diagnosis.replace("Diagnosis: ", "");
//
//        for (int i = 0; i < messages.size(); i++){
//            Assessment assessment = new Assessment((Integer) messages.get(i));
//            assessments.add(assessment);
//        }
//        recyclerView.setAdapter(assessmentAdapter);
//
//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editor.putString(CIDIAGNOSIS, diagnosis);
//                editor.apply();
//                dialog.dismiss();
//                calculatePoints();
//                startActivity(new Intent(getActivity(), DiagnosisActivity.class));
//            }
//        });
//
//        if (wheezing.equals("Wheezing") || day >= 10){
//            btnContinue.setVisibility(View.VISIBLE);
//        }else {
//            btnContinue.setVisibility(View.GONE);
//        }
//
//        btnContinue.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                editor.putString(CIDIAGNOSIS, diagnosis);
//                editor.apply();
//                dialog.dismiss();
//                calculatePoints();
//                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
//                fr.replace(R.id.fragment_container, new Bronchodilator());
//                fr.addToBackStack(null);
//                fr.commit();
//            }
//        });
//
////        dialog.getWindow().setLayout(650, 800);
//        dialog.getWindow().setGravity(Gravity.CENTER);
//        dialog.show();
//    }

    private void calculatePoints() {
        String granting = sharedPreferences.getString(CHOICEGN, "");

        if (second.isEmpty()){
            String pt = sharedPreferences.getString(POINT, "");
            Instructions instructions = new Instructions();
            int points = instructions.GetChestIndrawing(value8, granting, Integer.parseInt(pt));
            editor.putString(POINT, String.valueOf(points));
        }else {
            String pt = sharedPreferences.getString(POINT2, "");
            Instructions instructions = new Instructions();
            int points = instructions.GetChestIndrawing(value8, granting, Integer.parseInt(pt));
            editor.putString(POINT2, String.valueOf(points));
        }
        editor.apply();
    }
}