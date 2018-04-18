package com.example.shawon.foodies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class WelcomeActivity extends AppCompatActivity {

    Button mSigninButton, mSignupButton;
    TextView mWelcomeScreenText;

    private ProgressDialog mProgess;
    private DatabaseReference mDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*FacebookSdk.sdkInitialize(getApplicationContext());*/

        setContentView(R.layout.activity_welcome);

        /*printKeyHash();*/

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorStatusBar));

        mSigninButton = (Button) findViewById(R.id.sign_in_button);
        mSignupButton = (Button) findViewById(R.id.sign_up_button);
        mWelcomeScreenText = (TextView) findViewById(R.id.welcome_screen_text);

        mProgess = new ProgressDialog(this);

        Paper.init(this);

        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this,SignupActivity.class));
            }
        });

        mSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this,SigninActivity.class));
            }
        });

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        String mUser = Paper.book().read(CurrentUser.USER);
        String mPassword = Paper.book().read(CurrentUser.PASSWORD);
        String mType = Paper.book().read(CurrentUser.TYPE);

        if(mUser != null && mPassword != null && mType != null){
            if(!mUser.isEmpty() && !mPassword.isEmpty() && !mType.isEmpty()){
                signIn(mType,mUser,mPassword);
            }
        }
    }

   /* private void printKeyHash() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.shawon.foodies", PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures){

                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(messageDigest.digest(),Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }*/

    private void signIn(final String mType, final String mUser, final String mPassword) {


        String isType = null;

        if(mType.equals("Customer")){
            isType = "Customer";
        }
        else{
            isType = "Admin";
        }

        mProgess.setMessage("Processing");
        mProgess.show();

        if (CurrentUser.isConnectedToInternet(getApplicationContext())){

            if (isType.equals("Customer")) {

                if (!mUser.isEmpty() && !mPassword.isEmpty()) {

                    mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(mUser)) {

                                GetUserInfo getUserInfo = dataSnapshot.child(mUser).getValue(GetUserInfo.class);

                                getUserInfo.setPhone(mUser); // Set the Phone Number...

                                if (!Boolean.parseBoolean(getUserInfo.getIsstaff())) {

                                    if (getUserInfo.getPassword().equals(mPassword)){

                                        mProgess.dismiss();

                                        Intent homeIntent = new Intent(WelcomeActivity.this, HomeActivity.class);

                                        CurrentUser.currentUser = getUserInfo;

                                        startActivity(homeIntent);

                                        finish();

                                        mDatabaseUsers.removeEventListener(this);

                                    } else {
                                        mProgess.dismiss();
                                        Toast.makeText(WelcomeActivity.this, "You have typed incorrect password!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    mProgess.dismiss();
                                    Toast.makeText(WelcomeActivity.this, "You have selected wrong option of Admin/Customer!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mProgess.dismiss();
                                Toast.makeText(WelcomeActivity.this, "You have not signed up yet!", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            } else if (isType.equals("Admin")) {

                if (!mUser.isEmpty() && !mPassword.isEmpty()) {

                    mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(mUser)) {

                                GetServerUserInfo getServerUserInfo = dataSnapshot.child(mUser).getValue(GetServerUserInfo.class);
                                getServerUserInfo.setPhone(mUser);

                                if (Boolean.parseBoolean(getServerUserInfo.getIsstaff())) {

                                    if (getServerUserInfo.getPassword().equals(mPassword)) {

                                        mProgess.dismiss();

                                        Intent homeServerIntent = new Intent(WelcomeActivity.this, HomeServerActivity.class);

                                        CurrentServerUser.currentServerUser = getServerUserInfo;

                                        startActivity(homeServerIntent);
                                        finish();
                                        mDatabaseUsers.removeEventListener(this);
                                    }  else {
                                        mProgess.dismiss();
                                        Toast.makeText(WelcomeActivity.this, "You have typed incorrect password!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    mProgess.dismiss();
                                    Toast.makeText(WelcomeActivity.this, "You have selected wrong option of Admin/Customer!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mProgess.dismiss();
                                Toast.makeText(WelcomeActivity.this, "You have signed in as Admin, but you are not Actually an admin!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
        }
        else{
            mProgess.dismiss();
            Toast.makeText(WelcomeActivity.this,"Please check your internet connection",Toast.LENGTH_SHORT).show();
        }

    }
}