package com.gmail.afoserat.lesson1.views;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.afoserat.lesson1.R;
import com.gmail.afoserat.lesson1.model.Contact;
import com.gmail.afoserat.lesson1.viewmodels.ContactDetailsViewModel;

import java.lang.ref.WeakReference;
import java.util.Calendar;

public class ContactDetailsFragment extends Fragment {
    private static final String CONTACT_ID = "CONTACT_ID";
    private static final int NOTIFY_ABOUT_BIRTHDAY_AT_HOUR = 15;
    private static final int NOTIFY_ABOUT_BIRTHDAY_AT_MINUTES = 59;
    private Contact thisContact;
    ContactDetailsViewModel model;

    public static ContactDetailsFragment newInstance(String id) {
        ContactDetailsFragment fragment = new ContactDetailsFragment();
        Bundle args = new Bundle();
        args.putString(CONTACT_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.toolbar_title_contactDetails);
        model = new ContactDetailsViewModel(requireActivity().getApplication());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        model = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_details, container, false);
    }

    private Intent getIntentForAlarm() {
        final String ACTION = "com.gmail.afoserat.action.birthday_notification";
        Intent intent = new Intent(ACTION);
        intent.putExtra(CONTACT_ID, thisContact.getId());
        intent.putExtra(getString(R.string.contact_name), thisContact.getName());
        intent.putExtra(getString(R.string.birthday_message), getString(R.string.birthday_message__celebration));
        return intent;
    }

    private PendingIntent getAlarmIntent() {
        return PendingIntent.getBroadcast(
                getActivity(),
                thisContact.getId().hashCode(),
                getIntentForAlarm(),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    private void setAlarmAboutBirthday() {
        Calendar birthday = thisContact.getBirthdayCalendar();
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        if (birthday != null && alarmManager != null) {
            birthday.set(Calendar.HOUR_OF_DAY, NOTIFY_ABOUT_BIRTHDAY_AT_HOUR);
            birthday.set(Calendar.MINUTE, NOTIFY_ABOUT_BIRTHDAY_AT_MINUTES);
            birthday.set(Calendar.SECOND, 0);
            birthday.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
            if (birthday.getTimeInMillis() < System.currentTimeMillis()) {
                birthday.add(Calendar.YEAR, 1);
            }
            PendingIntent alarmIntent = getAlarmIntent();
            alarmManager.set(AlarmManager.RTC_WAKEUP, birthday.getTimeInMillis(), alarmIntent);
        }
    }

    private void cancelAlarmAboutBirthday() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(getAlarmIntent());
            getAlarmIntent().cancel();
        }
    }

    private boolean isAlarmUp() {
        return PendingIntent.getBroadcast(
                getContext(),
                thisContact.getId().hashCode(),
                getIntentForAlarm(),
                PendingIntent.FLAG_NO_CREATE) != null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        final WeakReference<View> refView = new WeakReference<>(view);
        if (arguments != null) {
            final String contactId = arguments.getString(CONTACT_ID);
            final CheckBox notifyBirthday = view.findViewById(R.id.user_birthday__checkbox);
            setOnCheckedChangeListenerBirthday(notifyBirthday);
            model.getContact(contactId).observe(getViewLifecycleOwner(), new Observer<Contact>() {
                @Override
                public void onChanged(Contact contact) {
                    if (refView != null) {
                        thisContact = contact;
                        final View v = refView.get();
                        if (v != null && thisContact != null) {
                            v.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (v != null) {
                                        TextView name = v.findViewById(R.id.user_name);
                                        name.setText(thisContact.getName());
                                        if (thisContact.getPhones().length > 0) {
                                            TextView phone = v.findViewById(R.id.phone_main);
                                            phone.setText(thisContact.getPhones()[0]);
                                        }
                                        if (thisContact.getEmails().length > 0) {
                                            TextView email = v.findViewById(R.id.email_main);
                                            email.setText(thisContact.getEmails()[0]);
                                        }
                                        if (thisContact.getImageUri() != null) {
                                            ImageView avatar = v.findViewById(R.id.user_photo);
                                            avatar.setImageURI(thisContact.getImageUri());
                                        }
                                        if (thisContact.getBirthday() != null) {
                                            TextView birthday = v.findViewById(R.id.user_birthday);
                                            birthday.setText(thisContact.getBirthday());
                                            notifyBirthday.setChecked(isAlarmUp());
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    private void setOnCheckedChangeListenerBirthday(@NonNull CheckBox birthdayCheckbox) {
        birthdayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setAlarmAboutBirthday();
                } else {
                    cancelAlarmAboutBirthday();
                }
            }
        });
    }
}
