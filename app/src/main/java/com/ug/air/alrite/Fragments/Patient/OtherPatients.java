package com.ug.air.alrite.Fragments.Patient;

import static android.content.Context.MODE_PRIVATE;
import static com.ug.air.alrite.Activities.DiagnosisActivity.PENDING;
import static com.ug.air.alrite.Activities.PatientActivity.APIPATH;
import static com.ug.air.alrite.Activities.PatientActivity.ASSESS_INCOMPLETE;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.DATE;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.USED_BRONCHODILATOR;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator3.AFTER_BRONCHODILATOR;
import static com.ug.air.alrite.Fragments.Patient.Initials.CHILD_INITIALS;
import static com.ug.air.alrite.Fragments.Patient.Initials.PARENT_INITIALS;
import static com.ug.air.alrite.Fragments.Patient.Sex.AGE_IN_YEARS;
import static com.ug.air.alrite.Fragments.Patient.Sex.SEX;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ug.air.alrite.APIs.ApiClient;
import com.ug.air.alrite.APIs.BackendRequests;
import com.ug.air.alrite.APIs.JsonPlaceHolder;
import com.ug.air.alrite.Activities.DiagnosisActivity;
import com.ug.air.alrite.Activities.PatientActivity;
import com.ug.air.alrite.Adapters.PatientAdapter;
import com.ug.air.alrite.BuildConfig;
import com.ug.air.alrite.Database.DatabaseHelper;
import com.ug.air.alrite.Models.History;
import com.ug.air.alrite.Models.Item;
import com.ug.air.alrite.R;
import com.ug.air.alrite.Utils.BackendPostRequest;
import com.ug.air.alrite.Utils.Credentials;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.internal.concurrent.TaskRunner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OtherPatients extends Fragment {

    View view;
    RecyclerView recyclerView;
    EditText etSearch;
    Button btnSubmit;
    File[] contents;
    ProgressBar progressBar;
    ImageView back;
    ArrayList<Item> items;
    PatientAdapter patientAdapter;
    String cin, pin, gender, age, search, dat, ag, token;
    ArrayList<String> types, files, file;
    SharedPreferences sharedPreferences;
    Intent intent;
    JsonPlaceHolder jsonPlaceHolder;
    DatabaseHelper databaseHelper;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_other_patients, container, false);

        databaseHelper = new DatabaseHelper(getActivity());
        jsonPlaceHolder = ApiClient.getClient(ApiClient.BASE_URL).create(JsonPlaceHolder.class);

        recyclerView = view.findViewById(R.id.recyclerView3);
        etSearch = view.findViewById(R.id.search);
        back = view.findViewById(R.id.back);
        btnSubmit = view.findViewById(R.id.submit_btn);
        progressBar = view.findViewById(R.id.progress_bar);
        swipeRefreshLayout = view.findViewById(R.id.swipe);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                accessSharedFile2();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        back.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        items = new ArrayList<>();
        files = new ArrayList<String>();

        accessSharedFile2();

        patientAdapter = new PatientAdapter(getActivity(), items);
        recyclerView.setAdapter(patientAdapter);

        patientAdapter.setOnItemClickListener(new PatientAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                History history = (History) items.get(position).getObject();
                String name = history.getFilename();
                intent = new Intent(getActivity(), DiagnosisActivity.class);

                intent.putExtra("filename", name);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    try {
                        sendAllCompletedAssessmentDataToBackend();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Toast.makeText(getActivity(),
                            "Please connect to the internet before submitting.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void accessSharedFile2(){
        files.clear();
        items.clear();
        File src = new File("/data/data/" + BuildConfig.APPLICATION_ID + "/shared_prefs");
        if (src.exists()) {
            File[] contents = src.listFiles();
//        Toast.makeText(getActivity(), "" + contents, Toast.LENGTH_SHORT).show();
            if (contents.length != 0) {
                for (File f : contents) {
                    if (f.isFile()) {
                        String name = f.getName().toString();
                        files.add(name);
                    }
                }
                Collections.reverse(files);
                for(String name : files){
                    if (!name.equals("sharedPrefs.xml") && !name.equals("counter_file.xml")){
                        String names = name.replace(".xml", "");
                        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(names, Context.MODE_PRIVATE);
                        String bron = sharedPreferences.getString(USED_BRONCHODILATOR, "");
                        String incomplete = sharedPreferences.getString(ASSESS_INCOMPLETE, "");
                        String pending = sharedPreferences.getString(PENDING, "");
                        String fin = sharedPreferences.getString(AFTER_BRONCHODILATOR, "");
                        if (bron.isEmpty() || bron.equals("Bronchodialtor Not Given") || !fin.isEmpty()){
                            cin = sharedPreferences.getString(CHILD_INITIALS, "");
                            pin = sharedPreferences.getString(PARENT_INITIALS, "");
                            age = sharedPreferences.getString(AGE_IN_YEARS, "");
                            gender = sharedPreferences.getString(SEX, "");
                            dat = sharedPreferences.getString(DATE, "");
                            if (age.isEmpty()){
                               ag = "0 years 0 months";
                               gender = "";
                            }else {
                                String[] split = age.split("\\.");
                                ag = split[0] + " years and " + split[1] + " months";
                            }

                            try {
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
                                Date date = df.parse(dat);
                                SimpleDateFormat df1 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
                                String formattedDate = df1.format(date);
                                History history = new History("Age: " + ag, "Gender: " + gender, cin, "Parent/Guardian: " + pin, formattedDate, names, pending, incomplete);
                                items.add(new Item(1, history));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }else {
                Toast.makeText(getActivity(), "There are no patients' records", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("CheckResult")
    private void sendAllCompletedAssessmentDataToBackend() throws JSONException {
        // Stop user from doing anything to mess this up and show them a progress bar
        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        // Get the data with ID = 1 from the local user database, and get the token in that data
        Cursor localUserData = databaseHelper.getData("1");
        localUserData.moveToNext();
        token =  localUserData.getString(2);

        // Get the folder of all current assessments that have not been sent to the backend
        ArrayList<String> assessmentsToBeSent = getValidAssessments();

        // For every pair of patient info, turn them into proper JSONObjects and
        // put them in the array to be sent to the backend
        HashMap<BackendPostRequest, String> requestToApipath = new HashMap<>();

        // Create requests for each pair of patient info
        for (int i = 0; i < assessmentsToBeSent.size(); i+=2) {
            String diagnosesIDxml = assessmentsToBeSent.get(i);
            String diagnosesID = diagnosesIDxml.substring(0, diagnosesIDxml.length() - 4);
            String summaryIDxml = assessmentsToBeSent.get(i+1);
            String summaryID = summaryIDxml.substring(0, summaryIDxml.length() - 4);
            SharedPreferences diagnosesPrefs = getContext().getSharedPreferences(diagnosesID, MODE_PRIVATE);
            SharedPreferences summaryPrefs = getContext().getSharedPreferences(summaryID, MODE_PRIVATE);

            // TODO: implement diagnoses stuff later
            diagnosesPrefs.edit().clear().apply();

            // Get all items from the shared preferences object and create a request with them
            String apipath = "";
            Map<String, ?> summaryPrefItems = summaryPrefs.getAll();
            HashMap<String, String> summaryPrefMap = new HashMap<>();
            for (String item : summaryPrefItems.keySet()) {
                if (item.equals(APIPATH)) {
                    apipath = item;
                } else {
                    summaryPrefMap.put(item, (String) summaryPrefItems.get(item));
                }
            }
            summaryPrefs.edit().clear().apply();

            BackendPostRequest nextRequest = new BackendPostRequest(summaryPrefMap, "");
            requestToApipath.put(nextRequest, apipath);
        }
        System.out.println(requestToApipath);

        // We'll chunk all of the requests and send them to the backend: if there
        // were any that didn't go through, we'll not delete the files so that the
        // user can try again.
        BackendRequests backendRequests = ApiClient.getClient(ApiClient.REMOTE_URL_TEMP)
                .create(BackendRequests.class);
        List<Observable<?>> requests = new ArrayList<>();
        for (BackendPostRequest currRequest : requestToApipath.keySet()) {
            String apipath = requestToApipath.get(currRequest);
            requests.add(backendRequests.postToBackend(currRequest));
        }

        Observable.zip(
            requests,
            (s -> s))
            .subscribeOn(Schedulers.io())
            .subscribe(
                // Will be triggered if all requests will end successfully (4xx and 5xx also are successful requests too)
                new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        // On success tell the user we're ok
                        System.out.println(o);

                        progressBar.setVisibility(View.GONE);
                        btnSubmit.setEnabled(true);
                    }
                },

                // Will be triggered if any error during requests will happen
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        System.out.println("there was an error getting requests: " + e);
                        progressBar.setVisibility(View.GONE);
                        btnSubmit.setEnabled(true);
                    }
                }
            );
    }

    private ArrayList<String> getValidAssessments() {
        ArrayList<String> assessmentsToBeSent = new ArrayList<>();

        File assessments = new File("/data/data/" + BuildConfig.APPLICATION_ID + "/shared_prefs");
        File[] assessmentsList = assessments.listFiles();
        // if no files in the folder, send nothing back
        if (assessmentsList == null) {
            return assessmentsToBeSent;
        }

        // Validate files
        for (File assessment : assessmentsList) {
            // If this is not a file, skip
            if (!assessment.isFile()) {
                System.out.println("not a file!");
                continue;
            }

            // If the file is a permanent tracker file, skip
            String filename = assessment.getName();
            if (filename.equals("sharedPrefs.xml") || filename.equals("counter_file.xml")) {
                System.out.println("Permanent file: don't send to backend");
                continue;
            }

            // Otherwise, we're good to send it to the backend! Add the file to
            // the list of files to send to the backend
            assessmentsToBeSent.add(filename);
        }

        // Sort the files, so that summary and diagnosis are next to each other
        Collections.sort(assessmentsToBeSent);
        return assessmentsToBeSent;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void deleteLocalPatientFiles() {
        File assessments = new File("/data/data/" + BuildConfig.APPLICATION_ID + "/shared_prefs");
        File[] assessmentsList = assessments.listFiles();
        // if no files in the folder, send nothing back
        if (assessmentsList == null) {
            return;
        }

        // Validate files
        for (File assessment : assessmentsList) {
            // If this is not a file, skip
            if (!assessment.isFile()) {
                System.out.println("not a file!");
                continue;
            }

            // If the file is a permanent tracker file, skip
            String filename = assessment.getName();
            if (filename.equals("sharedPrefs.xml") || filename.equals("counter_file.xml")) {
                System.out.println("Permanent file: don't send to backend");
                continue;
            }

            // Otherwise, we're good to send it to the backend! Add the file to
            // the list of files to send to the backend
            //assessment.delete();
            System.out.println("fake delete file");
        }
    }
}