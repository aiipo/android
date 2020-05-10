package com.gmail.afoserat.lesson1;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ContactDetailsFragment extends Fragment {
    private static final String CONTACT_ID = "CONTACT_ID";
    ContactsService mService;

    interface ResultDetailsListener {
        void onComplete(Contact contact);
    }

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
    public void onDetach() {
        super.onDetach();
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
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            ResultDetailsListener showContactDetails = new ResultDetailsListener() {
                @Override
                public void onComplete(final Contact contact) {
                    if (view != null) {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                if (view != null) {
                                    TextView name = view.findViewById(R.id.user_name);
                                    TextView phone = view.findViewById(R.id.phone_main);
                                    TextView email = view.findViewById(R.id.email_main);

                                    name.setText(contact.getName());
                                    phone.setText(contact.getPhone());
                                    email.setText(contact.getEmail());
                                }
                            }
                        });
                    }
                }
            };
            final int contactId = getArguments().getInt(CONTACT_ID, 0);
            mService.getContactById(contactId, showContactDetails);
        }
    }
}
