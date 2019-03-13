package net.bytebuddy.dynamic.loading;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;


public class NoOpClassFileTransformerTest {
    @Test
    public void testNoTransformation() throws Exception {
        MatcherAssert.assertThat(NoOpClassFileTransformer.INSTANCE.transform(Mockito.mock(ClassLoader.class), "foo", null, null, new byte[0]), CoreMatchers.nullValue(byte[].class));
    }
}

