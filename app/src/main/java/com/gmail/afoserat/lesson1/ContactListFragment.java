package com.gmail.afoserat.lesson1;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

public class ContactListFragment extends ListFragment {
    ContactsService mService;

    interface serviceAvailable {
        ContactsService getService();
    }

    interface ResultListener {
        void onComplete(Contact[] contacts);
    }

    public static ContactListFragment newInstance() {
        return new ContactListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof serviceAvailable) {
            mService = ((serviceAvailable) context).getService();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mService = null;
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        ContactDetailsFragment fragment = ContactDetailsFragment.newInstance((int) id);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("List of contacts");
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mService != null) {
            ResultListener showContacts = new ResultListener() {
                @Override
                public void onComplete(final Contact[] contacts) {
                    if (view != null) {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                ArrayAdapter<Contact> contactArrayAdapter = new ArrayAdapter<Contact>(getActivity(), 0, contacts) {
                                    @NonNull
                                    @Override
                                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                        if (convertView == null) {
                                            convertView = getLayoutInflater().inflate(R.layout.fragment_contact_list, null, false);
                                        }
                                        TextView name = convertView.findViewById(R.id.user_name);
                                        TextView phone = convertView.findViewById(R.id.user_phone);

                                        Contact currentContact = contacts[position];
                                        name.setText(currentContact.getName());
                                        phone.setText(currentContact.getPhone());
                                        return convertView;
                                    }
                                };
                                setListAdapter(contactArrayAdapter);
                            }
                        });
                    }
                }
            };
            mService.getContacts(showContacts);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("List of contacts");
    }
}
