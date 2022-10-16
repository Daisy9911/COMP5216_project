package comp5216.sydney.edu.au.haplanet.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.xuexiang.xui.adapter.simple.ViewHolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import comp5216.sydney.edu.au.haplanet.R;
import comp5216.sydney.edu.au.haplanet.MessageActivity;
import comp5216.sydney.edu.au.haplanet.model.EventModel;

public class ListviewChatAdapter extends ArrayAdapter<EventModel> {

    private Context mContext;

    ImageView ivImage;
    TextView txtTitle, txtNumber, txtStartTime;

    public ListviewChatAdapter(@NonNull Context context, ArrayList<EventModel> dataModalArrayList) {
        super(context, 0, dataModalArrayList);
        this.mContext = context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = null;
        View listitemView = convertView;
        if (listitemView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            listitemView = inflater.inflate(R.layout.lv_chat_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mIvIcon = listitemView.findViewById(R.id.iv_image);
            listitemView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) listitemView.getTag();
        }

//        View listitemView = convertView;
//        if (listitemView == null) {
//            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.lv_item, parent, false);
//        }

        EventModel eventModel = getItem(position);

        ivImage = listitemView.findViewById(R.id.iv_image);
        txtTitle = listitemView.findViewById(R.id.gv_txt_title);
        txtNumber = listitemView.findViewById(R.id.gv_txt_number_of_people);
        txtStartTime = listitemView.findViewById(R.id.lv_txt_start_time);

        txtTitle.setText(eventModel.getTitle());
        txtStartTime.setText(eventModel.getStartTime());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://haplanet-83dba.appspot.com")
                .child("files").child(eventModel.getPicture());

        try {
            File localFile = File.createTempFile("images", "jpg");
            ViewHolder newViewHolder = viewHolder;
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    newViewHolder.mIvIcon.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ArrayList<String> uidList = eventModel.getUidList();
        int number = uidList.size();

        if (number != Integer.parseInt(eventModel.getNumberOfPeople())) {
            txtNumber.setText(number + "/" + eventModel.getNumberOfPeople() + " Waiting...");
        } else {
            txtNumber.setText("Full");
        }

        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("eventModel", eventModel);
                Log.e("Title", eventModel.getTitle());

                mContext.startActivity(intent);

                Toast.makeText(getContext(), "Item clicked is : " + eventModel.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        return listitemView;
    }

}
