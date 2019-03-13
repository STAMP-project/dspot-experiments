/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.ejb.stateful.passivation;


import javax.ejb.NoSuchEJBException;
import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Paul Ferraro
 */
@RunWith(Arquillian.class)
@ServerSetup(PassivationTestCaseSetup.class)
public class PassivationFailedTestCase {
    @ArquillianResource
    private InitialContext ctx;

    /**
     * Tests passivation of bean that throws exception during serialization.
     */
    @Test
    public void testPassivationFailure() throws Exception {
        PassivationInterceptor.reset();
        // create first bean
        try (TestPassivationRemote remote1 = ((TestPassivationRemote) (ctx.lookup(("java:module/" + (BeanWithSerializationIssue.class.getSimpleName())))))) {
            // make an invocation
            Assert.assertEquals("Returned remote1 result was not expected", TestPassivationRemote.EXPECTED_RESULT, remote1.returnTrueString());
            // create second bean, this should force the first bean to passivate
            try (TestPassivationRemote remote2 = ((TestPassivationRemote) (ctx.lookup(("java:module/" + (BeanWithSerializationIssue.class.getSimpleName())))))) {
                // make an invocation
                Assert.assertEquals("Returned remote2 result was not expected", TestPassivationRemote.EXPECTED_RESULT, remote2.returnTrueString());
                // verify that a bean was prePassivated
                TestPassivationBean target = ((TestPassivationBean) (PassivationInterceptor.getPrePassivateTarget()));
                Assert.assertNotNull(target);
                // verify that bean was not postActivated yet
                Assert.assertTrue(target.hasBeenPassivated());
                // From EJB 4.2.1:
                // The container may destroy a session bean instance if the instance does not meet the requirements for serialization after PrePassivate.
                try {
                    // At least one of these invocations should fail
                    remote1.returnTrueString();
                    Assert.fail("Invocation of pre-passivated EJB should not succeed since passivation failed");
                } catch (NoSuchEJBException e) {
                    // Expected
                }
                // No EJBs should have activated, since they were unable to passivate
                Assert.assertTrue(((PassivationInterceptor.getPostActivateTarget()) == null));
            } catch (NoSuchEJBException e) {
                // Expected
            }
        } catch (NoSuchEJBException e) {
            // Expected
        }
        PassivationInterceptor.reset();
    }
}

