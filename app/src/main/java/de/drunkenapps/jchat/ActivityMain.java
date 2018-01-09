package de.drunkenapps.jchat;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityMain extends AppCompatActivity {

    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    DataManager dataManager;

    ListView groupsOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (user == null){
            Intent intent = new Intent(this.getApplicationContext(), ActivityLogin.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            final Intent intent = new Intent(ActivityMain.this, ActivityJoinCreateGroup.class);

            new AlertDialog.Builder(ActivityMain.this)
                .setTitle("vla")//fixme
                .setMessage("tut")//fixme
                .setPositiveButton("join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intent.putExtra("type", 1);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        intent.putExtra("type", 2);
                        startActivity(intent);
                    }
                })
                .show();
            }
        });

        dataManager = DataManager.getInstance(this);
        groupsOverview = findViewById(R.id.chats_overview_listview);

        groupsOverview.setAdapter(dataManager.getGroupAdapter());//new MyGroupAdapter(ActivityMain.this, R.layout.list_entry, dataManager.getGroups()));
//        Log.d("test2", groupsOverview.getAdapter().toString());

        groupsOverview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(ActivityMain.this, ActivityChat.class);
            intent.putExtra("groupId", dataManager.getGroups().get(position).getGroupId());
            startActivity(intent);
            }
        });

        groupsOverview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(
                    "copiedChat",
                    ((Group) parent.getItemAtPosition(position)).getGroupId());
                Log.d("test", ((Group) parent.getItemAtPosition(position)).getGroupId());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(ActivityMain.this, R.string.groupId_copied, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

//        Log.d("test", dataManager.getGroups().toString());

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
        if (id == R.id.menu_refresh) {
            update();
            return true;
        }
        if (id == R.id.menu_signout){
//            DataManager.getInstance(ActivityMain.this).deleteUserData();
            if (user.isAnonymous())
                user.delete();
            auth.signOut();
            DataManager.dropInstance();
            finish();
            Intent intent = new Intent(ActivityMain.this, ActivityMain.class);
            startActivity(intent);
        }
        if (id == R.id.menu_showcurrentuser){
            Toast.makeText(this.getApplicationContext(), user.getUid(), Toast.LENGTH_LONG).show();
            Log.d("test", user.getUid());
        }

        return super.onOptionsItemSelected(item);
    }


    public void update(){
        dataManager.updateAll();
    }
}
