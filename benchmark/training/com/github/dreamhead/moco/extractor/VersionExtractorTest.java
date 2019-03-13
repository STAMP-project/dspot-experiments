package com.github.dreamhead.moco.extractor;


import HttpProtocolVersion.VERSION_1_0;
import HttpVersion.HTTP_1_0;
import com.github.dreamhead.moco.HttpRequest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class VersionExtractorTest {
    private VersionExtractor extractor;

    private HttpRequest request;

    @Test
    public void should_extract_version() {
        Mockito.when(request.getVersion()).thenReturn(VERSION_1_0);
        Assert.assertThat(extractor.extract(request).get(), CoreMatchers.is(HTTP_1_0.toString()));
    }
}

