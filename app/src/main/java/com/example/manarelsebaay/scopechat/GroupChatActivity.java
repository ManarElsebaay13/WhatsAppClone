package com.example.manarelsebaay.scopechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import androidx.appcompat.widget.Toolbar;

public class GroupChatActivity extends AppCompatActivity {


    private Toolbar groupBar;
    private ScrollView groupview;
    private TextView DisplayTextmessage;
    private EditText UserMsgInput;
    private ImageButton send;
    private  String currentgroupname,currentusername,currentuserUID,currentDate,currentTime;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef,GroupNameRef,GroupMsgKeyRef;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);


        currentgroupname=getIntent().getExtras().get("groupName").toString();


        mAuth=FirebaseAuth.getInstance();
        currentuserUID=mAuth.getCurrentUser().getUid();
        UserRef=FirebaseDatabase.getInstance().getReference().child("Users");
        GroupNameRef=FirebaseDatabase.getInstance().getReference().child(currentgroupname);


        Intialization();

        GetUserInfo();


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendMessageInfoToDatabase();

                UserMsgInput.setText("");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                if(dataSnapshot.exists())
                {

                    DisplayMessages(dataSnapshot);

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists())
                {

                    DisplayMessages(dataSnapshot);

                }


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });












    }








    private void Intialization() {

        groupBar=findViewById(R.id.groupChat_bar);
        setSupportActionBar(groupBar);
        getSupportActionBar().setTitle(currentgroupname);



        groupview=findViewById(R.id.Scroll_Group_chat);
        DisplayTextmessage=findViewById(R.id.group_chat_Text);
        UserMsgInput=findViewById(R.id.Message);
        send=findViewById(R.id.Send_btn);


    }





    private void GetUserInfo() {


        UserRef.child(currentuserUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    currentusername=dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void SendMessageInfoToDatabase() {

        String Message =UserMsgInput.getText().toString();
        String MessageKey=GroupNameRef.push().getKey();


        if (TextUtils.isEmpty(Message))
        {

            Toast.makeText(this, "Please Enter your msg", Toast.LENGTH_SHORT).show();

        }else
        {

            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currDateFormat= new SimpleDateFormat("MM,dd,yyyy");
            currentDate=currDateFormat.format(calForDate.getTime());



            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currTimeFormat= new SimpleDateFormat("hh:mm a");
            currentTime=currTimeFormat.format(calForTime.getTime());


            HashMap<String,Object> groupMsgKey=new HashMap <>();
            GroupNameRef.updateChildren(groupMsgKey);

            GroupMsgKeyRef=GroupNameRef.child(MessageKey);




            HashMap<String,Object> messageInfoMap=new HashMap <>();

            messageInfoMap.put("name",currentusername);
            messageInfoMap.put("message",Message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);


            GroupMsgKeyRef.updateChildren(messageInfoMap);






        }







    }







    private void DisplayMessages(DataSnapshot dataSnapshot) {


        Iterator iterator= dataSnapshot.getChildren().iterator();

        while(iterator.hasNext())

        {
            String ChatDate=(String)((DataSnapshot)iterator.next()).getValue();
            String ChatMessage=(String)((DataSnapshot)iterator.next()).getValue();
            String ChatName=(String)((DataSnapshot)iterator.next()).getValue();
            String ChatTime=(String)((DataSnapshot)iterator.next()).getValue();

            DisplayTextmessage.append(ChatName +":\n" + ChatMessage + "\n"+ ChatTime +"   "+ ChatDate+"\n\n\n");

        }




    }










}
