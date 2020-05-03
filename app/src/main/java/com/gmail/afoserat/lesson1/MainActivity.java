package com.gmail.afoserat.lesson1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements ContactListFragment.onContactSelectedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main, ContactListFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public void onContactSelected(int id) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main, ContactDetailsFragment.newInstance(id))
                .addToBackStack(null)
                .commit();
    }
}
