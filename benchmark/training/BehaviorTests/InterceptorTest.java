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


import java.io.IOException;
import java.util.concurrent.ExecutionException;
import kong.unirest.Unirest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;


public class InterceptorTest extends BddTest {
    @Test
    public void canAddInterceptor() {
        Unirest.config().addInterceptor(new InterceptorTest.TestInterceptor());
        Unirest.get(MockServer.GET).asObject(RequestCapture.class).getBody().assertHeader("x-custom", "foo");
    }

    @Test
    public void canAddInterceptorToAsync() throws InterruptedException, ExecutionException {
        Unirest.config().addInterceptor(new InterceptorTest.TestInterceptor());
        Unirest.get(MockServer.GET).asObjectAsync(RequestCapture.class).get().getBody().assertHeader("x-custom", "foo");
    }

    private class TestInterceptor implements HttpRequestInterceptor {
        @Override
        public void process(HttpRequest httpRequest, HttpContext httpContext) throws IOException, HttpException {
            httpRequest.addHeader("x-custom", "foo");
        }
    }
}

