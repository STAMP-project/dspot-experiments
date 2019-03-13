/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.http4;


import Exchange.CONTENT_ENCODING;
import Exchange.CONTENT_TYPE;
import Exchange.HTTP_RESPONSE_CODE;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.junit.Test;


public class HttpCompressionTest extends BaseHttpTest {
    private HttpServer localServer;

    @Test
    public void compressedHttpPost() throws Exception {
        Exchange exchange = template.request((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/"), new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(CONTENT_TYPE, "text/plain");
                exchange.getIn().setHeader(CONTENT_ENCODING, "gzip");
                exchange.getIn().setBody(getBody());
            }
        });
        assertNotNull(exchange);
        Message out = exchange.getOut();
        assertNotNull(out);
        Map<String, Object> headers = out.getHeaders();
        assertEquals(HttpStatus.SC_OK, headers.get(HTTP_RESPONSE_CODE));
        assertBody(out.getBody(String.class));
    }

    static class RequestDecompressingInterceptor implements HttpRequestInterceptor {
        public void process(HttpRequest request, HttpContext context) throws IOException, HttpException {
            Header contentEncoding = request.getFirstHeader("Content-Encoding");
            if ((contentEncoding != null) && (contentEncoding.getValue().equalsIgnoreCase("gzip"))) {
                HttpEntity entity = ((HttpEntityEnclosingRequest) (request)).getEntity();
                ((HttpEntityEnclosingRequest) (request)).setEntity(new HttpCompressionTest.RequestDecompressingInterceptor.GzipDecompressingEntity(entity));
            }
        }

        static class GzipDecompressingEntity extends HttpEntityWrapper {
            GzipDecompressingEntity(final HttpEntity entity) {
                super(entity);
            }

            @Override
            public InputStream getContent() throws IOException, IllegalStateException {
                InputStream wrappedin = wrappedEntity.getContent();
                return new GZIPInputStream(wrappedin);
            }

            @Override
            public long getContentLength() {
                return -1;
            }

            @Override
            public boolean isStreaming() {
                return false;
            }
        }
    }

    static class ResponseCompressingInterceptor implements HttpResponseInterceptor {
        public void process(HttpResponse response, HttpContext context) throws IOException, HttpException {
            response.setHeader("Content-Encoding", "gzip");
            HttpEntity entity = response.getEntity();
            response.setEntity(new HttpCompressionTest.ResponseCompressingInterceptor.GzipCompressingEntity(entity));
        }

        static class GzipCompressingEntity extends HttpEntityWrapper {
            GzipCompressingEntity(final HttpEntity entity) {
                super(entity);
            }

            @Override
            public Header getContentEncoding() {
                return new BasicHeader("Content-Encoding", "gzip");
            }

            @Override
            public void writeTo(OutputStream outstream) throws IOException {
                GZIPOutputStream gzip = new GZIPOutputStream(outstream);
                gzip.write(EntityUtils.toByteArray(wrappedEntity));
                gzip.close();
            }

            @Override
            public long getContentLength() {
                return -1;
            }

            @Override
            public boolean isStreaming() {
                return false;
            }
        }
    }
}

