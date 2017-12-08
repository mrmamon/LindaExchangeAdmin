package com.lindaexchange.lindaexchangeadmin;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Milion on 11/4/2017.
 */

public class BuySellRecyclerViewAdapter extends RecyclerView.Adapter<BuySellRecyclerViewAdapter.ViewHolder> {
    private List<RateValueDB> rateList;
    private int branchIndex;
    private RateValueUpdateFragment.OnFragmentInteractionListener mListener;

    public BuySellRecyclerViewAdapter(List<RateValueDB> rateList, int branchIndex, RateValueUpdateFragment.OnFragmentInteractionListener listener) {
        this.rateList = rateList;
        this.branchIndex = branchIndex;
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.buy_sell_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (rateList.size() > position) {
            RateValueDB exchangeRate = rateList.get(position);
            holder.branchName.setText(exchangeRate.getCurrencyname() + " " + exchangeRate.getDenominationname());
            holder.rateDB = exchangeRate;
            holder.buyEditText.setText(exchangeRate.getBuy());
            holder.sellEditText.setText(exchangeRate.getSell());
        } else {

        }
        holder.buyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (rateList.size() > position) {
                    rateList.get(position).setBuy(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        holder.sellEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (rateList.size() > position) {
                    rateList.get(position).setSell(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return rateList.size();
    }

    public Map<String, Object> getMap() {
        Map<String, Object> denominationRate = new HashMap<>();
        for (RateValueDB rate : rateList) {
            denominationRate.putAll(rate.getMap());
        }
        return denominationRate;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView branchName;
        public EditText buyEditText;
        public EditText sellEditText;
        public RateValueDB rateDB;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            branchName = (TextView) view.findViewById(R.id.branchNameTextView);
            buyEditText = (EditText) view.findViewById(R.id.buyEditText);
            sellEditText = (EditText) view.findViewById(R.id.sellEditText);
        }
    }
}
