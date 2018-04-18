package com.example.shawon.foodies;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;

public class SignupActivity extends AppCompatActivity {

    private MaterialEditText mName,mPhoneNumber,mPassword;
    private FButton mSignup;

    private DatabaseReference mDatabaseUsers;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mName = (MaterialEditText) findViewById(R.id.edit_name);
        mPhoneNumber = (MaterialEditText) findViewById(R.id.edit_phone_number);
        mPassword = (MaterialEditText) findViewById(R.id.edit_password);
        mSignup = (FButton) findViewById(R.id.sign_up_button);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorStatusBar));

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mProgress = new ProgressDialog(this);

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress.setMessage("Signing up");
                mProgress.show();

                if (CurrentUser.isConnectedToInternet(getApplicationContext())){

                    if (!TextUtils.isEmpty(mName.getText().toString()) && !TextUtils.isEmpty(mPhoneNumber.getText().toString()) && !TextUtils.isEmpty(mPassword.getText().toString())) {

                        mDatabaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (!dataSnapshot.hasChild(mPhoneNumber.getText().toString())) {

                                    GetUserInfo getUserInfo = new GetUserInfo(mName.getText().toString(), mPassword.getText().toString());
                                    mDatabaseUsers.child(mPhoneNumber.getText().toString()).setValue(getUserInfo);
                                    mProgress.dismiss();
                                    Toast.makeText(SignupActivity.this, "Done! Please Sign in", Toast.LENGTH_SHORT).show();

                                    finish();

                                } else {
                                    mProgress.dismiss();
                                    Toast.makeText(SignupActivity.this, "This phone number has already registered!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        mProgress.dismiss();
                        Toast.makeText(SignupActivity.this, "Field's can not be empty!", Toast.LENGTH_SHORT).show();
                    }
            }

            else{
                    mProgress.dismiss();
                    Toast.makeText(SignupActivity.this,"Please check your internet connection",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
