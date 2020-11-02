package com.saleh.stocks;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.saleh.stocks.downloaders.StockDownloader;
import com.saleh.stocks.downloaders.SymbolNameDownloader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, SwipeRefreshLayout.OnRefreshListener {


    private final List<Stocks> stocksList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StocksAdapter stocksAdapter;
    private SwipeRefreshLayout swiper;
    private static final String TAG = "MainActivity";
    private static String stocksURL = "https://www.marketwatch.com/investing/stock/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        swiper = findViewById(R.id.swipe);
        stocksAdapter = new StocksAdapter(stocksList,this);
        recyclerView.setAdapter(stocksAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SymbolNameDownloader symbolNameDownloader = new SymbolNameDownloader();
        //StockDownloader stockDownloader = new StockDownloader(this,"TGT");
        loadJSONFile();
        if(isConnected()) {
            new Thread(symbolNameDownloader).start();
            for(int i=0;i<stocksList.size();i++) {
                new Thread(new StockDownloader(this, stocksList.get(i).getSymbol(),true,i)).start();
            }
        }
        else {
            noNetworkDialog();
            for(int i=0;i<stocksList.size();i++)
            {
                stocksList.get(i).setPrice(0.0);
                stocksList.get(i).setChange(0.0);
                stocksList.get(i).setChangePercent(0.0);
            }
        }
        //new Thread(stockDownloader).start();



         /*for(int i=0;i<stocksList.size();i++) {
            //stocksList.add(new Stocks("ABC"+i,"Name"+i,0,0,0));
             stocksList.get(i).setChange(0.0);
             stocksList.get(i).setPrice(0.0);
        }*/
        stocksAdapter.notifyDataSetChanged();
        swiper.setOnRefreshListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu_layout,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add) {
            Toast.makeText(this, "ADD", Toast.LENGTH_SHORT).show();
            if(isConnected())
                choiceDialog();
            else {
                noNetworkDialog();
            }
            //stocksList.add(new Stocks("TGT","Target",0.0,0.0,0.0));
            //stocksAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void noNetworkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network");
        builder.setMessage("Not Connected to the Internet");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        //Toast.makeText(this, "OnClick", Toast.LENGTH_SHORT).show();
        int index = recyclerView.getChildLayoutPosition(v);
        Uri.Builder builder = Uri.parse(stocksURL+stocksList.get(index).getSymbol()).buildUpon();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            intent.setData(Uri.parse(String.valueOf(new URL(builder.toString()))));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        final int index = recyclerView.getChildLayoutPosition(v);
        //Toast.makeText(this, "OnLongClick "+stocksList.get(index).getSymbol(), Toast.LENGTH_LONG).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stocksList.remove(index);
                stocksAdapter.notifyDataSetChanged();
                saveJSON();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setMessage("Delete "+stocksList.get(index).getSymbol()+"?");
        builder.setTitle("Deletion");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return true;
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveJSON();
    }

    private void saveJSON() {
        if(!stocksList.isEmpty()) {
            try {
                FileOutputStream fileOutputStream = getApplicationContext().openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);
                JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                jsonWriter.beginArray();
                for (Stocks stock : stocksList) {
                    jsonWriter.setIndent("  ");
                    jsonWriter.beginObject();
                    jsonWriter.name("symbol").value(stock.getSymbol());
                    jsonWriter.name("companyName").value(stock.getCompanyName());
                    jsonWriter.name("latestPrice").value(stock.getPrice());
                    jsonWriter.name("change").value(stock.getChange());
                    jsonWriter.name("changePercent").value(stock.getChangePercent());
                    jsonWriter.endObject();
                }
                jsonWriter.endArray();
                jsonWriter.close();
                Log.d(TAG, "saveJSON: " + stocksList.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadJSONFile(){
        try {
            InputStream inputStream = getApplicationContext().openFileInput(getString(R.string.file_name));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                Log.d(TAG, "loadJSONDate: " + line);
            }
            JSONArray jsonArray = new JSONArray(builder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Stocks stocks = new Stocks();
                stocks.setSymbol(jsonObject.getString("symbol"));
                stocks.setCompanyName(jsonObject.getString("companyName"));
                stocks.setPrice(jsonObject.getDouble("latestPrice"));
                stocks.setChange(jsonObject.getDouble("change"));
                stocks.setChangePercent(jsonObject.getDouble("changePercent"));
                stocksList.add(stocks);
            }
            Collections.sort(stocksList);
            stocksAdapter.notifyDataSetChanged();
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "NO FILE", Toast.LENGTH_SHORT).show();
        }

    }

    private void choiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText stockText = new EditText(this);
        stockText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_CLASS_TEXT);
        stockText.setGravity(Gravity.CENTER_HORIZONTAL);
        //stockText.
        //stockText.
        builder.setView(stockText);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = stockText.getText().toString().trim();
                Log.d(TAG, "onClick: "+text);
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
                listDialog(text);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "CANCELED", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setMessage("Enter Symbol or Name");
        builder.setTitle("Select Stock");

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void listDialog(String choice){
        final ArrayList<String> results = SymbolNameDownloader.getMatches(choice);
        if(results.size() == 0) {
            Toast.makeText(this, "No Such Stock", Toast.LENGTH_SHORT).show();
            noSuchStockDialog(choice);
        }
        else if(results.size() == 1) {
            Toast.makeText(this, results.get(0), Toast.LENGTH_SHORT).show();
            processSelectedSymbol(results.get(0));
        }
        else {
            String[] resultArray = results.toArray(new String[0]);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Make Selection");
            builder.setItems(resultArray, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String symbol = results.get(which);
                    Toast.makeText(MainActivity.this, symbol, Toast.LENGTH_SHORT).show();
                    processSelectedSymbol(symbol);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }


    private void processSelectedSymbol(String symbol){
        String[] data = symbol.split("-");
        StockDownloader stockDownloader = new StockDownloader(this,data[0].trim());
        new Thread(stockDownloader).start();
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
           return true;
        } else {
            return false;
        }
    }


    public void saveStock(Stocks stocks) {
        if(stocks == null)
        {
            Toast.makeText(this, "NULL", Toast.LENGTH_SHORT).show();
            return;
        }
        if(stocksList.contains(stocks)) {
            duplicatedDialog(stocks.getSymbol());
            Toast.makeText(this, "Already Existing", Toast.LENGTH_SHORT).show();
            return;
        }
        swiper.setRefreshing(false);
        stocksList.add(stocks);
        Collections.sort(stocksList);
        stocksAdapter.notifyDataSetChanged();
        saveJSON();

    }

    @Override
    public void onRefresh() {
        Toast.makeText(this, "Refreshing", Toast.LENGTH_SHORT).show();
        if(!isConnected()) {
            noNetworkDialog();
            swiper.setRefreshing(false);
            return;
        }
        for(int i=0;i<stocksList.size();i++) {
            new Thread(new StockDownloader(this, stocksList.get(i).getSymbol(),true,i)).start();
            //stocksList.remove(i);
            //stocksAdapter.notifyDataSetChanged();
        }
        //swiper.setRefreshing(false);
    }

    public void updateStock(Stocks stocks, int index) {
        stocksList.get(index).setChangePercent(stocks.getChangePercent());
        stocksList.get(index).setChange(stocks.getChange());
        stocksList.get(index).setPrice(stocks.getPrice());
        stocksAdapter.notifyDataSetChanged();
        Log.d(TAG, "updateStock: "+stocks.getChange());
        swiper.setRefreshing(false);
    }

    private void duplicatedDialog(String symbol){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Duplicate Stock");
        builder.setMessage("Stock "+symbol+" Already Exists");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void noSuchStockDialog(String choice) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stock Not Found: "+choice);
        builder.setMessage("No Such Data!");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}