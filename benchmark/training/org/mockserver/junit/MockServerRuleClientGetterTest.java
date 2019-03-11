package org.mockserver.junit;


import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class MockServerRuleClientGetterTest {
    @Rule
    public MockServerRule mockServerRule = new MockServerRule(this);

    @Test
    public void shouldSetTestMockServeField() {
        Assert.assertThat(mockServerRule.getClient(), Is.is(IsNot.not(Matchers.nullValue())));
    }
}

