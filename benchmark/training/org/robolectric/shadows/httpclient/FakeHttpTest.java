package org.robolectric.shadows.httpclient;


import java.io.IOException;
import org.apache.http.HttpException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.util.TestRunnerWithManifest;


@RunWith(TestRunnerWithManifest.class)
public class FakeHttpTest {
    @Test
    public void httpRequestWasSent_ReturnsTrueIfRequestWasSent() throws IOException, HttpException {
        makeRequest("http://example.com");
        Assert.assertTrue(FakeHttp.httpRequestWasMade());
    }

    @Test
    public void httpRequestWasMade_ReturnsFalseIfNoRequestWasMade() {
        Assert.assertFalse(FakeHttp.httpRequestWasMade());
    }

    @Test
    public void httpRequestWasMade_returnsTrueIfRequestMatchingGivenRuleWasMade() throws IOException, HttpException {
        makeRequest("http://example.com");
        Assert.assertTrue(FakeHttp.httpRequestWasMade("http://example.com"));
    }

    @Test
    public void httpRequestWasMade_returnsFalseIfNoRequestMatchingGivenRuleWasMAde() throws IOException, HttpException {
        makeRequest("http://example.com");
        Assert.assertFalse(FakeHttp.httpRequestWasMade("http://example.org"));
    }
}

