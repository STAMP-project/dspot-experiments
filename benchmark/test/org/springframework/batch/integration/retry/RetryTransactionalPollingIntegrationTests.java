package org.springframework.batch.integration.retry;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.support.transaction.TransactionAwareProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.Lifecycle;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class RetryTransactionalPollingIntegrationTests implements ApplicationContextAware {
    private Log logger = LogFactory.getLog(getClass());

    private static List<String> list = new ArrayList<>();

    @Autowired
    private SimpleRecoverer recoverer;

    @Autowired
    private SimpleService service;

    private Lifecycle bus;

    private static AtomicInteger count = new AtomicInteger(0);

    @Test
    @DirtiesContext
    public void testSunnyDay() throws Exception {
        RetryTransactionalPollingIntegrationTests.list = TransactionAwareProxyFactory.createTransactionalList(Arrays.asList(StringUtils.commaDelimitedListToStringArray("a,b,c,d,e,f,g,h,j,k")));
        List<String> expected = Arrays.asList(StringUtils.commaDelimitedListToStringArray("a,b,c,d"));
        service.setExpected(expected);
        waitForResults(bus, expected.size(), 60);
        Assert.assertEquals(4, service.getProcessed().size());// a,b,c,d

        Assert.assertEquals(expected, service.getProcessed());
    }

    @Test
    @DirtiesContext
    public void testRollback() throws Exception {
        RetryTransactionalPollingIntegrationTests.list = TransactionAwareProxyFactory.createTransactionalList(Arrays.asList(StringUtils.commaDelimitedListToStringArray("a,b,fail,d,e,f,g,h,j,k")));
        List<String> expected = Arrays.asList(StringUtils.commaDelimitedListToStringArray("a,b,fail,fail,d,e"));
        service.setExpected(expected);
        waitForResults(bus, expected.size(), 100);// a, b, (fail, fail, [fail]), d, e

        // System.err.println(service.getProcessed());
        Assert.assertEquals(6, service.getProcessed().size());// a,b,fail,fail,d,e

        Assert.assertEquals(1, recoverer.getRecovered().size());// fail

        Assert.assertEquals(expected, service.getProcessed());
    }
}

