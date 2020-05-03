package com.gmail.afoserat.lesson1;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ContactListFragment extends Fragment {
    static final Contact[] contacts = {
            new Contact("Will", "79509509595", "will@yandex.ru"),
    };

    private onContactSelectedListener listener = null;

    interface onContactSelectedListener {
        void onContactSelected(int id);
    }

    public static ContactListFragment newInstance() {
        return new ContactListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onContactSelectedListener) {
            listener = (onContactSelectedListener) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("List of contacts");
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("List of contacts");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        View user = view.findViewById(R.id.user);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onContactSelected((Integer) v.getTag());
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView name = view.findViewById(R.id.user_name);
        view.setTag(0);
        name.setText(contacts[0].getName());
        TextView phone = view.findViewById(R.id.user_name);
        phone.setText(contacts[0].getPhone());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
