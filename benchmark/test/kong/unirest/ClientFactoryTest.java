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


import java.lang.management.ManagementFactory;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ClientFactoryTest {
    @Test
    public void shouldReuseThreadPool() {
        int startingCount = ManagementFactory.getThreadMXBean().getThreadCount();
        // IntStream.range(0,100).forEach(i -> ClientFactory.refresh());
        Assert.assertThat(ManagementFactory.getThreadMXBean().getThreadCount(), CoreMatchers.is(Matchers.lessThan((startingCount + 10))));
    }

    @Test
    public void canSaveSomeOptions() {
        HttpRequestInterceptor i = Mockito.mock(HttpRequestInterceptor.class);
        CloseableHttpAsyncClient c = Mockito.mock(CloseableHttpAsyncClient.class);
        Unirest.config().addInterceptor(i).connectTimeout(4000).asyncClient(c);
        Unirest.shutDown(false);
        Assert.assertNotEquals(c, Unirest.config().getAsyncClient());
        Assert.assertEquals(i, Unirest.config().getInterceptors().get(0));
        Assert.assertEquals(4000, Unirest.config().getConnectionTimeout());
    }
}

