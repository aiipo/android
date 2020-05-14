package com.gmail.afoserat.lesson1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Calendar;

public class ContactDetailsFragment extends Fragment {
    private static final String CONTACT_ID = "CONTACT_ID";
    private static final String CONTACT_NAME = "CONTACT_NAME";
    private static final String BIRTHDAY_MESSAGE = "BIRTHDAY_MESSAGE";
    private static final String BIRTHDAY_MESSAGE_CELEBRATION = "Сегодня день рождения у ";
    private static final int NOTIFY_ABOUT_BIRTHDAY_AT_HOUR = 9;
    private static final int NOTIFY_ABOUT_BIRTHDAY_AT_MINUTES = 30;
    private Contact thisContact;
    ContactsService mService;

    interface ResultDetailsListener {
        void onComplete(Contact contact);
    }

    public static ContactDetailsFragment newInstance(int id) {
        ContactDetailsFragment fragment = new ContactDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(CONTACT_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ContactListFragment.serviceAvailable) {
            mService = ((ContactListFragment.serviceAvailable) context).getService();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mService = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Details of the contact");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_details, container, false);
    }

    private void setAlarmAboutBirthday() {
        Calendar birthday = thisContact.getBirthdayCalendar();
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        if (birthday != null && alarmManager != null) {
            birthday.set(Calendar.HOUR_OF_DAY, NOTIFY_ABOUT_BIRTHDAY_AT_HOUR);
            birthday.set(Calendar.MINUTE, NOTIFY_ABOUT_BIRTHDAY_AT_MINUTES);
            birthday.set(Calendar.SECOND, 0);
            Intent intent = new Intent(getActivity(), ContactBirthdayReceiver.class);
            intent.putExtra(CONTACT_ID, thisContact.getId());
            intent.putExtra(CONTACT_NAME, thisContact.getName());
            intent.putExtra(BIRTHDAY_MESSAGE, BIRTHDAY_MESSAGE_CELEBRATION);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(
                    getContext(),
                    thisContact.getId(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    birthday.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 365,
                    alarmIntent
            );
        }
    }

    private void cancelAlarmAboutBirthday() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), ContactBirthdayReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                thisContact.getId(),
                intent,
                PendingIntent.FLAG_NO_CREATE
        );
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            ResultDetailsListener showContactDetails = new ResultDetailsListener() {
                @Override
                public void onComplete(Contact contact) {
                    thisContact = contact;
                    if (view != null) {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                if (view != null) {
                                    TextView name = view.findViewById(R.id.user_name);
                                    TextView phone = view.findViewById(R.id.phone_main);
                                    TextView email = view.findViewById(R.id.email_main);
                                    TextView birthday = view.findViewById(R.id.user_birthday);
                                    CheckBox notifyBirthday = view.findViewById(R.id.user_birthday__checkbox);

                                    name.setText(thisContact.getName());
                                    phone.setText(thisContact.getPhone());
                                    email.setText(thisContact.getEmail());
                                    if (thisContact.getBirthday() != null) {
                                        birthday.setText(thisContact.getBirthday());
                                        notifyBirthday.setChecked(thisContact.getStatusNotificationAboutBirthday());
                                    }
                                }
                            }
                        });
                    }
                }
            };
            final int contactId = getArguments().getInt(CONTACT_ID, 0);
            mService.getContactById(contactId, showContactDetails);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        View view = getView();
        final CheckBox notifyBirthday = view.findViewById(R.id.user_birthday__checkbox);
        if (notifyBirthday != null) {
            if (notifyBirthday.isChecked() && !thisContact.getStatusNotificationAboutBirthday()) {
                setAlarmAboutBirthday();
            } else {
                cancelAlarmAboutBirthday();
            }
            thisContact.setNotificationAboutBirthday(notifyBirthday.isChecked());
        }
    }

}
