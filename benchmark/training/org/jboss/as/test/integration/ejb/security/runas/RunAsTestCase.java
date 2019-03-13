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
package org.jboss.as.test.integration.ejb.security.runas;


import java.security.Principal;
import java.util.concurrent.Callable;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.as.test.categories.CommonCriteria;
import org.jboss.as.test.integration.ejb.security.EjbSecurityDomainSetup;
import org.jboss.as.test.integration.ejb.security.WhoAmI;
import org.jboss.as.test.shared.integration.ejb.security.Util;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Test case to test the requirements related to the handling of a RunAs identity.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
@RunWith(Arquillian.class)
@ServerSetup({ EjbSecurityDomainSetup.class })
@Category(CommonCriteria.class)
public class RunAsTestCase {
    private static final Logger log = Logger.getLogger(RunAsTestCase.class.getName());

    @ArquillianResource
    InitialContext ctx;

    @EJB(mappedName = "java:global/ejb3security/WhoAmIBean!org.jboss.as.test.integration.ejb.security.WhoAmI")
    private WhoAmI whoAmIBean;

    @EJB(mappedName = "java:global/ejb3security/EntryBean!org.jboss.as.test.integration.ejb.security.runas.EntryBean")
    private EntryBean entryBean;

    @Test
    public void testAuthentication_TwoBeans() throws Exception {
        final Callable<Void> callable = () -> {
            String[] response = entryBean.doubleWhoAmI();
            Assert.assertEquals("user1", response[0]);
            Assert.assertEquals("anonymous", response[1]);// Unless a run-as-principal configuration has been done, you cannot expect a principal

            return null;
        };
        Util.switchIdentity("user1", "password1", callable);
    }

    @Test
    public void testRunAsICIR_TwoBeans() throws Exception {
        Callable<Void> callable = () -> {
            // TODO - Enable once auth checks are working.
            /* try { whoAmIBean.getCallerPrincipal(); fail("Expected call to whoAmIBean to fail"); } catch (Exception expected)
            { }
             */
            boolean[] response;
            response = entryBean.doubleDoIHaveRole("Users");
            Assert.assertTrue(response[0]);
            Assert.assertFalse(response[1]);
            response = entryBean.doubleDoIHaveRole("Role1");
            Assert.assertTrue(response[0]);
            Assert.assertFalse(response[1]);
            response = entryBean.doubleDoIHaveRole("Role2");
            Assert.assertFalse(response[0]);
            Assert.assertTrue(response[1]);
            return null;
        };
        Util.switchIdentity("user1", "password1", callable);
        callable = () -> {
            // Verify the call now passes.
            Principal user = whoAmIBean.getCallerPrincipal();
            Assert.assertNotNull(user);
            boolean[] response;
            response = entryBean.doubleDoIHaveRole("Users");
            Assert.assertTrue(response[0]);
            Assert.assertFalse(response[1]);
            response = entryBean.doubleDoIHaveRole("Role1");
            Assert.assertFalse(response[0]);
            Assert.assertFalse(response[1]);
            response = entryBean.doubleDoIHaveRole("Role2");
            Assert.assertTrue(response[0]);
            Assert.assertTrue(response[1]);
            return null;
        };
        Util.switchIdentity("user2", "password2", callable);
    }

    @Test
    public void testOnlyRole1() {
        try {
            entryBean.callOnlyRole1();
            Assert.fail("Expected EJBAccessException");
        } catch (EJBAccessException e) {
            // good
        }
    }

    /**
     * Migration test from EJB Testsuite (security/TimerRunAs) to AS7 [JBQA-5483].
     */
    @Test
    public void testTimerNoSecurityAssociationPrincipal() throws Exception {
        final Callable<Void> callable = () -> {
            TimerTester test = ((TimerTester) (ctx.lookup(("java:module/" + (TimerTesterBean.class.getSimpleName())))));
            Assert.assertNotNull(test);
            test.startTimer(150);
            Assert.assertTrue(TimerTesterBean.awaitTimerCall());
            Assert.assertEquals("user2", TimerTesterBean.calleeCallerPrincipal.iterator().next().getName());
            return null;
        };
        Util.switchIdentity("user1", "password1", callable);
    }
}

