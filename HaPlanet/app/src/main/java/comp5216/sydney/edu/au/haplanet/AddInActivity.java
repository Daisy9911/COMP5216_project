package comp5216.sydney.edu.au.haplanet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import comp5216.sydney.edu.au.haplanet.model.EventModel;
import comp5216.sydney.edu.au.haplanet.model.UserModel;

public class AddInActivity extends AppCompatActivity {

    ImageView ivImage, ivAvaterImage;
    TextView txtTitle, txtTime, txtCategory, txtOwner, txtContent, txtStartTime, txtLocation, txtNumberOfPeople, txtPrice, txtRemainNumber, txtRemainNumberTitle;
    Button btnJoin;
    ArrayList<EventModel> eventModelArrayList;
    EventModel eventModel;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        eventModel = (EventModel) getIntent().getSerializableExtra("eventModel");

        ivImage = findViewById(R.id.iv_pic);
        ivAvaterImage = findViewById(R.id.iv_owner_pic);

        txtTitle = findViewById(R.id.txt_title);
        txtTime = findViewById(R.id.txt_time);
        txtCategory = findViewById(R.id.txt_category);
        txtOwner = findViewById(R.id.txt_owner);
        txtContent = findViewById(R.id.txt_content);
        txtStartTime = findViewById(R.id.txt_start_time);
        txtLocation = findViewById(R.id.txt_location);
        txtNumberOfPeople = findViewById(R.id.txt_number_of_people);
        txtPrice = findViewById(R.id.txt_Price);
        txtRemainNumber = findViewById(R.id.txt_remain_number);
        txtRemainNumberTitle = findViewById(R.id.txt_remain_number_title);

        btnJoin = findViewById(R.id.btn_join);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageEventRef = storage.getReferenceFromUrl("gs://haplanet-83dba.appspot.com").child("files").child(eventModel.getPicture());

        try {
            File localEventFile = File.createTempFile("images", "jpg");
            storageEventRef.getFile(localEventFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

//                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                    ivImage.setImageBitmap(bitmap);
                    Glide.with(AddInActivity.this)
                            .load(localEventFile.getAbsolutePath())
                            //transition(TransitionOptions transitionOptions)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(ivImage);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        setOwnerImage();

//        Log.e("Content",eventModel.getDescription());

        txtTitle.setText(eventModel.getTitle());
        txtTime.setText(eventModel.getTime() + " minutes");
        txtCategory.setText(eventModel.getCategory());
        txtContent.setText("Introduction:\n" + eventModel.getDescription());
        txtStartTime.setText(eventModel.getStartTime());
        txtLocation.setText("Location:" + eventModel.getLocation());
        txtNumberOfPeople.setText(eventModel.getNumberOfPeople() + " People");
        txtPrice.setText("$" + eventModel.getPrice() + "/per person");
//        txtOwner.setText(owner + " invites you to join...");

        int number = eventModel.getUidList().size();
        int remainNumber = Integer.parseInt(eventModel.getNumberOfPeople()) - number;
        txtRemainNumberTitle.setText("Need " + eventModel.getNumberOfPeople() + " people");
        txtRemainNumber.setText("Waiting " + remainNumber + " people to start");

        String owner = eventModel.getUidList().get(0);
        btnJoin.setText("$" + eventModel.getPrice() + " / per person \nPay it and join now");

    }

    public void joinOnclick(View view) {

        String title = eventModel.getTitle();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ArrayList<String> uidList = eventModel.getUidList();
        if (!uidList.contains(uid)) {
            uidList.add(uid);
            db = FirebaseFirestore.getInstance();

            db.collection("files").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot d : list) {
                                    EventModel eventModel = d.toObject(EventModel.class);
                                    String id = d.getId();

                                    if (Objects.equals(eventModel.getTitle(), title)) {
                                        FirebaseFirestore
                                                .getInstance().collection("files").document(id).update("uidList", uidList);
                                        Toast.makeText(AddInActivity.this, "Join in...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddInActivity.this, "Fail to load data...", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Toast.makeText(this, "You have join before", Toast.LENGTH_SHORT).show();
        }
    }

    public void setOwnerImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        String ownerUid = eventModel.getUidList().get(0);

        db = FirebaseFirestore.getInstance();

        db.collection("users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            int flag = 1;
                            for (DocumentSnapshot d : list) {
                                UserModel userModel = d.toObject(UserModel.class);
                                if (Objects.equals(userModel.getUid(), ownerUid)) {
                                    String profileUrl = userModel.getAvatarUrl();
                                    StorageReference storageAvatarRef = storage.getReferenceFromUrl("gs://haplanet-83dba.appspot.com").child("users").child(profileUrl);

                                    try {
                                        File localAvatarFile = File.createTempFile("images", "jpg");
                                        storageAvatarRef.getFile(localAvatarFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Glide.with(AddInActivity.this)
                                                        .load(localAvatarFile.getAbsolutePath())
                                                        //transition(TransitionOptions transitionOptions)
                                                        .transition(DrawableTransitionOptions.withCrossFade())
                                                        .into(ivAvaterImage);

                                                if(userModel.getUsername() != null) {
                                                    txtOwner.setText(userModel.getUsername() + " invites you to join...");
                                                } else {
                                                    txtOwner.setText(" invites you to join...");
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                            if(flag == 1) {
                                txtOwner.setText("invites you to join...");
                            }
                        } else {
                            Toast.makeText(AddInActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddInActivity.this, "Fail to load data...", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
