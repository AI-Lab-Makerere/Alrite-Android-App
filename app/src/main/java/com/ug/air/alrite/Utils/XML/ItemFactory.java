package com.ug.air.alrite.Utils.XML;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemFactory {

    private static final String ns = null;
    List<String> values = new ArrayList<>();

    public String GetMaleItem(Context context, String age, float weight) throws XmlPullParserException, IOException {

        List<String> elements;
        String diagnosis;

        XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = parserFactory.newPullParser();
        InputStream inputStream = context.getAssets().open("male.xml");
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(inputStream, null);
        parser.nextTag();
        elements = readFeed(parser, age);
        String negative3 = elements.get(0);
        String negative2 = elements.get(1);
        String positive2 = elements.get(2);
        float n3 = Float.parseFloat(negative3);
        float n2 = Float.parseFloat(negative2);
        float p2 = Float.parseFloat(positive2);

        if (weight >= n2 && weight <= p2){
//            acceptable
            diagnosis = "acceptable";
        }else if (weight > p2){
//            greater than 2 SD above average
            diagnosis = "above 2";
        }else if (weight >= n3){
//            less than 2 SD below average, moderate malnutrition
            diagnosis = "below 2";
        }else{
//            less than 3 SD below average, severe malnutrition
            diagnosis = "below 3";
        }

        return diagnosis;

    }

    public String GetFemaleItem(Context context, String age, float weight) throws XmlPullParserException, IOException {

        List<String> elements;
        String diagnosis;

        XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = parserFactory.newPullParser();
        InputStream inputStream = context.getAssets().open("female.xml");
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(inputStream, null);
        parser.nextTag();
        elements = readFeed(parser, age);
        System.out.println(elements);
        String negative3 = elements.get(0);
        String negative2 = elements.get(1);
        String positive2 = elements.get(2);
        float n3 = Float.parseFloat(negative3);
        float n2 = Float.parseFloat(negative2);
        float p2 = Float.parseFloat(positive2);

        if (weight >= n2 && weight <= p2){
//            acceptable
            diagnosis = "acceptable";
        }else if (weight > p2){
//            greater than 2 SD above average
            diagnosis = "above 2";
        }else if (weight >= n3){
//            less than 2 SD below average, moderate malnutrition
            diagnosis = "below 2";
        }else{
//            less than 3 SD below average, severe malnutrition
            diagnosis = "below 3";
        }

        return diagnosis;

    }

    private List<String> readFeed(XmlPullParser parser, String year) throws IOException, XmlPullParserException {
        ArrayList<Item> items = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "root");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the item tag
            if (name.equals("item")) {
                items.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }

        if (year.endsWith("10")){
            year = year + "1";
        }

        for(Item item : items){
            String age = item.age;
            if (age.equals(year)){
                values = Arrays.asList(item.negative3, item.negative2, item.positive2);
            }
        }

//        Toast.makeText(context, ""+values, Toast.LENGTH_LONG).show();

        return values;
    }

    private Item readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "item");
        String age = null;
        String negative3 = null;
        String negative2 = null;
        String positive2 = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "age":
                    age = readAge(parser);
                    break;
                case "negative3":
                    negative3 = readNegative3(parser);
                    break;
                case "negative2":
                    negative2 = readNegative2(parser);
                    break;
                case "positive2":
                    positive2 = readPositive2(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new Item(age, negative3, negative2, positive2);
    }

    private String readAge(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "age");
        String age = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "age");
        return age;
    }

    private String readNegative3(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "negative3");
        String negative3 = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "negative3");
        return negative3;
    }

    private String readNegative2(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "negative2");
        String negative2 = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "negative2");
        return negative2;
    }

    private String readPositive2(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "positive2");
        String positive2 = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "positive2");
        return positive2;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}
