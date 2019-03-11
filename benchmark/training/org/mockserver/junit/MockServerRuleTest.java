package org.mockserver.junit;


import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsSame;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.MockServerClient;


/**
 *
 *
 * @author jamesdbloom
 */
public class MockServerRuleTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);

    private MockServerClient mockServerClient;

    @Test
    public void shouldSetTestMockServeField() {
        Assert.assertThat(mockServerClient, Is.is(IsNot.not(Matchers.nullValue())));
    }

    @Test
    public void shouldSetTestMockServerFieldWithSameValueFromGetter() {
        Assert.assertThat(mockServerClient, IsSame.sameInstance(mockServerRule.getClient()));
    }
}

