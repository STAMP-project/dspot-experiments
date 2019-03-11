package com.baeldung.bootique;


import com.baeldung.bootique.service.HelloService;
import io.bootique.BQRuntime;
import io.bootique.test.junit.BQDaemonTestFactory;
import io.bootique.test.junit.BQTestFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class AppUnitTest {
    @Rule
    public BQTestFactory bqTestFactory = new BQTestFactory();

    @Rule
    public BQDaemonTestFactory bqDaemonTestFactory = new BQDaemonTestFactory();

    @Test
    public void givenService_expectBoolen() {
        BQRuntime runtime = bqTestFactory.app("--server").autoLoadModules().createRuntime();
        HelloService service = runtime.getInstance(HelloService.class);
        Assert.assertEquals(true, service.save());
    }
}

