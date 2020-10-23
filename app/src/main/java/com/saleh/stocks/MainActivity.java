package com.saleh.stocks;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.saleh.stocks.downloaders.SymbolNameDownloader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {


    private final List<Stocks> stocksList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StocksAdapter stocksAdapter;
    private SwipeRefreshLayout swiper;
    private static final String TAG = "MainActivity";
    private static String stocksURL = "https://www.marketwatch.com/";
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
        new Thread(symbolNameDownloader).start();

        loadJSONFile();
         /*for(int i=0;i<5;i++) {
            stocksList.add(new Stocks("ABC"+i,"Name"+i,0,0,0));
        }*/
        stocksAdapter.notifyDataSetChanged();
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(MainActivity.this, "Refresh", Toast.LENGTH_SHORT).show();
                swiper.setRefreshing(false);
            }
        });

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
            choiceDialog();
            //stocksList.add(new Stocks("TGT","Target",0.0,0.0,0.0));
            //stocksAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "OnClick", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(stocksURL));
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(this, "OnLongClick", Toast.LENGTH_LONG).show();
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
        stockText.setInputType(InputType.TYPE_CLASS_TEXT);
        stockText.setGravity(Gravity.CENTER_HORIZONTAL);
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

    }

}