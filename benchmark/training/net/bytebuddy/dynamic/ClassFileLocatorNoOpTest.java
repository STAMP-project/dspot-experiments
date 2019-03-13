package net.bytebuddy.dynamic;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.ClassFileLocator.NoOp.INSTANCE;


public class ClassFileLocatorNoOpTest {
    private static final String FOO = "foo";

    @Test
    public void testLocation() throws Exception {
        MatcherAssert.assertThat(INSTANCE.locate(ClassFileLocatorNoOpTest.FOO).isResolved(), CoreMatchers.is(false));
    }

    @Test
    public void testClose() throws Exception {
        INSTANCE.close();
    }
}

