package mmcm.shs.jeepneyfarebuddy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class reset extends AppCompatActivity {

    TextInputLayout lemail;
    TextInputEditText email;
    Button reset;
    ProgressBar bar;
    private String emails = " ";
    private FirebaseAuth auth;
    boolean isConnected = true;
    private boolean monitoringConnectivity = false;
    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset);

        auth = FirebaseAuth.getInstance();
        layout = findViewById(R.id.resetconstraint);
        reset = findViewById(R.id.resetpassbutton);
        bar = findViewById(R.id.progressBarreset);
        lemail = findViewById(R.id.passemailreset);
        email = findViewById(R.id.editpassreset);

        reset.setOnClickListener(v -> {

            try{

                emails = email.getText().toString().trim();
                if(TextUtils.isEmpty(emails)){
                    Snackbar sn = Snackbar.make(layout, "Input your registered email.", Snackbar.LENGTH_SHORT);
                    sn.show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(reset.this);
                    builder.setTitle("Please confirm action!");
                    builder.setMessage("Are you sure you want to reset your password?");
                    builder.setIcon(R.drawable.jeepneyfarebuddyicon);
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        bar.setVisibility(View.GONE);
                        auth.sendPasswordResetEmail(emails)
                                .addOnCompleteListener(task -> {

                                    if(task.isSuccessful()){

                                        Snackbar sn2 = Snackbar.make(layout, "Password Reset Instructions has been set to your email address.", Snackbar.LENGTH_SHORT);
                                        sn2.show();
                                        bar.setVisibility(View.GONE);
                                        Intent intent = new Intent(reset.this, login.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        Snackbar sn3 = Snackbar.make(layout, "Failed to send Password Reset Instructions to your email address.", Snackbar.LENGTH_SHORT);
                                        sn3.show();
                                        email.setText(null);
                                        bar.setVisibility(View.GONE);
                                    }
                                });
                    });
                    builder.setNegativeButton("No", (dialog, which) -> {

                        dialog.dismiss();
                        Snackbar sn1 = Snackbar.make(layout, "Cancelled.", Snackbar.LENGTH_SHORT);
                        sn1.show();
                        email.setText(null);
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
            catch(Exception e){
                Log.e("On Reset Password BUtton Clicked: ", e.getMessage(), e);
            }
        });
    }
    @Override
    protected void onResume() {

        super.onResume();
        checkConnectivity();
    }

    @Override
    protected void onPause() {
        if (monitoringConnectivity) {

            final ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(connectivityCallback);
            monitoringConnectivity = false;
        }
        super.onPause();
    }

    private final ConnectivityManager.NetworkCallback connectivityCallback
            = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            isConnected = true;
            Snackbar snonavail = Snackbar.make(layout, "Connection Established!", Snackbar.LENGTH_SHORT);
            snonavail.show();
        }
        @Override
        public void onLost(Network network) {
            isConnected = false;
            Snackbar snonlost = Snackbar.make(layout, "Connection Lost!", Snackbar.LENGTH_INDEFINITE);
            snonlost.show();
        }
    };

    private void checkConnectivity() {

        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        if (!isConnected) {
            Snackbar sncc = Snackbar.make(layout, "No Connection!", Snackbar.LENGTH_INDEFINITE);
            sncc.show();
            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .build(), connectivityCallback);
            monitoringConnectivity = true;
        }
        else {
            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .build(), connectivityCallback);
            monitoringConnectivity = true;
        }
    }
}