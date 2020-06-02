package com.gmail.afoserat.lesson1.views;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gmail.afoserat.lesson1.R;
import com.gmail.afoserat.lesson1.model.Contact;
import com.gmail.afoserat.lesson1.viewmodels.ContactListViewModel;

import java.util.ArrayList;

public class ContactListFragment extends ListFragment {
    ContactListViewModel model;
    private ArrayList<Contact> contacts;

    public static ContactListFragment newInstance() {
        return new ContactListFragment();
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        ContactDetailsFragment fragment = ContactDetailsFragment.newInstance((String) v.getTag(R.string.contact_id));
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.toolbar_title_contactList);
        model = new ViewModelProvider(requireActivity()).get(ContactListViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model.getContacts().observe(getViewLifecycleOwner(), new Observer<ArrayList<Contact>>() {
            @Override
            public void onChanged(ArrayList<Contact> contactArrayList) {
                if (getActivity() != null) {
                    contacts = contactArrayList;
                    ArrayAdapter<Contact> contactArrayAdapter = new ArrayAdapter<Contact>(getActivity(), 0, contacts) {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            if (convertView == null) {
                                convertView = getLayoutInflater().inflate(R.layout.fragment_contact_list, null, false);
                            }
                            TextView name = convertView.findViewById(R.id.user_name);

                            Contact currentContact = contacts.get(position);
                            convertView.setTag(R.string.contact_id, currentContact.getId());

                            name.setText(currentContact.getName());
                            if (currentContact.getPhones().length > 0) {
                                TextView phone = convertView.findViewById(R.id.user_phone);
                                phone.setText(currentContact.getPhones()[0]);
                            }
                            if (currentContact.getImageUri() != null) {
                                ImageView avatar = convertView.findViewById(R.id.user_photo);
                                avatar.setImageURI(currentContact.getImageUri());
                            }
                            return convertView;
                        }
                    };
                    setListAdapter(contactArrayAdapter);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.toolbar_title_contactList);
    }
}
