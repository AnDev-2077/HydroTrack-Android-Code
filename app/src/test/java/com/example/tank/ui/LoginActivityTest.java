package com.example.tank.ui;

import junit.framework.TestCase;
import static org.mockito.Mockito.*;

import com.example.tank.Handler.LoginHandler;
import com.example.tank.domain.Member;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import android.content.Intent;

public class LoginActivityTest  {
    @Mock
    private FirebaseAuth mockAuth;
    @Mock
    private GoogleSignInClient mockGoogleSignInClient;
    @Mock
    private DatabaseReference mockDatabase;
    @Mock
    private FirebaseUser mockUser;

    private LoginHandler loginHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        loginHandler = new LoginHandler(mockAuth, mockGoogleSignInClient, mockDatabase);
    }
    @Test
    public void testFirebaseAuthWithGoogle_Success() {
        String testIdToken = "test_id_token";
        Task<AuthResult> mockTask = mock(Task.class);

        when(mockAuth.signInWithCredential(any(AuthCredential.class))).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(true);

        Task<AuthResult> result = loginHandler.firebaseAuthWithGoogle(testIdToken);

        assertEquals(mockTask, result);
    }
    @Test
    public void testSaveUserToDatabase_Success() {

        when(mockUser.getEmail()).thenReturn("test@example.com");
        when(mockUser.getDisplayName()).thenReturn("Test User");
        when(mockUser.getPhotoUrl()).thenReturn(null);

        DatabaseReference mockUserRef = mock(DatabaseReference.class);
        DatabaseReference mockUsersRef = mock(DatabaseReference.class);


        when(mockDatabase.child("users")).thenReturn(mockUsersRef);
        when(mockUsersRef.push()).thenReturn(mockUserRef);


        doAnswer(invocation -> {

            DatabaseReference.CompletionListener listener = invocation.getArgument(1);

            listener.onComplete(null, null);
            return null;
        }).when(mockUserRef).setValue(any(), any());


        String photoUrl = mockUser.getPhotoUrl() != null ? mockUser.getPhotoUrl().toString() : ""; // Maneja null aquí
        Member member = new Member(mockUser.getEmail(), photoUrl, mockUser.getDisplayName(), null, "");
        loginHandler.saveUserToDatabase(member);


        verify(mockUsersRef).push();
        verify(mockUserRef).setValue(any(), any());
    }
    @Test
    public void testOnStart_UserIsNotAuthenticated() {
        when(mockAuth.getCurrentUser()).thenReturn(null);

        boolean isAuthenticated = loginHandler.onStart();

        assertFalse(isAuthenticated);
    }

}