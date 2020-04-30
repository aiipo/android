package com.gmail.afoserat.lesson1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ContactDetailsFragment extends Fragment {
    private static final String CONTACT_ID = "CONTACT_ID";

    public static ContactDetailsFragment newInstance(int id) {
        ContactDetailsFragment fragment = new ContactDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(CONTACT_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            TextView name = view.findViewById(R.id.user_name);
            name.setText(arguments.getString(CONTACT_ID, "Will"));
            TextView phone = view.findViewById(R.id.phone_main);
            phone.setText(arguments.getString(CONTACT_ID, "89508528585"));
            TextView email = view.findViewById(R.id.email_main);
            email.setText(arguments.getString(CONTACT_ID, "will@yandex.ru"));
        }
    }

}
