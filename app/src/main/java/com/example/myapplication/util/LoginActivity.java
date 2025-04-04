package com.example.myapplication.util;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.database.UserDBHelper;
import com.example.myapplication.entity.User;
import com.example.myapplication.entity.UserManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etPhoneNum, etPassword;
    private UserDBHelper userDBHelper;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userDBHelper = UserDBHelper.getInstance(this);
        userDBHelper.openWriteLink();
        userDBHelper.openReadLink();
        if (userDBHelper.queryByPhoneNum("12345678901") == null) userDBHelper.insert(new User("admin", "12345678901", "123456"));

        userManager = UserManager.getInstance();

        etPhoneNum = findViewById(R.id.etPhoneNum);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegister = findViewById(R.id.tvRegister);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            String phoneNum = etPhoneNum.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (phoneNum.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.complete_info, Toast.LENGTH_SHORT).show();
            } else {
                User user = userDBHelper.queryByPhoneNum(phoneNum);
                if (user == null) {
                    Toast.makeText(this, R.string.phone_num_not_exist, Toast.LENGTH_SHORT).show();
                } else if (!user.getPassword().equals(password)) {
                    Toast.makeText(this, R.string.password_not_match, Toast.LENGTH_SHORT).show();
                } else {
                    userManager.setUser(user);
                    Intent intent;
//                    if (user.getInterests().isEmpty()) intent = new Intent(LoginActivity.this, SelectFieldActivity.class);
//                    else intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent = new Intent(LoginActivity.this, SelectFieldActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userDBHelper.closeLink();
    }

}