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

import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPhoneNum, etPassword, etConfirmPassword;
    private UserDBHelper userDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userDBHelper = UserDBHelper.getInstance(this);

        etUsername = findViewById(R.id.etUsername);
        etPhoneNum = findViewById(R.id.etPhoneNum);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String phoneNum = etPhoneNum.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            User user = null;
            List<User> list = userDBHelper.queryAll();

            boolean flag = false;
            for (User u : list)
                if (u.getPhone().equals(phoneNum)) {
                    flag = true;
                    break;
                }

            if (username.isEmpty() || phoneNum.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, R.string.complete_info, Toast.LENGTH_SHORT).show();
            } else if (phoneNum.length() != 11) {
                Toast.makeText(this, R.string.phone_num_not_valid, Toast.LENGTH_SHORT).show();
            } else if (flag) {
                Toast.makeText(this, R.string.phone_num_exist, Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(this, R.string.password_length_not_valid, Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, R.string.confirm_password_not_match, Toast.LENGTH_SHORT).show();
            } else {
                user = new User(username, phoneNum, password);
                userDBHelper.insert(user);
                Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

    }

}
