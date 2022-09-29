package comp5216.sydney.edu.au.haplanet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.xuexiang.xui.XUI;

import comp5216.sydney.edu.au.haplanet.model.UserModel;

public class RegisterActivity extends AppCompatActivity {

    EditText et_email, et_password;
    Button btn_register;
    TextView tv_login;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_register = findViewById(R.id.btn_register);
        tv_login = findViewById(R.id.tv_login);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });

        tv_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }

    private void signup() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(et_email.getText().toString(),et_password.getText()
                .toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    UserModel userModel = new UserModel(uid, et_email.getText().toString());

                    FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel);

                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(RegisterActivity.this, "error", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}
