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


import java.net.HttpURLConnection;
import java.net.ProtocolException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class HttpUrlConnectionConfigurerTest {
    @Mock
    private HttpURLConnection httpURLConnection;

    @Captor
    private ArgumentCaptor<String> userAgentCaptor;

    @Test
    public void userAgentIsSet() throws ProtocolException {
        HttpUrlConnectionConfigurer configurer = HttpUrlConnectionConfigurer.builder("POST").build();
        configurer.configure(httpURLConnection);
        Mockito.verify(httpURLConnection).setRequestProperty(ArgumentMatchers.eq("User-Agent"), userAgentCaptor.capture());
        assertThat(userAgentCaptor.getValue()).startsWith("jmxtrans").matches("^.*\\(.*;.*\\)$");
    }

    @Test
    public void contentTypeIsSetWhenConfigured() throws ProtocolException {
        HttpUrlConnectionConfigurer configurer = HttpUrlConnectionConfigurer.builder("POST").setContentType("plain/text").build();
        configurer.configure(httpURLConnection);
        Mockito.verify(httpURLConnection).setRequestProperty(ArgumentMatchers.eq("Content-Type"), ArgumentMatchers.eq("plain/text"));
    }

    @Test
    public void contentTypeIsNotSetWhenUnConfigured() throws ProtocolException {
        HttpUrlConnectionConfigurer configurer = HttpUrlConnectionConfigurer.builder("POST").build();
        configurer.configure(httpURLConnection);
        Mockito.verify(httpURLConnection, Mockito.never()).setRequestProperty(ArgumentMatchers.eq("Content-Type"), ArgumentMatchers.anyString());
    }

    @Test
    public void authorizationIsSetWhenConfigured() throws ProtocolException {
        HttpUrlConnectionConfigurer configurer = HttpUrlConnectionConfigurer.builder("POST").setAuthorization("abcd").build();
        configurer.configure(httpURLConnection);
        Mockito.verify(httpURLConnection).setRequestProperty(ArgumentMatchers.eq("Authorization"), ArgumentMatchers.eq("abcd"));
    }

    @Test
    public void authorizationIsNotSetWhenUnConfigured() throws ProtocolException {
        HttpUrlConnectionConfigurer configurer = HttpUrlConnectionConfigurer.builder("POST").build();
        configurer.configure(httpURLConnection);
        Mockito.verify(httpURLConnection, Mockito.never()).setRequestProperty(ArgumentMatchers.eq("Authorization"), ArgumentMatchers.anyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionIsThrownOnInvalidHttpMethod() {
        HttpUrlConnectionConfigurer.builder("InvalidMethod").build();
    }
}

