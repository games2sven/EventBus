package com.highgreat.sven.myapplication.responce;

import com.google.gson.Gson;
import com.highgreat.sven.myapplication.Request;
import com.highgreat.sven.myapplication.Responce;
import com.highgreat.sven.myapplication.bean.RequestBean;
import com.highgreat.sven.myapplication.bean.RequestParameter;
import com.highgreat.sven.myapplication.core.ObjectCenter;
import com.highgreat.sven.myapplication.core.TypeCenter;

public abstract  class ResponceMake {

    //HgUserManage  的Class
    protected Class<?> reslutClass;
    // getInstance()  参数数组
    protected Object[] mParameters;

    Gson GSON = new Gson();
    protected TypeCenter typeCenter = TypeCenter.getInstance();

    protected static final ObjectCenter OBJECT_CENTER = ObjectCenter.getInstance();


    protected abstract void setMethod(RequestBean requestBean);
    protected  abstract Object invokeMethod();

    public Responce makeResponce(Request request){
        RequestBean requestBean = GSON.fromJson(request.getData(),RequestBean.class);
        reslutClass = typeCenter.getClassType(requestBean.getResultClassName());
        //参数还原
        RequestParameter[]  requestParameters = requestBean.getRequestParameters();
        if(requestParameters != null && requestParameters.length > 0){
            mParameters = new Object[requestParameters.length];
            for(int i = 0;i<requestParameters.length;i++){
                RequestParameter requestParameter = requestParameters[i];
                Class<?> clazz = typeCenter.getClassType(requestParameter.getParameterClassName());
                mParameters[i] = GSON.fromJson(requestParameter.getParameterValue(),clazz);
            }
        }else{
            mParameters = new Object[0];
        }

        setMethod(requestBean);
        Object resultObject = invokeMethod();
        ResponceBean responceBean = new ResponceBean(resultObject);
        //把得到的结果序列化成字符串
        String data = GSON.toJson(responceBean);
        Responce responce = new Responce(data);
        return responce;
    }

}
