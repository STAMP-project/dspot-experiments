package org.junit.runners.model;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class FrameworkFieldTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void cannotBeCreatedWithoutUnderlyingField() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("FrameworkField cannot be created without an underlying field.");
        new FrameworkField(null);
    }

    @Test
    public void hasToStringWhichPrintsFieldName() throws Exception {
        Field field = FrameworkFieldTest.ClassWithDummyField.class.getField("dummyField");
        FrameworkField frameworkField = new FrameworkField(field);
        Assert.assertTrue(frameworkField.toString().contains("dummyField"));
    }

    @Test
    public void presentAnnotationIsAvailable() throws Exception {
        Field field = FrameworkFieldTest.ClassWithDummyField.class.getField("annotatedField");
        FrameworkField frameworkField = new FrameworkField(field);
        Annotation annotation = frameworkField.getAnnotation(Rule.class);
        Assert.assertTrue(Rule.class.isAssignableFrom(annotation.getClass()));
    }

    @Test
    public void missingAnnotationIsNotAvailable() throws Exception {
        Field field = FrameworkFieldTest.ClassWithDummyField.class.getField("annotatedField");
        FrameworkField frameworkField = new FrameworkField(field);
        Annotation annotation = frameworkField.getAnnotation(ClassRule.class);
        Assert.assertThat(annotation, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    private static class ClassWithDummyField {
        @SuppressWarnings("unused")
        public final int dummyField = 0;

        @Rule
        public final int annotatedField = 0;
    }
}

