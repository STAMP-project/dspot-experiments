package com.vaadin.tests.server.componentcontainer;


import com.vaadin.tests.VaadinClasses;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomLayout;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class AddRemoveComponentTest {
    @Test
    public void testRemoveComponentFromWrongContainer() throws IllegalAccessException, IllegalArgumentException, InstantiationException, InvocationTargetException {
        List<Class<? extends ComponentContainer>> containerClasses = VaadinClasses.getComponentContainersSupportingAddRemoveComponent();
        Assert.assertFalse(containerClasses.isEmpty());
        // No default constructor, special case
        containerClasses.remove(CustomLayout.class);
        testRemoveComponentFromWrongContainer(new CustomLayout("dummy"));
        for (Class<? extends ComponentContainer> clazz : containerClasses) {
            if (Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }
            try {
                Constructor<? extends ComponentContainer> constructor = clazz.getConstructor();
                constructor.setAccessible(true);
                testRemoveComponentFromWrongContainer(constructor.newInstance());
            } catch (NoSuchMethodException ignore) {
                // if there is no default CTOR, just ignore
            }
        }
    }
}

