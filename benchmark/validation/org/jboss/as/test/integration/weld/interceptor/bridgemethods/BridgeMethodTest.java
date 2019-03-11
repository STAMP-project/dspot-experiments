package org.jboss.as.test.integration.weld.interceptor.bridgemethods;


import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 */
@RunWith(Arquillian.class)
public class BridgeMethodTest {
    @Inject
    private SpecialService specialService;

    @Test
    public void testBridgeMethodInterceptor() {
        specialService.doSomething("foo");
        Assert.assertEquals(1, SomeInterceptor.invocationCount);
    }
}

