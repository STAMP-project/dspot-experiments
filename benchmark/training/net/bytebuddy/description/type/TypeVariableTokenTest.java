package net.bytebuddy.description.type;


import java.util.Collections;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class TypeVariableTokenTest {
    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic bound;

    @Mock
    private TypeDescription.Generic visitedBound;

    @Mock
    private AnnotationDescription annotation;

    @Mock
    private TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor;

    @Test
    public void testPropertiesSimple() throws Exception {
        MatcherAssert.assertThat(new TypeVariableToken(TypeVariableTokenTest.FOO, Collections.singletonList(bound)).getSymbol(), CoreMatchers.is(TypeVariableTokenTest.FOO));
        MatcherAssert.assertThat(new TypeVariableToken(TypeVariableTokenTest.FOO, Collections.singletonList(bound)).getBounds(), CoreMatchers.is(Collections.singletonList(bound)));
        MatcherAssert.assertThat(new TypeVariableToken(TypeVariableTokenTest.FOO, Collections.singletonList(bound)).getAnnotations().size(), CoreMatchers.is(0));
    }

    @Test
    public void testProperties() throws Exception {
        MatcherAssert.assertThat(new TypeVariableToken(TypeVariableTokenTest.FOO, Collections.singletonList(bound), Collections.singletonList(annotation)).getSymbol(), CoreMatchers.is(TypeVariableTokenTest.FOO));
        MatcherAssert.assertThat(new TypeVariableToken(TypeVariableTokenTest.FOO, Collections.singletonList(bound), Collections.singletonList(annotation)).getBounds(), CoreMatchers.is(Collections.singletonList(bound)));
        MatcherAssert.assertThat(new TypeVariableToken(TypeVariableTokenTest.FOO, Collections.singletonList(bound), Collections.singletonList(annotation)).getAnnotations(), CoreMatchers.is(Collections.singletonList(annotation)));
    }

    @Test
    public void testVisitor() throws Exception {
        MatcherAssert.assertThat(new TypeVariableToken(TypeVariableTokenTest.FOO, Collections.singletonList(bound)).accept(visitor), CoreMatchers.is(new TypeVariableToken(TypeVariableTokenTest.FOO, Collections.singletonList(visitedBound))));
    }
}

