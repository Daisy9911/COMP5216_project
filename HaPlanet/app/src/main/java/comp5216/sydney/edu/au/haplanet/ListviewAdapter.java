package comp5216.sydney.edu.au.haplanet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import comp5216.sydney.edu.au.haplanet.model.EventModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ListviewAdapter extends ArrayAdapter<EventModel> {

    private Context mContext;

    // constructor for our list view adapter.
    public ListviewAdapter(@NonNull Context context, ArrayList<EventModel> dataModalArrayList) {
        super(context, 0, dataModalArrayList);

        this.mContext = context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // below line is use to inflate the
        // layout for our item of list view.
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.gv_item, parent, false);
        }

        // after inflating an item of listview item
        // we are getting data from array list inside
        // our modal class.
        EventModel eventModel = getItem(position);

        // initializing our UI components of list view item.
        ImageView ivImage = listitemView.findViewById(R.id.iv_image);
        TextView txtTitle = listitemView.findViewById(R.id.gv_txt_title);
        TextView txtNumber = listitemView.findViewById(R.id.gv_txt_number_of_people);

        // after initializing our items we are
        // setting data to our view.
        // below line is use to set data to our text view.
        txtTitle.setText(eventModel.getTitle());

//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ArrayList<String> uidList = eventModel.getUidList();
        int number = uidList.size();

        txtNumber.setText("The number of participants: " + number);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://haplanet-83dba.appspot.com").child("files").child(eventModel.getPicture());

        try {
            final File localFile = File.createTempFile("images", "jpg");
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
        } catch (IOException e ) {
            e.printStackTrace();
        }

        // in below line we are using Picasso to load image
        // from URL in our Image VIew.
//        Picasso.get().load(storageRef).into(ivImage);

        // below line is use to add item
        // click listener for our item of list view.
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on the item click on our list view.
                // we are displaying a toast message.

                Intent intent = new Intent(mContext, AddInActivity.class);
                intent.putExtra("eventModel",eventModel);
                mContext.startActivity(intent);

                Toast.makeText(getContext(), "Item clicked is : " + eventModel.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        return listitemView;
    }
}