package net.bytebuddy.implementation.bind.annotation;


import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public abstract class AbstractAnnotationTest<T extends Annotation> {
    protected final Class<T> annotationType;

    protected AbstractAnnotationTest(Class<T> annotationType) {
        this.annotationType = annotationType;
    }

    @Test
    public void testAnnotationVisibility() throws Exception {
        MatcherAssert.assertThat(annotationType.isAnnotationPresent(Retention.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(annotationType.getAnnotation(Retention.class).value(), CoreMatchers.is(RetentionPolicy.RUNTIME));
    }
}

