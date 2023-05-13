package com.ug.air.alrite.Fragments.Patient;

import static com.ug.air.alrite.Activities.SplashActivity.STRIDOR_COUNT;
import static com.ug.air.alrite.Fragments.Patient.Assess.FINAL_DIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.Assess.SEVERE_SYMPTOMS;
import static com.ug.air.alrite.Fragments.Patient.Sex.AGE_IN_MONTHS;
import static com.ug.air.alrite.Fragments.Patient.Sex.KILO;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
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
import java.util.List;

public class Stridor extends Fragment {

    View view;
    Button back, next, btnSave, btnStridor, btnContinue;
    RadioGroup radioGroup;
    RadioButton radioButton1, radioButton2;
    String value7 = "none";
    TextView txtDisease, txtDefinition, txtOk,txtDiagnosis;
    LinearLayout linearLayoutDisease, linearLayout_instruction;
    VideoView videoView;
    MediaPlayer mediaPlayer;
    Dialog dialog, dialog1;
    RecyclerView recyclerView;
    ArrayList<Assessment> assessments;
    AssessmentAdapter assessmentAdapter;
    CardView inst;
    List messages;
    String diagnosis;
    private static final int YES = 0;
    private static final int NO = 1;
    public static final String CHOICE6 = "stridor";
    public static final String STDIAGNOSIS = "stDiagnosis";
    public static final String SHARED_PREFS = "sharedPrefs";
    SharedPreferences sharedPreferences, sharedPreferences1;
    SharedPreferences.Editor editor, editor1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_stridor, container, false);

        next = view.findViewById(R.id.next);
        back = view.findViewById(R.id.back);
        radioGroup = view.findViewById(R.id.radioGroup);
        radioButton1 = view.findViewById(R.id.yes);
        radioButton2 = view.findViewById(R.id.no);
        btnStridor = view.findViewById(R.id.stridor);

        sharedPreferences = this.requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        loadData();
        updateViews();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = radioGroup.findViewById(checkedId);
                int index = radioGroup.indexOfChild(radioButton);

                switch (index) {
                    case YES:
                        value7 = "Yes";
                        break;
                    case NO:
                        value7 = "No";
                        break;
                    default:
                        break;
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (value7.isEmpty()){
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
                fr.replace(R.id.fragment_container, new RRCounter());
                fr.commit();
            }
        });

        btnStridor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Counter counter = new Counter();
                counter.Count(requireActivity(), STRIDOR_COUNT);
                showDialog();
            }
        });

        return view;
    }

    private void saveData() {
        editor.putString(CHOICE6, value7);
        editor.apply();

        checkIfNone();
        
    }

    private void loadData() {
        value7 = sharedPreferences.getString(CHOICE6, "");
    }

    private void updateViews() {
        if (value7.equals("Yes")){
            radioButton1.setChecked(true);
        }else if (value7.equals("No")){
            radioButton2.setChecked(true);
        }else {
            radioButton1.setChecked(false);
            radioButton2.setChecked(false);
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
        inst = dialog.findViewById(R.id.inst);

        inst.setVisibility(View.GONE);

        txtDisease.setText("Stridor");
        txtDefinition.setText("A noise on breathing in due to obstruction of the upper airway");
        linearLayoutDisease.setBackgroundColor(getResources().getColor(R.color.green_dark));

        String videoPath = "android.resource://" + requireActivity().getPackageName() + "/" + R.raw.stridor_glossary_video;
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

    private void checkIfNone() {
        if (value7.equals("No")){
            editor.remove(STDIAGNOSIS);
            editor.remove(FINAL_DIAGNOSIS);
            editor.apply();
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_container, new Wheezing());
            fr.addToBackStack(null);
            fr.commit();
        }else{
            displayDialog();
        }
    }

    private void displayDialog() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.assessment_layout);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
//        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

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
            public void onClick(View v) {
                finalDiagnosis();
                editor.putString(STDIAGNOSIS, diagnosis);
                editor.apply();
                dialog.dismiss();
                startActivity(new Intent(getActivity(), DiagnosisActivity.class));
            }
        });
        btnSave.setVisibility(View.VISIBLE);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalDiagnosis();
                editor.putString(STDIAGNOSIS, diagnosis);
                editor.apply();
                dialog.dismiss();
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container, new Wheezing());
                fr.addToBackStack(null);
                fr.commit();
            }
        });

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }

    private void finalDiagnosis() {
        editor.putString(FINAL_DIAGNOSIS, "Severe Pneumonia OR very Severe Disease");
        editor.apply();
    }

}