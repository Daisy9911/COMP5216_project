package comp5216.sydney.edu.au.haplanet.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.haplanet.adapter.ListviewEventAdapter;
import comp5216.sydney.edu.au.haplanet.R;
import comp5216.sydney.edu.au.haplanet.model.EventModel;

public class HomeFragment extends Fragment {

    ListView mListView;
    ArrayList<EventModel> eventModelArrayList;
    FirebaseFirestore db;
    private TabLayout tabLayout;
    private List<String> tabList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        eventModelArrayList = new ArrayList<>();
        mListView = getActivity().findViewById(R.id.idLVEvents);
        tabLayout = (TabLayout) getActivity().findViewById(R.id.tayLayout);

        db = FirebaseFirestore.getInstance();

        db.collection("files").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            tabLayout.addTab(tabLayout.newTab().setText("All"));
                            for (DocumentSnapshot d : list) {

                                EventModel eventModel = d.toObject(EventModel.class);
                                eventModelArrayList.add(eventModel);

                                if (!tabList.contains(eventModel.getCategory())) {
                                    tabList.add(eventModel.getCategory());
                                    tabLayout.addTab(tabLayout.newTab().setText(eventModel.getCategory()));
//                                    Log.e("Tab category", eventModel.getCategory());
                                }
                            }
                            tabList.clear();
                            ListviewEventAdapter adapter = new ListviewEventAdapter(getActivity(), eventModelArrayList);
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

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e("tab", String.valueOf(tab.getText()));
                //深拷贝
                ArrayList<EventModel> newEventModelArrayList = new ArrayList<>();
                for (EventModel eventModel : eventModelArrayList) {
                    newEventModelArrayList.add(eventModel.clone());
                }
                if ((String) tab.getText() != "All") {
                    newEventModelArrayList.removeIf(e -> e.getCategory().equals(String.valueOf(tab.getText())));
                    ListviewEventAdapter adapter = new ListviewEventAdapter(getActivity(), newEventModelArrayList);
                    mListView.setAdapter(adapter);
                } else {
                    ListviewEventAdapter adapter = new ListviewEventAdapter(getActivity(), newEventModelArrayList);
                    mListView.setAdapter(adapter);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }


        });

    }
}