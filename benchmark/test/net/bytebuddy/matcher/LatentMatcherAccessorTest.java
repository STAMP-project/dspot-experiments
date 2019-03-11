package net.bytebuddy.matcher;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class LatentMatcherAccessorTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private ElementMatcher<? super Object> matcher;

    @Mock
    private TypeDescription typeDescription;

    @Test
    @SuppressWarnings("unchecked")
    public void testManifestation() throws Exception {
        LatentMatcher<Object> matcher = new LatentMatcher.Resolved<Object>(this.matcher);
        MatcherAssert.assertThat(matcher.resolve(typeDescription), CoreMatchers.is(((ElementMatcher) (this.matcher))));
        Mockito.verifyZeroInteractions(this.matcher);
        Mockito.verifyZeroInteractions(typeDescription);
    }
}

