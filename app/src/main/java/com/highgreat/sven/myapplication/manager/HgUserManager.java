package com.highgreat.sven.myapplication.manager;

import com.highgreat.sven.myapplication.Friend;
import com.highgreat.sven.myapplication.annotation.ClassId;

@ClassId("com.highgreat.sven.myapplication.manager.HgUserManager")
public class HgUserManager implements IUserManager{

    Friend friend;
    private static HgUserManager sInstance = null;
    private HgUserManager(){

    }

    //约定这个进程A  单例对象的  规则 getInstance()
    public static synchronized HgUserManager getInstance(){
        if(sInstance == null){
            sInstance = new HgUserManager();
        }
        return  sInstance;
    }


    @Override
    public Friend getFriend() {
        return friend;
    }

    @Override
    public void setFriend(Friend friend) {
        this.friend = friend;
    }
}
