package br.scaylart.malbile.views.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import br.scaylart.malbile.R;
import br.scaylart.malbile.models.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    public ArrayList<Message> records;
    public String username;
    Context context;

    public MessageAdapter(Context context, String username) {
        this.context = context;
        this.username = username;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_profile_message, parent, false);

        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message item = records.get(position);
        holder.vUsername.setText(item.getUsername());
        holder.vTitle.setText(item.getTitle());
        holder.vShortMessage.setText(item.getShortMessage());
        holder.vData.setText(item.getDateMsg());
    }

    public void setRecords(ArrayList<Message> newRecords) {
        if (records == newRecords)
            return;

        records = newRecords;

        if (records != null) {
            notifyDataSetChanged();
        }
    }

    public Message getDataByIndex(int position) {
        if (records != null) {
            return records.get(position);
        } else
            return null;
    }

    @Override
    public int getItemCount() {
        return (records != null) ? records.size() : 0;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView vTitle;
        protected TextView vUsername;
        protected TextView vShortMessage;
        protected TextView vData;

        public MessageViewHolder(View v) {
            super(v);
            vTitle = (TextView) v.findViewById(R.id.msg_title);
            vUsername = (TextView) v.findViewById(R.id.msg_username);
            vShortMessage = (TextView) v.findViewById(R.id.msg_shortMessage);
            vData = (TextView) v.findViewById(R.id.msg_dateMsg);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
