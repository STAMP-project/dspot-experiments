/**
 * The MIT License
 *
 * Copyright for portions of unirest-java are held by Kong Inc (c) 2013.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package BehaviorTests;


import java.util.concurrent.CompletableFuture;
import kong.unirest.HttpResponse;
import kong.unirest.TestUtil;
import kong.unirest.Unirest;
import org.junit.Assert;
import org.junit.Test;


public class AsStringTest extends BddTest {
    @Test
    public void whenNoBodyIsReturned() {
        HttpResponse<String> i = Unirest.get(MockServer.NOBODY).asString();
        Assert.assertEquals(200, i.getStatus());
        Assert.assertEquals("", i.getBody());
    }

    @Test
    public void canParseGzippedStringResponse() {
        HttpResponse<String> i = Unirest.get(MockServer.GZIP).queryString("foo", "bar").asString();
        RequestCapture cap = TestUtil.readValue(i.getBody(), RequestCapture.class);
        Assert.assertEquals(200, i.getStatus());
        cap.assertParam("foo", "bar");
    }

    @Test
    public void canParseGzippedResponseAsync() throws Exception {
        HttpResponse<String> i = Unirest.get(MockServer.GZIP).queryString("foo", "bar").asStringAsync().get();
        RequestCapture cap = TestUtil.readValue(i.getBody(), RequestCapture.class);
        Assert.assertEquals(200, i.getStatus());
        cap.assertParam("foo", "bar");
    }

    @Test
    public void canGetBinaryResponse() {
        HttpResponse<String> i = Unirest.get(MockServer.GET).queryString("foo", "bar").asString();
        RequestCapture cap = TestUtil.readValue(i.getBody(), RequestCapture.class);
        cap.assertParam("foo", "bar");
    }

    @Test
    public void canGetBinaryResponseAsync() throws Exception {
        CompletableFuture<HttpResponse<String>> r = Unirest.get(MockServer.GET).queryString("foo", "bar").asStringAsync();
        RequestCapture cap = TestUtil.readValue(r.get().getBody(), RequestCapture.class);
        cap.assertParam("foo", "bar");
    }

    @Test
    public void canGetBinaryResponseAsyncWithCallback() {
        Unirest.get(MockServer.GET).queryString("foo", "bar").asStringAsync(( r) -> {
            RequestCapture cap = TestUtil.readValue(r.getBody(), .class);
            cap.assertParam("foo", "bar");
            asyncSuccess();
        });
        assertAsync();
    }

    @Test
    public void unicodeResponse() {
        MockServer.setStringResponse("?????????");
        Assert.assertEquals("?????????", Unirest.get(MockServer.GET).asString().getBody());
    }

    @Test
    public void unicodeResponseAsync() throws Exception {
        MockServer.setStringResponse("?????????");
        Unirest.get(MockServer.GET).asStringAsync(( r) -> {
            assertEquals("?????????", r.getBody());
            asyncSuccess();
        });
        assertAsync();
    }

    @Test
    public void canSetExpectedCharsetOfResponse() {
        Assert.assertEquals("??????", Unirest.get(MockServer.WINDOWS_LATIN_1_FILE).responseEncoding("windows-1250").asString().getBody());
    }

    @Test
    public void canSetDefaultCharsetOfResponse() {
        Unirest.config().setDefaultResponseEncoding("windows-1250");
        Assert.assertEquals("??????", Unirest.get(MockServer.WINDOWS_LATIN_1_FILE).asString().getBody());
    }
}

