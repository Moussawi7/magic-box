package com.example.apple.test;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    private EditText elementFullName;
    private EditText elementCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        elementFullName = (EditText) findViewById(R.id.editTextFullName);
        elementCode = (EditText) findViewById(R.id.editTextCode);
    }

    public void onLogin(View view) {
        String fullName = elementFullName.getText().toString();
        String code = elementCode.getText().toString();
        Log.d("###",fullName+"-"+code);
    }

}
