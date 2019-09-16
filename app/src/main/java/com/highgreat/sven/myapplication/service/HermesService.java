package com.highgreat.sven.myapplication.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.highgreat.sven.myapplication.MyEventBusService;
import com.highgreat.sven.myapplication.Request;
import com.highgreat.sven.myapplication.Responce;
import com.highgreat.sven.myapplication.core.Hermes;
import com.highgreat.sven.myapplication.responce.InstanceResponceMake;
import com.highgreat.sven.myapplication.responce.ObjectResponceMake;
import com.highgreat.sven.myapplication.responce.ResponceMake;

public class HermesService extends Service{
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private MyEventBusService.Stub mBinder = new MyEventBusService.Stub() {
        @Override
        public Responce send(Request request) throws RemoteException {
            //队请求参数进行处理  生成Responce结果返回
            ResponceMake responceMake = null;
            switch (request.getType()){
                case Hermes.TYPE_GET://获取单例
                    responceMake = new InstanceResponceMake();
                    break;
                case Hermes.TYPE_NEW:
                    responceMake = new ObjectResponceMake();
                    break;
            }
            return responceMake.makeResponce(request);
        }
    };

}
