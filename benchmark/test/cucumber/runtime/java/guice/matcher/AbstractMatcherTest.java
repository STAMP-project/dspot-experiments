package cucumber.runtime.java.guice.matcher;


import junit.framework.TestCase;


/**
 * Copied from Hamcrest core test source tree.
 */
public abstract class AbstractMatcherTest extends TestCase {
    public void testIsNullSafe() {
        AbstractMatcherTest.assertNullSafe(createMatcher());
    }

    public void testCopesWithUnknownTypes() {
        AbstractMatcherTest.assertUnknownTypeSafe(createMatcher());
    }

    public static class UnknownType {}
}

