package com.etrading.client_connector.parser;

import java.util.HashMap;
import java.util.Map;

public class FixParser {

    public static Map<String, String> parse(String fix) {
        Map<String, String> map = new HashMap<>();
        String[] fields = fix.split("\\u0001"); // ASCII 1 is the delimiter
        for (String field : fields) {
            String[] kv = field.split("=");
            if (kv.length == 2) map.put(kv[0], kv[1]);
        }
        return map;
    }

}