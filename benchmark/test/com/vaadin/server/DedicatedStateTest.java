package com.vaadin.server;


import com.vaadin.tests.VaadinClasses;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.junit.Test;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class DedicatedStateTest {
    private static final Set<String> WHITE_LIST = DedicatedStateTest.createWhiteList();

    @Test
    public void checkDedicatedStates() {
        VaadinClasses.getAllServerSideClasses().stream().filter(( clazz) -> AbstractClientConnector.class.isAssignableFrom(clazz)).forEach(this::checkState);
    }
}

