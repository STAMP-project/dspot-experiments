/**
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.ejb.security;


import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.util.concurrent.Callable;
import javax.ejb.EJB;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.test.categories.CommonCriteria;
import org.jboss.as.test.shared.integration.ejb.security.Util;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.wildfly.security.auth.server.SecurityDomain;


/**
 * Test case to hold the authentication scenarios, these range from calling a servlet which calls a bean to calling a bean which
 * calls another bean to calling a bean which re-authenticated before calling another bean.
 *
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
/* isCallerInRole Scenarios with @RunAs Defined

EJB 3.1 FR 17.2.5.2 isCallerInRole tests the principal that represents the caller of the enterprise bean, not the
principal that corresponds to the run-as security identity for the bean.
 */
// 17.2.5 - Programatic Access to Caller's Security Context
// Include tests for methods not implemented to pick up if later they are implemented.
// 17.2.5.1 - Use of getCallerPrincipal
// 17.6.5 - Security Methods on EJBContext
// 17.2.5.2 - Use of isCallerInRole
// 17.2.5.3 - Declaration of Security Roles Referenced from the Bean's Code
// 17.3.1 - Security Roles
// 17.3.2.1 - Specification of Method Permissions with Metadata Annotation
// 17.3.2.2 - Specification of Method Permissions in the Deployment Descriptor
// 17.3.2.3 - Unspecified Method Permission
// 17.3.3 - Linking Security Role References to Security Roles
// 17.3.4 - Specification on Security Identities in the Deployment Descriptor
// (Include permutations for overrides esp where deployment descriptor removes access)
// 17.3.4.1 - Run-as
// 17.5 EJB Client Responsibilities
// A transactional client can not change principal association within transaction.
// A session bean client must not change the principal association for the duration of the communication.
// If transactional requests within a single transaction arrive from multiple clients all must be associated
// with the same security context.
// 17.6.3 - Security Mechanisms
// 17.6.4 - Passing Principals on EJB Calls
// 17.6.6 - Secure Access to Resource Managers
// 17.6.7 - Principal Mapping
// 17.6.9 - Runtime Security Enforcement
// 17.6.10 - Audit Trail
@RunWith(Arquillian.class)
@ServerSetup({ EjbSecurityDomainSetup.class })
@Category(CommonCriteria.class)
public class AuthenticationTestCase {
    private static final Logger log = Logger.getLogger(AuthenticationTestCase.class.getName());

    /* Authentication Scenarios

    Client -> Bean
    Client -> Bean -> Bean
    Client -> Bean (Re-auth) -> Bean
    Client -> Servlet -> Bean
    Client -> Servlet (Re-auth) -> Bean
    Client -> Servlet -> Bean -> Bean
    Client -> Servlet -> Bean (Re Auth) -> Bean
     */
    @ArquillianResource
    private URL url;

    @EJB(mappedName = "java:global/ejb3security/WhoAmIBean!org.jboss.as.test.integration.ejb.security.WhoAmI")
    private WhoAmI whoAmIBean;

    @EJB(mappedName = "java:global/ejb3security/EntryBean!org.jboss.as.test.integration.ejb.security.Entry")
    private Entry entryBean;

    @Test
    public void testAuthentication() throws Exception {
        final Callable<Void> callable = () -> {
            String response = entryBean.whoAmI();
            Assert.assertEquals("user1", response);
            return null;
        };
        Util.switchIdentity("user1", "password1", callable);
    }

    @Test
    public void testAuthentication_BadPwd() throws Exception {
        Util.switchIdentity("user1", "wrong_password", () -> entryBean.whoAmI(), true);
    }

    @Test
    public void testAuthentication_TwoBeans() throws Exception {
        final Callable<Void> callable = () -> {
            String[] response = entryBean.doubleWhoAmI();
            Assert.assertEquals("user1", response[0]);
            Assert.assertEquals("user1", response[1]);
            return null;
        };
        Util.switchIdentity("user1", "password1", callable);
    }

    @Test
    public void testAuthentication_TwoBeans_ReAuth() throws Exception {
        final Callable<Void> callable = () -> {
            String[] response = entryBean.doubleWhoAmI("user2", "password2");
            Assert.assertEquals("user1", response[0]);
            Assert.assertEquals("user2", response[1]);
            return null;
        };
        Util.switchIdentity("user1", "password1", callable);
    }

    // TODO - Similar test with first bean @RunAs - does it make sense to also manually switch?
    @Test
    public void testAuthentication_TwoBeans_ReAuth_BadPwd() throws Exception {
        Util.switchIdentity("user1", "password1", () -> entryBean.doubleWhoAmI("user2", "wrong_password"), true);
    }

    @Test
    public void testAuthenticatedCall() throws Exception {
        // TODO: this is not spec
        final Callable<Void> callable = () -> {
            try {
                final Principal principal = whoAmIBean.getCallerPrincipal();
                Assert.assertNotNull("EJB 3.1 FR 17.6.5 The container must never return a null from the getCallerPrincipal method.", principal);
                Assert.assertEquals("user1", principal.getName());
            } catch (RuntimeException e) {
                e.printStackTrace();
                Assert.fail((("EJB 3.1 FR 17.6.5 The EJB container must provide the caller?s security context information during the execution of a business method (" + (e.getMessage())) + ")"));
            }
            return null;
        };
        Util.switchIdentitySCF("user1", "password1", callable);
    }

    @Test
    public void testUnauthenticated() throws Exception {
        try {
            final Principal principal = whoAmIBean.getCallerPrincipal();
            Assert.assertNotNull("EJB 3.1 FR 17.6.5 The container must never return a null from the getCallerPrincipal method.", principal);
            // TODO: where is 'anonymous' configured?
            Assert.assertEquals("anonymous", principal.getName());
        } catch (RuntimeException e) {
            e.printStackTrace();
            Assert.fail((("EJB 3.1 FR 17.6.5 The EJB container must provide the caller?s security context information during the execution of a business method (" + (e.getMessage())) + ")"));
        }
    }

    @Test
    public void testAuthentication_ViaServlet() throws Exception {
        final String result = getWhoAmI("?method=whoAmI");
        Assert.assertEquals("user1", result);
    }

    @Test
    public void testAuthentication_ReAuth_ViaServlet() throws Exception {
        final String result = getWhoAmI("?method=whoAmI&username=user2&password=password2");
        Assert.assertEquals("user2", result);
    }

    @Test
    public void testAuthentication_TwoBeans_ViaServlet() throws Exception {
        final String result = getWhoAmI("?method=doubleWhoAmI");
        Assert.assertEquals("user1,user1", result);
    }

    @Test
    public void testAuthentication_TwoBeans_ReAuth_ViaServlet() throws Exception {
        final String result = getWhoAmI("?method=doubleWhoAmI&username=user2&password=password2");
        Assert.assertEquals("user1,user2", result);
    }

    @Test
    public void testAuthentication_TwoBeans_ReAuth__BadPwd_ViaServlet() throws Exception {
        try {
            getWhoAmI("?method=doubleWhoAmI&username=user2&password=bad_password");
            Assert.fail("Expected IOException");
        } catch (IOException e) {
            if ((SecurityDomain.getCurrent()) == null) {
                Assert.assertThat(e.getMessage(), CoreMatchers.containsString("javax.ejb.EJBAccessException"));
            } else {
                Assert.assertThat(e.getMessage(), CoreMatchers.containsString("javax.ejb.EJBException: java.lang.SecurityException: ELY01151"));
            }
        }
    }

    /* isCallerInRole Scenarios */
    @Test
    public void testICIRSingle() throws Exception {
        final Callable<Void> callable = () -> {
            Assert.assertTrue(entryBean.doIHaveRole("Users"));
            Assert.assertTrue(entryBean.doIHaveRole("Role1"));
            Assert.assertFalse(entryBean.doIHaveRole("Role2"));
            return null;
        };
        Util.switchIdentity("user1", "password1", callable);
    }

    @Test
    public void testICIR_TwoBeans() throws Exception {
        final Callable<Void> callable = () -> {
            boolean[] response;
            response = entryBean.doubleDoIHaveRole("Users");
            Assert.assertTrue(response[0]);
            Assert.assertTrue(response[1]);
            response = entryBean.doubleDoIHaveRole("Role1");
            Assert.assertTrue(response[0]);
            Assert.assertTrue(response[1]);
            response = entryBean.doubleDoIHaveRole("Role2");
            Assert.assertFalse(response[0]);
            Assert.assertFalse(response[1]);
            return null;
        };
        Util.switchIdentity("user1", "password1", callable);
    }

    @Test
    public void testICIR_TwoBeans_ReAuth() throws Exception {
        final Callable<Void> callable = () -> {
            boolean[] response;
            response = entryBean.doubleDoIHaveRole("Users", "user2", "password2");
            Assert.assertTrue(response[0]);
            Assert.assertTrue(response[1]);
            response = entryBean.doubleDoIHaveRole("Role1", "user2", "password2");
            Assert.assertTrue(response[0]);
            Assert.assertFalse(response[1]);
            response = entryBean.doubleDoIHaveRole("Role2", "user2", "password2");
            Assert.assertFalse(response[0]);
            Assert.assertTrue(response[1]);
            return null;
        };
        Util.switchIdentity("user1", "password1", callable);
    }

    @Test
    public void testICIR_ViaServlet() throws Exception {
        String result = getWhoAmI("?method=doIHaveRole&role=Users");
        Assert.assertEquals("true", result);
        result = getWhoAmI("?method=doIHaveRole&role=Role1");
        Assert.assertEquals("true", result);
        result = getWhoAmI("?method=doIHaveRole&role=Role2");
        Assert.assertEquals("false", result);
    }

    @Test
    public void testICIR_ReAuth_ViaServlet() throws Exception {
        String result = getWhoAmI("?method=doIHaveRole&role=Users&username=user2&password=password2");
        Assert.assertEquals("true", result);
        result = getWhoAmI("?method=doIHaveRole&role=Role1&username=user2&password=password2");
        Assert.assertEquals("false", result);
        result = getWhoAmI("?method=doIHaveRole&role=Role2&username=user2&password=password2");
        Assert.assertEquals("true", result);
    }

    @Test
    public void testICIR_TwoBeans_ViaServlet() throws Exception {
        String result = getWhoAmI("?method=doubleDoIHaveRole&role=Users");
        Assert.assertEquals("true,true", result);
        result = getWhoAmI("?method=doubleDoIHaveRole&role=Role1");
        Assert.assertEquals("true,true", result);
        result = getWhoAmI("?method=doubleDoIHaveRole&role=Role2");
        Assert.assertEquals("false,false", result);
    }

    @Test
    public void testICIR_TwoBeans_ReAuth_ViaServlet() throws Exception {
        String result = getWhoAmI("?method=doubleDoIHaveRole&role=Users&username=user2&password=password2");
        Assert.assertEquals("true,true", result);
        result = getWhoAmI("?method=doubleDoIHaveRole&role=Role1&username=user2&password=password2");
        Assert.assertEquals("true,false", result);
        result = getWhoAmI("?method=doubleDoIHaveRole&role=Role2&username=user2&password=password2");
        Assert.assertEquals("false,true", result);
    }
}

