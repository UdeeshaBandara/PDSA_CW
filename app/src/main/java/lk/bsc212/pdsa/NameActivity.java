package lk.bsc212.pdsa;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import lk.bsc212.pdsa.model.room.User;
import lk.bsc212.pdsa.utils.AlertDialog;
import lk.bsc212.pdsa.utils.TinyDB;

public class NameActivity extends AppCompatActivity {

    TinyDB tinyDB;

    EditText userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        tinyDB = new TinyDB(NameActivity.this);

        userName = findViewById(R.id.user_name);

        findViewById(R.id.btn_add_player).setOnClickListener(view -> {

            if (TextUtils.isEmpty(userName.getText().toString()))
                new AlertDialog().negativeAlert(NameActivity.this, "Invalid Input", "Please enter your name", "OK");
            else {
                tinyDB.putBoolean("isNameSelected", true);
                AsyncTask.execute(() -> tinyDB.putLong("userId", MainApplication.userDao.insertAll(new User(userName.getText().toString()))[0]));
                startActivity(new Intent(NameActivity.this, MenuActivity.class));
                finishAffinity();
            }

        });
    }
}