package com.example.bien_pc.movielist.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.bien_pc.movielist.R;
import com.example.bien_pc.movielist.helper.MyFirebaseUser;

public class ChatActivity extends AppCompatActivity {

    // Views
    private EditText edittxtSend;
    private ImageButton bttnSend;
    private String chatPartner;
    private int movieId;

    // Variables
    final private String TAG = "ChatActivity";
    private MyFirebaseUser myFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Init. Views
        edittxtSend = (EditText) findViewById(R.id.ac_chat_edittxt_send);
        bttnSend = (ImageButton) findViewById(R.id.act_chat_bttn_send);

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
    }
}
