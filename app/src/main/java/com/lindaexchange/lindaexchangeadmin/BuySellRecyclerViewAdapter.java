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
    private DenominationDB denomination;
    private List<List<BranchDB>> branchList;
    private DenominationDetailFragment.OnFragmentInteractionListener mListener;

    public BuySellRecyclerViewAdapter(DenominationDB denomination, List<List<BranchDB>> branchList, DenominationDetailFragment.OnFragmentInteractionListener listener) {
        this.denomination = denomination;
        this.branchList = branchList;
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
        BranchDB branchDB = branchList.get(position).get(0);
        holder.branchDB = branchDB;
        holder.branchName.setText(branchDB.getName());
        if (denomination.getDenominationrate().size() > position) {
            RateDB rateDB = denomination.getDenominationrate().get(position);
            holder.rateDB = rateDB;
            holder.buyEditText.setText(rateDB.getBuy());
            holder.sellEditText.setText(rateDB.getSell());
        } else {

        }
        holder.buyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (denomination.getDenominationrate().size() > position) {
                    denomination.getDenominationrate().get(position).setBuy(s.toString());
                } else {
                    RateDB rate = new RateDB(s.toString(), "0");
                    denomination.getDenominationrate().add(rate);
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
                if (denomination.getDenominationrate().size() > position) {
                    denomination.getDenominationrate().get(position).setSell(s.toString());
                } else {
                    RateDB rate = new RateDB(s.toString(), "0");
                    denomination.getDenominationrate().add(rate);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public int getItemCount() {
        int branchSize = branchList.size();
//        int rateSize = denomination.getDenominationrate().size();
        return branchSize;
    }

    public Map<String, Object> getMap() {
        Map<String, Object> denominationRate = new HashMap<>();
        for (int i = 0; i < branchList.size(); i++) {
            Map<String, Object> rate = new HashMap<>();
            rate.put("buy", denomination.getDenominationrate().get(i).getBuy());
            rate.put("sell", denomination.getDenominationrate().get(i).getSell());
            denominationRate.put(String.valueOf(i), rate);
        }
        return denominationRate;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView branchName;
        public EditText buyEditText;
        public EditText sellEditText;
        public BranchDB branchDB;
        public RateDB rateDB;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            branchName = (TextView) view.findViewById(R.id.branchNameTextView);
            buyEditText = (EditText) view.findViewById(R.id.buyEditText);
            sellEditText = (EditText) view.findViewById(R.id.sellEditText);
        }
    }
}
