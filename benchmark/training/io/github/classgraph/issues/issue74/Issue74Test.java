package io.github.classgraph.issues.issue74;


import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.junit.Test;


/**
 * The Class Issue74Test.
 */
public class Issue74Test {
    /**
     * The Interface Function.
     */
    public interface Function {}

    /**
     * The Class FunctionAdapter.
     */
    public abstract class FunctionAdapter implements Issue74Test.Function {}

    /**
     * The Class ExtendsFunctionAdapter.
     */
    public class ExtendsFunctionAdapter extends Issue74Test.FunctionAdapter {}

    /**
     * The Class ImplementsFunction.
     */
    public class ImplementsFunction implements Issue74Test.Function {}

    /**
     * Issue 74.
     */
    @Test
    public void issue74() {
        try (ScanResult scanResult = new ClassGraph().whitelistPackages(Issue74Test.class.getPackage().getName()).scan()) {
            assertThat(scanResult.getClassesImplementing(Issue74Test.Function.class.getName()).getNames()).containsExactlyInAnyOrder(Issue74Test.FunctionAdapter.class.getName(), Issue74Test.ImplementsFunction.class.getName(), Issue74Test.ExtendsFunctionAdapter.class.getName());
        }
    }
}

