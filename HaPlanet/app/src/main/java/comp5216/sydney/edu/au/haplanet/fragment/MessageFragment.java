package comp5216.sydney.edu.au.haplanet.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.haplanet.R;
import comp5216.sydney.edu.au.haplanet.adapter.ListviewChatAdapter;
import comp5216.sydney.edu.au.haplanet.model.EventModel;

public class MessageFragment extends Fragment {

    ListView mListView;
    ArrayList<EventModel> eventModelArrayList;
    FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        eventModelArrayList = new ArrayList<>();
        mListView = getActivity().findViewById(R.id.idLVMessages);

        db = FirebaseFirestore.getInstance();

        db.collection("files").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            eventModelArrayList.clear();
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                EventModel eventModel = d.toObject(EventModel.class);
                                eventModelArrayList.add(eventModel);
                            }
                            ListviewChatAdapter adapter = new ListviewChatAdapter(getActivity(), eventModelArrayList);
                            mListView.setAdapter(adapter);
                        } else {
                            Toast.makeText(getActivity(), "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Fail to load data...", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}