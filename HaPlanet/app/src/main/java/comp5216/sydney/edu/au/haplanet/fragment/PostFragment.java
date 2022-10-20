package comp5216.sydney.edu.au.haplanet.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import comp5216.sydney.edu.au.haplanet.MainActivity;
import comp5216.sydney.edu.au.haplanet.MarshmallowPermission;
import comp5216.sydney.edu.au.haplanet.R;
import comp5216.sydney.edu.au.haplanet.model.EventModel;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class PostFragment extends Fragment {

    //request codes
    private static final int MY_PERMISSIONS_REQUEST_READ_PHOTOS = 102;

    private String filePath;
    private EventModel eventModel;

    ImageView photopreview;
    EditText titleText, contentText, locationText, numberOfPeopleText, priceText, timeText, categoryText;
    TextView startDateText, startTimeText;
    Button submitBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        photopreview = (ImageView) getActivity().findViewById(R.id.iv_select_pic);

        titleText = (EditText) getActivity().findViewById(R.id.txt_title);
        contentText = (EditText) getActivity().findViewById(R.id.txt_content);

        startDateText = (TextView) getActivity().findViewById(R.id.txt_start_date);
        startTimeText = (TextView) getActivity().findViewById(R.id.txt_start_time);
//        DateTimePicker dateTimePicker = getActivity().findViewById(R.id.dateTimePicker);
        locationText = (EditText) getActivity().findViewById(R.id.txt_location);
        numberOfPeopleText = (EditText) getActivity().findViewById(R.id.txt_number_of_people);
        priceText = (EditText) getActivity().findViewById(R.id.txt_Price);
        timeText = (EditText) getActivity().findViewById(R.id.txt_time);
        categoryText = (EditText) getActivity().findViewById(R.id.txt_category);

        submitBtn = (Button) getActivity().findViewById(R.id.btn_submit);

        startDateText.setOnClickListener(v -> showDatePickerDialog());
        startTimeText.setOnClickListener(v -> showTimePickerDialog());

        photopreview.setOnClickListener(v -> takePhoto());

        submitBtn.setOnClickListener(v -> submitPost());
    }

    @SuppressLint("SetTextI18n")
    private void showDatePickerDialog() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog mDatePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme, (view, year, monthOfYear, dayOfMonth) -> {
            if ((monthOfYear + 1) < 10 && dayOfMonth < 10) {
                startDateText.setText(year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth);
            } else if ((monthOfYear + 1) < 10) {
                startDateText.setText(year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth);
            } else if (dayOfMonth < 10) {
                startDateText.setText(year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth);
            } else {
                startDateText.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        mDatePickerDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void showTimePickerDialog() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(getActivity(), R.style.DialogTheme, (timePicker, hour, minute) -> {
            if (hour < 10 && minute < 10) {
                startTimeText.setText("0" + hour + ":0" + minute);
            } else if (hour < 10) {
                startTimeText.setText("0" + hour + ":" + minute);
            } else if (minute < 10) {
                startTimeText.setText(hour + ":0" + minute);
            } else {
                startTimeText.setText(hour + ":" + minute);
            }
        }, c.get(Calendar.HOUR), c.get(Calendar.MINUTE), true).show();
    }

    private void takePhoto() {
        MarshmallowPermission marshmallowPermission = new MarshmallowPermission(getActivity());
        if (!marshmallowPermission.checkPermissionForReadfiles()) {
            marshmallowPermission.requestPermissionForReadfiles();
        } else {
            // Create intent for picking a photo from the gallery
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            // Bring up gallery to select a photo
            startActivityForResult(intent, MY_PERMISSIONS_REQUEST_READ_PHOTOS);
        }

    }

    private void submitPost() {
        String title = titleText.getText().toString();
        String content = contentText.getText().toString();

        String startTime = startDateText.getText().toString() + " " + startTimeText.getText().toString();

        String location = locationText.getText().toString();
        String numberOfPeople = numberOfPeopleText.getText().toString();
        String price = priceText.getText().toString();
        String time = timeText.getText().toString();
        String category = categoryText.getText().toString();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ArrayList<String> uidList = new ArrayList<>();
        uidList.add(uid);

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content) || startTime.equals(" ") || TextUtils.isEmpty(location) || TextUtils.isEmpty(numberOfPeople)
                || TextUtils.isEmpty(price) || TextUtils.isEmpty(time) || TextUtils.isEmpty(category) || filePath == null) {
            Toast.makeText(getActivity(), "Please fill in all blanks", Toast.LENGTH_SHORT).show();
        } else {
            eventModel = new EventModel(filePath, title, content, startTime, location, numberOfPeople, price, time, category, uidList);
//                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            if (eventModel != null) {

                String path = eventModel.getPicture();
                String fileName = path.substring(path.lastIndexOf("/") + 1);

                Log.e("path", path);

                File newFile = new File(path);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("files").child(fileName);
                storageReference.putFile(Uri.fromFile(newFile)).addOnCompleteListener(task -> {
//                                String newFilepath = task.toString();
                    CollectionReference files = FirebaseFirestore.getInstance().collection("files");

                    EventModel newEventModel = new EventModel(fileName, title, content, startTime, location, numberOfPeople, price, time, category, uidList);
                    files.add(newEventModel);

                    Toast.makeText(getActivity(), "Upload Firebase Success", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);

                });

            }
        }
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
                    selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);

                    // Load the selected image into a preview
                    ivPreview.setImageBitmap(selectedImage);
                    ivPreview.setVisibility(View.VISIBLE);

                    filePath = uriToPath(getActivity(), photoUri, 0);
                    File file = new File(filePath);
                    Log.e("file before", String.valueOf(file.length()));

                    File newFile = new File(filePath.substring(0, filePath.lastIndexOf("/") + 1) + "1" + filePath.substring(filePath.lastIndexOf("/") + 1));
                    compressBitmapToFile(selectedImage, newFile);
                    filePath = filePath.substring(0, filePath.lastIndexOf("/")) + "_" + filePath.substring(filePath.lastIndexOf("/") + 1);


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

    public static void compressBitmapToFile(Bitmap bmp, File file) {
        // 尺寸压缩倍数
        int ratio = 8;
        // 压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap(bmp.getWidth() / ratio, bmp.getHeight() / ratio, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, bmp.getWidth() / ratio, bmp.getHeight() / ratio);
        canvas.drawBitmap(bmp, null, rect, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        result.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            Log.e("file after", String.valueOf(file.length()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}