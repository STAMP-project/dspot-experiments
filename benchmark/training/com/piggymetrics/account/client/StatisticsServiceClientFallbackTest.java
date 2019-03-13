package com.piggymetrics.account.client;


import com.piggymetrics.account.domain.Account;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.test.context.junit4.SpringRunner;


/**
 *
 *
 * @author cdov
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = { "feign.hystrix.enabled=true" })
public class StatisticsServiceClientFallbackTest {
    @Autowired
    private StatisticsServiceClient statisticsServiceClient;

    @Rule
    public final OutputCapture outputCapture = new OutputCapture();

    @Test
    public void testUpdateStatisticsWithFailFallback() {
        statisticsServiceClient.updateStatistics("test", new Account());
        outputCapture.expect(Matchers.containsString("Error during update statistics for account: test"));
    }
}

