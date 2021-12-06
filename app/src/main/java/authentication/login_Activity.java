package authentication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import edu.neu.madcourse.forest_hunter.Appearance;
import edu.neu.madcourse.forest_hunter.MainActivity;
import edu.neu.madcourse.forest_hunter.Music;
import edu.neu.madcourse.forest_hunter.R;
import user.User;

public class login_Activity extends AppCompatActivity {

    Button signup_button;
    Button login_button;

    ImageButton music_button;
    Music bgm;

    private static String CLIENT_REGISTRATION_TOKEN;
    private DatabaseReference mDatabase;

    User login_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_page);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:348899570889:android:32bdcd7556fad4c0804918")
                .setProjectId("forest-hunter")
                .setApiKey("AIzaSyAMQQ9MJxTGqLdTx1avANgvaIutoyaw9qE")
                .build();
        FirebaseApp.initializeApp(this, options, "edu.neu.madcourse.forest_hunter");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(login_Activity.this, "Something is wrong!", Toast.LENGTH_SHORT).show();
                } else {
                    if (CLIENT_REGISTRATION_TOKEN == null) {
                        CLIENT_REGISTRATION_TOKEN = task.getResult();
                        System.out.print(CLIENT_REGISTRATION_TOKEN);
                    }
                    Log.e("CLIENT_REGISTRATION_TOKEN", CLIENT_REGISTRATION_TOKEN);
                    //Toast.makeText(MainActivity.this, "CLIENT_REGISTRATION_TOKEN Existed", Toast.LENGTH_SHORT).show();
                }
            }
        });


        signup_button = (Button) findViewById(R.id.signup_button);
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signup_dialog(view);
            }
        });

        login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login_dialog(view);
            }
        });

        //Handling Music
        bgm = new Music(this);
        bgm.play_music();
        music_button = findViewById(R.id.change_bgm_button);
        music_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bgm.play_next();
            }
        });



    }


    public void login_dialog(View view) {
        final AlertDialog.Builder dialog;
        final EditText input_username;
        final EditText input_password;
        Button input_cancel_button;
        Button input_confirm_button;
        final TextView forget_password;
        final AlertDialog alert_dialog;

        dialog = new AlertDialog.Builder(login_Activity.this);
        View dialog_view = getLayoutInflater().inflate(R.layout.login, null);

        input_username = dialog_view.findViewById(R.id.signup_enter_username_input_box);
        input_password = dialog_view.findViewById(R.id.signup_enter_password_input_box);

        input_cancel_button = dialog_view.findViewById(R.id.login_cancel_button);
        input_confirm_button = dialog_view.findViewById(R.id.login_confirm_button);
        forget_password =  dialog_view.findViewById(R.id.forget_password_button);

        dialog.setView(dialog_view);
        alert_dialog = dialog.create();

        alert_dialog.setCanceledOnTouchOutside(false);

        input_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert_dialog.dismiss();
            }
        });

        input_confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = input_username.getText().toString();
                String password = input_password.getText().toString();

                boolean is_password_valid = validate_password(password);

                if (is_password_valid) {
                    authentication(username, password);
                }

                alert_dialog.dismiss();

            }
        });

        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = input_username.getText().toString();
                String password = input_password.getText().toString();
                alert_dialog.dismiss();
            }

        });

        alert_dialog.show();
    }

    public void signup_dialog(View view) {
        final AlertDialog.Builder dialog;
        final EditText input_username;
        final EditText input_password;
        final EditText confirm_password;
        final EditText security_question;
        final EditText security_question_answer;
        Button input_cancel_button;
        Button input_confirm_button;
        final AlertDialog alert_dialog;

        dialog = new AlertDialog.Builder(login_Activity.this);
        View dialog_view = getLayoutInflater().inflate(R.layout.signup, null);

        input_username = dialog_view.findViewById(R.id.signup_enter_username_input_box);
        input_password = dialog_view.findViewById(R.id.signup_enter_password_input_box);
        confirm_password = dialog_view.findViewById(R.id.signup_confirm_password_input_box);

        input_cancel_button = dialog_view.findViewById(R.id.login_cancel_button);
        input_confirm_button = dialog_view.findViewById(R.id.login_confirm_button);
        security_question =  dialog_view.findViewById(R.id.signup_enter_security_question_input_box);
        security_question_answer =  dialog_view.findViewById(R.id.signup_enter_security_answer_input_box);

        dialog.setView(dialog_view);
        alert_dialog = dialog.create();

        alert_dialog.setCanceledOnTouchOutside(false);

        input_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert_dialog.dismiss();
            }
        });

        input_confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (input_username.getText().toString().equals(""))
                {
                    Toast.makeText(login_Activity.this, "Username Cannot be Empty!", Toast.LENGTH_SHORT).show();
                }
                else if (input_password.getText().toString().equals(""))
                {
                    Toast.makeText(login_Activity.this, "Password Cannot be Empty!", Toast.LENGTH_SHORT).show();
                }
                else if (confirm_password.getText().toString().equals(""))
                {
                    Toast.makeText(login_Activity.this, "Confirm Password Cannot be Empty!", Toast.LENGTH_SHORT).show();
                }
                else if (security_question.getText().toString().equals(""))
                {
                    Toast.makeText(login_Activity.this, "Security Question Cannot be Empty!", Toast.LENGTH_SHORT).show();
                }
                else if (security_question_answer.getText().toString().equals(""))
                {
                    Toast.makeText(login_Activity.this, "Answer to Security Question Cannot be Empty!", Toast.LENGTH_SHORT).show();
                }

                String username = input_username.getText().toString();
                String password = input_password.getText().toString();
                String password_second_input = confirm_password.getText().toString();
                String s_question = security_question.getText().toString();
                String s_question_answer = security_question_answer.getText().toString();

                boolean is_password_valid = validate_password_signup(password, password_second_input);

                if (is_password_valid) {
                    is_username_exist(username, password, s_question, s_question_answer);
                }

                alert_dialog.dismiss();

            }
        });

        alert_dialog.show();
    }



    public void authentication(String username, String password) {
        DatabaseReference user_database = mDatabase.child("users");
        user_database.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean is_user_exist = false;
                boolean is_password_correct = false;

                if (!username.equals(""))
                {
                    if (snapshot.child(username).exists())
                    {
                        is_user_exist = true;
                    }
                }


                if (!password.equals("") && is_user_exist == true)
                {
                    if (snapshot.child(username).child("password").getValue().toString().equals(password))
                    {
                        is_password_correct = true;

                    }
                }

                if(is_user_exist && is_password_correct)
                {
                    //TODO setup the charater index, score, friend_list and gold from firebase
                    Appearance.current_hair_index = Integer.parseInt(snapshot.child(username).child("Character_setting").child("0").child("current_hair_index").getValue().toString());
                    Appearance.current_head_index = Integer.parseInt(snapshot.child(username).child("Character_setting").child("0").child("current_head_index").getValue().toString());
                    Appearance.current_eye_index = Integer.parseInt(snapshot.child(username).child("Character_setting").child("0").child("current_eye_index").getValue().toString());
                    Appearance.current_nose_index = Integer.parseInt(snapshot.child(username).child("Character_setting").child("0").child("current_nose_index").getValue().toString());
                    Appearance.current_mouth_index = Integer.parseInt(snapshot.child(username).child("Character_setting").child("0").child("current_mouth_index").getValue().toString());
                    Appearance.current_l_eye_brow_index = Integer.parseInt(snapshot.child(username).child("Character_setting").child("0").child("current_l_eye_brow_index").getValue().toString());
                    Appearance.current_r_eye_brow_index = Integer.parseInt(snapshot.child(username).child("Character_setting").child("0").child("current_r_eye_brow_index").getValue().toString());
                    Appearance.current_ear_index = Integer.parseInt(snapshot.child(username).child("Character_setting").child("0").child("current_ear_index").getValue().toString());
                    Appearance.current_chest_wear_index = Integer.parseInt(snapshot.child(username).child("Character_setting").child("0").child("current_chest_wear_index").getValue().toString());
                    Appearance.current_arm_wear_index = Integer.parseInt(snapshot.child(username).child("Character_setting").child("0").child("current_arm_wear_index").getValue().toString());
                    Appearance.current_shoulder_wear_index = Integer.parseInt(snapshot.child(username).child("Character_setting").child("0").child("current_shoulder_wear_index").getValue().toString());
                    Appearance.current_leg_wear_index = Integer.parseInt(snapshot.child(username).child("Character_setting").child("0").child("current_leg_wear_index").getValue().toString());
                    Appearance.current_thigh_wear_index = Integer.parseInt(snapshot.child(username).child("Character_setting").child("0").child("current_thigh_wear_index").getValue().toString());
                    Appearance.current_bottom_wear_index = Integer.parseInt(snapshot.child(username).child("Character_setting").child("0").child("current_bottom_wear_index").getValue().toString());
                    Appearance.current_foot_wear_index = Integer.parseInt(snapshot.child(username).child("Character_setting").child("0").child("current_foot_wear_index").getValue().toString());
                    Appearance.current_chest_index = Integer.parseInt(snapshot.child(username).child("Character_setting").child("0").child("current_chest_index").getValue().toString());

                    String s_question = snapshot.child(username).child("security_question").getValue().toString();
                    String s_question_answer = snapshot.child(username).child("security_answer").getValue().toString();
                    int highest_score =  Integer.parseInt(snapshot.child(username).child("highest_score").getValue().toString());
                    int num_of_gold = Integer.parseInt(snapshot.child(username).child("num_of_gold").getValue().toString());
                    login_user = new User(username, password, s_question, s_question_answer, CLIENT_REGISTRATION_TOKEN, highest_score, num_of_gold);

                    ArrayList<String> temp_array_list;
                    temp_array_list = new ArrayList<String>();

                    if(snapshot.child(username).child("friend_list").exists()) {
                        for (DataSnapshot pss : snapshot.child(username).child("friend_list").getChildren()) {
                            temp_array_list.add(pss.getValue().toString());
                        }
                    }

                    login_user.friend_list = temp_array_list;


                    sleep(500);
                    activate_main_activity();
                }
                else {
                    String content = "Username and password do not match or Username does not exist";
                    Toast.makeText(login_Activity.this, content, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    public void is_username_exist(String username, String password, String s_question, String s_question_answer) {
        DatabaseReference user_database = mDatabase.child("users");
        user_database.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!username.equals("") )
                {
                    if (!snapshot.child(username).exists())
                    {
                        User user = new User(username, password, s_question, s_question_answer, CLIENT_REGISTRATION_TOKEN);
                        Task signup_user = mDatabase.child("users").child(username).setValue(user);
                        sleep(1000);
                        Toast.makeText(login_Activity.this, "Successfully Signed up!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(login_Activity.this, "This Username already exist, Please use another one", Toast.LENGTH_SHORT).show();
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

    public boolean validate_password_signup(String password, String password_confirm)
    {
        boolean is_contain_uppercase = false;
        boolean is_contain_number = false;
        boolean is_contain_lowercase = false;
        boolean is_length_valid = false;

        if (password.length() >= 8 || password.length() <= 16)
        {
            is_length_valid = true;
        }

        for (char c: password.toCharArray())
        {
            if(Character.isDigit(c))
            {
                is_contain_number = true;
            }

            else if(Character.isLowerCase(c))
            {
                is_contain_uppercase = true;
            }

            else if (Character.isUpperCase(c))
            {
                is_contain_lowercase = true;
            }
        }

        if (is_contain_uppercase && is_contain_number && is_contain_lowercase && is_length_valid)
        {
            if (password.equals(password_confirm))
            {
                return true;
            }
            else
            {
                Toast.makeText(login_Activity.this,  "Two Password inputs do not match!", Toast.LENGTH_LONG).show();
                return false;
            }

        }
        else
        {
            String content = "Invalid Password!\nPassword must contains 8- 16 characters and at least one number, one lowercase letter and uppercase letter";
            Toast.makeText(login_Activity.this,  content, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public boolean validate_password(String password)
    {
        boolean is_contain_uppercase = false;
        boolean is_contain_number = false;
        boolean is_contain_lowercase = false;
        boolean is_length_valid = false;

        if (password.length() >= 8 || password.length() <= 16)
        {
            is_length_valid = true;
        }

        for (char c: password.toCharArray())
        {
            if(Character.isDigit(c))
            {
                is_contain_number = true;
            }

            else if(Character.isLowerCase(c))
            {
                is_contain_uppercase = true;
            }

            else if (Character.isUpperCase(c))
            {
                is_contain_lowercase = true;
            }
        }

        if (is_contain_uppercase && is_contain_number && is_contain_lowercase && is_length_valid)
        {
            return true;
        }
        else
        {
            String content = "Invalid Password!\nPassword must contains 8- 16 characters and at least one number, one lowercase letter and uppercase letter";
            Toast.makeText(login_Activity.this,  content, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void activate_main_activity() {
        Intent main_intent = new Intent(this, MainActivity.class);
        startActivity(main_intent);

    }

    public void sleep(int millisecond)
    {
        try {
            Thread.sleep(millisecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}