/**
 * The MIT License
 * Copyright ? 2010 JmxTrans team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.googlecode.jmxtrans.model.output.support;


import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.googlecode.jmxtrans.model.QueryFixtures;
import com.googlecode.jmxtrans.model.ResultFixtures;
import com.googlecode.jmxtrans.model.ServerFixtures;
import com.googlecode.jmxtrans.test.IntegrationTest;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(IntegrationTest.class)
public class HttpOutputWriterIT {
    public static final String ENDPOINT = "/endpoint";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(0);

    @Test
    public void messageIsSentOverHttp() throws Exception {
        stubFor(post(urlEqualTo(HttpOutputWriterIT.ENDPOINT)).willReturn(aResponse().withBody("OK").withStatus(200)));
        simpleOutputWriter().doWrite(ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), ResultFixtures.dummyResults());
        verify(postRequestedFor(urlEqualTo(HttpOutputWriterIT.ENDPOINT)).withRequestBody(equalTo("message")).withHeader("User-Agent", containing("jmxtrans")));
    }

    @Test(expected = IOException.class)
    public void exceptionIsThrownOnServerError() throws Exception {
        stubFor(post(urlEqualTo(HttpOutputWriterIT.ENDPOINT)).willReturn(aResponse().withBody("KO").withStatus(500)));
        try {
            simpleOutputWriter().doWrite(ServerFixtures.dummyServer(), QueryFixtures.dummyQuery(), ResultFixtures.dummyResults());
        } finally {
            verify(postRequestedFor(urlEqualTo(HttpOutputWriterIT.ENDPOINT)).withRequestBody(equalTo("message")).withHeader("User-Agent", containing("jmxtrans")));
        }
    }
}

