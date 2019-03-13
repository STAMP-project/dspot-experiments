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


public class MethodDescriptionTypeTokenTest {
    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription returnType;

    @Mock
    private TypeDescription parameterType;

    @Test
    public void testProperties() throws Exception {
        MethodDescription.TypeToken token = new MethodDescription.TypeToken(returnType, Collections.singletonList(parameterType));
        MatcherAssert.assertThat(token.getReturnType(), CoreMatchers.is(returnType));
        MatcherAssert.assertThat(token.getParameterTypes(), CoreMatchers.is(Collections.singletonList(parameterType)));
    }
}

