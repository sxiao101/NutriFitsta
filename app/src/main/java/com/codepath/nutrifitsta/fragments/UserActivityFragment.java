package com.codepath.nutrifitsta.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.codepath.nutrifitsta.MainActivity;
import com.codepath.nutrifitsta.R;
import com.codepath.nutrifitsta.classes.Methods;
import com.codepath.nutrifitsta.classes.Post;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserActivityFragment extends Fragment {
    public static final String TAG = "UserActivityFragment";
    private static List<Post> posts;
    private Map<Integer, Integer> count;
    private static LineChart lineChart;
    private LimitLine avg_limit;
    private ArrayList<Entry> val;
    private Spinner spinner;

    public UserActivityFragment() {
        // Required empty public constructor
    }
    public static UserActivityFragment newInstance(List<Post> allPosts) {
        posts = allPosts;
        return new UserActivityFragment();
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

        spinner = view.findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.timeUnits)));
        spinner.setSelection(0);

        lineChart = view.findViewById(R.id.lineChart);
        count = new HashMap<Integer, Integer>();
        val = new ArrayList<>();
        avg_limit = null;

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        createGraph("day");

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long itemID) {
                if (position == 0) {
                    createGraph("day");
                } else if (position == 1) {
                    createGraph("week");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    private void createGraph(String time) {
        String label = (time.equals("day")) ? "Days" : "Weeks";
        Post lastPost = posts.get(posts.size()-1);
        int maxTime = Methods.getRelativeTime(lastPost.getCreatedAt(), time);

        int max = (maxTime >= 4) ? maxTime : 4;

        count.clear();
        for (int d = 0; d <= (max + 1); d++) { // initialize dictionary for the chart dataset
            count.put(d, 0);
        }

        for (Post p : posts) {
            int temp = Methods.getRelativeTime(p.getCreatedAt(), time);
            int currentCount = count.get(temp);
            count.put(temp, currentCount + 1);
        }

        val.clear();
        for (Integer key : count.keySet()) {
            Entry e = new Entry(key, count.get(key));
            val.add(e);
        }

        float avg = ((float)posts.size()) / (maxTime);
        if(avg_limit != null) {
            lineChart.getAxisLeft().removeLimitLine(avg_limit);
        }
        avg_limit = new LimitLine(avg, "Average Activity");
        avg_limit.setLineWidth(4f);
        avg_limit.enableDashedLine(10f, 10f, 10f);
        avg_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        avg_limit.setTextSize(15f);
        lineChart.getAxisLeft().addLimitLine(avg_limit);

        LineDataSet s1 = new LineDataSet(val, String.format("Post Activity based on %s Ago Created", label));
        s1.setFillAlpha(110);
        s1.setLineWidth(3f);
        s1.setValueTextSize(10f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(s1);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);

        lineChart.notifyDataSetChanged();
        lineChart.animateXY(1500, 1500);
    }



}