package net.bytebuddy.description.type;


import java.util.Collection;
import net.bytebuddy.description.annotation.AnnotationDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.description.type.TypeDescription.Generic.Builder.parameterizedType;
import static net.bytebuddy.description.type.TypeDescription.Generic.Builder.rawType;
import static net.bytebuddy.description.type.TypeDescription.Generic.Builder.typeVariable;
import static net.bytebuddy.description.type.TypeDescription.Generic.Visitor.AnnotationStripper.INSTANCE;


public class TypeDescriptionGenericVisitorAnnotationStripperTest {
    private static final String FOO = "foo";

    private AnnotationDescription annotationDescription;

    @Test
    public void testWildcardLowerBound() throws Exception {
        TypeDescription.Generic typeDescription = rawType(Object.class).annotate(annotationDescription).asWildcardLowerBound(annotationDescription);
        MatcherAssert.assertThat(INSTANCE.onWildcard(typeDescription), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(INSTANCE.onWildcard(typeDescription).getDeclaredAnnotations().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(INSTANCE.onWildcard(typeDescription).getLowerBounds().getOnly().getDeclaredAnnotations().size(), CoreMatchers.is(0));
    }

    @Test
    public void testWildcardUpperBound() throws Exception {
        TypeDescription.Generic typeDescription = rawType(Object.class).annotate(annotationDescription).asWildcardLowerBound(annotationDescription);
        MatcherAssert.assertThat(INSTANCE.onWildcard(typeDescription), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(INSTANCE.onWildcard(typeDescription).getDeclaredAnnotations().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(INSTANCE.onWildcard(typeDescription).getUpperBounds().getOnly().getDeclaredAnnotations().size(), CoreMatchers.is(0));
    }

    @Test
    public void testGenericArray() throws Exception {
        TypeDescription.Generic typeDescription = rawType(Object.class).annotate(annotationDescription).asArray().annotate(annotationDescription).build();
        MatcherAssert.assertThat(INSTANCE.onGenericArray(typeDescription), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(INSTANCE.onGenericArray(typeDescription).getDeclaredAnnotations().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(INSTANCE.onGenericArray(typeDescription).getComponentType().getDeclaredAnnotations().size(), CoreMatchers.is(0));
    }

    @Test
    public void testNonGenericArray() throws Exception {
        TypeDescription.Generic typeDescription = rawType(Object.class).annotate(annotationDescription).asArray().annotate(annotationDescription).build();
        MatcherAssert.assertThat(INSTANCE.onNonGenericType(typeDescription), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(INSTANCE.onNonGenericType(typeDescription).getDeclaredAnnotations().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(INSTANCE.onNonGenericType(typeDescription).getComponentType().getDeclaredAnnotations().size(), CoreMatchers.is(0));
    }

    @Test
    public void testNonGeneric() throws Exception {
        TypeDescription.Generic typeDescription = rawType(Object.class).annotate(annotationDescription).build();
        MatcherAssert.assertThat(INSTANCE.onNonGenericType(typeDescription), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(INSTANCE.onNonGenericType(typeDescription).getDeclaredAnnotations().size(), CoreMatchers.is(0));
    }

    @Test
    public void testTypeVariable() throws Exception {
        TypeDescription.Generic typeDescription = typeVariable(TypeDescriptionGenericVisitorAnnotationStripperTest.FOO).annotate(annotationDescription).build();
        MatcherAssert.assertThat(INSTANCE.onTypeVariable(typeDescription).getSymbol(), CoreMatchers.is(TypeDescriptionGenericVisitorAnnotationStripperTest.FOO));
        MatcherAssert.assertThat(INSTANCE.onTypeVariable(typeDescription).getDeclaredAnnotations().size(), CoreMatchers.is(0));
    }

    @Test
    public void testParameterized() throws Exception {
        TypeDescription.Generic typeDescription = parameterizedType(of(Collection.class), rawType(Object.class).annotate(annotationDescription).build()).annotate(annotationDescription).build();
        MatcherAssert.assertThat(INSTANCE.onParameterizedType(typeDescription), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(INSTANCE.onParameterizedType(typeDescription).getDeclaredAnnotations().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(INSTANCE.onParameterizedType(typeDescription).getTypeArguments().getOnly().getDeclaredAnnotations().size(), CoreMatchers.is(0));
    }

    private @interface Foo {}
}

