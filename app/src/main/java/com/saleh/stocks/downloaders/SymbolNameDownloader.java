package com.saleh.stocks.downloaders;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SymbolNameDownloader implements Runnable {

    private static final String TAG = "SymbolNameDownloader";
    private static final String STOCKS_SYMBOL_URL = "https://api.iextrading.com/1.0/ref-data/symbols";
    public static final HashMap<String,String> symbolNameMap = new HashMap<>();


    @Override
    public void run() {
        Uri stocksUri = Uri.parse(STOCKS_SYMBOL_URL);
        String urlString = stocksUri.toString();
        Log.d(TAG, "run: "+urlString);
        StringBuilder dataBuilder = new StringBuilder();

        try{
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                dataBuilder.append(line).append('\n');
            }

            Log.d(TAG, "run: " + dataBuilder.toString());

        }
        catch (Exception e){
            e.printStackTrace();
        }
        processData(dataBuilder.toString());

    }

    private void processData(String data) {
        try {
            JSONArray jsonArray = new JSONArray(data);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                String symbol = jsonObject.getString("symbol");
                String name = jsonObject.getString("name");
                Log.d(TAG, "processData: "+symbol+" : "+name);
                symbolNameMap.put(symbol, name);
            }
            Log.d(TAG, "process: ");
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getMatches(String match){
        String matcher = match.toLowerCase().trim();
        HashSet<String> matchSet = new HashSet<>();
        for(String symbol : symbolNameMap.keySet()) {
            if(symbol.trim().toLowerCase().contains(matcher)) {
                matchSet.add(symbol+" - "+symbolNameMap.get(symbol));
            }
            String name = symbolNameMap.get(symbol);
            if (name != null &&
                    name.toLowerCase().trim().contains(matcher)) {
                matchSet.add(symbol + " - " + name);
            }
        }
        ArrayList<String> matched = new ArrayList<>(matchSet);
        Collections.sort(matched);
        return matched;
    }

}
