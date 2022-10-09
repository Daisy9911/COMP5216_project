package comp5216.sydney.edu.au.haplanet.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import comp5216.sydney.edu.au.haplanet.AutoFitGridLayoutManager;
import comp5216.sydney.edu.au.haplanet.model.EventModel;
import comp5216.sydney.edu.au.haplanet.R;
import comp5216.sydney.edu.au.haplanet.RecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FindFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindFragment extends Fragment implements RecyclerViewAdapter.ItemListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FirebaseFirestore mFirestore;

    RecyclerView recyclerView;
    ArrayList<EventModel> arrayList;

    public FindFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindFragment newInstance(String param1, String param2) {
        FindFragment fragment = new FindFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirestore = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.recyclerview);
        arrayList = new ArrayList<>();
        CollectionReference eventsRef = mFirestore.collection("events");
        eventsRef.get().addOnCompleteListener((OnCompleteListener<QuerySnapshot>) task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else {
                task.getResult().getDocuments().stream().forEach(e -> {
                    System.out.println(e);
                    EventModel eventModel = new EventModel();
                    eventModel.setDescription(e.getString("description"));
                    eventModel.setLocation(e.getString("location"));
                    eventModel.setNumberOfPeople(Math.toIntExact(e.getLong("number_of_people")));
                    eventModel.setPicture(e.getString("picture"));
                    eventModel.setPrice(e.getDouble("price"));
                    eventModel.setStartTime(e.getDate("start_time"));
                    eventModel.setTitle(e.getString("title"));
                    arrayList.add(eventModel);
                });

                RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext(), arrayList, this);
                recyclerView.setAdapter(adapter);

                AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(getContext(), 500);
                recyclerView.setLayoutManager(layoutManager);
            }
        });


    }

    @Override
    public void onItemClick(EventModel item) {
        Toast.makeText(getContext(), item.getTitle() + " is clicked", Toast.LENGTH_SHORT).show();
    }
}