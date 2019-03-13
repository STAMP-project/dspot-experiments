package net.bytebuddy.dynamic;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class ClassFileLocatorResolutionTest {
    private static final String FOO = "foo";

    private static final byte[] DATA = new byte[]{ 1, 2, 3 };

    @Test
    public void testIllegal() throws Exception {
        MatcherAssert.assertThat(new ClassFileLocator.Resolution.Illegal(ClassFileLocatorResolutionTest.FOO).isResolved(), CoreMatchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalThrowsException() throws Exception {
        new ClassFileLocator.Resolution.Illegal(ClassFileLocatorResolutionTest.FOO).resolve();
    }

    @Test
    public void testExplicit() throws Exception {
        MatcherAssert.assertThat(new ClassFileLocator.Resolution.Explicit(ClassFileLocatorResolutionTest.DATA).isResolved(), CoreMatchers.is(true));
    }

    @Test
    public void testExplicitGetData() throws Exception {
        MatcherAssert.assertThat(new ClassFileLocator.Resolution.Explicit(ClassFileLocatorResolutionTest.DATA).resolve(), CoreMatchers.is(ClassFileLocatorResolutionTest.DATA));
    }
}

