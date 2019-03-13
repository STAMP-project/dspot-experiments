package net.bytebuddy.dynamic.scaffold.inline;


import java.util.Collections;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class RebaseDynamicTypeBuilderRebaseableMatcherTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription.Token targetToken;

    @Mock
    private MethodDescription.Token otherToken;

    @Test
    public void testMatchToken() throws Exception {
        Assert.assertThat(new RebaseDynamicTypeBuilder.RebaseableMatcher(Collections.singleton(targetToken)).matches(targetToken), CoreMatchers.is(true));
    }

    @Test
    public void testNoMatchToken() throws Exception {
        Assert.assertThat(new RebaseDynamicTypeBuilder.RebaseableMatcher(Collections.singleton(otherToken)).matches(targetToken), CoreMatchers.is(false));
    }
}

