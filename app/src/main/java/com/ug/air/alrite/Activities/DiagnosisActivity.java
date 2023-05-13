package com.ug.air.alrite.Activities;

import static com.ug.air.alrite.Activities.FinalActivity.S6;
import static com.ug.air.alrite.Activities.FinalActivity.S7;
import static com.ug.air.alrite.Activities.PatientActivity.ASSESS_INCOMPLETE;
import static com.ug.air.alrite.Fragments.Patient.Allergies.CHOICEY2;
import static com.ug.air.alrite.Fragments.Patient.Assess.FINAL_DIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.Assess.SEVERE_SYMPTOMS;
import static com.ug.air.alrite.Fragments.Patient.Breathless.S5;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.BRONCHODILATOR_WAS_GIVEN;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.USED_BRONCHODILATOR;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.DATE;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.DURATION;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.FILENAME;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.USERNAME;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.UUIDS;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator2.BDIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator2.REASON;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator3.B3DIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator3.AFTER_BRONCHODILATOR;
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
import static com.ug.air.alrite.Fragments.Patient.Sex.SEX;
import static com.ug.air.alrite.Fragments.Patient.Sex.KILO;
import static com.ug.air.alrite.Fragments.Patient.Sex.MDIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.Sex.MUAC;
import static com.ug.air.alrite.Fragments.Patient.Smoke.CHOICET1;
import static com.ug.air.alrite.Fragments.Patient.Stridor.CHOICE6;
import static com.ug.air.alrite.Fragments.Patient.Temperature.TDIAGNOSIS;
import static com.ug.air.alrite.Fragments.Patient.Temperature.TEMP;
import static com.ug.air.alrite.Fragments.Patient.WheezD.CHOICEX;
import static com.ug.air.alrite.Fragments.Patient.WheezY.DAY2;
import static com.ug.air.alrite.Fragments.Patient.Wheezing.WHEEZING_SOUNDS;
import static com.ug.air.alrite.Fragments.Patient.Wheezing.CHOICE82;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class DiagnosisActivity extends AppCompatActivity {

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

        // If we have "filename" then we should get the SharedPreferences object
        // associated with the filename
        Intent intent = getIntent();
        if (intent.hasExtra("filename")){
            folder = intent.getExtras().getString("filename");
            sharedPreferences = getSharedPreferences(folder, Context.MODE_PRIVATE);

            String pending = sharedPreferences.getString(PENDING, "");
//            String incomplete = sharedPreferences.getString(INCOMPLETE, "");

            // If we've opened this page before and indicated that we don't want
            // to exit, then this time we'll be forced to exit.
            // If we indicated last time that we want to save and exit, then make
            // sure that we can't press either of those buttons again
            if (pending.equals("pending")){
                btnSave.setVisibility(View.GONE);
                btnExit.setVisibility(View.VISIBLE);
            } else{
                btnSave.setVisibility(View.GONE);
                btnExit.setVisibility(View.GONE);
            }

        // If we don't have "filename" then we should get the generic shared preferences,
        // and then check in on what we entered for the bronchodilator
        }else{
            sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            String bron = sharedPreferences.getString(USED_BRONCHODILATOR, "");
            String fin = sharedPreferences.getString(AFTER_BRONCHODILATOR, "");

            // If we used a bronchodilator on the patient, but we haven't checked
            // in on if it worked or not, then set buttons as visible/not visible
            if (bron.equals(BRONCHODILATOR_WAS_GIVEN) && fin.isEmpty()){
                btnSave.setVisibility(View.GONE);
            }else {
                btnSave.setVisibility(View.VISIBLE);
            }
            btnExit.setVisibility(View.VISIBLE);
        }

        // Get an editor for our shared preferences
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
        LinearLayoutManager summaryLayoutManager = new LinearLayoutManager(DiagnosisActivity.this);
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

        LinearLayoutManager diagnosisLayoutManager = new LinearLayoutManager(DiagnosisActivity.this);
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
        createList(dia);

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

        String mDiagnosis = sharedPreferences.getString(MDIAGNOSIS, "");
        String oneDiagnosis = sharedPreferences.getString(FINAL_DIAGNOSIS, "");
        String tDiagnosis = sharedPreferences.getString(TDIAGNOSIS, "");
//        String oxDiagnosis = sharedPreferences.getString(OXDIAGNOSIS, "");
        String noDiagnosis = sharedPreferences.getString(NODIAGNOSIS, "");
        String hDiagnosis = sharedPreferences.getString(HDIAGNOSIS, "");
//        String stDiagnosis = sharedPreferences.getString(STDIAGNOSIS, "");
//        String gnDiagnosis = sharedPreferences.getString(GNDIAGNOSIS, "");
        String ciDiagnosis = sharedPreferences.getString(CIDIAGNOSIS, "");
        String wDiagnosis = sharedPreferences.getString(BDIAGNOSIS, "");
        String asDiagnosis = sharedPreferences.getString(ADIAGNOSIS, "");
        String tuDiagnosis = sharedPreferences.getString(TUDIAGNOSIS, "");
        String b3Diagnosis = sharedPreferences.getString(B3DIAGNOSIS, "");
        String ftDiagnosis = sharedPreferences.getString(FTDIAGNOSIS, "");

//        if (!aDiagnosis.isEmpty() || !oxDiagnosis.isEmpty() || !stDiagnosis.isEmpty() || !gnDiagnosis.isEmpty()){
//            addToList2("Severe Pneumonia OR very Severe Disease");
//        }

        addToDiagnosisList(oneDiagnosis);
        addToDiagnosisList(mDiagnosis);
        addToDiagnosisList(tDiagnosis);
//        addToList2(oxDiagnosis);
        addToDiagnosisList(noDiagnosis);
        addToDiagnosisList(hDiagnosis);
//        addToList2(stDiagnosis);
//        addToList2(gnDiagnosis);
        addToDiagnosisList(ciDiagnosis);
        addToDiagnosisList(wDiagnosis);
        addToDiagnosisList(asDiagnosis);
        addToDiagnosisList(tuDiagnosis);
        addToDiagnosisList(b3Diagnosis);
        addToDiagnosisList(ftDiagnosis);


        for (int i=0; i < messages.size(); i++) {
            Diagnosis diagnosis = new Diagnosis(messages.get(i), buildSubItemList(messages.get(i)));
            diagnosisList.add(diagnosis);
        }
        return diagnosisList;
    }

    private List<Assessment> buildSubItemList(String s) {
        List<Assessment> assessmentList = new ArrayList<>();

        createList(s);

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
        String assess = sharedPreferences.getString(SEVERE_SYMPTOMS, "");
        String temp = sharedPreferences.getString(TEMP, "");
        String feb = sharedPreferences.getString(TOUCH, "");
        String ox = sharedPreferences.getString(OXY, "");
        String wheez = sharedPreferences.getString(WHEEZING_SOUNDS, "");
        String wheez2 = sharedPreferences.getString(CHOICE82, "");
        String cough = sharedPreferences.getString(DIFFICULTY_BREATHING, "");
        String hiv = sharedPreferences.getString(CHOICE3, "");
        String care = sharedPreferences.getString(CHOICEHC, "");
        String coughD = sharedPreferences.getString(DAY1, "");
        String fastbreathing = sharedPreferences.getString(FASTBREATHING, "");
        String stidor = sharedPreferences.getString(CHOICE6, "");
//        Boolean steth = sharedPreferences.getBoolean(CHECKSTETHO, false);
        String nasal = sharedPreferences.getString(CHOICEGN, "");
        String chest = sharedPreferences.getString(CHOICE7, "");
        String chest2 = sharedPreferences.getString(CHOICE72, "");
        String bronch = sharedPreferences.getString(USED_BRONCHODILATOR, "");
        String reason = sharedPreferences.getString(REASON, "");
        String wheezD = sharedPreferences.getString(CHOICEX, "");
        String wheezY = sharedPreferences.getString(DAY2, "");
        String breathless = sharedPreferences.getString(S5, "");
        String eczema = sharedPreferences.getString(CHOICEX2, "");
        String allergies = sharedPreferences.getString(CHOICEY2, "");
        String smoke = sharedPreferences.getString(CHOICET1, "");
        String kerosene = sharedPreferences.getString(CHOICET2, "");
        String fastbreathing2 = sharedPreferences.getString(FASTBREATHING2, "");
        String better = sharedPreferences.getString(AFTER_BRONCHODILATOR, "");
        String point1 = sharedPreferences.getString(POINT, "");
        String point2 = sharedPreferences.getString(POINT2, "");
        String diagnosis = sharedPreferences.getString(S7, "");
        String treatment = sharedPreferences.getString(S6, "");

        addToSummaryList("Parent's initials", pin);
        addToSummaryList("Child's weight", weight);
        addToSummaryList("MUAC value", muac);
        addToSummaryList("Danger Signs", assess);
        addToSummaryList("Child Coughing", cough);
        addToSummaryList("Days coughing", coughD);
        addToSummaryList("Temperature", temp);
        addToSummaryList("Febrile to touch", feb);
        addToSummaryList("HIV Status", hiv);
        addToSummaryList("Child in HIV Care", care);
        addToSummaryList("Oxgyen Saturation", ox);
        addToSummaryList("Respiratory Rate", fastbreathing);
        addToSummaryList("Child has Stridor", stidor);
        addToSummaryList("Child Wheezing", wheez);
//        addToList("Stethoscope was used", String.valueOf(steth));
        addToSummaryList("Child has grunting or nasal flaring", nasal);
        addToSummaryList("Child has chest indrawing", chest);
        addToSummaryList("Respiratory score", point1);
        addToSummaryList("Bronchodilator", bronch);
        addToSummaryList("Reason", reason);
        addToSummaryList("Respiratory Rate (After bronchodilator)", fastbreathing2);
        addToSummaryList("Child Wheezing", wheez2);
        addToSummaryList("Child has chest indrawing", chest2);
        addToSummaryList("Child's breathing after bronchodilator", better);
        addToSummaryList("Respiratory score 2", point2);
        addToSummaryList("Child has breathing difficulty", wheezD);
        addToSummaryList("Episodes in the past year", wheezY);
        addToSummaryList("Child his breathless", breathless);
        addToSummaryList("Child has Eczema", eczema);
        addToSummaryList("Child's family has Allergies", allergies);
        addToSummaryList("Any family member smoking tobacco", smoke);
        addToSummaryList("Any family member using kerosene", kerosene);
        addToSummaryList("Clinician's diagnosis", diagnosis);
        addToSummaryList("Clinician's treatment", treatment);

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

    private void createList(String s) {
        String weight = sharedPreferences.getString(KILO, "");
        int ag = Integer.parseInt(ageInMonths);

        if (s.equals("Severe Pneumonia OR very Severe Disease")){
            String st = sharedPreferences.getString(SEVERE_SYMPTOMS, "");
            Instructions instructions = new Instructions();
            messageList = instructions.GetInstructions(ag, weight, st);
        }
        else if (s.equals("Severe acute malnutrition")){
            messageList = Arrays.asList(R.string.muac, R.string.muac1,
                    R.string.muac2, R.string.muac3, R.string.muac4, R.string.muac5);
        }
        else if (s.equals("Moderate acute malnutrition")){
            messageList = Arrays.asList(R.string.muac6, R.string.muac7);
        }
        else if (s.equals("Fever without danger signs")){
            if (!weight.isEmpty()) {
                float we = Float.parseFloat(weight);
                if (we >= 4.0 && we < 14.0) {
                    messageList = Arrays.asList(R.string.febril3, R.string.paracetamol2, R.string.febril5,
                            R.string.febril6, R.string.febril7);
                }else if (we >= 14.0) {
                    messageList = Arrays.asList(R.string.febril3, R.string.paracetamol3, R.string.febril5,
                            R.string.febril6, R.string.febril7);
                }else {
                    if (ag >= 2 && ag < 36){
                        messageList = Arrays.asList(R.string.febril3, R.string.paracetamol2, R.string.febril5,
                                R.string.febril6, R.string.febril7);
                    }else  if (ag >= 36 && ag < 60){
                        messageList = Arrays.asList(R.string.febril3, R.string.paracetamol3, R.string.febril5,
                                R.string.febril6, R.string.febril7);
                    }
                }
            } else {
                if (ag >= 2 && ag < 36){
                    messageList = Arrays.asList(R.string.febril3, R.string.paracetamol2, R.string.febril5,
                            R.string.febril6, R.string.febril7);
                }else  if (ag >= 36 && ag < 60){
                    messageList = Arrays.asList(R.string.febril3, R.string.paracetamol3, R.string.febril5,
                            R.string.febril6, R.string.febril7);
                }
            }

        }
        else if (s.equals("Very severe febrile illness")){
            Instructions instructions = new Instructions();
            messageList = instructions.GetFebrilInstructions(ag, weight);
        }
        else if (s.equals("No signs of Pneumonia or Wheezing illness")){
            messageList = Arrays.asList(R.string.selected, R.string.alrite, R.string.no_anti, R.string.other_illness);
        }
        else if (s.contains("HIV-Infected")){
            String care = sharedPreferences.getString(CHOICEHC, "");
            String chest = sharedPreferences.getString(CHOICE7, "");

            Instructions instructions = new Instructions();
            messageList = instructions.GetHIVInfected(care, chest, ag, weight);

        }
        else if (s.contains("HIV-Exposed")){
            String care = sharedPreferences.getString(CHOICEHC, "");
            String chest = sharedPreferences.getString(CHOICE7, "");

            Instructions instructions = new Instructions();
            messageList = instructions.GetHIVExposed(care, chest, ag, weight);

        }
        else if (s.equals("Pneumonia")){
            Instructions instructions = new Instructions();
            messageList = instructions.GetPneumoniaInstructions(ag, weight);
        }
        else if (s.equals("Cough/Cold/No Pneumonia")){
            messageList = Arrays.asList(R.string.cold1, R.string.cold2, R.string.cold3, R.string.cold4);
        }
        else if (s.equals("Wheezing (not clear Bronchodilator response)")){
            if (ag < 24){
                messageList = Arrays.asList(R.string.wheez_ill1, R.string.wheez_ill2, R.string.wheez_ill3,
                        R.string.wheez_ill4, R.string.wheez_ill5, R.string.wheez_ill6);
            }else {
                messageList = Arrays.asList(R.string.wheez_ill1, R.string.wheez_ill2, R.string.wheez_ill7,
                        R.string.wheez_ill8, R.string.wheez_ill9);
            }
        }
        else if (s.equals("Asthma Risk")){
            messageList = Arrays.asList(R.string.asthma1, R.string.wheez3, R.string.asthma2, R.string.asthma3);
        }
        else if (s.equals("Tuberculosis Risk")){
            messageList = Arrays.asList(R.string.tuber1, R.string.tuber2);
        }
        else if (s.equals("Wheezing illness (Bronchodilator response)")){
            messageList = Arrays.asList(R.string.wheez_ill00, R.string.wheez_ill01, R.string.wheez_ill71,
                    R.string.wheez_ill72, R.string.wheez_ill8, R.string.wheez_ill9);
        }else if (s.equals("Severe Disease")){
            messageList = Collections.singletonList(R.string.bronc1x);
        }

    }

    private void saveForm() {

        String file = sharedPreferences.getString(FILENAME, "");

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
        String formattedDate = df.format(currentTime);

        Credentials credentials = new Credentials();
        String username = credentials.creds(DiagnosisActivity.this).getUsername();

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
            intent = new Intent(DiagnosisActivity.this, Dashboard.class);
        }else{
            if (isPending.equals("not pending")){
                intent = new Intent(DiagnosisActivity.this, FinalActivity.class);
                intent.putExtra("filename", file);
            }else {
                editor1.putString(SECOND, file);
                editor1.apply();
                intent = new Intent(DiagnosisActivity.this, Dashboard.class);
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