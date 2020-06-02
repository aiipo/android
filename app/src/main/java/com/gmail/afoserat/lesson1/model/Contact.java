package com.gmail.afoserat.lesson1.model;

import android.net.Uri;

import java.util.Calendar;
import java.util.Locale;

public class Contact {
    private final String id;
    private final String name;
    private final String[] phones;
    private final String[] emails;
    private Uri imageUri = null;
    private Calendar birthday = null;

    public Contact(String id, String name, String[] phones, String[] emails) {
        this.id = id;
        this.name = name;
        this.phones = phones;
        this.emails = emails;
    }

    public Contact(String id, String name, String[] phones, String[] emails, Uri imageUri, Calendar birthday) {
        this(id, name, phones, emails, imageUri);
        this.birthday = Calendar.getInstance();
        this.birthday.set(Calendar.MONTH, birthday.get(Calendar.MONTH));
        this.birthday.set(Calendar.DAY_OF_MONTH, birthday.get(Calendar.DAY_OF_MONTH));
    }

    public Contact(String id, String name, String[] phones, String[] emails, Uri imageUri) {
        this(id, name, phones, emails);
        this.imageUri = imageUri;
    }

    public String getName() {
        return name;
    }

    public String[] getPhones() {
        return phones;
    }

    public String[] getEmails() {
        return emails;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public String getBirthday() {
        if (this.birthday != null) {
            String month = birthday.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            return birthday.get(Calendar.DAY_OF_MONTH) + " " + month;
        }
        return null;
    }

    public Calendar getBirthdayCalendar() {
        return this.birthday;
    }

    public String getId() {
        return id;
    }
}
