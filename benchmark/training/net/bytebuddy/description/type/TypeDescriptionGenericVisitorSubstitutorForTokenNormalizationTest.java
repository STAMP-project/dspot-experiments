package net.bytebuddy.description.type;


import java.util.Collections;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.dynamic.TargetType;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class TypeDescriptionGenericVisitorSubstitutorForTokenNormalizationTest {
    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription target;

    @Mock
    private TypeDescription.Generic source;

    @Mock
    private AnnotationDescription annotationDescription;

    @Test
    public void testTargetType() throws Exception {
        TypeDescription.Generic typeDescription = new TypeDescription.Generic.Visitor.Substitutor.ForTokenNormalization(target).onSimpleType(new TypeDescription.Generic.OfNonGenericType.Latent(TargetType.DESCRIPTION, new AnnotationSource.Explicit(annotationDescription)));
        Assert.assertThat(typeDescription.asErasure(), CoreMatchers.is(target));
        Assert.assertThat(typeDescription.getDeclaredAnnotations(), CoreMatchers.is(Collections.singletonList(annotationDescription)));
    }

    @Test
    public void testNotTargetType() throws Exception {
        Assert.assertThat(new TypeDescription.Generic.Visitor.Substitutor.ForTokenNormalization(target).onSimpleType(source), CoreMatchers.sameInstance(source));
    }

    @Test
    public void testTypeVariable() throws Exception {
        TypeDescription.Generic typeDescription = new TypeDescription.Generic.Visitor.Substitutor.ForTokenNormalization(target).onTypeVariable(source);
        Assert.assertThat(typeDescription, CoreMatchers.is(((TypeDescription.Generic) (new TypeDescription.Generic.OfTypeVariable.Symbolic(TypeDescriptionGenericVisitorSubstitutorForTokenNormalizationTest.FOO, new AnnotationSource.Explicit(annotationDescription))))));
        Assert.assertThat(typeDescription.getDeclaredAnnotations(), CoreMatchers.is(Collections.singletonList(annotationDescription)));
    }
}

