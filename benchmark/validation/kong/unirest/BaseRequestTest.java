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
package kong.unirest;


import HttpMethod.GET;
import org.junit.Assert;
import org.junit.Test;


public class BaseRequestTest {
    @Test
    public void socketTimeoutCanOverrrideConfig() {
        Config config = new Config();
        config.socketTimeout(42);
        HttpRequest request = new BaseRequestTest.TestRequest(config);
        Assert.assertEquals(42, request.getSocketTimeout());
        request.socketTimeout(111);
        Assert.assertEquals(111, request.getSocketTimeout());
    }

    @Test
    public void connectTimeoutCanOverrrideConfig() {
        Config config = new Config();
        config.connectTimeout(42);
        HttpRequest request = new BaseRequestTest.TestRequest(config);
        Assert.assertEquals(42, request.getConnectTimeout());
        request.connectTimeout(111);
        Assert.assertEquals(111, request.getConnectTimeout());
    }

    @Test
    public void copiesSettingsFromOtherRequest() {
        Config config = new Config();
        config.connectTimeout(42);
        config.socketTimeout(42);
        BaseRequestTest.TestRequest request = new BaseRequestTest.TestRequest(config);
        socketTimeout(111).connectTimeout(222);
        HttpRequest copy = new BaseRequestTest.TestRequest(request);
        Assert.assertEquals(111, copy.getSocketTimeout());
        Assert.assertEquals(222, copy.getConnectTimeout());
    }

    @Test
    public void canPassABasicProxyPerRequest() {
        Config config = new Config();
        Proxy cp = new Proxy("foo", 8080, "username", "password");
        config.proxy(cp);
        HttpRequest request = new BaseRequestTest.TestRequest(config);
        Assert.assertEquals(cp, request.getProxy());
        request.proxy("bar", 7979);
        Assert.assertEquals("bar", request.getProxy().getHost());
        Assert.assertEquals(7979, request.getProxy().getPort().intValue());
    }

    private class TestRequest extends BaseRequest<BaseRequestTest.TestRequest> {
        TestRequest(BaseRequest httpRequest) {
            super(httpRequest);
        }

        TestRequest(Config config) {
            super(config, GET, "");
        }
    }
}

