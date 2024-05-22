package com.group2.gameproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group2.gameproject.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity implements  View.OnClickListener{
    private ActivitySignUpBinding binding;
    private String password = "";
    private String confirmPassword = "";

    private final String REQUIRE_MESSAGE = "Require";
    private final String CONFIRM_MESSAGE = "Mật khẩu không khớp!";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //link view
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //code
        // password
        TextUtils.AddOnTextChange(binding.etSignUpPassword, string -> {
            if(TextUtils.isNullOrEmpty(string)){
                binding.tilSignUpPassword.setError(REQUIRE_MESSAGE);
            }else{
                binding.tilSignUpPassword.setError(null);
            }
        });

        // confirm password
        TextUtils.AddOnTextChange(binding.etSignUpConfirmPassword, string -> {
            if(TextUtils.isNullOrEmpty(string)){
                binding.tilSignUpConfirmPassword.setError(REQUIRE_MESSAGE);
            }else if(!string.equals(binding.etSignUpPassword.getText().toString())){
                binding.tilSignUpConfirmPassword.setError(CONFIRM_MESSAGE);
            }else{
                binding.tilSignUpConfirmPassword.setError(null);
            }
        });

        //Username
        TextUtils.AddOnTextChange(binding.etSignUpUsername, string -> {
            if(TextUtils.isNullOrEmpty(string)){
                binding.tilSignUpUsername.setError(REQUIRE_MESSAGE);
            }else{
                binding.tilSignUpUsername.setError(null);
            }
        });

        //
        binding.tvGoToSignIn.setOnClickListener(this);
        binding.btnSignUp.setOnClickListener(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void SignUp(){
        String username = binding.etSignUpUsername.getText().toString();
        String password = binding.etSignUpPassword.getText().toString();
        String confirmPassword = binding.etSignUpConfirmPassword.getText().toString();

        if(TextUtils.isNullOrEmpty(username)&& TextUtils.isNullOrEmpty(password)){
            Toast.makeText(this, "Vui lòng nhập username và password", Toast.LENGTH_SHORT).show();
        }else if(!password.equals(confirmPassword)){
            Toast.makeText(this, CONFIRM_MESSAGE, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();
            Account acc = new Account();
            acc.password = password;
            acc.username = username;
            Data.SignUp(acc);
            goToActivity(MainActivity.class);
            finish();
        }
    }

    private void goToActivity(Class classs){
        Intent intent = new Intent(this, classs);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == binding.btnSignUp.getId()){
            SignUp();
        }else if(id == binding.tvGoToSignIn.getId()){
            goToActivity(MainActivity.class);
        }
    }
}