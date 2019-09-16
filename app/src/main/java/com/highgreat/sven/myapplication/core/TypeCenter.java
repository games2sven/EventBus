package com.highgreat.sven.myapplication.core;

import android.text.TextUtils;

import com.highgreat.sven.myapplication.bean.RequestBean;
import com.highgreat.sven.myapplication.bean.RequestParameter;
import com.highgreat.sven.myapplication.manager.HgUserManager;
import com.highgreat.sven.myapplication.utils.TypeUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class TypeCenter {

    private static final  TypeCenter ourInstance = new TypeCenter();

    //name String
    private final ConcurrentHashMap<String, Class<?>> mAnnotatedClasses;
    //类对应的方法
    private final ConcurrentHashMap<Class<?>,ConcurrentHashMap<String,Method>> mRawMethods;
    //为了减少反射，所以缓存起来

    private TypeCenter(){
        mAnnotatedClasses = new ConcurrentHashMap<>();
        mRawMethods = new ConcurrentHashMap<>();
    }

    public static TypeCenter getInstance(){
        return ourInstance;
    }


    public void register(Class<HgUserManager> clazz) {
        //注册---类 注册----方法
        registerClass(clazz);
        registerMethod(clazz);
    }

    private void registerMethod(Class<HgUserManager> clazz) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            mRawMethods.putIfAbsent(clazz,new ConcurrentHashMap<String, Method>());
            ConcurrentHashMap<String,Method> map = mRawMethods.get(clazz);
            String methodId = TypeUtils.getMethodId(method);
            map.put(methodId,method);
        }
    }

    private void registerClass(Class<HgUserManager> clazz) {
        String name = clazz.getName();
        mAnnotatedClasses.putIfAbsent(name,clazz);
    }

    public Class<?> getClassType(String name){
        if(TextUtils.isEmpty(name)){
            return null;
        }
        Class<?> clazz = mAnnotatedClasses.get(name);
        if(clazz == null){
            try{
                clazz = Class.forName(name);
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }
        }
        return clazz;
    }

    public Method getMethod(Class<?> clazz , RequestBean requestBean){
        String name = requestBean.getMethodName();
        if(name!=null){
            mRawMethods.putIfAbsent(clazz,new ConcurrentHashMap<String, Method>());
            ConcurrentHashMap<String,Method> methods = mRawMethods.get(clazz);
            Method method = methods.get(name);
            if(method != null){
                return method;
            }

            int pos = name.indexOf('(');
            Class[] paramters = null;
            RequestParameter[] requestParameters = requestBean.getRequestParameter();
            if(requestParameters != null && requestParameters.length > 0){
                paramters=new Class[requestParameters.length];
                for (int i=0;i<requestParameters.length;i++) {
                    paramters[i] = getClassType(requestParameters[i].getParameterClassName());
                }
            }
            method = TypeUtils.getMethod(clazz,name.substring(0,pos),paramters);
            methods.put(name,method);
            return method;
        }
        return null;
    }


}
