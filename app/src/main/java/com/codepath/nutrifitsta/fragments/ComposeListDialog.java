package com.codepath.nutrifitsta.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.nutrifitsta.R;
import com.codepath.nutrifitsta.adapters.ItemsAdapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Headers;

public class ComposeListDialog extends DialogFragment {
    public static final String TAG = "ComposeListDialog";
    public static String API_ENDPOINT_FOOD = "https://trackapi.nutritionix.com/v2/natural/nutrients";
    public static String COUNT = "Calorie Count: ";
    private static String type;

    private TextView tvCount, mActionOk, mActionCancel;
    private EditText etItem;
    private Button btnAdd;
    private RecyclerView rvItems;
    ItemsAdapter itemsAdapter;
    List<String> items;
    List<Integer> itemsCal;
    Integer total_cal;

    // Defines the listener interface
    public interface ComposeDialogListener {
        void sendInput(String inputText);
    }

    public ComposeDialogListener dialogListener;

    public ComposeListDialog() {
        // Required empty public constructor
    }

    public static ComposeListDialog newInstance(String input) {
        type = input;
        return new ComposeListDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("Enter Ingredients");
        tvCount = view.findViewById(R.id.tvCount);
        etItem = view.findViewById(R.id.etItem);
        btnAdd = view.findViewById(R.id.btnAdd);
        rvItems = view.findViewById(R.id.rvItems);
        mActionOk = view.findViewById(R.id.action_ok);
        mActionCancel = view.findViewById(R.id.action_cancel);

        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });
        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: capturing input");
                String input = etItem.getText().toString();
                if(!input.equals("")){
                    dialogListener.sendInput(input);
                }
                getDialog().dismiss();
            }
        });
        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                // Delete the item from the model
                items.remove(position);
                total_cal -= itemsCal.get(position);
                tvCount.setText(COUNT + total_cal);
                itemsCal.remove(position);
                // Notify the adapter
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getContext(), "Item was removed", Toast.LENGTH_SHORT).show();
            }
        };
        items = new ArrayList<>();
        itemsCal = new ArrayList<>();
        total_cal = 0;
        itemsAdapter = new ItemsAdapter(items, onLongClickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvItems.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = etItem.getText().toString();
                findCalories(item);
                items.add(item);
                itemsAdapter.notifyItemInserted(items.size() - 1);
                rvItems.smoothScrollToPosition(items.size() - 1);
                etItem.setText("");
            }
        });

        // Show soft keyboard automatically and request focus to field
        etItem.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            dialogListener = (ComposeDialogListener) getTargetFragment();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException : " + e.getMessage() );
        }
    }

    private void findCalories(String item) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHeaders headers = new RequestHeaders();
        headers.put("Content-Type", "application/json");
        headers.put("x-app-id", getString(R.string.app_id));
        headers.put("x-app-key", getString(R.string.app_key));
        headers.put("x-remote-user-id", "0");
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("query", item);
        Gson gson = new Gson();
        client.post(API_ENDPOINT_FOOD, headers, new RequestParams(), gson.toJson(body), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "success");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("foods");
                    Integer cal = results.getJSONObject(0).getInt("nf_calories");
                    Toast.makeText(getContext(), String.format("%s calories were added", cal), Toast.LENGTH_SHORT).show();
                    itemsCal.add(cal);
                    total_cal += cal;
                    tvCount.setText(COUNT + total_cal);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure" + response, throwable);
            }
        });
    }
}