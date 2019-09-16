package com.highgreat.sven.myapplication.core;

import android.content.Context;

import com.google.gson.Gson;
import com.highgreat.sven.myapplication.Request;
import com.highgreat.sven.myapplication.Responce;
import com.highgreat.sven.myapplication.SecondActivity;
import com.highgreat.sven.myapplication.annotation.ClassId;
import com.highgreat.sven.myapplication.bean.RequestBean;
import com.highgreat.sven.myapplication.bean.RequestParameter;
import com.highgreat.sven.myapplication.manager.HgUserManager;
import com.highgreat.sven.myapplication.service.HermesService;
import com.highgreat.sven.myapplication.utils.TypeUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Hermes {

    //得到对象
    public static final int TYPE_NEW = 0;
    //得到单例
    public static final int TYPE_GET = 1;

    private Context mContext;
    private TypeCenter typeCenter;
    private static final Hermes ourInstance = new Hermes();
    private ServiceConnectionManager serviceConnectionManager;

    Gson GSON = new Gson();

    private Hermes(){
        typeCenter = TypeCenter.getInstance();
        serviceConnectionManager = ServiceConnectionManager.getInstance();
    }

    public static Hermes getDefault(){
        return ourInstance;
    };

    public void init(Context context){
        this.mContext = context.getApplicationContext();
    }

    //----------服务端------A进程-----------
    public void register(Class<HgUserManager> clazz) {
        typeCenter.register(clazz);
    }

    //----------客户端--------B进程-----------
    public void connect(Context context, Class< ? extends HermesService> hermesServiceClass) {
        connectApp(context,null,hermesServiceClass);
    }

    private void connectApp(Context context, String packageName, Class<? extends HermesService> hermesServiceClass) {
        init(context);
        serviceConnectionManager.bind(context.getApplicationContext(),packageName,hermesServiceClass);
    }

    //主要防止方法重载  单例对象  是不需要
    public <T> T getInstance(Class<T> clazz,Object... parameters){
        Responce responce = sendRequest(HermesService.class,clazz,null,parameters);
        return getProxy(HermesService.class,clazz);
    }

    private <T> T getProxy(Class<? extends HermesService> service, Class clazz) {
        ClassLoader classLoader = service.getClassLoader();
        T proxy = (T) Proxy.newProxyInstance(classLoader,new Class<?>[]{clazz},new HermesInvocationHandler(service,clazz));
        return proxy;
    }


    private <T> Responce sendRequest(Class<HermesService> hermesServiceClass,
                                 Class<T> clazz, Method method,Object[] parameters){
        RequestBean requestBean = new RequestBean();

        if(clazz.getAnnotation(ClassId.class) == null){
            requestBean.setClassName(clazz.getName());
            requestBean.setResultClassName(clazz.getName());
        }else{
            //返回类型的全类名
            requestBean.setClassName(clazz.getAnnotation(ClassId.class).value());
            requestBean.setResultClassName(clazz.getAnnotation(ClassId.class).value());
        }

        if(method != null){
            //方法名 统一传 方法名+参数名
            requestBean.setMethodName(TypeUtils.getMethodId(method));
        }

        RequestParameter[] requestParameters = null;
        if(parameters != null && parameters.length >0){
            requestParameters = new RequestParameter[parameters.length];
            for(int i = 0;i<parameters.length;i++){
                Object parameter = parameters[i];
                String parameterClassName = parameter.getClass().getName();
                String parameterValue = GSON.toJson(parameter);

                RequestParameter requestParameter = new RequestParameter(parameterClassName,parameterValue);
                requestParameters[i] = requestParameter;
            }
        }

        if(requestParameters != null){
            requestBean.setRequestParameters(requestParameters);
        }

        //请求获取单例----》对象---------》调用对象的方法
        Request request = new Request(GSON.toJson(requestBean),TYPE_GET);
        return serviceConnectionManager.request(hermesServiceClass,request);
    }

    public <T> Responce sendObjectRequest(Class<HermesService> hermesServiceClass
            , Class<T> clazz, Method method, Object[] parameters) {

        RequestBean requestBean = new RequestBean();
        String className = null;
        if(clazz.getAnnotation(ClassId.class) == null){
            requestBean.setClassName(clazz.getName());
            requestBean.setResultClassName(clazz.getName());
        }else{
            requestBean.setClassName(clazz.getAnnotation(ClassId.class).value());
            requestBean.setResultClassName(clazz.getAnnotation(ClassId.class).value());
        }

        if(method != null){
            requestBean.setMethodName(TypeUtils.getMethodId(method));
        }

        RequestParameter[] requestParameters = null;
        if (parameters != null && parameters.length > 0) {
            requestParameters = new RequestParameter[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Object parameter = parameters[i];
                String parameterClassName = parameter.getClass().getName();
                String parameterValue = GSON.toJson(parameter);

                RequestParameter requestParameter = new RequestParameter(parameterClassName, parameterValue);
                requestParameters[i] = requestParameter;
            }
        }

        if (requestParameters != null) {
            requestBean.setRequestParameter(requestParameters);
        }

        //        请求获取单例 ----》对象 ----------》调用对象的方法
        Request request = new Request(GSON.toJson(requestBean),TYPE_NEW);
        return serviceConnectionManager.request(hermesServiceClass, request);
    }

}
