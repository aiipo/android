package com.gmail.afoserat.lesson1;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

    private void waitFuture(Future<?> future) {
        while (true) {
            if (future.isDone() || future.isCancelled()) {
                break;
            }
        }
    }

    public Contact[] getContacts() throws ExecutionException, InterruptedException {
        Future<Contact[]> future = executorService.submit(new Callable<Contact[]>() {
            @Override
            public Contact[] call() {
                return Contact.contacts;
            }
        });
        waitFuture(future);
        return future.get();
    }

    public Contact getContactById(final int id) throws ExecutionException, InterruptedException {
        Future<Contact> future = executorService.submit(new Callable<Contact>() {
            @Override
            public Contact call() {
                try {
                    return Contact.contacts[id];
                } catch (ArrayIndexOutOfBoundsException e) {
                    return null;
                }
            }
        });
        waitFuture(future);
        return future.get();
    }
}
