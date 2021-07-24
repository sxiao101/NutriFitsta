package com.codepath.nutrifitsta.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.nutrifitsta.R;

public class ComposeListDialog extends DialogFragment {
    public static final String TAG = "ComposeListDialog";

    private EditText mInput;
    private TextView mActionOk, mActionCancel;

    // Defines the listener interface
    public interface ComposeDialogListener {
        void sendInput(String inputText);
    }

    public ComposeDialogListener dialogListener;

    public ComposeListDialog() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose_dialog, container, false);
    }

    public static ComposeListDialog newInstance(String title) {
        ComposeListDialog frag = new ComposeListDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mInput = (EditText) view.findViewById(R.id.input);
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

                String input = mInput.getText().toString();
                if(!input.equals("")){
//
//                    //Easiest way: just set the value.
//                    MainFragment fragment = (MainFragment) getActivity().getFragmentManager().findFragmentByTag("MainFragment");
//                    fragment.mInputDisplay.setText(input);

                    dialogListener.sendInput(input);
                }

                getDialog().dismiss();
            }
        });

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        mInput.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    /*// Call this method to send the data back to the parent fragment
    public void sendBackResult() {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        ComposeDialogListener listener = (ComposeDialogListener) getTargetFragment();
        listener.onFinishEditDialog(mEditText.getText().toString());
        dismiss();
    }*/
}