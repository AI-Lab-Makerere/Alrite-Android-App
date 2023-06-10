package com.ug.air.alrite.Fragments.Patient;

import static com.ug.air.alrite.Activities.SplashActivity.BRONCHODILATOR_COUNT;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator2.BDIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator2.REASON;
import static com.ug.air.alrite.Fragments.Patient.Initials.INITIAL_DATE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.ug.air.alrite.Activities.DiagnosisActivity;
import com.ug.air.alrite.Adapters.AssessmentAdapter;
import com.ug.air.alrite.Models.Assessment;
import com.ug.air.alrite.R;
import com.ug.air.alrite.Utils.Counter;
import com.ug.air.alrite.Utils.Credentials;
import com.ug.air.alrite.Worker.NotifyWorker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Bronchodilator extends Fragment {

    View view;
    Button btnBron, btnGiven, btnNot, btnSave;
    Dialog dialog;
    String bronchodilator, uniqueID, filename;
    TextView txtMessage;
    public static final String BRONCHODILATOR_WAS_GIVEN = "Bronchodialtor Given";
    public static final String BRONCHODILATOR_NOT_GIVEN = "Bronchodialtor Not Given";
    public static final String USED_BRONCHODILATOR = "bronchodilator";
    public static final String REASSESS = "reassess";
//    public static final String BACKGROUND = "background";
    public static final String SHARED_PREFS = "sharedPrefs";
//    public static final String SHARED_PREFS2 = "background";
    SharedPreferences sharedPreferences, sharedPreferences2;
    SharedPreferences.Editor editor, editor2;
    public static final String DATE = "end_date";
    public static final String PATIENT_ASSESSMENT_ID = "filename";
    public static final String UUIDS = "patient_uuid";
    public static final String CLINICIAN_USERNAME = "clinician";
    public static final String DURATION = "duration";
    TextView txtDisease, txtDefinition, txtOk, txtDiagnosis;
    LinearLayout linearLayoutDisease;
    LinearLayout linearLayout_instruction;
    VideoView videoView;
    String diagnosis, s, assess, second;
    RecyclerView recyclerView;
    Boolean check1;
    ArrayList<Assessment> assessments;
    AssessmentAdapter assessmentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bronchodilator, container, false);

        btnBron = view.findViewById(R.id.bronchodilator);
        btnGiven = view.findViewById(R.id.given);
        btnNot = view.findViewById(R.id.not_given);

        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
//        sharedPreferences2 = requireActivity().getSharedPreferences(SHARED_PREFS2, Context.MODE_PRIVATE);
//        editor2 = sharedPreferences2.edit();

        btnBron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Counter counter = new Counter();
                counter.Count(requireActivity(), BRONCHODILATOR_COUNT);
                showVideoDialog();
            }
        });

        // The patient was given a bronchodilator
        btnGiven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.remove(BDIAGNOSIS);
                editor.remove(REASON);
                bronchodilator = BRONCHODILATOR_WAS_GIVEN;
                editor.putString(USED_BRONCHODILATOR, bronchodilator);
                editor.apply();
                showDialog();
            }
        });

        // The patient was not given a bronchodilator
        btnNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bronchodilator = BRONCHODILATOR_NOT_GIVEN;
                editor.putString(USED_BRONCHODILATOR, bronchodilator);
                editor.apply();
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container, new Bronchodilator2());
                fr.addToBackStack(null);
                fr.commit();
            }
        });

        return view;
    }

    private void showDialog() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.assess);
        dialog.setCancelable(true);

        txtMessage = dialog.findViewById(R.id.message);
        btnSave = dialog.findViewById(R.id.ContinueButton);

        btnSave.setText("Continue");

        txtMessage.setText("Please wait for 15 minutes to reassess the patient");
        btnSave.setText("OK");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                editor.putBoolean(REASSESS, false);
                startBackGroundTask();
            }
        });

        dialog.show();
    }

    private void showVideoDialog() {
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

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        assessments = new ArrayList<>();
        assessmentAdapter = new AssessmentAdapter(assessments);

        List<Integer> messages = Arrays.asList(R.string.spacer1, R.string.spacer2, R.string.spacer3,
                R.string.spacer4, R.string.spacer5, R.string.spacer6, R.string.spacer7, R.string.spacer8,
                R.string.spacer9, R.string.spacer10, R.string.spacer11, R.string.spacer12, R.string.spacer13,
                R.string.spacer14, R.string.spacer15, R.string.spacer16, R.string.spacer17);

        for (int i = 0; i < messages.size(); i++){
            Assessment assessment = new Assessment(messages.get(i));
            assessments.add(assessment);
        }
        recyclerView.setAdapter(assessmentAdapter);

        txtDisease.setText("Bronchodilator");
        txtDefinition.setText(R.string.bronchodi);
        linearLayoutDisease.setBackgroundColor(getResources().getColor(R.color.green_dark));

        String videoPath = "android.resource://" + requireActivity().getPackageName() + "/" + R.raw.bronchodilator_glossary_video;
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

    private void startBackGroundTask() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
        String formattedDate = df.format(currentTime);

        getDuration(currentTime);

        Credentials credentials = new Credentials();
        String username = credentials.creds(getActivity()).getUsername();
        editor.putString(CLINICIAN_USERNAME, username);

        uniqueID = UUID.randomUUID().toString();

        editor.putString(DATE, formattedDate);
        editor.putString(UUIDS, uniqueID);

        filename = formattedDate + "_" + uniqueID;
        editor.putString(PATIENT_ASSESSMENT_ID, filename);
        editor.apply();

        Data inputData = new Data.Builder()
                .putString("filename", filename)
                .build();

        WorkRequest notifyWorkRequest = new OneTimeWorkRequest
                .Builder(NotifyWorker.class)
                .setInputData(inputData)
                .setInitialDelay(15, TimeUnit.MINUTES)
                .addTag(filename)
                .build();

        WorkManager.getInstance(getActivity()).enqueueUniqueWork(filename, ExistingWorkPolicy.REPLACE, (OneTimeWorkRequest) notifyWorkRequest);

        startActivity(new Intent(getActivity(), DiagnosisActivity.class));
    }

    private void getDuration(Date currentTime) {
        String initial_date = sharedPreferences.getString(INITIAL_DATE, "");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
        try {
            Date d1 = format.parse(initial_date);

            long diff = currentTime.getTime() - d1.getTime();//as given

            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            String duration = String.valueOf(minutes);
            editor.putString(DURATION, duration);
            editor.apply();
            Log.d("Difference in time", "getTimeDifference: " + minutes);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}