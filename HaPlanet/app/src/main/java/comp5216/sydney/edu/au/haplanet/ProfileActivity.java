package comp5216.sydney.edu.au.haplanet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import comp5216.sydney.edu.au.haplanet.adapter.ListviewEventAdapter;
import comp5216.sydney.edu.au.haplanet.model.EventModel;
import comp5216.sydney.edu.au.haplanet.model.ProfileModel;

public class ProfileActivity extends AppCompatActivity {

    //request codes
    private static final int MY_PERMISSIONS_REQUEST_READ_PHOTOS = 102;
    private String filePath, updateFilepath;

    ImageView ivImage;
    EditText txtUsername, txtIntroduction, txtGender, txtAge;
    Button btnModify;
    //    ArrayList<ProfileModel> profileModelArrayList;
    ProfileModel profileModel, modifyProfileModel;
    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        ivImage = findViewById(R.id.iv_profile_image);
        txtUsername = findViewById(R.id.et_username);
        txtIntroduction = findViewById(R.id.et_introduction);
//        txtGender = findViewById(R.id.et_gender);
        txtAge = findViewById(R.id.et_age);
        btnModify = findViewById(R.id.btn_modify);


        db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("profiles").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {

                                ProfileModel newProfileModel = d.toObject(ProfileModel.class);

                                if (Objects.equals(newProfileModel.getUid(), uid)) {
                                    profileModel = newProfileModel;
                                }

                                if (profileModel != null) {

                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                    StorageReference storageRef = storage.getReferenceFromUrl("gs://haplanet-83dba.appspot.com")
                                            .child("profiles").child(profileModel.getPicture());

                                    try {
                                        File localFile = File.createTempFile("images", "jpg");
                                        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                                ivImage.setImageBitmap(bitmap);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    txtUsername.setText(profileModel.getUsername());
                                    txtIntroduction.setText(profileModel.getIntroduction());
                                    txtAge.setText(String.valueOf(profileModel.getAge()));
                                    updateFilepath = profileModel.getPicture();

                                }

                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Fail to load data...", Toast.LENGTH_SHORT).show();
                    }
                });
        ivImage.setOnClickListener(v -> takePhoto());
        btnModify.setOnClickListener(v -> modify());
    }

    private void takePhoto() {
        MarshmallowPermission marshmallowPermission = new MarshmallowPermission(this);
        if (!marshmallowPermission.checkPermissionForReadfiles()) {
            marshmallowPermission.requestPermissionForReadfiles();
        } else {
            // Create intent for picking a photo from the gallery
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            // Bring up gallery to select a photo
            startActivityForResult(intent, MY_PERMISSIONS_REQUEST_READ_PHOTOS);
        }

    }

    @SuppressLint("Range")
    private String uriToPath(Context context, Uri uri, int type) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        if (cursor.moveToFirst() && type == 0) {
            try {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return path;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        ImageView ivPreview = (ImageView) this.findViewById(R.id.iv_profile_image);

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHOTOS) {
            if (resultCode == RESULT_OK) {
                Uri photoUri = data.getData();
                // Do something with the photo based on Uri
                Bitmap selectedImage;
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);

                    // Load the selected image into a preview
                    ivPreview.setImageBitmap(selectedImage);
                    ivPreview.setVisibility(View.VISIBLE);

                    filePath = uriToPath(this, photoUri, 0);

                    Log.e("photoUri", String.valueOf(photoUri));
                    Log.e("filePath", filePath);


                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private void modify() {

        String username = txtUsername.getText().toString();
        String introduction = txtIntroduction.getText().toString();
        String age = txtAge.getText().toString();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ArrayList<String> uidList = new ArrayList<>();
        uidList.add(uid);

        if (username.isEmpty() || introduction.isEmpty() || age.isEmpty() || updateFilepath == null) {
            Toast.makeText(this, "Please fill in all blanks", Toast.LENGTH_SHORT).show();
        } else {
            modifyProfileModel = new ProfileModel(filePath, uid, username, introduction, Integer.parseInt(age));
//                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            if (filePath != null) {

                String path = modifyProfileModel.getPicture();

//                Log.e("picture", path);

                File newFile = new File(path);
                String fileName = path.substring(path.lastIndexOf("/") + 1);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profiles").child(fileName);
                storageReference.putFile(Uri.fromFile(newFile)).addOnCompleteListener(task -> {
//                                String newFilepath = task.toString();
                    CollectionReference files = FirebaseFirestore.getInstance().collection("profiles");

                    ProfileModel newModifyProfileModel = new ProfileModel(fileName, uid, username, introduction, Integer.parseInt(age));
                    int[] flag = {0};

                    db.collection("profiles").get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                        for (DocumentSnapshot d : list) {
                                            ProfileModel updateProfileModel = d.toObject(ProfileModel.class);
                                            String id = d.getId();

                                            if (Objects.equals(updateProfileModel.getUid(), uid)) {
                                                FirebaseFirestore
                                                        .getInstance().collection("profiles").document(id).update("fileName", fileName);
                                                FirebaseFirestore
                                                        .getInstance().collection("profiles").document(id).update("username", username);
                                                FirebaseFirestore
                                                        .getInstance().collection("profiles").document(id).update("introduction", introduction);
                                                FirebaseFirestore
                                                        .getInstance().collection("profiles").document(id).update("age", Integer.parseInt(age));
                                                flag[0] = 1;
                                            }
                                        }

                                        if (flag[0] != 1) {
                                            files.add(newModifyProfileModel);
                                            Toast.makeText(ProfileActivity.this, "Upload Firebase Success", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        Toast.makeText(ProfileActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ProfileActivity.this, "Fail to load data...", Toast.LENGTH_SHORT).show();
                                }
                            });


//                    Intent intent = new Intent(this, MainActivity.class);
//                    startActivity(intent);

                });

            } else {

                db.collection("profiles").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    for (DocumentSnapshot d : list) {
                                        ProfileModel updateProfileModel = d.toObject(ProfileModel.class);
                                        String id = d.getId();

                                        if (Objects.equals(updateProfileModel.getUid(), uid)) {
                                            FirebaseFirestore
                                                    .getInstance().collection("profiles").document(id).update("username", username);
                                            FirebaseFirestore
                                                    .getInstance().collection("profiles").document(id).update("introduction", introduction);
                                            FirebaseFirestore
                                                    .getInstance().collection("profiles").document(id).update("age", Integer.parseInt(age));
                                        }
                                    }

                                } else {
                                    Toast.makeText(ProfileActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, "Fail to load data...", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }
    }
}
