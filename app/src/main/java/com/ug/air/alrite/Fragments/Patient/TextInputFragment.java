package com.ug.air.alrite.Fragments.Patient;

import static com.ug.air.alrite.Activities.PatientActivity.SUMMARY_ID;
import static com.ug.air.alrite.Fragments.Patient.FTouch.TOUCH;
import static com.ug.air.alrite.Fragments.Patient.MultipleChoiceFragment.DEFAULT;
import static com.ug.air.alrite.Fragments.Patient.MultipleChoiceFragment.VALUE_ID;

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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ug.air.alrite.Activities.PatientActivity;
import com.ug.air.alrite.R;

import org.json.JSONException;


public class TextInputFragment extends Fragment {

    public interface onGetResultListener {
        void getResultFromTextInputFragment(String inputted) throws JSONException;
        void getLastPage() throws JSONException;
    }
    onGetResultListener getResultListener;
    View view;
    EditText etDay;
    Button back, next, btnSkip, btnSave;
    String userInput, diagnosis;
    public static final String QUESTION = "What is the childs temperature";
    public static final String INPUT_TYPE = "waht is ths inmput type";
    public static final String VALUEID = "what valid for this thing";

    public static final String SHARED_PREFS = "sharedPrefs";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Dialog dialog;
    TextView txtMessage;
    String question;
    String inputType;
    String valueID;
    String summaryPrefsID;
    int MinValue;
    int MaxValue;
    int diagnosisCutoff;
    /**
     * This creates a new MultipleChoiceFragment with a separate constructor, so
     * that we can retain the given information and return the fragment
     *
     * @param question the question at the top of the page
     * @return the fragment to be used in the future
     */
    public static TextInputFragment newInstance(String question, String inputType, String valueID, String summaryPrefsID) {
        TextInputFragment tif = new TextInputFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION, question);
        args.putString(INPUT_TYPE, inputType);
        args.putString(VALUEID, valueID);
        args.putString(SUMMARY_ID, summaryPrefsID);
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

        // Get the arguments that were passed in upon creation
        assert getArguments() != null;
        question = getArguments().getString(QUESTION);
        inputType = getArguments().getString(INPUT_TYPE);
        valueID = getArguments().getString(VALUEID);
        summaryPrefsID = getArguments().getString(SUMMARY_ID);

        TextView questionDisplay = view.findViewById(R.id.ti_question);
        questionDisplay.setText(question);
        etDay = view.findViewById(R.id.IHint);
        etDay.requestFocus();

        // If there is any information for the skip button, display it: otherwise,
        // make the text box invisible

        next = view.findViewById(R.id.next);
        back = view.findViewById(R.id.back);

        loadSelectedChoiceIfAlreadySelected();

        // For now, don't worry about this
        // etDay.addTextChangedListener(textWatcher);

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
                if (userInput.equals(PatientActivity.INPUT_TYPES.numeric.name())) {
                    //TODO: in the future, if we add a min and max value to be passed in,
                    // we can update this
//                    float dy = Float.parseFloat(userInput);
//                    btnSkip.setEnabled(false);
//                    if (dy < MinValue) {
//                        etDay.setError("You have entered a value below the minimum");
//                        next.setEnabled(false);
//                    } else if (dy > MaxValue) {
//                        etDay.setError("You have entered a value above the maximum");
//                        next.setEnabled(false);
//                    } else {
//                        btnSkip.setEnabled(true);
//                        next.setEnabled(true);
//                    }
                } else {
                    //TODO: in the future, if we add a min and max value to be passed in,
                    // we can update this
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private void saveData() {
        try {
            getResultListener.getResultFromTextInputFragment(userInput);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * In the case that we're creating this view again after we've already stepped
     * through this option, pre-select the choice that we already made, so that we
     * don't get inconsistent answers from this
     */
    private void loadSelectedChoiceIfAlreadySelected() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(summaryPrefsID, Context.MODE_PRIVATE);
        String previousResponse = sharedPreferences.getString(valueID, "");

        etDay.setText(previousResponse);
    }
}