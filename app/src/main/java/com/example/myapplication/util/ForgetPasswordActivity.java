package com.example.myapplication.util;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.database.UserDBHelper;
import com.example.myapplication.entity.User;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText etPhoneNum;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnSubmit;
    private UserDBHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        etPhoneNum = findViewById(R.id.etPhoneNum);
        etPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmNewPassword);
        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {
            String phoneNum = etPhoneNum.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            User user = mHelper.queryByPhoneNum(phoneNum);

            if (phoneNum.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, R.string.complete_info, Toast.LENGTH_SHORT).show();
            } else if (phoneNum.length() != 11) {
                Toast.makeText(this, R.string.phone_num_not_valid, Toast.LENGTH_SHORT).show();
            } else if (user == null) {
                Toast.makeText(this, R.string.phone_num_not_exist, Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(this, R.string.password_length_not_valid, Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, R.string.confirm_password_not_match, Toast.LENGTH_SHORT).show();
            } else {
                user.setPassword(password);
                mHelper.update(user);
                Toast.makeText(this, R.string.password_update_success, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mHelper = UserDBHelper.getInstance(this);
    }
}