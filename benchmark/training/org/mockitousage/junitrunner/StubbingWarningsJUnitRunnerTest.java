/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.junitrunner;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.UnfinishedStubbingException;
import org.mockito.internal.util.SimpleMockitoLogger;
import org.mockito.junit.TestableJUnitRunner;
import org.mockitousage.IMethods;
import org.mockitoutil.JUnitResultAssert;
import org.mockitoutil.TestBase;


public class StubbingWarningsJUnitRunnerTest extends TestBase {
    JUnitCore runner = new JUnitCore();

    SimpleMockitoLogger logger = TestableJUnitRunner.refreshedLogger();

    @Test
    public void no_arg_mismatch_warnings() {
        // when
        runner.run(StubbingWarningsJUnitRunnerTest.PassingArgMismatch.class, StubbingWarningsJUnitRunnerTest.FailingWithMatchingArgs.class, StubbingWarningsJUnitRunnerTest.MismatchButStubAlreadyUsed.class);
        // then
        Assert.assertEquals("", TestBase.filterLineNo(logger.getLoggedInfo()));
    }

    @Test
    public void shows_arg_mismatch_warnings_when_test_fails() {
        // when
        runner.run(StubbingWarningsJUnitRunnerTest.FailingWithArgMismatch.class);
        // then
        Assert.assertEquals(("[MockitoHint] FailingWithArgMismatch.test (see javadoc for MockitoHint):\n" + ("[MockitoHint] 1. Unused... -> at org.mockitousage.junitrunner.StubbingWarningsJUnitRunnerTest$FailingWithArgMismatch.test(StubbingWarningsJUnitRunnerTest.java:0)\n" + "[MockitoHint]  ...args ok? -> at org.mockitousage.junitrunner.StubbingWarningsJUnitRunnerTest$FailingWithArgMismatch.test(StubbingWarningsJUnitRunnerTest.java:0)\n")), TestBase.filterLineNo(logger.getLoggedInfo()));
    }

    @Test
    public void shows_arg_mismatch_warnings_only_for_mismatches() {
        // when
        runner.run(StubbingWarningsJUnitRunnerTest.FailingWithSomeStubMismatches.class);
        // then
        Assert.assertEquals(("[MockitoHint] FailingWithSomeStubMismatches.test (see javadoc for MockitoHint):\n" + ("[MockitoHint] 1. Unused... -> at org.mockitousage.junitrunner.StubbingWarningsJUnitRunnerTest$FailingWithSomeStubMismatches.test(StubbingWarningsJUnitRunnerTest.java:0)\n" + "[MockitoHint]  ...args ok? -> at org.mockitousage.junitrunner.StubbingWarningsJUnitRunnerTest$FailingWithSomeStubMismatches.test(StubbingWarningsJUnitRunnerTest.java:0)\n")), TestBase.filterLineNo(logger.getLoggedInfo()));
    }

    @Test
    public void validates_mockito_usage() {
        // when
        Result result = runner.run(StubbingWarningsJUnitRunnerTest.InvalidMockitoUsage.class);
        // then
        JUnitResultAssert.assertThat(result).fails(1, UnfinishedStubbingException.class);
    }

    @RunWith(TestableJUnitRunner.class)
    public static class PassingArgMismatch {
        IMethods mock = Mockito.mock(IMethods.class);

        @Test
        public void test() throws Exception {
            Mockito.when(mock.simpleMethod(1)).thenReturn("1");
            mock.simpleMethod(2);
        }
    }

    @RunWith(TestableJUnitRunner.class)
    public static class FailingWithArgMismatch {
        @Mock
        IMethods mock;

        @Test
        public void test() throws Exception {
            Mockito.when(mock.simpleMethod(1)).thenReturn("1");
            mock.simpleMethod(2);
            throw new RuntimeException("x");
        }
    }

    @RunWith(TestableJUnitRunner.class)
    public static class FailingWithMatchingArgs {
        @Mock
        IMethods mock;

        @Test
        public void test() throws Exception {
            Mockito.when(mock.simpleMethod(1)).thenReturn("1");
            mock.simpleMethod(1);
            throw new RuntimeException("x");
        }
    }

    @RunWith(TestableJUnitRunner.class)
    public static class FailingWithSomeStubMismatches {
        @Mock
        IMethods mock;

        @Test
        public void test() throws Exception {
            Mockito.when(mock.simpleMethod(1)).thenReturn("1");// <- used

            Mockito.when(mock.simpleMethod(2)).thenReturn("2");// <- unused

            mock.simpleMethod(1);// <- not reported

            mock.simpleMethod(3);// <- reported

            throw new RuntimeException("x");
        }
    }

    @RunWith(TestableJUnitRunner.class)
    public static class MismatchButStubAlreadyUsed {
        @Mock
        IMethods mock;

        @Test
        public void test() throws Exception {
            Mockito.when(mock.simpleMethod(1)).thenReturn("1");
            mock.simpleMethod(1);// <-- used

            mock.simpleMethod(2);// <-- arg mismatch, but the stub was already used

            throw new RuntimeException("x");
        }
    }

    @RunWith(TestableJUnitRunner.class)
    public static class InvalidMockitoUsage {
        @Mock
        IMethods mock;

        @SuppressWarnings({ "MockitoUsage", "CheckReturnValue" })
        @Test
        public void test() throws Exception {
            Mockito.when(mock.simpleMethod());// <-- unfinished stubbing

        }
    }
}

