package io.github.classgraph.issues.issue78;


import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.junit.Test;


/**
 * The Class Issue78Test.
 */
public class Issue78Test {
    /**
     * Issue 78.
     */
    @Test
    public void issue78() {
        try (ScanResult scanResult = new ClassGraph().whitelistClasses(Issue78Test.class.getName()).scan()) {
            assertThat(scanResult.getAllClasses().getNames()).containsExactlyInAnyOrder(Issue78Test.class.getName());
        }
    }
}

