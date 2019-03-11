package com.nytimes.android.external.fs3;


import io.reactivex.Single;
import okio.BufferedSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ObjectToSourceTransformerTest {
    @Mock
    BufferedSourceAdapter<String> mockBufferedParser;

    @Mock
    BufferedSource mockBufferedSource;

    @Test
    public void testTransformer() throws Exception {
        BufferedSource source = Single.just("test").compose(new ObjectToSourceTransformer(mockBufferedParser)).blockingGet();
        assertThat(source).isEqualTo(mockBufferedSource);
    }
}

