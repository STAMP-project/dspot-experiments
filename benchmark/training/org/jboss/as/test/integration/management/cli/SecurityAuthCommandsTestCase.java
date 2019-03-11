/**
 * Copyright 2018 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.test.integration.management.cli;


import DefaultResourceNames.ROLE_MAPPER_NAME;
import ElytronUtil.OOTB_APPLICATION_DOMAIN;
import Util.CONSTANT_REALM_MAPPER;
import Util.CONSTANT_ROLE_MAPPER;
import Util.HTTP_AUTHENTICATION_FACTORY;
import Util.PROPERTIES_REALM;
import Util.ROLES;
import Util.SECURITY_DOMAIN;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.cli.CommandContext;
import org.jboss.as.cli.impl.aesh.cmd.security.model.ElytronUtil;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author jdenise@redhat.com
 */
@RunWith(Arquillian.class)
public class SecurityAuthCommandsTestCase {
    private static final ByteArrayOutputStream consoleOutput = new ByteArrayOutputStream();

    private static CommandContext ctx;

    @ClassRule
    public static final TemporaryFolder temporaryUserHome = new TemporaryFolder();

    private static final String TEST_DOMAIN = "test-domain";

    private static final String TEST_HTTP_FACTORY = "test-http-factory";

    private static final String TEST_USERS_REALM = "test-users-realm";

    private static final String TEST_KS_REALM = "test-ks-realm";

    private static final String TEST_FS_REALM = "test-fs-realm";

    private static final String TEST_KS = "test-ks";

    private static final String TEST_UNDERTOW_DOMAIN = "test-undertow-security-domain";

    private static List<ModelNode> originalPropertiesRealms;

    private static List<ModelNode> originalKSRealms;

    private static List<ModelNode> originalHttpFactories;

    private static List<ModelNode> originalSecurityDomains;

    private static List<ModelNode> originalFSRealms;

    private static List<ModelNode> originalConstantMappers;

    private static List<ModelNode> originalConstantRoleMappers;

    @Test
    public void testOOBHTTP() throws Exception {
        SecurityAuthCommandsTestCase.ctx.handle(("security enable-http-auth-http-server --no-reload --security-domain=" + (SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN)));
        Assert.assertEquals(OOTB_APPLICATION_DOMAIN, SecurityAuthCommandsTestCase.getReferencedSecurityDomain(SecurityAuthCommandsTestCase.ctx, SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN));
        SecurityAuthCommandsTestCase.ctx.handle(("security disable-http-auth-http-server --no-reload --security-domain=" + (SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN)));
        Assert.assertFalse(SecurityAuthCommandsTestCase.domainExists(SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN));
    }

    @Test
    public void testOOBHTTP2() throws Exception {
        // New factory but reuse OOB ApplicationRealm properties realm.
        // side effect is to create a constant realm mapper for ApplicationRealm.
        SecurityAuthCommandsTestCase.ctx.handle((((((("security enable-http-auth-http-server --no-reload --mechanism=BASIC " + ("--user-properties-file=application-users.properties --group-properties-file=application-roles.properties  " + "--relative-to=jboss.server.config.dir --exposed-realm=ApplicationRealm --new-security-domain-name=")) + (SecurityAuthCommandsTestCase.TEST_DOMAIN)) + " --new-auth-factory-name=") + (SecurityAuthCommandsTestCase.TEST_HTTP_FACTORY)) + " --security-domain=") + (SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN)));
        Assert.assertEquals(SecurityAuthCommandsTestCase.TEST_HTTP_FACTORY, SecurityAuthCommandsTestCase.getSecurityDomainAuthFactory(SecurityAuthCommandsTestCase.ctx, SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN));
        // Check Realm.
        Assert.assertEquals("ApplicationRealm", SecurityAuthCommandsTestCase.getExposedRealmName(SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN, "BASIC"));
        Assert.assertEquals("ApplicationRealm", SecurityAuthCommandsTestCase.getMechanismRealmMapper(SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN, "BASIC"));
        SecurityAuthCommandsTestCase.getNames(CONSTANT_REALM_MAPPER).contains("ApplicationRealm");
        // Replace with file system realm.
        SecurityAuthCommandsTestCase.ctx.handle((((("security enable-http-auth-http-server --no-reload --mechanism=BASIC " + "--file-system-realm-name=") + (SecurityAuthCommandsTestCase.TEST_FS_REALM)) + " --security-domain=") + (SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN)));
        Assert.assertEquals(SecurityAuthCommandsTestCase.TEST_FS_REALM, SecurityAuthCommandsTestCase.getMechanismRealmMapper(SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN, "BASIC"));
        SecurityAuthCommandsTestCase.getNames(CONSTANT_REALM_MAPPER).contains(SecurityAuthCommandsTestCase.TEST_FS_REALM);
        // check that domain has both realms.
        List<String> realms = SecurityAuthCommandsTestCase.getDomainRealms(SecurityAuthCommandsTestCase.TEST_DOMAIN);
        Assert.assertTrue(realms.contains("ApplicationRealm"));
        Assert.assertTrue(realms.contains(SecurityAuthCommandsTestCase.TEST_FS_REALM));
    }

    @Test
    public void testReferencedSecurityDomainHTTP() throws Exception {
        SecurityAuthCommandsTestCase.ctx.handle(((("security enable-http-auth-http-server --no-reload --security-domain=" + (SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN)) + " --referenced-security-domain=") + (ElytronUtil.OOTB_APPLICATION_DOMAIN)));
        Assert.assertEquals(OOTB_APPLICATION_DOMAIN, SecurityAuthCommandsTestCase.getReferencedSecurityDomain(SecurityAuthCommandsTestCase.ctx, SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN));
        SecurityAuthCommandsTestCase.ctx.handle(("security disable-http-auth-http-server --no-reload --security-domain=" + (SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN)));
        Assert.assertFalse(SecurityAuthCommandsTestCase.domainExists(SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN));
    }

    @Test
    public void testCompletion() throws Exception {
        {
            String cmd = "security enable-http-auth-http-server ";
            List<String> candidates = new ArrayList<>();
            SecurityAuthCommandsTestCase.ctx.getDefaultCommandCompleter().complete(SecurityAuthCommandsTestCase.ctx, cmd, cmd.length(), candidates);
            List<String> res = Arrays.asList("--no-reload", "--security-domain=");
            Assert.assertEquals(candidates.toString(), res, candidates);
            candidates = complete(SecurityAuthCommandsTestCase.ctx, cmd, null);
            Assert.assertEquals(candidates.toString(), res, candidates);
        }
        {
            String cmd = "security enable-http-auth-http-server --security-domain=foo ";
            List<String> candidates = new ArrayList<>();
            SecurityAuthCommandsTestCase.ctx.getDefaultCommandCompleter().complete(SecurityAuthCommandsTestCase.ctx, cmd, cmd.length(), candidates);
            List<String> res = Arrays.asList("--mechanism=", "--no-reload", "--referenced-security-domain=");
            Assert.assertEquals(candidates.toString(), res, candidates);
            candidates = complete(SecurityAuthCommandsTestCase.ctx, cmd, null);
            Assert.assertEquals(candidates.toString(), res, candidates);
        }
        {
            String cmd = "security enable-http-auth-http-server --security-domain=foo " + "--mechanism=";
            List<String> candidates = new ArrayList<>();
            SecurityAuthCommandsTestCase.ctx.getDefaultCommandCompleter().complete(SecurityAuthCommandsTestCase.ctx, cmd, cmd.length(), candidates);
            List<String> res = Arrays.asList("BASIC", "CLIENT_CERT", "DIGEST", "FORM");
            Assert.assertTrue(candidates.toString(), candidates.containsAll(res));
            candidates = complete(SecurityAuthCommandsTestCase.ctx, cmd, null);
            Assert.assertTrue(candidates.toString(), candidates.containsAll(res));
        }
        {
            String cmd = "security enable-http-auth-http-server --security-domain=foo " + "--mechanism=BASIC ";
            List<String> candidates = new ArrayList<>();
            SecurityAuthCommandsTestCase.ctx.getDefaultCommandCompleter().complete(SecurityAuthCommandsTestCase.ctx, cmd, cmd.length(), candidates);
            Assert.assertFalse(candidates.toString(), candidates.contains("--referenced-security-domain="));
            candidates = complete(SecurityAuthCommandsTestCase.ctx, cmd, null);
            Assert.assertFalse(candidates.toString(), candidates.contains("--referenced-security-domain="));
        }
        {
            String cmd = "security enable-http-auth-http-server --security-domain=foo " + "--referenced-security-domain=";
            List<String> candidates = new ArrayList<>();
            SecurityAuthCommandsTestCase.ctx.getDefaultCommandCompleter().complete(SecurityAuthCommandsTestCase.ctx, cmd, cmd.length(), candidates);
            List<String> res = Arrays.asList("ApplicationDomain");
            Assert.assertTrue(candidates.toString(), candidates.containsAll(res));
            candidates = complete(SecurityAuthCommandsTestCase.ctx, cmd, null);
            Assert.assertTrue(candidates.toString(), candidates.containsAll(res));
        }
        {
            String cmd = "security enable-http-auth-http-server --security-domain=foo " + "--referenced-security-domain=ApplicationDomain ";
            List<String> candidates = new ArrayList<>();
            SecurityAuthCommandsTestCase.ctx.getDefaultCommandCompleter().complete(SecurityAuthCommandsTestCase.ctx, cmd, cmd.length(), candidates);
            Assert.assertFalse(candidates.toString(), candidates.contains("--mechanism="));
            candidates = complete(SecurityAuthCommandsTestCase.ctx, cmd, null);
            Assert.assertFalse(candidates.toString(), candidates.contains("--mechanism="));
        }
    }

    @Test
    public void testHTTP() throws Exception {
        // Enable and add mechanisms.
        SecurityAuthCommandsTestCase.ctx.handle(((((((((("security enable-http-auth-http-server --no-reload --mechanism=BASIC" + (" --user-properties-file=application-users.properties --group-properties-file=application-roles.properties" + " --relative-to=jboss.server.config.dir --new-security-domain-name=")) + (SecurityAuthCommandsTestCase.TEST_DOMAIN)) + " --new-auth-factory-name=") + (SecurityAuthCommandsTestCase.TEST_HTTP_FACTORY)) + " --new-realm-name=") + (SecurityAuthCommandsTestCase.TEST_USERS_REALM)) + " --exposed-realm=ApplicationRealm") + " --security-domain=") + (SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN)));
        Assert.assertTrue(SecurityAuthCommandsTestCase.getDomainRealms(SecurityAuthCommandsTestCase.TEST_DOMAIN).contains(SecurityAuthCommandsTestCase.TEST_USERS_REALM));
        Assert.assertEquals("ApplicationRealm", SecurityAuthCommandsTestCase.getExposedRealmName(SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN, "BASIC"));
        Assert.assertEquals(SecurityAuthCommandsTestCase.TEST_USERS_REALM, SecurityAuthCommandsTestCase.getMechanismRealmMapper(SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN, "BASIC"));
        Assert.assertTrue(SecurityAuthCommandsTestCase.getNames(HTTP_AUTHENTICATION_FACTORY).contains(SecurityAuthCommandsTestCase.TEST_HTTP_FACTORY));
        Assert.assertTrue(SecurityAuthCommandsTestCase.getNames(SECURITY_DOMAIN).contains(SecurityAuthCommandsTestCase.TEST_DOMAIN));
        Assert.assertEquals(Arrays.asList("BASIC"), SecurityAuthCommandsTestCase.getMechanisms(SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN));
        // Add DIGEST.
        SecurityAuthCommandsTestCase.ctx.handle(((((("security enable-http-auth-http-server --no-reload --mechanism=DIGEST" + " --properties-realm-name=") + (SecurityAuthCommandsTestCase.TEST_USERS_REALM)) + " --exposed-realm=ApplicationRealm") + " --security-domain=") + (SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN)));
        Assert.assertEquals("ApplicationRealm", SecurityAuthCommandsTestCase.getExposedRealmName(SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN, "BASIC"));
        Assert.assertEquals(SecurityAuthCommandsTestCase.TEST_USERS_REALM, SecurityAuthCommandsTestCase.getMechanismRealmMapper(SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN, "BASIC"));
        Assert.assertTrue(((SecurityAuthCommandsTestCase.getDomainRealms(SecurityAuthCommandsTestCase.TEST_DOMAIN).size()) == 1));
        Assert.assertTrue(SecurityAuthCommandsTestCase.getDomainRealms(SecurityAuthCommandsTestCase.TEST_DOMAIN).contains(SecurityAuthCommandsTestCase.TEST_USERS_REALM));
        // capture the state.
        List<ModelNode> factories = SecurityAuthCommandsTestCase.getHttpFactories();
        List<ModelNode> domains = SecurityAuthCommandsTestCase.getSecurityDomains();
        List<ModelNode> mappers = SecurityAuthCommandsTestCase.getConstantRealmMappers();
        List<ModelNode> userRealms = SecurityAuthCommandsTestCase.getPropertiesRealm();
        List<String> expected1 = Arrays.asList("BASIC", "DIGEST");
        Assert.assertEquals(expected1, SecurityAuthCommandsTestCase.getMechanisms(SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN));
        Assert.assertTrue(SecurityAuthCommandsTestCase.getNames(PROPERTIES_REALM).contains(SecurityAuthCommandsTestCase.TEST_USERS_REALM));
        // Disable digest mechanism
        SecurityAuthCommandsTestCase.ctx.handle((("security disable-http-auth-http-server --no-reload --mechanism=DIGEST" + " --security-domain=") + (SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN)));
        Assert.assertTrue(((SecurityAuthCommandsTestCase.getDomainRealms(SecurityAuthCommandsTestCase.TEST_DOMAIN).size()) == 1));
        Assert.assertTrue(SecurityAuthCommandsTestCase.getDomainRealms(SecurityAuthCommandsTestCase.TEST_DOMAIN).contains(SecurityAuthCommandsTestCase.TEST_USERS_REALM));
        Assert.assertEquals("ApplicationRealm", SecurityAuthCommandsTestCase.getExposedRealmName(SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN, "BASIC"));
        Assert.assertEquals(SecurityAuthCommandsTestCase.TEST_USERS_REALM, SecurityAuthCommandsTestCase.getMechanismRealmMapper(SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN, "BASIC"));
        Assert.assertEquals(domains, SecurityAuthCommandsTestCase.getSecurityDomains());
        // still secured
        Assert.assertEquals(SecurityAuthCommandsTestCase.TEST_HTTP_FACTORY, SecurityAuthCommandsTestCase.getSecurityDomainAuthFactory(SecurityAuthCommandsTestCase.ctx, SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN));
        Assert.assertEquals(Arrays.asList("BASIC"), SecurityAuthCommandsTestCase.getMechanisms(SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN));
        {
            // Disable the last mechanism is forbidden
            boolean failed = false;
            try {
                SecurityAuthCommandsTestCase.ctx.handle((("security disable-http-auth-http-server --no-reload --mechanism=BASIC" + " --security-domain=") + (SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN)));
            } catch (Exception ex) {
                // XXX OK.
                failed = true;
            }
            if (!failed) {
                throw new Exception("Disabling the last mechanism should have failed");
            }
        }
        // Re-enable the mechanism
        SecurityAuthCommandsTestCase.ctx.handle(((((("security enable-http-auth-http-server --no-reload --mechanism=DIGEST" + " --properties-realm-name=") + (SecurityAuthCommandsTestCase.TEST_USERS_REALM)) + " --exposed-realm=ApplicationRealm") + " --security-domain=") + (SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN)));
        Assert.assertEquals("ApplicationRealm", SecurityAuthCommandsTestCase.getExposedRealmName(SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN, "BASIC"));
        Assert.assertEquals(SecurityAuthCommandsTestCase.TEST_USERS_REALM, SecurityAuthCommandsTestCase.getMechanismRealmMapper(SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN, "BASIC"));
        Assert.assertEquals("ApplicationRealm", SecurityAuthCommandsTestCase.getExposedRealmName(SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN, "DIGEST"));
        Assert.assertEquals(SecurityAuthCommandsTestCase.TEST_USERS_REALM, SecurityAuthCommandsTestCase.getMechanismRealmMapper(SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN, "DIGEST"));
        Assert.assertEquals(expected1, SecurityAuthCommandsTestCase.getMechanisms(SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN));
        Assert.assertEquals(factories, SecurityAuthCommandsTestCase.getHttpFactories());
        Assert.assertEquals(domains, SecurityAuthCommandsTestCase.getSecurityDomains());
        Assert.assertEquals(mappers, SecurityAuthCommandsTestCase.getConstantRealmMappers());
        Assert.assertEquals(userRealms, SecurityAuthCommandsTestCase.getPropertiesRealm());
        Assert.assertTrue(((SecurityAuthCommandsTestCase.getDomainRealms(SecurityAuthCommandsTestCase.TEST_DOMAIN).size()) == 1));
        Assert.assertTrue(SecurityAuthCommandsTestCase.getDomainRealms(SecurityAuthCommandsTestCase.TEST_DOMAIN).contains(SecurityAuthCommandsTestCase.TEST_USERS_REALM));
    }

    @Test
    public void testDisableAuth() throws Exception {
        boolean failed = false;
        try {
            SecurityAuthCommandsTestCase.ctx.handle((("security disable-http-auth-http-server --no-reload" + " --security-domain=") + (SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN)));
        } catch (Exception ex) {
            // XXX OK.
            failed = true;
        }
        if (!failed) {
            throw new Exception("Should have fail");
        }
    }

    @Test
    public void testHTTPCertificate() throws Exception {
        SecurityAuthCommandsTestCase.ctx.handle((("/subsystem=elytron/key-store=" + (SecurityAuthCommandsTestCase.TEST_KS)) + ":add(type=JKS, credential-reference={clear-text=pass})"));
        SecurityAuthCommandsTestCase.ctx.handle((((((((((("security enable-http-auth-http-server --no-reload --mechanism=CLIENT_CERT" + " --key-store-name=") + (SecurityAuthCommandsTestCase.TEST_KS)) + " --new-security-domain-name=") + (SecurityAuthCommandsTestCase.TEST_DOMAIN)) + " --new-auth-factory-name=") + (SecurityAuthCommandsTestCase.TEST_HTTP_FACTORY)) + " --new-realm-name=") + (SecurityAuthCommandsTestCase.TEST_KS_REALM)) + " --security-domain=") + (SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN)));
        Assert.assertEquals(SecurityAuthCommandsTestCase.TEST_KS_REALM, SecurityAuthCommandsTestCase.getMechanismRealmMapper(SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN, "CLIENT_CERT"));
        Assert.assertTrue(SecurityAuthCommandsTestCase.getDomainRealms(SecurityAuthCommandsTestCase.TEST_DOMAIN).contains(SecurityAuthCommandsTestCase.TEST_KS_REALM));
        // capture the state.
        List<ModelNode> factories = SecurityAuthCommandsTestCase.getHttpFactories();
        List<ModelNode> domains = SecurityAuthCommandsTestCase.getSecurityDomains();
        List<ModelNode> mappers = SecurityAuthCommandsTestCase.getConstantRealmMappers();
        List<ModelNode> ksRealms = SecurityAuthCommandsTestCase.getKSRealms();
        // Re-enable simply by re-using same key-store-realm, no changes expected.
        SecurityAuthCommandsTestCase.ctx.handle((((("security enable-http-auth-http-server --no-reload --mechanism=CLIENT_CERT" + " --key-store-realm-name=") + (SecurityAuthCommandsTestCase.TEST_KS_REALM)) + " --security-domain=") + (SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN)));
        Assert.assertEquals(SecurityAuthCommandsTestCase.TEST_KS_REALM, SecurityAuthCommandsTestCase.getMechanismRealmMapper(SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN, "CLIENT_CERT"));
        Assert.assertTrue(SecurityAuthCommandsTestCase.getDomainRealms(SecurityAuthCommandsTestCase.TEST_DOMAIN).contains(SecurityAuthCommandsTestCase.TEST_KS_REALM));
        Assert.assertEquals(factories, SecurityAuthCommandsTestCase.getHttpFactories());
        Assert.assertEquals(domains, SecurityAuthCommandsTestCase.getSecurityDomains());
        Assert.assertEquals(mappers, SecurityAuthCommandsTestCase.getConstantRealmMappers());
        Assert.assertEquals(ksRealms, SecurityAuthCommandsTestCase.getKSRealms());
        SecurityAuthCommandsTestCase.ctx.handle(("security disable-http-auth-http-server --no-reload --security-domain=" + (SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN)));
        // Re-enable simply by re-using same key-store-realm with roles, no changes expected other than roles.
        SecurityAuthCommandsTestCase.ctx.handle(((((("security enable-http-auth-http-server --no-reload --mechanism=CLIENT_CERT" + " --key-store-realm-name=") + (SecurityAuthCommandsTestCase.TEST_KS_REALM)) + " --roles=FOO,BAR") + " --security-domain=") + (SecurityAuthCommandsTestCase.TEST_UNDERTOW_DOMAIN)));
        Assert.assertEquals(factories, SecurityAuthCommandsTestCase.getHttpFactories());
        Assert.assertEquals(mappers, SecurityAuthCommandsTestCase.getConstantRealmMappers());
        Assert.assertEquals(ksRealms, SecurityAuthCommandsTestCase.getKSRealms());
        Assert.assertEquals(ROLE_MAPPER_NAME, SecurityAuthCommandsTestCase.getRoleMapper(SecurityAuthCommandsTestCase.TEST_KS_REALM, SecurityAuthCommandsTestCase.TEST_DOMAIN));
        List<String> names = SecurityAuthCommandsTestCase.getNames(CONSTANT_ROLE_MAPPER);
        Assert.assertTrue(names.toString(), names.contains(ROLE_MAPPER_NAME));
        ModelNode roleMapper = SecurityAuthCommandsTestCase.getConstantRoleMapper(ROLE_MAPPER_NAME);
        List<ModelNode> lst = roleMapper.get(ROLES).asList();
        Assert.assertTrue(((lst.size()) == 2));
        for (ModelNode r : lst) {
            if ((!(r.asString().equals("FOO"))) && (!(r.asString().equals("BAR")))) {
                throw new Exception(("Invalid roles in " + lst));
            }
        }
    }
}

