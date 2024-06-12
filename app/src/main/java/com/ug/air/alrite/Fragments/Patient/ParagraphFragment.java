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

import com.ug.air.alrite.Activities.PatientActivity;
import com.ug.air.alrite.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParagraphFragment extends Fragment {

    public interface onGetResultListener {
        void getResultFromParagraphFragment() throws JSONException;
        void getLastPage() throws JSONException;
    }
    onGetResultListener getResultListener;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String QUESTION = "question";
    public static final String PARAGRAPH = "paragraph";
    public static final String DEFAULT = "**default string**";
    String previousResponse;
    String paragraph;
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
     * This creates a new MultipleChoiceFragment with a separate constructor, so
     * that we can retain the given information and return the fragment
     *
     * @param paragraph the text at the top of the page
     * @return the fragment to be used in the future
     */
    public static ParagraphFragment newInstance(String paragraph) throws JSONException {
        ParagraphFragment pf = new ParagraphFragment();
        Bundle args = new Bundle();
        args.putString(PARAGRAPH, paragraph);
        pf.setArguments(args);
        return pf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.paragraph, container, false);
        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        assert getArguments() != null;
        paragraph = getArguments().getString(PARAGRAPH);

        TextView paragraphDisplay = view.findViewById(R.id.p_information);
        paragraphDisplay.setText(paragraph);

        nextButton = view.findViewById(R.id.next);
        backButton = view.findViewById(R.id.back);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getResultListener.getResultFromParagraphFragment();
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
}