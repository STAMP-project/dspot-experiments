package org.hamcrest;


import junit.framework.TestCase;


public abstract class AbstractMatcherTest extends TestCase {
    public void testIsNullSafe() {
        AbstractMatcherTest.assertNullSafe(createMatcher());
    }

    public void testCopesWithUnknownTypes() {
        createMatcher().matches(new AbstractMatcherTest.UnknownType());
    }

    @SuppressWarnings("WeakerAccess")
    public static class UnknownType {}
}

