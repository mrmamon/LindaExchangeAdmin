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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BranchDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BranchDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BranchDetailFragment extends Fragment {
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

    private OnFragmentInteractionListener mListener;
    private int branchIndex;
    private int language;
    private List<BranchDB> branch;

    private EditText nameEditText;
    private EditText addressEditText;
    private EditText contactNumberEditText;
    private EditText openingTimeEditText;
    private EditText locationEditText;
    private EditText imageEditText;

    private Button imageSelectButton;
    private ImageView imageView;

    private ProgressBar mProgressView;
    private View mContentView;

    private CountDownTimer timer = new CountDownTimer(10000, 10000) {
        @Override
        public void onTick(long millisUntilFinished) { }

        @Override
        public void onFinish() { showTimeoutSnackbar(); }
    };

    private Bitmap imageBitmap;
    private boolean imageFromUri = false;
    private boolean imageFromCamera = false;
    private Uri imageUri;

    public BranchDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BranchDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BranchDetailFragment newInstance(String param1, String param2) {
        BranchDetailFragment fragment = new BranchDetailFragment();
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
            language = getArguments().getInt("language", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_branch_detail, container, false);
        nameEditText = (EditText) view.findViewById(R.id.branchNameEditText);
        addressEditText = (EditText) view.findViewById(R.id.branchAddressEditText);
        contactNumberEditText = (EditText) view.findViewById(R.id.branchContactNumberEditText);
        openingTimeEditText = (EditText) view.findViewById(R.id.branchOpeningTimeEditText);
        locationEditText = (EditText) view.findViewById(R.id.branchLocationEditText);
        imageEditText = (EditText) view.findViewById(R.id.branchImageEditText);
        imageView = (ImageView) view.findViewById(R.id.branchImageView);
        imageSelectButton = (Button) view.findViewById(R.id.branchPhotoAddButton);
        imageSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGallery();
            }
        });

        mProgressView = (ProgressBar) view.findViewById(R.id.progressBar);
        mContentView = view.findViewById(R.id.content);

        Button openMapButton = (Button) view.findViewById(R.id.map_button);
        openMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mListener.openMap();

                Intent mappicker = new Intent(getActivity(), MapsActivity.class);
                startActivityForResult(mappicker, 0);
            }
        });

        if (branchIndex >= 0) {
            // Edit existing branch
            setBranch();
        }
        return view;
    }

    private void gotLatLng(Intent data) {
        String lat = data.getStringExtra("Lat");
        String lng = data.getStringExtra("Lng");
        String latlng = lat + "," + lng;
        locationEditText.setText(latlng);
    }

    private void setBranch() {
        showProgress(true);
        String branchRefString = getString(R.string.branch);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference branchRef = database.getReference(branchRefString).child(String.valueOf(branchIndex));
        branchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<BranchDB>> t = new GenericTypeIndicator<List<BranchDB>>() {};
                branch = dataSnapshot.getValue(t);
                if (branch != null && branch.size() > 0) {
                    BranchDB branchDB = branch.get(language);
                    nameEditText.setText(branchDB.getName());
                    addressEditText.setText(branchDB.getAddress());
                    contactNumberEditText.setText(branchDB.getContactnumber());
                    openingTimeEditText.setText(branchDB.getOpeningtime());
                    locationEditText.setText(branchDB.getLocation());
                    final String imageRef = branchDB.getPhoto();
                    imageEditText.setText(imageRef);
                    if (URLUtil.isValidUrl(imageRef)) {
                        Picasso.with(getContext()).load(imageRef).into(imageView);
                    } else if (!imageRef.equals("")) {
                        FirebaseStorage.getInstance().getReference(imageRef).getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Picasso.with(getContext()).load(imageRef).into(imageView);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
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

    public Map<String, Object> getMap() {
        Map<String, Object> branchMap = new HashMap<>();
        branchMap.put(getString(R.string.branch_address), addressEditText.getText().toString());
        branchMap.put(getString(R.string.branch_contact_number), contactNumberEditText.getText().toString());
        branchMap.put(getString(R.string.branch_location), locationEditText.getText().toString());
        branchMap.put(getString(R.string.branch_name), nameEditText.getText().toString());
        branchMap.put(getString(R.string.branch_opening_time), openingTimeEditText.getText().toString());

        if (imageFromUri && imageUri != null) {
            branchMap.put("uri", imageUri);
        } else {
            branchMap.put(getString(R.string.branch_photo), imageEditText.getText().toString());
        }
        return branchMap;
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
        void openMap();
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
            imageView.setImageBitmap(imageBitmap);
            this.imageBitmap = imageBitmap;
            imageFromCamera = true;
            imageFromUri = false;
        } else if (requestCode == REQUEST_GALLERY_PICTURE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri selectedImage = data.getData();
                imageView.setImageURI(selectedImage);
                imageFromUri = true;
                imageFromCamera = false;
                imageUri = selectedImage;
            }
        } else if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            gotLatLng(data);
        }
    }

    private void showAlert(String title) {
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
