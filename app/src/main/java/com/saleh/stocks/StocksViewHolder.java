package com.saleh.stocks;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.saleh.stocks.R.*;

public class StocksViewHolder extends RecyclerView.ViewHolder {

    TextView codeText;
    TextView priceText;
    TextView dataText;
    TextView companyText;


    @SuppressLint("ResourceAsColor")
    public StocksViewHolder(@NonNull View itemView) {
        super(itemView);
        codeText = itemView.findViewById(id.codeText);
        codeText.setTextColor(color.teal_700);
        priceText = itemView.findViewById(id.priceText);
        priceText.setTextColor(color.teal_700);
        dataText = itemView.findViewById(id.dataText);
        dataText.setTextColor(color.teal_700);
        companyText = itemView.findViewById(id.companyText);
        companyText.setTextColor(color.teal_700);
    }
}
