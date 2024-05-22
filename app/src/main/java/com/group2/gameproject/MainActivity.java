package com.group2.gameproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.developer.gbuttons.GoogleSignInButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.group2.gameproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    GoogleSignInButton btnGGLogin;
    GoogleSignInOptions gOptions;
    GoogleSignInClient gClient;

    private ActivityMainBinding binding;
    private final String REQUIRE_MESSAGE = "Require";
    private final String ACC_USERNAME = "test";
    private  final  String ACC_PASS = "12345678";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding =ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Initialize the Google Sign-In button
        SetUpUI();
        btnGGLogin = findViewById(R.id.btnLogin);

        gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gClient = GoogleSignIn.getClient(this, gOptions);

        GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (gAccount != null) {
            finish();
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        }

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                            try {
                                task.getResult(ApiException.class);
                                finish();
                                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                                startActivity(intent);
                            } catch (ApiException e) {
                                Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        btnGGLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signinIntent = gClient.getSignInIntent();
                activityResultLauncher.launch(signinIntent);
            }
        });
    }

    private void SetUpUI(){
        //
        Account acc = new Account();
        acc.username = ACC_USERNAME;
        acc.password = ACC_PASS;
        Data.SignUp(acc);
        //code
        // password
        TextUtils.AddOnTextChange(binding.etSignInPassword, string -> {
            if(TextUtils.isNullOrEmpty(string)){
                binding.tilSignInPassword.setError(REQUIRE_MESSAGE);
            }else{
                binding.tilSignInPassword.setError(null);
            }
        });

        //Username
        TextUtils.AddOnTextChange(binding.etSignInUsername, string -> {
            if(TextUtils.isNullOrEmpty(string)){
                binding.tilSignInUsername.setError(REQUIRE_MESSAGE);
            }else{
                binding.tilSignInUsername.setError(null);
            }
        });

        //
        binding.tvGoToSignUp.setOnClickListener(this);
        binding.btnSignIn.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id==binding.btnSignIn.getId()){
            SignIn();
        }else if(id == binding.tvGoToSignUp.getId()){
            goToActivity(SignUpActivity.class,null);
        }
    }

    private void SignIn(){
        String username = binding.etSignInUsername.getText().toString();
        String password = binding.etSignInPassword.getText().toString();

        if(TextUtils.isNullOrEmpty(username)&& TextUtils.isNullOrEmpty(password)){
            Toast.makeText(this, "Vui lòng nhập username và password", Toast.LENGTH_SHORT).show();
        }else {
            Account acc = Data.SignIn(username,password);
            if(acc!=null){
                Bundle bundle = new Bundle();
                bundle.putString("username",username);
                goToActivity(GameActivity.class, bundle);
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Username hoặc password không đúng!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void goToActivity(Class classs, Bundle bundle){
        Intent intent = new Intent(this, classs);
        if(bundle != null)
            intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
