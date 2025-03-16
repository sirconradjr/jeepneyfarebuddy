package mmcm.shs.jeepneyfarebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class login extends AppCompatActivity {

    Button register, login, reset;
    TextInputEditText eusername, epass;
    TextInputLayout lusername, lpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ConstraintLayout layout = findViewById(R.id.loginconstraint);

        register = findViewById(R.id.registernewuser);
        login = findViewById(R.id.loginbutton);
        reset = findViewById(R.id.forgotpassword);
        ProgressBar bar = findViewById(R.id.progressBarlogin);
        eusername = findViewById(R.id.editusername);
        epass = findViewById(R.id.editpassword);
        lusername = findViewById(R.id.username);
        lpass = findViewById(R.id.password);

        register.setOnClickListener(v -> {
            startActivity(new Intent(login.this, signup.class));
        });
    }
}