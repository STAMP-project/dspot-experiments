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
package org.apache.camel.component.xslt;


import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import org.apache.camel.CamelContext;
import org.apache.camel.FailedToCreateRouteException;
import org.apache.camel.TestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class XsltCustomErrorListenerTest extends TestSupport {
    private XsltCustomErrorListenerTest.MyErrorListener listener = new XsltCustomErrorListenerTest.MyErrorListener();

    private class MyErrorListener implements ErrorListener {
        private boolean warning;

        private boolean error;

        private boolean fatalError;

        @Override
        public void warning(TransformerException exception) throws TransformerException {
            warning = true;
        }

        @Override
        public void error(TransformerException exception) throws TransformerException {
            error = true;
        }

        @Override
        public void fatalError(TransformerException exception) throws TransformerException {
            fatalError = true;
        }

        public boolean isWarning() {
            return warning;
        }

        public boolean isError() {
            return error;
        }

        public boolean isFatalError() {
            return fatalError;
        }
    }

    @Test
    public void testErrorListener() throws Exception {
        try {
            RouteBuilder builder = createRouteBuilder();
            CamelContext context = new DefaultCamelContext();
            context.getRegistry().bind("myListener", listener);
            context.addRoutes(builder);
            context.start();
            Assert.fail("Should have thrown an exception due XSLT file not found");
        } catch (FailedToCreateRouteException e) {
            // expected
        }
        Assert.assertFalse(listener.isWarning());
        Assert.assertTrue("My error listener should been invoked", listener.isError());
        Assert.assertTrue("My error listener should been invoked", listener.isFatalError());
    }
}

