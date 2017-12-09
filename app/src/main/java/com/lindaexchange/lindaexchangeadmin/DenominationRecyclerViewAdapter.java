package com.lindaexchange.lindaexchangeadmin;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Milion on 11/4/2017.
 */

public class DenominationRecyclerViewAdapter extends RecyclerView.Adapter<DenominationRecyclerViewAdapter.ViewHolder> {
    private List<DenominationDB> mValues;
    private RateDetailFragment.OnFragmentInteractionListener mListener;
    private int rateIndex;

    public DenominationRecyclerViewAdapter(List<DenominationDB> items, int rateIndex, RateDetailFragment.OnFragmentInteractionListener listener) {
        this.mValues = items;
        this.mListener = listener;
        this.rateIndex = rateIndex;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.denomination_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position < mValues.size()) {
            DenominationDB denominationDB = mValues.get(position);
            holder.mItem = denominationDB;
            String denominationName = denominationDB.getDenominationname();
            if (denominationName.equals("")) {
                denominationName = "Default";
            }
            holder.mName.setText(denominationName);
        } else if (position == mValues.size()) {
            holder.mItem = null;
            holder.mName.setText("เพิ่มหน่วยเงิน");
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView mName;
        public DenominationDB mItem;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.denomination_name);

            mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getLayoutPosition();
                    if (position >= 0 && position < mValues.size()) {
                        mListener.deleteDenomination(rateIndex, position, mValues.get(position).getDenominationname());
                    }
                    return true;
                }
            });

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    if (position == mValues.size()) {
                        mListener.showDenominationDetail(rateIndex, -1);
                    } else if (position >= 0 && position < mValues.size()) {
                        mListener.showDenominationDetail(rateIndex, position);
                    }
                }
            });
        }
    }
}
