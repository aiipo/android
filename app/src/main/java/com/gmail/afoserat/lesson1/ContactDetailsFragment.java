package com.gmail.afoserat.lesson1;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

public class ContactDetailsFragment extends Fragment {
    private static final String CONTACT_ID = "CONTACT_ID";
    ContactsService mService;

    public static ContactDetailsFragment newInstance(int id) {
        ContactDetailsFragment fragment = new ContactDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(CONTACT_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ContactListFragment.serviceAvailable) {
            mService = ((ContactListFragment.serviceAvailable) context).getService();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mService = null;
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
            TextView name = view.findViewById(R.id.user_name);
            TextView phone = view.findViewById(R.id.phone_main);
            TextView email = view.findViewById(R.id.email_main);
            int contactId = getArguments().getInt(CONTACT_ID, 0);
            Contact currentContact = null;
            try {
                currentContact = mService.getContactById(contactId);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            name.setText(currentContact.getName());
            phone.setText(currentContact.getPhone());
            email.setText(currentContact.getEmail());
        }
    }
}
