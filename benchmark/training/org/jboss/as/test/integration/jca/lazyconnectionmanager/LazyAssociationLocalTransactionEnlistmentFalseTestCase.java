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
 * Test cases for deploying a lazy association resource adapter archive using LocalTransaction with enlistment=false
 *
 * @author <a href="mailto:jesper.pedersen@ironjacamar.org">Jesper Pedersen</a>
 * @author <a href="mailto:msimka@redhat.com">Martin Simka</a>
 */
@RunWith(Arquillian.class)
public class LazyAssociationLocalTransactionEnlistmentFalseTestCase extends LazyAssociationAbstractTestCase {
    private static Logger logger = Logger.getLogger(LazyAssociationLocalTransactionEnlistmentFalseTestCase.class);

    @Resource(mappedName = "java:/eis/Lazy")
    private LazyConnectionFactory lcf;

    @Resource(mappedName = "java:jboss/UserTransaction")
    private UserTransaction userTransaction;

    @Test
    public void verifyEagerlyEnlisted() throws Throwable {
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
            Assert.assertTrue(lc.isEnlisted());
        } catch (Throwable t) {
            LazyAssociationLocalTransactionEnlistmentFalseTestCase.logger.error(t.getMessage(), t);
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
}

