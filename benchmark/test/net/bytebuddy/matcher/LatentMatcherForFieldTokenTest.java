package net.bytebuddy.matcher;


import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class LatentMatcherForFieldTokenTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private FieldDescription.Token token;

    @Mock
    private FieldDescription.SignatureToken signatureToken;

    @Mock
    private FieldDescription.SignatureToken otherSignatureToken;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private FieldDescription fieldDescription;

    @Test
    public void testMatch() throws Exception {
        Mockito.when(fieldDescription.asSignatureToken()).thenReturn(signatureToken);
        Mockito.when(token.asSignatureToken(typeDescription)).thenReturn(signatureToken);
        MatcherAssert.assertThat(new LatentMatcher.ForFieldToken(token).resolve(typeDescription).matches(fieldDescription), CoreMatchers.is(true));
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(fieldDescription.asSignatureToken()).thenReturn(otherSignatureToken);
        Mockito.when(token.asSignatureToken(typeDescription)).thenReturn(signatureToken);
        MatcherAssert.assertThat(new LatentMatcher.ForFieldToken(token).resolve(typeDescription).matches(fieldDescription), CoreMatchers.is(false));
    }
}

