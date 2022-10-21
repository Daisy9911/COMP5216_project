package comp5216.sydney.edu.au.haplanet;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import comp5216.sydney.edu.au.haplanet.databinding.ActivityMainBinding;
import comp5216.sydney.edu.au.haplanet.fragment.FindFragment;
import comp5216.sydney.edu.au.haplanet.fragment.HomeFragment;
import comp5216.sydney.edu.au.haplanet.fragment.MessageFragment;
import comp5216.sydney.edu.au.haplanet.fragment.PostFragment;
import comp5216.sydney.edu.au.haplanet.fragment.UserFragment;
import comp5216.sydney.edu.au.haplanet.model.UserModel;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    UserModel userModel;
    FirebaseFirestore db;

    ImageView imageView;
    TextView txt_username;
    TextView txt_introduction;

    private BottomNavigationView nav_bottom_view;
    private HomeFragment home_fragment;
    private FindFragment find_fragment;
    private MessageFragment message_fragment;
    private UserFragment user_fragment;
    private PostFragment post_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FindActivity.class));
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bottom_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_post, R.id.nav_message, R.id.nav_user,
                R.id.nav_item_home, R.id.nav_item_post, R.id.nav_item_message, R.id.nav_item_user)
                .setOpenableLayout(drawer)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        //设置BottomNavigation
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_item_home, R.id.nav_item_post, R.id.nav_item_message, R.id.nav_item_user)
//                .build();

        //设置navigation监听
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                boolean handled = NavigationUI.onNavDestinationSelected(menuItem, navController);
                if (!handled) {

                    CharSequence title = menuItem.getTitle();
                    if ("Logout".equals(title)) {
                        setBtnLogout();
                    } else if ("Reset password".equals(title)) {
                        setBtnReset();
                    }
                }
//                Toast.makeText(getApplicationContext(), menuItem.getTitle(), Toast.LENGTH_LONG).show();
//                menuItem.setChecked(true);
                drawer.closeDrawers();
                return handled;
            }
        });

        //设置bottom_nav操作
        home_fragment = new HomeFragment();
        find_fragment = new FindFragment();
        message_fragment = new MessageFragment();
        user_fragment = new UserFragment();
        post_fragment = new PostFragment();

//        nav_bottom_view =  findViewById(R.id.nav_bottom_view);
//        nav_bottom_view.setOnItemSelectedListener(mNavItemListener);

        //nav_header_main的操作
        View drawerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        imageView = drawerView.findViewById(R.id.imageView);
        txt_username = drawerView.findViewById(R.id.txt_username);
        txt_introduction = drawerView.findViewById(R.id.txt_intruduction);

        loadData();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBtnSetting();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setBtnLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign out")
                .setMessage("Are you sure to sign out?")
                .setPositiveButton("Yes", (dialogInterface, i) -> logout())
                .setNegativeButton("No", (dialogInterface, i) -> {

                });
        builder.create().show();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void setBtnSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit profile")
                .setMessage("Are you sure to edit your profile?")
                .setPositiveButton("Yes", (dialogInterface, i) -> editProfile())
                .setNegativeButton("No", (dialogInterface, i) -> {
                });
        builder.create().show();
    }

    private void editProfile() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra("uid", uid);

        startActivity(intent);
    }

    private void setBtnReset() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset password")
                .setMessage("You will receive an email with instructions change your password")
                .setPositiveButton("Yes", (dialogInterface, i) -> resetPassword())
                .setNegativeButton("No", (dialogInterface, i) -> {
                });
        builder.create().show();
    }

    private void resetPassword() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Email has been sent to your mailbox", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void loadData() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db = FirebaseFirestore.getInstance();

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

                                txt_username.setText(userModel.getUsername());
                                if (!Objects.equals(userModel.getIntroduction(), "")) {
                                    txt_introduction.setText(userModel.getIntroduction());
                                }

                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReferenceFromUrl("gs://haplanet-83dba.appspot.com")
                                        .child("users").child(userModel.getAvatarUrl());

                                try {
                                    File localFile = File.createTempFile("images", ".jpg");
                                    storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                            Glide.with(MainActivity.this)
                                                    .load(localFile.getAbsolutePath())
                                                    //transition(TransitionOptions transitionOptions)
                                                    .transition(DrawableTransitionOptions.withCrossFade())
                                                    .into(imageView);

//                                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                                            imageProfile.setImageBitmap(bitmap);
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
                            Toast.makeText(MainActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Fail to load data...", Toast.LENGTH_SHORT).show();
                    }
                });

    }

}