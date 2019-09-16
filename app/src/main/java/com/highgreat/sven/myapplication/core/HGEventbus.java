package com.highgreat.sven.myapplication.core;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HGEventbus {

    private static HGEventbus mInstance;
    private Map<Object,List<SubscribleMethod>> cacheMap;
    private Handler handler;
    private ExecutorService executorService;//线程池

    private HGEventbus(){
        //缓存
        this.cacheMap = new HashMap<>();
        handler = new Handler();
        executorService = Executors.newCachedThreadPool();

    }

    public static HGEventbus getDefault(){
        if(mInstance == null ){
            synchronized (HGEventbus.class){
                mInstance = new HGEventbus();
            }
        }

        return mInstance;
    }


    public void register(Object subscriber){
        Class<?> aClass = subscriber.getClass();
        List<SubscribleMethod> subscribleMethods = cacheMap.get(subscriber);
       //如果已经注册，就不需要注册
        if(subscribleMethods == null){
            subscribleMethods = getSubscribleMethods(subscriber);
            cacheMap.put(subscriber,subscribleMethods);
        }
    }

    //遍历能够接收事件的方法
    private List<SubscribleMethod> getSubscribleMethods(Object subscriber) {
        List<SubscribleMethod> list = new ArrayList<>();
        Class<?> aClass = subscriber.getClass();
        while(aClass != null){
            //判断分类是在那个报下，（如果是系统的就不需要）
            String name = aClass.getName();
            if (name.startsWith("java.") ||
                    name.startsWith("javax.") ||
                    name.startsWith("android.") ||
                    name.startsWith("androidx.")) {
                break;
            }

            //获取当前类的所有方法
            Method[] declaredMethods = aClass.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                //当前方法是不是包含指定注解
                HGSubscribe annotation = declaredMethod.getAnnotation(HGSubscribe.class);
                if(annotation == null){
                    continue;
                }

                //获取当前方法的参数 检测这个方法是不是只有一个参数
                Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
                if(parameterTypes.length != 1){
                    throw new RuntimeException("eventbus只能接收一个参数");
                }

                //符合要求的方法
                HGThreadMode hgThreadMode = annotation.threadMode();
                SubscribleMethod subscribleMethod = new SubscribleMethod(parameterTypes[0],hgThreadMode,declaredMethod);
                list.add(subscribleMethod);
            }
            aClass = aClass.getSuperclass();
        }

        return list;
    }

    public void post(final Object obj){
        Set<Object> objects = cacheMap.keySet();
        Iterator<Object> iterator = objects.iterator();
        while (iterator.hasNext()){
            //拿到注册类
            final Object next = iterator.next();
            //获取类中的所有添加注解的方法
            List<SubscribleMethod> subscribleMethods = cacheMap.get(next);
            for (final SubscribleMethod subscribleMethod : subscribleMethods) {
                //判断这个方法是否应该接受事件(方法参数是我post上指定的参数)
                if(subscribleMethod.getEventType().isAssignableFrom(obj.getClass())){
                    switch (subscribleMethod.getThreadMode()){
                        case MAIN://接受方法在主线程运行
                            if(Looper.myLooper() == Looper.getMainLooper()){
                                //当前线程是主线程（直接反射调用）
                                invoke(subscribleMethod,next,obj);
                            }else{
                                //post方法执行在子线程中,接受消息在主线程中
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscribleMethod,next,obj);
                                    }
                                });
                            }
                            break;
                        case ASYNC://接收方法在子线程
                            if(Looper.myLooper() == Looper.getMainLooper()){
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscribleMethod,next,obj);
                                    }
                                });
                            }else{
                                //post方法执行在子线程中
                                invoke(subscribleMethod,next,obj);
                            }
                            break;
                        case POSTING:
                            break;
                    }
                }

            }

        }
    }

    //反射调用注册方法
    private void invoke(SubscribleMethod subscribleMethod,Object object,Object obj){
        //反射方法对象
        Method method = subscribleMethod.getMethod();
        try {
            //类对象，参数
            method.invoke(object,obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    //取消注册
    public void unregister(Object subscriber){
        Class<?> aClass = subscriber.getClass();
        List<SubscribleMethod> subscribleMethods = cacheMap.get(subscriber);
        //如果能取到
        if(subscribleMethods != null){
            cacheMap.remove(subscriber);
        }
    }


}
