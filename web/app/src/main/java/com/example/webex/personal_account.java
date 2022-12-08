package com.example.webex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class personal_account extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_account);

        Button project = (Button) findViewById(R.id.project);
        project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(personal_account.this, project.class);
                startActivity(intent1);
            }
        });

        Button allProjects = (Button) findViewById(R.id.allProjects);
        allProjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(personal_account.this, project_list.class);
                startActivity(intent1);
            }
        });

        Button web = (Button) findViewById(R.id.web2);
        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(personal_account.this, browser.class);
                startActivity(intent);
            }
        });
    }
}