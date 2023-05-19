package com.ug.air.alrite.Fragments.Patient;

import static com.ug.air.alrite.Fragments.Patient.RRCounter.SECOND;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ug.air.alrite.Activities.Dashboard;
import com.ug.air.alrite.R;
import com.ug.air.alrite.Utils.Credentials;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class InitialsModified extends Fragment {

   View view;
   EditText etChildInitials, etParentInitials, etStudy, etCode;
   Button back, next;
   String childInitials, parentInitials, formattedDate, studyId, code, h_code, counter, filename;
   public static final String CHILD_INITIALS = "patient_initials";
    public static final String  VERSION = "app_version";
   public static final String PARENT_INITIALS = "parent_initials";
   public static final String STUDY_ID = "study_id";
    public static final String STUDY_ID_2 = "study_id_2";
   public static final String INITIAL_DATE = "start_date";
   public static final String SHARED_PREFS = "sharedPrefs";
   SharedPreferences sharedPreferences, sharedPreferences1;
   SharedPreferences.Editor editor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_initials, container, false);

        etChildInitials = view.findViewById(R.id.cin);
        etCode = view.findViewById(R.id.code);
        etStudy = view.findViewById(R.id.studyId);
        etParentInitials = view.findViewById(R.id.pin);
        next = view.findViewById(R.id.next);
        back = view.findViewById(R.id.back);

        Intent intent = getActivity().getIntent();
        // If ____, then we can combine the data for
        // Otherwise, just get the current activity's shared preferences
        if (intent.hasExtra("filename")) {
            filename = intent.getExtras().getString("filename");
            sharedPreferences1 = requireActivity().getSharedPreferences(filename, Context.MODE_PRIVATE);
            sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            Map<String, ?> all = sharedPreferences1.getAll();
            for (Map.Entry<String, ?> x : all.entrySet()) {
                if (x.getValue().getClass().equals(String.class))  editor.putString(x.getKey(),  (String)x.getValue());
            }
            editor.commit();
            editor.putString(SECOND, filename);
            editor.apply();

        } else {
            sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        }

        // Get an editor for the shared preferences
        editor = sharedPreferences.edit();

        // Get the parent's initials and the child's initials and store them in
        // the fragment, if they exist: otherwise ""
        loadData();

        // Get the credentials for the current user, and set the current variables
        // to those values
        updateViews();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                childInitials = etChildInitials.getText().toString();
                parentInitials = etParentInitials.getText().toString();
                studyId = etStudy.getText().toString();

                if (childInitials.isEmpty() || parentInitials.isEmpty() || studyId.equals("0") || studyId.isEmpty()){
                    Toast.makeText(getActivity(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                }else {
                    saveData();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Dashboard.class));
            }
        });

        return view;
    }

    private void saveData() {

        String new_study = etCode.getText().toString();
        new_study = new_study + "_" + studyId;
        editor.putString(CHILD_INITIALS, childInitials);
        editor.putString(PARENT_INITIALS, parentInitials);
        editor.putString(VERSION, "2");
        editor.putString(STUDY_ID_2, studyId);
        editor.putString(STUDY_ID, new_study);
        if (formattedDate.isEmpty()){
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
            String formattedDate = df.format(currentTime);

            editor.putString(INITIAL_DATE, formattedDate);
        }
        editor.apply();

        int count = Integer.valueOf(counter);
        int count2 = Integer.valueOf(studyId);

//        Toast.makeText(getActivity(), count + " " + count2, Toast.LENGTH_SHORT).show();

        if (count == count2){
            count = count+1;
            String ct = String.valueOf(count);
            Credentials credentials = new Credentials();
            credentials.counting(getActivity(), ct);
        }

        FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
        fr.replace(R.id.fragment_container, new SexModified());
        fr.addToBackStack(null);
        fr.commit();
    }

    private void loadData() {
        parentInitials = sharedPreferences.getString(PARENT_INITIALS, "");
        childInitials = sharedPreferences.getString(CHILD_INITIALS, "");
        studyId = sharedPreferences.getString(STUDY_ID_2, "");
        formattedDate = sharedPreferences.getString(INITIAL_DATE, "");
    }

    private void updateViews() {
        etParentInitials.setText(parentInitials);
        etChildInitials.setText(childInitials);

        Credentials credentials = new Credentials();
        credentials.creds2(getActivity());
        code = credentials.creds2(getActivity()).getCode();
        h_code = credentials.creds2(getActivity()).getH_code();
        counter = credentials.creds2(getActivity()).getCounter();
        etCode.setText("AL"+h_code + "" + code);
        etCode.setEnabled(false);

        if (studyId.isEmpty()){
            etStudy.setText(counter);
        }else{
            etStudy.setText(studyId);
        }

    }

}