package mmcm.shs.jeepneyfarebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class signup extends AppCompatActivity {

    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        login = findViewById(R.id.loginRedirect);

        login.setOnClickListener(v -> {
            startActivity(new Intent(signup.this, login.class));
        });
    }
}
