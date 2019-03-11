/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
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
package org.jboss.as.test.clustering.cluster.ejb.remote;


import javax.ejb.EJBException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.clustering.cluster.AbstractClusteringTestCase;
import org.jboss.as.test.clustering.cluster.ejb.remote.bean.Incrementor;
import org.jboss.as.test.clustering.cluster.ejb.remote.bean.InfinispanExceptionThrowingIncrementorBean;
import org.jboss.as.test.clustering.ejb.EJBDirectory;
import org.jboss.as.test.clustering.ejb.RemoteEJBDirectory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test for WFLY-5788.
 *
 * @author Radoslav Husar
 */
@RunWith(Arquillian.class)
public class ClientExceptionRemoteEJBTestCase extends AbstractClusteringTestCase {
    private static final String MODULE_NAME = ClientExceptionRemoteEJBTestCase.class.getSimpleName();

    @Test
    public void test() throws Exception {
        try (EJBDirectory directory = new RemoteEJBDirectory(ClientExceptionRemoteEJBTestCase.MODULE_NAME)) {
            Incrementor bean = directory.lookupStateful(InfinispanExceptionThrowingIncrementorBean.class, Incrementor.class);
            bean.increment();
            Assert.fail("Expected EJBException but didn't catch it");
        } catch (EJBException e) {
            Assert.assertNull("Cause of EJBException has not been removed", e.getCause());
        }
    }
}

