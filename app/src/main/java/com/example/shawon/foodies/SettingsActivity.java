package com.example.shawon.foodies;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

public class SettingsActivity extends AppCompatActivity {

    private RelativeLayout mParentLayout,mRl3,mRl4,mRl1,mRl5;
    private MaterialEditText mCurrentPassword,mNewPassword,mRetypeNewPassword,mCurrentName;
    private TextView mUserName,mName;
    boolean check1,check2,check3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorStatusBar1));

        Paper.init(this);

        mRl3 = (RelativeLayout)  findViewById(R.id.rl3);
        mRl4 = (RelativeLayout)  findViewById(R.id.rl4);
        mRl1 = (RelativeLayout)  findViewById(R.id.rl1);
        mRl5 = (RelativeLayout)  findViewById(R.id.rl5);
        mParentLayout = (RelativeLayout) findViewById(R.id.parentLayout);

        mUserName = (TextView) findViewById(R.id.user_name);
        mName = (TextView) findViewById(R.id.name);

        mUserName.setText(CurrentUser.currentUser.getName());
        mName.setText(CurrentUser.currentUser.getName());

        mRl5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFavourites();
            }
        });

        mRl3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });

        mRl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeNameDialog();
            }
        });

        mRl4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }

    private void deleteFavourites() {

        ProgressDialog mProgress = new ProgressDialog(SettingsActivity.this);
        mProgress.setMessage("Deleting");
        mProgress.show();
        List<FavouriteFood> result = new SQLiteDatabase(SettingsActivity.this).getFavourites(CurrentUser.currentUser.getPhone());
        if (!result.isEmpty()){
            new SQLiteDatabase(SettingsActivity.this).deleteFavourites(CurrentUser.currentUser.getPhone());
            Snackbar.make(mParentLayout,"Deleted!",Snackbar.LENGTH_SHORT).show();
        }
        else {
            Snackbar.make(mParentLayout, "You have no favourite food items!", Snackbar.LENGTH_SHORT).show();
        }
        mProgress.dismiss();

    }

    private void showChangeNameDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
        alertDialogBuilder.setTitle("Change Name");
        alertDialogBuilder.setMessage("Please edit the name to change current username");

        LayoutInflater layoutInflater = this.getLayoutInflater();

        View changeNameLayout = layoutInflater.inflate(R.layout.change_name_layout,null);

        mCurrentName = (MaterialEditText) changeNameLayout.findViewById(R.id.current_name);

        mCurrentName.setText(CurrentUser.currentUser.getName());

        final ProgressDialog mProgress = new ProgressDialog(SettingsActivity.this);

        alertDialogBuilder.setView(changeNameLayout);
        alertDialogBuilder.setIcon(R.drawable.ic_edit_black_24dp);

        alertDialogBuilder.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                mProgress.setMessage("Processing");
                mProgress.show();

                Map<String,Object> nameUpdate = new HashMap<>();

                nameUpdate.put("name",mCurrentName.getText().toString());

                final DatabaseReference mUsers = FirebaseDatabase.getInstance().getReference().child("Users");

                mUsers.child(CurrentUser.currentUser.getPhone())
                        .updateChildren(nameUpdate)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                mProgress.dismiss();

                                CurrentUser.currentUser.setName(mCurrentName.getText().toString());

                                mUserName.setText(CurrentUser.currentUser.getName());
                                mName.setText(CurrentUser.currentUser.getName());

                                Snackbar.make(mParentLayout,"Name changed! ",Snackbar.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        mProgress.dismiss();

                        Snackbar.make(mParentLayout,""+e.getMessage(),Snackbar.LENGTH_SHORT).show();

                    }
                });

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

    private void signOut() {

        Paper.book().destroy();

        Intent signinIntent = new Intent(SettingsActivity.this, SigninActivity.class);

        signinIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(signinIntent);

    }

    private void showChangePasswordDialog() {

        check1 = false;
        check2 = false;
        check3 = false;

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
        alertDialogBuilder.setTitle("Change Password");
        alertDialogBuilder.setMessage("Please fill up full information to change password");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View changePasswordLayout = layoutInflater.inflate(R.layout.change_password_layout,null);

        mCurrentPassword = (MaterialEditText) changePasswordLayout.findViewById(R.id.current_password);
        mNewPassword = (MaterialEditText) changePasswordLayout.findViewById(R.id.new_password);
        mRetypeNewPassword = (MaterialEditText) changePasswordLayout.findViewById(R.id.retype_password);

        mCurrentPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP){
                    if (event.getRawX() >= mCurrentPassword.getRight() - mCurrentPassword.getTotalPaddingRight()){
                        if(check1==false) {
                            mCurrentPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_remove_red_eye_black_24dp,0);
                            mCurrentPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            check1 = true;
                        }
                        else if (check1 == true){
                            mCurrentPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_visibility_off_black_24dp,0);
                            mCurrentPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            check1 = false;
                            /*mCurrentPassword.setSelection(mCurrentPassword.length());*/
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        mNewPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP){
                    if (event.getRawX() >= mNewPassword.getRight() - mNewPassword.getTotalPaddingRight()){
                        if(check2==false) {
                            mNewPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_remove_red_eye_black_24dp,0);
                            mNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            check2 = true;
                        }
                        else if (check2 == true){
                            mNewPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_visibility_off_black_24dp,0);
                            mNewPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            check2 = false;
                          /*  mNewPassword.setSelection(mNewPassword.length());*/
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        mRetypeNewPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP){
                    if (event.getRawX() >= mRetypeNewPassword.getRight() - mRetypeNewPassword.getTotalPaddingRight()){
                        if(check3==false) {
                            mRetypeNewPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_remove_red_eye_black_24dp,0);
                            mRetypeNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            check3 = true;
                        }
                        else if (check3 == true){
                            mRetypeNewPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_visibility_off_black_24dp,0);
                            mRetypeNewPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            check3 = false;
                            /*mRetypeNewPassword.setSelection(mRetypeNewPassword.length());*/
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        final ProgressDialog mProgress = new ProgressDialog(SettingsActivity.this);

        alertDialogBuilder.setView(changePasswordLayout);
        alertDialogBuilder.setIcon(R.drawable.ic_security_black_24dp);

        alertDialogBuilder.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                mProgress.setMessage("Processing");
                mProgress.show();

                if (mCurrentPassword.getText().toString().equals(CurrentUser.currentUser.getPassword())){

                    if(mNewPassword.getText().toString().equals(mRetypeNewPassword.getText().toString())){

                        Map<String,Object> passwordUpdate = new HashMap<>();

                        passwordUpdate.put("password",mNewPassword.getText().toString());

                        final DatabaseReference mUsers = FirebaseDatabase.getInstance().getReference().child("Users");

                        mUsers.child(CurrentUser.currentUser.getPhone())
                                .updateChildren(passwordUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        mProgress.dismiss();

                                        CurrentUser.currentUser.setPassword(mNewPassword.getText().toString());

                                        Paper.book().write(CurrentUser.USER,CurrentUser.currentUser.getPhone());
                                        Paper.book().write(CurrentUser.PASSWORD,CurrentUser.currentUser.getPassword());
                                        Paper.book().write(CurrentUser.TYPE,"Customer");

                                        Snackbar.make(mParentLayout,"Password updated! ",Snackbar.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                mProgress.dismiss();

                                Snackbar.make(mParentLayout,""+e.getMessage(),Snackbar.LENGTH_SHORT).show();

                            }
                        });

                    }
                    else {
                        mProgress.dismiss();
                        Toast.makeText(SettingsActivity.this,"Please type the new password twice correctly!",Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    mProgress.dismiss();
                    Toast.makeText(SettingsActivity.this,"You have typed wrong current password!",Toast.LENGTH_SHORT).show();
                }

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

    @Override
    protected void onResume() {
        super.onResume();
        mUserName.setText(CurrentUser.currentUser.getName());
        mName.setText(CurrentUser.currentUser.getName());
    }
}
