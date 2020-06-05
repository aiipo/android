package com.gmail.afoserat.lesson1;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.annotation.Nullable;

import com.gmail.afoserat.lesson1.model.Contact;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class ContactRepository {
    private WeakReference<ContentResolver> ref;

    public ContactRepository(ContentResolver contentResolver) {
        ref = new WeakReference<>(contentResolver);
    }

    public ArrayList<Contact> getContacts(@Nullable String nameSelector) {
        final ContentResolver contentResolver = ref.get();
        if (contentResolver != null) {
            ArrayList<Contact> contacts = new ArrayList<>();
            String selection = nameSelector != null
                    ? ContactsContract.Contacts.DISPLAY_NAME + " LIKE '%" + nameSelector + "%'"
                    : null;
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                    null,
                    selection,
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
                        try (Cursor phones = contentResolver
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

                    try (Cursor emails = contentResolver.query(
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
            return contacts;
        }
        return null;
    }

    public Contact getContactById(final String id) {
        final ContentResolver contentResolver = ref.get();
        if (contentResolver != null) {
            Contact contact;
            try (Cursor cursor = contentResolver.query(
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
                        try (Cursor cursorPhone = contentResolver.query(
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

                    try (Cursor cursorEmail = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
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

                    try (Cursor cursorBirthday = contentResolver.query(
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
                    return contact;
                }
            }
        }
        return null;
    }
}
