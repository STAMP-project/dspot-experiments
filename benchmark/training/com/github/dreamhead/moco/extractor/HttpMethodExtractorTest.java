package com.github.dreamhead.moco.extractor;


import HttpMethod.GET;
import com.github.dreamhead.moco.HttpRequest;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class HttpMethodExtractorTest {
    private HttpMethodExtractor extractor;

    private HttpRequest request;

    @Test
    public void should_extract_http_method() {
        Mockito.when(request.getMethod()).thenReturn(GET);
        Assert.assertThat(extractor.extract(request).get(), Is.is(GET.name()));
    }
}

