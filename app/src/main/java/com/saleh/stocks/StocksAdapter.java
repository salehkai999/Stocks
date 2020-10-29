package com.saleh.stocks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static com.saleh.stocks.R.color;

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

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull StocksViewHolder holder, int position) {

        Stocks stocks = stocksList.get(position);
        holder.companyText.setText(stocks.getCompanyName());
        holder.priceText.setText(stocks.getPrice()+"");
        holder.codeText.setText(stocks.getSymbol());
        String changePercent = "("+stocks.getChangePercent()*100+"%)";
        String logo = "-";
        holder.dataText.setText(stocks.getChange()+" "+changePercent);
        if(stocks.getChange() > 0){
            holder.companyText.setTextColor(mActivity.getResources().getColor(color.green));
            holder.dataText.setTextColor(mActivity.getResources().getColor(color.green));
            holder.priceText.setTextColor(mActivity.getResources().getColor(color.green));
            holder.codeText.setTextColor(mActivity.getResources().getColor(color.green));
        }
        else if (stocks.getChange() < 0) {
            holder.companyText.setTextColor(mActivity.getResources().getColor(color.red));
            holder.dataText.setTextColor(mActivity.getResources().getColor(color.red));
            holder.priceText.setTextColor(mActivity.getResources().getColor(color.red));
            holder.codeText.setTextColor(mActivity.getResources().getColor(color.red));
        }
        else {
            holder.companyText.setTextColor(mActivity.getResources().getColor(color.white));
            holder.dataText.setTextColor(mActivity.getResources().getColor(color.white));
            holder.priceText.setTextColor(mActivity.getResources().getColor(color.white));
            holder.codeText.setTextColor(mActivity.getResources().getColor(color.white));
        }


    }

    @Override
    public int getItemCount() {
        return stocksList.size();
    }
}
