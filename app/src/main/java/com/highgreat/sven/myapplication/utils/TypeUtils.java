package com.highgreat.sven.myapplication.utils;

import java.lang.reflect.Method;

public class TypeUtils {
    public static String getMethodId(Method method) {
        StringBuilder builder = new StringBuilder(method.getName());
        builder.append('(').append(getMethodParameters(method.getParameterTypes())).append(')');
        return builder.toString();
    }

    private static String getMethodParameters(Class<?>[] classes) {
        StringBuilder builder = new StringBuilder();
        int length = classes.length;
        if(length == 0){
            return builder.toString();
        }
        builder.append(getClassName(classes[0]));
        for (int i = 0; i < length; ++i) {
            builder.append(",").append(getClassName(classes[i]));
        }
        return builder.toString();
    }

    private static String getClassName(Class<?> clazz) {
        if (clazz == Boolean.class) {
            return "boolean";
        } else if (clazz == Byte.class) {
            return "byte";
        } else if (clazz == Character.class) {
            return "char";
        } else if (clazz == Short.class) {
            return "short";
        } else if (clazz == Integer.class) {
            return "int";
        } else if (clazz == Long.class) {
            return "long";
        } else if (clazz == Float.class) {
            return "float";
        } else if (clazz == Double.class) {
            return "double";
        } else if (clazz == Void.class) {
            return "void";
        } else {
            return clazz.getName();
        }
    }

    public static Method getMethodForGettingInstance(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
        Method[] methods = clazz.getMethods();
        Method result = null;
        if(parameterTypes == null){
            parameterTypes = new Class[0];
        }
        for (Method method:methods){
            String tmpName = method.getName();
            if(tmpName.equals("getInstance")){
                if(classAssignable(method.getParameterTypes(),parameterTypes)){
                    result = method;
                    break;
                }
            }
        }
        if(result != null){
            return result;
        }
        return null;
    }

    private static boolean classAssignable(Class<?>[] parameterTypes, Class<?>[] parameterTypes1) {
        if(parameterTypes.length != parameterTypes1.length){
            return false;
        }
        int length = parameterTypes1.length;
        for(int i = 0 ;i<length;++i){
            if(parameterTypes1[i] == null){
                continue;
            }
            if(primitiveMatch(parameterTypes[i],parameterTypes1[i])){
                continue;
            }
            if(!parameterTypes[i].isAssignableFrom(parameterTypes1[i])){
                return false;
            }
        }
        return true;
    }

    private static boolean primitiveMatch(Class<?> class1, Class<?> class2) {
        if(!class1.isPrimitive() && !class2.isPrimitive()){
            return false;
        }else if(class1 == class2){
            return true;
        }else if (class1.isPrimitive()) {
            return primitiveMatch(class2, class1);
            //class2 is primitive
            //boolean, byte, char, short, int, long, float, and double void
        } else if (class1 == Boolean.class && class2 == boolean.class) {
            return true;
        } else if (class1 == Byte.class && class2 == byte.class) {
            return true;
        } else if (class1 == Character.class && class2 == char.class) {
            return true;
        } else if (class1 == Short.class && class2 == short.class) {
            return true;
        } else if (class1 == Integer.class && class2 == int.class) {
            return true;
        } else if (class1 == Long.class && class2 == long.class) {
            return true;
        } else if (class1 == Float.class && class2 == float.class) {
            return true;
        } else if (class1 == Double.class && class2 == double.class) {
            return true;
        } else if (class1 == Void.class && class2 == void.class) {
            return true;
        } else {
            return false;
        }
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class[] paramters) {
        Method result = null;
        Method[] methods = clazz.getMethods();
        for(Method method:methods){
            if(method.getName().equals(methodName) && classAssignable(method.getParameterTypes(),paramters)){
                if(result == null){
                    result = method;
                }
            }
        }
        return  result;
    }
}
