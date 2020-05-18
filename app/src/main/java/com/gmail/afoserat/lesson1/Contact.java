package com.gmail.afoserat.lesson1;

import java.util.Calendar;
import java.util.Locale;

public class Contact {
    private final String name;
    private final String phone;
    private final String email;
    private Calendar birthday = Calendar.getInstance();

    static final Contact[] contacts = {
        new Contact("Will", "79509509595", "will@yandex.ru"),
        new Contact("Chill", "72599999995", "chill@yandex.ru"),
        new Contact("Fill", "73655555656", "fill@yandex.ru"),
        new Contact("Hill", "77777775656", "hill@yandex.ru", 5, 7),
        new Contact("Till", "78888888888", "till@yandex.ru", 5, 14),
        new Contact("Pill", "79999999999", "nill@yandex.ru", 5, 14),
    };

    Contact(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.birthday = null;
    }

    Contact(String name, String phone, String email, int month, int day) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.birthday.set(Calendar.MONTH, month);
        this.birthday.set(Calendar.DAY_OF_MONTH, day);
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
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
        for (int i = 0; i < contacts.length; i++) {
            if (contacts[i] == this) {
                return i;
            }
        }
        return -1;
    }
}
