package mmcm.shs.jeepneyfarebuddy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    private ConstraintLayout layout;
    Button register, login, reset;
    TextInputEditText eusername, epass;
    TextInputLayout lusername, lpass;
    private FirebaseAuth auth;
    private boolean monitoringConnectivity = false;
    private boolean isConnected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        if(!CheckGpsStatus()){

            buildAlertMessageNoGps();
        }

        layout = findViewById(R.id.loginconstraint);
        register = findViewById(R.id.registernewuser);
        login = findViewById(R.id.loginbutton);
        reset = findViewById(R.id.forgotpassword);
        eusername = findViewById(R.id.editusername);
        epass = findViewById(R.id.editpassword);
        lusername = findViewById(R.id.username);
        lpass = findViewById(R.id.password);

        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){

            startActivity(new Intent(login.this, main.class));
            finish();
        }

        login.setOnClickListener(v -> {

            try {
                final String fuser = eusername.getText().toString().trim();
                final String fpass = epass.getText().toString().trim();

                if(TextUtils.isEmpty(fuser) && TextUtils.isEmpty(fpass)){
                    lusername.setError("A email address is required");
                    lpass.setError("A password is required");
                }
                else if(TextUtils.isEmpty(fuser)) {
                    lusername.setError("An email address is required");
                }
                else if(TextUtils.isEmpty(fpass)) {
                    lpass.setError("A password is required");
                }
                else if(!isConnected){
                    Snackbar sn = Snackbar.make(layout, "Authentication Failed, check your internet connection.", Snackbar.LENGTH_SHORT);
                    sn.show();
                }
                else {
                    auth.signInWithEmailAndPassword(fuser, fpass)
                            .addOnCompleteListener(login.this, task -> {

                                if(task.isSuccessful()){
                                    Intent intent = new Intent(login.this, main.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    Snackbar sn3 = Snackbar.make(layout, "Authentication Failed, check your credentials.", Snackbar.LENGTH_SHORT);
                                    sn3.show();
                                    eusername.setText(null);
                                    epass.setText(null);
                                }
                            });
                }
            }
            catch(Exception e){
                Log.e("On Login Button Clicked: ", e.getMessage(), e);
            }
        });

        register.setOnClickListener(v -> {
            startActivity(new Intent(login.this, signup.class));
        });

        reset.setOnClickListener(v -> {
            startActivity(new Intent(login.this, reset.class));
        });
    }

    public boolean CheckGpsStatus() {

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return GpsStatus;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, please enable it to proceed.")
                .setCancelable(false)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {

                        Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(i);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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