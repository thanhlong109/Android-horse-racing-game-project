package com.group2.gameproject;

import java.util.ArrayList;
import java.util.List;

public class Data {
    private static List<Account> AccountList = new ArrayList<>();
    private static Account currentAcc;
    public static void SignUp(Account account){
        AccountList.add(account);
    }

    public static Account SignIn(String username, String password){
        for(Account acc : AccountList){
            if(acc.password.equals(password) && acc.username.equals(username) ){
                currentAcc = acc;
                return acc;
            }
        }
        return null;
    }
    public static Account getCurrentAcc(){
        return currentAcc;
    }
}
