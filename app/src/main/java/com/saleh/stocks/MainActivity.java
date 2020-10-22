package com.saleh.stocks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {


    private final List<Stocks> stocksList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StocksAdapter stocksAdapter;
    private SwipeRefreshLayout swiper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        swiper = findViewById(R.id.swipe);
        stocksAdapter = new StocksAdapter(stocksList,this);
        recyclerView.setAdapter(stocksAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        for(int i=0;i<5;i++) {
            stocksList.add(new Stocks("ABC"+i,"Name"+i,0,0,0));
        }
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
            return true;
        }
        return super.onOptionsItemSelected(item);
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