package com.ug.air.alrite.Activities;

import static com.ug.air.alrite.Activities.FinalActivity.S6;
import static com.ug.air.alrite.Activities.FinalActivity.S7;
import static com.ug.air.alrite.Activities.PatientActivity.ASSESS_INCOMPLETE;
import static com.ug.air.alrite.Fragments.Patient.Allergies.CHOICEY2;
import static com.ug.air.alrite.Fragments.Patient.Assess.FINAL_DIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.Assess.SEVERE_SYMPTOMS;
import static com.ug.air.alrite.Fragments.Patient.Breathless.S5;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.BRONCHODILATOR_WAS_GIVEN;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.DATE;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.DURATION;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.FILENAME;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.USED_BRONCHODILATOR;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.USERNAME;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.UUIDS;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator2.BDIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator2.REASON;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator3.AFTER_BRONCHODILATOR;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator3.B3DIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.ChestIndrawing.CHOICE7;
import static com.ug.air.alrite.Fragments.Patient.ChestIndrawing.CHOICE72;
import static com.ug.air.alrite.Fragments.Patient.ChestIndrawing.CIDIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.ChestIndrawing.POINT;
import static com.ug.air.alrite.Fragments.Patient.ChestIndrawing.POINT2;
import static com.ug.air.alrite.Fragments.Patient.Cough.DIFFICULTY_BREATHING;
import static com.ug.air.alrite.Fragments.Patient.Cough.NODIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.CoughD.DAY1;
import static com.ug.air.alrite.Fragments.Patient.Eczema.CHOICEX2;
import static com.ug.air.alrite.Fragments.Patient.FTouch.FTDIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.FTouch.TOUCH;
import static com.ug.air.alrite.Fragments.Patient.HIVCare.CHOICEHC;
import static com.ug.air.alrite.Fragments.Patient.HIVStatus.CHOICE3;
import static com.ug.air.alrite.Fragments.Patient.HIVStatus.HDIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.Initials.CHILD_INITIALS;
import static com.ug.air.alrite.Fragments.Patient.Initials.INITIAL_DATE;
import static com.ug.air.alrite.Fragments.Patient.Initials.PARENT_INITIALS;
import static com.ug.air.alrite.Fragments.Patient.Kerosene.ADIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.Kerosene.CHOICET2;
import static com.ug.air.alrite.Fragments.Patient.Kerosene.TUDIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.Nasal.CHOICEGN;
import static com.ug.air.alrite.Fragments.Patient.Oxygen.OXY;
import static com.ug.air.alrite.Fragments.Patient.RRCounter.FASTBREATHING;
import static com.ug.air.alrite.Fragments.Patient.RRCounter.FASTBREATHING2;
import static com.ug.air.alrite.Fragments.Patient.RRCounter.INITIAL_DATE_2;
import static com.ug.air.alrite.Fragments.Patient.RRCounter.SECOND;
import static com.ug.air.alrite.Fragments.Patient.Sex.AGE_IN_MONTHS;
import static com.ug.air.alrite.Fragments.Patient.Sex.AGE_IN_YEARS;
import static com.ug.air.alrite.Fragments.Patient.Sex.KILO;
import static com.ug.air.alrite.Fragments.Patient.Sex.MDIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.Sex.MUAC;
import static com.ug.air.alrite.Fragments.Patient.Sex.SEX;
import static com.ug.air.alrite.Fragments.Patient.Smoke.CHOICET1;
import static com.ug.air.alrite.Fragments.Patient.Stridor.CHOICE6;
import static com.ug.air.alrite.Fragments.Patient.Temperature.TDIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.Temperature.TEMP;
import static com.ug.air.alrite.Fragments.Patient.WheezD.CHOICEX;
import static com.ug.air.alrite.Fragments.Patient.WheezY.DAY2;
import static com.ug.air.alrite.Fragments.Patient.Wheezing.CHOICE82;
import static com.ug.air.alrite.Fragments.Patient.Wheezing.WHEEZING_SOUNDS;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ug.air.alrite.Adapters.AssessmentAdapter;
import com.ug.air.alrite.Adapters.DiagnosisAdapter;
import com.ug.air.alrite.Adapters.SummaryAdapter;
import com.ug.air.alrite.BuildConfig;
import com.ug.air.alrite.Models.Assessment;
import com.ug.air.alrite.Models.Diagnosis;
import com.ug.air.alrite.Models.Summary;
import com.ug.air.alrite.R;
import com.ug.air.alrite.Utils.Calculations.Instructions;
import com.ug.air.alrite.Utils.Credentials;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DiagnosisActivityModified extends AppCompatActivity {

    LinearLayout summaryTabLayout, summaryExpandedLayout, diagnosisTabLayout, diagnosisExpandedLayout;
    Button btnExit, btnExit2, btnContinue, btnSave;
    ImageView summaryTabIcon, diagnosisTabIcon;
    RecyclerView summaryListRV, diagnosisListRV;
    TextView txtInitials, txtAge, txtGender;
    DiagnosisAdapter diagnosisAdapter;
    SummaryAdapter summaryAdapter;
    List<Summary> summaryList;
    List<String> messages = new ArrayList<>();;
    List messageList = new ArrayList<>();;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String DATE_2 = "end_date_2";
    public static final String PENDING = "pending";
    public static final String DURATION_2 = "duration_2";
    SharedPreferences sharedPreferences, sharedPreferences1;
    SharedPreferences.Editor editor, editor1;
    String ageInYears, uniqueID, ageInMonths, folder, isPending;
    float ag;
    Dialog dialog;
    RecyclerView recyclerView;
    LinearLayout linearLayout_instruction;
    TextView txtDiagnosis;
    ArrayList<Assessment> assessments;
    List messages2;
    AssessmentAdapter assessmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis);

        // Get references to page objects
        txtAge = findViewById(R.id.patient_age);
        txtGender = findViewById(R.id.patient_sex);
        txtInitials = findViewById(R.id.initials);
        summaryTabIcon = findViewById(R.id.accordion);
        summaryTabLayout = findViewById(R.id.clickable);
        summaryExpandedLayout = findViewById(R.id.summary2);
        summaryListRV = findViewById(R.id.recyclerView1);
        diagnosisTabIcon = findViewById(R.id.accordion2);
        diagnosisTabLayout = findViewById(R.id.clickable2);
        diagnosisExpandedLayout = findViewById(R.id.summary3);
        btnExit = findViewById(R.id.btnExit);
        btnSave = findViewById(R.id.btnExit2);
        diagnosisListRV = findViewById(R.id.recyclerView2);

        // Get an editor for our shared preferences
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Save the patient's age, for reference in the diagnosis instructions
        ageInMonths = sharedPreferences.getString(AGE_IN_MONTHS, "");

        // At the top of the page, display the patient's initials, age, and gender
        String initials = sharedPreferences.getString(CHILD_INITIALS, "");
        ageInYears = sharedPreferences.getString(AGE_IN_YEARS, "");
        String[] ageInYearsSplit = ageInYears.split("\\.");
        String gender = sharedPreferences.getString(SEX, "");
        txtInitials.setText(initials);
        txtAge.setText("Age: " + ageInYearsSplit[0] + " years and " + ageInYearsSplit[1] + " months");
        txtGender.setText("Gender: " + gender);

        // Click on the summary tab to expand it to show a summary of the patient's health
        summaryTabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (summaryExpandedLayout.getVisibility() == View.GONE){
                    summaryExpandedLayout.setVisibility(View.VISIBLE);
                    summaryTabIcon.setImageResource(R.drawable.ic_sub);
                }else{
                    summaryExpandedLayout.setVisibility(View.GONE);
                    summaryTabIcon.setImageResource(R.drawable.ic_add);
                }
            }
        });

        // Add the list to be expanded + a manager to expand it
        LinearLayoutManager summaryLayoutManager = new LinearLayoutManager(DiagnosisActivityModified.this);
        summaryAdapter = new SummaryAdapter(buildSummaryList());
        summaryListRV.setAdapter(summaryAdapter);
        summaryListRV.setLayoutManager(summaryLayoutManager);

        // Same for diagnosis tab
        diagnosisTabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diagnosisExpandedLayout.getVisibility() == View.GONE){
                    diagnosisExpandedLayout.setVisibility(View.VISIBLE);
                    diagnosisTabIcon.setImageResource(R.drawable.ic_sub);
                }else{
                    diagnosisExpandedLayout.setVisibility(View.GONE);
                    diagnosisTabIcon.setImageResource(R.drawable.ic_add);
                }
            }
        });

        LinearLayoutManager diagnosisLayoutManager = new LinearLayoutManager(DiagnosisActivityModified.this);
        diagnosisAdapter = new DiagnosisAdapter(buildDiagnosisList());
        diagnosisListRV.setAdapter(diagnosisAdapter);
        diagnosisListRV.setLayoutManager(diagnosisLayoutManager);

        // Inside of the expanded diagnosis tab, if we click on one of the diagnoses,
        // expand the tab and give instructions on what to do
        diagnosisAdapter.setOnItemClickListener(new DiagnosisAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Diagnosis diagnosis = buildDiagnosisList().get(position);
                String dia = diagnosis.getDiagnosis();
                showInstructions(dia);
//                Toast.makeText(DiagnosisActivity.this, dia, Toast.LENGTH_SHORT).show();
            }
        });

        // If we click "save and complete" then set PENDING to false and
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPending = "not pending";
                saveForm();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPending = "pending";
                saveForm();
            }
        });
    }

    private void showInstructions(String dia) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.assessment_layout);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        linearLayout_instruction = dialog.findViewById(R.id.diagnosis);
        txtDiagnosis = dialog.findViewById(R.id.txtDiagnosis);
        recyclerView = dialog.findViewById(R.id.recyclerView1);
        btnExit2 = dialog.findViewById(R.id.btnSave);
        btnContinue = dialog.findViewById(R.id.btnContinue);

        linearLayout_instruction.setBackgroundColor(getResources().getColor(R.color.severeDiagnosisColor));
        txtDiagnosis.setText("Diagnosis: " + dia);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        List<Assessment> assessments = new ArrayList<>();
        assessmentAdapter = new AssessmentAdapter(assessments);

//        messages2 = createList(dia);

        for (int i = 0; i < messageList.size(); i++){
            Assessment assessment = new Assessment((Integer) messageList.get(i));
            assessments.add(assessment);
        }
        recyclerView.setAdapter(assessmentAdapter);

        btnExit2.setVisibility(View.GONE);
        btnContinue.setText("Close");

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();

    }

    private List<Diagnosis> buildDiagnosisList() {
        List<Diagnosis> diagnosisList = new ArrayList<>();
        return diagnosisList;
    }

    private List<Assessment> buildSubItemList(String s) {
        List<Assessment> assessmentList = new ArrayList<>();

        for (int i=0; i < messageList.size(); i++) {
            Assessment assessment = new Assessment((Integer) messageList.get(i));
            assessmentList.add(assessment);
        }
        return assessmentList;
    }

    private List<Summary> buildSummaryList(){
        summaryList = new ArrayList<>();
        String pin = sharedPreferences.getString(PARENT_INITIALS, "");
        String weight = sharedPreferences.getString(KILO, "");
        String muac = sharedPreferences.getString(MUAC, "");

        addToSummaryList("Parent's initials", pin);
        addToSummaryList("Child's weight", weight);
        addToSummaryList("MUAC value", muac);

        Map<String, ?> all = sharedPreferences.getAll();
        System.out.println(all);
        for (Map.Entry<String, ?> x : all.entrySet()) {
            String question = x.getKey();
            if (question.substring(0, 2).equals("?:")) {
                addToSummaryList(question, (String) x.getValue());
            }
        }

        return summaryList;
    }

    private void addToSummaryList(String s, String pin) {
        if (!pin.isEmpty()){
            Summary summary = new Summary(s, pin);
            summaryList.add(summary);
        }
    }

    private void addToDiagnosisList(String s) {
        if (!s.isEmpty()){
            messages.add(s);
        }
    }

    private void saveForm() {

        String file = sharedPreferences.getString(FILENAME, "");

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
        String formattedDate = df.format(currentTime);

        Credentials credentials = new Credentials();
        String username = credentials.creds(DiagnosisActivityModified.this).getUsername();

        uniqueID = UUID.randomUUID().toString();

        if (file.isEmpty()){

            getDuration(currentTime);

            editor.putString(USERNAME, username);
            editor.putString(DATE, formattedDate);
            editor.putString(UUIDS, uniqueID);
            editor.putString(PENDING, isPending);
            editor.putString(ASSESS_INCOMPLETE, "complete");
            editor.apply();

            String filename = formattedDate + "_" + uniqueID;
            editor.putString(FILENAME, filename);
            editor.apply();
//            Toast.makeText(this, "empty", Toast.LENGTH_SHORT).show();
            doLogic(filename);
        }else {
            editor.putString(PENDING, isPending);
            editor.putString(ASSESS_INCOMPLETE, "complete");
            String filename = formattedDate + "_" + uniqueID;
            editor.putString(FILENAME, filename);
            editor.putString(DATE, formattedDate);
            editor.apply();

            getDuration2(currentTime);
//            Toast.makeText(this, "not empty", Toast.LENGTH_SHORT).show();
            doLogic(filename);
        }

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

    private void getDuration2(Date currentTime) {
        String initial_date = sharedPreferences.getString(INITIAL_DATE_2, "");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
        try {
            Date d1 = format.parse(initial_date);

            long diff = currentTime.getTime() - d1.getTime();//as given

            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            String duration = String.valueOf(minutes);
            editor.putString(DURATION_2, duration);
            editor.apply();
            Log.d("Difference in time", "getTimeDifference: " + minutes);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void doLogic(String file) {
        // Create a new shared preferences object + editor
        sharedPreferences1 = getSharedPreferences(file, Context.MODE_PRIVATE);
        editor1 = sharedPreferences1.edit();

        // Put all values from the current Shared Preferences object into the shared
        // preferences for the given file
        Map<String, ?> all = sharedPreferences.getAll();
        for (Map.Entry<String, ?> x : all.entrySet()) {
            if (x.getValue().getClass().equals(String.class))  editor1.putString(x.getKey(),  (String)x.getValue());
//            else if (x.getValue().getClass().equals(Boolean.class)) editor1.putBoolean(x.getKey(), (Boolean)x.getValue());
        }
        editor1.commit();
        editor.clear();
        editor.commit();

        String filename = sharedPreferences1.getString(SECOND, "");
        if (!filename.isEmpty()){
            filename = filename + ".xml";
            File src = new File("/data/data/" + BuildConfig.APPLICATION_ID + "/shared_prefs/" + filename);
            if (src.exists()){
                src.delete();
//                Toast.makeText(this, "filename", Toast.LENGTH_SHORT).show();
            }else{
//                Toast.makeText(this, "no file filename", Toast.LENGTH_SHORT).show();
            }
        }

        String bron = sharedPreferences1.getString(USED_BRONCHODILATOR, "");
        String fin = sharedPreferences1.getString(AFTER_BRONCHODILATOR, "");
        Intent intent;
        if (bron.equals("Bronchodialtor Given") && fin.isEmpty()){
            intent = new Intent(DiagnosisActivityModified.this, Dashboard.class);
        }else{
            if (isPending.equals("not pending")){
                intent = new Intent(DiagnosisActivityModified.this, FinalActivity.class);
                intent.putExtra("filename", file);
            }else {
                editor1.putString(SECOND, file);
                editor1.apply();
                intent = new Intent(DiagnosisActivityModified.this, Dashboard.class);
            }
        }

        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        if (intent.hasExtra("filename")){
            Bundle bundle = new Bundle();
            Intent intent2 = new Intent(this, PatientActivity.class);
            bundle.putInt("Fragment", 3);
            intent2.putExtras(bundle);
            startActivity(intent2);
            finish();
        }else{
            Toast.makeText(this, "Please click the Save button", Toast.LENGTH_SHORT).show();
        }

    }
}