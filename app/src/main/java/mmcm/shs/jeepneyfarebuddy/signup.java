package mmcm.shs.jeepneyfarebuddy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class signup extends AppCompatActivity {

    private ConstraintLayout layout;
    private TextInputEditText editFirstName, editLastName, dateOfBirth, editEmail, editPassword, editcPassword, editContact;
    private Button registerButton;
    private TextView loginRedirect;
    private FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private boolean monitoringConnectivity = false;
    private boolean isConnected = true;
    int age = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        auth = FirebaseAuth.getInstance();
        layout = findViewById(R.id.registerconstraint);
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        dateOfBirth = findViewById(R.id.dateofbirth);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editcPassword = findViewById(R.id.editcPassword);
        editContact = findViewById(R.id.editContact);
        registerButton = findViewById(R.id.registerButton);
        loginRedirect = findViewById(R.id.loginRedirect);

        FirebaseApp.initializeApp(signup.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        MaterialDatePicker.Builder<Long> materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("Select your birthdate");
        final MaterialDatePicker<Long> materialDatePicker = materialDateBuilder.build();

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            @Override
            public void onPositiveButtonClick(Long selection) {
                LocalDate birthDate = Instant.ofEpochMilli(selection)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
                String formattedDate = birthDate.format(formatter);

                dateOfBirth.setText(formattedDate);
                age = calculateAge(birthDate);
            }
        });

        dateOfBirth.setOnClickListener(v -> materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER"));

        registerButton.setOnClickListener(v ->{

            String fname = editFirstName.getText().toString().trim();
            String lname = editLastName.getText().toString().trim();
            String dob = dateOfBirth.getText().toString().trim();
            String mail = editEmail.getText().toString().trim();
            String pass = editPassword.getText().toString().trim();
            String cpass = editcPassword.getText().toString().trim();
            String contact = editContact.getText().toString().trim();

            if (TextUtils.isEmpty(fname) && TextUtils.isEmpty(lname) && TextUtils.isEmpty(dob) && TextUtils.isEmpty(mail)
                    && TextUtils.isEmpty(pass) && TextUtils.isEmpty(cpass) && TextUtils.isEmpty(contact)) {

                editFirstName.setError("First Name is required");
                editLastName.setError("Last Name is required");
                dateOfBirth.setError("Date of Birth is required");
                editEmail.setError("Email Address is required");
                editPassword.setError("Password is required");
                editcPassword.setError("Confirm Password is required");
                editContact.setError("Contact Number is required");

            } else if (TextUtils.isEmpty(fname)) {
                editFirstName.setError("First Name is required");

            } else if (TextUtils.isEmpty(lname)) {
                editLastName.setError("Last Name is required");

            } else if (TextUtils.isEmpty(dob)) {
                dateOfBirth.setError("Date of Birth is required");

            } else if (TextUtils.isEmpty(mail)) {
                editEmail.setError("Email Address is required");

            } else if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                editEmail.setError("Please enter a valid email address");

            } else if (TextUtils.isEmpty(contact)) {
                editContact.setError("Contact Number is required");

            } else if (TextUtils.isEmpty(pass)) {
                editPassword.setError("Password is required");

            } else if (pass.length() < 8) {
                editPassword.setError("Password is too short, must be at least 8 characters");

            } else if (TextUtils.isEmpty(cpass)) {
                editcPassword.setError("Please confirm your password");

            } else if (!pass.equals(cpass)) {
                editPassword.setError("Passwords do not match!");
                editcPassword.setError("Passwords do not match!");
                editPassword.setText(null);
                editcPassword.setText(null);
            }
            else{

                AlertDialog.Builder builder = new AlertDialog.Builder(signup.this);
                View root = getLayoutInflater().inflate((R.layout.dialogconfirmsignup), null);
                TextView names = root.findViewById(R.id.confirmcompletename);
                TextView dobs = root.findViewById(R.id.confirmdateofbirth);
                TextView numbers = root.findViewById(R.id.confirmcontact);
                TextView emails = root.findViewById(R.id.confirmemail);
                names.setText(fname+" "+lname);
                dobs.setText(dob);
                numbers.setText(contact);
                emails.setText(mail);
                builder.setView(root);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        auth.createUserWithEmailAndPassword(mail, pass)
                                .addOnCompleteListener(signup.this, task -> {

                                    if (!task.isSuccessful()) {

                                        Log.w("Registration", "signInWithCredential", task.getException());
                                        Snackbar sn = Snackbar.make(layout, "Firebase Signup Failed.", Snackbar.LENGTH_SHORT);
                                        sn.show();

                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                                            Snackbar sn1 = Snackbar.make(layout, "Account already exists", Snackbar.LENGTH_SHORT);
                                            sn1.show();
                                            editFirstName.setText(null);
                                            editLastName.setText(null);
                                            dateOfBirth.setText(null);
                                            editContact.setText(null);
                                            editEmail.setText(null);
                                            editPassword.setText(null);
                                            editcPassword.setText(null);

                                        } else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {

                                            Snackbar sn2 = Snackbar.make(layout, "Please input a stronger password", Snackbar.LENGTH_SHORT);
                                            sn2.show();
                                            editPassword.setText(null);
                                            editcPassword.setText(null);

                                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                            Snackbar sn3 = Snackbar.make(layout, "Please input a valid email address", Snackbar.LENGTH_SHORT);
                                            sn3.show();
                                            editEmail.setText(null);
                                            editPassword.setText(null);
                                            editcPassword.setText(null);

                                        }

                                    } else if (task.isSuccessful()) {

                                        String fullName = fname + " " + lname;
                                        createNewUser(fullName, dob, mail, contact);
                                        auth.signOut();
                                        Snackbar sn4 = Snackbar.make(layout, "Registration Successful", Snackbar.LENGTH_SHORT);
                                        sn4.show();
                                        startActivity(new Intent(signup.this, login.class));
                                        finish();

                                    }

                                });
                    }
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> {

                    dialog.dismiss();
                    editFirstName.setText(null);
                    editLastName.setText(null);
                    dateOfBirth.setText(null);
                    editContact.setText(null);
                    editEmail.setText(null);
                    editPassword.setText(null);
                    editcPassword.setText(null);

                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        loginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(signup.this, login.class));
        });
    }

    private void createNewUser(String name, String date, String mail, String contact) {

        User u = new User();
        u.setFullname(name);
        u.setdob(date);
        u.setEmailaddress(mail);
        u.setContactnumber(contact);
        u.setAge(age);

        String[] parts = mail.split("@");
        databaseReference.child("Users").child(parts[0]).setValue(u);
    }

    private int calculateAge(LocalDate birthDate) {
        LocalDate currentDate = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Period.between(birthDate, currentDate).getYears();
        }
        return 0;
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
