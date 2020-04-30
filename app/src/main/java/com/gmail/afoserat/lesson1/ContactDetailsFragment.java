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
        getActivity().setTitle("Details of the contact");
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
            Contact currentContact = ContactListFragment.contacts[getArguments().getInt(CONTACT_ID, 0)];
            TextView name = view.findViewById(R.id.user_name);
            name.setText(currentContact.getName());
            TextView phone = view.findViewById(R.id.phone_main);
            phone.setText(currentContact.getPhone());
            TextView email = view.findViewById(R.id.email_main);
            email.setText(currentContact.getEmail());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().setTitle("List of contacts");
    }
}
