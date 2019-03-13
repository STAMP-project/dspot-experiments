package com.github.dreamhead.moco.extractor;


import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import com.google.common.base.Optional;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class XPathRequestExtractorTest {
    @Test
    public void should_extract_empty_content_as_absent() {
        XPathRequestExtractor unitUnderTest = new XPathRequestExtractor("/request/parameters/id/text()");
        HttpRequest request = DefaultHttpRequest.builder().withContent("").build();
        Optional<String[]> result = unitUnderTest.extract(request);
        Assert.assertThat(result.isPresent(), CoreMatchers.is(false));
    }
}

