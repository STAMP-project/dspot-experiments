package net.bytebuddy.implementation;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.implementation.LoadedTypeInitializer.NoOp.INSTANCE;


public class LoadedTypeInitializerNoOpTest {
    @Test
    public void testIsNotAlive() throws Exception {
        MatcherAssert.assertThat(INSTANCE.isAlive(), CoreMatchers.is(false));
    }
}

