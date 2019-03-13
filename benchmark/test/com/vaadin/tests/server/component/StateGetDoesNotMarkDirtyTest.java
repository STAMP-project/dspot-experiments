package com.vaadin.tests.server.component;


import com.vaadin.tests.VaadinClasses;
import com.vaadin.ui.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class StateGetDoesNotMarkDirtyTest {
    private final Set<String> excludedMethods = new HashSet<>();

    @Test
    public void testGetDoesntMarkStateDirty() throws Exception {
        int count = 0;
        for (Class<? extends Component> clazz : VaadinClasses.getComponents()) {
            if ((clazz.isInterface()) || (Modifier.isAbstract(clazz.getModifiers()))) {
                continue;
            }
            Component newInstance = construct(clazz);
            if (newInstance == null) {
                continue;
            }
            count++;
            prepareMockUI(newInstance);
            Set<Method> methods = new HashSet<>();
            methods.addAll(Arrays.asList(clazz.getMethods()));
            methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
            for (Method method : methods) {
                try {
                    if ((method.getName().startsWith("is")) || (method.getName().startsWith("get"))) {
                        if (method.getName().startsWith("getState")) {
                            continue;
                        }
                        if ((method.getParameterTypes().length) > 0) {
                            // usually getters do not have params, if they have
                            // we still wouldnt know what to put into
                            continue;
                        }
                        if (excludedMethods.contains((((clazz.getName()) + ":") + (method.getName())))) {
                            // blacklisted method for specific classes
                            continue;
                        }
                        if (excludedMethods.contains(method.getName())) {
                            // blacklisted method for all classes
                            continue;
                        }
                        // just to make sure we can invoke it
                        method.setAccessible(true);
                        try {
                            method.invoke(newInstance);
                        } catch (InvocationTargetException e) {
                            if ((e.getCause()) instanceof UnsupportedOperationException) {
                                // Overridden getter which is not supposed to be
                                // called
                            } else {
                                throw e;
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println(((("problem with method " + (clazz.getName())) + "# ") + (method.getName())));
                    e.printStackTrace();
                    throw e;
                }
            }
        }
        Assert.assertTrue((count > 0));
    }
}

