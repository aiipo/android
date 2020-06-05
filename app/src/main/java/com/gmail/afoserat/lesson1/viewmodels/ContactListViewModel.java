package com.gmail.afoserat.lesson1.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gmail.afoserat.lesson1.ContactRepository;
import com.gmail.afoserat.lesson1.model.Contact;

import java.util.ArrayList;

public class ContactListViewModel extends AndroidViewModel {
    private ContactRepository repository;
    private MutableLiveData<ArrayList<Contact>> contactList;

    public ContactListViewModel(@NonNull Application application) {
        super(application);
        repository = new ContactRepository(application.getContentResolver());
    }

    public LiveData<ArrayList<Contact>> getContacts() {
        if (contactList == null) {
            contactList = new MutableLiveData<>();
            loadContacts(null);
        }
        return contactList;
    }

    public void updateContacts(@Nullable String selector) {
        if (contactList != null) {
            loadContacts(selector);
        }
    }

    private void loadContacts(@Nullable final String selector) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                contactList.postValue(repository.getContacts(selector));
            }
        }).start();
    }
}
