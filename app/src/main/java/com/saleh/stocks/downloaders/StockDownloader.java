package com.saleh.stocks.downloaders;

import android.net.Uri;
import android.util.Log;

import com.saleh.stocks.MainActivity;

public class StockDownloader implements Runnable {
    private static final String TAG = "StockDownloader";
    private static final String  URL = "https://cloud.iexapis.com/stable/stock/";
    private static final String API_KEY = "/quote?token=pk_de463bd240894f9695c33a9b8735fcce ";
    private MainActivity mainActivity;
    private String symbol;

    public StockDownloader(MainActivity mainActivity, String symbol) {
        this.mainActivity = mainActivity;
        this.symbol = symbol;
    }


    @Override
    public void run() {

        Uri.Builder builder = Uri.parse(URL+symbol+API_KEY).buildUpon();
        String stockURL =builder.toString();
        Log.d(TAG, "run: "+stockURL);


    }
}
