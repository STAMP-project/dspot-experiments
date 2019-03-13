/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2019, Red Hat, Inc., and individual contributors
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


import java.util.concurrent.TimeUnit;
import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Validates eager passivation of a stateful EJB.
 *
 * @author Paul Ferraro
 */
@RunWith(Arquillian.class)
@ServerSetup(EagerPassivationTestCaseSetup.class)
public class EagerPassivationTestCase {
    @ArquillianResource
    private InitialContext ctx;

    @Test
    public void testPassivation() throws Exception {
        PassivationInterceptor.reset();
        try (TestPassivationRemote remote1 = ((TestPassivationRemote) (this.ctx.lookup(("java:module/" + (TestPassivationBean.class.getSimpleName())))))) {
            Assert.assertEquals("Returned remote1 result was not expected", TestPassivationRemote.EXPECTED_RESULT, remote1.returnTrueString());
            remote1.addEntity(1, "Bob");
            remote1.setManagedBeanMessage("bar");
            Assert.assertTrue(remote1.isPersistenceContextSame());
            Assert.assertFalse("@PrePassivate called, check cache configuration and client sleep time", remote1.hasBeenPassivated());
            Assert.assertFalse("@PostActivate called, check cache configuration and client sleep time", remote1.hasBeenActivated());
            Assert.assertTrue(remote1.isPersistenceContextSame());
            Assert.assertEquals("Super", remote1.getSuperEmployee().getName());
            Assert.assertEquals("bar", remote1.getManagedBeanMessage());
            // SFSB should passivate after a second
            TimeUnit.SECONDS.sleep(5);
            Assert.assertTrue(("invalid: " + (PassivationInterceptor.getPrePassivateTarget())), ((PassivationInterceptor.getPrePassivateTarget()) instanceof TestPassivationBean));
        } finally {
            PassivationInterceptor.reset();
        }
    }
}

