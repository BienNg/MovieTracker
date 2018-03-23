package com.example.bien_pc.movielist.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.bien_pc.movielist.R;
import com.example.bien_pc.movielist.adapters.RvAdapterChat;
import com.example.bien_pc.movielist.helper.MyFirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    // Views
    private EditText edittxtSend;
    private ImageButton bttnSend;
    private String chatPartner;
    private int movieId;

    // Variables
    final private String TAG = "ChatActivity";
    private MyFirebaseUser myFirebaseUser;
    private ArrayList<String> listMessages = new ArrayList<>();

    // RV Variables
    private RvAdapterChat adapterChat;
    private RecyclerView rvChatMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Init. Views
        edittxtSend = (EditText) findViewById(R.id.act_chat_edittxt_send);
        bttnSend = (ImageButton) findViewById(R.id.act_chat_bttn_send);
        rvChatMessages = (RecyclerView) findViewById(R.id.act_chat_recycler_view);

        // Init. Variables
        myFirebaseUser = new MyFirebaseUser();

        // Get ChatPartner id
        chatPartner = getIntent().getStringExtra("ChatPartner");

        // Get Movie id
        movieId = getIntent().getIntExtra("MovieID",0);

        // Declaring the behaviour of the views
        setupViews();
    }

    /**
     * Declares behaviour of the views
     */
    private void setupViews(){
        Log.d(TAG, "setupViews: setting up views.");
        myFirebaseUser.createChat(myFirebaseUser.getUsername(), chatPartner, movieId);

        bttnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send text to the firebase chat database
                String sendText = edittxtSend.getText().toString();
                myFirebaseUser.sendChatMessage(myFirebaseUser.getUsername(),chatPartner,movieId,sendText);
                edittxtSend.setText("");
            }
        });

        setupRecyclerView();
    }

    private void setupRecyclerView(){
        // Setting up recyclerview items
        rvChatMessages.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvChatMessages.setLayoutManager(linearLayoutManager);
        adapterChat = new RvAdapterChat(this, listMessages);
        rvChatMessages.setAdapter(adapterChat);

        if(myFirebaseUser.getChatByIdRef(myFirebaseUser.getUsername(), chatPartner, movieId) != null){
            myFirebaseUser.getChatByIdRef(myFirebaseUser.getUsername(), chatPartner, movieId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int counter = 0;
                            for(DataSnapshot d : dataSnapshot.getChildren()){
                                counter++;
                                listMessages.add(d.child("message").getValue().toString());
                            }
                            adapterChat.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }
}
