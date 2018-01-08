package de.drunkenapps.jchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;

public class ActivityMain extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    DataManager dataManager;

    ListView chatsOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (user == null){
            Intent intent = new Intent(this.getApplicationContext(), ActivityLogin.class);
            startActivity(intent);
            finish();
            return;
        }

        if (user.getDisplayName().equals("")){
            user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName("anon").build());
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityMain.this, ActivityChat.class);
                startActivity(intent);
            }
        });


        dataManager = DataManager.getInstance(this);
        chatsOverview = findViewById(R.id.chats_overview_listview);

        ArrayList<String> strings = new ArrayList<>();
        strings.add("test");
        strings.add("test");
        strings.add("test");
        strings.add("test");
        strings.add("test");
        strings.add("test");
        strings.add("test");
        chatsOverview.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strings));
        chatsOverview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ActivityMain.this, ActivityChat.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_settings) {
            return true;
        }
        if (id == R.id.menu_signout){
            auth.signOut();
            DataManager.dropInstance();
            finish();
        }
        if (id == R.id.menu_showcurrentuser){
            Toast.makeText(this.getApplicationContext(), user.getUid(), Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }


}
