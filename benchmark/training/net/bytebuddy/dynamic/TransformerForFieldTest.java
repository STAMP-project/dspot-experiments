package net.bytebuddy.dynamic;


import java.util.Collections;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.description.type.TypeDefinition.Sort.PARAMETERIZED;
import static net.bytebuddy.dynamic.Transformer.ForField.withModifiers;


public class TransformerForFieldTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final int MODIFIERS = 42;

    private static final int RANGE = 3;

    private static final int MASK = 1;

    @Rule
    public TestRule mocktioRule = new MockitoRule(this);

    @Mock
    private TypeDescription instrumentedType;

    @Mock
    private Transformer<FieldDescription.Token> tokenTransformer;

    @Mock
    private FieldDescription fieldDescription;

    @Mock
    private FieldDescription.InDefinedShape definedField;

    @Mock
    private FieldDescription.Token fieldToken;

    @Mock
    private TypeDescription.Generic fieldType;

    @Mock
    private TypeDescription.Generic declaringType;

    @Mock
    private AnnotationDescription fieldAnnotation;

    @Mock
    private ModifierContributor.ForField modifierContributor;

    @Test
    public void testSimpleTransformation() throws Exception {
        Mockito.when(tokenTransformer.transform(instrumentedType, fieldToken)).thenReturn(fieldToken);
        FieldDescription transformed = new Transformer.ForField(tokenTransformer).transform(instrumentedType, fieldDescription);
        MatcherAssert.assertThat(transformed.getDeclaringType(), CoreMatchers.is(((TypeDefinition) (declaringType))));
        MatcherAssert.assertThat(transformed.getInternalName(), CoreMatchers.is(TransformerForFieldTest.FOO));
        MatcherAssert.assertThat(transformed.getModifiers(), CoreMatchers.is(TransformerForFieldTest.MODIFIERS));
        MatcherAssert.assertThat(transformed.getDeclaredAnnotations(), CoreMatchers.is(Collections.singletonList(fieldAnnotation)));
        MatcherAssert.assertThat(transformed.getType(), CoreMatchers.is(fieldType));
        MatcherAssert.assertThat(transformed.asDefined(), CoreMatchers.is(definedField));
    }

    @Test
    public void testModifierTransformation() throws Exception {
        FieldDescription.Token transformed = new Transformer.ForField.FieldModifierTransformer(ModifierContributor.Resolver.of(modifierContributor)).transform(instrumentedType, fieldToken);
        MatcherAssert.assertThat(transformed.getName(), CoreMatchers.is(TransformerForFieldTest.FOO));
        MatcherAssert.assertThat(transformed.getModifiers(), CoreMatchers.is((((TransformerForFieldTest.MODIFIERS) & (~(TransformerForFieldTest.RANGE))) | (TransformerForFieldTest.MASK))));
        MatcherAssert.assertThat(transformed.getType(), CoreMatchers.is(fieldType));
    }

    @Test
    public void testNoChangesUnlessSpecified() throws Exception {
        TypeDescription typeDescription = of(TransformerForFieldTest.Bar.class);
        FieldDescription fieldDescription = typeDescription.getSuperClass().getDeclaredFields().filter(ElementMatchers.named(TransformerForFieldTest.FOO)).getOnly();
        FieldDescription transformed = withModifiers().transform(typeDescription, fieldDescription);
        MatcherAssert.assertThat(transformed, CoreMatchers.is(fieldDescription));
        MatcherAssert.assertThat(transformed.getModifiers(), CoreMatchers.is(fieldDescription.getModifiers()));
    }

    @Test
    public void testRetainsInstrumentedType() throws Exception {
        TypeDescription typeDescription = of(TransformerForFieldTest.Bar.class);
        FieldDescription fieldDescription = typeDescription.getSuperClass().getDeclaredFields().filter(ElementMatchers.named(TransformerForFieldTest.BAR)).getOnly();
        FieldDescription transformed = withModifiers().transform(typeDescription, fieldDescription);
        MatcherAssert.assertThat(transformed, CoreMatchers.is(fieldDescription));
        MatcherAssert.assertThat(transformed.getModifiers(), CoreMatchers.is(fieldDescription.getModifiers()));
        MatcherAssert.assertThat(transformed.getType().asErasure(), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(transformed.getType().getSort(), CoreMatchers.is(PARAMETERIZED));
        MatcherAssert.assertThat(transformed.getType().getTypeArguments().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(transformed.getType().getTypeArguments().getOnly(), CoreMatchers.is(typeDescription.getSuperClass().getDeclaredFields().filter(ElementMatchers.named(TransformerForFieldTest.FOO)).getOnly().getType()));
    }

    private static class Foo<T> {
        T foo;

        TransformerForFieldTest.Bar<T> bar;
    }

    /* empty */
    private static class Bar<S> extends TransformerForFieldTest.Foo<S> {}
}

