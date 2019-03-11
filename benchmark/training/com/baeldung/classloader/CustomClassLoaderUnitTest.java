package com.baeldung.classloader;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.Test;


public class CustomClassLoaderUnitTest {
    @Test
    public void customLoader() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        CustomClassLoader customClassLoader = new CustomClassLoader();
        Class<?> c = customClassLoader.findClass(PrintClassLoader.class.getName());
        Object ob = c.newInstance();
        Method md = c.getMethod("printClassLoaders");
        md.invoke(ob);
    }
}

