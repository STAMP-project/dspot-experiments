package com.baeldung.hystrix;


import HystrixCommand.Setter;
import HystrixCommandGroupKey.Factory;
import HystrixCommandProperties.ExecutionIsolationStrategy.THREAD;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class HystrixTimeoutManualTest {
    @Test
    public void givenInputBobAndDefaultSettings_whenCommandExecuted_thenReturnHelloBob() {
        MatcherAssert.assertThat(new CommandHelloWorld("Bob").execute(), Matchers.equalTo("Hello Bob!"));
    }

    @Test
    public void givenSvcTimeoutOf100AndDefaultSettings_whenRemoteSvcExecuted_thenReturnSuccess() throws InterruptedException {
        HystrixCommand.Setter config = Setter.withGroupKey(Factory.asKey("RemoteServiceGroup2"));
        MatcherAssert.assertThat(execute(), Matchers.equalTo("Success"));
    }

    @Test(expected = HystrixRuntimeException.class)
    public void givenSvcTimeoutOf10000AndDefaultSettings__whenRemoteSvcExecuted_thenExpectHRE() throws InterruptedException {
        HystrixCommand.Setter config = Setter.withGroupKey(Factory.asKey("RemoteServiceGroupTest3"));
        execute();
    }

    @Test
    public void givenSvcTimeoutOf5000AndExecTimeoutOf10000_whenRemoteSvcExecuted_thenReturnSuccess() throws InterruptedException {
        HystrixCommand.Setter config = Setter.withGroupKey(Factory.asKey("RemoteServiceGroupTest4"));
        HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter();
        commandProperties.withExecutionTimeoutInMilliseconds(10000);
        config.andCommandPropertiesDefaults(commandProperties);
        MatcherAssert.assertThat(execute(), Matchers.equalTo("Success"));
    }

    @Test(expected = HystrixRuntimeException.class)
    public void givenSvcTimeoutOf15000AndExecTimeoutOf5000__whenExecuted_thenExpectHRE() throws InterruptedException {
        HystrixCommand.Setter config = Setter.withGroupKey(Factory.asKey("RemoteServiceGroupTest5"));
        HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter();
        commandProperties.withExecutionTimeoutInMilliseconds(5000);
        config.andCommandPropertiesDefaults(commandProperties);
        execute();
    }

    @Test
    public void givenSvcTimeoutOf500AndExecTimeoutOf10000AndThreadPool__whenExecuted_thenReturnSuccess() throws InterruptedException {
        HystrixCommand.Setter config = Setter.withGroupKey(Factory.asKey("RemoteServiceGroupThreadPool"));
        HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter();
        commandProperties.withExecutionTimeoutInMilliseconds(10000);
        config.andCommandPropertiesDefaults(commandProperties);
        config.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withMaxQueueSize(10).withCoreSize(3).withQueueSizeRejectionThreshold(10));
        MatcherAssert.assertThat(execute(), Matchers.equalTo("Success"));
    }

    @Test
    public void givenCircuitBreakerSetup__whenRemoteSvcCmdExecuted_thenReturnSuccess() throws InterruptedException {
        HystrixCommand.Setter config = Setter.withGroupKey(Factory.asKey("RemoteServiceGroupCircuitBreaker"));
        HystrixCommandProperties.Setter properties = HystrixCommandProperties.Setter();
        properties.withExecutionTimeoutInMilliseconds(1000);
        properties.withCircuitBreakerSleepWindowInMilliseconds(4000);
        properties.withExecutionIsolationStrategy(THREAD);
        properties.withCircuitBreakerEnabled(true);
        properties.withCircuitBreakerRequestVolumeThreshold(1);
        config.andCommandPropertiesDefaults(properties);
        config.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withMaxQueueSize(1).withCoreSize(1).withQueueSizeRejectionThreshold(1));
        MatcherAssert.assertThat(this.invokeRemoteService(config, 10000), Matchers.equalTo(null));
        MatcherAssert.assertThat(this.invokeRemoteService(config, 10000), Matchers.equalTo(null));
        MatcherAssert.assertThat(this.invokeRemoteService(config, 10000), Matchers.equalTo(null));
        Thread.sleep(5000);
        MatcherAssert.assertThat(execute(), Matchers.equalTo("Success"));
        MatcherAssert.assertThat(execute(), Matchers.equalTo("Success"));
        MatcherAssert.assertThat(execute(), Matchers.equalTo("Success"));
    }
}

