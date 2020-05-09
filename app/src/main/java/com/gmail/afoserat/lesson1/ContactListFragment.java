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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ContactListFragment extends ListFragment {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    ContactsService mService;
    private Contact[] contacts;

    interface serviceAvailable {
        ContactsService getService();
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

    static void waitFuture(Future<?> future) {
        while (true) {
            if (future.isDone() || future.isCancelled()) {
                break;
            }
        }
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
        try {
            Future<Contact[]> future = executorService.submit(new Callable<Contact[]>() {
                @Override
                public Contact[] call() throws Exception {
                    return mService.getContacts();
                }
            });
            waitFuture(future);
            contacts = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("List of contacts");
    }
}
