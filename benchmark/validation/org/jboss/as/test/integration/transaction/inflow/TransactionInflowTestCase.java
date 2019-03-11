/**
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.as.test.integration.transaction.inflow;


import javax.naming.NamingException;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.integration.transactions.TransactionCheckerSingletonRemote;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Testcase running jca inflow transaction from deployed RAR.
 * Two mock XA resources are enlisted inside of MDB to proceed 2PC.
 *
 * @author Ondrej Chaloupka <ochaloup@redhat.com>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class TransactionInflowTestCase {
    private static final String EJB_MODULE_NAME = "inflow-ejb-";

    private static final String COMMIT = TransactionInflowResourceAdapter.ACTION_COMMIT;

    private static final String ROLLBACK = TransactionInflowResourceAdapter.ACTION_ROLLBACK;

    @ArquillianResource
    public Deployer deployer;

    @Test
    public void inflowTransactionCommit() throws NamingException {
        deployer.deploy(((TransactionInflowTestCase.EJB_MODULE_NAME) + (TransactionInflowTestCase.COMMIT)));
        TransactionCheckerSingletonRemote checker = getSingletonChecker(((TransactionInflowTestCase.EJB_MODULE_NAME) + (TransactionInflowTestCase.COMMIT)));
        try {
            Assert.assertEquals("Expecting one message was passed from RAR to MDB", 1, checker.getMessages().size());
            Assert.assertEquals("Expecting message with the content was passed from RAR to MDB", TransactionInflowResourceAdapter.MSG, checker.getMessages().iterator().next());
            Assert.assertEquals("Two XAResources were enlisted thus expected to be prepared", 2, checker.getPrepared());
            Assert.assertEquals("Two XAResources are expected to be committed", 2, checker.getCommitted());
            Assert.assertEquals("Two XAResources were were committed thus not rolled-back", 0, checker.getRolledback());
        } finally {
            checker.resetAll();
        }
    }

    @Test
    public void inflowTransactionRollback() throws NamingException {
        deployer.deploy(((TransactionInflowTestCase.EJB_MODULE_NAME) + (TransactionInflowTestCase.ROLLBACK)));
        TransactionCheckerSingletonRemote checker = getSingletonChecker(((TransactionInflowTestCase.EJB_MODULE_NAME) + (TransactionInflowTestCase.ROLLBACK)));
        try {
            Assert.assertEquals("Expecting one message was passed from RAR to MDB", 1, checker.getMessages().size());
            Assert.assertEquals("Expecting message with the content was passed from RAR to MDB", TransactionInflowResourceAdapter.MSG, checker.getMessages().iterator().next());
            Assert.assertEquals("Two XAResources were enlisted thus expected to be prepared", 2, checker.getPrepared());
            Assert.assertEquals("Two XAResources are expected to be rolled-back", 2, checker.getRolledback());
            Assert.assertEquals("Two XAResources were were rolled-bck thus not committed", 0, checker.getCommitted());
        } finally {
            checker.resetAll();
        }
    }
}

