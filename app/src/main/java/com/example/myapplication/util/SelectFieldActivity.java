package com.example.myapplication.util;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.database.UserDBHelper;
import com.example.myapplication.entity.UserManager;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SelectFieldActivity extends AppCompatActivity {

    private final HashMap<MaterialButton, Boolean> selectionState = new HashMap<>();
    private final float selectedAlpha = 1.0f; // 气泡被选中时的透明度
    private final float unselectedAlpha = 0.5f; // 气泡未选中时的透明度
    private UserDBHelper userDBHelper;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_field);

        userDBHelper = UserDBHelper.getInstance(this);

        userManager = UserManager.getInstance();

        MaterialButton chipCulture = findViewById(R.id.chip_culture);
        MaterialButton chipScience = findViewById(R.id.chip_science);
        MaterialButton chipTechnology = findViewById(R.id.chip_technology);
        MaterialButton chipSports = findViewById(R.id.chip_sports);
        MaterialButton chipArt = findViewById(R.id.chip_art);
        Button btnConfirm = findViewById(R.id.btn_confirm);
        TextView jump = findViewById(R.id.jump);

        // 初始化气泡按钮
        setupButton(chipCulture);
        setupButton(chipScience);
        setupButton(chipTechnology);
        setupButton(chipSports);
        setupButton(chipArt);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 确认选择后更新用户的兴趣领域
                List<String> selectedFields = new LinkedList<>();
                if (selectionState.get(chipCulture)) {
                    selectedFields.add("culture");
                }
                if (selectionState.get(chipScience)) {
                    selectedFields.add("science");
                }
                if (selectionState.get(chipTechnology)) {
                    selectedFields.add("technology");
                }
                if (selectionState.get(chipSports)) {
                    selectedFields.add("sports");
                }
                if (selectionState.get(chipArt)) {
                    selectedFields.add("art");
                }
                String interests = String.join(",", selectedFields);
                if (interests.isEmpty()) {
                    Toast.makeText(SelectFieldActivity.this, "您还未选择兴趣领域", Toast.LENGTH_SHORT).show();
                    return;
                }
                userManager.setInterests(interests);
                userDBHelper.update(userManager);

                Intent intent = new Intent(SelectFieldActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String intrests = "none";
                userManager.setInterests(intrests);
                userDBHelper.update(userManager);

                Intent intent = new Intent(SelectFieldActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void setupButton(MaterialButton button) {
        selectionState.put(button, false); // 初始未选中
        button.setAlpha(unselectedAlpha);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSelected = selectionState.get(button);
                selectionState.put(button, !isSelected); // 切换状态
                button.setAlpha(isSelected ? unselectedAlpha : selectedAlpha);
            }
        });
    }

}