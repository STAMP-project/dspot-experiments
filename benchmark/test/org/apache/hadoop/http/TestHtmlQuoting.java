/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.http;


import HttpServer2.QuotingInputFilter.RequestQuoter;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestHtmlQuoting {
    @Test
    public void testNeedsQuoting() throws Exception {
        Assert.assertTrue(HtmlQuoting.needsQuoting("abcde>"));
        Assert.assertTrue(HtmlQuoting.needsQuoting("<abcde"));
        Assert.assertTrue(HtmlQuoting.needsQuoting("abc'de"));
        Assert.assertTrue(HtmlQuoting.needsQuoting("abcde\""));
        Assert.assertTrue(HtmlQuoting.needsQuoting("&"));
        Assert.assertFalse(HtmlQuoting.needsQuoting(""));
        Assert.assertFalse(HtmlQuoting.needsQuoting("ab\ncdef"));
        Assert.assertFalse(HtmlQuoting.needsQuoting(null));
    }

    @Test
    public void testQuoting() throws Exception {
        Assert.assertEquals("ab&lt;cd", HtmlQuoting.quoteHtmlChars("ab<cd"));
        Assert.assertEquals("ab&gt;", HtmlQuoting.quoteHtmlChars("ab>"));
        Assert.assertEquals("&amp;&amp;&amp;", HtmlQuoting.quoteHtmlChars("&&&"));
        Assert.assertEquals(" &apos;\n", HtmlQuoting.quoteHtmlChars(" \'\n"));
        Assert.assertEquals("&quot;", HtmlQuoting.quoteHtmlChars("\""));
        Assert.assertEquals(null, HtmlQuoting.quoteHtmlChars(null));
    }

    @Test
    public void testRoundtrip() throws Exception {
        runRoundTrip("");
        runRoundTrip("<>&\'\"");
        runRoundTrip("ab>cd<ef&ghi\'\"");
        runRoundTrip("A string\n with no quotable chars in it!");
        runRoundTrip(null);
        StringBuilder buffer = new StringBuilder();
        for (char ch = 0; ch < 127; ++ch) {
            buffer.append(ch);
        }
        runRoundTrip(buffer.toString());
    }

    @Test
    public void testRequestQuoting() throws Exception {
        HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
        HttpServer2.QuotingInputFilter.RequestQuoter quoter = new HttpServer2.QuotingInputFilter.RequestQuoter(mockReq);
        Mockito.doReturn("a<b").when(mockReq).getParameter("x");
        Assert.assertEquals("Test simple param quoting", "a&lt;b", quoter.getParameter("x"));
        Mockito.doReturn(null).when(mockReq).getParameter("x");
        Assert.assertEquals("Test that missing parameters dont cause NPE", null, quoter.getParameter("x"));
        Mockito.doReturn(new String[]{ "a<b", "b" }).when(mockReq).getParameterValues("x");
        Assert.assertArrayEquals("Test escaping of an array", new String[]{ "a&lt;b", "b" }, quoter.getParameterValues("x"));
        Mockito.doReturn(null).when(mockReq).getParameterValues("x");
        Assert.assertArrayEquals("Test that missing parameters dont cause NPE for array", null, quoter.getParameterValues("x"));
    }
}

