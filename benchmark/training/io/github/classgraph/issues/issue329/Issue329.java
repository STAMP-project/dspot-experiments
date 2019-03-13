package io.github.classgraph.issues.issue329;


import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.junit.Test;


/**
 * Unit test.
 */
public class Issue329 {
    /**
     * The Class Foo.
     */
    public class Foo {
        /**
         * Constructor.
         */
        public Foo() {
            new Issue329.Bar();
        }
    }

    /**
     * The Class Bar.
     */
    public class Bar {}

    /**
     * Test.
     */
    @Test
    public void test() {
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().enableInterClassDependencies().enableExternalClasses().whitelistClasses(Issue329.Foo.class.getName()).scan()) {
            final ClassInfo classInfo = scanResult.getClassInfo(Issue329.Foo.class.getName());
            assertThat(classInfo.getClassDependencies().getNames()).containsExactlyInAnyOrder(Issue329.class.getName(), Issue329.Bar.class.getName());
        }
    }
}

