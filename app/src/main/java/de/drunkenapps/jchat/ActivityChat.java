package de.drunkenapps.jchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class ActivityChat extends AppCompatActivity {

    EditText textField;
    ListView listView;

    DataManager dataManager;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        dataManager = DataManager.getInstance(this);
        textField = findViewById(R.id.chat_messagefield);
        listView = findViewById(R.id.chat_chats_listview);

        listView.setAdapter(dataManager.getListAdapter());
    }

    public void sendButtonClicked(View v){
        if (textField.getText().toString().equals("")) {
            return;
        }
        dataManager.pushMessage(new Message(textField.getText().toString(), user.getUid(), new Date().getTime(), user.getDisplayName()));
        textField.setText("");
        listView.setSelection(dataManager.getLastMessagesIndex());
    }
}
