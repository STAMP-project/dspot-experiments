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
package org.apache.camel.impl.transformer;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;
import org.apache.camel.AsyncCallback;
import org.apache.camel.Consumer;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Converter;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.TestSupport;
import org.apache.camel.TypeConverters;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.spi.DataType;
import org.apache.camel.spi.DataTypeAware;
import org.apache.camel.spi.RouteContext;
import org.apache.camel.spi.Transformer;
import org.apache.camel.support.DefaultAsyncProducer;
import org.apache.camel.support.DefaultComponent;
import org.apache.camel.support.DefaultEndpoint;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A TransformerTest demonstrates contract based declarative transformation via Java DSL.
 */
public class TransformerRouteTest extends ContextTestSupport {
    protected static final Logger LOG = LoggerFactory.getLogger(TransformerRouteTest.class);

    @Test
    public void testJavaTransformer() throws Exception {
        MockEndpoint abcresult = getMockEndpoint("mock:abcresult");
        abcresult.expectedMessageCount(1);
        abcresult.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                TransformerRouteTest.LOG.info("Asserting String -> XOrderResponse convertion");
                Assert.assertEquals(TransformerRouteTest.XOrderResponse.class, exchange.getIn().getBody().getClass());
            }
        });
        MockEndpoint xyzresult = getMockEndpoint("mock:xyzresult");
        xyzresult.expectedMessageCount(1);
        xyzresult.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                TransformerRouteTest.LOG.info("Asserting String -> XOrderResponse convertion is not yet performed");
                Assert.assertEquals("response", exchange.getIn().getBody());
            }
        });
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context, ExchangePattern.InOut);
        exchange.getIn().setBody(new TransformerRouteTest.AOrder());
        Exchange answerEx = template.send("direct:abc", exchange);
        if ((answerEx.getException()) != null) {
            throw answerEx.getException();
        }
        Assert.assertEquals(TransformerRouteTest.AOrderResponse.class, answerEx.getOut().getBody().getClass());
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testDataFormatTransformer() throws Exception {
        MockEndpoint xyzresult = getMockEndpoint("mock:xyzresult");
        xyzresult.expectedMessageCount(1);
        xyzresult.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                TransformerRouteTest.LOG.info("Asserting String -> XOrderResponse convertion is not yet performed");
                Assert.assertEquals("response", exchange.getIn().getBody());
            }
        });
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context, ExchangePattern.InOut);
        ((DataTypeAware) (exchange.getIn())).setBody("{name:XOrder}", new DataType("json:JsonXOrder"));
        Exchange answerEx = template.send("direct:dataFormat", exchange);
        if ((answerEx.getException()) != null) {
            throw answerEx.getException();
        }
        Assert.assertEquals("{name:XOrderResponse}", answerEx.getOut().getBody(String.class));
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testEndpointTransformer() throws Exception {
        MockEndpoint xyzresult = getMockEndpoint("mock:xyzresult");
        xyzresult.expectedMessageCount(1);
        xyzresult.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                TransformerRouteTest.LOG.info("Asserting String -> XOrderResponse convertion is not yet performed");
                Assert.assertEquals("response", exchange.getIn().getBody());
            }
        });
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context, ExchangePattern.InOut);
        exchange.getIn().setBody("<XOrder/>");
        Exchange answerEx = template.send("direct:endpoint", exchange);
        if ((answerEx.getException()) != null) {
            throw answerEx.getException();
        }
        Assert.assertEquals("<XOrderResponse/>", answerEx.getOut().getBody(String.class));
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testCustomTransformer() throws Exception {
        MockEndpoint xyzresult = getMockEndpoint("mock:xyzresult");
        xyzresult.expectedMessageCount(1);
        xyzresult.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                TransformerRouteTest.LOG.info("Asserting String -> XOrderResponse convertion is not yet performed");
                Assert.assertEquals("response", exchange.getIn().getBody());
            }
        });
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context, ExchangePattern.InOut);
        exchange.getIn().setBody("name=XOrder");
        Exchange answerEx = template.send("direct:custom", exchange);
        if ((answerEx.getException()) != null) {
            throw answerEx.getException();
        }
        Assert.assertEquals("name=XOrderResponse", answerEx.getOut().getBody(String.class));
        assertMockEndpointsSatisfied();
    }

    public static class MyTypeConverters implements TypeConverters {
        @Converter
        public TransformerRouteTest.AOrder toAOrder(String order) {
            TransformerRouteTest.LOG.info("TypeConverter: String -> AOrder");
            return new TransformerRouteTest.AOrder();
        }

        @Converter
        public TransformerRouteTest.XOrder toXOrder(TransformerRouteTest.AOrder aorder) {
            TransformerRouteTest.LOG.info("TypeConverter: AOrder -> XOrder");
            return new TransformerRouteTest.XOrder();
        }

        @Converter
        public TransformerRouteTest.XOrderResponse toXOrderResponse(String res) {
            TransformerRouteTest.LOG.info("TypeConverter: String -> XOrderResponse");
            return new TransformerRouteTest.XOrderResponse();
        }

        @Converter
        public TransformerRouteTest.AOrderResponse toAOrderResponse(TransformerRouteTest.XOrderResponse xres) {
            TransformerRouteTest.LOG.info("TypeConverter: XOrderResponse -> AOrderResponse");
            return new TransformerRouteTest.AOrderResponse();
        }
    }

    public static class MyJsonDataFormatDefinition extends DataFormatDefinition {
        public static DataFormat getDataFormat(RouteContext routeContext, DataFormatDefinition type, String ref) {
            return new TransformerRouteTest.MyJsonDataFormatDefinition().createDataFormat();
        }

        public DataFormat getDataFormat(RouteContext routeContext) {
            return createDataFormat();
        }

        private DataFormat createDataFormat() {
            return new DataFormat() {
                @Override
                public void marshal(Exchange exchange, Object graph, OutputStream stream) throws Exception {
                    Assert.assertEquals(graph.toString(), TransformerRouteTest.XOrderResponse.class, graph.getClass());
                    TransformerRouteTest.LOG.info("DataFormat: XOrderResponse -> JSON");
                    stream.write("{name:XOrderResponse}".getBytes());
                }

                @Override
                public Object unmarshal(Exchange exchange, InputStream stream) throws Exception {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    String line = "";
                    String input = "";
                    while ((line = reader.readLine()) != null) {
                        input += line;
                    } 
                    reader.close();
                    Assert.assertEquals("{name:XOrder}", input);
                    TransformerRouteTest.LOG.info("DataFormat: JSON -> XOrder");
                    return new TransformerRouteTest.XOrder();
                }
            };
        }
    }

    public static class MyXmlComponent extends DefaultComponent {
        @Override
        protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
            return new TransformerRouteTest.MyXmlEndpoint();
        }
    }

    public static class MyXmlEndpoint extends DefaultEndpoint {
        @Override
        public Producer createProducer() throws Exception {
            return new DefaultAsyncProducer(this) {
                @Override
                public boolean process(Exchange exchange, AsyncCallback callback) {
                    Object input = exchange.getIn().getBody();
                    if (input instanceof TransformerRouteTest.XOrderResponse) {
                        TransformerRouteTest.this.log.info("Endpoint: XOrderResponse -> XML");
                        exchange.getIn().setBody("<XOrderResponse/>");
                    } else {
                        Assert.assertEquals("<XOrder/>", input);
                        TransformerRouteTest.this.log.info("Endpoint: XML -> XOrder");
                        exchange.getIn().setBody(new TransformerRouteTest.XOrder());
                    }
                    callback.done(true);
                    return true;
                }
            };
        }

        @Override
        public Consumer createConsumer(Processor processor) throws Exception {
            return null;
        }

        @Override
        public boolean isSingleton() {
            return false;
        }

        @Override
        protected String createEndpointUri() {
            return "myxml:endpoint";
        }
    }

    public static class OtherToXOrderTransformer extends Transformer {
        @Override
        public void transform(Message message, DataType from, DataType to) throws Exception {
            Assert.assertEquals("name=XOrder", message.getBody());
            TransformerRouteTest.this.log.info("Bean: Other -> XOrder");
            message.setBody(new TransformerRouteTest.XOrder());
        }
    }

    public static class XOrderResponseToOtherTransformer extends Transformer {
        @Override
        public void transform(Message message, DataType from, DataType to) throws Exception {
            TransformerRouteTest.this.log.info("Bean: XOrderResponse -> Other");
            message.setBody("name=XOrderResponse");
        }
    }

    public static class AOrder {}

    public static class AOrderResponse {}

    public static class XOrder {}

    public static class XOrderResponse {}
}

