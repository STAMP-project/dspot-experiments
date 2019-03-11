package net.bytebuddy.description.annotation;


import java.lang.annotation.Annotation;
import org.junit.Test;

import static net.bytebuddy.description.annotation.AnnotationDescription.Builder.ofType;


public class AnnotationDescriptionBuilderTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    @Test(expected = IllegalArgumentException.class)
    public void testNonMatchingEnumerationValue() throws Exception {
        ofType(AnnotationDescriptionBuilderTest.Foo.class).define(AnnotationDescriptionBuilderTest.FOO, AnnotationDescriptionBuilderTest.FooBar.FIRST);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonMatchingAnnotationValue() throws Exception {
        ofType(AnnotationDescriptionBuilderTest.Qux.class).define(AnnotationDescriptionBuilderTest.FOO, new AnnotationDescriptionBuilderTest.QuxBaz.Instance());
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void testNonMatchingEnumerationArrayValue() throws Exception {
        ofType(AnnotationDescriptionBuilderTest.Foo.class).defineEnumerationArray(AnnotationDescriptionBuilderTest.BAR, ((Class) (AnnotationDescriptionBuilderTest.Bar.class)), AnnotationDescriptionBuilderTest.Bar.FIRST, AnnotationDescriptionBuilderTest.FooBar.SECOND);
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void testNonMatchingAnnotationArrayValue() throws Exception {
        ofType(AnnotationDescriptionBuilderTest.Foo.class).defineAnnotationArray(AnnotationDescriptionBuilderTest.BAZ, ((Class) (AnnotationDescriptionBuilderTest.Qux.class)), new AnnotationDescriptionBuilderTest.Qux.Instance(), new AnnotationDescriptionBuilderTest.QuxBaz.Instance());
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void testNonAnnotationType() throws Exception {
        ofType(((Class) (Object.class)));
    }

    @Test(expected = IllegalStateException.class)
    public void testIncompleteAnnotation() throws Exception {
        ofType(AnnotationDescriptionBuilderTest.Foo.class).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnknownProperty() throws Exception {
        ofType(AnnotationDescriptionBuilderTest.Foo.class).define(((AnnotationDescriptionBuilderTest.FOO) + (AnnotationDescriptionBuilderTest.BAR)), AnnotationDescriptionBuilderTest.FOO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalProperty() throws Exception {
        ofType(AnnotationDescriptionBuilderTest.Foo.class).define(AnnotationDescriptionBuilderTest.FOO, AnnotationDescriptionBuilderTest.FOO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateProperty() throws Exception {
        ofType(AnnotationDescriptionBuilderTest.Foo.class).define(AnnotationDescriptionBuilderTest.BAZ, AnnotationDescriptionBuilderTest.FOO).define(AnnotationDescriptionBuilderTest.BAZ, AnnotationDescriptionBuilderTest.FOO);
    }

    public enum Bar {

        FIRST,
        SECOND;}

    public enum FooBar {

        FIRST,
        SECOND;}

    public @interface Foo {
        AnnotationDescriptionBuilderTest.Bar foo();

        AnnotationDescriptionBuilderTest.Bar[] bar();

        AnnotationDescriptionBuilderTest.Qux qux();

        String baz();
    }

    public @interface Qux {
        class Instance implements AnnotationDescriptionBuilderTest.Qux {
            public Class<? extends Annotation> annotationType() {
                return AnnotationDescriptionBuilderTest.Qux.class;
            }
        }
    }

    public @interface QuxBaz {
        class Instance implements AnnotationDescriptionBuilderTest.QuxBaz {
            public Class<? extends Annotation> annotationType() {
                return AnnotationDescriptionBuilderTest.QuxBaz.class;
            }
        }
    }
}

