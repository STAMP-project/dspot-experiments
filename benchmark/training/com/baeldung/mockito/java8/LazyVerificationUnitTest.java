package com.baeldung.mockito.java8;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.VerificationCollector;


public class LazyVerificationUnitTest {
    @Test
    public void whenLazilyVerified_thenReportsMultipleFailures() {
        VerificationCollector collector = MockitoJUnit.collector().assertLazily();
        List mockList = Mockito.mock(List.class);
        Mockito.verify(mockList).add("one");
        Mockito.verify(mockList).clear();
        try {
            collector.collectAndReport();
        } catch (MockitoAssertionError error) {
            Assert.assertTrue(error.getMessage().contains("1. Wanted but not invoked:"));
            Assert.assertTrue(error.getMessage().contains("2. Wanted but not invoked:"));
        }
    }
}

