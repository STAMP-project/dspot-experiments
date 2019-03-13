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


import SpringLdapProducer.ATTRIBUTES;
import SpringLdapProducer.FILTER;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;


@RunWith(MockitoJUnitRunner.class)
public class SpringLdapComponentTest extends CamelSpringTestSupport {
    private LdapTemplate ldapTemplate;

    private ProducerTemplate producer;

    private Map<String, Object> body;

    @Captor
    private ArgumentCaptor<String> dnCaptor;

    @Captor
    private ArgumentCaptor<Attributes> attributesCaptor;

    @Captor
    private ArgumentCaptor<Object> objectToBindCaptor;

    @Captor
    private ArgumentCaptor<String> filterCaptor;

    @Captor
    private ArgumentCaptor<Integer> scopeCaptor;

    @Captor
    private ArgumentCaptor<AttributesMapper<String>> mapperCaptor;

    @Test
    public void testUnbind() throws Exception {
        String dnToUnbind = "some dn to unbind";
        initializeTest(dnToUnbind);
        producer.sendBody((("spring-ldap:" + (SpringLdapTestConfiguration.LDAP_MOCK_NAME)) + "?operation=unbind"), body);
        Mockito.verify(ldapTemplate).unbind(dnCaptor.capture());
        assertEquals(dnToUnbind, dnCaptor.getValue());
    }

    @Test
    public void testBind() throws Exception {
        String dnToBind = "some dn to bind";
        initializeTest(dnToBind);
        Attributes attributes = new BasicAttributes();
        attributes.put("some attribute name", "some attribute value");
        body.put(ATTRIBUTES, attributes);
        producer.sendBody((("spring-ldap:" + (SpringLdapTestConfiguration.LDAP_MOCK_NAME)) + "?operation=bind"), body);
        Mockito.verify(ldapTemplate).bind(dnCaptor.capture(), objectToBindCaptor.capture(), attributesCaptor.capture());
        assertEquals(dnToBind, dnCaptor.getValue());
        assertNull(objectToBindCaptor.getValue());
        assertEquals(attributes, attributesCaptor.getValue());
    }

    @Test
    public void testSearch() throws Exception {
        String dnToSearch = "some dn to bind";
        initializeTest(dnToSearch);
        String filter = "some ldap filter";
        body.put(FILTER, filter);
        List<String> searchResult = Collections.singletonList("some search result");
        Mockito.when(ldapTemplate.search(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(Integer.class), ArgumentMatchers.<AttributesMapper<String>>any())).thenReturn(searchResult);
        MockEndpoint resultEndpoint = ((MockEndpoint) (context.getEndpoint("mock:result")));
        resultEndpoint.expectedBodiesReceived(Collections.singletonList(searchResult));
        producer.sendBody("direct:start", body);
        Mockito.verify(ldapTemplate).search(dnCaptor.capture(), filterCaptor.capture(), scopeCaptor.capture(), mapperCaptor.capture());
        assertEquals(dnToSearch, dnCaptor.getValue());
        assertEquals(((Integer) (SearchControls.ONELEVEL_SCOPE)), scopeCaptor.getValue());
        assertEquals(filter, filterCaptor.getValue());
        resultEndpoint.assertIsSatisfied();
    }
}

