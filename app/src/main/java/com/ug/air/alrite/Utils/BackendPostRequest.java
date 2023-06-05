package com.ug.air.alrite.Utils;

import java.util.HashMap;

public class BackendPostRequest {
    HashMap<String, String> summary;
    String diagnoses;

    public BackendPostRequest(HashMap<String, String> summary, String diagnoses) {
        this.summary = summary;
        this.diagnoses = diagnoses;
    }
}
