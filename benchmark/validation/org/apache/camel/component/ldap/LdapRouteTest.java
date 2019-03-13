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
package org.apache.camel.component.ldap;


import java.util.Collection;
import javax.activation.DataHandler;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(FrameworkRunner.class)
@CreateLdapServer(transports = { @CreateTransport(protocol = "LDAP") })
@ApplyLdifFiles("org/apache/camel/component/ldap/LdapRouteTest.ldif")
public class LdapRouteTest extends AbstractLdapTestUnit {
    private CamelContext camel;

    private ProducerTemplate template;

    private int port;

    @Test
    public void testLdapRouteStandard() throws Exception {
        camel.addRoutes(createRouteBuilder((("ldap:localhost:" + (port)) + "?base=ou=system")));
        camel.start();
        // START SNIPPET: invoke
        Endpoint endpoint = camel.getEndpoint("direct:start");
        Exchange exchange = endpoint.createExchange();
        // then we set the LDAP filter on the in body
        exchange.getIn().setBody("(!(ou=test1))");
        // now we send the exchange to the endpoint, and receives the response from Camel
        Exchange out = template.send(endpoint, exchange);
        Collection<SearchResult> searchResults = defaultLdapModuleOutAssertions(out);
        Assert.assertFalse(contains("uid=test1,ou=test,ou=system", searchResults));
        Assert.assertTrue(contains("uid=test2,ou=test,ou=system", searchResults));
        Assert.assertTrue(contains("uid=testNoOU,ou=test,ou=system", searchResults));
        Assert.assertTrue(contains("uid=tcruise,ou=actors,ou=system", searchResults));
        // START SNIPPET: invoke
    }

    @Test
    public void testLdapRouteWithPaging() throws Exception {
        camel.addRoutes(createRouteBuilder((("ldap:localhost:" + (port)) + "?base=ou=system&pageSize=5")));
        camel.start();
        Endpoint endpoint = camel.getEndpoint("direct:start");
        Exchange exchange = endpoint.createExchange();
        // then we set the LDAP filter on the in body
        exchange.getIn().setBody("(objectClass=*)");
        // now we send the exchange to the endpoint, and receives the response from Camel
        Exchange out = template.send(endpoint, exchange);
        Collection<SearchResult> searchResults = defaultLdapModuleOutAssertions(out);
        Assert.assertEquals(16, searchResults.size());
    }

    @Test
    public void testLdapRouteReturnedAttributes() throws Exception {
        // LDAP servers behave differently when it comes to what attributes are returned
        // by default (Apache Directory server returns all attributes by default)
        // Using the returnedAttributes parameter this behavior can be controlled
        camel.addRoutes(createRouteBuilder((("ldap:localhost:" + (port)) + "?base=ou=system&returnedAttributes=uid,cn")));
        camel.start();
        Endpoint endpoint = camel.getEndpoint("direct:start");
        Exchange exchange = endpoint.createExchange();
        // then we set the LDAP filter on the in body
        exchange.getIn().setBody("(uid=tcruise)");
        // now we send the exchange to the endpoint, and receives the response from Camel
        Exchange out = template.send(endpoint, exchange);
        Collection<SearchResult> searchResults = defaultLdapModuleOutAssertions(out);
        Assert.assertEquals(1, searchResults.size());
        Assert.assertTrue(contains("uid=tcruise,ou=actors,ou=system", searchResults));
        Attributes theOneResultAtts = searchResults.iterator().next().getAttributes();
        Assert.assertEquals("tcruise", theOneResultAtts.get("uid").get());
        Assert.assertEquals("Tom Cruise", theOneResultAtts.get("cn").get());
        // make sure this att is NOT returned anymore
        Assert.assertNull(theOneResultAtts.get("sn"));
    }

    @Test
    public void testLdapRoutePreserveHeaderAndAttachments() throws Exception {
        camel.addRoutes(createRouteBuilder((("ldap:localhost:" + (port)) + "?base=ou=system")));
        camel.start();
        final DataHandler dataHandler = new DataHandler("test", "text");
        Exchange out = template.request("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody("(!(ou=test1))");
                exchange.getIn().setHeader("ldapTest", "Camel");
                exchange.getIn().addAttachment("ldapAttachment", dataHandler);
            }
        });
        Collection<SearchResult> searchResults = defaultLdapModuleOutAssertions(out);
        Assert.assertFalse(contains("uid=test1,ou=test,ou=system", searchResults));
        Assert.assertTrue(contains("uid=test2,ou=test,ou=system", searchResults));
        Assert.assertTrue(contains("uid=testNoOU,ou=test,ou=system", searchResults));
        Assert.assertTrue(contains("uid=tcruise,ou=actors,ou=system", searchResults));
        Assert.assertEquals("Camel", out.getOut().getHeader("ldapTest"));
        Assert.assertSame(dataHandler, out.getOut().getAttachment("ldapAttachment"));
    }
}

