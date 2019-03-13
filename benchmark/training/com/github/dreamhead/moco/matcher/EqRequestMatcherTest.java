package com.github.dreamhead.moco.matcher;


import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.RequestExtractor;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Optional;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class EqRequestMatcherTest {
    private EqRequestMatcher matcher;

    private RequestExtractor<String[]> extractor;

    private HttpRequest request;

    private Resource expected;

    @Test
    public void should_return_false_when_extracted_string_array_have_null() {
        Optional<String[]> extractedResultsWithNull = Optional.of(new String[]{ null, null });
        Mockito.when(extractor.extract(request)).thenReturn(extractedResultsWithNull);
        Assert.assertThat(matcher.match(request), CoreMatchers.is(false));
    }
}

