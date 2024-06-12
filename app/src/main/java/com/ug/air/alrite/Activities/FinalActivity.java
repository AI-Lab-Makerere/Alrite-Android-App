package com.ug.air.alrite.Activities;

import static com.ug.air.alrite.Activities.DiagnosisActivityModified.SHARED_PREFS;
import static com.ug.air.alrite.Activities.PatientActivity.DIAGNOSES_ID;
import static com.ug.air.alrite.Activities.PatientActivity.SUMMARY_ID;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.ug.air.alrite.R;

import org.json.JSONObject;

import java.util.Map;

public class FinalActivity extends AppCompatActivity {

    CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7, checkBox8, checkBox9, checkBox10, checkBox11, checkBox12, checkBox13, checkBox14;
    EditText etOther1, etOther2;
    Button btnSave;
    String s1, s2, diagnosis, treatment;
    public static final String S6 = "clinician_treatment";
    public static final String S7 = "clinician_diagnosis";
    SharedPreferences summaryPrefs, diagnosesPrefs;
    SharedPreferences.Editor summaryEditor, diagnosesEditor;
    String summaryPrefsID, diagnosesPrefsID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        // Get an editor for our shared preferences
        summaryPrefsID = getIntent().getStringExtra(SUMMARY_ID);
        summaryPrefs = getSharedPreferences(summaryPrefsID, Context.MODE_PRIVATE);
        summaryEditor = summaryPrefs.edit();
        diagnosesPrefsID = getIntent().getStringExtra(DIAGNOSES_ID);
        diagnosesPrefs = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        diagnosesEditor = diagnosesPrefs.edit();

        btnSave = findViewById(R.id.btnExit);
        etOther1 = findViewById(R.id.otherText);
        etOther2 = findViewById(R.id.otherText2);
        checkBox1 = findViewById(R.id.severe);
        checkBox2 = findViewById(R.id.pneumonia);
        checkBox3 = findViewById(R.id.wheezing);
        checkBox4 = findViewById(R.id.cough);
        checkBox5 = findViewById(R.id.other);
        checkBox6 = findViewById(R.id.referral);
        checkBox7 = findViewById(R.id.antibiotics);
        checkBox8 = findViewById(R.id.inhaled);
        checkBox9 = findViewById(R.id.oral);
//        checkBox10 = findViewById(R.id.antimalarials);
        checkBox11= findViewById(R.id.steroids);
        checkBox12= findViewById(R.id.supportive);
        checkBox13 = findViewById(R.id.other2);
//        checkBox14 = findViewById(R.id.malaria);

        checkBox5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (checkBox5.isChecked()){
                    etOther1.setVisibility(View.VISIBLE);
                }else {
                    etOther1.setVisibility(View.GONE);
                    etOther1.setText("");
                }
            }
        });
        
        checkBox13.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (checkBox13.isChecked()){
                    etOther2.setVisibility(View.VISIBLE);
                }else {
                    etOther2.setVisibility(View.GONE);
                    etOther2.setText("");
                }
            }
        });
        
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diagnosis = etOther1.getText().toString();
                treatment = etOther2.getText().toString();
                if (etOther1.getVisibility()==View.VISIBLE && diagnosis.isEmpty()){
                    Toast.makeText(FinalActivity.this, "Please provide your diagnosis", Toast.LENGTH_SHORT).show();
                }else if(etOther2.getVisibility()==View.VISIBLE && treatment.isEmpty()){
                    Toast.makeText(FinalActivity.this, "Please provide your treatment", Toast.LENGTH_SHORT).show();
                }else {
                    checkedList();
                }
            }
        });

    }

    private void checkedList() {
        s1 = "";

        if(checkBox1.isChecked()){
            s1 += "Severe pneumonia or very severe disease, ";
        }
        if(checkBox2.isChecked()){
            s1 += "Pneumonia, ";
        }
        if(checkBox3.isChecked()){
            s1 += "Wheezing illness, ";
        }
        if(checkBox4.isChecked()){
            s1 += "Cough/Cold/No pneumonia, ";
        }
//        if(checkBox14.isChecked()){
//            s1 += "Malaria, ";
//        }
        if (!diagnosis.isEmpty()){
            s1 += diagnosis + ", ";
        }
        s1 = s1.replaceAll(", $", "");
        if (s1.isEmpty()){
            Toast.makeText(this, "Choose at least one diagnosis option", Toast.LENGTH_SHORT).show();
        }else {
            checkedList2();
        }
    }

    private void checkedList2() {
        s2 = "";

        if(checkBox6.isChecked()){
            s2 += "Referral, ";
        }
        if(checkBox7.isChecked()){
            s2 += "Antibiotics, ";
        }
        if(checkBox8.isChecked()){
            s2 += "Inhaled salbutamol, ";
        }
        if(checkBox9.isChecked()){
            s2 += "Oral salbutamol, ";
        }
//        if(checkBox10.isChecked()){
//            s2 += "Antimalarials, ";
//        }
        if(checkBox11.isChecked()){
            s2 += "Systemic steroids, ";
        }
        if(checkBox12.isChecked()){
            s2 += "Supportive care, ";
        }
        if (!treatment.isEmpty()){
            s2 += treatment + ", ";
        }
        s2 = s2.replaceAll(", $", "");
        if (s2.isEmpty()){
            Toast.makeText(this, "Choose at least one treatment option", Toast.LENGTH_SHORT).show();
        }else {
            saveData();
        }
    }

    private void saveData() {
        diagnosesEditor.putString(S7, s1);
        diagnosesEditor.putString(S6, s2);
        diagnosesEditor.commit();

        // Finally, we can exit: go back to the dashboard home page
        startActivity(new Intent(FinalActivity.this, Dashboard.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please click the Save and Exit button", Toast.LENGTH_SHORT).show();
    }
}