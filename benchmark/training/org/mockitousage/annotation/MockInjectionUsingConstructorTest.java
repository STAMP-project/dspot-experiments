/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.annotation;


import java.util.AbstractCollection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.rules.ExpectedException;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.internal.util.MockUtil;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockitousage.IMethods;
import org.mockitousage.examples.use.ArticleCalculator;
import org.mockitousage.examples.use.ArticleDatabase;
import org.mockitousage.examples.use.ArticleManager;


public class MockInjectionUsingConstructorTest {
    @Mock
    private ArticleCalculator calculator;

    @Mock
    private ArticleDatabase database;

    @InjectMocks
    private ArticleManager articleManager;

    @Spy
    @InjectMocks
    private ArticleManager spiedArticleManager;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldNotFailWhenNotInitialized() {
        Assert.assertNotNull(articleManager);
    }

    @Test(expected = IllegalArgumentException.class)
    public void innerMockShouldRaiseAnExceptionThatChangesOuterMockBehavior() {
        Mockito.when(calculator.countArticles("new")).thenThrow(new IllegalArgumentException());
        articleManager.updateArticleCounters("new");
    }

    @Test
    public void mockJustWorks() {
        articleManager.updateArticleCounters("new");
    }

    @Test
    public void constructor_is_called_for_each_test_in_test_class() throws Exception {
        // given
        MockInjectionUsingConstructorTest.junit_test_with_3_tests_methods.constructor_instantiation = 0;
        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.addListener(new TextListener(System.out));
        // when
        jUnitCore.run(MockInjectionUsingConstructorTest.junit_test_with_3_tests_methods.class);
        // then
        Assert.assertThat(MockInjectionUsingConstructorTest.junit_test_with_3_tests_methods.constructor_instantiation).isEqualTo(3);
    }

    @Test
    public void objects_created_with_constructor_initialization_can_be_spied() throws Exception {
        Assert.assertFalse(MockUtil.isMock(articleManager));
        Assert.assertTrue(MockUtil.isMock(spiedArticleManager));
    }

    @Test
    public void should_report_failure_only_when_object_initialization_throws_exception() throws Exception {
        try {
            MockitoAnnotations.initMocks(new MockInjectionUsingConstructorTest.ATest());
            Assert.fail();
        } catch (MockitoException e) {
            Assert.assertThat(e.getMessage()).contains("failingConstructor").contains("constructor").contains("threw an exception");
            Assert.assertThat(e.getCause()).isInstanceOf(IllegalStateException.class);
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class junit_test_with_3_tests_methods {
        private static int constructor_instantiation = 0;

        @Mock
        List<?> some_collaborator;

        @InjectMocks
        MockInjectionUsingConstructorTest.junit_test_with_3_tests_methods.some_class_with_parametered_constructor should_be_initialized_3_times;

        @Test
        public void test_1() {
        }

        @Test
        public void test_2() {
        }

        @Test
        public void test_3() {
        }

        private static class some_class_with_parametered_constructor {
            public some_class_with_parametered_constructor(List<?> collaborator) {
                (MockInjectionUsingConstructorTest.junit_test_with_3_tests_methods.constructor_instantiation)++;
            }
        }
    }

    private static class FailingConstructor {
        FailingConstructor(Set<?> set) {
            throw new IllegalStateException("always fail");
        }
    }

    @Ignore("don't run this code in the test runner")
    private static class ATest {
        @Mock
        Set<?> set;

        @InjectMocks
        MockInjectionUsingConstructorTest.FailingConstructor failingConstructor;
    }

    @Test
    public void injectMocksMustFailWithInterface() throws Exception {
        class TestCase {
            @InjectMocks
            IMethods f;
        }
        exception.expect(MockitoException.class);
        exception.expectMessage("Cannot instantiate @InjectMocks field named 'f'! Cause: the type 'IMethods' is an interface");
        MockitoAnnotations.initMocks(new TestCase());
    }

    @Test
    public void injectMocksMustFailWithEnum() throws Exception {
        class TestCase {
            @InjectMocks
            TimeUnit f;
        }
        exception.expect(MockitoException.class);
        exception.expectMessage("Cannot instantiate @InjectMocks field named 'f'! Cause: the type 'TimeUnit' is an enum");
        MockitoAnnotations.initMocks(new TestCase());
    }

    @Test
    public void injectMocksMustFailWithAbstractClass() throws Exception {
        class TestCase {
            @InjectMocks
            AbstractCollection<?> f;
        }
        exception.expect(MockitoException.class);
        exception.expectMessage("Cannot instantiate @InjectMocks field named 'f'! Cause: the type 'AbstractCollection' is an abstract class");
        MockitoAnnotations.initMocks(new TestCase());
    }

    @Test
    public void injectMocksMustFailWithNonStaticInnerClass() throws Exception {
        class TestCase {
            class InnerClass {}

            @InjectMocks
            TestCase.InnerClass f;
        }
        exception.expect(MockitoException.class);
        exception.expectMessage("Cannot instantiate @InjectMocks field named 'f'! Cause: the type 'InnerClass' is an inner non static class");
        MockitoAnnotations.initMocks(new TestCase());
    }

    static class StaticInnerClass {}

    @Test
    public void injectMocksMustSucceedWithStaticInnerClass() throws Exception {
        class TestCase {
            @InjectMocks
            MockInjectionUsingConstructorTest.StaticInnerClass f;
        }
        TestCase testClass = new TestCase();
        MockitoAnnotations.initMocks(testClass);
        Assert.assertThat(testClass.f).isInstanceOf(MockInjectionUsingConstructorTest.StaticInnerClass.class);
    }

    @Test
    public void injectMocksMustSucceedWithInstance() throws Exception {
        class TestCase {
            @InjectMocks
            MockInjectionUsingConstructorTest.StaticInnerClass f = new MockInjectionUsingConstructorTest.StaticInnerClass();
        }
        TestCase testClass = new TestCase();
        MockInjectionUsingConstructorTest.StaticInnerClass original = testClass.f;
        MockitoAnnotations.initMocks(testClass);
        Assert.assertThat(testClass.f).isSameAs(original);
    }
}

