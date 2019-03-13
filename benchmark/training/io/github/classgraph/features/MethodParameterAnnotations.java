package io.github.classgraph.features;


import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.Test;


/**
 * The Class AnnotationEquality.
 */
public class MethodParameterAnnotations {
    /**
     * The Annotation W.
     */
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface W {}

    /**
     * The Annotation X.
     */
    @Retention(RetentionPolicy.RUNTIME)
    private static @interface X {}

    /**
     * The Class Y.
     */
    private abstract static class Y {
        abstract void w(@MethodParameterAnnotations.W
        int w);
    }

    /**
     * The Class Z.
     */
    private abstract static class Z {
        abstract void x(@MethodParameterAnnotations.X
        int x);
    }

    /**
     * Test equality of JRE-instantiated Annotation with proxy instance instantiated by ClassGraph.
     */
    @Test
    public void annotationEquality() {
        try (ScanResult scanResult = new ClassGraph().whitelistPackages(MethodParameterAnnotations.class.getPackage().getName()).enableAllInfo().scan()) {
            assertThat(scanResult.getClassInfo(MethodParameterAnnotations.Y.class.getName()).getMethodParameterAnnotations().getNames()).containsOnly(MethodParameterAnnotations.W.class.getName());
            assertThat(scanResult.getClassInfo(MethodParameterAnnotations.Z.class.getName()).getMethodParameterAnnotations().getNames()).containsOnly(MethodParameterAnnotations.X.class.getName());
            assertThat(scanResult.getClassesWithMethodParameterAnnotation(MethodParameterAnnotations.W.class.getName()).getNames()).containsOnly(MethodParameterAnnotations.Y.class.getName());
            assertThat(scanResult.getClassesWithMethodParameterAnnotation(MethodParameterAnnotations.X.class.getName()).getNames()).containsOnly(MethodParameterAnnotations.Z.class.getName());
        }
    }
}

