package com.ug.air.alrite.Fragments.Patient;

import static com.ug.air.alrite.Fragments.Patient.FTouch.TOUCH;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ug.air.alrite.R;

import org.json.JSONException;


public class TextInputFragment extends Fragment {
    //Fragment wise should be done may need listener for skip
    public interface onGetResultListener {
        void getResultFromTextInputFragment(int choiceIndex) throws JSONException;
        void getLastPage() throws JSONException;
    }
    onGetResultListener getResultListener;
    View view;
    EditText etDay;
    Button back, next, btnSkip, btnSave;
    String userInput, diagnosis;
    public static final String Question = "What is the childs temperature";
    public static final String TDIAGNOSIS = "diagnosis_fever";
    public static final String SHARED_PREFS = "sharedPrefs";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Dialog dialog;
    TextView txtMessage;
    public static final String IHint = "IHint";
    public static final String IInformation = "IInformation";
    public static final String SInformation = "SInformation";
    public static final String min_value = "min_value";
    public static final String max_value = "max_value";
    public static final String minValueforDiagnosis = "minValueforDiagnosis";
    String question;
    String InputHint;
    String InputInformation;
    String SkipInformation;
    int MinValue;
    int MaxValue;
    int diagnosisCutoff;
    /**
     * This creates a new MultipleChoiceFragment with a separate constructor, so
     * that we can retain the given information and return the fragment
     *
     * @param question the question at the top of the page
     * @param InputHint the input hint for the user
     * @param InputInformation the input hint for the user
     * @param SkipInformation the input hint for the user
     * @param MinValue the input hint for the user
     * @param MaxValue the input hint for the user
     * @param diagnosisCutoff the input hint for the user
     * @return the fragment to be used in the future
     */
    public static TextInputFragment newInstance(String question, String InputHint, String InputInformation, String SkipInformation,
                                                int MinValue, int MaxValue, int diagnosisCutoff) throws JSONException {
        TextInputFragment tif = new TextInputFragment();
        Bundle args = new Bundle();
        args.putString(Question, question);
        args.putString(IHint, InputHint);
        args.putString(IInformation, InputInformation);
        args.putString(SInformation, SkipInformation);
        args.putInt(min_value ,MinValue);
        args.putInt(max_value ,MaxValue);
        args.putInt(minValueforDiagnosis , diagnosisCutoff);
        tif.setArguments(args);
        return tif;
}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.text_input, container, false);
        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        assert getArguments() != null;
        question = getArguments().getString(Question);
        InputHint = getArguments().getString(IHint);
        InputInformation = getArguments().getString(IInformation);
        SkipInformation = getArguments().getString(SInformation);
        MinValue = getArguments().getInt(min_value);
        MaxValue = getArguments().getInt(max_value);
        diagnosisCutoff = getArguments().getInt(minValueforDiagnosis);

        TextView questionDisplay = view.findViewById(R.id.ti_question);
        questionDisplay.setText(question);
        etDay = view.findViewById(R.id.IHint);
        etDay.setHint(InputHint);
        TextView InputInformationDisplay = view.findViewById(R.id.ti_IInformation);
        InputInformationDisplay.setText(InputInformation);
        TextView SkipInformationDisplay = view.findViewById(R.id.ti_SInformation);
        SkipInformationDisplay.setText(SkipInformation);

        next = view.findViewById(R.id.next);
        back = view.findViewById(R.id.back);
        btnSkip = view.findViewById(R.id.skip);

        etDay.requestFocus();

        loadData();
        updateViews();


        etDay.addTextChangedListener(textWatcher);

        next.setOnClickListener(new View.OnClickListener() {
            //if there is nothing inputted
            //get the int result from the id and get the text from there!
            @Override
            public void onClick(View v) {
                userInput = etDay.getText().toString();
                if (userInput.isEmpty()){
                    Toast.makeText(getActivity(), "Please fill in the field before you continue", Toast.LENGTH_SHORT).show();
                }else{
                    saveData();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            getResultListener.getLastPage();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container, new FTouch());
                fr.addToBackStack(null);
                fr.commit();
            }
        });

        return view;
    }

    /**
     * This is where we do what we usually do in the onAttach section, except that
     * we also add a listener for sending the result of the Text Input up to
     * the activity
     *
     * @param activity the PatientActivity that we're running this fragment on
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            getResultListener = (TextInputFragment.onGetResultListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            userInput = etDay.getText().toString();
            if (!userInput.isEmpty()){
                float dy = Float.parseFloat(userInput);
                btnSkip.setEnabled(false);
                if (dy < MinValue){
                    etDay.setError("You have entered a value below the minimum");
                    next.setEnabled(false);
                }else if (dy > MaxValue) {
                    etDay.setError("You have entered a value above the maximum");
                    next.setEnabled(false);
                } else {
                    btnSkip.setEnabled(true);
                    next.setEnabled(true);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void saveData() {
        deleteSharedPreferences();

        int tp = Integer.parseInt(userInput);
        try {
            getResultListener.getResultFromTextInputFragment(tp);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private void loadData() {
        userInput = sharedPreferences.getString(Question, "");
    }

    private void updateViews() {
        if (!userInput.isEmpty()){
            etDay.setText(userInput);
            btnSkip.setEnabled(false);
        }else {
            btnSkip.setEnabled(true);
        }
    }

    private void deleteSharedPreferences() {
        editor.remove(TOUCH);
        editor.remove(TDIAGNOSIS);
        editor.apply();
    }

    private void showDialog() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.assess);
        dialog.setCancelable(true);

        txtMessage = dialog.findViewById(R.id.message);
        btnSave = dialog.findViewById(R.id.ContinueButton);

        btnSave.setText("Continue");

        txtMessage.setText("Popup text for the user indicating message");
        txtMessage.setTextColor(Color.parseColor("#FF0000"));
        txtMessage.setTypeface(Typeface.DEFAULT_BOLD);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container, new Oxygen());
                fr.addToBackStack(null);
                fr.commit();
            }
        });

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }
}