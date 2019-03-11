/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat Middleware LLC, and individual contributors
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
package org.wildfly.test.manual.elytron.seccontext;


import org.jboss.as.test.shared.TestSuiteEnvironment;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for authentication and authorization forwarding for 3 servers. Test scenarios use following configuration.
 * Description of used users and beans WhoAmIBean and EntryBean is in superclass.
 *
 * <h3>Given</h3>
 *
 * <pre>
 * Another EJBs used for testing:
 * - FirstServerChainBean - protected (entry, admin and no-server2-identity roles are allowed), stateless,
 * configures identity propagation and calls a remote EntryBean
 *
 * Deployments used for testing:
 * - first-server-ejb.jar (FirstServerChainBean)
 * - entry-ejb-server-chain.jar (EntryBean)
 * - whoami-server-chain.jar (WhoAmIBean)
 *
 * Servers started and configured for context propagation scenarios:
 * - seccontext-server1 (standalone-ha.xml)
 *   * first-server-ejb.jar
 * - seccontext-server2 (standalone.xml)
 *   * entry-ejb-server-chain.jar
 * - seccontext-server3
 *   * whoami-server-chain.jar
 * </pre>
 *
 * @author olukas
 */
public class ServerChainSecurityContextPropagationTestCase extends AbstractSecurityContextPropagationTestBase {
    public static final String FIRST_SERVER_CHAIN_EJB = "first-server-chain";

    public static final String JAR_ENTRY_EJB_SERVER_CHAIN = (SeccontextUtil.JAR_ENTRY_EJB) + "-server-chain";

    public static final String WAR_WHOAMI_SERVER_CHAIN = (SeccontextUtil.WAR_WHOAMI) + "-server-chain";

    private static final AbstractSecurityContextPropagationTestBase.ServerHolder server3 = new AbstractSecurityContextPropagationTestBase.ServerHolder(SeccontextUtil.SERVER3, TestSuiteEnvironment.getServerAddressNode1(), 250);

    /**
     * Test forwarding authentication (credential forwarding) works for EJB calls after another authentication forwarding.
     *
     * <pre>
     * When: EJB client calls FirstServerChainBean as admin user and Elytron AuthenticationContext API is used to
     *       authentication forwarding to EntryBean call and Elytron AuthenticationContext API is used to
     *       authentication forwarding to WhoAmIBean call.
     * Then: credentials are reused for EntryBean as well as WhoAmIBean call and it correctly returns "admin" username for both
     *       beans.
     * </pre>
     */
    @Test
    public void testForwardedAuthenticationPropagationChain() throws Exception {
        String[] tripleWhoAmI = SeccontextUtil.switchIdentity("admin", "admin", getTripleWhoAmICallable(ReAuthnType.FORWARDED_AUTHENTICATION, null, null, ReAuthnType.FORWARDED_AUTHENTICATION, null, null), ReAuthnType.AC_AUTHENTICATION);
        Assert.assertNotNull("The firstServerChainBean.tripleWhoAmI() should return not-null instance", tripleWhoAmI);
        Assert.assertArrayEquals("Unexpected principal names returned from tripleWhoAmI", new String[]{ "admin", "admin", "admin" }, tripleWhoAmI);
    }

    /**
     * Test forwarding authentication (credential forwarding) for EJB calls after another authentication forwarding is not
     * possible when given identity does not exist in intermediate server.
     *
     * <pre>
     * When: EJB client calls FirstServerChainBean as no-server2-identity user and Elytron AuthenticationContext API is used to
     *       authentication forwarding to EntryBean call and Elytron AuthenticationContext API is used to
     *       authentication forwarding to WhoAmIBean call.
     * Then: authentication for EntryBean should fail because no-server2-identity does not exist on seccontext-server2.
     * </pre>
     */
    @Test
    public void testForwardedAuthenticationIdentityDoesNotExistOnIntermediateServer() throws Exception {
        String[] tripleWhoAmI = SeccontextUtil.switchIdentity("no-server2-identity", "no-server2-identity", getTripleWhoAmICallable(ReAuthnType.FORWARDED_AUTHENTICATION, null, null, ReAuthnType.FORWARDED_AUTHENTICATION, null, null), ReAuthnType.AC_AUTHENTICATION);
        Assert.assertNotNull("The firstServerChainBean.tripleWhoAmI() should return not-null instance", tripleWhoAmI);
        Assert.assertEquals("Unexpected principal names returned from first call in tripleWhoAmI", "no-server2-identity", tripleWhoAmI[0]);
        Assert.assertThat("Access should be denied for second call in tripleWhoAmI when identity does not exist on second server", tripleWhoAmI[1], AbstractSecurityContextPropagationTestBase.isEjbAuthenticationError());
        Assert.assertNull("Third call in tripleWhoAmI should not exist", tripleWhoAmI[2]);
    }

    /**
     * Test forwarding authorization (credential less forwarding) works for EJB calls after another authorization forwarding.
     * {@link RunAsPrincipalPermission} is assigned to caller server and another-server identity.
     *
     * <pre>
     * When: EJB client calls FirstServerChainBean as admin user and Elytron AuthenticationContext API is used to
     *       authorization forwarding to EntryBean call with "server" user used as caller server identity and
     *       Elytron AuthenticationContext API is used to
     *       authorization forwarding to WhoAmIBean call with "another-server" user used as caller server identity.
     * Then: EntryBean call is possible and returns "admin" username
     *       and WhoAmIBean call is possible and returns "admin" username.
     * </pre>
     */
    @Test
    public void testForwardedAuthorizationPropagationChain() throws Exception {
        String[] tripleWhoAmI = SeccontextUtil.switchIdentity("admin", "admin", getTripleWhoAmICallable(ReAuthnType.FORWARDED_AUTHORIZATION, "server", "server", ReAuthnType.FORWARDED_AUTHORIZATION, "another-server", "another-server"), ReAuthnType.AC_AUTHENTICATION);
        Assert.assertNotNull("The firstServerChainBean.tripleWhoAmI() should return not-null instance", tripleWhoAmI);
        Assert.assertArrayEquals("Unexpected principal names returned from tripleWhoAmI", new String[]{ "admin", "admin", "admin" }, tripleWhoAmI);
    }

    /**
     * Test forwarding authorization (credential less forwarding) for EJB calls after another authorization forwarding is not
     * possible when given authorization identity does not exist in intermediate server. {@link RunAsPrincipalPermission} is
     * assigned to caller server and another-server identity.
     *
     * <pre>
     * When: EJB client calls FirstServerChainBean as no-server2-identity user and Elytron AuthenticationContext API is used to
     *       authorization forwarding to EntryBean call with "server" user used as caller server identity
     *       and Elytron AuthenticationContext API is used to
     *       authorization forwarding to WhoAmIBean call with "another-server" user used as caller server identity.
     * Then: authorization for EntryBean should fail because no-server2-identity does not exist on seccontext-server2.
     * </pre>
     */
    @Test
    public void testForwardedAuthorizationIdentityDoesNotExistOnIntermediateServer() throws Exception {
        String[] tripleWhoAmI = SeccontextUtil.switchIdentity("no-server2-identity", "no-server2-identity", getTripleWhoAmICallable(ReAuthnType.FORWARDED_AUTHORIZATION, "server", "server", ReAuthnType.FORWARDED_AUTHORIZATION, "another-server", "another-server"), ReAuthnType.AC_AUTHENTICATION);
        Assert.assertNotNull("The firstServerChainBean.tripleWhoAmI() should return not-null instance", tripleWhoAmI);
        Assert.assertEquals("Unexpected principal names returned from first call in tripleWhoAmI", "no-server2-identity", tripleWhoAmI[0]);
        Assert.assertThat("Access should be denied for second call in tripleWhoAmI when identity does not exist on second server", tripleWhoAmI[1], AbstractSecurityContextPropagationTestBase.isEjbAuthenticationError());
        Assert.assertNull("Third call in tripleWhoAmI should not exist", tripleWhoAmI[2]);
    }

    /**
     * Test forwarding authentication (credential forwarding) for EJB calls is not possible after authorization forwarding.
     * {@link RunAsPrincipalPermission} is assigned to caller server and another-server identity.
     *
     * <pre>
     * When: EJB client calls FirstServerChainBean as admin user and Elytron AuthenticationContext API is used to
     *       authorization forwarding to EntryBean call with "server" user used as caller server identity and
     *       Elytron AuthenticationContext API is used to authentication forwarding to WhoAmIBean.
     * Then: WhoAmIBean call is fails because credentails should not be available on seccontext-server2 after authorization
     *       forwarding.
     * </pre>
     */
    @Test
    public void testForwardingAuthenticationIsNotPossibleAfterForwardingAuthorization() throws Exception {
        String[] tripleWhoAmI = SeccontextUtil.switchIdentity("admin", "admin", getTripleWhoAmICallable(ReAuthnType.FORWARDED_AUTHORIZATION, "server", "server", ReAuthnType.FORWARDED_AUTHENTICATION, null, null), ReAuthnType.AC_AUTHENTICATION);
        Assert.assertNotNull("The firstServerChainBean.tripleWhoAmI() should return not-null instance", tripleWhoAmI);
        Assert.assertEquals("Unexpected principal names returned from first call in tripleWhoAmI", "admin", tripleWhoAmI[0]);
        Assert.assertEquals("Unexpected principal names returned from second call in tripleWhoAmI", "admin", tripleWhoAmI[1]);
        Assert.assertThat("Access should be denied for third call in tripleWhoAmI", tripleWhoAmI[2], AbstractSecurityContextPropagationTestBase.isEjbAuthenticationError());
    }
}

