package comp5216.sydney.edu.au.haplanet;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import comp5216.sydney.edu.au.haplanet.adapter.ListviewMessageAdapter;
import comp5216.sydney.edu.au.haplanet.model.EventModel;
import comp5216.sydney.edu.au.haplanet.model.MessageModel;

public class MessageActivity extends AppCompatActivity {

    ListView mListView;
    ImageView btn_submit;
    EditText txtMessage;
    ArrayList<MessageModel> messageModelArrayList;
    FirebaseFirestore db;
    FirebaseDatabase database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        EventModel eventModel = (EventModel) getIntent().getSerializableExtra("eventModel");
        String messageTitle = eventModel.getTitle();

        db = FirebaseFirestore.getInstance();

        messageModelArrayList = new ArrayList<MessageModel>();
        mListView = findViewById(R.id.idLVMessages);
        //去除listView边框
        mListView.setDivider(null);
        btn_submit = findViewById(R.id.btn_submit);
        txtMessage = findViewById(R.id.et_input);

        db = FirebaseFirestore.getInstance();

//        db.collection("message").get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        if (!queryDocumentSnapshots.isEmpty()) {
//                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
//                            for (DocumentSnapshot d : list) {
//                                MessageModel messageModel = d.toObject(MessageModel.class);
//                                if (messageModel.getTitle().contains(messageTitle)) {
//                                    messageModelArrayList.add(messageModel);
//                                }
//                                ListviewReplyAdapter adapter = new ListviewReplyAdapter(ReplyActivity.this, messageModelArrayList);
//                                mListView.setAdapter(adapter);
//                            }
//                        } else {
//                            Toast.makeText(ReplyActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(ReplyActivity.this, "Fail to load data...", Toast.LENGTH_SHORT).show();
//                    }
//                });

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
                messageModelArrayList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    MessageModel messageModel = ds.getValue(MessageModel.class);
                    if (Objects.equals(messageModel.getTitle(), messageTitle)) {
                        messageModelArrayList.add(messageModel);
                    }
                }
                ListviewMessageAdapter adapter = new ListviewMessageAdapter(MessageActivity.this, messageModelArrayList);
                mListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = String.valueOf(txtMessage.getText());
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                long timestamp = System.currentTimeMillis();
                Date date = new Date(timestamp);

                txtMessage.setText("");

                Log.e("Date", String.valueOf(date));

                Log.e("Timestamp", String.valueOf(timestamp));

//                CollectionReference files = FirebaseFirestore.getInstance().collection("message");

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference messageRef = database.getReference("message").child(String.valueOf(timestamp));

                MessageModel newMessageModel = new MessageModel(messageTitle, uid, message, date);

                messageRef.setValue(newMessageModel);

//                files.add(newMessageModel);
//                messageModelArrayList.add(newMessageModel);
            }
        });

    }
}
