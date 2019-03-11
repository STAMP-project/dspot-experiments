package net.bytebuddy.pool;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static net.bytebuddy.pool.TypePool.Empty.INSTANCE;


public class TypePoolEmptyTest {
    private static final String FOO = "foo";

    @Test
    public void testResolutionUnresolved() throws Exception {
        Assert.assertThat(INSTANCE.describe(TypePoolEmptyTest.FOO).isResolved(), CoreMatchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testResolutionThrowsException() throws Exception {
        INSTANCE.describe(TypePoolEmptyTest.FOO).resolve();
    }

    @Test
    public void testClearNoEffect() throws Exception {
        INSTANCE.clear();
    }
}

