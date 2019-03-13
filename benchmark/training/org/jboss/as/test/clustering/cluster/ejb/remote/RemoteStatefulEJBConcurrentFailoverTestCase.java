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


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.test.clustering.cluster.AbstractClusteringTestCase;
import org.jboss.as.test.clustering.cluster.ejb.remote.bean.Incrementor;
import org.jboss.as.test.clustering.ejb.EJBDirectory;
import org.jboss.as.test.shared.TimeoutUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Validates mid-invocation failover behavior of a remotely accessed @Stateful EJB.
 *
 * @author Paul Ferraro
 */
@RunWith(Arquillian.class)
@Ignore("WFLY-11322")
public class RemoteStatefulEJBConcurrentFailoverTestCase extends AbstractClusteringTestCase {
    private static final String MODULE_NAME = RemoteStatefulEJBConcurrentFailoverTestCase.class.getSimpleName();

    private static final long CLIENT_TOPOLOGY_UPDATE_WAIT = TimeoutUtil.adjust(5000);

    @Test
    public void test() throws Exception {
        this.test(new AbstractClusteringTestCase.GracefulRestartLifecycle());
    }

    private class IncrementTask implements Runnable {
        private final Incrementor bean;

        private final CountDownLatch latch;

        private final AtomicInteger value;

        IncrementTask(Incrementor bean, AtomicInteger value, CountDownLatch latch) {
            this.bean = bean;
            this.value = value;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                int value = this.bean.increment().getValue();
                Assert.assertEquals(this.value.incrementAndGet(), value);
            } finally {
                this.latch.countDown();
            }
        }
    }

    private class LookupTask implements Runnable {
        private final EJBDirectory directory;

        private final Class<? extends Incrementor> beanClass;

        private final CountDownLatch latch;

        LookupTask(EJBDirectory directory, Class<? extends Incrementor> beanClass, CountDownLatch latch) {
            this.directory = directory;
            this.beanClass = beanClass;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                this.directory.lookupStateful(this.beanClass, Incrementor.class).remove();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            } finally {
                this.latch.countDown();
            }
        }
    }
}

