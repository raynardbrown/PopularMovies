package com.example.android.popularmovies.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.databinding.DialogAppInfoBinding;

public class AppInfoDialogFragment extends DialogFragment
{
  private static final String DIALOG_FRAGMENT_TAG = "app_info_tag";

  public static AppInfoDialogFragment newInstance()
  {
    AppInfoDialogFragment fragment = new AppInfoDialogFragment();
    Bundle bundle = new Bundle();
    fragment.setArguments(bundle);
    return fragment;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    LayoutInflater inflater = getActivity().getLayoutInflater();

    DialogAppInfoBinding binding = DataBindingUtil.inflate(inflater,
                                                           R.layout.dialog_app_info,
                                                           null,
                                                           false);

    builder.setView(binding.getRoot());

    return builder.create();
  }

  public void show(FragmentManager fragmentManager)
  {
    super.show(fragmentManager, AppInfoDialogFragment.DIALOG_FRAGMENT_TAG);
  }
}
