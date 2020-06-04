package com.gmail.afoserat.lesson1.views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.afoserat.lesson1.R;
import com.gmail.afoserat.lesson1.adapters.ContactRecyclerViewAdapter;
import com.gmail.afoserat.lesson1.decorators.ContactDecorator;
import com.gmail.afoserat.lesson1.model.Contact;
import com.gmail.afoserat.lesson1.viewmodels.ContactListViewModel;

import java.util.ArrayList;

public class ContactListFragment extends Fragment implements ContactRecyclerViewAdapter.onContactListener {
    ContactListViewModel model;

    public static ContactListFragment newInstance() {
        return new ContactListFragment();
    }

    @Override
    public void onContactClick(@NonNull View v) {
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            final ContactRecyclerViewAdapter contactAdapter = new ContactRecyclerViewAdapter(this);
            model.getContacts().observe(getViewLifecycleOwner(), new Observer<ArrayList<Contact>>() {
                @Override
                public void onChanged(ArrayList<Contact> contacts) {
                    contactAdapter.setContactList(contacts);
                }
            });
            recyclerView.addItemDecoration(new ContactDecorator(ContextCompat.getDrawable(getContext(), R.drawable.divider)));
            recyclerView.setAdapter(contactAdapter);

            setHasOptionsMenu(true);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
        MenuItem mSearch = menu.findItem(R.id.app_bar_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint(getString(R.string.searchBar));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                model.updateContacts(newText);
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.toolbar_title_contactList);
    }
}
