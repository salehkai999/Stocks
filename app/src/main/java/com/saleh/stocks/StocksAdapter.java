package com.saleh.stocks;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StocksAdapter extends RecyclerView.Adapter<StocksViewHolder> {

    private List<Stocks> stocksList;
    private MainActivity mActivity;

    public StocksAdapter(List<Stocks> stocksList, MainActivity mActivity) {
        this.stocksList = stocksList;
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public StocksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stocks_row_layout,parent,false);
        itemView.setOnClickListener(mActivity);
        itemView.setOnLongClickListener(mActivity);

        return new StocksViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StocksViewHolder holder, int position) {

        Stocks stocks = stocksList.get(position);
        holder.companyText.setText(stocks.getCompanyName());
        holder.priceText.setText(stocks.getPrice()+"");
        holder.codeText.setText(stocks.getSymbol());
        holder.dataText.setText(stocks.getChange()+" "+stocks.getChangePercent());


    }

    @Override
    public int getItemCount() {
        return stocksList.size();
    }
}
