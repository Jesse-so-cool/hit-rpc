package com.jesse.reflect;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author jesse hsj
 * @date 2020/10/12 14:43
 */
@Slf4j
public class CglibRefletUtils {

    public static String getServiceKey(String serviceName, String version) {
        if (version == null || version.equals("")) {
            return serviceName;
        }
        return serviceName + "-" + version;
    }

    public static Object invoke(Object o, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws InvocationTargetException {
        FastClass serviceFastClass = FastClass.create(o.getClass());
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(o, parameters);
    }

}
