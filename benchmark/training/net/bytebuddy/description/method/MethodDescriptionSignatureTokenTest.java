package net.bytebuddy.description.method;


import java.util.Collections;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class MethodDescriptionSignatureTokenTest {
    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription returnType;

    @Mock
    private TypeDescription parameterType;

    @Test
    public void testProperties() throws Exception {
        MethodDescription.SignatureToken token = new MethodDescription.SignatureToken(MethodDescriptionSignatureTokenTest.FOO, returnType, Collections.singletonList(parameterType));
        MatcherAssert.assertThat(token.getName(), CoreMatchers.is(MethodDescriptionSignatureTokenTest.FOO));
        MatcherAssert.assertThat(token.getReturnType(), CoreMatchers.is(returnType));
        MatcherAssert.assertThat(token.getParameterTypes(), CoreMatchers.is(Collections.singletonList(parameterType)));
    }

    @Test
    public void testTypeToken() throws Exception {
        MatcherAssert.assertThat(new MethodDescription.SignatureToken(MethodDescriptionSignatureTokenTest.FOO, returnType, Collections.singletonList(parameterType)).asTypeToken(), CoreMatchers.is(new MethodDescription.TypeToken(returnType, Collections.singletonList(parameterType))));
    }
}

