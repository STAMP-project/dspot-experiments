package net.bytebuddy.description.method;


import java.util.Collections;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class ParameterDescriptionTokenTest {
    private static final String FOO = "foo";

    private static final int MODIFIERS = 42;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic type;

    @Mock
    private TypeDescription.Generic visitedType;

    @Mock
    private AnnotationDescription annotation;

    @Mock
    private TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor;

    @Test
    public void testProperties() throws Exception {
        ParameterDescription.Token token = new ParameterDescription.Token(type, Collections.singletonList(annotation), ParameterDescriptionTokenTest.FOO, ParameterDescriptionTokenTest.MODIFIERS);
        MatcherAssert.assertThat(token.getType(), CoreMatchers.is(type));
        MatcherAssert.assertThat(token.getAnnotations(), CoreMatchers.is(Collections.singletonList(annotation)));
        MatcherAssert.assertThat(token.getName(), CoreMatchers.is(ParameterDescriptionTokenTest.FOO));
        MatcherAssert.assertThat(token.getModifiers(), CoreMatchers.is(ParameterDescriptionTokenTest.MODIFIERS));
    }

    @Test
    public void testVisitor() throws Exception {
        MatcherAssert.assertThat(new ParameterDescription.Token(type, Collections.singletonList(annotation), ParameterDescriptionTokenTest.FOO, ParameterDescriptionTokenTest.MODIFIERS).accept(visitor), CoreMatchers.is(new ParameterDescription.Token(visitedType, Collections.singletonList(annotation), ParameterDescriptionTokenTest.FOO, ParameterDescriptionTokenTest.MODIFIERS)));
    }
}

