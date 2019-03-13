/**
 * Copyright (c) 2018 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.session;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.exceptions.misusing.UnfinishedMockingSessionException;
import org.mockito.quality.Strictness;
import org.mockito.session.MockitoSessionLogger;
import org.mockitoutil.ThrowableAssert;


public class DefaultMockitoSessionBuilderTest {
    @Test
    public void creates_sessions() {
        // no configuration is legal
        new DefaultMockitoSessionBuilder().startMocking().finishMocking();
        // passing null to configuration is legal, default value will be used
        new DefaultMockitoSessionBuilder().initMocks(((Object) (null))).startMocking().finishMocking();
        new DefaultMockitoSessionBuilder().initMocks(((Object[]) (null))).startMocking().finishMocking();
        new DefaultMockitoSessionBuilder().initMocks(null, null).strictness(null).startMocking().finishMocking();
        new DefaultMockitoSessionBuilder().strictness(null).startMocking().finishMocking();
        // happy path
        new DefaultMockitoSessionBuilder().initMocks(this).startMocking().finishMocking();
        new DefaultMockitoSessionBuilder().initMocks(new Object()).startMocking().finishMocking();
        new DefaultMockitoSessionBuilder().strictness(Strictness.LENIENT).startMocking().finishMocking();
    }

    @Test
    public void creates_sessions_for_multiple_test_class_instances_for_repeated_calls() {
        DefaultMockitoSessionBuilderTest.TestClass testClass = new DefaultMockitoSessionBuilderTest.TestClass();
        DefaultMockitoSessionBuilderTest.TestClass.NestedTestClass nestedTestClass = testClass.new NestedTestClass();
        new DefaultMockitoSessionBuilder().initMocks(testClass).initMocks(nestedTestClass).startMocking().finishMocking();
        Assert.assertNotNull(testClass.set);
        Assert.assertNotNull(nestedTestClass.list);
    }

    @Test
    public void creates_sessions_for_multiple_test_class_instances_for_varargs_call() {
        DefaultMockitoSessionBuilderTest.TestClass testClass = new DefaultMockitoSessionBuilderTest.TestClass();
        DefaultMockitoSessionBuilderTest.TestClass.NestedTestClass nestedTestClass = testClass.new NestedTestClass();
        new DefaultMockitoSessionBuilder().initMocks(testClass, nestedTestClass).startMocking().finishMocking();
        Assert.assertNotNull(testClass.set);
        Assert.assertNotNull(nestedTestClass.list);
    }

    @Test
    public void uses_logger_and_strictness() {
        DefaultMockitoSessionBuilderTest.TestClass testClass = new DefaultMockitoSessionBuilderTest.TestClass();
        final List<String> hints = new ArrayList<String>();
        MockitoSession session = logger(new MockitoSessionLogger() {
            @Override
            public void log(String hint) {
                hints.add(hint);
            }
        }).startMocking();
        Mockito.when(testClass.set.add(1)).thenReturn(true);
        session.finishMocking();
        Assert.assertFalse(hints.isEmpty());
    }

    @Test
    public void requires_finish_mocking() {
        new DefaultMockitoSessionBuilder().startMocking();
        ThrowableAssert.assertThat(new Runnable() {
            public void run() {
                new DefaultMockitoSessionBuilder().startMocking();
            }
        }).throwsException(UnfinishedMockingSessionException.class);
    }

    @Test
    public void auto_cleans_dirty_listeners() {
        new DefaultMockitoSessionBuilder().startMocking();
        ThrowableAssert.assertThat(new Runnable() {
            public void run() {
                new DefaultMockitoSessionBuilder().startMocking();
            }
        }).throwsException(UnfinishedMockingSessionException.class);
    }

    class TestClass {
        @Mock
        public Set<Object> set;

        class NestedTestClass {
            @Mock
            public List<Object> list;
        }
    }
}

