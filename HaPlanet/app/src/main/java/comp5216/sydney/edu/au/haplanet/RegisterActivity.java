package comp5216.sydney.edu.au.haplanet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.xuexiang.xui.XUI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import comp5216.sydney.edu.au.haplanet.model.UserModel;

public class RegisterActivity extends AppCompatActivity {

    //request codes
    private static final int MY_PERMISSIONS_REQUEST_READ_PHOTOS = 102;
    private String filePath;

    EditText et_username,et_email, et_password;
    Button btn_register;
    TextView tv_login;
    ImageView iv_add_avatar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_username = findViewById(R.id.et_username);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_register = findViewById(R.id.btn_register);
        tv_login = findViewById(R.id.tv_login);

        iv_add_avatar = findViewById(R.id.iv_add_avatar);

        iv_add_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });

        tv_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

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

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHOTOS) {
            if (resultCode == RESULT_OK) {
                Uri photoUri = data.getData();
                // Do something with the photo based on Uri
                Bitmap selectedImage;
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);

                    // Load the selected image into a preview
                    iv_add_avatar.setImageBitmap(selectedImage);
                    iv_add_avatar.setVisibility(View.VISIBLE);

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

    private void signup() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(et_email.getText().toString(),et_password.getText()
                .toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                File file = new File(filePath);

                if(task.isSuccessful()) {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("users").child(fileName);
                    storageReference.putFile(Uri.fromFile(file)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if(task.isSuccessful()) {

                                CollectionReference users = FirebaseFirestore.getInstance().collection("users");
                                UserModel newUserModel = new UserModel(uid, et_email.getText().toString(), fileName, et_username.getText().toString(), "");
                                users.add(newUserModel);

                            }

                        }
                    });
                    Toast.makeText(RegisterActivity.this, "Upload Firebase Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(RegisterActivity.this, "error", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}
