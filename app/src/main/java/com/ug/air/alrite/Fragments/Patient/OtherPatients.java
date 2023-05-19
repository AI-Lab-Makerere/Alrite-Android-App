package com.ug.air.alrite.Fragments.Patient;

import static android.content.Context.MODE_PRIVATE;
import static com.ug.air.alrite.Activities.DiagnosisActivity.PENDING;
import static com.ug.air.alrite.Activities.PatientActivity.ASSESS_INCOMPLETE;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.DATE;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator.USED_BRONCHODILATOR;
import static com.ug.air.alrite.Fragments.Patient.Bronchodilator3.AFTER_BRONCHODILATOR;
import static com.ug.air.alrite.Fragments.Patient.Initials.CHILD_INITIALS;
import static com.ug.air.alrite.Fragments.Patient.Initials.PARENT_INITIALS;
import static com.ug.air.alrite.Fragments.Patient.Sex.AGE_IN_YEARS;
import static com.ug.air.alrite.Fragments.Patient.Sex.SEX;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ug.air.alrite.APIs.ApiClient;
import com.ug.air.alrite.APIs.JsonPlaceHolder;
import com.ug.air.alrite.Activities.DiagnosisActivity;
import com.ug.air.alrite.Activities.PatientActivity;
import com.ug.air.alrite.Adapters.PatientAdapter;
import com.ug.air.alrite.BuildConfig;
import com.ug.air.alrite.Database.DatabaseHelper;
import com.ug.air.alrite.Models.History;
import com.ug.air.alrite.Models.Item;
import com.ug.air.alrite.R;
import com.ug.air.alrite.Utils.Credentials;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
                String incomplete = history.getIncomplete();
                if (incomplete.equals("incomplete")){
                    intent = new Intent(getActivity(), PatientActivity.class);
                    intent.putExtra("Fragment", 1);
                }else {
                    intent = new Intent(getActivity(), DiagnosisActivity.class);
                }

                intent.putExtra("filename", name);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitData();
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

    private void submitData() {
        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);
        Credentials credentials = new Credentials();
        Cursor res = databaseHelper.getData("1");
        while (res.moveToNext()){
            token =  res.getString(2);
        }
        File src = new File("/data/data/" + BuildConfig.APPLICATION_ID + "/shared_prefs");
        if (src.exists()){
            contents = src.listFiles();
            if (contents.length != 0) {
                for (File f : contents) {
                    if (f.isFile()) {
                        String name = f.getName();
                        if (!name.equals("sharedPrefs.xml") && !name.equals("counter_file.xml")) {
                            String names = name.replace(".xml", "");
                            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(names, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String bron = sharedPreferences.getString(USED_BRONCHODILATOR, "");
                            String fin = sharedPreferences.getString(AFTER_BRONCHODILATOR, "");
                            String pending = sharedPreferences.getString(PENDING, "");
                            String incomplete = sharedPreferences.getString(ASSESS_INCOMPLETE, "");

                            if (!pending.equals("pending") && (bron.isEmpty() || bron.equals("Bronchodialtor Not Given") || !fin.isEmpty())){
                                File patient = new File("/data/data/" + BuildConfig.APPLICATION_ID + "/shared_prefs/" + name);
                                RequestBody filePart = RequestBody.create(MediaType.parse("*/*"), patient);
                                MultipartBody.Part fileUpload = MultipartBody.Part.createFormData("patient", patient.getName() ,filePart);

                                Call<String> call = jsonPlaceHolder.sendFile("Token " + token, fileUpload);
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (!response.isSuccessful()){
                                            Log.d("Alrite server issue", "There is an issue with the server" );
                                            return;
                                        }
                                        String message = response.body();
                                        Log.d("Alrite app: ", "Sever response: " + message);
                                        patient.delete();
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Log.d("Alrite server issue", "No internet connection" );
//                                    Toast.makeText(getActivity(), "Please turn on your internet connection", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    }
                }
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
            }else {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
            }
        }else {
            progressBar.setVisibility(View.GONE);
            btnSubmit.setEnabled(true);
        }
    }
}