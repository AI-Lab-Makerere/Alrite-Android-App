package com.ug.air.alrite.Fragments.Patient;

import static com.ug.air.alrite.Activities.SplashActivity.WHEEZING_COUNT;
import static com.ug.air.alrite.Fragments.Patient.Assess.SEVERE_SYMPTOMS;
import static com.ug.air.alrite.Fragments.Patient.ChestIndrawing.POINT;
import static com.ug.air.alrite.Fragments.Patient.ChestIndrawing.POINT2;
import static com.ug.air.alrite.Fragments.Patient.RRCounter.SECOND;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ug.air.alrite.Adapters.AssessmentAdapter;
import com.ug.air.alrite.Models.Assessment;
import com.ug.air.alrite.R;
import com.ug.air.alrite.Utils.Calculations.Instructions;
import com.ug.air.alrite.Utils.Counter;

import java.util.ArrayList;

public class Wheezing extends Fragment {

    View view;
    Button back, next, btnSave, btnWheez;
    RadioGroup radioGroup;
    CheckBox checkBox;
    RadioButton radioButton1, radioButton2, radioButton3;
    String breathSounds = "none";
    TextView txtDisease, txtDefinition, txtOk, txtDiagnosis;
    LinearLayout linearLayoutDisease;
    LinearLayout linearLayout_instruction;
    VideoView videoView;
    Dialog dialog;
    String diagnosis, s, assess, second;
    RecyclerView recyclerView;
    Boolean check1;
    ArrayList<Assessment> assessments;
    AssessmentAdapter assessmentAdapter;
    private static final int YES = 0;
    private static final int NO = 1;
    private static final int NOT = 2;
    public static final String STETHOSCOPE_USED = "stethoscope_used";
    public static final String WHEEZING_SOUNDS = "wheezing";
    public static final String CHOICE82 = "wheezing_2";
    public static final String SHARED_PREFS = "sharedPrefs";
    SharedPreferences sharedPreferences, sharedPreferences1;
    SharedPreferences.Editor editor, editor1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_wheezing, container, false);

        next = view.findViewById(R.id.next);
        back = view.findViewById(R.id.back);
        radioGroup = view.findViewById(R.id.radioGroup);
        radioButton1 = view.findViewById(R.id.wheezing);
        radioButton2 = view.findViewById(R.id.noisy_breathing);
        radioButton3 = view.findViewById(R.id.normal);
        btnWheez = view.findViewById(R.id.wheez);
        checkBox = view.findViewById(R.id.stethoscope);

        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        assess = sharedPreferences.getString(SEVERE_SYMPTOMS, "");
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
                    case YES:
                        breathSounds = "Wheezing";
                        break;
                    case NO:
                        breathSounds = "Other abnormal breath sounds";
                        break;
                    case NOT:
                        breathSounds = "Normal breath sounds";
                        break;
                    default:
                        break;
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (breathSounds.isEmpty()){
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
                if (second.isEmpty()){
                    if (!assess.equals("None of these")){
                        fr.replace(R.id.fragment_container, new RRCounter());
                    }else {
                        fr.replace(R.id.fragment_container, new Stridor());
                    }
                }else {
                    fr.replace(R.id.fragment_container, new RRCounter());
                }
                fr.commit();

            }
        });

        btnWheez.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Counter counter = new Counter();
                counter.Count(requireActivity(), WHEEZING_COUNT);
                showDialog();
            }
        });

        return view;
    }

    private void saveData() {

        if (second.isEmpty()){
            editor.putString(WHEEZING_SOUNDS, breathSounds);
            editor.putBoolean(STETHOSCOPE_USED, checkBox.isChecked());
            editor.apply();
            if (!assess.equals("None of these")){
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container, new ChestIndrawing());
                fr.addToBackStack(null);
                fr.commit();
            }else {
                String pt = sharedPreferences.getString(POINT, "");
                int point = Integer.parseInt(pt);

                Instructions instructions = new Instructions();
                int pot = instructions.GetWheezing(breathSounds, point);
                editor.putString(POINT, String.valueOf(pot));
                editor.apply();

                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container, new Nasal());
                fr.addToBackStack(null);
                fr.commit();
            }
        }else {
            editor.putString(CHOICE82, breathSounds);
            editor.putBoolean(STETHOSCOPE_USED, checkBox.isChecked());
            editor.apply();

            String pt = sharedPreferences.getString(POINT2, "");
            int point = Integer.parseInt(pt);

            Instructions instructions = new Instructions();
            int pot = instructions.GetWheezing(breathSounds, point);
            editor.putString(POINT2, String.valueOf(pot));
            editor.apply();

            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_container, new ChestIndrawing());
            fr.addToBackStack(null);
            fr.commit();
        }

    }

    private void loadData() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        breathSounds = sharedPreferences.getString(WHEEZING_SOUNDS, "");
        check1 = sharedPreferences.getBoolean(STETHOSCOPE_USED, false);
    }

    private void updateViews() {
        if (breathSounds.equals("Wheezing")){
            radioButton1.setChecked(true);
        }else if (breathSounds.equals("Noisy breathing")){
            radioButton2.setChecked(true);
        }else if (breathSounds.equals("Normal breathing")){
            radioButton3.setChecked(true);
        }else {
            radioButton1.setChecked(false);
            radioButton2.setChecked(false);
            radioButton3.setChecked(false);
        }

        checkBox.setChecked(check1);
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

        txtDisease.setText("Wheezing");
        txtDefinition.setText(R.string.wheez);
        linearLayoutDisease.setBackgroundColor(getResources().getColor(R.color.green_dark));

        String videoPath = "android.resource://" + requireActivity().getPackageName() + "/" + R.raw.wheezing_glossary_video;
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
}