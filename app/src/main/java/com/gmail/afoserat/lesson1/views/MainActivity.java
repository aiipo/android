package com.gmail.afoserat.lesson1.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.gmail.afoserat.lesson1.R;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private boolean existSavedInstanceState;

    private void addContactListFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main, ContactListFragment.newInstance())
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        existSavedInstanceState = savedInstanceState != null;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else if (savedInstanceState == null) {
            addContactListFragment();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!existSavedInstanceState) {
                        addContactListFragment();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.allow_read_contacts, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void showContactDetails(String id) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main, ContactDetailsFragment.newInstance(id))
                .commit();
    }
}
