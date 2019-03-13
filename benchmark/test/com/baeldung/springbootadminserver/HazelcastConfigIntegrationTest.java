package com.baeldung.springbootadminserver;


import com.baeldung.springbootadminserver.configs.HazelcastConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = { HazelcastConfig.class }, webEnvironment = NONE)
public class HazelcastConfigIntegrationTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void whenApplicationContextStarts_HazelcastConfigBeanExists() {
        Assert.assertNotEquals(applicationContext.getBean("hazelcast"), null);
    }
}

