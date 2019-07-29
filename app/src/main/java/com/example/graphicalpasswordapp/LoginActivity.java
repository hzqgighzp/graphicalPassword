package com.example.graphicalpasswordapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private TextView loginResult, loginTip, loginTitle, notification;
    private ProgressBar progressBar;
    private String tempEmail, currentUsername, currentUserEmail;
    private Button loginButton, confirmButton, homeButton;
    private ImageView password1, password2, password3, password4, password5, password6,
            password7, password8, password9, password10, password11, password12;
    private ImageView[] imageViews = new ImageView[12];
    private FirebaseFirestore db;
    private int numberOfUsers = -1;
    private ArrayList<String> allImgEncoded = new ArrayList<String>();
    private ArrayList<Bitmap> allImgDecoded = new ArrayList<>();
    private ArrayList<Bitmap> currentUsersPasswords = new ArrayList<>();
    private ArrayList<Bitmap> pickedPasswords = new ArrayList<>();
    private int pickedPic = 0;
    private User currentUser;
    private boolean isAutoRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        /*
        * Initiate the database and fetch current number of users.
        * */
        db = FirebaseFirestore.getInstance();
        initUserNumber();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if(hasFocus){
                        username.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_orange_dark)));
                    }else{
                        username.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
                    }
                }
            });
        }
    }

    private void initView(){
        username = findViewById(R.id.username);
        loginResult = findViewById(R.id.loginResult);
        loginTip = findViewById(R.id.loginTip);
        loginTitle = findViewById(R.id.loginTitle);
        notification = findViewById(R.id.notification);
        loginButton = findViewById(R.id.loginButton);
        confirmButton = findViewById(R.id.confirmButton);
        homeButton = findViewById(R.id.home);
        progressBar = findViewById(R.id.progressBar);

        password1 = findViewById(R.id.password1);
        password2 = findViewById(R.id.password2);
        password3 = findViewById(R.id.password3);
        password4 = findViewById(R.id.password4);
        password5 = findViewById(R.id.password5);
        password6 = findViewById(R.id.password6);
        password7 = findViewById(R.id.password7);
        password8 = findViewById(R.id.password8);
        password9 = findViewById(R.id.password9);
        password10 = findViewById(R.id.password10);
        password11 = findViewById(R.id.password11);
        password12 = findViewById(R.id.password12);

        imageViews[0] = password1;
        imageViews[1] = password2;
        imageViews[2] = password3;
        imageViews[3] = password4;
        imageViews[4] = password5;
        imageViews[5] = password6;
        imageViews[6] = password7;
        imageViews[7] = password8;
        imageViews[8] = password9;
        imageViews[9] = password10;
        imageViews[10] = password11;
        imageViews[11] = password12;
    }
    /*
     *Confirm and verify if inputted email address exists.
     *If exists, fetch the user's password.
     * */
    public void authentication(View view){
        boolean isEmpty;
        tempEmail = username.getText().toString();
        progressBar.setVisibility(View.VISIBLE);

        if(tempEmail.equals("")){
            isEmpty = true;
            loginResult.setText("Please enter your email address.");
            progressBar.setVisibility(View.GONE);
        }else{
            isEmpty = false;
        }

        if(!isEmpty){
            db.collection("users").document(tempEmail)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                if(document.exists()){
                                    username.setVisibility(View.GONE);
                                    loginResult.setVisibility(View.GONE);
                                    loginTitle.setVisibility(View.GONE);
                                    loginTip.setVisibility(View.VISIBLE);
                                    loginTip.setText("Please select three your passwords below.");
                                    loginButton.setVisibility(View.GONE);
                                    confirmButton.setClickable(false);
                                    confirmButton.setAlpha(.5f);
                                    confirmButton.setVisibility(View.VISIBLE);

                                    for(int i=0;i<imageViews.length;i++){
                                        imageViews[i].setVisibility(View.VISIBLE);
                                    }


                                    allImgEncoded.add(document.getString("firstPassword"));
                                    allImgEncoded.add(document.getString("secondPassword"));
                                    allImgEncoded.add(document.getString("thirdPassword"));
                                    isAutoRefresh = document.getBoolean("autoRefresh");

                                    currentUsername = document.getString("name");
                                    currentUserEmail = document.getString("email");
                                    currentUser = new User(currentUsername, currentUserEmail, allImgEncoded.get(0),
                                            allImgEncoded.get(1), allImgEncoded.get(2), isAutoRefresh);

                                    currentUsersPasswords.add(PasswordFactory.decodeFromBase64(allImgEncoded.get(0)));
                                    currentUsersPasswords.add(PasswordFactory.decodeFromBase64(allImgEncoded.get(1)));
                                    currentUsersPasswords.add(PasswordFactory.decodeFromBase64(allImgEncoded.get(2)));

                                    db.collection("loginPasswords").document(tempEmail)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        DocumentSnapshot document = task.getResult();
                                                        if(document.exists() && !isAutoRefresh){
                                                            Message msg = new Message();
                                                            Bundle data = new Bundle();

                                                            for(int i=1;i<13;i++){
                                                                data.putString(String.valueOf(i), document.getString(String.valueOf(i)));
                                                            }

                                                            msg.setData(data);
                                                            fetchDistractors.sendMessage(msg);
                                                        }else{
                                                            addDistractors();
                                                        }
                                                    }
                                                }
                                            });

                                }else{
                                    loginResult.setText("This account has not been registered.");
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
        }
    }

    Handler fetchDistractors = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();

            allImgEncoded.clear();

            for(int i=1;i<13;i++){
                allImgEncoded.add(data.getString(String.valueOf(i)));
            }

//            if(isAutoRefresh){
//                Collections.shuffle(allImgEncoded);
//            }

            for(int i=0;i<allImgEncoded.size();i++){
                allImgDecoded.add(PasswordFactory.decodeFromBase64(allImgEncoded.get(i)));
            }

            for(int i=0;i<imageViews.length;i++){
                imageViews[i].setImageBitmap(allImgDecoded.get(i));
            }

            progressBar.setVisibility(View.GONE);
        }
    };

    /*
     *Add distractors to users passwords.
     * */
    protected void addDistractors(){
        Random r = new Random();
        initUserNumber();


        final int temp = r.nextInt(numberOfUsers);
        int pos = r.nextInt(3);
        String whichPassword = "";

        if(pos == 0){
            whichPassword = "firstPassword";
        }else if (pos == 1){
            whichPassword = "secondPassword";
        }else if(pos == 2){
            whichPassword = "thirdPassword";
        }

        final String finalWhichPassword = whichPassword;
        db.collection("passwordBank").document(String.valueOf(temp))
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Message msg = new Message();
                        Bundle data = new Bundle();

                        data.putString("distractors", document.getString(finalWhichPassword));
                        msg.setData(data);
                        handler.sendMessage(msg);
                    }else{
                        Toast.makeText(LoginActivity.this, "Document does not exist!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Get dis Failed!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    /*
     *Handler to solve remaining instructions after fetched data from database.
     * */
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String s = data.getString("distractors");
            boolean isSame = false;

            for(int i=0;i<allImgEncoded.size();i++){
                if(s.equals(allImgEncoded.get(i))){
                    isSame = true;
                }
            }

            if(!isSame){
                allImgEncoded.add(s);
            }

            if(allImgEncoded.size() == 12){

                Collections.shuffle(allImgEncoded);

                Map<String, Object> loginPasswords = new HashMap<>();
                for(int i=1;i<13;i++){
                    loginPasswords.put(String.valueOf(i), allImgEncoded.get(i-1));
                }

                db.collection("loginPasswords").document(tempEmail)
                        .set(loginPasswords)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });


                for(int i=0;i<allImgEncoded.size();i++){
                    allImgDecoded.add(PasswordFactory.decodeFromBase64(allImgEncoded.get(i)));
                }

                for(int i=0;i<imageViews.length;i++){
                    imageViews[i].setImageBitmap(allImgDecoded.get(i));
                }

                progressBar.setVisibility(View.GONE);
            }else if(allImgEncoded.size() < 12){
                addDistractors();
            }
        }
    };
    /*
     *Verify selected passwords.
     * */
    public void verifyPassword(View view){
        boolean isSuccess = false;
        progressBar.setVisibility(View.VISIBLE);
        if(pickedPasswords.get(0).sameAs(currentUsersPasswords.get(0))){
            if(pickedPasswords.get(1).sameAs(currentUsersPasswords.get(1))){
                if(pickedPasswords.get(2).sameAs(currentUsersPasswords.get(2))){
                    isSuccess = true;
                }
            }
        }
        if(isSuccess){
            Intent intent = new Intent(LoginActivity.this, UserActivity.class);
            intent.putExtra("currentUser", currentUser);
            progressBar.setVisibility(View.GONE);
            startActivity(intent);

            restore();
        }else{
            homeButton.setVisibility(View.VISIBLE);
            notification.setVisibility(View.VISIBLE);
            notification.setText("Failed!");
            progressBar.setVisibility(View.GONE);
            loginTip.setVisibility(View.GONE);
            confirmButton.setVisibility(View.GONE);

            for(int i=0;i<imageViews.length;i++){
                imageViews[i].setVisibility(View.GONE);
            }

            restore();
        }
    }

    public void goHome(View view){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    protected void restore(){
        allImgEncoded.clear();
        allImgDecoded.clear();
        currentUsersPasswords.clear();
        currentUserEmail = "";
        currentUsername = "";
        pickedPasswords.clear();
        pickedPic = 0;
        currentUser = null;
    }
    /*
     *Function to control password selected.
     * */
    public void pickPic(View view){
        //Bitmap tick = BitmapFactory.decodeResource(getResources(), R.drawable.checked);
        ImageView imageView = (ImageView) findViewById(view.getId());
        if(imageView.isSelected()){
            imageView.setImageAlpha(255);
            imageView.setSelected(false);
            pickedPic--;
            pickedPasswords.remove(((BitmapDrawable)imageView.getDrawable()).getBitmap());
        }else{
            if(pickedPic < 3){
                imageView.setImageAlpha(100);
                imageView.setSelected(true);
                pickedPic++;
                pickedPasswords.add(((BitmapDrawable)imageView.getDrawable()).getBitmap());
            }
        }

        if(pickedPic == 3){
            confirmButton.setClickable(true);
            confirmButton.setAlpha(1f);
        }else{
            confirmButton.setClickable(false);
            confirmButton.setAlpha(.5f);
        }
    }

    protected void initUserNumber(){
        db.collection("Statistic")
                .document("Users")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        updateUserNumber(Integer.valueOf(document.getString("userAmount")));
                    }else{
                        Toast.makeText(LoginActivity.this, "Document does not exist!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Init Task Failed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected void updateUserNumber(int number){
        numberOfUsers = number;
    }
}
