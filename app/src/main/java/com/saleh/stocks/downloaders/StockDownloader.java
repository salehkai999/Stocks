package com.saleh.stocks.downloaders;

import android.net.Uri;
import android.util.Log;

import com.saleh.stocks.MainActivity;
import com.saleh.stocks.Stocks;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class StockDownloader implements Runnable {
    private static final String TAG = "StockDownloader";
    private static final String  API_URL = "https://cloud.iexapis.com/stable/stock/";
    private static final String API_KEY = "/quote?token=pk_de463bd240894f9695c33a9b8735fcce ";
    private MainActivity mainActivity;
    private String symbol;
    private boolean update = false;
    private int index = -1;

    public StockDownloader(MainActivity mainActivity, String symbol) {
        this.mainActivity = mainActivity;
        this.symbol = symbol;
    }

    public StockDownloader(MainActivity mainActivity, String symbol, boolean update, int index) {
        this.mainActivity = mainActivity;
        this.symbol = symbol;
        this.update = update;
        this.index = index;
    }

    @Override
    public void run() {

        Uri.Builder builder = Uri.parse(API_URL+symbol+API_KEY).buildUpon();
        String stockURL =builder.toString();
        Log.d(TAG, "run: "+stockURL);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(stockURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                Log.d(TAG, "run: "+urlConnection.getResponseCode());
                return;
            }
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String data;
            while ((data = bufferedReader.readLine()) != null) {
                stringBuilder.append(data).append("\n");
            }
            Log.d(TAG, "run: "+stringBuilder.toString());

        }
        catch (Exception e){
            e.printStackTrace();
        }

        process(stringBuilder.toString());
    }

    private void process(String stock){
        try{
            //JSONArray jsonArray = new JSONArray(stock);
            JSONObject jsonObject = new JSONObject(stock);
            String symbol = jsonObject.getString("symbol");
            String companyName = jsonObject.getString("companyName");
            String priceString = jsonObject.getString("latestPrice");
            double price = 0.0;
            if(!priceString.isEmpty())
                price = Double.parseDouble(priceString);
            String changeString = jsonObject.getString("change");
            double change =0.0;
            if(!changeString.isEmpty())
                change = Double.parseDouble(changeString);
            String percentageString = jsonObject.getString("changePercent");
            double percentage = 0.0;
            if(!percentageString.isEmpty())
                percentage = Double.parseDouble(percentageString);

            final Stocks stocks = new Stocks(symbol,companyName,change,percentage,price);
            Log.d(TAG, "process: "+stocks.toString());

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(update)
                        mainActivity.updateStock(stocks,index);
                    else
                        mainActivity.saveStock(stocks);
                }
            });

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
