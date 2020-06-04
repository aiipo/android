package com.gmail.afoserat.lesson1.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.afoserat.lesson1.R;
import com.gmail.afoserat.lesson1.model.Contact;
import com.gmail.afoserat.lesson1.viewholders.ContactViewHolder;
import com.gmail.afoserat.lesson1.views.ContactListFragment;

import java.util.List;

/**
 * {@link ListAdapter} that can display a {@link ContactListFragment}.
 */
public class ContactRecyclerViewAdapter extends ListAdapter<Contact, ContactViewHolder> {
    private onContactListener mOnContactListener;

    public interface onContactListener {
        void onContactClick(View view);
    }

    public ContactRecyclerViewAdapter(onContactListener onContactListener) {
        super(DIFF_CALLBACK);
        this.mOnContactListener = onContactListener;
    }

    public void setContactList(List<Contact> contactList) {
        submitList(contactList);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contact_list_item, parent, false);
        return new ContactViewHolder(view, mOnContactListener);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static final DiffUtil.ItemCallback<Contact> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Contact>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull Contact oldContact, @NonNull Contact newContact) {
                    return oldContact.getId().equals(newContact.getId());
                }
                @Override
                public boolean areContentsTheSame(
                        @NonNull Contact oldContact, @NonNull Contact newContact) {
                    return oldContact.getId().equals(newContact.getId())
                            && oldContact.getName().equals(newContact.getName());
                }
            };
}