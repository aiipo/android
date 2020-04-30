package com.gmail.afoserat.lesson1;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ContactListFragment extends Fragment {
    private View.OnClickListener listener = null;

    public static ContactListFragment newInstance() {
        return new ContactListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof View.OnClickListener) {
            listener = (View.OnClickListener) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("List of contacts");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        View user = view.findViewById(R.id.user);
        if (listener != null) {
            user.setOnClickListener(listener);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView name = view.findViewById(R.id.user_name);
        name.setText("Will");
        TextView phone = view.findViewById(R.id.user_name);
        phone.setText("79509509595");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
