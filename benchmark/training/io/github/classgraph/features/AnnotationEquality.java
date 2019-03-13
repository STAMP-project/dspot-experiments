package io.github.classgraph.features;


import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.Test;


/**
 * The Class AnnotationEquality.
 */
public class AnnotationEquality {
    /**
     * The Interface W.
     */
    private static interface W {}

    /**
     * The Interface X.
     */
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface X {
        /**
         * A.
         *
         * @return the int
         */
        int a() default 3;

        /**
         * B.
         *
         * @return the int
         */
        int b();

        /**
         * C.
         *
         * @return the class[]
         */
        Class<?>[] c();
    }

    /**
     * The Class Y.
     */
    @AnnotationEquality.X(b = 5, c = { Long.class, Integer.class, AnnotationEquality.class, AnnotationEquality.W.class, AnnotationEquality.X.class })
    private static class Y {}

    /**
     * Test equality of JRE-instantiated Annotation with proxy instance instantiated by ClassGraph.
     */
    @Test
    public void annotationEquality() {
        try (ScanResult scanResult = new ClassGraph().whitelistPackages(AnnotationEquality.class.getPackage().getName()).enableAllInfo().scan()) {
            final ClassInfo classInfo = scanResult.getClassInfo(AnnotationEquality.Y.class.getName());
            assertThat(classInfo).isNotNull();
            final Class<?> cls = classInfo.loadClass();
            final Annotation annotation = cls.getAnnotations()[0];
            assertThat(AnnotationEquality.X.class.isInstance(annotation));
            final AnnotationInfo annotationInfo = classInfo.getAnnotationInfo().get(0);
            final Annotation proxyAnnotation = annotationInfo.loadClassAndInstantiate();
            assertThat(AnnotationEquality.X.class.isInstance(proxyAnnotation));
            assertThat(annotation.hashCode()).isEqualTo(proxyAnnotation.hashCode());
            assertThat(annotation).isEqualTo(proxyAnnotation);
            assertThat(annotation.toString()).isEqualTo(annotationInfo.toString());
            assertThat(annotation.toString()).isEqualTo(proxyAnnotation.toString());
        }
    }
}

