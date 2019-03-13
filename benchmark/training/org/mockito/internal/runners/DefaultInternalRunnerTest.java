/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.runners;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.junit.MockitoTestListener;
import org.mockito.internal.junit.TestFinishedEvent;
import org.mockito.internal.util.Supplier;


public class DefaultInternalRunnerTest {
    private final RunListener runListener = Mockito.mock(RunListener.class);

    private final MockitoTestListener mockitoTestListener = Mockito.mock(MockitoTestListener.class);

    private final Supplier<MockitoTestListener> supplier = new Supplier<MockitoTestListener>() {
        public MockitoTestListener get() {
            return mockitoTestListener;
        }
    };

    @Test
    public void does_not_fail_when_tests_succeeds() throws Exception {
        new DefaultInternalRunner(DefaultInternalRunnerTest.SuccessTest.class, supplier).run(newNotifier(runListener));
        Mockito.verify(runListener, Mockito.never()).testFailure(ArgumentMatchers.any(Failure.class));
        Mockito.verify(runListener, Mockito.times(1)).testFinished(ArgumentMatchers.any(Description.class));
        Mockito.verify(mockitoTestListener, Mockito.only()).testFinished(ArgumentMatchers.any(TestFinishedEvent.class));
    }

    @Test
    public void does_not_fail_second_test_when_first_test_fail() throws Exception {
        new DefaultInternalRunner(DefaultInternalRunnerTest.TestFailOnInitialization.class, supplier).run(newNotifier(runListener));
        Mockito.verify(runListener, Mockito.times(1)).testFailure(ArgumentMatchers.any(Failure.class));
        Mockito.verify(runListener, Mockito.never()).testFinished(ArgumentMatchers.any(Description.class));
        Mockito.verify(mockitoTestListener, Mockito.never()).testFinished(ArgumentMatchers.any(TestFinishedEvent.class));
        Mockito.reset(runListener);
        new DefaultInternalRunner(DefaultInternalRunnerTest.SuccessTest.class, supplier).run(newNotifier(runListener));
        Mockito.verify(runListener, Mockito.never()).testFailure(ArgumentMatchers.any(Failure.class));
        Mockito.verify(runListener, Mockito.times(1)).testFinished(ArgumentMatchers.any(Description.class));
        Mockito.verify(mockitoTestListener, Mockito.only()).testFinished(ArgumentMatchers.any(TestFinishedEvent.class));
    }

    public static final class SuccessTest {
        @Test
        public void this_test_is_NOT_supposed_to_fail() {
            Assert.assertTrue(true);
        }
    }

    public static final class TestFailOnInitialization {
        @Mock
        private System system;

        @Test
        public void this_test_is_supposed_to_fail() {
            Assert.assertNotNull(system);
        }
    }
}

