package com.ug.air.alrite.Fragments.Patient;

import static com.ug.air.alrite.Activities.PatientActivity.SYMPTOM_TYPE;
import static com.ug.air.alrite.Activities.PatientActivity.VALUE_ID;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ug.air.alrite.Activities.PatientActivity;
import com.ug.air.alrite.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MultipleChoiceFragment extends Fragment {

    public interface onGetResultListener {
        void getResultFromMultipleChoiceFragment(int choiceIndex) throws JSONException;
        void getLastPage() throws JSONException;
    }
    onGetResultListener getResultListener;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String QUESTION = "question";
    public static final String CHOICES = "choices";
    public static final String VALUE_ID = "valueID";
    public static final String DEFAULT = "**default string**";
    String previousResponse;
    String question;
    ArrayList<String> choices;
    String valueID;
    View view;
    RadioGroup choiceGroup;
    ArrayList<RadioButton> choiceButtonList;
    Button backButton, nextButton;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    /**
     * This is where we do what we usually do in the onAttach section, except that
     * we also add a listener for sending the result of the Multiple Choice up to
     * the activity
     *
     * @param activity the PatientActivity that we're running this fragment on
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            getResultListener = (onGetResultListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    /**
     * This creates a new MultipleChoiceFragment with a separate constructor, so
     * that we can retain the given information and return the fragment
     *
     * @param question the question at the top of the page
     * @param choices the given choices for the user to choose from
     * @return the fragment to be used in the future
     */
    public static MultipleChoiceFragment newInstance(String question, ArrayList<JSONObject> choices, String valueID) throws JSONException {
        MultipleChoiceFragment mfc = new MultipleChoiceFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION, question);
        ArrayList<String> text_choices = getTextFromChoices(choices);
        args.putStringArrayList(CHOICES, text_choices);
        args.putString(VALUE_ID, valueID);
        mfc.setArguments(args);
        return mfc;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Idk exactly what this does right now, but it sets up the view and the stored information
        view = inflater.inflate(R.layout.multiple_choice, container, false);
        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Retrieve the information for the question/choices of the page
        assert getArguments() != null;
        question = getArguments().getString(QUESTION);
        choices = getArguments().getStringArrayList(CHOICES);
        valueID = getArguments().getString(VALUE_ID);

        // Then, set the information for each question/choice to line up with our givens
        TextView questionDisplay = view.findViewById(R.id.mc_question);
        questionDisplay.setText(question);

        // Create the RadioGroup and RadioButtons that will be displayed on the page
        // within the given multiple_choice_layout on the layout for the fragment
        LinearLayout multipleChoiceLayout = view.findViewById(R.id.multiple_choice_layout);
        choiceGroup = new RadioGroup(this.getContext());
        multipleChoiceLayout.addView(choiceGroup);
        choiceButtonList = new ArrayList<>();
        for (String choiceText : choices) {
            RadioButton but = new RadioButton(this.getContext());
            choiceGroup.addView(but);
            but.setText(choiceText);
            choiceButtonList.add(but);
        }

        // All of the buttons/things that we'll need to reference
        nextButton = view.findViewById(R.id.next);
        backButton = view.findViewById(R.id.back);

        // If we've already seen this page, reload our past choices
        loadSelectedChoiceIfAlreadySelected();

        // This is a listener for, if the next button is pressed, whether we can go
        // on or not, and what information should be sent up
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // First, we check that at least one option was selected
                // if not, then we create a popup which tells the user to select at
                // least one option
                int chosenOption = choiceGroup.getCheckedRadioButtonId();
                if (chosenOption == -1) {
                    Toast.makeText(getActivity(), "Please select at least one of the options", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get the index of the choice in the set of buttons, starting at index 0
                for (int i = 0; i < choiceButtonList.size(); i++) {
                    RadioButton choiceButton = choiceButtonList.get(i);
                    if (choiceButton.getId() == chosenOption) {
                        // Send the result back up to main: a listener there will trigger
                        // and start the next fragment sequence up
                        try {
                            getResultListener.getResultFromMultipleChoiceFragment(i);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        return;
                    }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getResultListener.getLastPage();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return view;
    }

    /**
     * In the case that we're creating this view again after we've already stepped
     * through this option, pre-select the choice that we already made, so that we
     * don't get inconsistent answers from this
     */
    private void loadSelectedChoiceIfAlreadySelected() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        previousResponse = sharedPreferences.getString(SYMPTOM_TYPE + valueID, DEFAULT);

        for (int i = 0; i < choices.size(); i++) {
            if (previousResponse.equals(choices.get(i))) {
                ((RadioButton) choiceGroup.getChildAt(i)).setChecked(true);
                return;
            }
        }
    }

    /**
     * This is used specifically for newInstance, and relies on the JSON being set
     * up as in the "example.json" file. This will extract all of the text answers
     * from the given choices list.
     *
     * @param list is an ArrayList of JSONObjects (choices)
     * @return the list of strings of text for each choice, in order
     */
    private static ArrayList<String> getTextFromChoices(ArrayList<JSONObject> list) throws JSONException {
        ArrayList<String> ret = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ret.add(list.get(i).getString(PatientActivity.TEXT));
        }
        return ret;
    }
}
