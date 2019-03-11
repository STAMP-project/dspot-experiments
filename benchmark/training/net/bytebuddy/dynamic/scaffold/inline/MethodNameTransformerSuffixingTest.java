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


public class MethodNameTransformerSuffixingTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription methodDescription;

    @Test
    public void testTransformation() throws Exception {
        Mockito.when(methodDescription.getInternalName()).thenReturn(MethodNameTransformerSuffixingTest.FOO);
        String transformed = new MethodNameTransformer.Suffixing(MethodNameTransformerSuffixingTest.BAR).transform(methodDescription);
        MatcherAssert.assertThat(transformed, CoreMatchers.is((((MethodNameTransformerSuffixingTest.FOO) + "$") + (MethodNameTransformerSuffixingTest.BAR))));
    }
}

