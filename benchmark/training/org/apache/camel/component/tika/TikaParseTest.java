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
package org.apache.camel.component.tika;


import Exchange.CONTENT_TYPE;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.txt.UniversalEncodingDetector;
import org.junit.Test;


public class TikaParseTest extends CamelTestSupport {
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Test
    public void testDocumentParse() throws Exception {
        File document = new File("src/test/resources/test.doc");
        template.sendBody("direct:start", document);
        resultEndpoint.setExpectedMessageCount(1);
        resultEndpoint.expectedMessagesMatches(new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                Object body = exchange.getIn().getBody(String.class);
                Map<String, Object> headerMap = exchange.getIn().getHeaders();
                assertThat(body, instanceOf(String.class));
                Charset detectedCharset = null;
                try {
                    InputStream bodyIs = new ByteArrayInputStream(((String) (body)).getBytes());
                    UniversalEncodingDetector encodingDetector = new UniversalEncodingDetector();
                    detectedCharset = encodingDetector.detect(bodyIs, new Metadata());
                } catch (IOException e1) {
                    fail();
                }
                assertThat(detectedCharset.name(), startsWith(Charset.defaultCharset().name()));
                assertThat(((String) (body)), containsString("test"));
                assertThat(headerMap.get(CONTENT_TYPE), equalTo("application/msword"));
                return true;
            }
        });
        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testDocumentParseWithEncoding() throws Exception {
        File document = new File("src/test/resources/testOpenOffice2.odt");
        template.sendBody("direct:start4", document);
        resultEndpoint.setExpectedMessageCount(1);
        resultEndpoint.expectedMessagesMatches(new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                Object body = exchange.getIn().getBody(String.class);
                Map<String, Object> headerMap = exchange.getIn().getHeaders();
                assertThat(body, instanceOf(String.class));
                Charset detectedCharset = null;
                try {
                    InputStream bodyIs = new ByteArrayInputStream(((String) (body)).getBytes(StandardCharsets.UTF_16));
                    UniversalEncodingDetector encodingDetector = new UniversalEncodingDetector();
                    detectedCharset = encodingDetector.detect(bodyIs, new Metadata());
                } catch (IOException e1) {
                    fail();
                }
                assertThat(detectedCharset.name(), startsWith(StandardCharsets.UTF_16.name()));
                assertThat(headerMap.get(CONTENT_TYPE), equalTo("application/vnd.oasis.opendocument.text"));
                return true;
            }
        });
        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testImageParse() throws Exception {
        File document = new File("src/test/resources/testGIF.gif");
        template.sendBody("direct:start", document);
        resultEndpoint.setExpectedMessageCount(1);
        resultEndpoint.expectedMessagesMatches(new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                Object body = exchange.getIn().getBody(String.class);
                Map<String, Object> headerMap = exchange.getIn().getHeaders();
                assertThat(body, instanceOf(String.class));
                assertThat(((String) (body)), containsString("<body/>"));
                assertThat(headerMap.get(CONTENT_TYPE), equalTo("image/gif"));
                return true;
            }
        });
        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testEmptyConfigDocumentParse() throws Exception {
        File document = new File("src/test/resources/test.doc");
        template.sendBody("direct:start3", document);
        resultEndpoint.setExpectedMessageCount(1);
        resultEndpoint.expectedMessagesMatches(new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                Object body = exchange.getIn().getBody(String.class);
                Map<String, Object> headerMap = exchange.getIn().getHeaders();
                assertThat(body, instanceOf(String.class));
                assertThat(((String) (body)), containsString("<body/>"));
                assertThat(headerMap.get(CONTENT_TYPE), equalTo("application/msword"));
                return true;
            }
        });
        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testRegistryConfigDocumentParse() throws Exception {
        File document = new File("src/test/resources/test.doc");
        template.sendBody("direct:start3", document);
        resultEndpoint.setExpectedMessageCount(1);
        resultEndpoint.expectedMessagesMatches(new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                Object body = exchange.getIn().getBody(String.class);
                Map<String, Object> headerMap = exchange.getIn().getHeaders();
                assertThat(body, instanceOf(String.class));
                assertThat(((String) (body)), containsString("<body/>"));
                assertThat(headerMap.get(CONTENT_TYPE), equalTo("application/msword"));
                return true;
            }
        });
        resultEndpoint.assertIsSatisfied();
    }
}

