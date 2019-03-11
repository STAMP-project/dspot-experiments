package net.bytebuddy.description.annotation;


import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import net.bytebuddy.description.method.MethodDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class AnnotationDescriptionForLoadedAnnotationTest extends AbstractAnnotationDescriptionTest {
    private static final String FOO = "foo";

    @Test
    public void testAnnotationNonVisible() throws Exception {
        MatcherAssert.assertThat(describe(AnnotationDescriptionForLoadedAnnotationTest.Carrier.class.getAnnotation(AnnotationDescriptionForLoadedAnnotationTest.PrivateAnnotation.class), AnnotationDescriptionForLoadedAnnotationTest.Carrier.class).getValue(new MethodDescription.ForLoadedMethod(AnnotationDescriptionForLoadedAnnotationTest.PrivateAnnotation.class.getDeclaredMethod("value"))).resolve(String.class), CoreMatchers.is(AnnotationDescriptionForLoadedAnnotationTest.FOO));
    }

    @Test
    public void testAnnotationNonVisibleAccessible() throws Exception {
        Method method = AnnotationDescriptionForLoadedAnnotationTest.PrivateAnnotation.class.getDeclaredMethod("value");
        method.setAccessible(true);
        MatcherAssert.assertThat(describe(AnnotationDescriptionForLoadedAnnotationTest.Carrier.class.getAnnotation(AnnotationDescriptionForLoadedAnnotationTest.PrivateAnnotation.class), AnnotationDescriptionForLoadedAnnotationTest.Carrier.class).getValue(new MethodDescription.ForLoadedMethod(method)).resolve(String.class), CoreMatchers.is(AnnotationDescriptionForLoadedAnnotationTest.FOO));
    }

    @Test(expected = IllegalStateException.class)
    public void testInoperational() throws Exception {
        describe(AnnotationDescriptionForLoadedAnnotationTest.PrivateAnnotation.Defect.INSTANCE, AnnotationDescriptionForLoadedAnnotationTest.Carrier.class).getValue(new MethodDescription.ForLoadedMethod(AnnotationDescriptionForLoadedAnnotationTest.PrivateAnnotation.class.getDeclaredMethod("value")));
    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface PrivateAnnotation {
        String value();

        enum Defect implements AnnotationDescriptionForLoadedAnnotationTest.PrivateAnnotation {

            INSTANCE;
            public String value() {
                throw new RuntimeException();
            }

            public Class<? extends Annotation> annotationType() {
                return AnnotationDescriptionForLoadedAnnotationTest.PrivateAnnotation.class;
            }
        }
    }

    @AnnotationDescriptionForLoadedAnnotationTest.PrivateAnnotation(AnnotationDescriptionForLoadedAnnotationTest.FOO)
    private static class Carrier {}
}

