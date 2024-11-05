package com.example.tank.ui;

import junit.framework.TestCase;
import static org.mockito.Mockito.*;

import com.example.tank.Handler.HistoryFragmentHandler;
import com.example.tank.Handler.LoginHandler;
import com.example.tank.domain.DataModule;
import com.example.tank.domain.Member;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import android.content.Intent;
import static org.junit.Assert.assertArrayEquals; // Para JUnit 4
// o
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryFragmentTest extends TestCase {
    @Mock
    private DatabaseReference mockDatabaseReference;
    @Mock
    private DataSnapshot mockDataSnapshot;

    private HistoryFragmentHandler handler;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new HistoryFragmentHandler(mockDatabaseReference);
    }

    @Test
    public void testObtenerDatos_Success() {
        String keyModuleCurrent = "testModule";
        when(mockDatabaseReference.child("ModulesWifi/" + keyModuleCurrent)).thenReturn(mockDatabaseReference);
        when(mockDatabaseReference.orderByChild("fecha")).thenReturn(mockDatabaseReference);
        ValueEventListener valueEventListener = mock(ValueEventListener.class);
        doAnswer(invocation -> {
            DataSnapshot snapshot = mock(DataSnapshot.class);
            when(snapshot.getChildren()).thenReturn(new ArrayList<DataSnapshot>());
            valueEventListener.onDataChange(snapshot);
            return null;
        }).when(mockDatabaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));
        HistoryFragmentHandler.DataCallback callback = new HistoryFragmentHandler.DataCallback() {
            @Override
            public void onDataRetrieved(List<DataModule> dataModules) {
                assertNotNull(dataModules);
            }

            @Override
            public void onError(Exception e) {
                fail("Error callback called");
            }
        };
        handler.obtenerDatos(keyModuleCurrent, callback);
    }

    @Test
    public void testObtenerDatos_Error() {
        String keyModuleCurrent = "testModule";

        when(mockDatabaseReference.child("ModulesWifi/" + keyModuleCurrent)).thenReturn(mockDatabaseReference);
        when(mockDatabaseReference.orderByChild("fecha")).thenReturn(mockDatabaseReference);
        ValueEventListener valueEventListener = mock(ValueEventListener.class);
        doAnswer(invocation -> {
            DatabaseError databaseError = DatabaseError.fromCode(DatabaseError.DISCONNECTED);
            valueEventListener.onCancelled(databaseError);
            return null;
        }).when(mockDatabaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));
        HistoryFragmentHandler.DataCallback callback = new HistoryFragmentHandler.DataCallback() {
            @Override
            public void onDataRetrieved(List<DataModule> dataModules) {
                fail("Success callback called when it should have failed");
            }

            @Override
            public void onError(Exception e) {
                assertNotNull(e);
            }
        };
        handler.obtenerDatos(keyModuleCurrent, callback);
    }





}