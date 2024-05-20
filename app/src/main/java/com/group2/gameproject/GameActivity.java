package com.group2.gameproject;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.snackbar.Snackbar;
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

    private boolean isCallChangeCB = true;

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
        binding.sb1.setEnabled(false);
        binding.sb2.setEnabled(false);
        binding.sb3.setEnabled(false);
        setDefaultState();
        updateMoneyUI();
        setUpDialog();
        binding.cb1.setOnCheckedChangeListener((v,isChecked) -> onSelectChangeHorseBet(1, isChecked));
        binding.cb2.setOnCheckedChangeListener((v,isChecked) -> onSelectChangeHorseBet(2, isChecked));
        binding.cb3.setOnCheckedChangeListener((v,isChecked) -> onSelectChangeHorseBet(3, isChecked));
        reset();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private int getHorseBetValue(int horseNum){
        String textValue = "";
        switch (horseNum){
            case 1:{
                textValue = binding.tvBet1.getText().toString();
                break;
            }
            case 2:{
                textValue = binding.tvBet2.getText().toString();
                break;
            }
            case 3:{
                textValue = binding.tvBet3.getText().toString();
                break;
            }
        }
        return Integer.parseInt(textValue.substring(0,textValue.indexOf("$")));
    }

    private void onSelectChangeHorseBet(int horseNum, boolean isBet){
        if(isCallChangeCB){
            if(isBet){
                selectedHorseBet(horseNum);
            }else {
                unSelectHorseBet(horseNum);
            }
        }

    }

    private void setChecked(int horseNum,boolean isChecked){
        switch (horseNum){
            case 1:{
                binding.cb1.setChecked(isChecked);
                break;
            }
            case 2:{
                binding.cb2.setChecked(isChecked);
                break;
            }
            case 3:{
                binding.cb3.setChecked(isChecked);
                break;
            }
        }
    }

    private void unSelectHorseBet(int horseNum){
        int valueBet = getHorseBetValue(horseNum);
        currentMoney += valueBet;
        showMessage("Hủy đặt cược đội "+horseNum+": +"+valueBet+"$");

        updateMoneyUI();
    }

    private void selectedHorseBet(int horseNum) {
       int valueBet = getHorseBetValue(horseNum);
        if(valueBet>currentMoney){
            setChecked(horseNum, false);
            showMessage("Bạn không đủ tiền để đặt cược!");

        }else{
            currentMoney -= valueBet;
            showMessage("Đặt cược đội "+horseNum+": -"+valueBet+"$");
            updateMoneyUI();
        }

    }

    private void showMessage(String message){
        Snackbar snackbar = Snackbar.make(binding.llResult, message,Snackbar.LENGTH_SHORT);
        snackbar.show();
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
        dialogBinding.tvBetTitle.setText("Đặt cược đội "+editingBetNum);
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
        value = value.substring(0,value.indexOf("$"));
        dialogBinding.etChange.setText(value);
        if(isCheckedHorse(horseNum)){
            setChecked(horseNum, false);
        }

        dialog.show();
    }

    private void reset(){
        isCallChangeCB = false;
        binding.cb1.setChecked(false);
        binding.cb2.setChecked(false);
        binding.cb3.setChecked(false);
        isCallChangeCB = true;

        binding.sb1.setProgress(0);
        binding.sb2.setProgress(0);
        binding.sb3.setProgress(0);

        binding.llTop1.setVisibility(ViewGroup.INVISIBLE);
        binding.llTop2.setVisibility(ViewGroup.INVISIBLE);
        binding.llTop3.setVisibility(ViewGroup.INVISIBLE);

        //enable change
        setChangeState(true);

    }

    private void setChangeState(boolean isChange){
        binding.btnStart.setClickable(isChange);
        binding.cb1.setClickable(isChange);
        binding.cb2.setClickable(isChange);
        binding.cb3.setClickable(isChange);
        binding.ibEdit1.setClickable(isChange);
        binding.ibEdit2.setClickable(isChange);
        binding.ibEdit3.setClickable(isChange);
    }

    private void startRacing(){
        //disable
        setChangeState(false);
        binding.btnReset.setClickable(false);

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
                if(isCheckedHorse(h.horseNumber)){
                    currentMoney+= getHorseBetValue(h.horseNumber);
                    updateMoneyUI();
                }
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
                onFinishRace();
                break;
            }
        }
    }

    private boolean isCheckedHorse(int horseNum){
        boolean isChecked = false;
        switch (horseNum){
            case 1:{
                isChecked = binding.cb1.isChecked();
                break;
            }
            case 2:{
                isChecked = binding.cb2.isChecked();
                break;
            }
            case 3:{
                isChecked = binding.cb3.isChecked();
                break;
            }
        }
        return isChecked;
    }

    private void onFinishRace(){
        binding.btnReset.setClickable(true);
    }
}