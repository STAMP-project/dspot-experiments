package com.github.scribejava.core.services;


import TimestampServiceImpl.Timer;
import org.junit.Assert;
import org.junit.Test;


public class TimestampServiceTest {
    private TimestampServiceImpl service;

    @Test
    public void shouldReturnTimestampInSeconds() {
        final String expected = "1000";
        Assert.assertEquals(expected, service.getTimestampInSeconds());
    }

    @Test
    public void shouldReturnNonce() {
        final String expected = "1042";
        Assert.assertEquals(expected, service.getNonce());
    }

    private static class TimerStub extends TimestampServiceImpl.Timer {
        @Override
        public Long getMilis() {
            return 1000000L;
        }

        @Override
        public Integer getRandomInteger() {
            return 42;
        }
    }
}

