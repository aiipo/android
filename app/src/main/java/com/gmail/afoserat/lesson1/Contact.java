package com.gmail.afoserat.lesson1;

import android.net.Uri;

import java.util.Calendar;
import java.util.Locale;

public class Contact {
    private final int id;
    private final String name;
    private final String[] phones;
    private final String[] emails;
    private Uri imageUri = null;
    private Calendar birthday = null;

    Contact(int id, String name, String[] phones, String[] emails) {
        this.id = id;
        this.name = name;
        this.phones = phones;
        this.emails = emails;
    }

    Contact(int id, String name, String[] phones, String[] emails, int month, int day) {
        this(id, name, phones, emails);
        this.birthday = Calendar.getInstance();
        this.birthday.set(Calendar.MONTH, month);
        this.birthday.set(Calendar.DAY_OF_MONTH, day);
    }

    Contact(int id, String name, String[] phones, String[] emails, Uri imageUri) {
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

    public int getId() {
        return id;
    }
}
