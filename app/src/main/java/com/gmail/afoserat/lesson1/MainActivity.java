package com.gmail.afoserat.lesson1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public class MainActivity extends AppCompatActivity implements ContactListFragment.serviceAvailable {
    private ContactsService boundService;
    private boolean isFirstCreated;
    private boolean isBound = false;
    private static final String CONTACT_ID = "CONTACT_ID";

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
            Intent receivedIntent = getIntent();
            if (boundService != null && receivedIntent != null && receivedIntent.hasExtra(CONTACT_ID)) {
                int id = receivedIntent.getExtras().getInt(CONTACT_ID);
                showContactDetails(id);
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
        bindService(intent, boundServiceConnection, Context.BIND_AUTO_CREATE);
        isFirstCreated = savedInstanceState == null;
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

    private void showContactDetails(int id) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main, ContactDetailsFragment.newInstance(id))
                .commit();
    }
}
