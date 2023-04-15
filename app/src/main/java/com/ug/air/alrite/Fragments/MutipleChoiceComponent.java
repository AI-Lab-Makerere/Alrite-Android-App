package com.ug.air.alrite.Fragments;

import java.util.HashMap;
import java.util.Map;

/**
 * List of choices that the user will make
 * Depending on the type of question the
 * items added to the list will differ
 * */

public class MutipleChoiceComponent {
    private Map<Integer, String> MultipleChoice;
    private Map<Boolean, String> TrueOrFalse;

    /**
     * Constructor
     * Creates a new, empty list
     */
    public MutipleChoiceComponent() {
        this.MultipleChoice = new HashMap<>();
        this.TrueOrFalse = new HashMap<>();
    }

    /**
     * Add a new choice into the table
     * Index of the list will be the choice number
     * @param ChoiceName Value to provide into the list
     * that corresponds to its value
     * @param ChoiceValue Value which corresponds to the numbering
     * of the choice within the Map
    */
    public void addBooleanValue(Boolean ChoiceValue, String ChoiceName) {
            if(ChoiceName == null) {
                throw new IllegalArgumentException("Need to have an input for the string");
            }
            if(ChoiceValue != true || ChoiceValue != false) {
                throw new IllegalArgumentException("For a true or false value we need to have a" +
                        "true or false choice answer");
            }
            this.TrueOrFalse.put(ChoiceValue, ChoiceName);
    }

    /**
     * Add a new choice into the table
     * Index of the list will be the choice number
     * @param ChoiceName Value to provide into the list
     * that corresponds to its value
     * @param ChoiceNum Value which corresponds to the numbering
     * of the choice within the Map
     */
    public void addMultipleChoiceValue(Integer ChoiceNum, String ChoiceName) {
        if(ChoiceName == null) {
            throw new IllegalArgumentException("Need to have an input for the string");
        }
        if(ChoiceNum == -1) {
            throw new IllegalArgumentException("Cannot have choice number be negative");
        }
        this.MultipleChoice.put(ChoiceNum, ChoiceName);
    }
}
