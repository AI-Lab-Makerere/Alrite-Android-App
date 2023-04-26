package com.ug.air.alrite.Activities;

import static com.ug.air.alrite.Activities.DiagnosisActivity.DATE_2;
import static com.ug.air.alrite.Activities.DiagnosisActivity.DURATION_2;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.DATE;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.DURATION;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.FILENAME;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.USERNAME;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.UUIDS;
import static com.ug.air.alrite.Fragments.Patient.Initials.CIN;
import static com.ug.air.alrite.Fragments.Patient.Initials.INITIAL_DATE;
import static com.ug.air.alrite.Fragments.Patient.Initials.PIN;
import static com.ug.air.alrite.Fragments.Patient.RRCounter.FASTBREATHING2;
import static com.ug.air.alrite.Fragments.Patient.RRCounter.INITIAL_DATE_2;
import static com.ug.air.alrite.Fragments.Patient.RRCounter.SECOND;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.makeramen.roundedimageview.BuildConfig;
import com.ug.air.alrite.Fragments.Patient.ActivePatients;
import com.ug.air.alrite.Fragments.Patient.MultipleChoiceFragment;
import com.ug.air.alrite.Fragments.Patient.OtherPatients;
import com.ug.air.alrite.R;
import com.ug.air.alrite.Utils.Credentials;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PatientActivity extends AppCompatActivity implements MultipleChoiceFragment.onGetResultListener {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String INCOMPLETE = "incomplete";
    SharedPreferences sharedPreferences, sharedPreferences1;
    SharedPreferences.Editor editor, editor1;
    int frag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Intent intent = getIntent();
        if (intent.hasExtra("Fragment")){
            frag = intent.getExtras().getInt("Fragment");
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (frag == 1){
                // fragmentTransaction.add(R.id.fragment_container, new Initials());
                try {
                    implementEditableDecisionTree();
                } catch (JSONException e) {
                    throw new RuntimeException("JSONException in Patient: " + e);
                }
            }else if (frag == 2){
                fragmentTransaction.add(R.id.fragment_container, new ActivePatients());
            }else {
                fragmentTransaction.add(R.id.fragment_container, new OtherPatients());
            }
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (frag == 2 || frag == 3){
            startActivity(new Intent(PatientActivity.this, Dashboard.class));
            finish();
        }
        else {
            String pin = sharedPreferences.getString(PIN, "");
            String cin = sharedPreferences.getString(CIN, "");

            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.exit);
            dialog.setCancelable(true);

            Button btnYes = dialog.findViewById(R.id.yes);
            Button btnNo = dialog.findViewById(R.id.no);

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (pin.isEmpty() && cin.isEmpty()){
                        dialog.dismiss();
                        startActivity(new Intent(PatientActivity.this, Dashboard.class));
                        finish();
                    }else{
                        Date currentTime = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
                        String formattedDate = df.format(currentTime);

                        String file = sharedPreferences.getString(FILENAME, "");

                        Credentials credentials = new Credentials();
                        String username = credentials.creds(PatientActivity.this).getUsername();


                        if (file.isEmpty()){
                            getDuration(currentTime);

                            String uniqueID = UUID.randomUUID().toString();

                            editor.putString(DATE, formattedDate);
                            editor.putString(USERNAME, username);
                            editor.putString(UUIDS, uniqueID);
                            editor.putString(INCOMPLETE, "incomplete");
                            editor.apply();

                            String filename = formattedDate + "_" + uniqueID;
                            doLogic(filename);
                            dialog.dismiss();
                        }
                        else {
                            String fast = sharedPreferences.getString(FASTBREATHING2, "");
                            if (fast.isEmpty()){
                                dialog.dismiss();
                                startActivity(new Intent(PatientActivity.this, Dashboard.class));
                                finish();
                            }else{

                                editor.putString(DATE_2, formattedDate);
                                editor.putString(INCOMPLETE, "incomplete");
                                editor.apply();

                                getDuration2(currentTime);
                                doLogic(file);
                            }

                        }

                    }
                }
            });

            dialog.show();
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
        sharedPreferences1 = getSharedPreferences(file, Context.MODE_PRIVATE);
        editor1 = sharedPreferences1.edit();
        Map<String, ?> all = sharedPreferences.getAll();
        for (Map.Entry<String, ?> x : all.entrySet()) {
            if (x.getValue().getClass().equals(String.class))  editor1.putString(x.getKey(),  (String)x.getValue());
//            else if (x.getValue().getClass().equals(Boolean.class)) editor1.putBoolean(x.getKey(), (Boolean)x.getValue());
        }
        editor1.commit();

        String filename = sharedPreferences1.getString(SECOND, "");
        if (!filename.isEmpty()){
            filename = filename + ".xml";
            File src = new File("/data/data/" + BuildConfig.APPLICATION_ID + "/shared_prefs/" + filename);
            if (src.exists()){
                src.delete();
            }
        }

        editor.clear();
        editor.commit();

        Intent intent;
        intent = new Intent(PatientActivity.this, Dashboard.class);
        startActivity(intent);
    }

    // TODO: ensure that the strings are not hard-coded in the future
    private void implementEditableDecisionTree() throws JSONException {
        pages = Objects.requireNonNull(createTestingJson()).getJSONArray("pages");
        // This is just an array because it needs to be mutable: think of it as
        // its own element
        JSONObject nextPage = pages.getJSONObject(0);

        // Continue looping through each page in sequence: depending on the component,
        // inflate a new fragment for that component type
        // If the next page is the final page, exit the loop
        getNextPage(nextPage);
    }

    private JSONObject createTestingJson() throws JSONException {
        try {
            // Get the file that has the json in it
            Object o = new JsonParser().parse(new FileReader("example.json"));
            JSONObject jsonStr = (JSONObject) o;

            return jsonStr;

        } catch(FileNotFoundException e) {
            throw new RuntimeException("issue with finding the json file: " + e);
        }
    }

    /**
     * NOTE: THIS SHOULD ONLY BE USED FOR SINGLE-LAYER ARRAYS
     *
     * @param jsonArray
     * @return
     * @throws JSONException
     */
    private ArrayList<JSONObject> JSONArrayToListOfJSONObjects(JSONArray jsonArray) throws JSONException {
        ArrayList<JSONObject> ret = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            ret.add(jsonArray.getJSONObject(i));
        }
        return ret;
    }

    /**
     * This will add the given fragment to the back stack, and do a transaction
     * which replaces the current fragment with the given fragment
     *
     * @param fragment the fragment to swap to
     */
    private void completeFragmentTransaction(Fragment fragment) {
        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.fragment_container, fragment);
        // Complete the changes added above
        ft.addToBackStack(null);
        ft.commit();
    }

    // Full JSON infos to call getNextPage
    JSONArray pages;

    // Information gotten from JSON
    String question;
    ArrayList<JSONObject> choices;

    private void getNextPage(JSONObject currNextPage) throws JSONException {
        if (currNextPage.getString("pageID").equals("final_page")) {
            // We're done! Let's exit out to the diagnosis page after finalizing our result
            startActivity(new Intent(getApplicationContext(), DiagnosisActivity.class));
            return;
        }

        // Get all of the items that should be displayed on the page
        // TODO: we're not accounting for multiple things on a page, fix in the future
        JSONArray nextPageComponents = currNextPage.getJSONArray("content");

        for (int i = 0; i < nextPageComponents.length(); i++) {
            JSONObject nextPageComponent = nextPageComponents.getJSONObject(i);

            // Multiple choice option:
            if (nextPageComponent.getString("type").equals("multiple-choice")) {
                // Collect the important arguments from the component
                question = nextPageComponent.getString("text");
                choices = JSONArrayToListOfJSONObjects(nextPageComponent.getJSONArray("choices"));

                // Get the new page's fragment, and set a listener for when the next button
                // is clicked
                MultipleChoiceFragment mc_fragment = MultipleChoiceFragment.newInstance(question, choices);

                // Replace and commit the fragment
                completeFragmentTransaction(mc_fragment);

            } else {
                throw new IllegalStateException("should never get here lol");
            }
        }
    }

    @Override
    public void getResultFromMultipleChoiceFragment(int choiceIndex) throws JSONException {
        String diagnosis = choices.get(choiceIndex - 1).getString("valueID");
        String nextPageName = choices.get(choiceIndex - 1).getString("link");


        // Enter the diagnosis into the editor
        editor.putString(question, diagnosis);
        editor.apply();

        // Decide on the next page based on the result
        int pagesIdx = findIndexInPagesGivenPageId(nextPageName);
        JSONObject nextPage = pages.getJSONObject(pagesIdx);
        try {
            getNextPage(nextPage);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private int findIndexInPagesGivenPageId(String pageID) throws JSONException {
        for (int i = 0; i < pages.length(); i++) {
            JSONObject currPage = pages.getJSONObject(i);
            String pageName = currPage.getString("pageID");
            if (pageID.equals(pageName)) {
                return i;
            }
        }
        return -1;
    }
}