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
package org.apache.camel.impl.validator;


import java.util.Map;
import org.apache.camel.AsyncCallback;
import org.apache.camel.Consumer;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.TestSupport;
import org.apache.camel.ValidationException;
import org.apache.camel.spi.DataType;
import org.apache.camel.spi.Validator;
import org.apache.camel.support.DefaultAsyncProducer;
import org.apache.camel.support.DefaultComponent;
import org.apache.camel.support.DefaultEndpoint;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A ValidatorRouteTest demonstrates contract based declarative validation via Java DSL.
 */
public class ValidatorRouteTest extends ContextTestSupport {
    protected static final Logger LOG = LoggerFactory.getLogger(ValidatorRouteTest.class);

    private static final String VALIDATOR_INVOKED = "validator-invoked";

    @Test
    public void testPredicateValidator() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context, ExchangePattern.InOut);
        exchange.getIn().setBody("{name:XOrder}");
        Exchange answerEx = template.send("direct:predicate", exchange);
        if ((answerEx.getException()) != null) {
            throw answerEx.getException();
        }
        Assert.assertEquals("{name:XOrderResponse}", answerEx.getIn().getBody(String.class));
    }

    @Test
    public void testEndpointValidator() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context, ExchangePattern.InOut);
        exchange.getIn().setBody("<XOrder/>");
        Exchange answerEx = template.send("direct:endpoint", exchange);
        if ((answerEx.getException()) != null) {
            throw answerEx.getException();
        }
        Assert.assertEquals("<XOrderResponse/>", answerEx.getOut().getBody(String.class));
        Assert.assertEquals(ValidatorRouteTest.MyXmlEndpoint.class, answerEx.getProperty(ValidatorRouteTest.VALIDATOR_INVOKED));
    }

    @Test
    public void testCustomValidator() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context, ExchangePattern.InOut);
        exchange.getIn().setBody("name=XOrder");
        Exchange answerEx = template.send("direct:custom", exchange);
        if ((answerEx.getException()) != null) {
            throw answerEx.getException();
        }
        Assert.assertEquals("name=XOrderResponse", answerEx.getOut().getBody(String.class));
        Assert.assertEquals(ValidatorRouteTest.OtherXOrderResponseValidator.class, answerEx.getProperty(ValidatorRouteTest.VALIDATOR_INVOKED));
    }

    public static class MyXmlComponent extends DefaultComponent {
        @Override
        protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
            return new ValidatorRouteTest.MyXmlEndpoint();
        }
    }

    public static class MyXmlEndpoint extends DefaultEndpoint {
        @Override
        public Producer createProducer() throws Exception {
            return new DefaultAsyncProducer(this) {
                @Override
                public boolean process(Exchange exchange, AsyncCallback callback) {
                    exchange.setProperty(ValidatorRouteTest.VALIDATOR_INVOKED, ValidatorRouteTest.MyXmlEndpoint.class);
                    Assert.assertEquals("<XOrderResponse/>", exchange.getIn().getBody());
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

    public static class OtherXOrderValidator extends Validator {
        @Override
        public void validate(Message message, DataType type) throws ValidationException {
            message.getExchange().setProperty(ValidatorRouteTest.VALIDATOR_INVOKED, ValidatorRouteTest.OtherXOrderValidator.class);
            Assert.assertEquals("name=XOrder", message.getBody());
            ValidatorRouteTest.this.log.info("Java validation: other XOrder");
        }
    }

    public static class OtherXOrderResponseValidator extends Validator {
        @Override
        public void validate(Message message, DataType type) throws ValidationException {
            message.getExchange().setProperty(ValidatorRouteTest.VALIDATOR_INVOKED, ValidatorRouteTest.OtherXOrderResponseValidator.class);
            Assert.assertEquals("name=XOrderResponse", message.getBody());
            ValidatorRouteTest.this.log.info("Java validation: other XOrderResponse");
        }
    }
}

