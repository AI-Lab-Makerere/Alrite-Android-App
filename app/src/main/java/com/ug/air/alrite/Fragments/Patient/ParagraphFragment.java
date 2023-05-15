//package com.ug.air.alrite.Fragments.Patient;
//
//import static com.ug.air.alrite.Fragments.Patient.FTouch.TOUCH;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentTransaction;
//
//import com.ug.air.alrite.R;
//
//import org.json.JSONException;
//
//
//public class ParagraphFragment extends Fragment {
//    //Fragment wise should be done may need listener for skip
//    public interface onGetResultListener {
//        void getResultFromTextInputFragment(int choiceIndex) throws JSONException;
//
//        void getLastPage() throws JSONException;
//    }
//
//    onGetResultListener getResultListener;
//    View view;
//    EditText etDay;
//    Button back, next, btnSkip, btnSave;
//    String userInput, diagnosis;
//    public static final String Question = "What is the childs temperature";
//    public static final String TDIAGNOSIS = "diagnosis_fever";
//    public static final String SHARED_PREFS = "sharedPrefs";
//    SharedPreferences sharedPreferences;
//    SharedPreferences.Editor editor;
//    Dialog dialog;
//    TextView txtMessage;
//    public static final String text = "";
//    String text;
//    String question;
//
//    public static ParagraphFragment newInstance(String question, String content) throws JSONException {
//        ParagraphFragment pf = new ParagraphFragment();
//        Bundle args = new Bundle();
//        args.putString(Question, question);
//        args.putString(text, content);
//        pf.setArguments(args);
//        return pf;
//    }
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
//
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                back.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        try {
//                            getResultListener.getLastPage();
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                });
//            }
//        });
//
//        btnSkip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
//                fr.replace(R.id.fragment_container, new FTouch());
//                fr.addToBackStack(null);
//                fr.commit();
//            }
//        });
//
//        return view;
//    }