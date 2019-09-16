package com.highgreat.sven.myapplication.core;

import java.lang.reflect.Method;

//注册类中的注册方法的信息
public class SubscribleMethod {
    //方法参数类型
    private Class<?> eventType;
    //所在的线程类型
    private HGThreadMode threadMode;
    //注册方法
    private Method method;

    public SubscribleMethod(Class<?> eventType, HGThreadMode threadMode, Method method) {
        this.eventType = eventType;
        this.threadMode = threadMode;
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public HGThreadMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(HGThreadMode threadMode) {
        this.threadMode = threadMode;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public void setEventType(Class<?> eventType) {
        this.eventType = eventType;
    }
}
