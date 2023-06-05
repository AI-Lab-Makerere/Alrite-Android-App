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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ug.air.alrite.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MultipleSelectionFragment extends Fragment {

    public interface onGetResultListener {
        void getResultFromMultipleSelectionFragment(ArrayList<Integer> chosenOptions) throws JSONException;
        void getLastPage() throws JSONException;
    }
    onGetResultListener getResultListener;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String QUESTION = "question";
    public static final String CHOICES = "choices";
    public static final String DEFAULT = "**default string**";
    public static final String VALUE_ID = "valueID";
    String valueID;
    String previousResponse;
    String question;
    ArrayList<String> choices; // list of the texts of the checkboxes
    ArrayList<CheckBox> choiceGroup; // list of checkboxes
    View view;
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
     * This creates a new MultipleSelectionFragment with a separate constructor, so
     * that we can retain the given information and return the fragment
     *
     * @param question the question at the top of the page
     * @param choices the given choices for the user to choose from
     * @return the fragment to be used in the future
     */
    public static MultipleSelectionFragment newInstance(String question, ArrayList<JSONObject> choices, String valueID) throws JSONException {
        MultipleSelectionFragment msc = new MultipleSelectionFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION, question);
        ArrayList<String> text_choices = getTextFromChoices(choices);
        args.putStringArrayList(CHOICES, text_choices);
        args.putString(VALUE_ID, valueID);
        msc.setArguments(args);
        return msc;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.multiple_select, container, false);
        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Retrieve the information for the question/choices of the page
        assert getArguments() != null;
        question = getArguments().getString(QUESTION);
        choices = getArguments().getStringArrayList(CHOICES); // list of strings
        valueID = getArguments().getString(VALUE_ID);

        // Then, set the information for each question/choice to line up with our givens
        TextView questionDisplay = view.findViewById(R.id.mc_question);
        questionDisplay.setText(question);

        // create buttons in here, and then choose their ids more easily
        // Currently storing 5 checkboxes will update later
        // might add more or create optimized loop

        LinearLayout multipleSelectLayout = view.findViewById(R.id.multiple_select_layout);

        choiceGroup = new ArrayList<>();
        for (String choiceText : choices) {
            CheckBox c = new CheckBox(this.getContext());
            multipleSelectLayout.addView(c); //?
            c.setText(choiceText);
            choiceGroup.add(c);
        }


        // All of the buttons/things that we'll need to reference
        nextButton = view.findViewById(R.id.next);
        backButton = view.findViewById(R.id.back);

        // If we've already seen this page, reload our past choices
        loadSelectedChoicesIfAlreadySelected();

        // This is a listener for, if the next button is pressed, whether we can go
        // on or not, and what information should be sent up
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // we need to see which choices are selected
                // store the ids
                ArrayList<Integer> chosenOptions = new ArrayList<Integer>();
                for (CheckBox choice : choiceGroup) {
                    if (choice.isChecked()) {
                        // get the id of this choice
                        int id = choice.getId();
                        chosenOptions.add(id);
                    }
                }
//                // do not allow selection number less than 1
//                if (chosenOptions.size() < 1) {
//                    Toast.makeText(getActivity(), "Please select at least one of the options", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                // Get the indices of the choices in the set of buttons: relies on the
                // buttons having names with their index at the end
                ArrayList<Integer> chosenOptionIds = new ArrayList<>();
                for (int c : chosenOptions) {
                    String chosenOptionId = v.getResources().getResourceName(c);
                    int index = Integer.parseInt(chosenOptionId.substring(chosenOptionId.length() - 1));
                    chosenOptionIds.add(index);
                }

                // Send the result back up to main: a listener there will trigger
                // and start the next fragment sequence up
                try {
                    getResultListener.getResultFromMultipleSelectionFragment(chosenOptionIds);  // adding to the diagnosis
                } catch (JSONException ex) {
                    throw new RuntimeException(ex);
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
     * don't get inconsistent answers from this.
     */
    private void loadSelectedChoicesIfAlreadySelected() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        previousResponse = sharedPreferences.getString(SYMPTOM_TYPE + valueID, DEFAULT);
        String[] selectedOptionsHistory = previousResponse.split("\n"); // split by \n


        for (int i = 0; i < choices.size(); i++) {
            for (String temp : selectedOptionsHistory) {
                if (temp.equals(choices.get(i))) {
                    ((CheckBox) choiceGroup.get(i)).setChecked(true);
                    return;
                }
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
