package com.gmail.afoserat.lesson1.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.afoserat.lesson1.R;
import com.gmail.afoserat.lesson1.adapters.ContactRecyclerViewAdapter;
import com.gmail.afoserat.lesson1.model.Contact;

public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private View mView;
    public final TextView contactNameView;
    public final TextView contactPhoneView;
    public final ImageView contactImageView;

    private ContactRecyclerViewAdapter.onContactListener onContactListener;

    public ContactViewHolder(@NonNull View itemView, ContactRecyclerViewAdapter.onContactListener onContactListener) {
        super(itemView);
        mView = itemView;
        contactNameView = (TextView) itemView.findViewById(R.id.user_name);
        contactPhoneView = (TextView) itemView.findViewById(R.id.user_phone);
        contactImageView = (ImageView) itemView.findViewById(R.id.user_photo);
        this.onContactListener = onContactListener;
        itemView.setOnClickListener(this);
    }

    public void bind(Contact contact) {
        mView.setTag(R.string.contact_id, contact.getId());

        contactNameView.setText(contact.getName());
        if (contact.getPhones().length > 0) {
            contactPhoneView.setText(contact.getPhones()[0]);
        }
        if (contact.getImageUri() != null) {
            contactImageView.setImageURI(contact.getImageUri());
        }
    }

    @Override
    public void onClick(View v) {
        if (getAdapterPosition() != RecyclerView.NO_POSITION) {
            onContactListener.onContactClick(v);
        }
    }
}