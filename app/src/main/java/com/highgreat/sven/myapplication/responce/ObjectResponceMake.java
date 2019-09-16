package com.highgreat.sven.myapplication.responce;

import com.highgreat.sven.myapplication.bean.RequestBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ObjectResponceMake extends ResponceMake{

    private Method mMethod;

    private Object mObject;


    @Override
    protected void setMethod(RequestBean requestBean) {
        mObject = OBJECT_CENTER.getObject(reslutClass.getName());
        Method method = typeCenter.getMethod(mObject.getClass(),requestBean);
        mMethod = method;
    }

    @Override
    protected Object invokeMethod() {
        Exception exception;
        try {
            return mMethod.invoke(mObject,mParameters);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
