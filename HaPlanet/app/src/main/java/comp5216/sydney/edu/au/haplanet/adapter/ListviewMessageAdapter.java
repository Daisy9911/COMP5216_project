package comp5216.sydney.edu.au.haplanet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (uid.equals(messageModel.getUid())) {
            txtReply.setVisibility(View.GONE);
            txtmReply.setText(messageModel.getReply());
        } else {
            txtReply.setText(messageModel.getReply());
            txtmReply.setVisibility(View.GONE);
        }

        return listitemView;
    }

}
