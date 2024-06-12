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


public class TextInputTextFragment extends Fragment {
    //Fragment wise should be done may need listener for skip
    public interface onGetResultListener {
        void getResultFromTextInputTextFragment(String TextInputted) throws JSONException;
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
    String question;
    String InputHint;
    String InputInformation;
    String SkipInformation;

    /**
     * This creates a new MultipleChoiceFragment with a separate constructor, so
     * that we can retain the given information and return the fragment
     *
     * @param question the question at the top of the page
     * @param InputHint the input hint for the user
     * @param InputInformation the input hint for the user
     * @param SkipInformation the input hint for the user
     * @return the fragment to be used in the future
     */
    public static TextInputTextFragment newInstance(String question, String InputHint, String InputInformation, String SkipInformation) throws JSONException {
        TextInputTextFragment tif = new TextInputTextFragment();
        Bundle args = new Bundle();
        args.putString(Question, question);
        args.putString(IHint, InputHint);
        args.putString(IInformation, InputInformation);
        args.putString(SInformation, SkipInformation);
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

        TextView questionDisplay = view.findViewById(R.id.ti_question);
        questionDisplay.setText(question);
        etDay = view.findViewById(R.id.IHint);
        etDay.setHint(InputHint);
        TextView InputInformationDisplay = view.findViewById(R.id.ti_IInformation);
        InputInformationDisplay.setText(InputInformation);

        next = view.findViewById(R.id.next);
        back = view.findViewById(R.id.back);
        btnSkip = view.findViewById(R.id.skip);

        etDay.requestFocus();

        loadData();
        updateViews();



        next.setOnClickListener(new View.OnClickListener() {
            //if there is nothing inputted
            //get the int result from the id and get the text from there!
            @Override
            public void onClick(View v) {
                userInput = etDay.getText().toString();
                if (userInput.isEmpty()){
                    Toast.makeText(getActivity(), "Please fill in the field before you continue", Toast.LENGTH_SHORT).show();
                } else{
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
            getResultListener = (TextInputTextFragment.onGetResultListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    private void saveData() {
        deleteSharedPreferences();
        try {
            getResultListener.getResultFromTextInputTextFragment(userInput);
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
}