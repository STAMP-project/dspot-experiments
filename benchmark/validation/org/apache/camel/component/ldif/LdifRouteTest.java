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
package org.apache.camel.component.ldif;


import java.net.URL;
import java.util.List;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(FrameworkRunner.class)
@CreateLdapServer(transports = { @CreateTransport(protocol = "LDAP") })
public class LdifRouteTest extends AbstractLdapTestUnit {
    // Constants
    private static final String LDAP_CONN_NAME = "conn";

    private static final String ENDPOINT_LDIF = "ldif:" + (LdifRouteTest.LDAP_CONN_NAME);

    private static final String ENDPOINT_START = "direct:start";

    private static final SearchControls SEARCH_CONTROLS = new SearchControls(SearchControls.SUBTREE_SCOPE, 0, 0, null, true, true);

    // Properties
    private CamelContext camel;

    private ProducerTemplate template;

    private LdapContext ldapContext;

    @Test
    public void addOne() throws Exception {
        camel.addRoutes(createRouteBuilder(LdifRouteTest.ENDPOINT_LDIF));
        camel.start();
        Endpoint endpoint = camel.getEndpoint(LdifRouteTest.ENDPOINT_START);
        Exchange exchange = endpoint.createExchange();
        // then we set the LDAP filter on the in body
        URL loc = this.getClass().getResource("/org/apache/camel/component/ldif/AddOne.ldif");
        exchange.getIn().setBody(loc.toString());
        // now we send the exchange to the endpoint, and receives the response
        // from Camel
        Exchange out = template.send(endpoint, exchange);
        // Check the results
        List<String> ldifResults = defaultLdapModuleOutAssertions(out);
        Assert.assertThat(ldifResults, CoreMatchers.notNullValue());
        Assert.assertThat(ldifResults.size(), CoreMatchers.equalTo(2));// Container and user

        Assert.assertThat(ldifResults.get(0), CoreMatchers.equalTo("success"));
        Assert.assertThat(ldifResults.get(1), CoreMatchers.equalTo("success"));
        // Check LDAP
        SearchResult sr;
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("", "(uid=test*)", LdifRouteTest.SEARCH_CONTROLS);
        Assert.assertNotNull(searchResults);
        sr = searchResults.next();
        Assert.assertNotNull(sr);
        Assert.assertThat("uid=test1,ou=test,ou=system", CoreMatchers.equalTo(sr.getName()));
        Assert.assertThat(false, CoreMatchers.equalTo(searchResults.hasMore()));
    }

    @Test
    public void addOneInline() throws Exception {
        camel.addRoutes(createRouteBuilder(LdifRouteTest.ENDPOINT_LDIF));
        camel.start();
        Endpoint endpoint = camel.getEndpoint(LdifRouteTest.ENDPOINT_START);
        Exchange exchange = endpoint.createExchange();
        // then we set the LDAP filter on the in body
        URL loc = this.getClass().getResource("/org/apache/camel/component/ldif/AddOne.ldif");
        exchange.getIn().setBody(readUrl(loc));
        // now we send the exchange to the endpoint, and receives the response
        // from Camel
        Exchange out = template.send(endpoint, exchange);
        // Check the results
        List<String> ldifResults = defaultLdapModuleOutAssertions(out);
        Assert.assertThat(ldifResults, CoreMatchers.notNullValue());
        Assert.assertThat(ldifResults.size(), CoreMatchers.equalTo(2));// Container and user

        Assert.assertThat(ldifResults.get(0), CoreMatchers.equalTo("success"));
        Assert.assertThat(ldifResults.get(1), CoreMatchers.equalTo("success"));
        // Check LDAP
        SearchResult sr;
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("", "(uid=test*)", LdifRouteTest.SEARCH_CONTROLS);
        Assert.assertNotNull(searchResults);
        sr = searchResults.next();
        Assert.assertNotNull(sr);
        Assert.assertThat("uid=test1,ou=test,ou=system", CoreMatchers.equalTo(sr.getName()));
        Assert.assertThat(false, CoreMatchers.equalTo(searchResults.hasMore()));
    }

    @Test
    @ApplyLdifFiles({ "org/apache/camel/component/ldif/DeleteOneSetup.ldif" })
    public void deleteOne() throws Exception {
        camel.addRoutes(createRouteBuilder(LdifRouteTest.ENDPOINT_LDIF));
        camel.start();
        Endpoint endpoint = camel.getEndpoint(LdifRouteTest.ENDPOINT_START);
        Exchange exchange = endpoint.createExchange();
        // then we set the LDAP filter on the in body
        URL loc = this.getClass().getResource("/org/apache/camel/component/ldif/DeleteOne.ldif");
        exchange.getIn().setBody(loc.toString());
        // now we send the exchange to the endpoint, and receives the response
        // from Camel
        Exchange out = template.send(endpoint, exchange);
        // Check the results
        List<String> ldifResults = defaultLdapModuleOutAssertions(out);
        Assert.assertThat(ldifResults, CoreMatchers.notNullValue());
        Assert.assertThat(ldifResults.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(ldifResults.get(0), CoreMatchers.equalTo("success"));
        // Check LDAP
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("", "(uid=test*)", LdifRouteTest.SEARCH_CONTROLS);
        Assert.assertThat(false, CoreMatchers.equalTo(searchResults.hasMore()));
    }

    @Test
    @ApplyLdifFiles({ "org/apache/camel/component/ldif/AddDuplicateSetup.ldif" })
    public void addDuplicate() throws Exception {
        camel.addRoutes(createRouteBuilder(LdifRouteTest.ENDPOINT_LDIF));
        camel.start();
        Endpoint endpoint = camel.getEndpoint(LdifRouteTest.ENDPOINT_START);
        Exchange exchange = endpoint.createExchange();
        // then we set the LDAP filter on the in body
        URL loc = this.getClass().getResource("/org/apache/camel/component/ldif/AddDuplicate.ldif");
        exchange.getIn().setBody(loc.toString());
        // now we send the exchange to the endpoint, and receives the response
        // from Camel
        Exchange out = template.send(endpoint, exchange);
        // Check the results
        List<String> ldifResults = defaultLdapModuleOutAssertions(out);
        Assert.assertThat(ldifResults, CoreMatchers.notNullValue());
        Assert.assertThat(ldifResults.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(ldifResults.get(0), CoreMatchers.not(CoreMatchers.equalTo("success")));
    }

    @Test
    @ApplyLdifFiles({ "org/apache/camel/component/ldif/ModifySetup.ldif" })
    public void modify() throws Exception {
        camel.addRoutes(createRouteBuilder(LdifRouteTest.ENDPOINT_LDIF));
        camel.start();
        Endpoint endpoint = camel.getEndpoint(LdifRouteTest.ENDPOINT_START);
        Exchange exchange = endpoint.createExchange();
        // then we set the LDAP filter on the in body
        URL loc = this.getClass().getResource("/org/apache/camel/component/ldif/Modify.ldif");
        exchange.getIn().setBody(loc.toString());
        // now we send the exchange to the endpoint, and receives the response
        // from Camel
        Exchange out = template.send(endpoint, exchange);
        // Check the results
        List<String> ldifResults = defaultLdapModuleOutAssertions(out);
        Assert.assertThat(ldifResults, CoreMatchers.notNullValue());
        Assert.assertThat(ldifResults.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(ldifResults.get(0), CoreMatchers.equalTo("success"));
        // Check LDAP
        SearchResult sr;
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("", "(uid=test*)", LdifRouteTest.SEARCH_CONTROLS);
        Assert.assertNotNull(searchResults);
        sr = searchResults.next();
        Assert.assertNotNull(sr);
        Assert.assertThat("uid=test4,ou=test,ou=system", CoreMatchers.equalTo(sr.getName()));
        // Check the attributes of the search result
        Attributes attribs = sr.getAttributes();
        Assert.assertNotNull(attribs);
        Attribute attrib = attribs.get("sn");
        Assert.assertNotNull(attribs);
        Assert.assertThat(1, CoreMatchers.equalTo(attrib.size()));
        Assert.assertThat("5", CoreMatchers.equalTo(attrib.get(0).toString()));
        // Check no more results
        Assert.assertThat(false, CoreMatchers.equalTo(searchResults.hasMore()));
    }

    @Test
    @ApplyLdifFiles({ "org/apache/camel/component/ldif/ModRdnSetup.ldif" })
    public void modRdn() throws Exception {
        camel.addRoutes(createRouteBuilder(LdifRouteTest.ENDPOINT_LDIF));
        camel.start();
        Endpoint endpoint = camel.getEndpoint(LdifRouteTest.ENDPOINT_START);
        Exchange exchange = endpoint.createExchange();
        // then we set the LDAP filter on the in body
        URL loc = this.getClass().getResource("/org/apache/camel/component/ldif/ModRdn.ldif");
        exchange.getIn().setBody(loc.toString());
        // now we send the exchange to the endpoint, and receives the response
        // from Camel
        Exchange out = template.send(endpoint, exchange);
        // Check the results
        List<String> ldifResults = defaultLdapModuleOutAssertions(out);
        Assert.assertThat(ldifResults, CoreMatchers.notNullValue());
        Assert.assertThat(ldifResults.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(ldifResults.get(0), CoreMatchers.equalTo("success"));
        // Check LDAP
        SearchResult sr;
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("", "(uid=test*)", LdifRouteTest.SEARCH_CONTROLS);
        Assert.assertNotNull(searchResults);
        sr = searchResults.next();
        Assert.assertNotNull(sr);
        // Check the DN
        Assert.assertThat("uid=test6,ou=test,ou=system", CoreMatchers.equalTo(sr.getName()));
        // Check no more results
        Assert.assertThat(false, CoreMatchers.equalTo(searchResults.hasMore()));
    }

    @Test
    @ApplyLdifFiles({ "org/apache/camel/component/ldif/ModDnSetup.ldif" })
    public void modDn() throws Exception {
        camel.addRoutes(createRouteBuilder(LdifRouteTest.ENDPOINT_LDIF));
        camel.start();
        Endpoint endpoint = camel.getEndpoint(LdifRouteTest.ENDPOINT_START);
        Exchange exchange = endpoint.createExchange();
        // then we set the LDAP filter on the in body
        URL loc = this.getClass().getResource("/org/apache/camel/component/ldif/ModDn.ldif");
        exchange.getIn().setBody(loc.toString());
        // now we send the exchange to the endpoint, and receives the response
        // from Camel
        Exchange out = template.send(endpoint, exchange);
        // Check the results
        List<String> ldifResults = defaultLdapModuleOutAssertions(out);
        Assert.assertThat(ldifResults, CoreMatchers.notNullValue());
        Assert.assertThat(ldifResults.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(ldifResults.get(0), CoreMatchers.equalTo("success"));
        // Check LDAP
        SearchResult sr;
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("", "(uid=test*)", LdifRouteTest.SEARCH_CONTROLS);
        Assert.assertNotNull(searchResults);
        sr = searchResults.next();
        Assert.assertNotNull(sr);
        // Check the DN
        Assert.assertThat("uid=test7,ou=testnew,ou=system", CoreMatchers.equalTo(sr.getName()));
        // Check no more results
        Assert.assertThat(false, CoreMatchers.equalTo(searchResults.hasMore()));
    }
}

