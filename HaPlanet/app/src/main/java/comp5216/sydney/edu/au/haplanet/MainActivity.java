package comp5216.sydney.edu.au.haplanet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.xuexiang.xui.XUI;

import comp5216.sydney.edu.au.haplanet.fragment.FindFragment;
import comp5216.sydney.edu.au.haplanet.fragment.HomeFragment;
import comp5216.sydney.edu.au.haplanet.fragment.MessageFragment;
import comp5216.sydney.edu.au.haplanet.fragment.PostFragment;
import comp5216.sydney.edu.au.haplanet.fragment.UserFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView nav_view;
    private HomeFragment home_fragment;
    private FindFragment find_fragment;
    private MessageFragment message_fragment;
    private UserFragment user_fragment;
    private PostFragment post_fragment;

    private FrameLayout mNavContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        home_fragment = new HomeFragment();
        find_fragment = new FindFragment();
        message_fragment = new MessageFragment();
        user_fragment = new UserFragment();
        post_fragment = new PostFragment();

        mNavContainer = findViewById(R.id.nav_container);

        nav_view = findViewById(R.id.nav_view);
        nav_view.setOnItemSelectedListener(mNavItemListener);

        switchFragment(home_fragment);
    }

    private BottomNavigationView.OnItemSelectedListener mNavItemListener
            = new BottomNavigationView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.nav_item_home:
                    switchFragment(home_fragment);
                    Toast.makeText(MainActivity.this, R.string.home, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.nav_item_find:
                    switchFragment(find_fragment);
                    Toast.makeText(MainActivity.this, R.string.find, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.nav_item_post:
                    switchFragment(post_fragment);
                    Toast.makeText(MainActivity.this,"Post", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.nav_item_message:
                    switchFragment(message_fragment);
                    Toast.makeText(MainActivity.this, R.string.message, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.nav_item_user:
                    switchFragment(user_fragment);
                    Toast.makeText(MainActivity.this, R.string.profile, Toast.LENGTH_SHORT).show();
                    break;
            }

            return true;
        }
    };

    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_container, fragment).commitNow();
    }

}