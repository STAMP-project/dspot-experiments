package com.baeldung.aop;


import com.baeldung.config.TestConfig;
import com.baeldung.dao.FooDao;
import com.baeldung.events.FooCreationEventListener;
import com.baeldung.model.Foo;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class }, loader = AnnotationConfigContextLoader.class)
public class AopPublishingIntegrationTest {
    @Autowired
    private FooDao dao;

    private Handler logEventHandler;

    private List<String> messages;

    @Test
    public void givenPublishingAspect_whenCallCreate_thenCreationEventIsPublished() {
        Logger logger = Logger.getLogger(FooCreationEventListener.class.getName());
        logger.addHandler(logEventHandler);
        dao.create(1L, "Bar");
        String logMessage = messages.get(0);
        Pattern pattern = Pattern.compile(("Created foo instance: " + (Pattern.quote(new Foo(1L, "Bar").toString()))));
        Assert.assertTrue(pattern.matcher(logMessage).matches());
    }
}

