/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.ldap;


import com.google.inject.Injector;
import org.apache.ambari.server.ldap.domain.AmbariLdapConfiguration;
import org.apache.ambari.server.ldap.service.ads.LdapConnectionTemplateFactory;
import org.apache.ambari.server.ldap.service.ads.detectors.AttributeDetectorFactory;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.template.LdapConnectionTemplate;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test for the GUICE LdapModule setup
 *
 * - checks the module's bindings (can the GUICE context be created properely)
 * - checks for specific instances in the GUICE context (re they constructed properly, what is the instance' scope)
 *
 * It's named functional test as it creates a GUICE context. ("Real" unit tests only mock a class' collaborators, and
 * are more lightweight)
 *
 * By default the test is ignored, as it connects to external LDAP instances, thus in different environments may fail
 */
@Ignore
public class LdapModuleFunctionalTest {
    private static final Logger LOG = LoggerFactory.getLogger(LdapModuleFunctionalTest.class);

    private static Injector injector;

    @Test
    public void shouldLdapTemplateBeInstantiated() throws Exception {
        // GIVEN
        // the injector is set up
        Assert.assertNotNull(LdapModuleFunctionalTest.injector);
        // WHEN
        LdapConnectionTemplateFactory ldapConnectionTemplateFactory = LdapModuleFunctionalTest.injector.getInstance(LdapConnectionTemplateFactory.class);
        AmbariLdapConfiguration ldapConfiguration = new AmbariLdapConfiguration(LdapModuleFunctionalTest.getProps());
        LdapConnectionTemplate template = ldapConnectionTemplateFactory.create(ldapConfiguration);
        // THEN
        Assert.assertNotNull(template);
        // template.authenticate(new Dn("cn=read-only-admin,dc=example,dc=com"), "password".toCharArray());
        Boolean success = template.execute(new org.apache.directory.ldap.client.template.ConnectionCallback<Boolean>() {
            @Override
            public Boolean doWithConnection(LdapConnection connection) throws LdapException {
                return (connection.isConnected()) && (connection.isAuthenticated());
            }
        });
        Assert.assertTrue("Could not bind to the LDAP server", success);
    }

    @Test
    public void testShouldDetectorsBeBound() throws Exception {
        // GIVEN
        // WHEN
        AttributeDetectorFactory f = LdapModuleFunctionalTest.injector.getInstance(AttributeDetectorFactory.class);
        // THEN
        Assert.assertNotNull(f);
        LdapModuleFunctionalTest.LOG.info(f.groupAttributeDetector().toString());
        LdapModuleFunctionalTest.LOG.info(f.userAttributDetector().toString());
    }
}

