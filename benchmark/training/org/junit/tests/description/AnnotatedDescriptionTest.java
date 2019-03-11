package org.junit.tests.description;


import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;


public class AnnotatedDescriptionTest {
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyOwnAnnotation {}

    @AnnotatedDescriptionTest.MyOwnAnnotation
    public static class AnnotatedClass {
        @Test
        public void a() {
        }
    }

    @Test
    public void annotationsExistOnDescriptionsOfClasses() {
        Assert.assertTrue(((describe(AnnotatedDescriptionTest.AnnotatedClass.class).getAnnotation(AnnotatedDescriptionTest.MyOwnAnnotation.class)) != null));
    }

    @Test
    public void getAnnotationsReturnsAllAnnotations() {
        Assert.assertEquals(1, describe(AnnotatedDescriptionTest.ValueAnnotatedClass.class).getAnnotations().size());
    }

    @Ignore
    public static class IgnoredClass {
        @Test
        public void a() {
        }
    }

    @Test
    public void annotationsExistOnDescriptionsOfIgnoredClass() {
        Assert.assertTrue(((describe(AnnotatedDescriptionTest.IgnoredClass.class).getAnnotation(Ignore.class)) != null));
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface ValuedAnnotation {
        String value();
    }

    @AnnotatedDescriptionTest.ValuedAnnotation("hello")
    public static class ValueAnnotatedClass {
        @Test
        public void a() {
        }
    }

    @Test
    public void descriptionOfTestClassHasValuedAnnotation() {
        Description description = describe(AnnotatedDescriptionTest.ValueAnnotatedClass.class);
        Assert.assertEquals("hello", description.getAnnotation(AnnotatedDescriptionTest.ValuedAnnotation.class).value());
    }

    @Test
    public void childlessCopyOfDescriptionStillHasAnnotations() {
        Description description = describe(AnnotatedDescriptionTest.ValueAnnotatedClass.class);
        Assert.assertEquals("hello", description.childlessCopy().getAnnotation(AnnotatedDescriptionTest.ValuedAnnotation.class).value());
    }

    @Test
    public void characterizeCreatingMyOwnAnnotation() {
        Annotation annotation = new Ignore() {
            public String value() {
                return "message";
            }

            public Class<? extends Annotation> annotationType() {
                return Ignore.class;
            }
        };
        Assert.assertEquals(Ignore.class, annotation.annotationType());
    }
}

