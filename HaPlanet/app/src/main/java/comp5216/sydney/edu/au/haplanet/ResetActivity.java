package comp5216.sydney.edu.au.haplanet;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetActivity extends AppCompatActivity {

    TextView txt_email;
    EditText et_password, et_confirm_password;
    Button btn_reset;
    String email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        txt_email = findViewById(R.id.txt_email);
        et_password = findViewById(R.id.et_password);
        et_confirm_password = findViewById(R.id.et_confirm_password);
        btn_reset = findViewById(R.id.btn_reset);

        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        txt_email.setText(email);

        btn_reset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

    }

    private void resetPassword() {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "Email sent.");
                        }
                    }
                });

    }

}
