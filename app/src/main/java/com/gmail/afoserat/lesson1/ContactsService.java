package com.gmail.afoserat.lesson1;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class ContactsService extends Service {
    private final IBinder myBinder = new myLocalBinder();

    class myLocalBinder extends Binder {
        public ContactsService getService() {
            return ContactsService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    public Contact[] getContacts() {
        return Contact.contacts;
    }

    public Contact getContactById(int id) {
        try {
            return Contact.contacts[id];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
}
