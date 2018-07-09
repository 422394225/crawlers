package core.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class ReflectUtils {
    private static Logger logger = LoggerFactory.getLogger(ReflectUtils.class);

    public static Object invokeMethod(String methodPath,Object...params){
        if(StringUtils.isEmpty(methodPath)){
            logger.error("反射出错,未获取到方法名");
            return null;
        }
        Object result = null;
        try {
            String className = null;
            String methodName = null;
            if(StringUtils.contains(methodPath,".")){
                className = StringUtils.substringBeforeLast(methodPath,".");
                methodName = StringUtils.substringAfterLast(methodPath,".");
            }else{
                throw new ClassNotFoundException("请检查方法名,eg com.package.Class.method");
            }
            if(StringUtils.isAnyEmpty(className,methodName)){
                throw new ClassNotFoundException("未获取到方法名,methodPath:"+methodPath+" className:"+className+" methodName:"+methodName);
            }
            Class clazz = Class.forName(className);
            Method method = clazz.getDeclaredMethod(methodName);
            Object  instance = clazz.newInstance();
            result = method.invoke(instance,params);
        } catch (Exception e) {
            logger.error("反射出错",e);
        }
        return result;
    }
}
