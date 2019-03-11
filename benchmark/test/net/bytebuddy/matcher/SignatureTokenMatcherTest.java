package net.bytebuddy.matcher;


import net.bytebuddy.description.method.MethodDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class SignatureTokenMatcherTest extends AbstractElementMatcherTest<SignatureTokenMatcher<?>> {
    @Mock
    private MethodDescription.SignatureToken token;

    @Mock
    private MethodDescription methodDescription;

    @Mock
    private ElementMatcher<? super MethodDescription.SignatureToken> matcher;

    @SuppressWarnings("unchecked")
    public SignatureTokenMatcherTest() {
        super(((Class<SignatureTokenMatcher<?>>) ((Object) (SignatureTokenMatcher.class))), "signature");
    }

    @Test
    public void testMatche() throws Exception {
        Mockito.when(methodDescription.asSignatureToken()).thenReturn(token);
        Mockito.when(matcher.matches(token)).thenReturn(true);
        MatcherAssert.assertThat(new SignatureTokenMatcher<MethodDescription>(matcher).matches(methodDescription), CoreMatchers.is(true));
        Mockito.verify(matcher).matches(token);
        Mockito.verifyNoMoreInteractions(matcher);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(methodDescription.asSignatureToken()).thenReturn(token);
        Mockito.when(matcher.matches(token)).thenReturn(false);
        MatcherAssert.assertThat(new SignatureTokenMatcher<MethodDescription>(matcher).matches(methodDescription), CoreMatchers.is(false));
        Mockito.verify(matcher).matches(token);
        Mockito.verifyNoMoreInteractions(matcher);
    }
}

