package de.drunkenapps.jchat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * @author max
 * @date 1/6/18.
 */

public class AdapterForGroups extends ArrayAdapter<Group> {

    private final LayoutInflater inflater;
    private final int resourceId;
    private SimpleDateFormat simpleDateFormat;

    AdapterForGroups(Context context, final int resourceId, ArrayList<Group> items) {
        super(context, resourceId, items);

        this.resourceId = resourceId;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.GERMANY);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        convertView = inflater.inflate(R.layout.list_entry, null);

        TextView messageUsername = convertView.findViewById(R.id.list_view_entry_username);
        TextView messageText = convertView.findViewById(R.id.list_view_entry_message);
        TextView messageDate = convertView.findViewById(R.id.list_view_entry_timestamp);

        Group group = getItem(position);

        messageUsername.setText(group.getName());
        messageText.setText(group.getLastMessage());
        messageDate.setText(
                simpleDateFormat.format(
                        new Date(group.getLastTimestamp())
                )
        );

        return convertView;
    }
}
