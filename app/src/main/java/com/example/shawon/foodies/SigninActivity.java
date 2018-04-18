package com.example.shawon.foodies;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.ganfra.materialspinner.MaterialSpinner;
import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class SigninActivity extends AppCompatActivity {

    private MaterialEditText mPhoneNumber,mPassword;
    private FButton mSigninButton;
    private RelativeLayout relativeLayout;

    private MaterialSpinner materialSpinner;
    private List<String> listItem = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    private CheckBox checkBox;
    private MaterialEditText mConfirmationCode;
    private FButton mSendCodeButton;
    
    String isType;

    private DatabaseReference mDatabaseUsers;
    private ProgressDialog mProgess;

    private TextView mForgotPassword;
    private String mRandomStringCode;

    CountryCodePicker ccp;
    MaterialEditText mFWDphone_number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        listItem.add("Admin");
        listItem.add("Customer");

        materialSpinner = (MaterialSpinner) findViewById(R.id.spinner);
        checkBox = (CheckBox) findViewById(R.id.remember_me);

        mForgotPassword = (TextView) findViewById(R.id.forgotPassword);

        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,listItem);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        materialSpinner.setAdapter(arrayAdapter);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        materialSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String type = "Empty";

                if(parent.getItemAtPosition(position).toString().equals("Customer")){
                    type = "Customer";
                }
                else if(parent.getItemAtPosition(position).toString().equals("Admin")){
                    type="Admin";
                }
                
                isType = type;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorStatusBar));

        mProgess = new ProgressDialog(this);

        mPhoneNumber = (MaterialEditText) findViewById(R.id.edit_phone_number);
        mPassword = (MaterialEditText) findViewById(R.id.edit_password);
        mSigninButton = (FButton) findViewById(R.id.sign_in_button);

        Paper.init(this);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgess.setMessage("Signing in");
                mProgess.show();

                if (CurrentUser.isConnectedToInternet(getApplicationContext())){

                    if (isType.equals("Customer")) {

                        if (!TextUtils.isEmpty(mPhoneNumber.getText().toString()) && !TextUtils.isEmpty(mPassword.getText().toString())) {

                            if(checkBox.isChecked()){

                                Paper.book().write(CurrentUser.USER,mPhoneNumber.getText().toString());
                                Paper.book().write(CurrentUser.PASSWORD,mPassword.getText().toString());
                                Paper.book().write(CurrentUser.TYPE,isType);

                            }

                            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(mPhoneNumber.getText().toString())) {

                                        GetUserInfo getUserInfo = dataSnapshot.child(mPhoneNumber.getText().toString()).getValue(GetUserInfo.class);

                                        getUserInfo.setPhone(mPhoneNumber.getText().toString()); // Set the Phone Number...

                                        if (!Boolean.parseBoolean(getUserInfo.getIsstaff())) {

                                            if (getUserInfo.getPassword().equals(mPassword.getText().toString())) {

                                                mProgess.dismiss();

                                                Intent homeIntent = new Intent(SigninActivity.this, HomeActivity.class);

                                                CurrentUser.currentUser = getUserInfo;

                                                startActivity(homeIntent);

                                                finish();

                                                mDatabaseUsers.removeEventListener(this);


                                            } else {
                                                mProgess.dismiss();
                                                Toast.makeText(SigninActivity.this, "Incorrect password!", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            mProgess.dismiss();
                                            Toast.makeText(SigninActivity.this, "Please choose correct option, Admin/Customer!", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        mProgess.dismiss();
                                        Toast.makeText(SigninActivity.this, "Please Sign up before Sign in!", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            mProgess.dismiss();
                            Toast.makeText(SigninActivity.this, "Phone Number and Password can't be empty!", Toast.LENGTH_SHORT).show();
                        }
                    } else if (isType.equals("Admin")) {

                        if (!TextUtils.isEmpty(mPhoneNumber.getText().toString()) && !TextUtils.isEmpty(mPassword.getText().toString())) {

                            if(checkBox.isChecked()){
                                Paper.book().write(CurrentUser.USER,mPhoneNumber.getText().toString());
                                Paper.book().write(CurrentUser.PASSWORD,mPassword.getText().toString());
                                Paper.book().write(CurrentUser.TYPE,isType);
                            }

                            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(mPhoneNumber.getText().toString())) {

                                        GetServerUserInfo getServerUserInfo = dataSnapshot.child(mPhoneNumber.getText().toString()).getValue(GetServerUserInfo.class);
                                        getServerUserInfo.setPhone(mPhoneNumber.getText().toString());

                                        if (Boolean.parseBoolean(getServerUserInfo.getIsstaff())) {

                                            if (getServerUserInfo.getPassword().equals(mPassword.getText().toString())) {

                                                mProgess.dismiss();

                                                Intent homeServerIntent = new Intent(SigninActivity.this, HomeServerActivity.class);

                                                CurrentServerUser.currentServerUser = getServerUserInfo;

                                                startActivity(homeServerIntent);
                                                finish();
                                                mDatabaseUsers.removeEventListener(this);
                                            } else {
                                                mProgess.dismiss();
                                                Toast.makeText(SigninActivity.this, "Incorrect password!", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            mProgess.dismiss();
                                            Toast.makeText(SigninActivity.this, "Please choose correct option, Admin/Customer!", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        mProgess.dismiss();
                                        Toast.makeText(SigninActivity.this, "Sorry, you are not admin!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        } else {
                            mProgess.dismiss();
                            Toast.makeText(SigninActivity.this, "Phone Number and Password can't be empty!", Toast.LENGTH_SHORT).show();
                        }
                    } else if (isType.equals("Empty")) {
                        mProgess.dismiss();
                        Toast.makeText(SigninActivity.this, "Please choose a option!", Toast.LENGTH_SHORT).show();
                    }
            }
            else{
                    mProgess.dismiss();
                    Toast.makeText(SigninActivity.this,"Please check your internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showForgotPasswordDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SigninActivity.this);

        alertDialogBuilder.setTitle("Forgot Password");

        alertDialogBuilder.setMessage("Please write your registered phone number and get confirmation code to retrieve password");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View addMenuLayout = layoutInflater.inflate(R.layout.forgot_password,null);

        alertDialogBuilder.setView(addMenuLayout);
        alertDialogBuilder.setIcon(R.drawable.ic_vpn_key_black_24dp);

        ccp = (CountryCodePicker) addMenuLayout.findViewById(R.id.ccp);
        mFWDphone_number = (MaterialEditText) addMenuLayout.findViewById(R.id.fwd_phone_number);
        mSendCodeButton = (FButton) addMenuLayout.findViewById(R.id.send_code);
        mConfirmationCode = (MaterialEditText) addMenuLayout.findViewById(R.id.confirmation_code);
        ccp.registerCarrierNumberEditText(mFWDphone_number);

        mSendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ccp.isValidFullNumber()){
                    showFailureNotification();
                }
                else{
                    getRandomstring();
                }
            }
        });

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(!ccp.isValidFullNumber()){
                    Snackbar.make(relativeLayout,"Your phone number was not valid. Please write again with valid information!",Snackbar.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(mConfirmationCode.getText().toString())){
                    Snackbar.make(relativeLayout,"You have not confirmed the confirmation code!",Snackbar.LENGTH_SHORT).show();
                }
                else {
                    String s = mConfirmationCode.getText().toString();

                    if(!s.equals(mRandomStringCode)){
                        Snackbar.make(relativeLayout,"Sorry! Your Confirmation code doesn't matched!",Snackbar.LENGTH_SHORT).show();
                    }
                    else{
                       mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {
                               String s = ccp.getFullNumberWithPlus();
                               GetUserInfo getUserInfo = dataSnapshot.child(s).getValue(GetUserInfo.class);
                               showPasswordNotification(getUserInfo.getPassword());
                           }

                           @Override
                           public void onCancelled(DatabaseError databaseError) {

                           }
                       });
                    }
                }

                dialog.dismiss();

            }
        });

        alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });

        alertDialogBuilder.show();

    }

    private void showPasswordNotification(String password) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.drawable.ic_restaurant_black_24dp)
                .setContentTitle("Foodies")
                .setContentText("Your password was "+password)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis());

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());

    }

    private void getRandomstring() {

        String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 11) { // length of the random string.
            int index = (int) (rnd.nextFloat() * s.length());
            salt.append(s.charAt(index));
        }



        String saltStr = salt.toString();
        mRandomStringCode = saltStr;
        showSuccessNotification(saltStr);
    }

    private void showSuccessNotification(String s) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.drawable.ic_restaurant_black_24dp)
                .setContentTitle("Foodies")
                .setContentText("Your confirmation code is "+s)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis());

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());

    }

    private void showFailureNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.drawable.ic_restaurant_black_24dp)
                .setContentTitle("Foodies")
                .setContentText("Your phone number is not valid")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis());

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }
}