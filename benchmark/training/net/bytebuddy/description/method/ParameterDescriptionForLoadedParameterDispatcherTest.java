package net.bytebuddy.description.method;


import java.lang.reflect.AccessibleObject;
import org.junit.Test;

import static net.bytebuddy.description.method.ParameterDescription.ForLoadedParameter.Dispatcher.ForLegacyVm.INSTANCE;


public class ParameterDescriptionForLoadedParameterDispatcherTest {
    private static final int FOO = 42;

    private AccessibleObject accessibleObject;

    @Test(expected = UnsupportedOperationException.class)
    public void testLegacyVmGetName() throws Exception {
        INSTANCE.getName(accessibleObject, ParameterDescriptionForLoadedParameterDispatcherTest.FOO);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLegacyVmGetModifiers() throws Exception {
        INSTANCE.getModifiers(accessibleObject, ParameterDescriptionForLoadedParameterDispatcherTest.FOO);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLegacyVmIsNamePresent() throws Exception {
        INSTANCE.isNamePresent(accessibleObject, ParameterDescriptionForLoadedParameterDispatcherTest.FOO);
    }

    /* empty */
    private static class Foo {}
}

