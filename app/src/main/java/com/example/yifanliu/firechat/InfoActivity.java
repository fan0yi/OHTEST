package com.example.yifanliu.firechat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity {
    private ArrayList<Contact> list;
    String value;
    private String[] responseAnswer;
    TextView doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        value = getIntent().getExtras().getString("who");
        list = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recycler);
        doneButton = findViewById(R.id.doneButton);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final MyRecyclerAdapter adapter = new MyRecyclerAdapter(list);
        recyclerView.setAdapter(adapter);

        recyclerView.setItemViewCacheSize(50);

        DatabaseReference reference_contacts = FirebaseDatabase.getInstance().getReference(value);
        reference_contacts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //adapter.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //for(int i = 1; i< dataSnapshot.getChildrenCount() ; i++){
                    //    Contact contact = dataSnapshot.getValue(Contact.class);
                    Contact contact = ds.getValue(Contact.class);
                    list.add(contact);
                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyRecyclerAdapter adapter = new MyRecyclerAdapter(list);
                responseAnswer = adapter.getResponseAnswer();
                Intent intent = new Intent(InfoActivity.this, AnswerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArray("response", responseAnswer);
                bundle.putString("where", value);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


    }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.submit, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//
//        switch (item.getItemId()) {
//            case R.id.submit:
//                MyRecyclerAdapter adapter = new MyRecyclerAdapter(list);
//                responseAnswer = adapter.getResponseAnswer();
//                Intent intent = new Intent(InfoActivity.this, AnswerActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putStringArray("response", responseAnswer);
//                bundle.putString("where", value);
//                intent.putExtras(bundle);
//                startActivity(intent);
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
}

class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {
    ArrayList<Contact> list;
    static String[] responseAnswer;
    boolean isReady = false;

    public MyRecyclerAdapter(ArrayList<Contact> list) {
        this.list = list;
    }

    public String[] getResponseAnswer() {
        return responseAnswer;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Contact contact = list.get(position);
        holder.tv_question.setText((position + 1) + ". " + contact.getQuestion());
        //responseAnswer = new String[list.size()];
        String item = contact.getItem();
        RadioButton button;
        final String[] item_array = item.split(",");
        holder.rGroup.removeAllViews();
        for (String s : item_array) {
            button = new RadioButton(holder.rGroup.getContext());
            button.setText(s);
            button.setTextColor(Color.parseColor("#333333"));
            button.setTextSize(20);
            holder.rGroup.addView(button);
            if(responseAnswer[position].equals(s)){
                button.setChecked(true);
            }
        }

        holder.rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton checkedButton = group.findViewById(checkedId);
                responseAnswer[position] = checkedButton.getText().toString();
                for(int i = 0; i<responseAnswer.length; i++){
                    Log.i("response: " + i, responseAnswer[i]);
                }

            }
        });

    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tv_question;
        RadioGroup rGroup;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_question = itemView.findViewById(R.id.tv_question);
            rGroup = itemView.findViewById(R.id.itemGroup);

            Log.i("viewholder", "here");
            if(!isReady){
                responseAnswer = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    responseAnswer[i] = "";
                    Log.i("isReady", "here");
                }
                isReady = true;
            }


        }

    }


}
