package net.bytebuddy.asm;


import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class AdviceAnnotationTest {
    private final Class<? extends Annotation> type;

    private final ElementType elementType;

    public AdviceAnnotationTest(Class<? extends Annotation> type, ElementType elementType) {
        this.type = type;
        this.elementType = elementType;
    }

    @Test
    public void testDocumented() throws Exception {
        MatcherAssert.assertThat(type.isAnnotationPresent(Documented.class), CoreMatchers.is(true));
    }

    @Test
    public void testVisible() throws Exception {
        MatcherAssert.assertThat(type.getAnnotation(Retention.class).value(), CoreMatchers.is(RetentionPolicy.RUNTIME));
    }

    @Test
    public void testTarget() throws Exception {
        MatcherAssert.assertThat(type.getAnnotation(Target.class).value().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(type.getAnnotation(Target.class).value()[0], CoreMatchers.is(elementType));
    }
}

