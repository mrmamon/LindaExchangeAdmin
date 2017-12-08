package com.lindaexchange.lindaexchangeadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lindaexchange.lindaexchangeadmin.dummy.DummyContent;

import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity implements  BranchFragment.OnListFragmentInteractionListener,
        BranchDetailFragment.OnFragmentInteractionListener, RateFragment.OnListFragmentInteractionListener,
        NewsFragment.OnListFragmentInteractionListener, NewsDetailFragment.OnFragmentInteractionListener,
        RateDetailFragment.OnFragmentInteractionListener, DenominationDetailFragment.OnFragmentInteractionListener,
        NewsPagerFragment.OnFragmentInteractionListener, BranchPagerFragment.OnFragmentInteractionListener,
        RateForEachBranchFragment.OnListFragmentInteractionListener, RateValueUpdateFragment.OnFragmentInteractionListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DummyContent.DummyItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        if (holder.mItem.id.equals("1")) {
                            RateFragment fragment = new RateFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.item_detail_container, fragment)
                                    .commit();
                        } else if (holder.mItem.id.equals("2")) {
                            RateForEachBranchFragment fragment = new RateForEachBranchFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.item_detail_container, fragment)
                                    .commit();
                        } else if (holder.mItem.id.equals("3")) {
                            NewsFragment fragment = new NewsFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.item_detail_container, fragment)
                                    .commit();
                        } else if (holder.mItem.id.equals("4")) {
                            BranchFragment fragment = new BranchFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.item_detail_container, fragment)
                                    .commit();
                        } else {
                            Bundle arguments = new Bundle();
                            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                            ItemDetailFragment fragment = new ItemDetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.item_detail_container, fragment)
                                    .commit();
                        }
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
//            public final TextView mIdView;
            public final TextView mContentView;
            public DummyContent.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
//                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    @Override
    public void showBranchDetail(int index) {
        Bundle arguments = new Bundle();
        arguments.putInt("branchIndex", index);
        BranchPagerFragment fragment = new BranchPagerFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showBranchAdd() {
        Bundle arguments = new Bundle();
        arguments.putInt("branchIndex", -1);
        BranchPagerFragment fragment = new BranchPagerFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void deleteBranch(final int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle(R.string.delete_confirm_title)
                .setMessage(R.string.delete_confirm)
                .setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String branchString = getString(R.string.branch);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference(branchString).child("/" + String.valueOf(index));
                        ref.removeValue(new DatabaseReference.CompletionListener() {
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
                })
                .setNegativeButton("ไม่ใช่", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void openMap() {
//        MapFragment fragment = new MapFragment();
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.item_detail_container, fragment)
//                .addToBackStack(null)
//                .commit();
    }

    @Override
    public void showRateDetail(int index) {
        Bundle arguments = new Bundle();
        arguments.putInt("rateIndex", index);
        RateDetailFragment fragment = new RateDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showRateAdd() {
        Bundle arguments = new Bundle();
        arguments.putInt("rateIndex", -1);
        RateDetailFragment fragment = new RateDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void deleteRate(final int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle(R.string.delete_confirm_title)
                .setMessage(R.string.delete_confirm)
                .setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String rateString = getString(R.string.rate);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference(rateString).child("/" + String.valueOf(index));
                        ref.removeValue(new DatabaseReference.CompletionListener() {
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
                })
                .setNegativeButton("ไม่ใช่", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void showRateValueUpdate(int branchIndex) {
        Bundle arguments = new Bundle();
        arguments.putInt("branchIndex", branchIndex);
        RateValueUpdateFragment fragment = new RateValueUpdateFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showNewsDetail(int index) {
        Bundle arguments = new Bundle();
        arguments.putInt("newsIndex", index);
        NewsPagerFragment fragment = new NewsPagerFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showNewsAdd() {
        Bundle arguments = new Bundle();
        arguments.putInt("newsIndex", -1);
        NewsPagerFragment fragment = new NewsPagerFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void deleteNews(final int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle(R.string.delete_confirm_title)
                .setMessage(R.string.delete_confirm)
                .setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newsString = getString(R.string.news);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference(newsString).child("/" + String.valueOf(index));
                        ref.removeValue(new DatabaseReference.CompletionListener() {
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
                })
                .setNegativeButton("ไม่ใช่", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    //    @Override
//    public void showDenominationDetail(String child) {
//        Bundle arguments = new Bundle();
//        arguments.putString("child", child);
//        DenominationDetailFragment fragment = new DenominationDetailFragment();
//        fragment.setArguments(arguments);
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.item_detail_container, fragment)
//                .addToBackStack(null)
//                .commit();
//    }

    @Override
    public void showDenominationDetail(int rateIndex, int denominationIndex) {
        Bundle arguments = new Bundle();
        arguments.putInt("rateIndex", rateIndex);
        arguments.putInt("denominationIndex", denominationIndex);
        DenominationDetailFragment fragment = new DenominationDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void deleteDenomination(final int rateIndex, final int denominationIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle(R.string.delete_confirm_title)
            .setMessage(R.string.delete_confirm)
            .setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String denominationString = getString(R.string.rate);
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    String child = String.valueOf(rateIndex) + "/rate/" + String.valueOf(denominationIndex);
                    DatabaseReference ref = database.getReference(denominationString).child(child);
                    ref.removeValue(new DatabaseReference.CompletionListener() {
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
            })
            .setNegativeButton("ไม่ใช่", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }

    private void showAlert(String title) {
//        showProgress(false);
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
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
}
