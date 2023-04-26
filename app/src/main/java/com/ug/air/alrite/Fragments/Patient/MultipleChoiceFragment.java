package com.ug.air.alrite.Fragments.Patient;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ug.air.alrite.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MultipleChoiceFragment extends Fragment {

    public interface onGetResultListener {
        public void getResultFromMultipleChoiceFragment(int choiceIndex) throws JSONException;
    }
    onGetResultListener getResultListener;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String QUESTION = "question";
    public static final String CHOICES = "choices";
    public static final String DEFAULT = "**default string**";
    String previousResponse;
    String question;
    ArrayList<String> choices;
    View view;
    RadioGroup choiceGroup;
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
    public static MultipleChoiceFragment newInstance(String question, ArrayList<JSONObject> choices) throws JSONException {
        MultipleChoiceFragment mfc = new MultipleChoiceFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION, question);
        ArrayList<String> text_choices = getTextFromChoices(choices);
        args.putStringArrayList(CHOICES, text_choices);
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

        // Then, set the information for each question/choice to line up with our givens
        TextView questionDisplay = view.findViewById(R.id.mc_question);
        questionDisplay.setText(question);

        // TODO: create buttons in here, and then choose their ids more easily
        // Currently storing 3 radio buttons will update later
        // Set for demo might add more or create optimized loop
        RadioButton choice1 = view.findViewById(R.id.choice1);
        choice1.setText(choices.get(0));
        RadioButton choice2 = view.findViewById(R.id.choice2);
        choice2.setText(choices.get(1));
        RadioButton choice3 = view.findViewById(R.id.choice3);
        choice3.setText(choices.get(2));

        // All of the buttons/things that we'll need to reference
        nextButton = view.findViewById(R.id.next);
        backButton = view.findViewById(R.id.back);
        choiceGroup = view.findViewById(R.id.mc_options);

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

                // Get the index of the choice in the set of buttons: relies on the
                // buttons having names with their index at the end
                String chosenOptionId = v.getResources().getResourceName(chosenOption);
                int index = Integer.parseInt(chosenOptionId.substring(chosenOptionId.length() - 1));

                // Send the result back up to main: a listener there will trigger
                // and start the next fragment sequence up
                try {
                    getResultListener.getResultFromMultipleChoiceFragment(index);
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
        previousResponse = sharedPreferences.getString(question, DEFAULT);

        for (int i = 0; i < choices.size(); i++) {
            if (previousResponse.equals(choices.get(i))) {
                RadioGroup allChoices = view.findViewById(R.id.mc_options);
                allChoices.check(i);
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
            ret.add(list.get(i).getString("text"));
        }
        return ret;
    }
}
