/**
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.oauth2.provider.client;


import java.util.Collections;
import java.util.TreeSet;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.StringUtils;


/**
 *
 *
 * @author Dave Syer
 */
public class BaseClientDetailsTests {
    /**
     * test default constructor
     */
    @Test
    public void testBaseClientDetailsDefaultConstructor() {
        BaseClientDetails details = new BaseClientDetails();
        Assert.assertEquals("[]", details.getResourceIds().toString());
        Assert.assertEquals("[]", details.getScope().toString());
        Assert.assertEquals("[]", details.getAuthorizedGrantTypes().toString());
        Assert.assertEquals("[]", details.getAuthorities().toString());
    }

    /**
     * test explicit convenience constructor
     */
    @Test
    public void testBaseClientDetailsConvenienceConstructor() {
        BaseClientDetails details = new BaseClientDetails("foo", "", "foo,bar", "authorization_code", "ROLE_USER");
        Assert.assertEquals("[]", details.getResourceIds().toString());
        Assert.assertEquals("[bar, foo]", new TreeSet<String>(details.getScope()).toString());
        Assert.assertEquals("[authorization_code]", details.getAuthorizedGrantTypes().toString());
        Assert.assertEquals("[ROLE_USER]", details.getAuthorities().toString());
    }

    /**
     * test explicit autoapprove
     */
    @Test
    public void testBaseClientDetailsAutoApprove() {
        BaseClientDetails details = new BaseClientDetails("foo", "", "foo,bar", "authorization_code", "ROLE_USER");
        details.setAutoApproveScopes(StringUtils.commaDelimitedListToSet("read,write"));
        Assert.assertTrue(details.isAutoApprove("read"));
    }

    @Test
    public void testBaseClientDetailsImplicitAutoApprove() {
        BaseClientDetails details = new BaseClientDetails("foo", "", "foo,bar", "authorization_code", "ROLE_USER");
        details.setAutoApproveScopes(StringUtils.commaDelimitedListToSet("true"));
        Assert.assertTrue(details.isAutoApprove("read"));
    }

    @Test
    public void testBaseClientDetailsNoAutoApprove() {
        BaseClientDetails details = new BaseClientDetails("foo", "", "foo,bar", "authorization_code", "ROLE_USER");
        details.setAutoApproveScopes(StringUtils.commaDelimitedListToSet("none"));
        Assert.assertFalse(details.isAutoApprove("read"));
    }

    @Test
    public void testBaseClientDetailsNullAutoApprove() {
        BaseClientDetails details = new BaseClientDetails("foo", "", "foo,bar", "authorization_code", "ROLE_USER");
        Assert.assertFalse(details.isAutoApprove("read"));
    }

    @Test
    public void testJsonSerialize() throws Exception {
        BaseClientDetails details = new BaseClientDetails("foo", "", "foo,bar", "authorization_code", "ROLE_USER");
        details.setClientId("foo");
        details.setClientSecret("bar");
        String value = new ObjectMapper().writeValueAsString(details);
        Assert.assertTrue(value.contains("client_id"));
        Assert.assertTrue(value.contains("client_secret"));
        Assert.assertTrue(value.contains("authorized_grant_types"));
        Assert.assertTrue(value.contains("[\"ROLE_USER\"]"));
    }

    @Test
    public void testJsonSerializeAdditionalInformation() throws Exception {
        BaseClientDetails details = new BaseClientDetails("foo", "", "foo,bar", "authorization_code", "ROLE_USER");
        details.setClientId("foo");
        details.setAdditionalInformation(Collections.singletonMap("foo", "bar"));
        String value = new ObjectMapper().writeValueAsString(details);
        Assert.assertTrue(value.contains("\"foo\":\"bar\""));
    }

    @Test
    public void testJsonDeserialize() throws Exception {
        String value = "{\"foo\":\"bar\",\"client_id\":\"foo\",\"scope\":[\"bar\",\"foo\"],\"authorized_grant_types\":[\"authorization_code\"],\"authorities\":[\"ROLE_USER\"]}";
        BaseClientDetails details = new ObjectMapper().readValue(value, BaseClientDetails.class);
        BaseClientDetails expected = new BaseClientDetails("foo", "", "foo,bar", "authorization_code", "ROLE_USER");
        expected.setAdditionalInformation(Collections.singletonMap("foo", ((Object) ("bar"))));
        Assert.assertEquals(expected, details);
    }

    @Test
    public void testJsonDeserializeWithArraysAsStrings() throws Exception {
        // Collection values can be deserialized from space or comma-separated lists
        String value = "{\"foo\":\"bar\",\"client_id\":\"foo\",\"scope\":\"bar  foo\",\"authorized_grant_types\":\"authorization_code\",\"authorities\":\"ROLE_USER,ROLE_ADMIN\"}";
        BaseClientDetails details = new ObjectMapper().readValue(value, BaseClientDetails.class);
        BaseClientDetails expected = new BaseClientDetails("foo", "", "foo,bar", "authorization_code", "ROLE_USER,ROLE_ADMIN");
        expected.setAdditionalInformation(Collections.singletonMap("foo", ((Object) ("bar"))));
        Assert.assertEquals(expected, details);
    }

    /**
     * test equality
     */
    @Test
    public void testEqualityOfValidity() {
        BaseClientDetails details = new BaseClientDetails();
        details.setAccessTokenValiditySeconds(100);
        BaseClientDetails other = new BaseClientDetails();
        other.setAccessTokenValiditySeconds(100);
        Assert.assertEquals(details, other);
    }
}

