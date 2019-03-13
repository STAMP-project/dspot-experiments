package com.baeldung.aop;


import com.baeldung.config.TestConfig;
import com.baeldung.dao.FooDao;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class }, loader = AnnotationConfigContextLoader.class)
public class AopPerformanceIntegrationTest {
    @Autowired
    private FooDao dao;

    private Handler logEventHandler;

    private List<String> messages;

    @Test
    public void givenPerformanceAspect_whenCallDaoMethod_thenPerformanceMeasurementAdviceIsCalled() {
        Logger logger = Logger.getLogger(PerformanceAspect.class.getName());
        logger.addHandler(logEventHandler);
        final String entity = dao.findById(1L);
        Assert.assertThat(entity, CoreMatchers.notNullValue());
        Assert.assertThat(messages, Matchers.hasSize(1));
        String logMessage = messages.get(0);
        Pattern pattern = Pattern.compile("Execution of findById took \\d+ ms");
        Assert.assertTrue(pattern.matcher(logMessage).matches());
    }
}

