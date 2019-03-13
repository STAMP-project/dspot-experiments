package com.vaadin.tests.server.component;


import com.vaadin.tests.VaadinClasses;
import com.vaadin.ui.Component;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;


public class FinalMethodTest {
    // public void testThatContainersHaveNoFinalMethods() {
    // HashSet<Class<?>> tested = new HashSet<Class<?>>();
    // for (Class<?> c : VaadinClasses.getAllServerSideClasses()) {
    // if (Container.class.isAssignableFrom(c)) {
    // ensureNoFinalMethods(c, tested);
    // }
    // }
    // }
    @Test
    public void testThatComponentsHaveNoFinalMethods() {
        HashSet<Class<?>> tested = new HashSet<>();
        int count = 0;
        for (Class<? extends Component> c : VaadinClasses.getComponents()) {
            ensureNoFinalMethods(c, tested);
            count++;
        }
        Assert.assertTrue((count > 0));
    }
}

