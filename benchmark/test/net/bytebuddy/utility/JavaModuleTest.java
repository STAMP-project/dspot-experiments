package net.bytebuddy.utility;


import java.lang.instrument.Instrumentation;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.utility.JavaModule.Dispatcher.Disabled.INSTANCE;


public class JavaModuleTest {
    private static final String FOO = "foo";

    @Test
    public void testSupportsDisabledThrowException() throws Exception {
        Assert.assertThat(INSTANCE.isAlive(), CoreMatchers.is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtractModule() throws Exception {
        JavaModule.of(Mockito.mock(Object.class));
    }

    @Test
    public void testUnwrap() throws Exception {
        Object object = new Object();
        JavaModule module = new JavaModule(object);
        Assert.assertThat(module.unwrap(), CoreMatchers.sameInstance(object));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIsNamedDisabledThrowException() throws Exception {
        INSTANCE.isNamed(Mockito.mock(Object.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetNameDisabledThrowException() throws Exception {
        INSTANCE.getName(Mockito.mock(Object.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetClassLoaderDisabledThrowException() throws Exception {
        INSTANCE.getClassLoader(Mockito.mock(Object.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCanReadThrowsException() throws Exception {
        INSTANCE.canRead(Mockito.mock(Object.class), Mockito.mock(Object.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetResourceAsStreamThrowsException() throws Exception {
        INSTANCE.getResourceAsStream(Mockito.mock(Object.class), JavaModuleTest.FOO);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddReadsThrowsException() throws Exception {
        INSTANCE.addReads(Mockito.mock(Instrumentation.class), Mockito.mock(Object.class), Mockito.mock(Object.class));
    }

    @Test
    public void testDisabledModuleIsNull() throws Exception {
        Assert.assertThat(INSTANCE.moduleOf(Object.class), CoreMatchers.nullValue(JavaModule.class));
    }
}

