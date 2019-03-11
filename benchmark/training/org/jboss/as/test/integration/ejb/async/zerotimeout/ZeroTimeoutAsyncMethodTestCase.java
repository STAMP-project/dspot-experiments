package org.jboss.as.test.integration.ejb.async.zerotimeout;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.naming.InitialContext;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests calling Future on asynchronous method with zero time-out.
 * Test for [ JBEAP-6826 ].
 *
 * @author Daniel Cihak
 */
@RunWith(Arquillian.class)
public class ZeroTimeoutAsyncMethodTestCase {
    private static final String ARCHIVE_NAME = "ZeroTimeoutTestCase";

    @ContainerResource
    private InitialContext remoteContext;

    /**
     * Test call asynchronous method on remote bean with zero time-out, which means we demand the result immediately,
     * but the asynchronous method invocation takes 5000 ms. Therefore the result is not available and TimeoutException is thrown.
     *
     * @throws Exception
     * 		
     */
    @Test
    @RunAsClient
    public void testCallAsyncFutureZeroTimeout() throws Exception {
        ZeroTimeoutAsyncBeanRemoteInterface asyncBean = this.lookup(ZeroTimeoutAsyncBeanRemote.class, ZeroTimeoutAsyncBeanRemoteInterface.class);
        try {
            asyncBean.futureMethod().get(0, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            Assert.assertTrue((e instanceof TimeoutException));
        }
        Assert.assertTrue(asyncBean.futureMethod().get(10000, TimeUnit.MILLISECONDS));
    }
}

