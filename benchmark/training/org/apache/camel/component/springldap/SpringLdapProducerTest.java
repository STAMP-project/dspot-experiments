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
package org.apache.camel.component.springldap;


import LdapOperation.AUTHENTICATE;
import LdapOperation.BIND;
import LdapOperation.FUNCTION_DRIVEN;
import LdapOperation.MODIFY_ATTRIBUTES;
import LdapOperation.SEARCH;
import LdapOperation.UNBIND;
import SpringLdapProducer.ATTRIBUTES;
import SpringLdapProducer.DN;
import SpringLdapProducer.FILTER;
import SpringLdapProducer.FUNCTION;
import SpringLdapProducer.MODIFICATION_ITEMS;
import SpringLdapProducer.PASSWORD;
import SpringLdapProducer.REQUEST;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;


@RunWith(MockitoJUnitRunner.class)
public class SpringLdapProducerTest extends CamelTestSupport {
    @Mock
    private SpringLdapEndpoint ldapEndpoint;

    @Mock
    private LdapTemplate ldapTemplate;

    private SpringLdapProducer ldapProducer;

    @Test(expected = UnsupportedOperationException.class)
    public void testEmptyExchange() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        ldapProducer.process(exchange);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testWrongBodyType() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Message in = new org.apache.camel.support.DefaultMessage(context);
        in.setBody("");
        exchange.setIn(in);
        ldapProducer.process(exchange);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNoDN() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Message in = new org.apache.camel.support.DefaultMessage(context);
        Map<String, Object> body = new HashMap<>();
        processBody(exchange, in, body);
    }

    @Test
    public void testNoDNForFunctionDrivenOperation() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Message in = new org.apache.camel.support.DefaultMessage(context);
        Map<String, Object> body = new HashMap<>();
        body.put(FUNCTION, Mockito.mock(BiFunction.class));
        Mockito.when(ldapEndpoint.getOperation()).thenReturn(FUNCTION_DRIVEN);
        processBody(exchange, in, body);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testEmptyDN() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Message in = new org.apache.camel.support.DefaultMessage(context);
        Map<String, Object> body = new HashMap<>();
        body.put(DN, "");
        processBody(exchange, in, body);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNullDN() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Message in = new org.apache.camel.support.DefaultMessage(context);
        Map<String, Object> body = new HashMap<>();
        body.put(DN, null);
        processBody(exchange, in, body);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNullOperation() throws Exception {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Message in = new org.apache.camel.support.DefaultMessage(context);
        Map<String, Object> body = new HashMap<>();
        body.put(DN, " ");
        processBody(exchange, in, body);
    }

    @Test
    public void testSearch() throws Exception {
        String dn = "some dn";
        String filter = "filter";
        Integer scope = SearchControls.SUBTREE_SCOPE;
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Message in = new org.apache.camel.support.DefaultMessage(context);
        Map<String, Object> body = new HashMap<>();
        body.put(DN, dn);
        body.put(FILTER, filter);
        Mockito.when(ldapEndpoint.getOperation()).thenReturn(SEARCH);
        Mockito.when(ldapEndpoint.scopeValue()).thenReturn(scope);
        processBody(exchange, in, body);
        Mockito.verify(ldapTemplate).search(ArgumentMatchers.eq(dn), ArgumentMatchers.eq(filter), ArgumentMatchers.eq(scope), ArgumentMatchers.<AttributesMapper<String>>any());
    }

    @Test
    public void testBind() throws Exception {
        String dn = "some dn";
        BasicAttributes attributes = new BasicAttributes();
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Message in = new org.apache.camel.support.DefaultMessage(context);
        Map<String, Object> body = new HashMap<>();
        body.put(DN, dn);
        body.put(ATTRIBUTES, attributes);
        Mockito.when(ldapEndpoint.getOperation()).thenReturn(BIND);
        processBody(exchange, in, body);
        Mockito.verify(ldapTemplate).bind(ArgumentMatchers.eq(dn), ArgumentMatchers.isNull(), ArgumentMatchers.eq(attributes));
    }

    @Test
    public void testUnbind() throws Exception {
        String dn = "some dn";
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Message in = new org.apache.camel.support.DefaultMessage(context);
        Map<String, Object> body = new HashMap<>();
        body.put(DN, dn);
        Mockito.when(ldapEndpoint.getOperation()).thenReturn(UNBIND);
        processBody(exchange, in, body);
        Mockito.verify(ldapTemplate).unbind(ArgumentMatchers.eq(dn));
    }

    @Test
    public void testAuthenticate() throws Exception {
        String dn = "cn=dn";
        String filter = "filter";
        String password = "password";
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Message in = new org.apache.camel.support.DefaultMessage(context);
        Map<String, Object> body = new HashMap<>();
        body.put(DN, dn);
        body.put(FILTER, filter);
        body.put(PASSWORD, password);
        Mockito.when(ldapEndpoint.getOperation()).thenReturn(AUTHENTICATE);
        processBody(exchange, in, body);
        Mockito.verify(ldapTemplate).authenticate(ArgumentMatchers.any(LdapQuery.class), ArgumentMatchers.eq(password));
    }

    @Test
    public void testModifyAttributes() throws Exception {
        String dn = "cn=dn";
        ModificationItem[] modificationItems = new ModificationItem[]{ new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("key", "value")) };
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Message in = new org.apache.camel.support.DefaultMessage(context);
        Map<String, Object> body = new HashMap<>();
        body.put(DN, dn);
        body.put(MODIFICATION_ITEMS, modificationItems);
        Mockito.when(ldapEndpoint.getOperation()).thenReturn(MODIFY_ATTRIBUTES);
        processBody(exchange, in, body);
        Mockito.verify(ldapTemplate).modifyAttributes(ArgumentMatchers.eq(dn), ArgumentMatchers.eq(modificationItems));
    }

    @Test
    public void testFunctionDriven() throws Exception {
        String dn = "cn=dn";
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Message in = new org.apache.camel.support.DefaultMessage(context);
        Map<String, Object> body = new HashMap<>();
        body.put(DN, dn);
        body.put(REQUEST, dn);
        body.put(FUNCTION, ((BiFunction<LdapOperations, String, Void>) (( l, q) -> {
            l.lookup(q);
            return null;
        })));
        Mockito.when(ldapEndpoint.getOperation()).thenReturn(FUNCTION_DRIVEN);
        processBody(exchange, in, body);
        Mockito.verify(ldapTemplate).lookup(ArgumentMatchers.eq(dn));
    }
}

