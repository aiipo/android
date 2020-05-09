package com.gmail.afoserat.lesson1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public class MainActivity extends AppCompatActivity implements ContactListFragment.serviceAvailable {
    private ContactsService boundService;
    private boolean isFirstCreated = false;
    private boolean isBound = false;

    private void addContactListFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main, ContactListFragment.newInstance())
                .commit();
    }

    private ServiceConnection boundServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ContactsService.myLocalBinder binderBridge = (ContactsService.myLocalBinder) service;
            boundService = binderBridge.getService();
            isBound = true;
            if (isFirstCreated) {
                addContactListFragment();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            boundService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, ContactsService.class);
        startService(intent);
        bindService(intent, boundServiceConnection, Context.BIND_AUTO_CREATE);
        if (savedInstanceState == null) {
            isFirstCreated = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isBound) {
            unbindService(boundServiceConnection);
            isBound = false;
        }
    }

    @Override
    public ContactsService getService() {
        return isBound ? boundService : null;
    }
}
