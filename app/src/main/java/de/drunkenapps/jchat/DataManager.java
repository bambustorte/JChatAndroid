package de.drunkenapps.jchat;

import android.content.Context;
import android.widget.ListAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * @author max
 * @date 1/6/18.
 */

class DataManager {

    private static DataManager instance = null;

    private ArrayList<Message> messages;
    private Context context;
    private ArrayList<AdapterForChats> listAdapters;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference userRootNode = database.getReference();//asdf.child(user.getUid());


    private DataManager(Context context){
        this.context = context;

        messages = new ArrayList<>();
        listAdapters = new ArrayList<>();

        userRootNode.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                messages.add(0, dataSnapshot.getValue(Message.class));
                for (AdapterForChats listadapter :
                        listAdapters) {
                    listadapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    static DataManager getInstance(Context context) {
        if (instance == null){
            instance = new DataManager(context);
        }
        return instance;
    }

    static void dropInstance() {
        instance = null;
    }

    void pushMessage(Message message){
        DatabaseReference newMessage = userRootNode.child("messages").push();
        message.setMid(newMessage.getKey());
        newMessage.setValue(message);
    }

    ListAdapter getListAdapter(){
        listAdapters.add(new AdapterForChats(context, R.layout.list_entry, messages));
        return listAdapters.get(listAdapters.size() - 1);
    }

    int getLastIndex(){
        return messages.size() - 1;
    }
}
