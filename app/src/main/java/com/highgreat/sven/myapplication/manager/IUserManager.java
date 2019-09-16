package com.highgreat.sven.myapplication.manager;

import com.highgreat.sven.myapplication.Friend;
import com.highgreat.sven.myapplication.annotation.ClassId;

@ClassId("com.highgreat.sven.myapplication.manager.HgUserManager")
public interface IUserManager {

    public Friend getFriend();
    public void setFriend(Friend friend);

}
