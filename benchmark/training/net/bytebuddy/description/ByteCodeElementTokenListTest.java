package net.bytebuddy.description;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ByteCodeElementTokenListTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private ByteCodeElement.Token<?> original;

    @Mock
    private ByteCodeElement.Token<?> transformed;

    @Mock
    private TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor;

    @Test
    @SuppressWarnings("unchecked")
    public void testTransformation() throws Exception {
        Mockito.when(original.accept(visitor)).thenReturn(((ByteCodeElement.Token) (transformed)));
        ByteCodeElement.Token.TokenList<?> tokenList = new ByteCodeElement.Token.TokenList(original).accept(visitor);
        MatcherAssert.assertThat(tokenList.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(tokenList.get(0), CoreMatchers.is(((ByteCodeElement.Token) (transformed))));
    }
}

