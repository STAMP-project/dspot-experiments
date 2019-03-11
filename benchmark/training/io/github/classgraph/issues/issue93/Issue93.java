package io.github.classgraph.issues.issue93;


import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.Test;


/**
 * The Class Issue93.
 */
public class Issue93 {
    /**
     * The Constant PKG.
     */
    private static final String PKG = Issue93.class.getPackage().getName();

    /**
     * The Interface RetentionClass.
     */
    @Retention(RetentionPolicy.CLASS)
    private @interface RetentionClass {}

    /**
     * The Interface RetentionRuntime.
     */
    @Retention(RetentionPolicy.RUNTIME)
    private @interface RetentionRuntime {}

    /**
     * The Class RetentionClassAnnotated.
     */
    @Issue93.RetentionClass
    static class RetentionClassAnnotated {}

    /**
     * The Class RetentionRuntimeAnnotated.
     */
    @Issue93.RetentionRuntime
    static class RetentionRuntimeAnnotated {}

    /**
     * Test that both CLASS-retained and RUNTIME-retained annotations are visible by default.
     */
    @Test
    public void classRetentionIsDefault() {
        try (ScanResult scanResult = new ClassGraph().whitelistPackages(Issue93.PKG).enableAnnotationInfo().ignoreClassVisibility().scan()) {
            assertThat(scanResult.getClassesWithAnnotation(Issue93.RetentionClass.class.getName()).getNames()).containsExactlyInAnyOrder(Issue93.RetentionClassAnnotated.class.getName());
            assertThat(scanResult.getClassesWithAnnotation(Issue93.RetentionRuntime.class.getName()).getNames()).containsExactlyInAnyOrder(Issue93.RetentionRuntimeAnnotated.class.getName());
        }
    }

    /**
     * Test that CLASS-retained annotations are not visible after calling
     * .setAnnotationVisibility(RetentionPolicy.RUNTIME), but RUNTIME-retained annotations are still visible.
     */
    @Test
    public void classRetentionIsNotVisibleWithRetentionPolicyRUNTIME() {
        try (ScanResult scanResult = new ClassGraph().whitelistPackages(Issue93.PKG).enableAnnotationInfo().ignoreClassVisibility().disableRuntimeInvisibleAnnotations().scan()) {
            assertThat(scanResult.getClassesWithAnnotation(Issue93.RetentionClass.class.getName()).getNames()).isEmpty();
            assertThat(scanResult.getClassesWithAnnotation(Issue93.RetentionRuntime.class.getName()).getNames()).containsExactlyInAnyOrder(Issue93.RetentionRuntimeAnnotated.class.getName());
        }
    }
}

