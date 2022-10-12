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

import comp5216.sydney.edu.au.haplanet.ListviewAdapter;
import comp5216.sydney.edu.au.haplanet.R;
import comp5216.sydney.edu.au.haplanet.model.EventModel;

public class HomeFragment extends Fragment {

    // creating a variable for our
    // grid view, arraylist and
    // firebase Firestore.
    ListView mGridView;
    ArrayList<EventModel> eventModelArrayList;
    FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        // below line is use to initialize our variables.
//        mGridView = getActivity().findViewById(R.id.idGVCourses);
//        eventModelArrayList = new ArrayList<>();
//
//        // initializing our variable for firebase
//        // firestore and getting its instance.
//        db = FirebaseFirestore.getInstance();
//
//        // here we are calling a method
//        // to load data in our list view.
//        loadDatainGridView();

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

        // initializing our variable for firebase
        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();

        // below line is use to initialize our variables.
        mGridView = getActivity().findViewById(R.id.idGVCourses);
        eventModelArrayList = new ArrayList<>();

        // initializing our variable for firebase
        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();

        // below line is use to get data from Firebase
        // firestore using collection in android.
        db.collection("files").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // after getting the data we are calling on success method
                        // and inside this method we are checking if the received
                        // query snapshot is empty or not.
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // if the snapshot is not empty we are hiding our
                            // progress bar and adding our data in a list.
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {

                                // after getting this list we are passing
                                // that list to our object class.
                                EventModel eventModel = d.toObject(EventModel.class);

                                // after getting data from Firebase
                                // we are storing that data in our array list
                                eventModelArrayList.add(eventModel);
                            }
                            // after that we are passing our array list to our adapter class.
                            ListviewAdapter adapter = new ListviewAdapter(getActivity(), eventModelArrayList);

                            // after passing this array list
                            // to our adapter class we are setting
                            // our adapter to our list view.
                            mGridView.setAdapter(adapter);
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            Toast.makeText(getActivity(), "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // we are displaying a toast message
                        // when we get any error from Firebase.
                        Toast.makeText(getActivity(), "Fail to load data..", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}