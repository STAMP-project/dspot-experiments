/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.integration.jca.lazyconnectionmanager;


import javax.annotation.Resource;
import javax.transaction.UserTransaction;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.integration.jca.lazyconnectionmanager.rar.LazyConnection;
import org.jboss.as.test.integration.jca.lazyconnectionmanager.rar.LazyConnectionFactory;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test cases for deploying a lazy association resource adapter archive using LocalTransaction
 *
 * @author <a href="mailto:jesper.pedersen@ironjacamar.org">Jesper Pedersen</a>
 * @author <a href="mailto:msimka@redhat.com">Martin Simka</a>
 */
@RunWith(Arquillian.class)
public class LazyAssociationLocalTransactionTestCase extends LazyAssociationAbstractTestCase {
    private static Logger logger = Logger.getLogger(LazyAssociationLocalTransactionTestCase.class);

    @Resource(mappedName = "java:/eis/Lazy")
    private LazyConnectionFactory lcf;

    @Resource(mappedName = "java:jboss/UserTransaction")
    private UserTransaction userTransaction;

    @Test
    public void testBasic() throws Throwable {
        Assert.assertNotNull(lcf);
        Assert.assertNotNull(userTransaction);
        boolean status = true;
        userTransaction.begin();
        LazyConnection lc = null;
        try {
            lc = lcf.getConnection();
            Assert.assertTrue(lc.isManagedConnectionSet());
            Assert.assertTrue(lc.closeManagedConnection());
            Assert.assertFalse(lc.isManagedConnectionSet());
            Assert.assertTrue(lc.associate());
            Assert.assertTrue(lc.isManagedConnectionSet());
            Assert.assertFalse(lc.isEnlisted());
            Assert.assertTrue(lc.enlist());
            Assert.assertTrue(lc.isEnlisted());
        } catch (Throwable t) {
            LazyAssociationLocalTransactionTestCase.logger.error(t.getMessage(), t);
            status = false;
            Assert.fail(("Throwable:" + (t.getMessage())));
        } finally {
            if (lc != null) {
                lc.close();
            }
            if (status) {
                userTransaction.commit();
            } else {
                userTransaction.rollback();
            }
        }
    }

    /**
     * Two connections - one managed connection - without enlistment
     *
     * @throws Throwable
     * 		Thrown if case of an error
     */
    @Test
    public void testTwoConnectionsWithoutEnlistment() throws Throwable {
        Assert.assertNotNull(lcf);
        Assert.assertNotNull(userTransaction);
        boolean status = true;
        userTransaction.begin();
        LazyConnection lc1 = null;
        LazyConnection lc2 = null;
        try {
            lc1 = lcf.getConnection();
            Assert.assertTrue(lc1.isManagedConnectionSet());
            LazyAssociationLocalTransactionTestCase.logger.trace("testTwoConnectionsWithoutEnlistment: Before 2nd getConnection");
            lc2 = lcf.getConnection();
            Assert.assertTrue(lc2.isManagedConnectionSet());
            Assert.assertFalse(lc1.isManagedConnectionSet());
            LazyAssociationLocalTransactionTestCase.logger.trace("testTwoConnectionsWithoutEnlistment: Before closeManagedConnection");
            Assert.assertTrue(lc2.closeManagedConnection());
            Assert.assertFalse(lc1.isManagedConnectionSet());
            Assert.assertFalse(lc2.isManagedConnectionSet());
            LazyAssociationLocalTransactionTestCase.logger.trace("testTwoConnectionsWithoutEnlistment: Before associate");
            Assert.assertTrue(lc1.associate());
            Assert.assertTrue(lc1.isManagedConnectionSet());
            Assert.assertFalse(lc2.isManagedConnectionSet());
            LazyAssociationLocalTransactionTestCase.logger.infof("testTwoConnectionsWithoutEnlistment: After associate");
        } catch (Throwable t) {
            LazyAssociationLocalTransactionTestCase.logger.error(t.getMessage(), t);
            status = false;
            Assert.fail(("Throwable:" + (t.getMessage())));
        } finally {
            if (lc1 != null) {
                lc1.close();
            }
            if (lc2 != null) {
                lc2.close();
            }
            if (status) {
                userTransaction.commit();
            } else {
                userTransaction.rollback();
            }
        }
    }

    /**
     * Two connections - one managed connection - with enlistment
     *
     * @throws Throwable
     * 		Thrown if case of an error
     */
    @Test
    public void testTwoConnectionsWithEnlistment() throws Throwable {
        Assert.assertNotNull(lcf);
        Assert.assertNotNull(userTransaction);
        boolean status = true;
        userTransaction.begin();
        LazyConnection lc1 = null;
        LazyConnection lc2 = null;
        try {
            lc1 = lcf.getConnection();
            Assert.assertTrue(lc1.isManagedConnectionSet());
            Assert.assertFalse(lc1.isEnlisted());
            Assert.assertTrue(lc1.enlist());
            Assert.assertTrue(lc1.isEnlisted());
            lc2 = lcf.getConnection();
            Assert.assertTrue(lc2.isManagedConnectionSet());
            Assert.assertFalse(lc1.isManagedConnectionSet());
            Assert.assertTrue(lc2.closeManagedConnection());
            Assert.assertFalse(lc1.isManagedConnectionSet());
            Assert.assertFalse(lc2.isManagedConnectionSet());
            Assert.assertTrue(lc1.associate());
            Assert.assertTrue(lc1.isManagedConnectionSet());
            Assert.assertFalse(lc2.isManagedConnectionSet());
        } catch (Throwable t) {
            LazyAssociationLocalTransactionTestCase.logger.error(t.getMessage(), t);
            status = false;
            Assert.fail(("Throwable:" + (t.getMessage())));
        } finally {
            if (lc1 != null) {
                lc1.close();
            }
            if (lc2 != null) {
                lc2.close();
            }
            if (status) {
                userTransaction.commit();
            } else {
                userTransaction.rollback();
            }
        }
    }
}

