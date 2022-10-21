package comp5216.sydney.edu.au.haplanet.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import comp5216.sydney.edu.au.haplanet.R;
import comp5216.sydney.edu.au.haplanet.adapter.ListviewEventAdapter;
import comp5216.sydney.edu.au.haplanet.model.EventModel;
import comp5216.sydney.edu.au.haplanet.model.UserModel;

public class UserFragment extends Fragment {

    ArrayList<EventModel> eventModelArrayList;
    UserModel userModel;
    FirebaseFirestore db;

    ListView mListView;
    ImageView imageProfile, imageEdit;
    TextView txtUsername, txtIntroduction;
    private TabLayout tabLayout;

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db = FirebaseFirestore.getInstance();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        eventModelArrayList = new ArrayList<>();
        mListView = getActivity().findViewById(R.id.idLVUserEvents);
        imageProfile = getActivity().findViewById(R.id.iv_profile_image);
//        imageEdit = getActivity().findViewById(R.id.iv_edit_profile);
        txtUsername = getActivity().findViewById(R.id.txt_username);
        txtIntroduction = getActivity().findViewById(R.id.txt_intruduction);

        tabLayout = (TabLayout) getActivity().findViewById(R.id.userTabLayout);

        tabLayout.addTab(tabLayout.newTab().setText("My Post"));
        tabLayout.addTab(tabLayout.newTab().setText("Joined"));

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

                            //深拷贝
                            ArrayList<EventModel> newEventModelArrayList = new ArrayList<>();
                            for (EventModel eventModel : eventModelArrayList) {
                                newEventModelArrayList.add(eventModel.clone());
                            }
                            newEventModelArrayList = (ArrayList<EventModel>) newEventModelArrayList.stream()
                                    .filter(e -> e.getUidList().get(0).equals(uid)).collect(Collectors.toList());
                            Log.e("Count", "2");
                            ListviewEventAdapter adapter = new ListviewEventAdapter(getActivity(), newEventModelArrayList);
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

        db.collection("users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                UserModel newProfileModel = d.toObject(UserModel.class);

                                if (Objects.equals(newProfileModel.getUid(), uid)) {
                                    userModel = newProfileModel;
                                }
                            }

                            if (userModel != null) {

                                txtUsername.setText(userModel.getUsername());
                                txtIntroduction.setText(userModel.getIntroduction());

                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReferenceFromUrl("gs://haplanet-83dba.appspot.com")
                                        .child("users").child(userModel.getAvatarUrl());

                                try {
                                    File localFile = File.createTempFile("images", ".jpg");
                                    storageRef.getFile(localFile)
                                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

//                                            Glide.with(getContext())
//                                                    .load(localFile.getAbsolutePath())
//                                                    //transition(TransitionOptions transitionOptions)
//                                                    .transition(DrawableTransitionOptions.withCrossFade())
//                                                    .into(imageProfile);

                                                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                                    imageProfile.setImageBitmap(bitmap);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                }
                                            });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

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
                if (tab.getText() == "My Post") {
                    newEventModelArrayList = (ArrayList<EventModel>) newEventModelArrayList.stream()
                            .filter(e -> e.getUidList().get(0).equals(uid)).collect(Collectors.toList());
//                    newEventModelArrayList.removeIf(e -> e.getUidList().contains(uid));
                    Log.e("Count", "2");
                    ListviewEventAdapter adapter = new ListviewEventAdapter(getActivity(), newEventModelArrayList);
                    mListView.setAdapter(adapter);
                } else {
                    newEventModelArrayList = (ArrayList<EventModel>) newEventModelArrayList.stream()
                            .filter(e -> e.getUidList().contains(uid)).collect(Collectors.toList());
                    Log.e("Count", "2");
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
