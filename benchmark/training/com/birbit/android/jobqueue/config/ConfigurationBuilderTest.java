package com.birbit.android.jobqueue.config;


import Configuration.Builder;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ConfigurationBuilderTest {
    Builder builder;

    @Test(expected = IllegalArgumentException.class)
    public void idWithSpaces() {
        testId("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullId() {
        testId(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void idWithSpace() {
        testId("hello world");
    }

    @Test(expected = IllegalArgumentException.class)
    public void idWithBadChars() {
        testId("hello~world");
    }

    @Test(expected = IllegalArgumentException.class)
    public void idWithBadChars2() {
        testId("hello?world");
    }

    @Test
    public void goodId() {
        testId("blah");
    }

    @Test
    public void goodId2() {
        testId("blah123");
    }

    @Test
    public void goodId3() {
        testId("blah_123");
    }

    @Test
    public void goodId4() {
        testId("blah-123");
    }

    @Test
    public void goodUUID() {
        testId(UUID.randomUUID().toString());
    }
}

