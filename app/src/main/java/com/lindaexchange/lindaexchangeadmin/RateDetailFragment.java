package com.lindaexchange.lindaexchangeadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RateDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RateDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RateDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ExchangeRateDB rate;
    private RecyclerView recyclerView;
    private int rateIndex;

    private EditText countryNameEditText;
    private EditText currencyNameEditText;
    private EditText flagEditText;

    private OnFragmentInteractionListener mListener;

    public RateDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RateDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RateDetailFragment newInstance(String param1, String param2) {
        RateDetailFragment fragment = new RateDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            rateIndex = getArguments().getInt("rateIndex", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rate_detail, container, false);
        countryNameEditText = (EditText) view.findViewById(R.id.rateCountryEditText);
        currencyNameEditText = (EditText) view.findViewById(R.id.rateCurrencyEditText);
        flagEditText = (EditText) view.findViewById(R.id.rateFlagEditText);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

        if (rateIndex < 0) {        // New Rate
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addRate();
                }
            });
        } else {                    // For existed rate
            fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRate(false);
            }
        });
            setRate();
        }

        return view;
    }

    private void setRate() {
        String rateString = getString(R.string.rate);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(rateString).child(String.valueOf(rateIndex));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ExchangeRateDB rateDB = dataSnapshot.getValue(ExchangeRateDB.class);
                if (rateDB != null) {
                    countryNameEditText.setText(rateDB.getCountryname());
                    currencyNameEditText.setText(rateDB.getCurrencyname());
                    flagEditText.setText(rateDB.getFlag());
                    rate = rateDB;
                    if (rate.getRate() != null) {
                        recyclerView.setAdapter(new DenominationRecyclerViewAdapter(rate.getRate(), rateIndex, mListener));
                    } else {
                        recyclerView.setAdapter(new DenominationRecyclerViewAdapter(new ArrayList<DenominationDB>(), rateIndex, mListener));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addRate() {
        String rateString = getString(R.string.rate);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(rateString);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                    rateArray = new ArrayList<ExchangeRateDB>();
                GenericTypeIndicator<List<ExchangeRateDB>> t = new GenericTypeIndicator<List<ExchangeRateDB>>() {};
                List<ExchangeRateDB> rateArray = dataSnapshot.getValue(t);
                rateIndex = rateArray.size();
                saveRate(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveRate(final boolean isNewRate) {
        String countryName = countryNameEditText.getText().toString();
        String currencyName = currencyNameEditText.getText().toString();
        String flagUrl = flagEditText.getText().toString();
        Map<String, Object> map = new HashMap<>();
        String rateString = getString(R.string.rate);
        map.put(rateString + "/" + String.valueOf(rateIndex) + "/countryname", countryName);
        map.put(rateString + "/" + String.valueOf(rateIndex) + "/currencyname", currencyName);
        map.put(rateString + "/" + String.valueOf(rateIndex) + "/flag", flagUrl);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.updateChildren(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    showAlert("Finished");
                    if (isNewRate) {
                        setRate();
                    }
                } else {
                    showAlert("ERROR!");
                }
            }
        });
    }

    private void showAlert(String title) {
//        showProgress(false);
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
            builder.setTitle(title)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue
                        }
                    })
                    .show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
        void showDenominationDetail(int rateIndex, int denominationIndex);
        void deleteDenomination(int rateIndex, int denominationIndex);
    }
}