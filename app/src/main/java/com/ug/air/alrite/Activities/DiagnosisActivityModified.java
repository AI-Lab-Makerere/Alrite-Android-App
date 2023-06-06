package com.ug.air.alrite.Activities;

import static com.ug.air.alrite.Activities.PatientActivity.DIAGNOSES_ID;
import static com.ug.air.alrite.Activities.PatientActivity.SUMMARY_ID;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.DATE;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.DURATION;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.PATIENT_ASSESSMENT_ID;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.CLINICIAN_USERNAME;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.UUIDS;
import static com.ug.air.alrite.Fragments.Patient.InitialsModified.CHILD_INITIALS;
import static com.ug.air.alrite.Fragments.Patient.InitialsModified.INITIAL_DATE;
import static com.ug.air.alrite.Fragments.Patient.SexModified.AGE_IN_MONTHS;
import static com.ug.air.alrite.Fragments.Patient.SexModified.AGE_IN_YEARS;
import static com.ug.air.alrite.Fragments.Patient.SexModified.KILO;
import static com.ug.air.alrite.Fragments.Patient.SexModified.MUAC;
import static com.ug.air.alrite.Fragments.Patient.SexModified.SEX;

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
import com.ug.air.alrite.Utils.Credentials;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    SharedPreferences summaryPrefs, diagnosesPrefs;
    SharedPreferences.Editor summaryEditor, diagnosesEditor;
    String summaryPrefsID, diagnosesPrefsID;
    String ageInYears, ageInMonths, isPending;
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
        summaryPrefsID = getIntent().getStringExtra(SUMMARY_ID);
        summaryPrefs = getSharedPreferences(summaryPrefsID, Context.MODE_PRIVATE);
        summaryEditor = summaryPrefs.edit();
        diagnosesPrefsID = getIntent().getStringExtra(DIAGNOSES_ID);
        diagnosesPrefs = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        diagnosesEditor = diagnosesPrefs.edit();

        // Save the patient's age, for reference in the diagnosis instructions
        ageInMonths = summaryPrefs.getString(AGE_IN_MONTHS, "");

        // At the top of the page, display the patient's initials, age, and gender
        String initials = summaryPrefs.getString(CHILD_INITIALS, "");
        ageInYears = summaryPrefs.getString(AGE_IN_YEARS, "");
        String[] ageInYearsSplit = ageInYears.split("\\.");
        String gender = summaryPrefs.getString(SEX, "");
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

    /**
     * Create a list of all of the current diagnoses (in the future, this should
     * happen within the diagnosis activity)
     * TODO: implement gathering diagnosis in the editor, then in the app
     *
     * @return a list of diagnoses for the patient
     */
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
        String weight = summaryPrefs.getString(KILO, "");
        String muac = summaryPrefs.getString(MUAC, "");

        addToSummaryList("Child's weight", weight);
        addToSummaryList("MUAC value", muac);

        Map<String, ?> all = summaryPrefs.getAll();
        System.out.println(all);
        for (Map.Entry<String, ?> x : all.entrySet()) {
            // TODO: prevent non-summary results from showing
            addToSummaryList(x.getKey(), x.getValue().toString());
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

    // TODO: implement logic for revisiting page upon the use of a bronchodilator
    /**
     * After confirming the information on the diagnosis page, we complete the assessment
     * and move to the FinalActivity which confirms the collected information.
     */
    private void saveForm() {
        // We get the current date and time
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
        String formattedDate = df.format(currentTime);
        enterDurationIntoSharedPrefs(currentTime);

        // We get the current clinician's username
        Credentials credentials = new Credentials();
        String username = credentials.creds(DiagnosisActivityModified.this).getUsername();

        // Put the assessment info into the SharedPrefs
        summaryEditor.putString(CLINICIAN_USERNAME, username);
        summaryEditor.putString(DATE, formattedDate);
        summaryEditor.apply();

        // Go to the final activity, since we aren't using a bronchodilator and therefore
        // aren't revisiting this page (currently)
        startFinalActivity();
    }

    private void enterDurationIntoSharedPrefs(Date currentTime) {
        String initial_date = summaryPrefs.getString(INITIAL_DATE, "");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
        try {
            Date d1 = format.parse(initial_date);

            long diff = currentTime.getTime() - d1.getTime();//as given

            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            String duration = String.valueOf(minutes);
            summaryEditor.putString(DURATION, duration);
            summaryEditor.apply();
            Log.d("Difference in time", "getTimeDifference: " + minutes);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void startFinalActivity() {
        Intent finalActivityIntent = new Intent(DiagnosisActivityModified.this, FinalActivity.class);
        finalActivityIntent.putExtra(SUMMARY_ID, summaryPrefsID);
        finalActivityIntent.putExtra(DIAGNOSES_ID, diagnosesPrefsID);
        startActivity(finalActivityIntent);
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