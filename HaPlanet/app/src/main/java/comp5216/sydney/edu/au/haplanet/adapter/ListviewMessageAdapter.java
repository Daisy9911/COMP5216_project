package comp5216.sydney.edu.au.haplanet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import comp5216.sydney.edu.au.haplanet.R;
import comp5216.sydney.edu.au.haplanet.model.MessageModel;

public class ListviewMessageAdapter extends ArrayAdapter<MessageModel> {

    private Context mContext;

    TextView txtReply, txtmReply;
    LinearLayout leftLayout, rightLayout;
    String messageTitle;

    FirebaseFirestore db;

    public ListviewMessageAdapter(@NonNull Context context, ArrayList<MessageModel> messageModelArrayList) {
        super(context, 0, messageModelArrayList);
        this.mContext = context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            listitemView = inflater.inflate(R.layout.lv_message_item, parent, false);

        }

        MessageModel messageModel = getItem(position);
        messageTitle = messageModel.getTitle();

        txtReply = listitemView.findViewById(R.id.gv_txt_reply);
        txtmReply = listitemView.findViewById(R.id.gv_txt_mreply);

        leftLayout = listitemView.findViewById(R.id.left_layout);
        rightLayout = listitemView.findViewById(R.id.right_layout);

//        txtTime = listitemView.findViewById(R.id.gv_txt_time);
//        txtmTime = listitemView.findViewById(R.id.gv_txt_mtime);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String date = String.valueOf(messageModel.getDate());
        date = date.substring(0, date.indexOf("GMT"));
        if (uid.equals(messageModel.getUid())) {
            leftLayout.setVisibility(View.GONE);
            rightLayout.setVisibility(View.VISIBLE);
//            txtReply.setVisibility(View.GONE);
            txtmReply.setText(messageModel.getReply());
//            txtmTime.setText(date);
        } else {
            txtReply.setText(messageModel.getReply());
            rightLayout.setVisibility(View.GONE);
            leftLayout.setVisibility(View.VISIBLE);
//            txtmReply.setVisibility(View.GONE);
//            txtTime.setText(date);
        }

        return listitemView;
    }

}
