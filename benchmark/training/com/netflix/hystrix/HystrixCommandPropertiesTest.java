/**
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hystrix;


import HystrixCommandProperties.default_circuitBreakerForceClosed;
import HystrixCommandProperties.default_metricsRollingStatisticalWindow;
import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.HystrixCommandProperties.Setter;
import org.junit.Assert;
import org.junit.Test;


public class HystrixCommandPropertiesTest {
    // NOTE: We use "unitTestPrefix" as a prefix so we can't end up pulling in external properties that change unit test behavior
    public enum TestKey implements HystrixCommandKey {

        TEST;}

    private static class TestPropertiesCommand extends HystrixCommandProperties {
        protected TestPropertiesCommand(HystrixCommandKey key, Setter builder, String propertyPrefix) {
            super(key, builder, propertyPrefix);
        }
    }

    @Test
    public void testBooleanBuilderOverride1() {
        HystrixCommandProperties properties = new HystrixCommandPropertiesTest.TestPropertiesCommand(HystrixCommandPropertiesTest.TestKey.TEST, new HystrixCommandProperties.Setter().withCircuitBreakerForceClosed(true), "unitTestPrefix");
        // the builder override should take precedence over the default
        Assert.assertEquals(true, properties.circuitBreakerForceClosed().get());
    }

    @Test
    public void testBooleanBuilderOverride2() {
        HystrixCommandProperties properties = new HystrixCommandPropertiesTest.TestPropertiesCommand(HystrixCommandPropertiesTest.TestKey.TEST, new HystrixCommandProperties.Setter().withCircuitBreakerForceClosed(false), "unitTestPrefix");
        // the builder override should take precedence over the default
        Assert.assertEquals(false, properties.circuitBreakerForceClosed().get());
    }

    @Test
    public void testBooleanCodeDefault() {
        HystrixCommandProperties properties = new HystrixCommandPropertiesTest.TestPropertiesCommand(HystrixCommandPropertiesTest.TestKey.TEST, new HystrixCommandProperties.Setter(), "unitTestPrefix");
        Assert.assertEquals(default_circuitBreakerForceClosed, properties.circuitBreakerForceClosed().get());
    }

    @Test
    public void testBooleanGlobalDynamicOverrideOfCodeDefault() throws Exception {
        HystrixCommandProperties properties = new HystrixCommandPropertiesTest.TestPropertiesCommand(HystrixCommandPropertiesTest.TestKey.TEST, new HystrixCommandProperties.Setter(), "unitTestPrefix");
        ConfigurationManager.getConfigInstance().setProperty("unitTestPrefix.command.default.circuitBreaker.forceClosed", true);
        // the global dynamic property should take precedence over the default
        Assert.assertEquals(true, properties.circuitBreakerForceClosed().get());
        // cleanup
        ConfigurationManager.getConfigInstance().clearProperty("unitTestPrefix.command.default.circuitBreaker.forceClosed");
    }

    @Test
    public void testBooleanInstanceBuilderOverrideOfGlobalDynamicOverride1() throws Exception {
        HystrixCommandProperties properties = new HystrixCommandPropertiesTest.TestPropertiesCommand(HystrixCommandPropertiesTest.TestKey.TEST, new HystrixCommandProperties.Setter().withCircuitBreakerForceClosed(true), "unitTestPrefix");
        ConfigurationManager.getConfigInstance().setProperty("unitTestPrefix.command.default.circuitBreaker.forceClosed", false);
        // the builder injected should take precedence over the global dynamic property
        Assert.assertEquals(true, properties.circuitBreakerForceClosed().get());
        // cleanup
        ConfigurationManager.getConfigInstance().clearProperty("unitTestPrefix.command.default.circuitBreaker.forceClosed");
    }

    @Test
    public void testBooleanInstanceBuilderOverrideOfGlobalDynamicOverride2() throws Exception {
        HystrixCommandProperties properties = new HystrixCommandPropertiesTest.TestPropertiesCommand(HystrixCommandPropertiesTest.TestKey.TEST, new HystrixCommandProperties.Setter().withCircuitBreakerForceClosed(false), "unitTestPrefix");
        ConfigurationManager.getConfigInstance().setProperty("unitTestPrefix.command.default.circuitBreaker.forceClosed", true);
        // the builder injected should take precedence over the global dynamic property
        Assert.assertEquals(false, properties.circuitBreakerForceClosed().get());
        // cleanup
        ConfigurationManager.getConfigInstance().clearProperty("unitTestPrefix.command.default.circuitBreaker.forceClosed");
    }

    @Test
    public void testBooleanInstanceDynamicOverrideOfEverything() throws Exception {
        HystrixCommandProperties properties = new HystrixCommandPropertiesTest.TestPropertiesCommand(HystrixCommandPropertiesTest.TestKey.TEST, new HystrixCommandProperties.Setter().withCircuitBreakerForceClosed(false), "unitTestPrefix");
        ConfigurationManager.getConfigInstance().setProperty("unitTestPrefix.command.default.circuitBreaker.forceClosed", false);
        ConfigurationManager.getConfigInstance().setProperty("unitTestPrefix.command.TEST.circuitBreaker.forceClosed", true);
        // the instance specific dynamic property should take precedence over everything
        Assert.assertEquals(true, properties.circuitBreakerForceClosed().get());
        // cleanup
        ConfigurationManager.getConfigInstance().clearProperty("unitTestPrefix.command.default.circuitBreaker.forceClosed");
        ConfigurationManager.getConfigInstance().clearProperty("unitTestPrefix.command.TEST.circuitBreaker.forceClosed");
    }

    @Test
    public void testIntegerBuilderOverride() {
        HystrixCommandProperties properties = new HystrixCommandPropertiesTest.TestPropertiesCommand(HystrixCommandPropertiesTest.TestKey.TEST, new HystrixCommandProperties.Setter().withMetricsRollingStatisticalWindowInMilliseconds(5000), "unitTestPrefix");
        // the builder override should take precedence over the default
        Assert.assertEquals(5000, properties.metricsRollingStatisticalWindowInMilliseconds().get().intValue());
    }

    @Test
    public void testIntegerCodeDefault() {
        HystrixCommandProperties properties = new HystrixCommandPropertiesTest.TestPropertiesCommand(HystrixCommandPropertiesTest.TestKey.TEST, new HystrixCommandProperties.Setter(), "unitTestPrefix");
        Assert.assertEquals(default_metricsRollingStatisticalWindow, properties.metricsRollingStatisticalWindowInMilliseconds().get());
    }

    @Test
    public void testIntegerGlobalDynamicOverrideOfCodeDefault() throws Exception {
        HystrixCommandProperties properties = new HystrixCommandPropertiesTest.TestPropertiesCommand(HystrixCommandPropertiesTest.TestKey.TEST, new HystrixCommandProperties.Setter(), "unitTestPrefix");
        ConfigurationManager.getConfigInstance().setProperty("unitTestPrefix.command.default.metrics.rollingStats.timeInMilliseconds", 1234);
        // the global dynamic property should take precedence over the default
        Assert.assertEquals(1234, properties.metricsRollingStatisticalWindowInMilliseconds().get().intValue());
        // cleanup
        ConfigurationManager.getConfigInstance().clearProperty("unitTestPrefix.command.default.metrics.rollingStats.timeInMilliseconds");
    }

    @Test
    public void testIntegerInstanceBuilderOverrideOfGlobalDynamicOverride() throws Exception {
        HystrixCommandProperties properties = new HystrixCommandPropertiesTest.TestPropertiesCommand(HystrixCommandPropertiesTest.TestKey.TEST, new HystrixCommandProperties.Setter().withMetricsRollingStatisticalWindowInMilliseconds(5000), "unitTestPrefix");
        ConfigurationManager.getConfigInstance().setProperty("unitTestPrefix.command.default.rollingStats.timeInMilliseconds", 3456);
        // the builder injected should take precedence over the global dynamic property
        Assert.assertEquals(5000, properties.metricsRollingStatisticalWindowInMilliseconds().get().intValue());
        // cleanup
        ConfigurationManager.getConfigInstance().clearProperty("unitTestPrefix.command.default.rollingStats.timeInMilliseconds");
    }

    @Test
    public void testIntegerInstanceDynamicOverrideOfEverything() throws Exception {
        HystrixCommandProperties properties = new HystrixCommandPropertiesTest.TestPropertiesCommand(HystrixCommandPropertiesTest.TestKey.TEST, new HystrixCommandProperties.Setter().withMetricsRollingStatisticalWindowInMilliseconds(5000), "unitTestPrefix");
        ConfigurationManager.getConfigInstance().setProperty("unitTestPrefix.command.default.metrics.rollingStats.timeInMilliseconds", 1234);
        ConfigurationManager.getConfigInstance().setProperty("unitTestPrefix.command.TEST.metrics.rollingStats.timeInMilliseconds", 3456);
        // the instance specific dynamic property should take precedence over everything
        Assert.assertEquals(3456, properties.metricsRollingStatisticalWindowInMilliseconds().get().intValue());
        // cleanup
        ConfigurationManager.getConfigInstance().clearProperty("unitTestPrefix.command.default.metrics.rollingStats.timeInMilliseconds");
        ConfigurationManager.getConfigInstance().clearProperty("unitTestPrefix.command.TEST.metrics.rollingStats.timeInMilliseconds");
    }

    @Test
    public void testThreadPoolOnlyHasInstanceOverride() throws Exception {
        HystrixCommandProperties properties = new HystrixCommandPropertiesTest.TestPropertiesCommand(HystrixCommandPropertiesTest.TestKey.TEST, new HystrixCommandProperties.Setter(), "unitTestPrefix");
        ConfigurationManager.getConfigInstance().setProperty("unitTestPrefix.command.default.threadPoolKeyOverride", 1234);
        // it should be null
        Assert.assertEquals(null, properties.executionIsolationThreadPoolKeyOverride().get());
        ConfigurationManager.getConfigInstance().setProperty("unitTestPrefix.command.TEST.threadPoolKeyOverride", "testPool");
        // now it should have a value
        Assert.assertEquals("testPool", properties.executionIsolationThreadPoolKeyOverride().get());
        // cleanup
        ConfigurationManager.getConfigInstance().clearProperty("unitTestPrefix.command.default.threadPoolKeyOverride");
        ConfigurationManager.getConfigInstance().clearProperty("unitTestPrefix.command.TEST.threadPoolKeyOverride");
    }
}

