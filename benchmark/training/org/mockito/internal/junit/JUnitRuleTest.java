/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.junit;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.UnfinishedStubbingException;
import org.mockito.junit.MockitoJUnit;
import org.mockitousage.IMethods;
import org.mockitoutil.SafeJUnitRule;


public class JUnitRuleTest {
    @Rule
    public SafeJUnitRule rule = new SafeJUnitRule(MockitoJUnit.rule());

    @Mock
    IMethods mock;

    @Test
    public void injects_into_test_case() throws Throwable {
        Assert.assertTrue(Mockito.mockingDetails(mock).isMock());
    }

    @Test
    public void rethrows_exception() throws Throwable {
        rule.expectFailure(RuntimeException.class, "foo");
        throw new RuntimeException("foo");
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test
    public void detects_invalid_mockito_usage_on_success() throws Throwable {
        rule.expectFailure(UnfinishedStubbingException.class);
        Mockito.when(mock.simpleMethod());
    }

    @SuppressWarnings({ "CheckReturnValue", "MockitoUsage" })
    @Test
    public void does_not_check_invalid_mockito_usage_on_failure() throws Throwable {
        // This intended behavior is questionable
        // However, it was like that since the beginning of JUnit rule support
        // Users never questioned this behavior. Hence, let's stick to it unless we have more data
        rule.expectFailure(RuntimeException.class, "foo");
        Mockito.when(mock.simpleMethod());// <--- unfinished stubbing

        throw new RuntimeException("foo");// <--- some failure

    }
}

