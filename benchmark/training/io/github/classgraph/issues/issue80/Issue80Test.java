package io.github.classgraph.issues.issue80;


import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.junit.Test;


/**
 * The Class Issue80Test.
 */
public class Issue80Test {
    /**
     * Issue 80.
     */
    @Test
    public void issue80() {
        try (ScanResult scanResult = new ClassGraph().enableSystemJarsAndModules().enableClassInfo().scan()) {
            assertThat(scanResult.getAllStandardClasses().getNames()).contains("java.util.ArrayList");
        }
    }
}

