package comp5216.sydney.edu.au.haplanet;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.haplanet.adapter.ListviewEventAdapter;
import comp5216.sydney.edu.au.haplanet.model.EventModel;

public class FindActivity extends AppCompatActivity {

    private SearchView mSearchView;
    private ListView mListView;
    ArrayList<EventModel> eventModelArrayList;
    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        mSearchView = (SearchView) findViewById(R.id.search_view);

        mListView = findViewById(R.id.idLVFindEvents);
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

                                    if (eventModelArrayList.isEmpty()) {
                                        Toast.makeText(FindActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                                    }

                                    ListviewEventAdapter adapter = new ListviewEventAdapter(FindActivity.this, eventModelArrayList);
                                    mListView.setAdapter(adapter);
                                } else {
                                    Toast.makeText(FindActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(FindActivity.this, "Fail to load data...", Toast.LENGTH_SHORT).show();
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
}
