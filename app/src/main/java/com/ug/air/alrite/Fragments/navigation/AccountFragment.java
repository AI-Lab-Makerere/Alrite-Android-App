package com.ug.air.alrite.Fragments.navigation;

import static com.ug.air.alrite.Activities.SplashActivity.APP_OPENING_COUNT;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ug.air.alrite.APIs.ApiClient;
import com.ug.air.alrite.APIs.JsonPlaceHolder;
import com.ug.air.alrite.BuildConfig;
import com.ug.air.alrite.Database.DatabaseHelper;
import com.ug.air.alrite.Fragments.Patient.Allergies;
import com.ug.air.alrite.Models.Token;
import com.ug.air.alrite.Models.User;
import com.ug.air.alrite.R;
import com.ug.air.alrite.Utils.Counter;
import com.ug.air.alrite.Utils.Credentials;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {

    View view;
    Button loginBtn;
    ProgressBar progressBar;
    EditText etUsername, etPassword;
    String username, password, token;
    DatabaseHelper databaseHelper;
    JsonPlaceHolder jsonPlaceHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_account, container, false);

        loginBtn = view.findViewById(R.id.login_btn);
        progressBar = view.findViewById(R.id.login_progress_bar);
        etUsername = view.findViewById(R.id.username);
        etPassword = view.findViewById(R.id.password);

        jsonPlaceHolder = ApiClient.getClient(ApiClient.BASE_URL).create(JsonPlaceHolder.class);
        databaseHelper = new DatabaseHelper(getActivity());

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = etUsername.getText().toString().trim();
                password = etPassword.getText().toString().trim();

                if (username.isEmpty() && password.isEmpty()){
                    Toast.makeText(getActivity(), "Please enter both your username and password", Toast.LENGTH_SHORT).show();
                }else{
                    sendToServer();
                }
            }
        });

        return view;
    }

    private void sendToServer() {
        loginBtn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        User user = new User(username, password);
        Call<Token> call = jsonPlaceHolder.login(user);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(getActivity(), "Please check your username and password", Toast.LENGTH_SHORT).show();
                    loginBtn.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                loginBtn.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
                System.out.println(response.body());

                token = response.body().getToken();
                String code = response.body().getInformation().getCode();
                String h_code = response.body().getInformation().getHealthy_facility();
                String study_id = String.valueOf(response.body().getInformation().getStudy_id());

                File src = new File("/data/data/" + BuildConfig.APPLICATION_ID + "/databases/alrite.db");
                if (src.exists()){
                    databaseHelper.updateToken("1", token, username, code, h_code, study_id, password);
                }else {
                    databaseHelper.insertData(1, token, username, code, h_code, study_id, password);
                }

                Counter counter = new Counter();
                counter.Count(requireActivity(), APP_OPENING_COUNT);
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.navHostFragment, new HomeFragment());
                fr.commit();

            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Toast.makeText(getActivity(), "Please turn on your internet connection", Toast.LENGTH_SHORT).show();
                loginBtn.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}