package com.example.tank;

import junit.framework.TestCase;
import static org.mockito.Mockito.*;

import com.example.tank.Handler.LoginHandler;
import com.example.tank.Handler.MainActivityHandler;
import com.example.tank.domain.Group;
import com.example.tank.domain.Member;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import android.content.Intent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Collections;

public class MainActivityTest extends TestCase {
    @Mock
    private FirebaseAuth mockAuth;

    @Mock
    private DatabaseReference mockDatabaseRef;
    @Mock
    DataSnapshot mockUserSnapshot;
    @Mock
    private FirebaseUser mockUser;
    @Mock
    Query mockQuery;
    @Mock
    DatabaseReference mockIdModuleRef;
    @Mock
    private DataSnapshot mockDataSnapshot;

    private MainActivityHandler mainActivityHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mainActivityHandler = new MainActivityHandler(mockAuth, mockDatabaseRef);
    }

    @Test
    public void testGetUser_UserExists() {
        String email = "test@example.com";

        when(mockUser.getEmail()).thenReturn(email);


        DatabaseReference usersRef = mock(DatabaseReference.class);
        when(mockDatabaseRef.child("users")).thenReturn(usersRef);

        Query mockQuery = mock(Query.class);
        when(usersRef.orderByChild("email")).thenReturn(mockQuery);
        when(mockQuery.equalTo(email)).thenReturn(mockQuery);


        when(mockDataSnapshot.exists()).thenReturn(true);


        DataSnapshot mockChildSnapshot = mock(DataSnapshot.class);
        when(mockChildSnapshot.getValue(Member.class)).thenReturn(new Member(email));


        List<DataSnapshot> children = Arrays.asList(mockChildSnapshot);
        when(mockDataSnapshot.getChildren()).thenReturn(children);


        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            listener.onDataChange(mockDataSnapshot);
            return null;
        }).when(mockQuery).addListenerForSingleValueEvent(any(ValueEventListener.class));


        mainActivityHandler.getUser(mockUser, new MainActivityHandler.UserCallback() {
            @Override
            public void onUserRetrieved(Member member) {
                assertNotNull("El objeto Member no debería ser nulo", member); // Verificar que el miembro no sea nulo
                assertEquals("El email del Member no coincide", email, member.getEmail()); // Verificar que el email sea el esperado
            }

            @Override
            public void onError(Exception e) {
                fail("No debería haberse llamado a onError");
            }
        });


        verify(mockDatabaseRef).child("users");
        verify(usersRef).orderByChild("email");
        verify(mockQuery).equalTo(email);
    }

    @Test
    public void testGetUser_UserDoesNotExist() {
        String nonExistentEmail = "nonexistent@example.com";


        when(mockUser.getEmail()).thenReturn(nonExistentEmail);


        DatabaseReference usersRef = mock(DatabaseReference.class);
        when(mockDatabaseRef.child("users")).thenReturn(usersRef);

        Query mockQuery = mock(Query.class);
        when(usersRef.orderByChild("email")).thenReturn(mockQuery);
        when(mockQuery.equalTo(nonExistentEmail)).thenReturn(mockQuery);


        when(mockDataSnapshot.exists()).thenReturn(false);


        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            listener.onDataChange(mockDataSnapshot);
            return null;
        }).when(mockQuery).addListenerForSingleValueEvent(any(ValueEventListener.class));


        mainActivityHandler.getUser(mockUser, new MainActivityHandler.UserCallback() {
            @Override
            public void onUserRetrieved(Member member) {
                assertNull("El objeto Member debería ser nulo ya que el usuario no existe", member); // Verificar que `member` sea nulo
            }

            @Override
            public void onError(Exception e) {
                fail("No debería haberse llamado a onError");
            }
        });


        verify(mockDatabaseRef).child("users");
        verify(usersRef).orderByChild("email");
        verify(mockQuery).equalTo(nonExistentEmail);
    }
    @Test
    public void testGetUserGroups() {

        Member member = new Member();
        member.setGroups(Arrays.asList("groupId1", "groupId2"));


        DatabaseReference mockGroupsRef = mock(DatabaseReference.class);
        DatabaseReference mockGroupId1Ref = mock(DatabaseReference.class);
        DatabaseReference mockGroupId2Ref = mock(DatabaseReference.class);

        when(mockDatabaseRef.child("groups")).thenReturn(mockGroupsRef);
        when(mockGroupsRef.child("groupId1")).thenReturn(mockGroupId1Ref);
        when(mockGroupsRef.child("groupId2")).thenReturn(mockGroupId2Ref);


        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            DataSnapshot mockSnapshot = mock(DataSnapshot.class);
            when(mockSnapshot.exists()).thenReturn(true);
            when(mockSnapshot.getValue(Group.class)).thenReturn(new Group());  // Simula un grupo
            listener.onDataChange(mockSnapshot);
            return null;
        }).when(mockGroupId1Ref).addListenerForSingleValueEvent(any(ValueEventListener.class));

        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            DataSnapshot mockSnapshot = mock(DataSnapshot.class);
            when(mockSnapshot.exists()).thenReturn(true);
            when(mockSnapshot.getValue(Group.class)).thenReturn(new Group());  // Simula un grupo
            listener.onDataChange(mockSnapshot);
            return null;
        }).when(mockGroupId2Ref).addListenerForSingleValueEvent(any(ValueEventListener.class));


        mainActivityHandler.getUserGroups(member, new MainActivityHandler.GroupCallback() {
            @Override
            public void onGroupsRetrieved(List<Group> groups) {
                assertEquals(2, groups.size());
            }

            @Override
            public void onError(Exception e) {
                fail("Should not have called onError");
            }
        });
    }


}