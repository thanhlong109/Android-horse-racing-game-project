package com.group2.gameproject;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.group2.gameproject.databinding.ActivityGameBinding;
import com.group2.gameproject.databinding.BetPopUpBinding;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private ActivityGameBinding binding;
    private int defaultBetValue = 10;
    private int defaultMoneyValue = 100;
    private int currentMoney = defaultMoneyValue;
    Dialog dialog;
    BetPopUpBinding dialogBinding;
    private int editingBetNum = 0;

    TextView txtUsername;
    GoogleSignInOptions gOptions;
    GoogleSignInClient gClient;

    private ArrayList<HorseData> horseDatas;
    private ArrayList<Integer> selectedList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //link to view
        binding = ActivityGameBinding.inflate(getLayoutInflater());


        setContentView(binding.getRoot());

        txtUsername = (TextView) findViewById(R.id.userName);

        gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gClient = GoogleSignIn.getClient(this, gOptions);

        GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(gAccount != null){
            String loginName = gAccount.getDisplayName();
            txtUsername.setText(loginName);
        }

        //set up ui
        binding.btnStart.setOnClickListener(v -> startRacing());
        binding.btnReset.setOnClickListener(v -> reset());
        binding.ibEdit1.setOnClickListener(v -> showBetDialog(1));
        binding.ibEdit2.setOnClickListener(v -> showBetDialog(2));
        binding.ibEdit3.setOnClickListener(v -> showBetDialog(3));
        setDefaultState();
        updateMoneyUI();
        setUpDialog();
        reset();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updateMoneyUI(){
        binding.tvTotalMoney.setText(currentMoney+"$");
    }

    private void setDefaultState(){
        binding.tvBet1.setText(defaultBetValue+"$");
        binding.tvBet2.setText(defaultBetValue+"$");
        binding.tvBet3.setText(defaultBetValue+"$");
    }

    private void setUpDialog(){
        dialogBinding = BetPopUpBinding.inflate(getLayoutInflater());
        dialog = new Dialog(this);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getColor(R.color.zxing_transparent)));

        dialogBinding.ibClose.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnBet.setOnClickListener(v -> onBetClick());
        dialogBinding.tvBetTitle.setText("Đặt cược đội "+editingBetNum);
    }

    private void onBetClick(){
        String value = dialogBinding.etChange.getText().toString()+"$";
        switch (editingBetNum){
            case 1:{
                binding.tvBet1.setText(value);
                break;
            }
            case 2:{
                binding.tvBet2.setText(value);
                break;
            }
            case 3:{
                binding.tvBet3.setText(value);
                break;
            }
        }
        dialog.dismiss();
    }

    private void showBetDialog(int horseNum){
        editingBetNum =  horseNum;
        String value = "";
        switch (horseNum){
            case 1:{
                value = binding.tvBet1.getText().toString();
                break;
            }
            case 2:{
                value = binding.tvBet2.getText().toString();
                break;
            }
            case 3:{
                value = binding.tvBet3.getText().toString();
                break;
            }
        }

        dialogBinding.etChange.setText(value.substring(0,value.indexOf("$")));
        dialog.show();
    }

    private void reset(){

        binding.cb1.setActivated(false);
        binding.cb2.setActivated(false);
        binding.cb3.setActivated(false);

        binding.sb1.setProgress(0);
        binding.sb2.setProgress(0);
        binding.sb3.setProgress(0);

        binding.llTop1.setVisibility(ViewGroup.INVISIBLE);
        binding.llTop2.setVisibility(ViewGroup.INVISIBLE);
        binding.llTop3.setVisibility(ViewGroup.INVISIBLE);

    }

    private void startRacing(){
        binding.sb1.setMax(1000);
        binding.sb2.setMax(1000);
        binding.sb3.setMax(1000);
        horseDatas = new ArrayList<>();
        runRace(binding.sb1,1);
        runRace(binding.sb2,2);
        runRace(binding.sb3,3);
    }

    private void runRace(final SeekBar seekBar, int horseNum) {
        final Random random = new Random();
        final long startTime = System.currentTimeMillis();
        HorseData horseData = new HorseData();
        horseData.horseNumber = horseNum;
        new Thread(() -> {
            while (seekBar.getProgress() < seekBar.getMax()) {
                try {
                    // Đợi 0.1 giây trước khi cập nhật lại giá trị
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // tạo ngẫu nhiên giá trị
                final int progress = seekBar.getProgress() + random.nextInt(20);
                // Cập nhật giá trị trên UI
                runOnUiThread(() -> seekBar.setProgress(Math.min(progress, seekBar.getMax())));
            }
            final long endTime = System.currentTimeMillis();
            horseData.finishTime = endTime - startTime;
            horseDatas.add(horseData);
            runOnUiThread(() -> updateRanking());

        }).start();
    }

    private void updateRanking(){
        switch (horseDatas.size()){
            case 1:{
                HorseData h = horseDatas.get(0);
                binding.tvResult1.setText("Đội "+h.horseNumber+" - Thời gian: "+(h.finishTime/1000.0) +" Giây");
                binding.llTop1.setVisibility(ViewGroup.VISIBLE);
                break;
            }
            case 2:{
                HorseData h = horseDatas.get(1);
                binding.tvResult2.setText("Đội "+h.horseNumber+" - Thời gian: "+(h.finishTime/1000.0) +" Giây");
                binding.llTop2.setVisibility(ViewGroup.VISIBLE);
                break;
            }
            case 3:{
                HorseData h = horseDatas.get(2);
                binding.tvResult3.setText("Đội "+h.horseNumber+" - Thời gian: "+(h.finishTime/1000.0) +" Giây");
                binding.llTop3.setVisibility(ViewGroup.VISIBLE);
                break;
            }
        }
    }
}