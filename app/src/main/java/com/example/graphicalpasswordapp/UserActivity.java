package com.example.graphicalpasswordapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UserActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private int numberOfUsers = -1;
    private TextView welcome, firstTip, secondTip, thirdTip, resetTip, patterns, recommendation;
    private RadioGroup radioGroup;
    private ProgressBar progressBar;
    private Intent intent;
    private User currentUser;
    private PaintView paintView;
    private Button backButton, backForSettingButton;
    private Switch autoRefresh;
    private ArrayList<Button> mainButtons = new ArrayList<>();
    private Button[] changePasswordButtons = new Button[3];
    private Button[] manageDistractorButton = new Button[4];
    private ImageView[] passwordImageViews = new ImageView[3];
    private ArrayList<String> distractors = new ArrayList<>();
    private ArrayList<ImageView> distractorsImageViews = new ArrayList<>();
    private ArrayList<ImageView> distractorsPicked = new ArrayList<>();
    private ArrayList<String> tempDistractors = new ArrayList<>();
    private Bitmap[] passwords = new Bitmap[3];
    private ArrayList<String> passwordChanged = new ArrayList();
    private ArrayList<Integer> selectedImageViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initView();
        initUserNumber();
        decodePasswordFromBASE64();
    }

    private void initView(){
        db = FirebaseFirestore.getInstance();

        paintView = findViewById(R.id.paintView);
        int width = DensityUtil.dp2px(UserActivity.this, 350);
        int height = DensityUtil.dp2px(UserActivity.this, 350);
        paintView.init(width, height);

        welcome = findViewById(R.id.welcome);
        firstTip = findViewById(R.id.firstTip);
        secondTip = findViewById(R.id.secondTip);
        thirdTip = findViewById(R.id.thirdTip);
        resetTip = findViewById(R.id.resetTip);
        patterns = findViewById(R.id.patterns);
        recommendation = findViewById(R.id.recommendation);

        radioGroup = findViewById(R.id.radioGroup);

        progressBar = findViewById(R.id.progressBar);

        passwordImageViews[0] = findViewById(R.id.firstPassword);
        passwordImageViews[1] = findViewById(R.id.secondPassword);
        passwordImageViews[2] = findViewById(R.id.thirdPassword);

        distractorsImageViews.add((ImageView) findViewById(R.id.distractor1));
        distractorsImageViews.add((ImageView) findViewById(R.id.distractor2));
        distractorsImageViews.add((ImageView) findViewById(R.id.distractor3));
        distractorsImageViews.add((ImageView) findViewById(R.id.distractor4));
        distractorsImageViews.add((ImageView) findViewById(R.id.distractor5));
        distractorsImageViews.add((ImageView) findViewById(R.id.distractor6));
        distractorsImageViews.add((ImageView) findViewById(R.id.distractor7));
        distractorsImageViews.add((ImageView) findViewById(R.id.distractor8));
        distractorsImageViews.add((ImageView) findViewById(R.id.distractor9));
        distractorsImageViews.add((ImageView) findViewById(R.id.distractor10));
        distractorsImageViews.add((ImageView) findViewById(R.id.distractor11));
        distractorsImageViews.add((ImageView) findViewById(R.id.distractor12));

        intent = getIntent();
        currentUser = (User) intent.getSerializableExtra("currentUser");

        mainButtons.add((Button) findViewById(R.id.viewPasswordButton));
        mainButtons.add((Button) findViewById(R.id.changePasswordButton));
        mainButtons.add((Button) findViewById(R.id.manageDistractorButton));
        mainButtons.add((Button) findViewById(R.id.comingSoonButton));
        mainButtons.add((Button) findViewById(R.id.logout));
        backButton = findViewById(R.id.back);
        backForSettingButton = findViewById(R.id.backForSetting);
        autoRefresh = findViewById(R.id.autoRefresh);
        changePasswordButtons[0] = findViewById(R.id.backForChangePassword);
        changePasswordButtons[1] = findViewById(R.id.clear);
        changePasswordButtons[2] = findViewById(R.id.next);
        manageDistractorButton[0] = findViewById(R.id.backForManageDis);
        manageDistractorButton[1] = findViewById(R.id.fresh);
        manageDistractorButton[2] = findViewById(R.id.changeDistractor);
        manageDistractorButton[3] = findViewById(R.id.save);

        welcome.setText("Welcome, " + currentUser.getUsername());
    }

    protected void decodePasswordFromBASE64(){
        passwords[0] = PasswordFactory.decodeFromBase64(currentUser.getFirstPassword());
        passwords[1] = PasswordFactory.decodeFromBase64(currentUser.getSecondPassword());
        passwords[2] = PasswordFactory.decodeFromBase64(currentUser.getThirdPassword());
    }

    public void viewPassword(View view){
        setVisibility(mainButtons, View.GONE);

        firstTip.setVisibility(View.VISIBLE);
        secondTip.setVisibility(View.VISIBLE);
        thirdTip.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);

        for(int i=0;i<passwordImageViews.length;i++){
            passwordImageViews[i].setVisibility(View.VISIBLE);
            passwordImageViews[i].setImageBitmap(passwords[i]);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisibility(mainButtons, View.VISIBLE);

                firstTip.setVisibility(View.GONE);
                secondTip.setVisibility(View.GONE);
                thirdTip.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);

                setVisibility(passwordImageViews, View.GONE);
            }
        });
    }

    public void changePassword(View view){
        resetTip.setVisibility(View.VISIBLE);
        patterns.setVisibility(View.VISIBLE);
        radioGroup.setVisibility(View.VISIBLE);
        paintView.setVisibility(View.VISIBLE);
        setVisibility(mainButtons, View.GONE);
        setVisibility(changePasswordButtons, View.VISIBLE);

        resetTip.setText("1 / 3");

        changePasswordButtons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTip.setVisibility(View.GONE);
                patterns.setVisibility(View.GONE);
                radioGroup.setVisibility(View.GONE);
                paintView.setVisibility(View.GONE);

                setVisibility(changePasswordButtons, View.GONE);
                setVisibility(mainButtons, View.VISIBLE);

                passwordChanged.clear();
            }
        });
    }

    public void updatePassword(View view){
        String email = currentUser.getEmail();

        Bitmap password = PasswordFactory.takeScreenShot(paintView);
        String passwordEncoded = PasswordFactory.getImgB64(password);
        passwordChanged.add(passwordEncoded);

        switch(passwordChanged.size()){
            case 1:
                resetTip.setText("2 / 3");
                paintView.clear();
                Toast.makeText(UserActivity.this, "New password created.", Toast.LENGTH_LONG).show();
                break;

            case 2:
                resetTip.setText("3 / 3");
                paintView.clear();
                Toast.makeText(UserActivity.this, "New password created.", Toast.LENGTH_LONG).show();
                break;

            case 3:
                Toast.makeText(UserActivity.this, "New password created.", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.VISIBLE);

                Map<String, Object> data = new HashMap<>();
                data.put("firstPassword", passwordChanged.get(0));
                data.put("secondPassword", passwordChanged.get(1));
                data.put("thirdPassword", passwordChanged.get(2));

                db.collection("users").document(email)
                        .update(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(UserActivity.this, SuccessActivity.class));
                                progressBar.setVisibility(View.GONE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                startActivity(new Intent(UserActivity.this, MainActivity.class));
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                db.collection("loginPasswords").document(email).delete();
                break;
        }
    }

    public void logout(View view){
        restore();
        startActivity(new Intent(UserActivity.this, MainActivity.class));
    }

    private void restore(){
        currentUser = null;
        mainButtons.clear();
        changePasswordButtons = new Button[3];
        passwordImageViews = new ImageView[3];
        distractors.clear();
        distractorsImageViews.clear();
        passwords = new Bitmap[3];
        passwordChanged.clear();
    }

    public void manageDistractors(View view){
        progressBar.setVisibility(View.VISIBLE);
        setVisibility(manageDistractorButton, View.VISIBLE);
        setVisibility(mainButtons, View.GONE);
        manageDistractorButton[2].setClickable(false);
        manageDistractorButton[2].setAlpha(.5f);
        manageDistractorButton[3].setClickable(false);
        manageDistractorButton[3].setAlpha(.5f);

        db.collection("loginPasswords").document(currentUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                Message msg = new Message();
                                Bundle data = new Bundle();

                                for(int i=1;i<13;i++){
                                    data.putString(String.valueOf(i), document.getString(String.valueOf(i)));
                                }

                                msg.setData(data);
                                fetchDistractor.sendMessage(msg);
                            }
                        }
                    }
                });

        manageDistractorButton[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisibility(distractorsImageViews, View.GONE);
                setVisibility(manageDistractorButton, View.GONE);
                setVisibility(mainButtons, View.VISIBLE);
                recommendation.setVisibility(View.GONE);
                distractorsPicked.clear();
                tempDistractors.clear();
                distractors.clear();
                for(int i=0;i<distractorsImageViews.size();i++){
                    distractorsImageViews.get(i).setSelected(false);
                    distractorsImageViews.get(i).setImageAlpha(255);
                    distractorsImageViews.get(i).clearAnimation();
                }
            }
        });
    }

    Handler fetchDistractor = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();

            ArrayList<Bitmap> decodedDistractors = new ArrayList<>();

            for(int i=1;i<13;i++){
                distractors.add(data.getString(String.valueOf(i)));
            }

            for(int i=0;i<distractors.size();i++){
                decodedDistractors.add(PasswordFactory.decodeFromBase64(distractors.get(i)));
            }

            for (int i=0;i<distractorsImageViews.size();i++){
                distractorsImageViews.get(i).setImageBitmap(decodedDistractors.get(i));
            }

            setVisibility(distractorsImageViews, View.VISIBLE);
            recommendation.setText("It's recommended that keep the auto-refresh function off if you prefer a fixed password.");
            recommendation.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    };

    public void pickImage(View view){

        ImageView imageView = (ImageView) findViewById(view.getId());
        BitmapDrawable temp = (BitmapDrawable) imageView.getDrawable();
        Bitmap picked = temp.getBitmap();
        boolean isSame = false;

        for(int i=0;i<passwords.length;i++){
            if(picked.sameAs(passwords[i])){
                isSame = true;
            }
        }

        if(imageView.isSelected()){
            imageView.setImageAlpha(255);
            imageView.setSelected(false);
            distractorsPicked.remove(imageView);
        }else{
            if(!isSame){
                imageView.setImageAlpha(100);
                imageView.setSelected(true);
                recommendation.setText("It's recommended that keep the auto-refresh function off if you prefer a fixed password.");
                distractorsPicked.add(imageView);
            }else{
                Toast.makeText(UserActivity.this, "You can't pick your own password.", Toast.LENGTH_LONG).show();
                recommendation.setText("You can't pick your own password.");
                setFlickerAnimation(imageView);
                imageView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            }
        }

        if(distractorsPicked.size() > 0){
            manageDistractorButton[2].setClickable(true);
            manageDistractorButton[2].setAlpha(1f);
        }else{
            manageDistractorButton[2].setClickable(false);
            manageDistractorButton[2].setAlpha(.5f);
        }
    }

    public void swapDistractors(View view){

        for(int i=0;i<distractorsPicked.size();i++){
            selectedImageViews.add(distractorsImageViews.indexOf(distractorsPicked.get(i)));
        }

        changeDistractors();
    }

    protected void changeDistractors(){

        manageDistractorButton[3].setClickable(true);
        manageDistractorButton[3].setAlpha(1f);

        if(distractorsPicked.size()>0){
            progressBar.setVisibility(View.VISIBLE);
            Random r = new Random();
            int temp = r.nextInt(numberOfUsers);

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
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                if(document.exists()){
                                    Message msg = new Message();
                                    Bundle data = new Bundle();

                                    data.putString("distractor", document.getString(finalWhichPassword));

                                    msg.setData(data);
                                    changeDistractor.sendMessage(msg);
                                }
                            }
                        }
                    });
        }else{
            Toast.makeText(UserActivity.this, "Pick a image first.", Toast.LENGTH_LONG).show();
        }
    }

    Handler changeDistractor = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String distractor = data.getString("distractor");
            boolean isSame = false;
            ArrayList<Bitmap> tempBitmap = new ArrayList<>();

            for(int i=0;i<distractors.size();i++){
                if(distractor.equals(distractors.get(i))){
                    isSame = true;
                }
            }

            if(isSame){
                changeDistractors();
            }else{
                if(tempDistractors.contains(distractor)){
                    changeDistractors();
                }else{
                    tempDistractors.add(distractor);

                    if(tempDistractors.size() == distractorsPicked.size()){
                        for(int i=0;i<tempDistractors.size();i++){
                            tempBitmap.add(PasswordFactory.decodeFromBase64(tempDistractors.get(i)));
                        }

                        for(int i=0;i<distractorsPicked.size();i++){
                            distractorsPicked.get(i).setImageBitmap(tempBitmap.get(i));
                            distractorsPicked.get(i).invalidate();
                        }

                        for(int i=0;i<tempDistractors.size();i++){
                            distractors.remove((int) selectedImageViews.get(i));
                            distractors.add(selectedImageViews.get(i), tempDistractors.get(i));
                        }

                        distractorsPicked.clear();
                        tempDistractors.clear();
                        selectedImageViews.clear();
                        tempBitmap.clear();

                        for(int i=0;i<distractorsImageViews.size();i++){
                            distractorsImageViews.get(i).setSelected(false);
                            distractorsImageViews.get(i).setImageAlpha(255);
                        }

                        progressBar.setVisibility(View.GONE);
                    }else{
                        changeDistractors();
                    }
                }
            }


        }
    };

    public void fresh(View view){

        manageDistractorButton[3].setClickable(true);
        manageDistractorButton[3].setAlpha(1f);

        Collections.shuffle(distractors);

        ArrayList<Bitmap> decoded = new ArrayList<>();

        for(int i=0;i<distractors.size();i++){
            decoded.add(PasswordFactory.decodeFromBase64(distractors.get(i)));
        }

        for (int i=0;i<distractorsImageViews.size();i++){
            distractorsImageViews.get(i).setImageBitmap(decoded.get(i));
        }
    }

    public void save(View view){

        progressBar.setVisibility(View.VISIBLE);

        Map<String, Object> data = new HashMap<>();

        for(int i=0;i<distractors.size();i++){
            data.put(String.valueOf(i+1), distractors.get(i));
        }

        db.collection("loginPasswords").document(currentUser.getEmail())
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(UserActivity.this, SuccessActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(UserActivity.this, MainActivity.class));
                    }
                });
    }

    public void setting(View view){
        setVisibility(mainButtons, View.GONE);
        backForSettingButton.setVisibility(View.VISIBLE);
        autoRefresh.setVisibility(View.VISIBLE);

        if(currentUser.getAutoRefresh()){
            autoRefresh.setChecked(true);
        }else{
            autoRefresh.setChecked(false);
        }

        autoRefresh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    turnOnAutoRefresh();
                }else{
                    turnOffAutoRefresh();
                }
            }
        });

        backForSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisibility(mainButtons, View.VISIBLE);
                backForSettingButton.setVisibility(View.GONE);
                autoRefresh.setVisibility(View.GONE);
            }
        });
    }

    private void turnOnAutoRefresh(){
        db.collection("users").document(currentUser.getEmail())
                .update("autoRefresh", true)
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
    }

    private void turnOffAutoRefresh(){
        db.collection("users").document(currentUser.getEmail())
                .update("autoRefresh", false)
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
    }

    public void clear(View view){
        paintView.clear();
    }

    public void normal(View view){
        paintView.normal();
    }

    public void emboss(View view){
        paintView.emboss();
    }

    public void blur(View view){
        paintView.blur();
    }

    private void setVisibility(Button[] b, int x){
        for(int i=0;i<b.length;i++){
            b[i].setVisibility(x);
        }
    }

    private void setVisibility(ImageView[] iv, int x){
        for(int i=0;i<iv.length;i++){
            iv[i].setVisibility(x);
        }
    }

    private void setVisibility(ArrayList<Button> b, Integer x){
        int y = x.intValue();
        for(int i=0;i<b.size();i++){
            b.get(i).setVisibility(y);
        }
    }

    private void setVisibility(ArrayList<ImageView> iv, int x){
        for(int i=0;i<iv.size();i++){
            iv.get(i).setVisibility(x);
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
                        numberOfUsers = Integer.valueOf(document.getString("userAmount"));
                    }else{
                        Toast.makeText(UserActivity.this, "Document does not exist!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(UserActivity.this, "Init Task Failed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setFlickerAnimation(ImageView iv) {
        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(300); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(3); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); //
        iv.setAnimation(animation);
    }
}
