package net.bytebuddy.pool;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.objectweb.asm.ClassReader;

import static net.bytebuddy.pool.TypePool.Default.ReaderMode.EXTENDED;
import static net.bytebuddy.pool.TypePool.Default.ReaderMode.FAST;


public class TypePoolDefaultReaderModeTest {
    @Test
    public void testDefinition() throws Exception {
        MatcherAssert.assertThat(EXTENDED.isExtended(), CoreMatchers.is(true));
        MatcherAssert.assertThat(FAST.isExtended(), CoreMatchers.is(false));
    }

    @Test
    public void testFlags() throws Exception {
        MatcherAssert.assertThat(EXTENDED.getFlags(), CoreMatchers.is(ClassReader.SKIP_FRAMES));
        MatcherAssert.assertThat(FAST.getFlags(), CoreMatchers.is(ClassReader.SKIP_CODE));
    }
}

