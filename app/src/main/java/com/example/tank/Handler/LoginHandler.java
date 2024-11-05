package com.example.tank.Handler;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.tank.MainActivity;
import com.example.tank.domain.Member;
import com.example.tank.ui.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.tasks.Task;

public class LoginHandler {
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference database;

    public LoginHandler(FirebaseAuth auth, GoogleSignInClient googleSignInClient, DatabaseReference database) {
        this.mAuth = auth;
        this.mGoogleSignInClient = googleSignInClient;
        this.database = database;
    }

    public Task<AuthResult> firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        return mAuth.signInWithCredential(credential);
    }
    public void saveUserToDatabase(Member user) {
        DatabaseReference usersRef = database.child("users").push();

        String photoUrl = user.getEmail() != null ? user.getEmail() : ""; // Verifica null
        usersRef.setValue(new Member(user.getEmail(), photoUrl, user.getName(), null, null), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                // Manejo de errores o acciones adicionales
            }
        });
    }
    public boolean onStart(){
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null;
    }

}
