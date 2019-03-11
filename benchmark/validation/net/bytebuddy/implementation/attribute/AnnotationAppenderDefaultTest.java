package net.bytebuddy.implementation.attribute;


import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.AnnotationVisitor;

import static net.bytebuddy.description.type.TypeDescription.Generic.AnnotationReader.DISPATCHER;


public class AnnotationAppenderDefaultTest {
    private static final String BAR = "net.bytebuddy.test.Bar";

    private static final String FOOBAR = "foobar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Mock
    private AnnotationAppender.Target target;

    @Mock
    private AnnotationValueFilter valueFilter;

    private AnnotationAppender annotationAppender;

    @Test
    public void testNoArgumentAnnotation() throws Exception {
        Class<?> bar = makeTypeWithAnnotation(new AnnotationAppenderDefaultTest.Foo.Instance());
        MatcherAssert.assertThat(bar.getAnnotations().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(bar.isAnnotationPresent(AnnotationAppenderDefaultTest.Foo.class), CoreMatchers.is(true));
    }

    @Test
    public void testNoArgumentAnnotationSourceCodeRetention() throws Exception {
        Class<?> bar = makeTypeWithAnnotation(new AnnotationAppenderDefaultTest.FooSourceCodeRetention.Instance());
        MatcherAssert.assertThat(bar.getAnnotations().length, CoreMatchers.is(0));
    }

    @Test
    public void testNoArgumentAnnotationByteCodeRetention() throws Exception {
        Class<?> bar = makeTypeWithAnnotation(new AnnotationAppenderDefaultTest.FooByteCodeRetention.Instance());
        MatcherAssert.assertThat(bar.getAnnotations().length, CoreMatchers.is(0));
    }

    @Test
    public void testNoArgumentAnnotationNoRetention() throws Exception {
        Class<?> bar = makeTypeWithAnnotation(new AnnotationAppenderDefaultTest.FooNoRetention.Instance());
        MatcherAssert.assertThat(bar.getAnnotations().length, CoreMatchers.is(0));
    }

    @Test
    public void testSingleArgumentAnnotation() throws Exception {
        Class<?> bar = makeTypeWithAnnotation(new AnnotationAppenderDefaultTest.Qux.Instance(AnnotationAppenderDefaultTest.FOOBAR));
        MatcherAssert.assertThat(bar.getAnnotations().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(bar.isAnnotationPresent(AnnotationAppenderDefaultTest.Qux.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(bar.getAnnotation(AnnotationAppenderDefaultTest.Qux.class).value(), CoreMatchers.is(AnnotationAppenderDefaultTest.FOOBAR));
    }

    @Test
    public void testMultipleArgumentAnnotation() throws Exception {
        int[] array = new int[]{ 2, 3, 4 };
        Class<?> bar = makeTypeWithAnnotation(new AnnotationAppenderDefaultTest.Baz.Instance(AnnotationAppenderDefaultTest.FOOBAR, array, new AnnotationAppenderDefaultTest.Foo.Instance(), AnnotationAppenderDefaultTest.Baz.Enum.VALUE, Void.class));
        MatcherAssert.assertThat(bar.getAnnotations().length, CoreMatchers.is(1));
        MatcherAssert.assertThat(bar.isAnnotationPresent(AnnotationAppenderDefaultTest.Baz.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(bar.getAnnotation(AnnotationAppenderDefaultTest.Baz.class).value(), CoreMatchers.is(AnnotationAppenderDefaultTest.FOOBAR));
        MatcherAssert.assertThat(bar.getAnnotation(AnnotationAppenderDefaultTest.Baz.class).array(), CoreMatchers.is(array));
        MatcherAssert.assertThat(bar.getAnnotation(AnnotationAppenderDefaultTest.Baz.class).annotation(), CoreMatchers.is(((AnnotationAppenderDefaultTest.Foo) (new AnnotationAppenderDefaultTest.Foo.Instance()))));
        MatcherAssert.assertThat(bar.getAnnotation(AnnotationAppenderDefaultTest.Baz.class).enumeration(), CoreMatchers.is(AnnotationAppenderDefaultTest.Baz.Enum.VALUE));
        MatcherAssert.assertThat(bar.getAnnotation(AnnotationAppenderDefaultTest.Baz.class).type(), CoreMatchers.<Class<?>>is(Void.class));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testNoArgumentTypeAnnotation() throws Exception {
        Class<?> bar = makeTypeWithSuperClassAnnotation(new AnnotationAppenderDefaultTest.Foo.Instance());
        TypeDescription.Generic.AnnotationReader annotationReader = DISPATCHER.resolveSuperClassType(bar);
        MatcherAssert.assertThat(annotationReader.asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(annotationReader.asList().isAnnotationPresent(AnnotationAppenderDefaultTest.Foo.class), CoreMatchers.is(true));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testNoArgumentTypeAnnotationSourceCodeRetention() throws Exception {
        Class<?> bar = makeTypeWithSuperClassAnnotation(new AnnotationAppenderDefaultTest.FooSourceCodeRetention.Instance());
        TypeDescription.Generic.AnnotationReader annotationReader = DISPATCHER.resolveSuperClassType(bar);
        MatcherAssert.assertThat(annotationReader.asList().size(), CoreMatchers.is(0));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testNoArgumentTypeAnnotationByteCodeRetention() throws Exception {
        Class<?> bar = makeTypeWithSuperClassAnnotation(new AnnotationAppenderDefaultTest.FooByteCodeRetention.Instance());
        TypeDescription.Generic.AnnotationReader annotationReader = DISPATCHER.resolveSuperClassType(bar);
        MatcherAssert.assertThat(annotationReader.asList().size(), CoreMatchers.is(0));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testNoArgumentTypeAnnotationNoRetention() throws Exception {
        Class<?> bar = makeTypeWithSuperClassAnnotation(new AnnotationAppenderDefaultTest.FooNoRetention.Instance());
        TypeDescription.Generic.AnnotationReader annotationReader = DISPATCHER.resolveSuperClassType(bar);
        MatcherAssert.assertThat(annotationReader.asList().size(), CoreMatchers.is(0));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testSingleTypeArgumentAnnotation() throws Exception {
        Class<?> bar = makeTypeWithSuperClassAnnotation(new AnnotationAppenderDefaultTest.Qux.Instance(AnnotationAppenderDefaultTest.FOOBAR));
        TypeDescription.Generic.AnnotationReader annotationReader = DISPATCHER.resolveSuperClassType(bar);
        MatcherAssert.assertThat(annotationReader.asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(annotationReader.asList().isAnnotationPresent(AnnotationAppenderDefaultTest.Qux.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(annotationReader.asList().ofType(AnnotationAppenderDefaultTest.Qux.class).load().value(), CoreMatchers.is(AnnotationAppenderDefaultTest.FOOBAR));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testMultipleTypeArgumentAnnotation() throws Exception {
        int[] array = new int[]{ 2, 3, 4 };
        Class<?> bar = makeTypeWithSuperClassAnnotation(new AnnotationAppenderDefaultTest.Baz.Instance(AnnotationAppenderDefaultTest.FOOBAR, array, new AnnotationAppenderDefaultTest.Foo.Instance(), AnnotationAppenderDefaultTest.Baz.Enum.VALUE, Void.class));
        TypeDescription.Generic.AnnotationReader annotationReader = DISPATCHER.resolveSuperClassType(bar);
        MatcherAssert.assertThat(annotationReader.asList().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(annotationReader.asList().isAnnotationPresent(AnnotationAppenderDefaultTest.Baz.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(annotationReader.asList().ofType(AnnotationAppenderDefaultTest.Baz.class).load().value(), CoreMatchers.is(AnnotationAppenderDefaultTest.FOOBAR));
        MatcherAssert.assertThat(annotationReader.asList().ofType(AnnotationAppenderDefaultTest.Baz.class).load().array(), CoreMatchers.is(array));
        MatcherAssert.assertThat(annotationReader.asList().ofType(AnnotationAppenderDefaultTest.Baz.class).load().annotation(), CoreMatchers.is(((AnnotationAppenderDefaultTest.Foo) (new AnnotationAppenderDefaultTest.Foo.Instance()))));
        MatcherAssert.assertThat(annotationReader.asList().ofType(AnnotationAppenderDefaultTest.Baz.class).load().enumeration(), CoreMatchers.is(AnnotationAppenderDefaultTest.Baz.Enum.VALUE));
        MatcherAssert.assertThat(annotationReader.asList().ofType(AnnotationAppenderDefaultTest.Baz.class).load().type(), CoreMatchers.<Class<?>>is(Void.class));
    }

    @Test
    public void testSourceRetentionAnnotation() throws Exception {
        AnnotationVisitor annotationVisitor = Mockito.mock(AnnotationVisitor.class);
        Mockito.when(target.visit(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(annotationVisitor);
        AnnotationDescription annotationDescription = Mockito.mock(AnnotationDescription.class);
        Mockito.when(annotationDescription.getRetention()).thenReturn(RetentionPolicy.SOURCE);
        annotationAppender.append(annotationDescription, valueFilter);
        Mockito.verifyZeroInteractions(valueFilter);
        Mockito.verifyZeroInteractions(annotationVisitor);
    }

    @Test
    public void testSourceRetentionTypeAnnotation() throws Exception {
        AnnotationVisitor annotationVisitor = Mockito.mock(AnnotationVisitor.class);
        Mockito.when(target.visit(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(annotationVisitor);
        AnnotationDescription annotationDescription = Mockito.mock(AnnotationDescription.class);
        Mockito.when(annotationDescription.getRetention()).thenReturn(RetentionPolicy.SOURCE);
        annotationAppender.append(annotationDescription, valueFilter, 0, null);
        Mockito.verifyZeroInteractions(valueFilter);
        Mockito.verifyZeroInteractions(annotationVisitor);
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Foo {
        @SuppressWarnings("all")
        class Instance implements AnnotationAppenderDefaultTest.Foo {
            public Class<? extends Annotation> annotationType() {
                return AnnotationAppenderDefaultTest.Foo.class;
            }
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface FooSourceCodeRetention {
        @SuppressWarnings("all")
        class Instance implements AnnotationAppenderDefaultTest.FooSourceCodeRetention {
            public Class<? extends Annotation> annotationType() {
                return AnnotationAppenderDefaultTest.FooSourceCodeRetention.class;
            }
        }
    }

    @Retention(RetentionPolicy.CLASS)
    public @interface FooByteCodeRetention {
        @SuppressWarnings("all")
        class Instance implements AnnotationAppenderDefaultTest.FooByteCodeRetention {
            public Class<? extends Annotation> annotationType() {
                return AnnotationAppenderDefaultTest.FooByteCodeRetention.class;
            }
        }
    }

    public @interface FooNoRetention {
        @SuppressWarnings("all")
        class Instance implements AnnotationAppenderDefaultTest.FooNoRetention {
            public Class<? extends Annotation> annotationType() {
                return AnnotationAppenderDefaultTest.FooNoRetention.class;
            }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Qux {
        String value();

        @SuppressWarnings("all")
        class Instance implements AnnotationAppenderDefaultTest.Qux {
            private final String value;

            public Instance(String value) {
                this.value = value;
            }

            public String value() {
                return value;
            }

            public Class<? extends Annotation> annotationType() {
                return AnnotationAppenderDefaultTest.Qux.class;
            }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Baz {
        String value();

        int[] array();

        AnnotationAppenderDefaultTest.Foo annotation();

        AnnotationAppenderDefaultTest.Baz.Enum enumeration();

        Class<?> type();

        enum Enum {

            VALUE;}

        @SuppressWarnings("all")
        class Instance implements AnnotationAppenderDefaultTest.Baz {
            private final String value;

            private final int[] array;

            private final AnnotationAppenderDefaultTest.Foo annotation;

            private final AnnotationAppenderDefaultTest.Baz.Enum enumeration;

            private final Class<?> type;

            public Instance(String value, int[] array, AnnotationAppenderDefaultTest.Foo annotation, AnnotationAppenderDefaultTest.Baz.Enum enumeration, Class<?> type) {
                this.value = value;
                this.array = array;
                this.annotation = annotation;
                this.enumeration = enumeration;
                this.type = type;
            }

            public String value() {
                return value;
            }

            public int[] array() {
                return array;
            }

            public AnnotationAppenderDefaultTest.Foo annotation() {
                return annotation;
            }

            public AnnotationAppenderDefaultTest.Baz.Enum enumeration() {
                return enumeration;
            }

            public Class<?> type() {
                return type;
            }

            public Class<? extends Annotation> annotationType() {
                return AnnotationAppenderDefaultTest.Baz.class;
            }
        }
    }
}

