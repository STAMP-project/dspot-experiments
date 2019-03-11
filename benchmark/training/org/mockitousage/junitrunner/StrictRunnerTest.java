/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.junitrunner;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.UnnecessaryStubbingException;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.mockitousage.IMethods;
import org.mockitoutil.JUnitResultAssert;
import org.mockitoutil.TestBase;


public class StrictRunnerTest extends TestBase {
    JUnitCore runner = new JUnitCore();

    @Test
    public void succeeds_when_all_stubs_were_used() {
        // when
        Result result = runner.run(StrictRunnerTest.StubbingInConstructorUsed.class, StrictRunnerTest.StubbingInBeforeUsed.class, StrictRunnerTest.StubbingInTestUsed.class);
        // then
        JUnitResultAssert.assertThat(result).isSuccessful();
    }

    @Test
    public void fails_when_stubs_were_not_used() {
        Class[] tests = new Class[]{ StrictRunnerTest.StubbingInConstructorUnused.class, StrictRunnerTest.StubbingInBeforeUnused.class, StrictRunnerTest.StubbingInTestUnused.class };
        // when
        Result result = runner.run(tests);
        // then
        JUnitResultAssert.assertThat(result).fails(3, UnnecessaryStubbingException.class);
    }

    @Test
    public void does_not_report_unused_stubs_when_different_failure_is_present() {
        // when
        Result result = runner.run(StrictRunnerTest.WithUnrelatedAssertionFailure.class);
        // then
        JUnitResultAssert.assertThat(result).fails(1, StrictRunnerTest.MyAssertionError.class);
    }

    @Test
    public void runner_can_coexist_with_rule() {
        // I don't believe that this scenario is useful
        // I only wish that Mockito does not break awkwardly when both: runner & rule is used
        // when
        Result result = runner.run(StrictRunnerTest.RunnerAndRule.class);
        // then
        JUnitResultAssert.assertThat(result).fails(1, UnnecessaryStubbingException.class);
    }

    @Test
    public void runner_in_multi_threaded_tests() {
        // when
        Result result = runner.run(StrictRunnerTest.StubUsedFromDifferentThread.class);
        // then
        JUnitResultAssert.assertThat(result).isSuccessful();
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class StubbingInConstructorUsed extends StrictRunnerTest.StubbingInConstructorUnused {
        @Test
        public void test() {
            Assert.assertEquals("1", mock.simpleMethod(1));
        }
    }

    // using Strict to make sure it does the right thing
    @RunWith(MockitoJUnitRunner.Strict.class)
    public static class StubbingInConstructorUnused {
        IMethods mock = Mockito.when(Mockito.mock(IMethods.class).simpleMethod(1)).thenReturn("1").getMock();

        @Test
        public void dummy() {
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class StubbingInBeforeUsed extends StrictRunnerTest.StubbingInBeforeUnused {
        @Test
        public void test() {
            Assert.assertEquals("1", mock.simpleMethod(1));
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class StubbingInBeforeUnused {
        @Mock
        IMethods mock;

        @Before
        public void before() {
            Mockito.when(mock.simpleMethod(1)).thenReturn("1");
        }

        @Test
        public void dummy() {
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class StubbingInTestUsed {
        @Test
        public void test() {
            IMethods mock = Mockito.mock(IMethods.class);
            Mockito.when(mock.simpleMethod(1)).thenReturn("1");
            Assert.assertEquals("1", mock.simpleMethod(1));
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class StubbingInTestUnused {
        @Test
        public void test() {
            IMethods mock = Mockito.mock(IMethods.class);
            Mockito.when(mock.simpleMethod(1)).thenReturn("1");
            mock.simpleMethod(2);// different arg

        }
    }

    private static class MyAssertionError extends AssertionError {}

    @RunWith(MockitoJUnitRunner.class)
    public static class WithUnrelatedAssertionFailure {
        IMethods mock = Mockito.mock(IMethods.class);

        IMethods mock2 = Mockito.mock(IMethods.class);

        @Before
        public void before() {
            Mockito.when(mock2.simpleMethod("unused stubbing")).thenReturn("");
        }

        @Test
        public void passing_test() {
            Mockito.when(mock.simpleMethod(1)).thenReturn("1");
            Assert.assertEquals("1", mock.simpleMethod(1));
        }

        @Test
        public void failing_test() {
            throw new StrictRunnerTest.MyAssertionError();
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class RunnerAndRule {
        @Rule
        public MockitoRule rule = MockitoJUnit.rule();

        IMethods mock = Mockito.mock(IMethods.class);

        @Test
        public void passing_test() {
            Mockito.when(mock.simpleMethod(1)).thenReturn("1");
            mock.simpleMethod(2);
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class StubUsedFromDifferentThread {
        IMethods mock = Mockito.mock(IMethods.class);

        @Test
        public void passing_test() throws Exception {
            // stubbing is done in main thread:
            Mockito.when(mock.simpleMethod(1)).thenReturn("1");
            // stubbing is used in a different thread
            // stubbing should not be reported as unused by the runner
            Thread t = new Thread() {
                public void run() {
                    mock.simpleMethod(1);
                }
            };
            t.start();
            t.join();
        }
    }
}

