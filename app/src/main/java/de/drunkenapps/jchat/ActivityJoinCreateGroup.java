package de.drunkenapps.jchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class ActivityJoinCreateGroup extends AppCompatActivity {

    String editTextText;
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    EditText groupName;
    Button button;

    Intent intent;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_join_create_group);

        intent = getIntent();

        groupName = findViewById(R.id.create_group_et_name);
        button = findViewById(R.id.create_group_button_create);

        type = intent.getIntExtra("type", 1);

        if (type == 1){
            button.setText(R.string.create_join_group_join);
            groupName.setHint(R.string.create_join_group_groupid);
        } else {
            button.setText(R.string.create_join_group_create);
            groupName.setHint(R.string.create_join_group_groupname);
        }

    }

    public void buttonClicked(View v){
        if (intent == null)
            return;

        editTextText = groupName.getText().toString();

        if (type == 1){
            DataManager.getInstance(ActivityJoinCreateGroup.this).joinGroup(editTextText);
            finish();
            return;
        }

        DataManager.getInstance(ActivityJoinCreateGroup.this).createGroup(editTextText, userId, "public");
        finish();
    }
}
