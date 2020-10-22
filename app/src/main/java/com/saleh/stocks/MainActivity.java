package com.saleh.stocks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {


    private final List<Stocks> stocksList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StocksAdapter stocksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        stocksAdapter = new StocksAdapter(stocksList,this);
        recyclerView.setAdapter(stocksAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        for(int i=0;i<5;i++) {
            stocksList.add(new Stocks("ABC"+i,"Name"+i,0,0,0));
        }
        stocksAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "OnClick", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(this, "OnLongClick", Toast.LENGTH_LONG).show();
        return true;
    }
}