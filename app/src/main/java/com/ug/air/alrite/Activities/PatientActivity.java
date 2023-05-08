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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.makeramen.roundedimageview.BuildConfig;
import com.ug.air.alrite.APIs.ApiClient;
import com.ug.air.alrite.APIs.DecisionTreeJSON;
import com.ug.air.alrite.APIs.JsonPlaceHolder;
import com.ug.air.alrite.Fragments.Patient.ActivePatients;
import com.ug.air.alrite.Fragments.Patient.MultipleChoiceFragment;
import com.ug.air.alrite.Fragments.Patient.OtherPatients;
import com.ug.air.alrite.Fragments.Patient.TextInputFragment;
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
import java.net.URL;
import java.nio.file.Path;
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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.Response;

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
                getJSONFromBackend();
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


    /**---------------------------------------------------------------------------------------------
     *
     * Decision Tree creation and maintenance functions below vv
     *
     *--------------------------------------------------------------------------------------------*/

    // Strings for the JSON identifiers
    public static final String COMPONENT = "component";
    public static final String MULTISELECT = "multiselect";
    public static final String PAGE_ID = "pageID";
    public static final String PAGES = "pages";
    public static final String LABEL = "label";
    public static final String CHOICES = "choices";
    public static final String TEXT = "text";
    public static final String VALUE = "value";
    public static final String LINK = "link";
    public static final String CONTENT = "content";
    public static final String MULTIPLE_CHOICE = "MultipleChoice";
    public static final String TEXT_INPUT = "TextInput";
    public static final String DIAGNOSIS_PAGE = "Diagnosis Page";
    public static final String TITLE = "title";

     // Full JSON infos to call getNextPage
     JSONArray pages;

     // Information gotten from JSON for the current page
     // Information gotten from JSON
     String question;
     ArrayList<JSONObject> choices;
     ArrayList<String> backstack;
     // Other than question there is more information needed
     // from the JSON for text input
     String InputHint;
     String InputInformation;
     String SkipInformation;
     int MinValue;
     int MaxValue;
     int diagnosisCutoff;

    /**
     *
     * @throws JSONException
     * @throws IOException
     */
    // TODO: ensure that the strings are not hard-coded in the future
    private void implementEditableDecisionTree(JSONObject json) throws JSONException, IOException {
//        DecisionTreeJSON json = ApiClient.getClient(ApiClient.TEMP_SERV_URL)
//                                    .create(DecisionTreeJSON.class);
//        Call<String> call = DecisionTreeJSON.getJson();
//        System.out.println(json);

        backstack = new ArrayList<>();
        pages = json.getJSONArray(PAGES);
        // This is just an array because it needs to be mutable: think of it as
        // its own element
        String nextPage = pages.getJSONObject(0).getString(PAGE_ID);

        // Handling the intent

        // Continue looping through each page in sequence: depending on the component,
        // inflate a new fragment for that component type
        // If the next page is the final page, exit the loop
        getNextPage(nextPage);
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

    /**
     * Given a page from the json object, we create the fragment for that page and
     * populate it with the necessary information.
     *
     * @param nextPageID the ID of the next page to be displayed in the app
     * @throws JSONException if the json is mad at you for calling it improperly
     */
    private void getNextPage(String nextPageID) throws JSONException {
        // Add the new page to the backstack, if it's not already there somewhere
        if (!backstack.contains(nextPageID)) {
            backstack.add(nextPageID);
        }

        // Get all of the items that should be displayed on the page
        JSONObject nextPageJSON = pages.getJSONObject(findIndexInPagesGivenPageId(nextPageID));
        JSONArray nextPageContent = nextPageJSON.getJSONArray(CONTENT);

        // If the next page would be the final page, then the assessment is over!
        // So, start up the diagnosis activity
        if (nextPageJSON.get(TITLE).equals(DIAGNOSIS_PAGE)) {
            exitActivity();
            return;
        }

        // Then, for each component that we're given within the page, display a
        // fragment for that component.
        for (int i = 0; i < nextPageContent.length(); i++) {
            JSONObject nextPageComponent = nextPageContent.getJSONObject(i);
            String nextPageComponentType = nextPageComponent.getString(COMPONENT);

            // Multiple choice option(s):
            if (nextPageComponentType.equals(MULTIPLE_CHOICE)) {
                Boolean isMultiSelect = nextPageComponent.getBoolean(MULTISELECT);
                if (isMultiSelect) {
                    createMultiSelectFragment(nextPageComponent);
                } else {
                    createMultipleChoiceFragment(nextPageComponent);
                }

            // Text input option:
            } else if (nextPageComponentType.equals(TEXT_INPUT)) {
                createTextInputFragment(nextPageComponent);

            // There was an issue with identifying the page...
            } else {
                throw new IllegalStateException("should never get here lol");
            }
        }
    }

    /**
     * Essentially, make an asynchronous API call to get the HTTP
     */
    private void getJSONFromBackend() {
        DecisionTreeJSON dtJson = ApiClient.getClient(ApiClient.TEMP_SERV_URL).create(DecisionTreeJSON.class);
        Call<String> call = dtJson.getJson();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (!response.isSuccessful()) {
                    System.out.println("no site, plase try again");
                    return;
                }

                // We've received a response! We can turn this into a JSONObject
                // and store the result locally
                try {
                    assert response.body() != null;
                    JSONObject json = new JSONObject(response.body());
                    implementEditableDecisionTree(json);
                } catch (JSONException | IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                throw new RuntimeException(t);
            }
        });
    }

    /**
     * Start the Diagnosis activity that directly follows from this one, and exit
     * the activity.
     */
    private void exitActivity() {
        startActivity(new Intent(PatientActivity.this, DiagnosisActivity.class));
        this.finish();
    }

    private void createMultipleChoiceFragment(JSONObject nextPageComponent) throws JSONException {
        // Collect the important arguments from the component
        question = nextPageComponent.getString(LABEL);
        choices = JSONArrayToListOfJSONObjects(nextPageComponent.getJSONArray(CHOICES));

        // Get the new page's fragment, and set a listener for when the next button
        // is clicked
        MultipleChoiceFragment mc_fragment = MultipleChoiceFragment.newInstance(question, choices);

        // Replace and commit the fragment
        completeFragmentTransaction(mc_fragment);
    }
    /**
     * Creating the Fragment for Text Input
     *
     * @throws JSONException because we use json objects
     */
    private void createTextInputFragment(JSONObject page) throws JSONException {
        // Collect the important arguments from the component
        question = page.getString("text");
        InputHint = page.getString("text");
        InputInformation = page.getString("");
        SkipInformation = page.getString("");
        MinValue = page.getInt("");
        MaxValue = page.getInt("");
        diagnosisCutoff = page.getInt("");

        // Get the new page's fragment, and set a listener for when the next button
        // is clicked
        TextInputFragment ti_fragment = TextInputFragment.newInstance(question, InputHint, InputInformation, SkipInformation,
        MinValue, MaxValue, diagnosisCutoff);

        // Replace and commit the fragment
        completeFragmentTransaction(ti_fragment);
    }
    public void getResultFromTextInputFragment(int numberInputted) throws JSONException {
        String diagnosis = "";
        // Enter the diagnosis into the editor
        editor.putString(question, diagnosis);
        editor.apply();

    }
    private void createMultiSelectFragment(JSONObject nextPageComponent) throws JSONException {
//        // Collect the important arguments from the component
//        question = page.getString(LABEL);
//        choices = JSONArrayToListOfJSONObjects(page.getJSONArray(CHOICES));
//
//        // Get the new page's fragment, and set a listener for when the next button
//        // is clicked
//        MultiSelectFragment ms_fragment = MultiSelectFragment.newInstance(question, choices);
//
//        // Replace and commit the fragment
//        completeFragmentTransaction(ms_fragment);
    }

    /**
     * Listener for clicking the next button: we can move to the correct next
     * page, as given by the user's sent JSON. This one is specifically for the
     * multiple choice fragment.
     *
     * @param choiceIndex the radiobutton choice that the user picked
     * @throws JSONException because we use json objects
     */
    @Override
    public void getResultFromMultipleChoiceFragment(int choiceIndex) throws JSONException {
        String diagnosis = choices.get(choiceIndex - 1).getString(TEXT);
        String nextPageName = choices.get(choiceIndex - 1).getString(LINK);
        System.out.println("NEXT PAGE NAME:" + nextPageName);

        // Enter the diagnosis into the editor
        editor.putString(question, diagnosis);
        editor.apply();

        // Decide on the next page based on the result
        try {
            getNextPage(nextPageName);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Listener for clicking the back button: calls up the next page based on the
     * current backstack of pages
     *
     * @throws JSONException if the json hates you
     */
    @Override
    public void getLastPage() throws JSONException {
        // TODO: exit to main activity, if it's the first page

        // Get the last page JSON object that we put on to the backstack
        backstack.remove(backstack.size() - 1);
        String backPageName = backstack.get(backstack.size() - 1);

        getNextPage(backPageName);
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