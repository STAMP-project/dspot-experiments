package org.baeldung.httpclient.rare;


import java.io.IOException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class HttpClientUnshortenLiveTest {
    private CloseableHttpClient client;

    // tests
    @Test
    public final void givenShortenedOnce_whenUrlIsUnshortened_thenCorrectResult() throws IOException {
        final String expectedResult = "http://www.baeldung.com/rest-versioning";
        final String actualResult = expandSingleLevel("http://bit.ly/13jEoS1");
        Assert.assertThat(actualResult, Matchers.equalTo(expectedResult));
    }

    @Test
    public final void givenShortenedMultiple_whenUrlIsUnshortened_thenCorrectResult() throws IOException {
        final String expectedResult = "http://www.baeldung.com/rest-versioning";
        final String actualResult = expand("http://t.co/e4rDDbnzmk");
        Assert.assertThat(actualResult, Matchers.equalTo(expectedResult));
    }
}

