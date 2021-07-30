package com.codepath.nutrifitsta.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.nutrifitsta.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserActivityFragment extends Fragment {
    public static final String TAG = "UserActivityFragment";

    private LineChart lineChart;

    public UserActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lineChart = view.findViewById(R.id.lineChart);

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);

        ArrayList<Entry> val = new ArrayList<>();
        val.add(new Entry(0, 60f));
        val.add(new Entry(1, 50f));
        val.add(new Entry(2, 70f));
        val.add(new Entry(3, 30f));
        val.add(new Entry(4, 50f));
        val.add(new Entry(5, 60f));
        val.add(new Entry(6, 65f));

        LineDataSet s1 = new LineDataSet(val, "Data Set 1");
        s1.setFillAlpha(110);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(s1);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);
    }
}