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
import javax.naming.directory.SearchResult;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
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
public class LdapRouteTwoTest extends AbstractLdapTestUnit {
    private CamelContext camel;

    private ProducerTemplate template;

    private int port;

    @Test
    public void testLdapRouteStandardTwo() throws Exception {
        camel.addRoutes(createRouteBuilder((("ldap:localhost:" + (port)) + "?base=ou=system")));
        camel.start();
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
        // call again
        endpoint = camel.getEndpoint("direct:start");
        exchange = endpoint.createExchange();
        // then we set the LDAP filter on the in body
        exchange.getIn().setBody("(!(ou=test1))");
        // now we send the exchange to the endpoint, and receives the response from Camel
        out = template.send(endpoint, exchange);
        searchResults = defaultLdapModuleOutAssertions(out);
        Assert.assertFalse(contains("uid=test1,ou=test,ou=system", searchResults));
        Assert.assertTrue(contains("uid=test2,ou=test,ou=system", searchResults));
        Assert.assertTrue(contains("uid=testNoOU,ou=test,ou=system", searchResults));
        Assert.assertTrue(contains("uid=tcruise,ou=actors,ou=system", searchResults));
    }
}

