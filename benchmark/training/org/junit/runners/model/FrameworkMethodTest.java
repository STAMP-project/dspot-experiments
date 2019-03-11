package org.junit.runners.model;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class FrameworkMethodTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void cannotBeCreatedWithoutUnderlyingField() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("FrameworkMethod cannot be created without an underlying method.");
        new FrameworkMethod(null);
    }

    @Test
    public void hasToStringWhichPrintsMethodName() throws Exception {
        Method method = FrameworkMethodTest.ClassWithDummyMethod.class.getMethod("dummyMethod");
        FrameworkMethod frameworkMethod = new FrameworkMethod(method);
        Assert.assertTrue(frameworkMethod.toString().contains("dummyMethod"));
    }

    @Test
    public void presentAnnotationIsAvailable() throws Exception {
        Method method = FrameworkMethodTest.ClassWithDummyMethod.class.getMethod("annotatedDummyMethod");
        FrameworkMethod frameworkMethod = new FrameworkMethod(method);
        Annotation annotation = frameworkMethod.getAnnotation(Rule.class);
        Assert.assertTrue(Rule.class.isAssignableFrom(annotation.getClass()));
    }

    @Test
    public void missingAnnotationIsNotAvailable() throws Exception {
        Method method = FrameworkMethodTest.ClassWithDummyMethod.class.getMethod("annotatedDummyMethod");
        FrameworkMethod frameworkMethod = new FrameworkMethod(method);
        Annotation annotation = frameworkMethod.getAnnotation(ClassRule.class);
        Assert.assertThat(annotation, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    private static class ClassWithDummyMethod {
        @SuppressWarnings("unused")
        public void dummyMethod() {
        }

        @Rule
        public void annotatedDummyMethod() {
        }
    }
}

