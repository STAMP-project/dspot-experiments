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


import Exchange.EXCEPTION_CAUGHT;
import Exchange.XSLT_FATAL_ERROR;
import net.sf.saxon.expr.instruct.TerminationException;
import org.apache.camel.Exchange;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class SaxonXsltMessageTerminateTest extends CamelTestSupport {
    @Test
    public void testXsltTerminate() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(0);
        getMockEndpoint("mock:dead").expectedMessageCount(1);
        context.getRouteController().startRoute("foo");
        assertMockEndpointsSatisfied();
        Exchange out = getMockEndpoint("mock:dead").getReceivedExchanges().get(0);
        assertNotNull(out);
        // this exception is just a generic xslt error
        Exception cause = out.getProperty(EXCEPTION_CAUGHT, Exception.class);
        assertNotNull(cause);
        // we have the xsl termination message as a error property on the exchange as we set terminate=true
        Exception error = out.getProperty(XSLT_FATAL_ERROR, Exception.class);
        assertNotNull(error);
        assertIsInstanceOf(TerminationException.class, error);
    }
}

