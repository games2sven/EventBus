package com.highgreat.sven.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.highgreat.sven.myapplication.core.HGEventbus;
import com.highgreat.sven.myapplication.core.Hermes;
import com.highgreat.sven.myapplication.manager.IUserManager;
import com.highgreat.sven.myapplication.service.HermesService;

import org.greenrobot.eventbus.EventBus;

public class SecondActivity extends AppCompatActivity {

    IUserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Hermes.getDefault().connect(this, HermesService.class);

    }

//    public void send(View view) {
////        new Thread(new Runnable() {
////            @Override
////            public void run() {
////                EventBus.getDefault().post(new Friend("Alan", 18));
////            }
////        }).start();
////        new Thread(new Runnable() {
////            @Override
////            public void run() {
//                HGEventbus.getDefault().post(new Friend("Alan", 18));
////            }
////        }).start();
//    }

    public void userManager(View view) {
        userManager = Hermes.getDefault().getInstance(IUserManager.class);
//        downManager=Hermes.getDefault().getInstance(IDownManager.class);
    }

    public void getUser(View view) {
        //1、接收从服务端传过来的数据
        Toast.makeText(this,"-----> "+userManager.getFriend().toString(), Toast.LENGTH_SHORT).show();
//        userManager.setFriend(new Friend("jett",20));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
