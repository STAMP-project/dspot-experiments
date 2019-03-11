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
import javax.resource.ResourceException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.integration.jca.lazyconnectionmanager.rar.LazyConnection;
import org.jboss.as.test.integration.jca.lazyconnectionmanager.rar.LazyConnectionFactory;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test cases for deploying a lazy association resource adapter archive with sharable=false
 *
 * @author <a href="mailto:jesper.pedersen@ironjacamar.org">Jesper Pedersen</a>
 * @author <a href="mailto:msimka@redhat.com">Martin Simka</a>
 */
@RunWith(Arquillian.class)
public class LazyAssociationSharableFalseTestCase extends LazyAssociationAbstractTestCase {
    private static Logger logger = Logger.getLogger(LazyAssociationSharableFalseTestCase.class);

    @Resource(mappedName = "java:/eis/Lazy")
    private LazyConnectionFactory lcf;

    @Test
    public void verifyDisabledLazyAssociation() {
        Assert.assertNotNull(lcf);
        LazyConnection lc1 = null;
        LazyConnection lc2 = null;
        try {
            lc1 = lcf.getConnection();
            Assert.assertTrue(lc1.isManagedConnectionSet());
            try {
                lc2 = lcf.getConnection();
                Assert.fail("Exception should have been thrown");
            } catch (ResourceException re) {
                // expected
            }
        } catch (Throwable t) {
            LazyAssociationSharableFalseTestCase.logger.error(t.getMessage(), t);
            Assert.fail(("Throwable:" + (t.getLocalizedMessage())));
        } finally {
            if (lc1 != null) {
                lc1.close();
            }
            if (lc2 != null) {
                lc2.close();
            }
        }
    }

    @Test
    public void testDefaultBehaviorWithoutLazyAssociation() {
        Assert.assertNotNull(lcf);
        LazyConnection lc1 = null;
        LazyConnection lc2 = null;
        try {
            lc1 = lcf.getConnection();
            Assert.assertTrue(lc1.isManagedConnectionSet());
            lc1.close();
            lc2 = lcf.getConnection();
            Assert.assertTrue(lc2.isManagedConnectionSet());
            lc2.close();
        } catch (Throwable t) {
            LazyAssociationSharableFalseTestCase.logger.error(t.getMessage(), t);
            Assert.fail(("Throwable:" + (t.getLocalizedMessage())));
        } finally {
            if (lc1 != null) {
                lc1.close();
            }
            if (lc2 != null) {
                lc2.close();
            }
        }
    }
}

