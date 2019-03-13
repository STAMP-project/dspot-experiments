package net.bytebuddy.dynamic.scaffold.inline;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class MethodNameTransformerPrefixingTest {
    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription methodDescription;

    @Test
    public void testTransformation() throws Exception {
        Mockito.when(methodDescription.getInternalName()).thenReturn(MethodNameTransformerPrefixingTest.FOO);
        String transformed = new MethodNameTransformer.Prefixing().transform(methodDescription);
        MatcherAssert.assertThat(transformed, CoreMatchers.not(MethodNameTransformerPrefixingTest.FOO));
        MatcherAssert.assertThat(transformed, CoreMatchers.endsWith(MethodNameTransformerPrefixingTest.FOO));
    }
}

