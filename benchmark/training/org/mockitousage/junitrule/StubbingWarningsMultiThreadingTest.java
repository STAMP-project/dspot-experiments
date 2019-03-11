/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.junitrule;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.junit.JUnitRule;
import org.mockito.internal.util.SimpleMockitoLogger;
import org.mockito.quality.Strictness;
import org.mockitousage.IMethods;
import org.mockitoutil.ConcurrentTesting;
import org.mockitoutil.SafeJUnitRule;
import org.mockitoutil.TestBase;


public class StubbingWarningsMultiThreadingTest {
    private SimpleMockitoLogger logger = new SimpleMockitoLogger();

    @Rule
    public SafeJUnitRule rule = new SafeJUnitRule(new JUnitRule(logger, Strictness.WARN));

    @Mock
    IMethods mock;

    @Test
    public void using_stubbing_from_different_thread() throws Throwable {
        // expect no warnings
        rule.expectSuccess(new Runnable() {
            public void run() {
                Assert.assertTrue(logger.getLoggedInfo().isEmpty());
            }
        });
        // when stubbing is declared
        Mockito.when(mock.simpleMethod()).thenReturn("1");
        // and used from a different thread
        ConcurrentTesting.inThread(new Runnable() {
            public void run() {
                mock.simpleMethod();
            }
        });
    }

    @Test
    public void unused_stub_from_different_thread() throws Throwable {
        // expect warnings
        rule.expectSuccess(new Runnable() {
            public void run() {
                Assert.assertEquals(("[MockitoHint] StubbingWarningsMultiThreadingTest.unused_stub_from_different_thread (see javadoc for MockitoHint):\n" + "[MockitoHint] 1. Unused -> at org.mockitousage.junitrule.StubbingWarningsMultiThreadingTest.unused_stub_from_different_thread(StubbingWarningsMultiThreadingTest.java:0)\n"), TestBase.filterLineNo(logger.getLoggedInfo()));
            }
        });
        // when stubbings are declared
        Mockito.when(mock.simpleMethod(1)).thenReturn("1");
        Mockito.when(mock.simpleMethod(2)).thenReturn("2");
        // and one of the stubbings is used from a different thread
        ConcurrentTesting.inThread(new Runnable() {
            public void run() {
                mock.simpleMethod(1);
            }
        });
    }
}

