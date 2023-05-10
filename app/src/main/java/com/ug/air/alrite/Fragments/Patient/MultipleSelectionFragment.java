package com.ug.air.alrite.Fragments.Patient;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ug.air.alrite.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MultipleSelectionFragment extends Fragment {

    public interface onGetResultListener {
        public void getResultFromMultipleChoiceFragment(int choiceIndex) throws JSONException;
        void getLastPage() throws JSONException;
    }
    onGetResultListener getResultListener;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String QUESTION = "question";
    public static final String CHOICES = "choices";
    public static final String DEFAULT = "**default string**";
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
    public static MultipleSelectionFragment newInstance(String question, ArrayList<JSONObject> choices) throws JSONException {
        MultipleSelectionFragment msc = new MultipleSelectionFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION, question);
        ArrayList<String> text_choices = getTextFromChoices(choices);
        args.putStringArrayList(CHOICES, text_choices);
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

        // Then, set the information for each question/choice to line up with our givens
        TextView questionDisplay = view.findViewById(R.id.mc_question);
        questionDisplay.setText(question);

        // create buttons in here, and then choose their ids more easily
        // Currently storing 5 checkboxes will update later
        // might add more or create optimized loop

        CheckBox choice1 = view.findViewById(R.id.choice1);
        choice1.setText(choices.get(0));
        CheckBox choice2 = view.findViewById(R.id.choice2);
        choice2.setText(choices.get(1));
        CheckBox choice3 = view.findViewById(R.id.choice3);
        choice3.setText(choices.get(2));
        CheckBox choice4 = view.findViewById(R.id.choice4);
        choice4.setText(choices.get(3));
        CheckBox choice5 = view.findViewById(R.id.choice5);
        choice5.setText(choices.get(4));

        choiceGroup.add(choice1);
        choiceGroup.add(choice2);
        choiceGroup.add(choice3);
        choiceGroup.add(choice4);
        choiceGroup.add(choice5);
        System.out.println(choiceGroup);


        // All of the buttons/things that we'll need to reference
        nextButton = view.findViewById(R.id.next);
        backButton = view.findViewById(R.id.back);

        // If we've already seen this page, reload our past choices
        // loadSelectedChoicesIfAlreadySelected();

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
                if (chosenOptions.size() < 1) {
                    Toast.makeText(getActivity(), "Please select at least one of the options", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get the indices of the choices in the set of buttons: relies on the
                // buttons having names with their index at the end
                ArrayList<Integer> chosenOptionIds = new ArrayList<Integer>();
                for (int c : chosenOptions) {
                    String chosenOptionId = v.getResources().getResourceName(c);
                    int index = Integer.parseInt(chosenOptionId.substring(chosenOptionId.length() - 1));
                    chosenOptionIds.add(index);
                }

                // Send the result back up to main: a listener there will trigger
                // and start the next fragment sequence up
                try {
                    for (int index : chosenOptionIds) {
                        getResultListener.getResultFromMultipleChoiceFragment(index);  // adding to the diagnosis
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
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
//    private void loadSelectedChoicesIfAlreadySelected() {
//
//        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
//        previousResponse = sharedPreferences.getString(question, DEFAULT);
//
//        //
//    }

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
