package com.codepath.nutrifitsta.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.nutrifitsta.R;
import com.codepath.nutrifitsta.adapters.ItemsAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ViewDetailsDialog extends DialogFragment {
    public static final String TAG = "DetailsDialog";
    private static List<String> items;
    private static int cal;
    private TextView tvCal, mActionExit;
    private RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    public ViewDetailsDialog() {
        // Required empty public constructor
    }

    public static ViewDetailsDialog newInstance(List<String> list, Integer totalCal) {
        items = new ArrayList<>();
        items.addAll(list);
        cal = totalCal;
        return new ViewDetailsDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_details_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("Ingredients");
        mActionExit = view.findViewById(R.id.action_exit);
        mActionExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        rvItems = view.findViewById(R.id.rvItems);
        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {@Override public void onItemLongClicked(int position){}};
        itemsAdapter = new ItemsAdapter(items, onLongClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvItems.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));


        tvCal = view.findViewById(R.id.tvCal);
        tvCal.setText("Total Calories: " + cal);
    }
}