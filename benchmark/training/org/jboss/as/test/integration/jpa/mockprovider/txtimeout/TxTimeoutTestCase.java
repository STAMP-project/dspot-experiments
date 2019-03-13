/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.jpa.mockprovider.txtimeout;


import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Transaction timeout test that ensures that the entity manager is not closed concurrently while application
 * is using EntityManager.
 * AS7-6586
 *
 * @author Scott Marlow
 */
@RunWith(Arquillian.class)
public class TxTimeoutTestCase {
    private static final String ARCHIVE_NAME = "jpa_txTimeoutTestWithMockProvider";

    @ArquillianResource
    private InitialContext iniCtx;

    /**
     * Tests if the entity manager is closed by the application thread.
     * The transaction does not timeout for this test, so the EntityManager.close() will happen in the application
     * thread.
     *
     * @throws Exception
     * 		
     */
    @Test
    @InSequence(1)
    public void test_positiveTxTimeoutTest() throws Exception {
        TestEntityManager.clearState();
        Assert.assertFalse("entity manager state is not reset", TestEntityManager.getClosedByReaperThread());
        SFSB1 sfsb1 = lookup("ejbjar/SFSB1", SFSB1.class);
        sfsb1.createEmployee("Wily", "1 Appletree Lane", 10);
        Assert.assertFalse("entity manager should be closed by application thread but was closed by TX Reaper thread", TestEntityManager.getClosedByReaperThread());
    }
}

