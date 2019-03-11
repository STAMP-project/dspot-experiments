/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.web.security;


import java.util.List;
import java.util.Properties;
import org.apache.nifi.authorization.Authorizer;
import org.apache.nifi.authorization.util.IdentityMapping;
import org.apache.nifi.util.NiFiProperties;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;


public class NiFiAuthenticationProviderTest {
    @Test
    public void testValidPropertiesProvided() {
        final String pattern = "^cn=(.*?),dc=(.*?),dc=(.*?)$";
        final String value = "$1@$2.$3";
        Properties properties = new Properties();
        properties.setProperty("nifi.security.identity.mapping.pattern.dn", pattern);
        properties.setProperty("nifi.security.identity.mapping.value.dn", value);
        final NiFiProperties nifiProperties = getNiFiProperties(properties);
        NiFiAuthenticationProviderTest.TestableNiFiAuthenticationProvider provider = new NiFiAuthenticationProviderTest.TestableNiFiAuthenticationProvider(nifiProperties);
        List<IdentityMapping> mappings = getMappings();
        Assert.assertEquals(1, mappings.size());
        Assert.assertEquals("dn", mappings.get(0).getKey());
        Assert.assertEquals(pattern, mappings.get(0).getPattern().pattern());
        Assert.assertEquals(value, mappings.get(0).getReplacementValue());
    }

    @Test
    public void testNoMappings() {
        Properties properties = new Properties();
        final NiFiProperties nifiProperties = getNiFiProperties(properties);
        NiFiAuthenticationProviderTest.TestableNiFiAuthenticationProvider provider = new NiFiAuthenticationProviderTest.TestableNiFiAuthenticationProvider(nifiProperties);
        List<IdentityMapping> mappings = getMappings();
        Assert.assertEquals(0, mappings.size());
        final String identity = "john";
        Assert.assertEquals(identity, mapIdentity(identity));
    }

    @Test
    public void testPatternPropertyWithNoValue() {
        Properties properties = new Properties();
        properties.setProperty("nifi.security.identity.mapping.pattern.dn", "");
        properties.setProperty("nifi.security.identity.mapping.value.dn", "value");
        final NiFiProperties nifiProperties = getNiFiProperties(properties);
        NiFiAuthenticationProviderTest.TestableNiFiAuthenticationProvider provider = new NiFiAuthenticationProviderTest.TestableNiFiAuthenticationProvider(nifiProperties);
        List<IdentityMapping> mappings = getMappings();
        Assert.assertEquals(0, mappings.size());
    }

    @Test
    public void testPatternPropertyWithNoCorrespondingValueProperty() {
        Properties properties = new Properties();
        properties.setProperty("nifi.security.identity.mapping.pattern.dn", "");
        final NiFiProperties nifiProperties = getNiFiProperties(properties);
        NiFiAuthenticationProviderTest.TestableNiFiAuthenticationProvider provider = new NiFiAuthenticationProviderTest.TestableNiFiAuthenticationProvider(nifiProperties);
        List<IdentityMapping> mappings = getMappings();
        Assert.assertEquals(0, mappings.size());
    }

    @Test
    public void testMultipleMappings() {
        Properties properties = new Properties();
        properties.setProperty("nifi.security.identity.mapping.pattern.1", "pattern1");
        properties.setProperty("nifi.security.identity.mapping.value.1", "value1");
        properties.setProperty("nifi.security.identity.mapping.pattern.2", "pattern2");
        properties.setProperty("nifi.security.identity.mapping.value.2", "value2");
        properties.setProperty("nifi.security.identity.mapping.pattern.3", "pattern3");
        properties.setProperty("nifi.security.identity.mapping.value.3", "value3");
        final NiFiProperties nifiProperties = getNiFiProperties(properties);
        NiFiAuthenticationProviderTest.TestableNiFiAuthenticationProvider provider = new NiFiAuthenticationProviderTest.TestableNiFiAuthenticationProvider(nifiProperties);
        List<IdentityMapping> mappings = getMappings();
        Assert.assertEquals(3, mappings.size());
    }

    @Test
    public void testMapIdentityWithSingleMapping() {
        final Properties properties = new Properties();
        properties.setProperty("nifi.security.identity.mapping.pattern.dn", "^cn=(.*?),dc=(.*?),dc=(.*?)$");
        properties.setProperty("nifi.security.identity.mapping.value.dn", "$1@$2.$3");
        final NiFiProperties nifiProperties = getNiFiProperties(properties);
        final NiFiAuthenticationProviderTest.TestableNiFiAuthenticationProvider provider = new NiFiAuthenticationProviderTest.TestableNiFiAuthenticationProvider(nifiProperties);
        final String identity = "cn=jsmith,dc=aaa,dc=bbb";
        final String mappedIdentity = provider.mapIdentity(identity);
        Assert.assertEquals("jsmith@aaa.bbb", mappedIdentity);
    }

    @Test
    public void testMapIdentityWithIncorrectGroupReference() {
        final Properties properties = new Properties();
        properties.setProperty("nifi.security.identity.mapping.pattern.dn", "^cn=(.*?),dc=(.*?),dc=(.*?)$");
        properties.setProperty("nifi.security.identity.mapping.value.dn", "$1@$2.$4");
        final NiFiProperties nifiProperties = getNiFiProperties(properties);
        final NiFiAuthenticationProviderTest.TestableNiFiAuthenticationProvider provider = new NiFiAuthenticationProviderTest.TestableNiFiAuthenticationProvider(nifiProperties);
        final String identity = "cn=jsmith,dc=aaa,dc=bbb";
        final String mappedIdentity = provider.mapIdentity(identity);
        Assert.assertEquals("jsmith@aaa.$4", mappedIdentity);
    }

    @Test
    public void testMapIdentityWithNoGroupReference() {
        final Properties properties = new Properties();
        properties.setProperty("nifi.security.identity.mapping.pattern.dn", "^cn=(.*?),dc=(.*?),dc=(.*?)$");
        properties.setProperty("nifi.security.identity.mapping.value.dn", "this makes no sense");
        final NiFiProperties nifiProperties = getNiFiProperties(properties);
        final NiFiAuthenticationProviderTest.TestableNiFiAuthenticationProvider provider = new NiFiAuthenticationProviderTest.TestableNiFiAuthenticationProvider(nifiProperties);
        final String identity = "cn=jsmith,dc=aaa,dc=bbb";
        final String mappedIdentity = provider.mapIdentity(identity);
        Assert.assertEquals("this makes no sense", mappedIdentity);
    }

    @Test
    public void testMapIdentityWithMultipleMatchingPatterns() {
        // create two pattern properties that are the same, but the value properties are different
        final Properties properties = new Properties();
        properties.setProperty("nifi.security.identity.mapping.pattern.dn2", "^cn=(.*?),dc=(.*?),dc=(.*?)$");
        properties.setProperty("nifi.security.identity.mapping.value.dn2", "$1_$2_$3");
        properties.setProperty("nifi.security.identity.mapping.pattern.dn1", "^cn=(.*?),dc=(.*?),dc=(.*?)$");
        properties.setProperty("nifi.security.identity.mapping.value.dn1", "$1 $2 $3");
        final NiFiProperties nifiProperties = getNiFiProperties(properties);
        final NiFiAuthenticationProviderTest.TestableNiFiAuthenticationProvider provider = new NiFiAuthenticationProviderTest.TestableNiFiAuthenticationProvider(nifiProperties);
        // the mapping should always use dn1 because it is sorted
        final String identity = "cn=jsmith,dc=aaa,dc=bbb";
        final String mappedIdentity = provider.mapIdentity(identity);
        Assert.assertEquals("jsmith aaa bbb", mappedIdentity);
    }

    private static class TestableNiFiAuthenticationProvider extends NiFiAuthenticationProvider {
        /**
         *
         *
         * @param properties
         * 		the NiFiProperties instance
         */
        public TestableNiFiAuthenticationProvider(NiFiProperties properties) {
            super(properties, Mockito.mock(Authorizer.class));
        }

        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            return null;
        }

        @Override
        public boolean supports(Class<?> authentication) {
            return false;
        }
    }
}

