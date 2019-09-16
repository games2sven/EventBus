package com.highgreat.sven.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.highgreat.sven.myapplication.core.HGEventbus;
import com.highgreat.sven.myapplication.core.HGSubscribe;
import com.highgreat.sven.myapplication.core.HGThreadMode;
import com.highgreat.sven.myapplication.core.Hermes;
import com.highgreat.sven.myapplication.manager.HgUserManager;


public class MainActivity extends AppCompatActivity {

    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        EventBus.getDefault().register(this);
//        HGEventbus.getDefault().register(this);
//        textView = (TextView) findViewById(R.id.tv);

        //为什么要初始化（在进程中，单例存在不同，所以需要初始化）
        Hermes.getDefault().init(this);
        Hermes.getDefault().register(HgUserManager.class);
        HgUserManager.getInstance().setFriend(new Friend("Sven",18));

    }

    public void change(View view) {
        startActivity(new Intent(this, SecondActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        EventBus.getDefault().unregister(this);
        HGEventbus.getDefault().unregister(this);
    }

    @HGSubscribe(threadMode = HGThreadMode.MAIN)
    public void test(Friend friend){
        Toast.makeText(this, friend.toString(), Toast.LENGTH_SHORT).show();
    }

}
