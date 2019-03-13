package net.bytebuddy.dynamic;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class TransformerCompoundTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private Transformer<Object> first;

    @Mock
    private Transformer<Object> second;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private Object firstTarget;

    @Mock
    private Object secondTarget;

    @Mock
    private Object finalTarget;

    // In absence of @SafeVarargs
    @Test
    @SuppressWarnings("unchecked")
    public void testTransformation() throws Exception {
        MatcherAssert.assertThat(new Transformer.Compound<Object>(first, second).transform(typeDescription, firstTarget), CoreMatchers.is(finalTarget));
    }
}

