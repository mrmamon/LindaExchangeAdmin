package com.lindaexchange.lindaexchangeadmin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DenominationDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DenominationDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DenominationDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
//    private String child;
    private int rateIndex;
    private int denominationIndex;

    private DenominationDB denomination;

    private EditText denominationNameEditText;
    private View mContentView;
    private ProgressBar mProgressView;
    private FloatingActionButton fab;

    private CountDownTimer timer = new CountDownTimer(10000, 10000) {
        @Override
        public void onTick(long millisUntilFinished) { }

        @Override
        public void onFinish() { showTimeoutSnackbar(); }
    };

    private OnFragmentInteractionListener mListener;

    public DenominationDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DenominationDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DenominationDetailFragment newInstance(String param1, String param2) {
        DenominationDetailFragment fragment = new DenominationDetailFragment();
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
//            child = getArguments().getString("child", "");
            rateIndex = getArguments().getInt("rateIndex", 0);
            denominationIndex = getArguments().getInt("denominationIndex", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_denomination_detail, container, false);
        denominationNameEditText = (EditText) view.findViewById(R.id.denominationNameEditText);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveClicked();
            }
        });

        mContentView = view.findViewById(R.id.content);
        mProgressView = (ProgressBar) view.findViewById(R.id.progressBar);

        if (denominationIndex < 0) {
            denomination = new DenominationDB("");
        } else {
            setDenomination();
        }

        return view;
    }

    private void setDenomination() {
        showProgress(true);
        String denominationString = getString(R.string.rate);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        String child = String.valueOf(rateIndex) + "/rate/" + String.valueOf(denominationIndex);
        DatabaseReference ref = database.getReference(denominationString).child(child);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DenominationDB denominationDB = dataSnapshot.getValue(DenominationDB.class);
                if (denominationDB != null) {
                    denominationNameEditText.setText(denominationDB.getDenominationname());
                    denomination = denominationDB;
                }
                showProgress(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showProgress(false);
                showAlert("ERROR!");
            }
        });
    }

    private void saveClicked() {
        if (denominationIndex == -1) {
            showProgress(true);
            String rateString = getString(R.string.rate);
            final FirebaseDatabase database = FirebaseDatabase.getInstance();

            // exchangerate/0/rate/0
            Query ref = database.getReference(rateString).child(String.valueOf(rateIndex)).child(getString(R.string.rate_rate)).orderByKey().limitToLast(1);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    showProgress(false);
                    if (!dataSnapshot.hasChildren()) {
                        denominationIndex = 0;
                        saveRate();
                    }
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        denominationIndex = Integer.parseInt(child.getKey()) + 1;
                        saveRate();
                        break;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    showProgress(false);
                    showAlert("ERROR!");
                }
            });
        } else if (denominationIndex >= 0) {
            saveRate();
        }
    }

    private void saveRate() {
        showProgress(true);
        Map<String, Object> item = new HashMap<>();
        String refString = String.valueOf(rateIndex) + "/rate/" + String.valueOf(denominationIndex) + "/" + getString(R.string.rate_denominationname);
        item.put(refString, denominationNameEditText.getText().toString());
        String denominationString = getString(R.string.rate);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(denominationString);
        ref.updateChildren(item, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                showProgress(false);
                if (databaseError == null) {
                    showAlert(getString(R.string.finish));
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
    }

    private void showProgress(final boolean show) {
        if (show) {
            timer.start();
        } else {
            timer.cancel();
        }

        int shortAnimTime = 200;
        try {
            shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        mContentView.setVisibility(show ? View.GONE : View.VISIBLE);
        mContentView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mContentView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        fab.setVisibility(show ? View.GONE : View.VISIBLE);
        fab.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fab.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void showTimeoutSnackbar() {
        Snackbar snackbar = Snackbar.make(mContentView, R.string.timeout_string, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}
