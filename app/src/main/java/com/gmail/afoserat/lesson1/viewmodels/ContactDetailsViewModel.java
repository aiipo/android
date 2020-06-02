package com.gmail.afoserat.lesson1.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gmail.afoserat.lesson1.ContactRepository;
import com.gmail.afoserat.lesson1.model.Contact;

public class ContactDetailsViewModel extends AndroidViewModel {
    private ContactRepository repository;
    private MutableLiveData<Contact> contact;

    public ContactDetailsViewModel(@NonNull Application application) {
        super(application);
        repository = new ContactRepository(application.getContentResolver());
    }

    public LiveData<Contact> getContact(String id) {
        if (contact == null) {
            contact = new MutableLiveData<>();
            loadContact(id);
        }
        return contact;
    }

    private void loadContact(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                contact.postValue(repository.getContactById(id));
            }
        }).start();
    }
}
