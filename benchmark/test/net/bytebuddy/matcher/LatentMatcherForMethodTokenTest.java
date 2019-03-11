package net.bytebuddy.matcher;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class LatentMatcherForMethodTokenTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription.Token token;

    @Mock
    private MethodDescription.SignatureToken signatureToken;

    @Mock
    private MethodDescription.SignatureToken otherToken;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private MethodDescription methodDescription;

    @Test
    public void testMatch() throws Exception {
        Mockito.when(methodDescription.asSignatureToken()).thenReturn(signatureToken);
        Mockito.when(token.asSignatureToken(typeDescription)).thenReturn(signatureToken);
        MatcherAssert.assertThat(new LatentMatcher.ForMethodToken(token).resolve(typeDescription).matches(methodDescription), CoreMatchers.is(true));
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(methodDescription.asSignatureToken()).thenReturn(signatureToken);
        Mockito.when(token.asSignatureToken(typeDescription)).thenReturn(otherToken);
        MatcherAssert.assertThat(new LatentMatcher.ForMethodToken(token).resolve(typeDescription).matches(methodDescription), CoreMatchers.is(false));
    }
}

