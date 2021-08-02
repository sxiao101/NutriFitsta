package com.codepath.nutrifitsta.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
    private Map<Integer, Integer> daysCount;
    private LineChart lineChart;

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

        Date currentTime = Calendar.getInstance().getTime();
        Post lastPost = posts.get(posts.size()-1);
        int maxDays = Methods.calculateDaysAgo(lastPost.getCreatedAt());
        daysCount = new HashMap<Integer, Integer>();

        for (int d = 0; d <= (maxDays + 1); d++) { // initialize dictionary for the chart dataset
            daysCount.put(d, 0);
        }

        for (Post p : posts) {
            int tempDay = Methods.calculateDaysAgo(p.getCreatedAt());
            int currentCount = daysCount.get(tempDay);
            daysCount.put(tempDay, currentCount + 1);
        }

        lineChart = view.findViewById(R.id.lineChart);

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.animateXY(3000, 3000);

        ArrayList<Entry> val = new ArrayList<>();

        for (Integer key : daysCount.keySet()) {

            Entry e = new Entry(key, daysCount.get(key));
            val.add(e);
        }

        float avg = ((float)posts.size()) / (daysCount.keySet().size());
        LimitLine avg_limit = new LimitLine(avg, "Average Activity");
        avg_limit.setLineWidth(4f);
        avg_limit.enableDashedLine(10f, 10f, 10f);
        avg_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        avg_limit.setTextSize(15f);
        lineChart.getAxisLeft().addLimitLine(avg_limit);


        LineDataSet s1 = new LineDataSet(val, "Post Activity based on Days Ago Created");
        s1.setFillAlpha(110);
        s1.setLineWidth(3f);
        s1.setValueTextSize(10f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(s1);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);
    }
}