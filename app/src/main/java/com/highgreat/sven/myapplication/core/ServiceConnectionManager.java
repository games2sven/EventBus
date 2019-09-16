package com.highgreat.sven.myapplication.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.highgreat.sven.myapplication.MyEventBusService;
import com.highgreat.sven.myapplication.Request;
import com.highgreat.sven.myapplication.Responce;
import com.highgreat.sven.myapplication.service.HermesService;

import java.util.concurrent.ConcurrentHashMap;

public class ServiceConnectionManager {

    public static  ServiceConnectionManager ourInstance;

    private final ConcurrentHashMap<Class<? extends HermesService>,MyEventBusService>
            mHermesServices = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Class<? extends HermesService>,HermesServiceConnection>
                    mHermesServiceConnection = new ConcurrentHashMap<>();

    private ServiceConnectionManager(){

    }

    public synchronized static ServiceConnectionManager getInstance(){
        if(ourInstance == null){
            ourInstance = new ServiceConnectionManager();
        }
        return ourInstance;
    }

    public void bind(Context context, String packageName, Class<? extends  HermesService> hermesServiceClass){
        HermesServiceConnection hermesServiceConnection = new HermesServiceConnection(hermesServiceClass);
        mHermesServiceConnection.put(hermesServiceClass,hermesServiceConnection);
        Intent intent;
        if(TextUtils.isEmpty(packageName)){
            intent = new Intent(context,hermesServiceClass);
        }else{
            intent = new Intent();
            intent.setClassName(packageName,hermesServiceClass.getName());
        }
        context.bindService(intent,hermesServiceConnection,Context.BIND_AUTO_CREATE);
    }

    public Responce request(Class<HermesService> hermesServiceClass, Request request){
        //从缓存中获取binder代理对象，发送请求
        MyEventBusService eventBusService = mHermesServices.get(hermesServiceClass);
        if(eventBusService != null){
            try {
                Responce responce = eventBusService.send(request);
                return responce;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private class HermesServiceConnection implements ServiceConnection{
        private Class<? extends HermesService> mClass;

        HermesServiceConnection(Class<? extends HermesService> service){this.mClass = service;};
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyEventBusService myEventBusService = MyEventBusService.Stub.asInterface(service);
            mHermesServices.put(mClass,myEventBusService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mHermesServices.remove(mClass);
        }
    }

}
