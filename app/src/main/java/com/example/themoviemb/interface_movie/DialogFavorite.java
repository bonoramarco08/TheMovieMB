package com.example.themoviemb.interface_movie;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.themoviemb.R;

public class DialogFavorite extends DialogFragment {
    public interface IFavoritDialog {
        void onResponse(boolean aResponse, long aId, Boolean isRemoved);
    }

    IFavoritDialog mListener;

    String mTitle, mMessage;
    Boolean isRemoved;
    long mId;

    public DialogFavorite(String aTitle, String aMessage, long aId, Boolean isRemoved) {
        mTitle = aTitle;
        mMessage = aMessage;
        mId = aId;
        this.isRemoved = isRemoved;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder vBuilder = new AlertDialog.Builder(getActivity() , R.style.MyDialog);
        vBuilder.setTitle(mTitle);
        vBuilder.setMessage(mMessage);
        vBuilder.setPositiveButton(getString(R.string.dialogbuttonpositive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onResponse(true, mId, isRemoved);
            }
        });
        vBuilder.setNegativeButton(getString(R.string.dialogbuttonnegative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onResponse(false, mId, isRemoved);
            }
        });
        vBuilder.setNeutralButton(getString(R.string.dialogbuttonannulla), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onResponse(false, mId, isRemoved);
            }
        });
        return vBuilder.create();
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IFavoritDialog) {
            mListener = (IFavoritDialog) activity;
        } else {
            mListener = null;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IFavoritDialog) {
            mListener = (IFavoritDialog) context;
        } else {
            mListener = null;
        }
    }
}