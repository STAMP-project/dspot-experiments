package com.github.dreamhead.moco.matcher;


import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.model.DefaultHttpRequest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class XmlRequestMatcherTest {
    @Test
    public void should_return_false_for_empty_content() {
        XmlRequestMatcher unitUnderTest = new XmlRequestMatcher(Moco.text("<request><parameters><id>1</id></parameters></request>"));
        HttpRequest request = DefaultHttpRequest.builder().withContent("").build();
        Assert.assertThat(unitUnderTest.match(request), CoreMatchers.is(false));
    }
}

