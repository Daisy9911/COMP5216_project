package comp5216.sydney.edu.au.haplanet.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import comp5216.sydney.edu.au.haplanet.AddInActivity;
import comp5216.sydney.edu.au.haplanet.R;
import comp5216.sydney.edu.au.haplanet.adapter.ListviewAdapter;
import comp5216.sydney.edu.au.haplanet.model.EventModel;

public class FindFragment extends Fragment {

    private String[] mStrs = {};
    private SearchView mSearchView;
    private ListView mListView;
    ArrayList<EventModel> eventModelArrayList;
    FirebaseFirestore db;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search, menu);

        // 获取SearchView
        MenuItem item = menu.findItem(R.id.search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(item);

        db = FirebaseFirestore.getInstance();

        mListView = getActivity().findViewById(R.id.idLVEvents);
        eventModelArrayList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // 提交文本时调用
                db.collection("files").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    eventModelArrayList.clear();
                                    for (DocumentSnapshot d : list) {
                                        EventModel eventModel = d.toObject(EventModel.class);
                                        if (eventModel.getTitle().contains(query)) {
                                            eventModelArrayList.add(eventModel);
                                        }
                                    }

                                    if(eventModelArrayList.isEmpty()) {
                                        Toast.makeText(getActivity(), "No data found in Database", Toast.LENGTH_SHORT).show();
                                    }

                                    ListviewAdapter adapter = new ListviewAdapter(getActivity(), eventModelArrayList);
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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find, container, false);
    }

}