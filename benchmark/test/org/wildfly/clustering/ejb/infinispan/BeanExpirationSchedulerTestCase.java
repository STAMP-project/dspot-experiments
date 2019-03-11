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
package org.wildfly.clustering.ejb.infinispan;


import java.time.Duration;
import java.util.concurrent.Executors;
import org.junit.Test;
import org.mockito.Mockito;
import org.wildfly.clustering.ee.Batcher;
import org.wildfly.clustering.ee.infinispan.TransactionBatch;
import org.wildfly.clustering.ejb.RemoveListener;


public class BeanExpirationSchedulerTestCase {
    @Test
    public void testImmortal() throws InterruptedException {
        Batcher<TransactionBatch> batcher = Mockito.mock(Batcher.class);
        BeanRemover<String, Object> remover = Mockito.mock(BeanRemover.class);
        ExpirationConfiguration<Object> config = Mockito.mock(ExpirationConfiguration.class);
        RemoveListener<Object> listener = Mockito.mock(RemoveListener.class);
        String beanId = "immortal";
        Mockito.when(config.getExecutor()).thenReturn(Executors.newSingleThreadScheduledExecutor());
        // Fun fact: the EJB specification allows a timeout value of 0, so only negative timeouts are treated as immortal
        Mockito.when(config.getTimeout()).thenReturn(Duration.ofMinutes((-1L)));
        Mockito.when(config.getRemoveListener()).thenReturn(listener);
        try (Scheduler<String> scheduler = new BeanExpirationScheduler(batcher, remover, config)) {
            scheduler.schedule(beanId);
            Thread.sleep(1000);
        }
        Mockito.verify(batcher, Mockito.never()).createBatch();
        Mockito.verify(remover, Mockito.never()).remove(beanId, listener);
    }

    @Test
    public void testExpire() throws InterruptedException {
        Batcher<TransactionBatch> batcher = Mockito.mock(Batcher.class);
        TransactionBatch batch = Mockito.mock(TransactionBatch.class);
        BeanRemover<String, Object> remover = Mockito.mock(BeanRemover.class);
        ExpirationConfiguration<Object> config = Mockito.mock(ExpirationConfiguration.class);
        RemoveListener<Object> listener = Mockito.mock(RemoveListener.class);
        String beanId = "expiring";
        Mockito.when(config.getExecutor()).thenReturn(Executors.newSingleThreadScheduledExecutor());
        Mockito.when(batcher.createBatch()).thenReturn(batch);
        Mockito.when(config.getTimeout()).thenReturn(Duration.ofMillis(1L));
        Mockito.when(config.getRemoveListener()).thenReturn(listener);
        try (Scheduler<String> scheduler = new BeanExpirationScheduler(batcher, remover, config)) {
            scheduler.schedule(beanId);
            Thread.sleep(1000);
        }
        Mockito.verify(remover).remove(beanId, listener);
        Mockito.verify(batch).close();
    }

    @Test
    public void testCancel() throws InterruptedException {
        Batcher<TransactionBatch> batcher = Mockito.mock(Batcher.class);
        BeanRemover<String, Object> remover = Mockito.mock(BeanRemover.class);
        ExpirationConfiguration<Object> config = Mockito.mock(ExpirationConfiguration.class);
        RemoveListener<Object> listener = Mockito.mock(RemoveListener.class);
        String beanId = "canceled";
        Mockito.when(config.getExecutor()).thenReturn(Executors.newSingleThreadScheduledExecutor());
        Mockito.when(config.getTimeout()).thenReturn(Duration.ofMinutes(1L));
        Mockito.when(config.getRemoveListener()).thenReturn(listener);
        try (Scheduler<String> scheduler = new BeanExpirationScheduler(batcher, remover, config)) {
            scheduler.schedule(beanId);
            Thread.sleep(1000);
            scheduler.cancel(beanId);
            scheduler.schedule(beanId);
        }
        Mockito.verify(remover, Mockito.never()).remove(beanId, listener);
        Mockito.verify(batcher, Mockito.never()).createBatch();
    }
}

