package com.vaadin.tests.components.ui;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class RpcInvocationHandlerToStringTest extends MultiBrowserTest {
    @Test
    public void testMethodsOnInvocationProxy() throws Exception {
        openTestURL();
        execMethodForProxy("toString()");
        execMethodForProxy("hashCode()");
        execMethodForProxy("equals(false)");
    }
}

