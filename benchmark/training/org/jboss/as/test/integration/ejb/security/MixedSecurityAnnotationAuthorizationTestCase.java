package org.jboss.as.test.integration.ejb.security;


import Constants.CLASSIC;
import Constants.LOGIN_MODULE;
import Constants.MODULE_OPTIONS;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.Callable;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.security.Constants;
import org.jboss.as.test.integration.ejb.security.authorization.DenyAllOverrideBean;
import org.jboss.as.test.integration.ejb.security.authorization.PermitAllOverrideBean;
import org.jboss.as.test.integration.ejb.security.authorization.RolesAllowedOverrideBean;
import org.jboss.dmr.ModelNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.security.auth.server.SecurityDomain;
import org.wildfly.test.security.common.elytron.EjbElytronDomainSetup;
import org.wildfly.test.security.common.elytron.ElytronDomainSetup;
import org.wildfly.test.security.common.elytron.ServletElytronDomainSetup;


/**
 * Test case to test the general authorization requirements for annotated beans. The server setup has both
 * an application-security-domain backed by Elytron security domain and legacy security domain with the same name.
 *
 * @author <a href="mailto:mjurc@redhat.com">Michal Jurc</a> (c) 2017 Red Hat, Inc.
 */
@RunWith(Arquillian.class)
@ServerSetup({ MixedSecurityAnnotationAuthorizationTestCase.OverridenEjbSecurityDomainSetup.class, MixedSecurityAnnotationAuthorizationTestCase.OverridingElytronDomainSetup.class, MixedSecurityAnnotationAuthorizationTestCase.OverridingEjbElytronDomainSetup.class, MixedSecurityAnnotationAuthorizationTestCase.OverridingServletElytronDomainSetup.class })
public class MixedSecurityAnnotationAuthorizationTestCase {
    @EJB(mappedName = "java:global/ejb3security/RolesAllowedOverrideBean")
    private RolesAllowedOverrideBean rolesAllowedOverridenBean;

    /* Test overrides within a bean annotated @RolesAllowed at bean level. */
    @Test
    public void testRolesAllowedOverriden_NoUser() throws Exception {
        Assert.assertNotNull("An Elytron security domain should be associated with test EJB deployment.", SecurityDomain.getCurrent());
        try {
            rolesAllowedOverridenBean.defaultEcho("1");
            Assert.fail("Expected EJBAccessException not thrown");
        } catch (EJBAccessException ignored) {
        }
        try {
            rolesAllowedOverridenBean.denyAllEcho("2");
            Assert.fail("Expected EJBAccessException not thrown");
        } catch (EJBAccessException ignored) {
        }
        String response = rolesAllowedOverridenBean.permitAllEcho("3");
        Assert.assertEquals("3", response);
        try {
            rolesAllowedOverridenBean.role2Echo("4");
            Assert.fail("Expected EJBAccessException not thrown");
        } catch (EJBAccessException ignored) {
        }
    }

    @Test
    public void testRolesAllowedOverriden_User1() throws Exception {
        Assert.assertNotNull("An Elytron security domain should be associated with test EJB deployment.", SecurityDomain.getCurrent());
        final Callable<Void> callable = () -> {
            String response = rolesAllowedOverridenBean.defaultEcho("1");
            Assert.assertEquals("1", response);
            try {
                rolesAllowedOverridenBean.denyAllEcho("2");
                Assert.fail("Expected EJBAccessException not thrown");
            } catch (EJBAccessException ignored) {
            }
            response = rolesAllowedOverridenBean.permitAllEcho("3");
            Assert.assertEquals("3", response);
            try {
                rolesAllowedOverridenBean.role2Echo("4");
                Assert.fail("Expected EJBAccessException not thrown");
            } catch (EJBAccessException ignored) {
            }
            return null;
        };
        MixedSecurityAnnotationAuthorizationTestCase.runAsElytronIdentity("user1", "elytronPassword1", callable);
    }

    @Test
    public void testRolesAllowedOverridenInBaseClass_Admin() throws Exception {
        Assert.assertNotNull("An Elytron security domain should be associated with test EJB deployment.", SecurityDomain.getCurrent());
        final Callable<Void> callable = () -> {
            try {
                rolesAllowedOverridenBean.aMethod("aMethod");
                Assert.fail("Expected EJBAccessException not thrown");
            } catch (EJBAccessException ignored) {
            }
            String response = rolesAllowedOverridenBean.bMethod("bMethod");
            Assert.assertEquals("bMethod", response);
            return null;
        };
        MixedSecurityAnnotationAuthorizationTestCase.runAsElytronIdentity("admin", "elytronAdmin", callable);
    }

    @Test
    public void testRolesAllowedOverridenInBaseClass_HR() throws Exception {
        Assert.assertNotNull("An Elytron security domain should be associated with test EJB deployment.", SecurityDomain.getCurrent());
        final Callable<Void> callable = () -> {
            String response = rolesAllowedOverridenBean.aMethod("aMethod");
            Assert.assertEquals("aMethod", response);
            try {
                rolesAllowedOverridenBean.bMethod("bMethod");
                Assert.fail("Expected EJBAccessException not thrown");
            } catch (EJBAccessException ignored) {
            }
            return null;
        };
        MixedSecurityAnnotationAuthorizationTestCase.runAsElytronIdentity("hr", "elytronHr", callable);
    }

    @Test
    public void testRolesAllowedOverriden_User2() throws Exception {
        Assert.assertNotNull("An Elytron security domain should be associated with test EJB deployment.", SecurityDomain.getCurrent());
        final Callable<Void> callable = () -> {
            try {
                rolesAllowedOverridenBean.defaultEcho("1");
                Assert.fail("Expected EJBAccessException not thrown");
            } catch (EJBAccessException ignored) {
            }
            try {
                rolesAllowedOverridenBean.denyAllEcho("2");
                Assert.fail("Expected EJBAccessException not thrown");
            } catch (EJBAccessException ignored) {
            }
            String response = rolesAllowedOverridenBean.permitAllEcho("3");
            Assert.assertEquals("3", response);
            response = rolesAllowedOverridenBean.role2Echo("4");
            Assert.assertEquals("4", response);
            return null;
        };
        MixedSecurityAnnotationAuthorizationTestCase.runAsElytronIdentity("user2", "elytronPassword2", callable);
    }

    /* Test overrides of bean annotated at bean level with @PermitAll */
    @EJB(mappedName = "java:global/ejb3security/PermitAllOverrideBean")
    private PermitAllOverrideBean permitAllOverrideBean;

    @Test
    public void testPermitAllOverride_NoUser() throws Exception {
        Assert.assertNotNull("An Elytron security domain should be associated with test EJB deployment.", SecurityDomain.getCurrent());
        String response = permitAllOverrideBean.defaultEcho("1");
        Assert.assertEquals("1", response);
        try {
            permitAllOverrideBean.denyAllEcho("2");
            Assert.fail("Expected EJBAccessException not thrown");
        } catch (EJBAccessException ignored) {
        }
        try {
            permitAllOverrideBean.role1Echo("3");
            Assert.fail("Expected EJBAccessException not thrown");
        } catch (EJBAccessException ignored) {
        }
    }

    @Test
    public void testPermitAllOverride_User1() throws Exception {
        Assert.assertNotNull("An Elytron security domain should be associated with test EJB deployment.", SecurityDomain.getCurrent());
        final Callable<Void> callable = () -> {
            String response = permitAllOverrideBean.defaultEcho("1");
            Assert.assertEquals("1", response);
            try {
                permitAllOverrideBean.denyAllEcho("2");
                Assert.fail("Expected EJBAccessException not thrown");
            } catch (EJBAccessException ignored) {
            }
            response = permitAllOverrideBean.role1Echo("3");
            Assert.assertEquals("3", response);
            return null;
        };
        MixedSecurityAnnotationAuthorizationTestCase.runAsElytronIdentity("user1", "elytronPassword1", callable);
    }

    /* Test overrides of ben annotated at bean level with @DenyAll */
    @EJB(mappedName = "java:global/ejb3security/DenyAllOverrideBean")
    private DenyAllOverrideBean denyAllOverrideBean;

    @Test
    public void testDenyAllOverride_NoUser() throws Exception {
        Assert.assertNotNull("An Elytron security domain should be associated with test EJB deployment.", SecurityDomain.getCurrent());
        try {
            denyAllOverrideBean.defaultEcho("1");
            Assert.fail("Expected EJBAccessException not thrown");
        } catch (EJBAccessException ignored) {
        }
        String response = denyAllOverrideBean.permitAllEcho("2");
        Assert.assertEquals("2", response);
        try {
            denyAllOverrideBean.role1Echo("3");
            Assert.fail("Expected EJBAccessException not thrown");
        } catch (EJBAccessException ignored) {
        }
    }

    @Test
    public void testDenyAllOverride_User1() throws Exception {
        Assert.assertNotNull("An Elytron security domain should be associated with test EJB deployment.", SecurityDomain.getCurrent());
        final Callable<Void> callable = () -> {
            try {
                denyAllOverrideBean.defaultEcho("1");
                Assert.fail("Expected EJBAccessException not thrown");
            } catch (EJBAccessException ignored) {
            }
            String response = denyAllOverrideBean.permitAllEcho("2");
            Assert.assertEquals("2", response);
            response = denyAllOverrideBean.role1Echo("3");
            Assert.assertEquals("3", response);
            return null;
        };
        MixedSecurityAnnotationAuthorizationTestCase.runAsElytronIdentity("user1", "elytronPassword1", callable);
    }

    /**
     * Tests that a method which accepts an array as a parameter and is marked with @PermitAll is allowed to be invoked by clients.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPermitAllMethodWithArrayParams() throws Exception {
        Assert.assertNotNull("An Elytron security domain should be associated with test EJB deployment.", SecurityDomain.getCurrent());
        final Callable<Void> callable = () -> {
            final String[] messages = new String[]{ "foo", "bar" };
            final String[] echoes = denyAllOverrideBean.permitAllEchoWithArrayParams(messages);
            Assert.assertArrayEquals("Unexpected echoes returned by bean method", messages, echoes);
            return null;
        };
        MixedSecurityAnnotationAuthorizationTestCase.runAsElytronIdentity("user1", "elytronPassword1", callable);
    }

    public static class OverridingElytronDomainSetup extends ElytronDomainSetup {
        public OverridingElytronDomainSetup() {
            super(new File(MixedSecurityAnnotationAuthorizationTestCase.class.getResource("elytronusers.properties").getFile()).getAbsolutePath(), new File(MixedSecurityAnnotationAuthorizationTestCase.class.getResource("roles.properties").getFile()).getAbsolutePath());
        }
    }

    public static class OverridingEjbElytronDomainSetup extends EjbElytronDomainSetup {
        @Override
        protected String getEjbDomainName() {
            return "ejb3-tests";
        }
    }

    public static class OverridingServletElytronDomainSetup extends ServletElytronDomainSetup {
        @Override
        protected String getUndertowDomainName() {
            return "ejb3-tests";
        }
    }

    public static class OverridenEjbSecurityDomainSetup extends EjbSecurityDomainSetup {
        @Override
        public void setup(ManagementClient managementClient, String containerId) throws Exception {
            final ModelNode compositeOp = new ModelNode();
            compositeOp.get(OP).set(COMPOSITE);
            compositeOp.get(OP_ADDR).setEmptyList();
            ModelNode steps = compositeOp.get(STEPS);
            PathAddress securityDomainAddress = PathAddress.pathAddress().append(SUBSYSTEM, "security").append(Constants.SECURITY_DOMAIN, getSecurityDomainName());
            steps.add(createAddOperation(securityDomainAddress));
            PathAddress authAddress = securityDomainAddress.append(Constants.AUTHENTICATION, CLASSIC);
            steps.add(createAddOperation(authAddress));
            ModelNode op = createAddOperation(authAddress.append(LOGIN_MODULE, "Remoting"));
            op.get(Constants.CODE).set("Remoting");
            op.get(Constants.FLAG).set("optional");
            op.get(MODULE_OPTIONS).add("password-stacking", "useFirstPass");
            steps.add(op);
            ModelNode loginModule = createAddOperation(authAddress.append(LOGIN_MODULE, "UsersRoles"));
            loginModule.get(Constants.CODE).set("UsersRoles");
            loginModule.get(Constants.FLAG).set("required");
            loginModule.get(MODULE_OPTIONS).add("password-stacking", "useFirstPass").add("rolesProperties", getGroupsFile()).add("usersProperties", getUsersFile());
            loginModule.get(OPERATION_HEADERS).get(ALLOW_RESOURCE_SERVICE_RESTART).set(true);
            steps.add(loginModule);
            applyUpdates(managementClient.getControllerClient(), Arrays.asList(compositeOp));
        }
    }
}

