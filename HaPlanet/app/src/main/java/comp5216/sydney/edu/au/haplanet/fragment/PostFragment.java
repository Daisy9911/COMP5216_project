package comp5216.sydney.edu.au.haplanet.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import comp5216.sydney.edu.au.haplanet.MainActivity;
import comp5216.sydney.edu.au.haplanet.MarshmallowPermission;
import comp5216.sydney.edu.au.haplanet.R;
import comp5216.sydney.edu.au.haplanet.model.EventModel;

public class PostFragment extends Fragment {

    //request codes
    private static final int MY_PERMISSIONS_REQUEST_READ_PHOTOS = 102;

    private String filePath;
    private EventModel eventModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MarshmallowPermission marshmallowPermission = new MarshmallowPermission(getActivity());

        ImageView photopreview = (ImageView) getActivity().findViewById(R.id.iv_select_pic);

        EditText titleText = (EditText) getActivity().findViewById(R.id.txt_title);
        EditText contentText = (EditText) getActivity().findViewById(R.id.txt_content);
        EditText timeText = (EditText) getActivity().findViewById(R.id.txt_time);
        EditText locationText = (EditText) getActivity().findViewById(R.id.txt_location);
        EditText numberOfPeopleText = (EditText) getActivity().findViewById(R.id.txt_number_of_people);
        EditText priceText = (EditText) getActivity().findViewById(R.id.txt_Price);
        Button submitBtn = (Button) getActivity().findViewById(R.id.btn_submit);

        photopreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!marshmallowPermission.checkPermissionForReadfiles()) {
                    marshmallowPermission.requestPermissionForReadfiles();
                } else {
                    // Create intent for picking a photo from the gallery
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    // Bring up gallery to select a photo
                    startActivityForResult(intent, MY_PERMISSIONS_REQUEST_READ_PHOTOS);
                }
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                String title = titleText.getText().toString();
                String content = contentText.getText().toString();
                String time = timeText.getText().toString();
                String location = locationText.getText().toString();
                String numberOfPeople = numberOfPeopleText.getText().toString();
                String price = priceText.getText().toString();

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                ArrayList<String> uidList = new ArrayList<>();
                uidList.add(uid);

                eventModel = new EventModel(filePath, title, content, time, location, numberOfPeople, price, uidList);

                FirebaseDatabase.getInstance().setPersistenceEnabled(true);

                if(eventModel != null) {

                    String path = eventModel.getPicture();

                    Log.e("picture", path);
                    Log.e("title", title);

                    File newFile = new File(path);
                    String fileName = path.substring(path.lastIndexOf("/") + 1);

                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReference().child("files").child(fileName);
                    storageReference.putFile(Uri.fromFile(newFile))
                            .addOnCompleteListener(task -> {
//                                String newFilepath = task.toString();
                                CollectionReference files = FirebaseFirestore
                                        .getInstance().collection("files");

                                EventModel newEventModel = new EventModel(fileName, title, content, time, location, numberOfPeople, price, uidList);
                                files.add(newEventModel);

                                Toast.makeText(getActivity(),
                                        "Upload Firebase Success",
                                        Toast.LENGTH_SHORT).show();
                            });

                }

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        ImageView ivPreview = (ImageView) getActivity().findViewById(R.id.iv_select_pic);



        ivPreview.setVisibility(View.GONE);

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHOTOS) {
            if (resultCode == RESULT_OK) {
                Uri photoUri = data.getData();
                // Do something with the photo based on Uri
                Bitmap selectedImage;
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(
                            getActivity().getContentResolver(), photoUri);

                    // Load the selected image into a preview
                    ivPreview.setImageBitmap(selectedImage);
                    ivPreview.setVisibility(View.VISIBLE);

                    filePath = uriToPath(getActivity(), photoUri, 0);

                    Log.e("photoUri", String.valueOf(photoUri));
                    Log.e("filePath",filePath);


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

}