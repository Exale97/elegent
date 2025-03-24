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

public class LoginActivity extends AppCompatActivity {

    private EditText etPhoneNum, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;
    private UserDBHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etPhoneNum = findViewById(R.id.etPhoneNum);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

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
                User user = mHelper.queryByPhoneNum(phoneNum);
                if (user == null) {
                    Toast.makeText(this, R.string.phone_num_not_exist, Toast.LENGTH_SHORT).show();
                } else if (!user.getPassword().equals(password)) {
                    Toast.makeText(this, R.string.password_not_match, Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
    protected void onStart() {
        super.onStart();
        mHelper = UserDBHelper.getInstance(this);
        mHelper.openWriteLink();
        mHelper.openReadLink();
        mHelper.insert(new User("admin", "12345678901", "123456"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHelper.closeLink();
    }
}
