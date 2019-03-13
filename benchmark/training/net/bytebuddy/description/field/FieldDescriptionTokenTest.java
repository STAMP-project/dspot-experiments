package net.bytebuddy.description.field;


import java.util.Collections;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class FieldDescriptionTokenTest {
    private static final String FOO = "foo";

    private static final int MODIFIERS = 42;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic type;

    @Mock
    private TypeDescription.Generic visitedType;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private TypeDescription rawType;

    @Mock
    private AnnotationDescription annotation;

    @Mock
    private TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor;

    @Test
    public void testProperties() throws Exception {
        FieldDescription.Token token = new FieldDescription.Token(FieldDescriptionTokenTest.FOO, FieldDescriptionTokenTest.MODIFIERS, type, Collections.singletonList(annotation));
        MatcherAssert.assertThat(token.getName(), CoreMatchers.is(FieldDescriptionTokenTest.FOO));
        MatcherAssert.assertThat(token.getModifiers(), CoreMatchers.is(FieldDescriptionTokenTest.MODIFIERS));
        MatcherAssert.assertThat(token.getType(), CoreMatchers.is(type));
        MatcherAssert.assertThat(token.getAnnotations(), CoreMatchers.is(Collections.singletonList(annotation)));
    }

    @Test
    public void testVisitor() throws Exception {
        MatcherAssert.assertThat(new FieldDescription.Token(FieldDescriptionTokenTest.FOO, FieldDescriptionTokenTest.MODIFIERS, type, Collections.singletonList(annotation)).accept(visitor), CoreMatchers.is(new FieldDescription.Token(FieldDescriptionTokenTest.FOO, FieldDescriptionTokenTest.MODIFIERS, visitedType, Collections.singletonList(annotation))));
    }

    @Test
    public void testSignatureTokenTransformation() throws Exception {
        Mockito.when(type.accept(FieldByFieldComparison.matchesPrototype(new TypeDescription.Generic.Visitor.Reducing(typeDescription)))).thenReturn(rawType);
        MatcherAssert.assertThat(new FieldDescription.Token(FieldDescriptionTokenTest.FOO, FieldDescriptionTokenTest.MODIFIERS, type, Collections.singletonList(annotation)).asSignatureToken(typeDescription), CoreMatchers.is(new FieldDescription.SignatureToken(FieldDescriptionTokenTest.FOO, rawType)));
    }
}

