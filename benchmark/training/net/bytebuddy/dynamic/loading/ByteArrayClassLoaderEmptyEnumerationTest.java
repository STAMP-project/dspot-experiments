package net.bytebuddy.dynamic.loading;


import java.util.NoSuchElementException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.dynamic.loading.ByteArrayClassLoader.EmptyEnumeration.INSTANCE;


public class ByteArrayClassLoaderEmptyEnumerationTest {
    @Test
    public void testNoFurtherElements() throws Exception {
        MatcherAssert.assertThat(INSTANCE.hasMoreElements(), CoreMatchers.is(false));
    }

    @Test(expected = NoSuchElementException.class)
    public void testNextElementThrowsException() throws Exception {
        INSTANCE.nextElement();
    }
}

