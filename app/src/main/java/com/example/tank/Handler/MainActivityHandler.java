package com.example.tank.Handler;

import android.telephony.mbms.GroupCallCallback;

import com.example.tank.MainActivity;
import com.example.tank.domain.Group;
import com.example.tank.domain.Member;
import com.example.tank.ui.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivityHandler {
    private FirebaseAuth mAuth;
    private DatabaseReference database;

    public MainActivityHandler(FirebaseAuth auth, DatabaseReference database) {
        this.mAuth = auth;
        this.database = database;
    }

    public void getUser(FirebaseUser currentUser, UserCallback callback) {
        DatabaseReference membersRef = database.child("users");

        membersRef.orderByChild("email").equalTo(currentUser.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Member member = null;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                        member = memberSnapshot.getValue(Member.class);
                    }
                }
                callback.onUserRetrieved(member);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }
    public void getUserGroups(Member member, GroupCallback callback) {
        if (member != null && member.getGroups() != null) {
            List<Group> userGroups = new ArrayList<>();
            DatabaseReference groupsRef = database.child("groups");
            AtomicInteger groupsProcessed = new AtomicInteger(0);
            int totalGroups = member.getGroups().size();

            for (String groupId : member.getGroups()) {
                groupsRef.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Group group = dataSnapshot.getValue(Group.class);
                            if (group != null) {
                                userGroups.add(group);
                            }
                        }
                        if (groupsProcessed.incrementAndGet() == totalGroups) {
                            callback.onGroupsRetrieved(userGroups);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onError(databaseError.toException());
                    }
                });
            }
        } else {
            callback.onError(new Exception("Member or groups list is null."));
        }
    }
    public void updateUserModule(FirebaseUser currentUser, String newModuleId, UpdateCallback callback) {
        DatabaseReference usersRef = database.child("users");
        usersRef.orderByChild("email").equalTo(currentUser.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey();
                        DatabaseReference userIdModuleRef = usersRef.child(userId).child("idModule");

                        userIdModuleRef.setValue(newModuleId).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                callback.onUpdateSuccess();
                            } else {
                                callback.onUpdateError(task.getException());
                            }
                        });
                    }
                } else {
                    callback.onUpdateError(new Exception("User not found."));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onUpdateError(databaseError.toException());
            }
        });
    }

    public interface UserCallback {
        void onUserRetrieved(Member member);
        void onError(Exception e);
    }

    public interface GroupCallback {
        void onGroupsRetrieved(List<Group> groups);
        void onError(Exception e);
    }

    public interface UpdateCallback {
        void onUpdateSuccess();
        void onUpdateError(Exception e);
    }

}
