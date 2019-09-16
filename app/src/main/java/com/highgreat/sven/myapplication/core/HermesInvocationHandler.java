package com.highgreat.sven.myapplication.core;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.highgreat.sven.myapplication.Responce;
import com.highgreat.sven.myapplication.responce.ResponceBean;
import com.highgreat.sven.myapplication.service.HermesService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class HermesInvocationHandler implements InvocationHandler {
    private static final String TAG = "Sven";
    private Class clazz;
    private Class hermeService;
    private static final Gson GSON = new Gson();

    public HermesInvocationHandler(Class<? extends HermesService> service,Class clazz){
        this.clazz = clazz;
        this.hermeService = service;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.i(TAG, "invoke:-------> " + method.getName());
        Responce responce = Hermes.getDefault().sendObjectRequest(hermeService,clazz,method,args);
        if(!TextUtils.isEmpty(responce.getData())){
            ResponceBean responceBean = GSON.fromJson(responce.getData(),ResponceBean.class);
            if(responceBean.getData() != null){
                Object getUserResult = responceBean.getData();
                String data = GSON.toJson(getUserResult);

                Class stringgetUser = method.getReturnType();
                Object o = GSON.fromJson(data,stringgetUser);
                return o;
            }
        }

        return null;
    }
}
