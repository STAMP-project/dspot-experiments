package io.github.classgraph.issues.issue38;


import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import java.lang.annotation.Annotation;
import org.junit.Test;


/**
 * The Class Issue38Test.
 */
public class Issue38Test {
    /**
     * The Class AnnotationLiteral.
     *
     * @param <T>
     * 		the generic type
     */
    public abstract static class AnnotationLiteral<T extends Annotation> implements Annotation {}

    /**
     * Test implements suppress warnings.
     */
    @Test
    public void testImplementsSuppressWarnings() {
        try (ScanResult scanResult = new ClassGraph().whitelistPackages(Issue38Test.class.getPackage().getName()).scan()) {
            assertThat(scanResult.getClassesImplementing(SuppressWarnings.class.getName()).getNames()).containsExactlyInAnyOrder(ImplementsSuppressWarnings.class.getName());
        }
    }
}

