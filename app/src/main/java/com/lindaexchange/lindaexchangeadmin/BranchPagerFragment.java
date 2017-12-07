package com.lindaexchange.lindaexchangeadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BranchPagerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BranchPagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BranchPagerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int branchIndex;
    private BranchPagerAdapter adapter;

    private OnFragmentInteractionListener mListener;

    public BranchPagerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BranchPagerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BranchPagerFragment newInstance(String param1, String param2) {
        BranchPagerFragment fragment = new BranchPagerFragment();
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
            branchIndex = getArguments().getInt("branchIndex", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_branch_pager, container, false);
        TabLayout tab = (TabLayout) view.findViewById(R.id.branchTab);
        tab.addTab(tab.newTab().setText(R.string.thai));
        tab.addTab(tab.newTab().setText(R.string.english));
        tab.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.branchViewPager);
        BranchDetailFragment thaiFragment = new BranchDetailFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("branchIndex", branchIndex);
        arguments.putInt("language", 0);
        thaiFragment.setArguments(arguments);

        BranchDetailFragment englishFragment = new BranchDetailFragment();
        Bundle arguments1 = new Bundle();
        arguments1.putInt("branchIndex", branchIndex);
        arguments1.putInt("language", 1);
        englishFragment.setArguments(arguments1);

        adapter = new BranchPagerAdapter(getChildFragmentManager(), thaiFragment, englishFragment);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        if (branchIndex < 0) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addBranch();
                }
            });
        } else {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveBranch();
                }
            });
        }
        return view;
    }

    private void addBranch() {
        String branchString = getString(R.string.branch);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query reference = database.getReference(branchString).orderByKey().limitToLast(1);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    branchIndex = 0;
                    saveBranch();
                } else {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        branchIndex = Integer.parseInt(child.getKey()) + 1;
                        saveBranch();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveBranch() {
        List<Map<String, Object>> mapList = adapter.getMap();
        Map<String, Object> map = mapList.get(0);
        Map<String, Object> thMap = mapList.get(1);
        Map<String, Object> enMap = mapList.get(2);

        Uri thUri = (Uri) map.get("thUri");
        Uri enUri = (Uri) map.get("enUri");
        if (thUri != null) {
            uploadThaiImage(thUri, enUri, thMap, enMap);
        } else {
            prepareToUploadEnglishImage(enUri, thMap, enMap);
        }
    }

    private void uploadThaiImage(Uri thUri, final Uri enUri, final Map<String, Object> thMap, final Map<String, Object> enMap) {
        String fileName = String.valueOf(branchIndex) + "_0.jpg";
        StorageReference ref = FirebaseStorage.getInstance().getReference(getString(R.string.news)).child(fileName);
        UploadTask uploadTask;
        uploadTask = ref.putFile(thUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getDownloadUrl() != null) {
                    String ref = taskSnapshot.getDownloadUrl().toString();
                    thMap.put("photo", ref);
                    prepareToUploadEnglishImage(enUri, thMap, enMap);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                prepareToUploadEnglishImage(enUri, thMap, enMap);
            }
        });
    }

    private void prepareToUploadEnglishImage(Uri enUri, Map<String, Object> thMap, Map<String, Object> enMap) {
        if (enUri != null) {
            uploadEnglishImage(enUri, thMap, enMap);
        } else {
            updateBranch(thMap, enMap);
        }
    }

    private void uploadEnglishImage(Uri enUri, final Map<String, Object> thMap, final Map<String, Object> enMap) {
        String fileName = String.valueOf(branchIndex) + "_1.jpg";
        StorageReference ref = FirebaseStorage.getInstance().getReference(getString(R.string.news)).child(fileName);
        UploadTask uploadTask;
        uploadTask = ref.putFile(enUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getDownloadUrl() != null) {
                    String ref = taskSnapshot.getDownloadUrl().toString();
                    enMap.put("photo", ref);
                    updateBranch(thMap, enMap);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                updateBranch(thMap, enMap);
            }
        });
    }

    private void updateBranch(Map<String, Object> thMap, Map<String, Object> enMap) {
        String refString = getString(R.string.branch) + "/" + String.valueOf(branchIndex) + "/";
        Map<String, Object> map = new HashMap<>();
        map.put(refString + "0", thMap);
        map.put(refString + "1", enMap);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.updateChildren(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
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
}
