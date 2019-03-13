package com.ctrip.framework.foundation.internals;


import java.util.ServiceConfigurationError;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class ServiceBootstrapTest {
    @Test
    public void loadFirstSuccessfully() throws Exception {
        ServiceBootstrapTest.Interface1 service = ServiceBootstrap.loadFirst(ServiceBootstrapTest.Interface1.class);
        Assert.assertTrue((service instanceof ServiceBootstrapTest.Interface1Impl));
    }

    @Test(expected = IllegalStateException.class)
    public void loadFirstWithNoServiceFileDefined() throws Exception {
        ServiceBootstrap.loadFirst(ServiceBootstrapTest.Interface2.class);
    }

    @Test(expected = IllegalStateException.class)
    public void loadFirstWithServiceFileButNoServiceImpl() throws Exception {
        ServiceBootstrap.loadFirst(ServiceBootstrapTest.Interface3.class);
    }

    @Test(expected = ServiceConfigurationError.class)
    public void loadFirstWithWrongServiceImpl() throws Exception {
        ServiceBootstrap.loadFirst(ServiceBootstrapTest.Interface4.class);
    }

    @Test(expected = ServiceConfigurationError.class)
    public void loadFirstWithServiceImplNotExists() throws Exception {
        ServiceBootstrap.loadFirst(ServiceBootstrapTest.Interface5.class);
    }

    private interface Interface1 {}

    public static class Interface1Impl implements ServiceBootstrapTest.Interface1 {}

    private interface Interface2 {}

    private interface Interface3 {}

    private interface Interface4 {}

    private interface Interface5 {}
}

