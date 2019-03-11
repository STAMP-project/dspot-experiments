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
public class TransactionalPollingIntegrationTests implements ApplicationContextAware {
    private Log logger = LogFactory.getLog(getClass());

    private static List<String> processed = new ArrayList<>();

    private static List<String> handled = new ArrayList<>();

    private static List<String> expected = new ArrayList<>();

    private static List<String> list = new ArrayList<>();

    private Lifecycle bus;

    private static volatile int count = 0;

    @Test
    @DirtiesContext
    public void testSunnyDay() throws Exception {
        try {
            TransactionalPollingIntegrationTests.list = TransactionAwareProxyFactory.createTransactionalList(Arrays.asList(StringUtils.commaDelimitedListToStringArray("a,b,c,d,e,f,g,h,j,k")));
            TransactionalPollingIntegrationTests.expected = Arrays.asList(StringUtils.commaDelimitedListToStringArray("a,b,c,d"));
            waitForResults(bus, 4, 60);
            Assert.assertEquals(TransactionalPollingIntegrationTests.expected, TransactionalPollingIntegrationTests.processed);
        } catch (Throwable t) {
            System.out.println(t.getMessage());
            t.printStackTrace();
        }
    }

    @Test
    @DirtiesContext
    public void testRollback() throws Exception {
        TransactionalPollingIntegrationTests.list = TransactionAwareProxyFactory.createTransactionalList(Arrays.asList(StringUtils.commaDelimitedListToStringArray("a,b,fail,d,e,f,g,h,j,k")));
        TransactionalPollingIntegrationTests.expected = Arrays.asList(StringUtils.commaDelimitedListToStringArray("a,b,fail,fail"));
        waitForResults(bus, 4, 30);
        Assert.assertEquals(TransactionalPollingIntegrationTests.expected, TransactionalPollingIntegrationTests.processed);
        Assert.assertEquals(2, TransactionalPollingIntegrationTests.handled.size());// a,b

    }
}

