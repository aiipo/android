package com.gmail.afoserat.lesson1;

public class Contact {
    private final String name;
    private final String phone;
    private final String email;

    static final Contact[] contacts = {
        new Contact("Will", "79509509595", "will@yandex.ru"),
        new Contact("Chill", "72599999995", "chill@yandex.ru"),
        new Contact("Fill", "73655555656", "fill@yandex.ru"),
    };

    Contact(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
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
}
