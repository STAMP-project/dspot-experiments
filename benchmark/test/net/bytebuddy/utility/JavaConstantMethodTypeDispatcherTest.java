package net.bytebuddy.utility;


import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.utility.JavaConstant.MethodType.Dispatcher.ForLegacyVm.INSTANCE;


public class JavaConstantMethodTypeDispatcherTest {
    @Test(expected = UnsupportedOperationException.class)
    public void testLegacyVmReturnType() throws Exception {
        INSTANCE.returnType(Mockito.mock(Object.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLegacyVmParameterArray() throws Exception {
        INSTANCE.parameterArray(Mockito.mock(Object.class));
    }
}

