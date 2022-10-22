package comp5216.sydney.edu.au.haplanet.adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import comp5216.sydney.edu.au.haplanet.R;
import comp5216.sydney.edu.au.haplanet.MessageActivity;
import comp5216.sydney.edu.au.haplanet.model.EventModel;
import comp5216.sydney.edu.au.haplanet.model.MessageModel;
import comp5216.sydney.edu.au.haplanet.model.ViewHolder;


public class ListviewChatAdapter extends ArrayAdapter<EventModel> {

    private Context mContext;

    AVLoadingIndicatorView avi;
    ImageView ivImage;
    TextView txtTitle, txtNumber, txtMessageTime, txtMessage;

    ArrayList<MessageModel> messageModelArrayList;

    FirebaseDatabase database;

    public ListviewChatAdapter(@NonNull Context context, ArrayList<EventModel> dataModalArrayList) {
        super(context, 0, dataModalArrayList);
        this.mContext = context;
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext()).build();
        ImageLoader.getInstance().init(config);
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
            viewHolder.ivImage = listitemView.findViewById(R.id.iv_image);
            viewHolder.txtMessage = listitemView.findViewById(R.id.gv_txt_message);
            viewHolder.txtMessageTime = listitemView.findViewById(R.id.lv_txt_message_time);
            viewHolder.avi = listitemView.findViewById(R.id.avi);
            listitemView.setTag(viewHolder);
            viewHolder.ivImage.setTag(position);
            viewHolder.avi.setTag(position);
        } else {
            viewHolder = (ViewHolder) listitemView.getTag();
        }

//        View listitemView = convertView;
//        if (listitemView == null) {
//            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.lv_item, parent, false);
//        }

        EventModel eventModel = getItem(position);
        messageModelArrayList = new ArrayList<MessageModel>();

        ivImage = listitemView.findViewById(R.id.iv_image);
        txtTitle = listitemView.findViewById(R.id.gv_txt_title);
        txtNumber = listitemView.findViewById(R.id.gv_txt_number_of_people);
        txtMessageTime = listitemView.findViewById(R.id.lv_txt_message_time);
        txtMessage = listitemView.findViewById(R.id.gv_txt_message);

        txtTitle.setText(eventModel.getTitle());
        txtMessageTime.setText(eventModel.getStartTime());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://haplanet-83dba.appspot.com")
                .child("files").child(eventModel.getPicture());

        try {
            File localFile = File.createTempFile("images", "jpg");
            ViewHolder newViewHolder = viewHolder;
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                    newViewHolder.ivImage.setImageBitmap(bitmap);
                    if(newViewHolder.ivImage.getTag() != null && newViewHolder.ivImage.getTag().equals(position)){
                        ImageLoader.getInstance().displayImage("file://"+localFile.getAbsolutePath(), newViewHolder.ivImage);
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

//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ArrayList<String> uidList = eventModel.getUidList();
        int number = uidList.size();

        txtNumber.setText("(" + number + " people" + ")");

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        ViewHolder newViewHolder = viewHolder;
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String newReply = "";
                Date newDate = new Date(System.currentTimeMillis());
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    MessageModel messageModel = ds.getValue(MessageModel.class);
                    if (Objects.equals(messageModel.getTitle(), eventModel.getTitle())) {
                        newReply = messageModel.getReply();
                        newDate = messageModel.getDate();
                    }
                }
                if(newReply.length() > 20){
                    newViewHolder.txtMessage.setText(newReply.substring(0, 20));
                } else {
                    newViewHolder.txtMessage.setText(newReply);
                }

                String date = String.valueOf(newDate);
                date = date.substring(0, date.indexOf("GMT"));

                newViewHolder.txtMessageTime.setText(date);
//                txtMessageTime.setText(String.valueOf(newDate));
//                DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
//                String formattedDate = formatter.format(messageModelArrayList.get(0).getDate());
//                txtMessageTime.setText(formattedDate);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });

        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("eventModel", eventModel);
                Log.e("Title", eventModel.getTitle());

                mContext.startActivity(intent);

//                Toast.makeText(getContext(), "Item clicked is : " + eventModel.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        return listitemView;
    }

    public String dateToString(Date date) {
        SimpleDateFormat sformat = new SimpleDateFormat("yyyy-MM-dd");//日期格式

        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制
        // SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//12小时制

        String s = sformat.format(date);

        return s;
    }

}
