package org.kairosdb.eventbus;


import PipelineRegistry.DEFAULT_PRIORITY;
import com.google.common.collect.ImmutableMap;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.kairosdb.core.KairosRootConfig;


public class EventBusConfigurationTest {
    @Test(expected = NullPointerException.class)
    public void test_constructor_nullProperties_invalid() {
        new EventBusConfiguration(null);
    }

    @Test
    public void test() {
        KairosRootConfig properties = new KairosRootConfig();
        properties.load(ImmutableMap.of("kairosdb.eventbus.filter.priority.com.foo.Filter1", "10"));
        properties.load(ImmutableMap.of("kairosdb.eventbus.filter.priority.com.bar.Filter2", "20"));
        properties.load(ImmutableMap.of("kairosdb.eventbus.filter.priority.com.fi.Filter3", "30"));
        properties.load(ImmutableMap.of("kairosdb.eventbus.filter.priority.org.apache.Filter4", "40"));
        EventBusConfiguration config = new EventBusConfiguration(properties);
        MatcherAssert.assertThat(config.getFilterPriority("com.foo.Filter1"), CoreMatchers.equalTo(10));
        MatcherAssert.assertThat(config.getFilterPriority("com.bar.Filter2"), CoreMatchers.equalTo(20));
        MatcherAssert.assertThat(config.getFilterPriority("com.fi.Filter3"), CoreMatchers.equalTo(30));
        MatcherAssert.assertThat(config.getFilterPriority("org.apache.Filter4"), CoreMatchers.equalTo(40));
        MatcherAssert.assertThat(config.getFilterPriority("com.foo.Bogus"), CoreMatchers.equalTo(DEFAULT_PRIORITY));
    }

    @Test
    public void test_invalid_priority() {
        KairosRootConfig properties = new KairosRootConfig();
        properties.load(ImmutableMap.of("kairosdb.eventbus.filter.priority.com.foo.Filter1", "10.5"));
        EventBusConfiguration config = new EventBusConfiguration(properties);
        MatcherAssert.assertThat(config.getFilterPriority("com.foo.Filter1"), CoreMatchers.equalTo(DEFAULT_PRIORITY));
    }
}

