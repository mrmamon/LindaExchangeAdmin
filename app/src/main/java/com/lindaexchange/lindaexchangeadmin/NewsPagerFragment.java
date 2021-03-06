package com.lindaexchange.lindaexchangeadmin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsPagerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsPagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsPagerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int newsIndex;
    private NewsPagerAdapter adapter;

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

    public NewsPagerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsPagerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsPagerFragment newInstance(String param1, String param2) {
        NewsPagerFragment fragment = new NewsPagerFragment();
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
            newsIndex = getArguments().getInt("newsIndex", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_pager, container, false);
        mContentView = view.findViewById(R.id.content);
        mProgressView = (ProgressBar) view.findViewById(R.id.progressBar);

        TabLayout tab = (TabLayout) view.findViewById(R.id.newsTab);
        tab.addTab(tab.newTab().setText(R.string.thai));
        tab.addTab(tab.newTab().setText(R.string.english));
        tab.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.newsViewPager);
        NewsDetailFragment thaiFragment = new NewsDetailFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("newsIndex", newsIndex);
        arguments.putInt("language", 0);
        thaiFragment.setArguments(arguments);
        NewsDetailFragment englishFragment = new NewsDetailFragment();
        Bundle arguments1 = new Bundle();
        arguments1.putInt("newsIndex", newsIndex);
        arguments1.putInt("language", 1);
        englishFragment.setArguments(arguments1);

        adapter = new NewsPagerAdapter(getChildFragmentManager(), thaiFragment, englishFragment);
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

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        if (newsIndex < 0) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addNews();
                }
            });
        } else {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveNews();
                }
            });
        }

        return view;
    }

    private void addNews() {
        showProgress(true);
        String refString = getString(R.string.news);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query ref = database.getReference(refString).orderByKey().limitToLast(1);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showProgress(false);
                if (!dataSnapshot.hasChildren()) {
                    newsIndex = 0;
                    saveNews();
                } else {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        newsIndex = Integer.parseInt(child.getKey()) + 1;
                        saveNews();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showProgress(false);
                showAlert("ERROR!");
            }
        });
    }

    private void saveNews() {
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
        showProgress(true);
        String fileName = String.valueOf(newsIndex) + "_0.jpg";
        StorageReference ref = FirebaseStorage.getInstance().getReference(getString(R.string.news)).child(fileName);
        UploadTask uploadTask;
        uploadTask = ref.putFile(thUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                showProgress(false);
                if (taskSnapshot.getDownloadUrl() != null) {
                    String ref = taskSnapshot.getDownloadUrl().toString();
                    thMap.put("photo", ref);
                }
                prepareToUploadEnglishImage(enUri, thMap, enMap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showProgress(false);
                showAlert("การอัพโหลดภาพล้มเหลว!");
                prepareToUploadEnglishImage(enUri, thMap, enMap);
            }
        });
    }

    private void prepareToUploadEnglishImage(Uri enUri, Map<String, Object> thMap, Map<String, Object> enMap) {
        if (enUri != null) {
            uploadEnglishImage(enUri, thMap, enMap);
        } else {
            updateNews(thMap, enMap);
        }
    }

    private void uploadEnglishImage(Uri enUri, final Map<String, Object> thMap, final Map<String, Object> enMap) {
        showProgress(true);
        String fileName = String.valueOf(newsIndex) + "_1.jpg";
        StorageReference ref = FirebaseStorage.getInstance().getReference(getString(R.string.news)).child(fileName);
        UploadTask uploadTask;
        uploadTask = ref.putFile(enUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                showProgress(false);
                if (taskSnapshot.getDownloadUrl() != null) {
                    String ref = taskSnapshot.getDownloadUrl().toString();
                    enMap.put("photo", ref);
                }
                updateNews(thMap, enMap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showProgress(false);
                showAlert("การอัพโหลดภาพล้มเหลว!");
                updateNews(thMap, enMap);
            }
        });
    }

    private void updateNews(Map<String, Object> thMap, Map<String, Object> enMap) {
        showProgress(true);
        String refString = getString(R.string.news) + "/" + String.valueOf(newsIndex) + "/";
        Map<String, Object> map = new HashMap<>();
        map.put(refString + "0", thMap);
        map.put(refString + "1", enMap);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.updateChildren(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                showProgress(false);
                showAlert("ERROR!");
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
