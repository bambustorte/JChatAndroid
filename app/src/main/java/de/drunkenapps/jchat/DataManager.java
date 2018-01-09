package de.drunkenapps.jchat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author max
 * @date 1/6/18.
 */

class DataManager {

    private static DataManager instance = null;

    private ArrayList<Group> groups;
    private Context context;
    private ArrayList<AdapterForChats> chatAdapters;
    private ArrayList<AdapterForGroups> groupAdapters;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference rootNode = database.getReference();
    private DatabaseReference userRootNode = database.getReference().child("users").child(user.getUid());


    private DataManager(final Context context){
        this.context = context;

        groups = new ArrayList<>();
        chatAdapters = new ArrayList<>();
        groupAdapters = new ArrayList<>();

        userRootNode.child("groups").addChildEventListener(new MyChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final String groupId = dataSnapshot.getKey();

            final DatabaseReference referenceToGroup = rootNode.child("groups").child(groupId);

            Log.d("test", "groupId = " + groupId
                    + ", path to group: " + referenceToGroup.toString());

            final String[] groupName = {""};

            referenceToGroup.addChildEventListener(new MyChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (!dataSnapshot.getKey().equals("info")){
                        return;
                    }

                    if (!dataSnapshot.child("name").exists()) {
                        Log.d("test", "name does not exist, key was: " + referenceToGroup.getKey());
                        return;
                    }

                    groupName[0] = (String) dataSnapshot.child("name").getValue();

                    groups.add(
                        new Group(
                                referenceToGroup,
                                groupName[0],
                                groupId,
                                (Long) dataSnapshot.child("lastTimestamp").getValue()
                        )
                    );

                    referenceToGroup.child("info").child("lastTimestamp").addChildEventListener(new MyChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s){

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            Log.d("test", "changed! " + dataSnapshot.getValue());
                        }
                    });

                    for (ArrayAdapter groupAdapter : groupAdapters) {
                        groupAdapter.notifyDataSetChanged();
                    }

                    Log.d("test", "groupName = " + groupName[0]);

                    final Group group = groups.get(groups.size()-1);

                    group.getDatabaseReference().child("messages").addChildEventListener(new MyChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        group.addMessage(dataSnapshot.getValue(Message.class));
                        for (ArrayAdapter chatAdapter : chatAdapters) {
                            chatAdapter.notifyDataSetChanged();
                        }
                        //Todo: only show notification if unread and not opened
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel")
                            .setContentTitle("title")
                            .setContentText("text")
                            .setSmallIcon(R.mipmap.icon);
                        Intent resultIntent = new Intent(context, ActivityChat.class);

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

                        stackBuilder.addParentStack(ActivityChat.class);

                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                            );
                        builder.setContentIntent(resultPendingIntent);

                        NotificationManager notificationManager =
                                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(0, builder.build());

                        }
                    });
                    return;
                }
            });
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

    void pushMessage(String groupId, Message message){
        DatabaseReference newMessage = rootNode.child("groups").child(groupId).child("messages").push();
        message.setMid(newMessage.getKey());
        newMessage.setValue(message);
    }

    /**
     *
     * @param groupId is a string, representing the group id the user is trying to join
     * @return status code number:
     *      0 is representing success
     *      1 is representing non existing group
     *      2 is representing already in group
     *      3 is representing private group
     */
    int joinGroup(String groupId){
        final int[] returnResult = {0};

        rootNode.child("groups").child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ( dataSnapshot.getValue() == null ) {
                    returnResult[0] = 1;
                    Toast.makeText(context, "group does not exist", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ( dataSnapshot.child("members").hasChild(user.getUid())){
                    returnResult[0] = 2;
                    Toast.makeText(context, "already in group", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ( dataSnapshot.hasChild("policy")
                                && ( (String)dataSnapshot.child("policy").getValue() ).equals("private") ){
                    returnResult[0] = 3;
                    Toast.makeText(context, "not allowed", Toast.LENGTH_SHORT).show();
                    return;
                }

                //todo: think about more returns
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (returnResult[0] == 0){
            userRootNode.child("groups").child(groupId).setValue(1);
            rootNode.child("groups").child(groupId).child("members").child(user.getUid()).setValue(1);
            Log.d("joinGroup", groupId );
        }

        Log.d("joinGroup", Integer.toString(returnResult[0]) );

        return returnResult[0];
    }

    String createGroup(String name, String userId, String policy){
        DatabaseReference newGroupReference = rootNode.child("groups").push();

        newGroupReference.child("members").child(userId).setValue(1);
        newGroupReference.child("policy").setValue(policy);
        newGroupReference.child("info").child("name").setValue(name);
        newGroupReference.child("info").child("lastTimestamp").setValue(new Date().getTime());
        pushMessage(
                newGroupReference.getKey(),
                new Message(user.getDisplayName()
                        + "joined the chat", "system",
                        new Date().getTime(), ""));

        userRootNode.child("groups").child(newGroupReference.getKey()).setValue(1);

        Log.d("test", "created group: " + newGroupReference.getKey() );

        return newGroupReference.toString();
    }

    AdapterForChats getChatAdapter(String groupId){
//        Object t = new Message();//upcast
        ArrayList<Message> msg = new ArrayList<>();

        for (Group group : groups) {
            if (group.getGroupId().equals(groupId)) {
                msg = group.getMessages();
                break;
            }
        }

        AdapterForChats adapterForChats = new AdapterForChats(context, R.layout.list_entry, msg);
        chatAdapters.add(adapterForChats);
        return adapterForChats;
    }

    AdapterForGroups getGroupAdapter(){
//        Object t = new Message();//upcast
        ArrayList<Group> grp = groups;

        AdapterForGroups adapterForGroups = new AdapterForGroups(context, R.layout.list_entry, grp);
        groupAdapters.add(adapterForGroups);
        return adapterForGroups;
    }

    ArrayList<Group> getGroups() {
        return groups;
    }

    void updateAll() {
        for (AdapterForGroups adapterForGroups: groupAdapters){
            adapterForGroups.notifyDataSetChanged();
        }

        for (AdapterForChats adapterForChats: chatAdapters){
            adapterForChats.notifyDataSetChanged();
        }
    }

    void cleanUp(){
        Log.d("test", "" + groups.size());
        for (Group group : groups) {
            Log.d("test", "group is " + group.getName());
//
//            DatabaseReference reference = group.getDatabaseReference();
//            reference.child()
//
//            rootNode.child("groups")
        }
    }
}
