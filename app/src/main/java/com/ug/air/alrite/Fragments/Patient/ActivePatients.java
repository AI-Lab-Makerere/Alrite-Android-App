package com.ug.air.alrite.Fragments.Patient;


import static com.ug.air.alrite.Activities.PatientActivity.ASSESS_INCOMPLETE;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.BRONCHODILATOR_WAS_GIVEN;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.USED_BRONCHODILATOR;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.DATE;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.REASSESS;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator2.REASON;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator3.AFTER_BRONCHODILATOR;
import static com.ug.air.alrite.Fragments.Patient.Initials.CHILD_INITIALS;
import static com.ug.air.alrite.Fragments.Patient.Initials.PARENT_INITIALS;
import static com.ug.air.alrite.Fragments.Patient.Sex.AGE_IN_YEARS;
import static com.ug.air.alrite.Fragments.Patient.Sex.SEX;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ug.air.alrite.Adapters.PatientAdapter;
import com.ug.air.alrite.BuildConfig;
import com.ug.air.alrite.Models.Item;
import com.ug.air.alrite.Models.Patient;
import com.ug.air.alrite.R;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ActivePatients extends Fragment {

    View view;
    RecyclerView rvActivePatients;
    EditText etSearch;
    ImageView buttonBack;
    ArrayList<Item> patientsList;
    PatientAdapter patientAdapter;
    String childInitials, parentInitials, gender, age, search, date;
    ArrayList<String> pToReassessInitialsList, files, patientsToReassessList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_active_patients, container, false);

        rvActivePatients = view.findViewById(R.id.recyclerView3);
        etSearch = view.findViewById(R.id.search);
        buttonBack = view.findViewById(R.id.back);

//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), Dashboard.class));
//            }
//        });

        buttonBack.setVisibility(View.GONE);

        rvActivePatients.setHasFixedSize(true);
        rvActivePatients.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        patientsList = new ArrayList<>();
        files = new ArrayList<String>();

        accessSharedFile();

        etSearch.addTextChangedListener(textWatcher);

        patientAdapter = new PatientAdapter(getActivity(), patientsList);
        rvActivePatients.setAdapter(patientAdapter);

        patientAdapter.setOnItemClickListener(new PatientAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Patient patient = (Patient) patientsList.get(position).getObject();
                String name = patient.getFilename();
                boolean reassess = patient.isReassess();
                if (reassess){
                    Bundle bundle = new Bundle();
                    bundle.putString("fileName", name);
                    RRCounter rrCounter = new RRCounter();
                    rrCounter.setArguments(bundle);
                    FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                    fr.replace(R.id.fragment_container, rrCounter);
                    fr.addToBackStack(null);
                    fr.commit(); 
                }else {
                    Toast.makeText(getActivity(), "Patient not ready for reassessment", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            search = etSearch.getText().toString();
            if (!search.isEmpty()) {
                patientsList.clear();
                for(String type : pToReassessInitialsList){
                    String ty = type.toLowerCase();
                    if (ty.contains(search)){
                        int index = pToReassessInitialsList.indexOf(type);
                        String fileName = patientsToReassessList.get(index);
                        sharedPreferences = requireActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE);
                        childInitials = sharedPreferences.getString(CHILD_INITIALS, "");
                        parentInitials = sharedPreferences.getString(PARENT_INITIALS, "");
                        age = sharedPreferences.getString(AGE_IN_YEARS, "");
                        gender = sharedPreferences.getString(SEX, "");
                        date = sharedPreferences.getString(DATE, "");
                        boolean reassess = sharedPreferences.getBoolean(REASSESS, false);
                        String[] split = age.split("\\.");
                        String ag = split[0] + " years and " + split[1] + " months";
                        try {
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
                            Date date = df.parse(ActivePatients.this.date);
                            SimpleDateFormat df1 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
                            String formattedDate = df1.format(date);
                            Patient patient = new Patient("Age: " + ag, "Gender: " + gender, childInitials, "Parent/Guardian: " + parentInitials, formattedDate, fileName, reassess);
                            patientsList.add(new Item(0, patient));
//                            patientAdapter.notifyDataSetChanged();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
//                    patientAdapter.notifyDataSetChanged();
                }
                patientAdapter.notifyDataSetChanged();
            }else {
                patientsList.clear();
                accessSharedFile();
                patientAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     *
     */
    private void accessSharedFile() {
        // Get the shared preferences folder on the current device
        File sharedPrefsFolder = new File("/data/data/" + BuildConfig.APPLICATION_ID + "/shared_prefs");
        if (sharedPrefsFolder.exists()){
            File[] sharedPrefsFiles = sharedPrefsFolder.listFiles();
//        Toast.makeText(getActivity(), "" + contents, Toast.LENGTH_SHORT).show();
            if (sharedPrefsFiles.length != 0) {
                pToReassessInitialsList = new ArrayList<String>();
                patientsToReassessList = new ArrayList<String>();

                // Go through each file in the shared preferences folder
                for (File f : sharedPrefsFiles) {
                    if (f.isFile()) {
                        String filename = f.getName().toString();
                        if (!filename.equals("sharedPrefs.xml") && !filename.equals("counter_file.xml")){
                            // Get a file from the folder for a patient, and get the name itself
                            // and the shared preferences object + editor for that file
                            String patientFilename = filename.replace(".xml", "");
                            sharedPreferences = requireActivity().getSharedPreferences(patientFilename, Context.MODE_PRIVATE);
                            editor = sharedPreferences.edit();

                            // Look in the shared preferences for if we used a bronchodilator
                            // or not, and how it turned out if we did, and if we finished
                            // the assessment or not.
                            String givenBronch = sharedPreferences.getString(USED_BRONCHODILATOR, "");
                            String afterBronch = sharedPreferences.getString(AFTER_BRONCHODILATOR, "");
                            String assessIncomplete = sharedPreferences.getString(ASSESS_INCOMPLETE, "");

                            // If we gave the bronchodilator but never checked final condition,
                            //
                            if (givenBronch.equals(BRONCHODILATOR_WAS_GIVEN) && afterBronch.isEmpty()){
                                patientsToReassessList.add(patientFilename);
                                childInitials = sharedPreferences.getString(CHILD_INITIALS, "");
                                parentInitials = sharedPreferences.getString(PARENT_INITIALS, "");
                                age = sharedPreferences.getString(AGE_IN_YEARS, "");
                                gender = sharedPreferences.getString(SEX, "");
                                date = sharedPreferences.getString(DATE, "");
                                boolean reassess = sharedPreferences.getBoolean(REASSESS, false);
                                pToReassessInitialsList.add(childInitials);
                                String[] split = age.split("\\.");
                                String ag = split[0] + " years and " + split[1] + " months";
                                try {
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
                                    Date date = df.parse(this.date);
                                    SimpleDateFormat df1 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
                                    String formattedDate = df1.format(date);

                                    Date currentTime = Calendar.getInstance().getTime();
                                    long diff = currentTime.getTime() - date.getTime();
                                    long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                                    if (minutes >= 15 && minutes < 240){
                                        editor.putBoolean(REASSESS, true);
                                        editor.apply();
                                        Patient patient = new Patient("Age: " + ag, "Gender: " + gender, childInitials, "Parent/Guardian: " + parentInitials, formattedDate, patientFilename, reassess);
                                        patientsList.add(new Item(0, patient));
                                    }else if (minutes >= 240){
                                        editor.putString(USED_BRONCHODILATOR, "Bronchodialtor Not Given");
                                        editor.putString(REASON, "A 4 hour time period elapsed");
                                        editor.apply();
                                    }else {
                                        Patient patient = new Patient("Age: " + ag, "Gender: " + gender, childInitials, "Parent/Guardian: " + parentInitials, formattedDate, patientFilename, reassess);
                                        patientsList.add(new Item(0, patient));
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

            }else{
                Toast.makeText(getActivity(), "There no patients' records", Toast.LENGTH_SHORT).show();

            }
        }
    }

}