package com.apache.camel.file.processor;


import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class DeadLetterChannelFileRouterIntegrationTest {
    private static final long DURATION_MILIS = 10000;

    private static final String SOURCE_FOLDER = "src/test/source-folder";

    @Test
    public void routeTest() throws InterruptedException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("camel-context-DeadLetterChannelFileRouter.xml");
        Thread.sleep(DeadLetterChannelFileRouterIntegrationTest.DURATION_MILIS);
        applicationContext.close();
    }
}

