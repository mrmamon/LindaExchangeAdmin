package com.lindaexchange.lindaexchangeadmin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

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
    private static final int REQUEST_GALLERY_PICTURE = 10;
    private static final int REQUEST_IMAGE_CAPTURE = 11;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 12;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ExchangeRateDB rate;
    private RecyclerView recyclerView;
    private int rateIndex;

    private EditText countryNameEditText;
    private EditText currencyNameEditText;
    private EditText flagEditText;
    private ImageView flagImageView;

    private ProgressBar mProgressView;
    private View mContentView;
    private FloatingActionButton fab;

    private Bitmap imageBitmap;
    private boolean imageFromUri = false;
    private boolean imageFromCamera = false;
    private Uri imageUri;

    private CountDownTimer timer = new CountDownTimer(10000, 10000) {
        @Override
        public void onTick(long millisUntilFinished) { }

        @Override
        public void onFinish() { showTimeoutSnackbar(); }
    };

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
        flagImageView = (ImageView) view.findViewById(R.id.ratePhotoImageView);
        Button flagImageButton = (Button) view.findViewById(R.id.ratePhotoButton);
        flagImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGallery();
            }
        });

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        mContentView = view.findViewById(R.id.content);
        mProgressView = (ProgressBar) view.findViewById(R.id.progressBar);

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
        showProgress(true);
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
                    String flagRef = rateDB.getFlag();
                    flagEditText.setText(flagRef);
                    if (URLUtil.isValidUrl(flagRef)) {
                        Picasso.with(getContext()).load(flagRef).into(flagImageView);
                    } else if (!flagRef.equals("")) {
                        FirebaseStorage.getInstance().getReference(flagRef).getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Picasso.with(getContext()).load(uri).into(flagImageView);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }

                    rate = rateDB;
                    if (rate.getRate() != null) {
                        recyclerView.setAdapter(new DenominationRecyclerViewAdapter(rate.getRate(), rateIndex, mListener));
                    } else {
                        recyclerView.setAdapter(new DenominationRecyclerViewAdapter(new ArrayList<DenominationDB>(), rateIndex, mListener));
                    }
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

    private void addRate() {
        showProgress(true);
        String rateString = getString(R.string.rate);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query ref = database.getReference(rateString).orderByKey().limitToLast(1);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                    rateArray = new ArrayList<ExchangeRateDB>();
                if (!dataSnapshot.hasChildren()) {
                    rateIndex = 0;
                    saveRate(true);
                } else {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        rateIndex = Integer.parseInt(child.getKey()) + 1;
                        saveRate(true);
                        break;
                    }
                }
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
        final Map<String, Object> map = new HashMap<>();
        final String rateString = getString(R.string.rate);
        map.put(rateString + "/" + String.valueOf(rateIndex) + "/countryname", countryName);
        map.put(rateString + "/" + String.valueOf(rateIndex) + "/currencyname", currencyName);
        if (imageFromUri) {
            showProgress(true);
            String flagString = getString(R.string.rate_flag);
            String filename = "flag" + String.valueOf(rateIndex) + ".jpg";
            StorageReference ref = FirebaseStorage.getInstance().getReference(flagString).child(filename);
            UploadTask uploadTask;
            uploadTask = ref.putFile(imageUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    showProgress(false);
                    if (taskSnapshot.getDownloadUrl() != null) {
                        String ref = taskSnapshot.getDownloadUrl().toString();
                        map.put(rateString + "/" + String.valueOf(rateIndex) + "/flag", ref);
                    }
                    updateMap(map, isNewRate);
                }
            });
        } else {
            map.put(rateString + "/" + String.valueOf(rateIndex) + "/flag", flagUrl);
            updateMap(map, isNewRate);
        }
    }

    private void updateMap(Map<String, Object> map, final Boolean isNewRate) {
        showProgress(true);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.updateChildren(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    showProgress(false);
                    showAlert(getString(R.string.finish));
                    if (isNewRate) {
                        setRate();
                    }
                } else {
                    showProgress(false);
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
        void deleteDenomination(int rateIndex, int denominationIndex, String denominationName);
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
        Snackbar snackbar = Snackbar.make(recyclerView, R.string.timeout_string, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void startCameraOrGalleryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Upload picture options");
        builder.setMessage("How do you want to set your picture?");
        builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showGallery();
            }
        });
        builder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dispatchTakePictureIntent();
            }
        });
        builder.show();
    }

    private void showGallery() {
        Intent pictureActionIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pictureActionIntent, REQUEST_GALLERY_PICTURE);
    }

    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extra = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extra.get("data");
            flagImageView.setImageBitmap(imageBitmap);
            this.imageBitmap = imageBitmap;
            imageFromCamera = true;
            imageFromUri = false;
        } else if (requestCode == REQUEST_GALLERY_PICTURE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri selectedImage = data.getData();
                flagImageView.setImageURI(selectedImage);
                imageFromUri = true;
                imageFromCamera = false;
                imageUri = selectedImage;
            }
        }
    }
}
