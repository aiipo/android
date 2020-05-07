package com.gmail.afoserat.lesson1;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ContactsService extends Service {
    private final IBinder myBinder = new myLocalBinder();
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 2, 0,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

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

    public Contact[] getContacts() throws ExecutionException, InterruptedException {
        Future<Contact[]> future = threadPoolExecutor.submit(new Callable<Contact[]>() {
            @Override
            public Contact[] call() throws Exception {
                return Contact.contacts;
            }
        });
        return future.get();
    }

    public Contact getContactById(final int id) throws ExecutionException, InterruptedException {
        Future<Contact> future = threadPoolExecutor.submit(new Callable<Contact>() {
            @Override
            public Contact call() throws Exception {
                try {
                    return Contact.contacts[id];
                } catch (ArrayIndexOutOfBoundsException e) {
                    return null;
                }
            }
        });
        return future.get();
    }
}
