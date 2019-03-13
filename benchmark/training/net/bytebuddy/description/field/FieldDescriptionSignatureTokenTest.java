package net.bytebuddy.description.field;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class FieldDescriptionSignatureTokenTest {
    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription type;

    @Test
    public void testProperties() throws Exception {
        FieldDescription.SignatureToken token = new FieldDescription.SignatureToken(FieldDescriptionSignatureTokenTest.FOO, type);
        MatcherAssert.assertThat(token.getName(), CoreMatchers.is(FieldDescriptionSignatureTokenTest.FOO));
        MatcherAssert.assertThat(token.getType(), CoreMatchers.is(type));
    }
}

