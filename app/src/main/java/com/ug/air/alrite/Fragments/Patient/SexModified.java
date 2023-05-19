package com.ug.air.alrite.Fragments.Patient;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ug.air.alrite.Activities.PatientActivity;
import com.ug.air.alrite.R;
import com.ug.air.alrite.Utils.XML.ItemFactory;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Objects;


public class SexModified extends Fragment {
    View view;
    EditText etYears, etKilo, etKilo2, etMonths, etMuac;
    Button buttonBack, buttonNext;
    LinearLayout linearLayout;
    RadioGroup rgSex;
    RadioButton rbMale, rbFemale;
    String ageInMonths, ageInYearsAndMonths, weight, text, kg, fileName, months, years, muac, diagnosis, score, message;
    Spinner spinner;
    Dialog dialog;
    String value_sex = "none";
    private static final int YES = 0;
    private static final int NO = 1;
    public static final String MDIAGNOSIS = "diagnosis_malnutrition";
    public static final String AGE_IN_MONTHS = "age";
    public static final String AGE_IN_YEARS = "age2";
    public static final String KILO = "weight";
    public static final String MUAC = "muac";
    public static final String SEX = "gender";
    public static final String SHARED_PREFS = "sharedPrefs";
    SharedPreferences sharedPreferences, sharedPreferences1;
    SharedPreferences.Editor editor;
    Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sex, container, false);

        buttonNext = view.findViewById(R.id.next);
        buttonBack = view.findViewById(R.id.back);
        rgSex = view.findViewById(R.id.radioGroup);
        rbMale = view.findViewById(R.id.yes);
        rbFemale = view.findViewById(R.id.no);
        etYears = view.findViewById(R.id.years);
        etMonths = view.findViewById(R.id.months);
        etKilo = view.findViewById(R.id.kg1);
        etMuac = view.findViewById(R.id.muac);
        linearLayout = view.findViewById(R.id.linearMUAC);

        rgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = rgSex.findViewById(checkedId);
                int index = rgSex.indexOfChild(radioButton);

                switch (index) {
                    case YES:
                        value_sex = "Male";
                        break;
                    case NO:
                        value_sex = "Female";
                        break;

                    default:
                        break;
                }
            }
        });

        etKilo.addTextChangedListener(textListenerWeight);
        etYears.addTextChangedListener(textListenerYears);
        etMonths.addTextChangedListener(textListenerMonths);
        etMuac.addTextChangedListener(textListenerMUAC);


        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        loadData();
        updateViews();

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                years = etYears.getText().toString();
                months = etMonths.getText().toString();
                kg = etKilo.getText().toString();
                muac = etMuac.getText().toString();
//                kg2 = etKilo2.getText().toString();

                if (value_sex.equals("none") || years.isEmpty() || months.isEmpty()){
                    Toast.makeText(getActivity(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                }else{
                    int year = Integer.parseInt(years);
                    int ageInMos = (year*12) + Integer.parseInt(months);
                    ageInMonths = String.valueOf(ageInMos);
                    ageInYearsAndMonths = years + "." + months;
                    weight = kg;
                    saveData();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ((PatientActivity) Objects.requireNonNull(getActivity())).getJSONFromBackend();
                    }
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container, new InitialsModified());
                fr.commit();
            }
        });

        return view;
    }

    public TextWatcher textListenerWeight = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            kg = etKilo.getText().toString();
            if (!kg.isEmpty()){
                float k1 = Float.parseFloat(kg);
                if (k1 > 30.0){
                    etKilo.setError("The maximum kilograms for a child has to be 30.0kgs");
                    buttonNext.setEnabled(false);
                }else if (k1 < 0.5){
                    etKilo.setError("The minimum kilograms for a child has to be 0.5kgs");
                    buttonNext.setEnabled(false);
                }else {
                    buttonNext.setEnabled(true);
                }

            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public TextWatcher textListenerMUAC = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            muac = etMuac.getText().toString();
            if (!muac.isEmpty()){
                float k1 = Float.parseFloat(muac);
                if (k1 > 40.0){
                    etMuac.setError("The maximum value for MUAC has to be 40.0cm");
                    buttonNext.setEnabled(false);
                }else if (k1 < 9.0){
                    etMuac.setError("The minimum value for MUAC has to be 9.0cm");
                    buttonNext.setEnabled(false);
                }else {
                    buttonNext.setEnabled(true);
                }
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public TextWatcher textListenerYears = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            years = etYears.getText().toString();
            if (!years.isEmpty()){
                int yr = Integer.parseInt(years);
                if (yr > 4){
                    etYears.setError("The maximum number of years should be 4");
                    buttonNext.setEnabled(false);
                }else {
                    buttonNext.setEnabled(true);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public TextWatcher textListenerMonths = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            months = etMonths.getText().toString();
            if (!months.isEmpty()){
                int mt = Integer.parseInt(months);
                if (mt > 11){
                    etMonths.setError("The maximum number of months should be 11");
                    buttonNext.setEnabled(false);
                }else {
                    buttonNext.setEnabled(true);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void saveData() {
        diagnosis = "";
        if(!weight.isEmpty()){
            editor.putString(KILO, weight);
            editor.apply();
            float we = Float.parseFloat(weight);
            ItemFactory itemFactory = new ItemFactory();
            try {
                if (value_sex.equals("Male")){
                    score = itemFactory.GetMaleItem(requireActivity(), ageInMonths, we);
                }else{
                    score = itemFactory.GetFemaleItem(requireActivity(), ageInMonths, we);
                }
                makeDecisions();

            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
        }else{
            editor.remove(KILO);
            moveToNext();
        }

    }

    private void loadData() {
        ageInMonths = sharedPreferences.getString(AGE_IN_MONTHS, "");
        ageInYearsAndMonths = sharedPreferences.getString(AGE_IN_YEARS, "");
        muac = sharedPreferences.getString(MUAC, "");
        weight = sharedPreferences.getString(KILO, "");
        value_sex = sharedPreferences.getString(SEX, "");
    }

    private void updateViews() {
        if (value_sex.equals("Male")){
            rbMale.setChecked(true);
        }else if (value_sex.equals("Female")){
            rbFemale.setChecked(true);
        }else {
            rbMale.setChecked(false);
            rbFemale.setChecked(false);
        }

        if (!weight.isEmpty()){
            etKilo.setText(weight);
        }
        if (!muac.isEmpty()){
            etMuac.setText(muac);
        }

        if (!ageInYearsAndMonths.isEmpty()) {
            String[] separated = ageInYearsAndMonths.split("\\.");
            etYears.setText(separated[0]);
            etMonths.setText(separated[1]);
        }
    }

    private void makeDecisions() {
        if (score.equals("acceptable")){
            moveToNext();
        }else if (score.equals("above 2")){
            message = "This child’s weight is greater than 2 SD, above average. Are you sure you entered the right weight?";
            diagnosis = "none";
            showDialog(message);
        }else if (score.equals("below 2")){
            message = "This child’s weight is less than 2 SD, below average. The child may have Moderate Acute Malnutrition.. Are you sure you entered the right weight?";
            diagnosis = "Moderate acute malnutrition";
            showDialog(message);
        }else {
            message = "This child’s weight is less than 3 SD, below average. The child may have Severe Acute Malnutrition.. Are you sure you entered the right weight?";
            diagnosis = "Severe acute malnutrition";
            showDialog(message);
        }
    }

    private void showDialog(String message) {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.assess);
        dialog.setCancelable(true);

        TextView txtMessage = dialog.findViewById(R.id.message);
        Button btnSave = dialog.findViewById(R.id.ContinueButton);
        Button btnNo = dialog.findViewById(R.id.cancel);

        btnSave.setText("Yes");
        btnNo.setVisibility(View.VISIBLE);

        txtMessage.setText(message);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (!diagnosis.equals("none")){
                    editor.putString(MDIAGNOSIS, diagnosis);
                    editor.apply();
                }
                moveToNext();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                etKilo.setText("");
                etKilo.requestFocus();
                Toast.makeText(getActivity(), "Please check your scale and enter the weight again", Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }

    private void moveToNext() {
        if (!muac.isEmpty()){
            editor.putString(MUAC, muac);
            if (diagnosis.isEmpty() || diagnosis.equals("none")){
                float mw = Float.parseFloat(muac);
                if (mw < 11.5){
                    diagnosis = "Severe Acute malnutrition";
                }else if (mw >= 11.5 && mw <= 12.4){
                    diagnosis = "Moderate acute malnutrition";
                }else {
                    diagnosis = "none";
                }

                if (!diagnosis.equals("none")){
                    editor.putString(MDIAGNOSIS, diagnosis);
                }
            }
        }

        editor.putString(AGE_IN_MONTHS, ageInMonths);
        editor.putString(AGE_IN_YEARS, ageInYearsAndMonths);
        editor.putString(SEX, value_sex);
        editor.apply();
    }
}