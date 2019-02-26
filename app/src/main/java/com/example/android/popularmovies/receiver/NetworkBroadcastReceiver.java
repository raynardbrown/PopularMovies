package com.example.android.popularmovies.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

/**
 * NetworkBroadcastReceiver is a BroadcastReceiver that listens for network connectivity events
 * from the system and notifies this application accordingly.
 */
public class NetworkBroadcastReceiver extends BroadcastReceiver
{
  private INetworkListener networkListener;

  public NetworkBroadcastReceiver(INetworkListener networkListener)
  {
    this.networkListener = networkListener;
  }

  @Override
  public void onReceive(Context context, Intent intent)
  {
    String action = intent.getAction();

    if(action != null && action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
    {
      if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false))
      {
        // no connectivity
        networkListener.onNetworkDisconnected();
      }
      else
      {
        // we have a connection
        networkListener.onNetworkConnected();
      }
    }
  }
}
