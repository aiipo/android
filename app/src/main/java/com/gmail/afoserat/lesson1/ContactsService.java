package com.gmail.afoserat.lesson1;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.ContactsContract;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
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
                ArrayList<Contact> contacts = new ArrayList<>();
                Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );

                if (cursor != null) {
                    int displayName = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    int hasPhone = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                    int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                    int photoUri = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);

                    while (cursor.moveToNext()) {
                        String id = cursor.getString(idIndex);
                        String name = cursor.getString(displayName);
                        ArrayList<String> contact_phones = new ArrayList<>();
                        ArrayList<String> contact_emails = new ArrayList<>();
                        Uri image = cursor.getString(photoUri) != null ? Uri.parse(cursor.getString(photoUri)) : null;

                        if (hasPhone > 0) {
                            try (Cursor phones = getContentResolver()
                                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                            null,
                                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                            null,
                                            null)) {
                                if (phones != null) {
                                    while (phones.moveToNext()) {
                                        contact_phones.add(phones.getString(hasPhone));
                                    }
                                }
                            }
                        }

                        try (Cursor emails = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id,
                                null,
                                null)) {
                            if (emails != null) {
                                int emailColumnIndex = emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
                                while (emails.moveToNext()) {
                                    contact_emails.add(emails.getString(emailColumnIndex));
                                }
                            }
                        }
                        contacts.add(new Contact(
                                id,
                                name,
                                contact_phones.toArray(new String[contact_phones.size()]),
                                contact_emails.toArray(new String[contact_emails.size()]),
                                image));
                    }
                    cursor.close();
                }
                ContactListFragment.ResultListener listener = ref.get();
                if (listener != null) {
                    listener.onComplete(contacts.toArray(new Contact[contacts.size()]));
                }
            }
        });
    }

    public void getContactById(final String id, ContactDetailsFragment.ResultDetailsListener callback) {
        final WeakReference<ContactDetailsFragment.ResultDetailsListener> ref = new WeakReference<>(callback);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Contact contact = null;
                try (Cursor cursor = getContentResolver().query(
                        ContactsContract.Contacts.CONTENT_URI,
                        new String[]{
                                ContactsContract.Contacts._ID,
                                ContactsContract.Contacts.PHOTO_URI,
                                ContactsContract.Contacts.DISPLAY_NAME,
                                ContactsContract.Contacts.HAS_PHONE_NUMBER,
                        },
                        ContactsContract.Contacts._ID + " = " + id,
                        null,
                        null
                )) {
                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        ArrayList<String> phones = new ArrayList<>();
                        ArrayList<String> emails = new ArrayList<>();
                        Uri photo = null;
                        Calendar birthday = null;

                        int hasPhoneNumber = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                        if (cursor.getInt(hasPhoneNumber) > 0) {
                            try (Cursor cursorPhone = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{ id },
                                    null
                            )) {
                                if (cursorPhone != null && cursorPhone.getCount() > 0) {
                                    int numberColumnIndex = cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                                    while (cursorPhone.moveToNext()) {
                                        phones.add(cursorPhone.getString(numberColumnIndex));
                                    }
                                }
                            }
                        }

                        try (Cursor cursorEmail = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS},
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{ id },
                                null)) {
                            if (cursorEmail != null && cursorEmail.getCount() > 0) {
                                int emailColumnIndex = cursorEmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
                                while (cursorEmail.moveToNext()) {
                                    emails.add(cursorEmail.getString(emailColumnIndex));
                                }
                            }
                        }

                        String imageString = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                        if (imageString != null) {
                            photo = Uri.parse(imageString);
                        }

                        try (Cursor cursorBirthday = getContentResolver().query(
                                ContactsContract.Data.CONTENT_URI,
                                new String[]{ContactsContract.CommonDataKinds.Event.START_DATE},
                                ContactsContract.Data.CONTACT_ID + "= ? AND " +
                                        ContactsContract.Data.MIMETYPE + "= ? AND " +
                                        ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                                        ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY,
                                new String[]{ id, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE },
                                null)) {
                            if (cursorBirthday != null && cursorBirthday.moveToFirst()) {
                                int startDate = cursorBirthday.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
                                String date = cursorBirthday.getString(startDate);
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                Calendar calendar = Calendar.getInstance();
                                try {
                                    calendar.setTime(Objects.requireNonNull(format.parse(date)));
                                } catch (ParseException e) {
                                    calendar = null;
                                }
                                birthday = calendar;
                            }
                        }

                        if (birthday == null) {
                            contact = new Contact(id, name, phones.toArray(new String[phones.size()]), emails.toArray(new String[emails.size()]), photo);
                        } else {
                            contact = new Contact(id, name, phones.toArray(new String[phones.size()]), emails.toArray(new String[emails.size()]), photo, birthday);
                        }
                    }
                }
                ContactDetailsFragment.ResultDetailsListener listener = ref.get();
                if (listener != null) {
                    listener.onComplete(contact);
                }
            }
        });
    }
}
