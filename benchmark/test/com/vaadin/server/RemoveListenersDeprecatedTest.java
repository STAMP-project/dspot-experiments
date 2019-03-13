package com.vaadin.server;


import com.vaadin.event.EventRouter;
import com.vaadin.shared.Registration;
import com.vaadin.tests.VaadinClasses;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;


public class RemoveListenersDeprecatedTest {
    private static final List<Predicate<Method>> ALLOW_REMOVE_LISTENER = new ArrayList<>();

    static {
        RemoveListenersDeprecatedTest.ALLOW_REMOVE_LISTENER.add(RemoveListenersDeprecatedTest::acceptAbstarctClientConnectorRemoveMethods);
        RemoveListenersDeprecatedTest.ALLOW_REMOVE_LISTENER.add(RemoveListenersDeprecatedTest::acceptAbstractDataProvider);
        RemoveListenersDeprecatedTest.ALLOW_REMOVE_LISTENER.add(RemoveListenersDeprecatedTest::acceptMethodEventSource);
    }

    @Test
    public void allRemoveListenerMethodsMarkedAsDeprecated() {
        Pattern removePattern = Pattern.compile("remove.*Listener");
        Pattern addPattern = Pattern.compile("add.*Listener");
        int count = 0;
        for (Class<? extends Object> serverClass : VaadinClasses.getAllServerSideClasses()) {
            count++;
            if (serverClass.equals(EventRouter.class)) {
                continue;
            }
            for (Method method : serverClass.getDeclaredMethods()) {
                if (Modifier.isPrivate(method.getModifiers())) {
                    continue;
                }
                if ((addPattern.matcher(method.getName()).matches()) && ((method.getAnnotation(Deprecated.class)) == null)) {
                    Class<?> returnType = method.getReturnType();
                    Assert.assertEquals((((("Method " + (method.getName())) + " is not deprectated in class ") + (serverClass.getName())) + " and doesn't return a Registration object"), Registration.class, returnType);
                }
                if (RemoveListenersDeprecatedTest.ALLOW_REMOVE_LISTENER.stream().anyMatch(( predicate) -> predicate.test(method))) {
                    continue;
                }
                if (removePattern.matcher(method.getName()).matches()) {
                    Assert.assertNotNull((((("Method " + (method.getName())) + " in class ") + (serverClass.getName())) + " has not been marked as deprecated."), method.getAnnotation(Deprecated.class));
                }
            }
        }
        Assert.assertTrue((count > 0));
    }
}

