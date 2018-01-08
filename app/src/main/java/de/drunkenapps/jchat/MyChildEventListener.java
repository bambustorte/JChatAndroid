package de.drunkenapps.jchat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * @author max
 * @date 1/8/18.
 */

public abstract class MyChildEventListener implements ChildEventListener {
    @Override
    public abstract void onChildAdded(DataSnapshot dataSnapshot, String s);

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
}
