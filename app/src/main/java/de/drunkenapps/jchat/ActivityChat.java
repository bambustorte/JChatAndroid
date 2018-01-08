package de.drunkenapps.jchat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class ActivityChat extends AppCompatActivity {

    EditText textField;
    ListView listView;
    String groupId;

    DataManager dataManager;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        groupId = getIntent().getStringExtra("groupId");

        dataManager = DataManager.getInstance(this);
        textField = findViewById(R.id.chat_messagefield);
        listView = findViewById(R.id.chat_chats_listview);

        listView.setAdapter(dataManager.getChatAdapter(groupId));

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(
                        "copiedChat",
                        ((Message) parent.getItemAtPosition(position)).getMessage());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(ActivityChat.this, R.string.chats_message_copied, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }

    public void sendButtonClicked(View v){
        if (textField.getText().toString().equals("")) {
            return;
        }
        dataManager.pushMessage(groupId, new Message(textField.getText().toString(), user.getUid(), new Date().getTime(), user.getDisplayName()));
        textField.setText("");

        //Fixme: does not work
//        listView.smoothScrollToPosition(dataManager.getLastMessagesIndex());
//        listView.setSelection(dataManager.getLastMessagesIndex());
    }
}
