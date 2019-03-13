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
package org.apache.camel.processor.validation;


import javax.xml.transform.sax.SAXResult;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.support.processor.validation.DefaultValidationErrorHandler;
import org.apache.camel.support.processor.validation.SchemaValidationException;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXParseException;


public class DefaultValidationErrorHandlerTest extends ContextTestSupport {
    @Test
    public void testWarning() throws Exception {
        DefaultValidationErrorHandler eh = new DefaultValidationErrorHandler();
        eh.warning(new SAXParseException("foo", createLocator(1, 2)));
        // just a warning so should be valid
        Assert.assertEquals(true, eh.isValid());
    }

    @Test
    public void testError() throws Exception {
        DefaultValidationErrorHandler eh = new DefaultValidationErrorHandler();
        eh.error(new SAXParseException("foo", createLocator(3, 5)));
        Assert.assertEquals(false, eh.isValid());
    }

    @Test
    public void testFatalError() throws Exception {
        DefaultValidationErrorHandler eh = new DefaultValidationErrorHandler();
        eh.fatalError(new SAXParseException("foo", createLocator(5, 8)));
        Assert.assertEquals(false, eh.isValid());
    }

    @Test
    public void testReset() throws Exception {
        DefaultValidationErrorHandler eh = new DefaultValidationErrorHandler();
        eh.fatalError(new SAXParseException("foo", createLocator(5, 8)));
        Assert.assertEquals(false, eh.isValid());
        eh.reset();
        Assert.assertEquals(true, eh.isValid());
    }

    @Test
    public void testHandleErrors() throws Exception {
        DefaultValidationErrorHandler eh = new DefaultValidationErrorHandler();
        eh.error(new SAXParseException("foo", createLocator(3, 5)));
        eh.error(new SAXParseException("bar", createLocator(9, 12)));
        eh.fatalError(new SAXParseException("cheese", createLocator(13, 17)));
        Assert.assertEquals(false, eh.isValid());
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        try {
            eh.handleErrors(exchange, createScheme());
            Assert.fail("Should have thrown an exception");
        } catch (SchemaValidationException e) {
            Assert.assertEquals(2, e.getErrors().size());
            Assert.assertEquals(1, e.getFatalErrors().size());
            Assert.assertEquals(0, e.getWarnings().size());
            Assert.assertNotNull(e.getSchema());
            Assert.assertNotNull(e.getExchange());
            Assert.assertTrue(e.getMessage().startsWith("Validation failed for: org.apache.camel.processor.validation.DefaultValidationErrorHandlerTest"));
            Assert.assertTrue(e.getMessage().contains("fatal errors: ["));
            Assert.assertTrue(e.getMessage().contains("org.xml.sax.SAXParseException: cheese, Line : 13, Column : 17"));
            Assert.assertTrue(e.getMessage().contains("errors: ["));
            Assert.assertTrue(e.getMessage().contains("org.xml.sax.SAXParseException: foo, Line : 3, Column : 5"));
            Assert.assertTrue(e.getMessage().contains("org.xml.sax.SAXParseException: bar, Line : 9, Column : 12"));
            Assert.assertTrue(e.getMessage().contains("Exchange[]"));
        }
    }

    @Test
    public void testHandleErrorsResult() throws Exception {
        DefaultValidationErrorHandler eh = new DefaultValidationErrorHandler();
        eh.error(new SAXParseException("foo", createLocator(3, 5)));
        eh.error(new SAXParseException("bar", createLocator(9, 12)));
        Assert.assertEquals(false, eh.isValid());
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        try {
            eh.handleErrors(exchange, createScheme(), new SAXResult());
            Assert.fail("Should have thrown an exception");
        } catch (SchemaValidationException e) {
            Assert.assertEquals(2, e.getErrors().size());
            Assert.assertEquals(0, e.getFatalErrors().size());
            Assert.assertEquals(0, e.getWarnings().size());
            Assert.assertNotNull(e.getSchema());
            Assert.assertNotNull(e.getExchange());
            Assert.assertTrue(e.getMessage().startsWith("Validation failed for: org.apache.camel.processor.validation.DefaultValidationErrorHandlerTest"));
            Assert.assertTrue(e.getMessage().contains("errors: ["));
            Assert.assertTrue(e.getMessage().contains("org.xml.sax.SAXParseException: foo, Line : 3, Column : 5"));
            Assert.assertTrue(e.getMessage().contains("org.xml.sax.SAXParseException: bar, Line : 9, Column : 12"));
            Assert.assertTrue(e.getMessage().contains("Exchange[]"));
        }
    }
}

