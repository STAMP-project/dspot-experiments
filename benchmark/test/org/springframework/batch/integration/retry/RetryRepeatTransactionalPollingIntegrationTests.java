package org.springframework.batch.integration.retry;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.support.transaction.TransactionAwareProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.Lifecycle;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@MessageEndpoint
public class RetryRepeatTransactionalPollingIntegrationTests implements ApplicationContextAware {
    private Log logger = LogFactory.getLog(getClass());

    private static volatile List<String> list = new ArrayList<>();

    @Autowired
    private SimpleRecoverer recoverer;

    @Autowired
    private SimpleService service;

    private Lifecycle lifecycle;

    private static volatile int count = 0;

    @Test
    @DirtiesContext
    public void testSunnyDay() throws Exception {
        RetryRepeatTransactionalPollingIntegrationTests.list = TransactionAwareProxyFactory.createTransactionalList(Arrays.asList(StringUtils.commaDelimitedListToStringArray("a,b,c,d,e,f,g,h,j,k")));
        List<String> expected = Arrays.asList(StringUtils.commaDelimitedListToStringArray("a,b,c,d"));
        service.setExpected(expected);
        waitForResults(lifecycle, expected.size(), 60);
        Assert.assertEquals(4, service.getProcessed().size());// a,b,c,d

        Assert.assertEquals(expected, service.getProcessed());
    }

    @Test
    @DirtiesContext
    public void testRollback() throws Exception {
        RetryRepeatTransactionalPollingIntegrationTests.list = TransactionAwareProxyFactory.createTransactionalList(Arrays.asList(StringUtils.commaDelimitedListToStringArray("a,b,fail,d,e,f,g,h,j,k")));
        List<String> expected = Arrays.asList(StringUtils.commaDelimitedListToStringArray("a,b,fail,fail,d,e,f"));
        service.setExpected(expected);
        waitForResults(lifecycle, expected.size(), 60);// (a,b), (fail), (fail), ([fail],d), (e,f)

        System.err.println(service.getProcessed());
        Assert.assertEquals(7, service.getProcessed().size());// a,b,fail,fail,d,e,f

        Assert.assertEquals(1, recoverer.getRecovered().size());// fail

        Assert.assertEquals(expected, service.getProcessed());
    }
}

