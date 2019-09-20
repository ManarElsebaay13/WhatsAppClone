package com.example.manarelsebaay.scopechat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private ListView LV;

    private ArrayAdapter arrayAdapter;
    private ArrayList<String> List_of_Groups = new ArrayList<>();
    private DatabaseReference GroupsRef;

    private  View groupsFragment ;


    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupsFragment= inflater.inflate(R.layout.fragment_groups, container, false);


        GroupsRef= FirebaseDatabase.getInstance().getReference().child("Groups");



        IntializeFields();

        RetrieveGroupsData();



        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> parent, View view, int position, long id) {

                String currentGroupname=parent.getItemAtPosition(position).toString();

                Intent ToGroupChatActivity= new Intent(getContext(),GroupChatActivity.class);
                ToGroupChatActivity.putExtra("groupName",currentGroupname);
                startActivity(ToGroupChatActivity);

            }
        });


        return  groupsFragment;


    }



    public void IntializeFields() {

        LV=(ListView)groupsFragment.findViewById(R.id.List_view_Group);
        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,List_of_Groups);
        LV.setAdapter(arrayAdapter);

    }


    private void RetrieveGroupsData()

    {
        GroupsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                Iterator iterator =dataSnapshot.getChildren().iterator();

                Set<String> set= new HashSet<>();


                while (iterator.hasNext())
                {
                    set.add(((DataSnapshot)iterator.next()).getKey());

                }

                List_of_Groups.clear();
                List_of_Groups.addAll(set);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }





}
