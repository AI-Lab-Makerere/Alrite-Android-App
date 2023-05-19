package com.ug.air.alrite.Fragments.Patient;

import static com.ug.air.alrite.Activities.SplashActivity.MANUAL_COUNT;
import static com.ug.air.alrite.Activities.SplashActivity.RR_COUNTER_COUNT;
import static com.ug.air.alrite.Fragments.Patient.Assess.SEVERE_SYMPTOMS;
import static com.ug.air.alrite.Fragments.Patient.ChestIndrawing.POINT;
import static com.ug.air.alrite.Fragments.Patient.ChestIndrawing.POINT2;
import static com.ug.air.alrite.Fragments.Patient.Sex.AGE_IN_MONTHS;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ug.air.alrite.R;
import com.ug.air.alrite.Utils.Calculations.Instructions;
import com.ug.air.alrite.Utils.Counter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class RRCounter extends Fragment {

    View view;
    Button next, back, reset, manual, btnRate, btnReset, btnContinue;
    EditText etRate;
    Bundle bundle;
    LinearLayout tap;
    TextView txtElapse, txtRate, txtBreathe, txtMsg;
    Dialog dialog, dialog1;
    private ArrayList<Long> durations;
    private long lastBreath;
    private double value = 0;
    private int numBreaths, margin, birthday;
    private String ifFastBreathing,ifNormalBreathing;
    private boolean fastBreathing;
    private CountDownTimer timer;
    private boolean completed;
    public static final String SHARED_PREFS = "sharedPrefs";
    SharedPreferences sharedPreferences, sharedPreferences1;
    SharedPreferences.Editor editor;
    public static final String FASTBREATHING = "breathing_rate";
    public static final String FASTBREATHING2 = "breathing_rate_2";
    public static final String INITIAL_DATE_2 = "start_date_2";
    public static final String SECOND = "second";
    public static final String RATE = "respiratory_rate";
    public static final String RATE2 = "respiratory_rate_2";
    String age, rate, rating, check;
    float ag = 0;
    int score, taps = 0;
    long duration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_rr_counter, container, false);

        back = view.findViewById(R.id.back);
        next = view.findViewById(R.id.next);
        reset = view.findViewById(R.id.reset);
        manual = view.findViewById(R.id.manual);
        txtElapse = view.findViewById(R.id.elapsedTime);
        tap = view.findViewById(R.id.Circle);

        bundle = this.getArguments();
        if (bundle != null){
            String fileName = bundle.getString("fileName");
            bronchodilator(fileName);
        } else {
            sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            String second = sharedPreferences.getString(SECOND, "");
            if (second.isEmpty()){
                check = "new data";
                loadData();
                updateViews();
            }else{
                bronchodilator(second);
//                reset.setVisibility(View.GONE);
//                manual.setVisibility(View.GONE);
//                tap.setVisibility(View.GONE);
//                txtElapse.setText("Respiratory Rate was already captured, click NEXT to continue");
//                next.setVisibility(View.VISIBLE);
            }

        }

        editor = sharedPreferences.edit();
        age = sharedPreferences.getString(AGE_IN_MONTHS, "");
        ag = Integer.parseInt(age);

        numBreaths = 5;
        margin=13;
        durations = new ArrayList<>();
        newTimer();
        lastBreath=-1;

        duration = TimeUnit.MINUTES.toMillis(1);

        CountDownTimer countDownTimer = new CountDownTimer(duration, 1000){
            @Override
            public void onTick(long l) {
                String sDuration = String.format(Locale.ENGLISH, "%02d : %02d",
                        TimeUnit.MILLISECONDS.toMinutes(l),
                        TimeUnit.MILLISECONDS.toSeconds(l) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));

                txtElapse.setText(sDuration);
            }

            @Override
            public void onFinish() {
                Counter counter = new Counter();
                counter.Count(requireActivity(), RR_COUNTER_COUNT);
                evalFastBreathing(ag);
                showDialog();
            }
        };

        tap.setOnClickListener(view -> {
            view.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.circle_animation));
//            ((TextView) view.findViewById(R.id.TapOnInhale)).setText(R.string.tap_on_inhale);
//            breathTaken();
//            if (validateDataCollection()) {
//                value = getBreathRate(numBreaths);
//                rate = String.valueOf(value);
//                completeMeasuring();
//            }
            if (value == 0){
                ((TextView) view.findViewById(R.id.TapOnInhale)).setText(R.string.tap_on_inhale);
                countDownTimer.start();
            }
            value += 1;
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                resetRespRate();
                countDownTimer.cancel();
                txtElapse.setText("01 : 00");
                value = 0;
            }
        });

        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnotherDialog();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                if(check.equals("bronchodilator")){
                    fr.replace(R.id.fragment_container, new ActivePatients());
                }else {
                    fr.replace(R.id.fragment_container, new Oxygen());
                }
                fr.commit();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String assess = sharedPreferences.getString(SEVERE_SYMPTOMS, "");
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                if (!assess.equals("None of these")){
                    fr.replace(R.id.fragment_container, new Wheezing());
                }else{
                    fr.replace(R.id.fragment_container, new Stridor());
                }
                fr.addToBackStack(null);
                fr.commit();
            }
        });

        return view;
    }

    public void bronchodilator(String fileName){
        sharedPreferences1 = requireActivity().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Map<String, ?> all = sharedPreferences1.getAll();
        for (Map.Entry<String, ?> x : all.entrySet()) {
            if (x.getValue().getClass().equals(String.class))  editor.putString(x.getKey(),  (String)x.getValue());
        }
        editor.commit();
        check = "bronchodilator";
        editor.putString(SECOND, fileName);
        editor.apply();
    }

    public void breathTaken() {
        long currTime = System.currentTimeMillis();
        if (lastBreath != -1) {
            long dur = currTime - lastBreath;
            durations.add(dur);
        } else{
            startTimer();
        }

        lastBreath = currTime;
    }

    public boolean validateDataCollection() {
        return getValidProgress() >= numBreaths;
    }

    public double getBreathRate(int num) {
        return 60 / (getMedian(num) / 1000.0);
    }

    public long getMedian(int length) {
        if (length > durations.size()) {
            return -1;
        }
        if (length == 0) {
            return -1;
        }

        ArrayList<Long> sub = new ArrayList<Long>(durations.subList(durations.size() - (length), durations.size()));
        Collections.sort(sub);
        int half = (length / 2);
        if (length % 2 == 0) {
            return (sub.get(half - 1) + sub.get(half)) / 2;
        }
        return sub.get(half);
    }

    public void resetTimer(){
        timer.cancel();
        newTimer();
    }

    public void startTimer(){
        timer.start();
    }

    public void newTimer(){
        timer = new CountDownTimer(60*1000,1000) {
            @Override
            public void onTick(long l) {
                updateElapsedTimeView((60*1000)-l);
            }

            @Override
            public void onFinish() {
                if(durations.size()<5){
                    resetRespRate();
                } else {
                    value = getBreathRate(durations.size());
                    completeMeasuring();
                }
            }
        };
    }

    public void completeMeasuring(){
        setCompleted(true);
        resetTimer();
        evalFastBreathing(ag);
        showDialog();
    }

    public int getValidProgress() {
        for (int ii = 1; ii <= numBreaths; ii++) {
            if (ii > durations.size()) {
                return ii - 1;
            }
            long median = getMedian(ii);
            if (median == -1) {
                return ii - 1;
            }
            long up = upperBound(median);
            long low = lowerBound(median);

            for (int jj = durations.size() - 1; jj > ((durations.size() - 1) - ii); jj--) {
                if (!inBounds(durations.get(jj), up, low)) {
                    return ii - 1;
                }
            }
        }
        return numBreaths;
    }

    public long lowerBound(long med) {
        return (long) (med * (1.0 - (margin / 100.0)));
    }

    public long upperBound(long med) {
        return (long) (med * (1.0 + (margin / 100.0)));
    }

    public boolean inBounds(long val, long up, long low) {
        return (val < up) && (val > low);
    }

    public void updateElapsedTimeView(long millis){
        String b = this.getResources().getString(R.string.elapsed_time);
        txtElapse.setText(b+" "+millisecondsToString(millis));
    }

    public String millisecondsToString(long millis){
        String  ms = (TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)))+
                ":"+ (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        return ms;
    }

    public void resetRespRate(){
        ((TextView) tap.findViewById(R.id.TapOnInhale)).setText(R.string.start_text);
        durations.clear();
        lastBreath = -1;
        resetTimer();
        updateElapsedTimeView(0);
    }

    public void evalFastBreathing(float birthday){
//        if(isCompleted()) {
//            if (birthday < 2){
//                if (value >= 60){
//                    fastBreathing = true;
//                    score = R.string.breathing_info_under2;
//                    return;
//                }
//                fastBreathing = false;
//            }
//            if (birthday >= 2 && birthday <= 12 ){
//                if (value >= 50){
//                    fastBreathing = true;
//                    score = R.string.breathing_info_under1;
//                    return;
//                }
//                fastBreathing = false;
//            }
//            if (birthday > 12){
//                if (value >= 40){
//                    fastBreathing = true;
//                    score = R.string.breathing_info_over1;
//                    return;
//                }
//                fastBreathing = false;
//            }
//        } else {
//            //TODO
//        }

        if (birthday < 2){
            if (value >= 60){
                fastBreathing = true;
                score = R.string.breathing_info_under2;
                return;
            }
            fastBreathing = false;
        }
        if (birthday >= 2 && birthday <= 12 ){
            if (value >= 50){
                fastBreathing = true;
                score = R.string.breathing_info_under1;
                return;
            }
            fastBreathing = false;
        }
        if (birthday > 12){
            if (value >= 40){
                fastBreathing = true;
                score = R.string.breathing_info_over1;
                return;
            }
            fastBreathing = false;
        }
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;

    }

    private void showAnotherDialog() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.manual_layout);

        etRate = dialog.findViewById(R.id.rate);
        btnRate = dialog.findViewById(R.id.saveRate);

        etRate.requestFocus();

        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = etRate.getText().toString();
                if (rate.isEmpty()){
                    Toast.makeText(getActivity(), "Please fill in the required field", Toast.LENGTH_SHORT).show();
                }else {
                    ag = Float.parseFloat(age);
                    double rt = Double.parseDouble(rate);
                    value = rt;
                    dialog.dismiss();
                    Counter counter = new Counter();
                    counter.Count(requireActivity(), MANUAL_COUNT);
                    completeMeasuring();
                }
            }
        });

        dialog.show();

    }

    private void showDialog() {
        dialog1 = new Dialog(getActivity());
        dialog1.setContentView(R.layout.respiratory_results_ayout);

        txtRate = dialog1.findViewById(R.id.RespRateNum);
        txtMsg = dialog1.findViewById(R.id.breathingInfo);
        txtBreathe = dialog1.findViewById(R.id.FastBreathing);
        btnReset = dialog1.findViewById(R.id.ResetButton);
        btnContinue = dialog1.findViewById(R.id.ContinueButton);

        rate = String.valueOf(value);
        String[] separated = rate.split("\\.");
        txtRate.setText(separated[0]);

        if (fastBreathing){
            rating = "Fast Breathing";
            txtBreathe.setText(rating);
            txtBreathe.setTextColor(getResources().getColor(R.color.red));
            txtMsg.setText(score);
        }else {
            rating = "Normal Breathing";
            txtBreathe.setText(rating);
            txtBreathe.setTextColor(getResources().getColor(R.color.green));
            txtMsg.setVisibility(View.GONE);
        }

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
                resetRespRate();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
                saveData();
            }
        });

        dialog1.show();

    }

    private void saveData() {

        if (check.equals("bronchodilator")){
            editor.putString(FASTBREATHING2, rating);
            editor.putString(RATE2, rate);

            String age = sharedPreferences.getString(AGE_IN_MONTHS, "");
            int ag = Integer.parseInt(age);

            Instructions instructions = new Instructions();
            int point = instructions.GetPointsFromRR(value, ag);
            String pot = String.valueOf(point);
            editor.putString(POINT2, pot);
            editor.apply();

            saveInitialDate();

            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_container, new Wheezing());
            fr.addToBackStack(null);
            fr.commit();

        }
        else{
            editor.putString(FASTBREATHING, rating);
            editor.putString(RATE, rate);
            editor.apply();

            String age = sharedPreferences.getString(AGE_IN_MONTHS, "");
            int ag = Integer.parseInt(age);

            Instructions instructions = new Instructions();
            int point = instructions.GetPointsFromRR(value, ag);
            String pot = String.valueOf(point);
            editor.putString(POINT, pot);
            editor.apply();

            String assess = sharedPreferences.getString(SEVERE_SYMPTOMS, "");

            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            if (!assess.equals("None of these")){
                fr.replace(R.id.fragment_container, new Wheezing());
            }else {
                fr.replace(R.id.fragment_container, new Stridor());
            }
//            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
//            fr.replace(R.id.fragment_container, new Stridor());
            fr.addToBackStack(null);
            fr.commit();
        }

    }

    private void saveInitialDate() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
        String formattedDate = df.format(currentTime);

        editor.putString(INITIAL_DATE_2, formattedDate);
        editor.apply();
    }

    private void loadData() {
        rate = sharedPreferences.getString(RATE, "");
    }

    private void updateViews() {
        if (!rate.isEmpty()){
            reset.setVisibility(View.GONE);
            manual.setVisibility(View.GONE);
            tap.setVisibility(View.GONE);
            txtElapse.setText("Respiratory Rate was already captured, click NEXT to continue");
            next.setVisibility(View.VISIBLE);
        }else {
            reset.setVisibility(View.VISIBLE);
            manual.setVisibility(View.VISIBLE);
            tap.setVisibility(View.VISIBLE);
            txtElapse.setText("Elapsed time:");
            next.setVisibility(View.GONE);
        }
    }

}