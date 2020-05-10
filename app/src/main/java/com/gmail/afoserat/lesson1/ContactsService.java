package com.gmail.afoserat.lesson1;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContactsService extends Service {
    private final IBinder myBinder = new myLocalBinder();
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    class myLocalBinder extends Binder {
        public ContactsService getService() {
            return ContactsService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public void getContacts(ContactListFragment.ResultListener callback) {
        final WeakReference<ContactListFragment.ResultListener> ref = new WeakReference<>(callback);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                ContactListFragment.ResultListener listener = ref.get();
                if (listener != null) {
                    listener.onComplete(Contact.contacts);
                }
            }
        });
    }

    public void getContactById(final int id, ContactDetailsFragment.ResultDetailsListener callback) {
        final WeakReference<ContactDetailsFragment.ResultDetailsListener> ref = new WeakReference<>(callback);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Contact contact;
                try {
                    contact = Contact.contacts[id];
                } catch (ArrayIndexOutOfBoundsException e) {
                    contact = null;
                }
                ContactDetailsFragment.ResultDetailsListener listener = ref.get();
                if (listener != null) {
                    listener.onComplete(contact);
                }
            }
        });
    }
}
