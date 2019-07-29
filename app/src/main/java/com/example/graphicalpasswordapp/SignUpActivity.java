package com.example.graphicalpasswordapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private TextView nameTip, emailTip, currentStep, notification, passwordTip, pattern;
    private EditText username, email;
    private Button stepOneButton, stepTwoButton, clearButton;
    private RadioGroup patterns;
    private PaintView firstGraphic;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private String tempName, tempEmail;
    private ArrayList<String> passwords = new ArrayList<>();
    private int numberOfUsers = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initView();
        initUserNumber();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
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

            email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if(hasFocus){
                        email.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_orange_dark)));
                    }else{
                        email.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
                    }
                }
            });
        }
    }

    private void initView(){
        username = findViewById(R.id.name);
        email = findViewById(R.id.e_mail);
        nameTip = findViewById(R.id.nameTip);
        emailTip = findViewById(R.id.emailTip);
        notification = findViewById(R.id.loginResult);
        passwordTip = findViewById(R.id.passwordTip);
        pattern = findViewById(R.id.pattern);
        currentStep = findViewById(R.id.currentStep);
        stepOneButton = findViewById(R.id.stepOneButton);
        stepTwoButton = findViewById(R.id.stepTwoButton);
        patterns = findViewById(R.id.patternRadio);
        clearButton = findViewById(R.id.clearButton);
        firstGraphic = findViewById(R.id.firstGraphic);
        db = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);
    }
    /*
     *Deal with inputted username and email.
     * Direct users to next activity.
     * */
    public void verification(View view){

        tempName = username.getText().toString();
        tempEmail = email.getText().toString();

        if(!tempName.equals("") && !tempEmail.equals("")){
            if(isEmailValid(tempEmail)){
                db.collection("users").document(tempEmail)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                notification.setText("The email has been already used.");
                            }else{
                                nameTip.setVisibility(View.GONE);
                                username.setVisibility(View.GONE);
                                emailTip.setVisibility(View.GONE);
                                email.setVisibility(View.GONE);
                                currentStep.setText("2");
                                stepOneButton.setVisibility(View.GONE);
                                notification.setVisibility(View.GONE);
                                pattern.setVisibility(View.VISIBLE);
                                patterns.setVisibility(View.VISIBLE);

                                firstGraphic.setVisibility(View.VISIBLE);
                                int width = DensityUtil.dp2px(SignUpActivity.this, 350);
                                int height = DensityUtil.dp2px(SignUpActivity.this, 350);
                                firstGraphic.init(width, height);
                                passwordTip.setVisibility(View.VISIBLE);
                                passwordTip.setText("Please draw your first password above!");
                                clearButton.setVisibility(View.VISIBLE);
                                stepTwoButton.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }else{
                notification.setText("Email Address is invalid.");
            }
        }else{
            notification.setText("Username or email cannot be empty.");
        }
    }
    /*
     *Process and upload the password user has just drawn.
     * */
    public void complete(View view){
        Bitmap password = PasswordFactory.takeScreenShot(firstGraphic);
        String passwordEncoded = PasswordFactory.getImgB64(password);
        passwords.add(passwordEncoded);

        if(passwords.size() == 1){
            currentStep.setText("3");
            passwordTip.setText("Please draw your second password above!");
            firstGraphic.clear();
            Toast.makeText(SignUpActivity.this, "First password created successfully!", Toast.LENGTH_LONG).show();
        }else if(passwords.size() == 2){
            currentStep.setText("4");
            passwordTip.setText("Please draw your third password above!");
            firstGraphic.clear();
            Toast.makeText(SignUpActivity.this, "Second password created successfully!", Toast.LENGTH_LONG).show();
        }else if(passwords.size() == 3){
            Toast.makeText(SignUpActivity.this, "Third password created successfully!", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.VISIBLE);

            Map<String, Object> temp = new HashMap<>();
            temp.put("name", tempName);
            temp.put("email", tempEmail);
            temp.put("firstPassword", passwords.get(0));
            temp.put("secondPassword", passwords.get(1));
            temp.put("thirdPassword", passwords.get(2));
            temp.put("autoRefresh", false);

            final String one = passwords.get(0);
            final String two = passwords.get(1);
            final String three = passwords.get(2);
            db.collection("users").document(tempEmail)
                    .set(temp)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(SignUpActivity.this, "Account Created!", Toast.LENGTH_LONG).show();
                            //startActivity(new Intent(SignUpActivity.this, test.class));

                            Map<String, Object> imgBank = new HashMap<>();
                            initUserNumber();
                            imgBank.put("firstPassword", one);
                            imgBank.put("secondPassword", two);
                            imgBank.put("thirdPassword", three);

                            db.collection("passwordBank").document(String.valueOf(numberOfUsers))
                                    .set(imgBank)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUpActivity.this, "Update imgBank Failed!", Toast.LENGTH_LONG).show();
                                }
                            });

                            Map<String, Object> amount = new HashMap<>();
                            amount.put("userAmount", String.valueOf(numberOfUsers+1));

                            db.collection("Statistic").document("Users")
                                    .set(amount)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUpActivity.this, "Update usernumber Failed!", Toast.LENGTH_LONG).show();
                                }
                            });
                            startActivity(new Intent(SignUpActivity.this, SuccessActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this, "Failed!", Toast.LENGTH_LONG).show();
                        }
                    });
            restore();
            progressBar.setVisibility(View.GONE);
        }

    }

    protected void restore(){
        tempEmail = "";
        tempName = "";
        passwords.clear();
    }

    public void clear(View view){
        firstGraphic.clear();
    }

    public void normal(View view){
        firstGraphic.normal();
    }

    public void emboss(View view){
        firstGraphic.emboss();
    }

    public void blur(View view){
        firstGraphic.blur();
    }

    protected boolean isEmailValid(CharSequence email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
                        Toast.makeText(SignUpActivity.this, "Document does not exist!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(SignUpActivity.this, "Init Task Failed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected void updateUserNumber(int number){
        numberOfUsers = number;
    }
}
