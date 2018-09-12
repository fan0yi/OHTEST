package com.example.yifanliu.firechat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AnswerActivity extends AppCompatActivity {


    private ArrayList<Contact> list;
    String[] realAnswer;
    String[] response;
    int quantity_right, quantity_wrong;
    boolean isReady = false;
    TextView right,wrong, homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        response = getIntent().getExtras().getStringArray("response");
        String where = getIntent().getExtras().getString("where");

        for(int i = 0; i < response.length ; i++){
            Log.i("response "+i, response[i]);
        }

        right = findViewById(R.id.right);
        wrong = findViewById(R.id.wrong);


        //Recyclerview adapter
        list = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recyclerView_answer);
        homeButton = findViewById(R.id.homeButton);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final MyAnswerRecyclerViewAdapter adapter = new MyAnswerRecyclerViewAdapter(list , response);
        recyclerView.setAdapter(adapter);

        //get value frome firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(where);
        Log.i("poin", "DatabaseReference");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                realAnswer = new String[(int) dataSnapshot.getChildrenCount()];
                for(int i = 0; i<realAnswer.length ; i++){
                    String tempAnswer = (String) dataSnapshot.child((i+1) + "/answer").getValue();
                    realAnswer[i] = tempAnswer;
                }
                isReady = true;
                countNum();
                //Log.i("point", "onDataChange");

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

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnswerActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        //countNum();
    }

    private void countNum(){
        quantity_right = 0;
        quantity_wrong = 0;
        for(int i = 0; i < realAnswer.length ; i++){
            if(realAnswer[i].equals(response[i])){
                quantity_right++;
            }else if( !realAnswer[i].equals(response[i])){
                quantity_wrong++;
            }
            Log.i("response", response[i]);
        }

        right.setText("Right:"+quantity_right);
        wrong.setText("Wrong:"+quantity_wrong);

    }

}

class MyAnswerRecyclerViewAdapter extends RecyclerView.Adapter<MyAnswerRecyclerViewAdapter.ViewHolder>{

    ArrayList<Contact> list;
    String[] response;
    public MyAnswerRecyclerViewAdapter(ArrayList<Contact> list, String[] response) {
        this.list = list;
        this.response = response;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_answer, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyAnswerRecyclerViewAdapter.ViewHolder holder, int position) {
        Contact contact = list.get(position);
        holder.answer_question.setText((position+1) + ". " +contact.getQuestion());
        holder.answer_item.setText(contact.getAnswer());

        if(!response[position].equals(contact.getAnswer())){
            holder.answer_item.setTextColor(Color.parseColor("#B30810"));
        }


        String item = contact.getItem();
        RadioButton button;
        final String[] item_array = item.split(",");
        holder.answerGroup.removeAllViews();

        for (String s : item_array) {
            button = new RadioButton(holder.answerGroup.getContext());
            button.setTextSize(20);
            button.setTextColor(Color.parseColor("#333333"));
            button.setText(s);
            holder.answerGroup.addView(button);
            if(response[position].equals(s)){
                button.setChecked(true);
            }
            button.setEnabled(false);
        }



    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView answer_question, answer_item;
        RadioGroup answerGroup;

        public ViewHolder(View itemView) {
            super(itemView);
            answer_question = itemView.findViewById(R.id.answer_question);
            answer_item = itemView.findViewById(R.id.answer_item);
            //answer = itemView.findViewById(R.id.answer);
            //response = itemView.findViewById(R.id.response);
            answerGroup = itemView.findViewById(R.id.answerGroup);
        }
    }
}
