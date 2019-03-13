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
package org.wildfly.clustering.web.infinispan.session;


import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.Mockito;
import org.wildfly.clustering.ee.Batcher;
import org.wildfly.clustering.ee.Remover;
import org.wildfly.clustering.ee.infinispan.TransactionBatch;
import org.wildfly.clustering.web.session.ImmutableSessionMetaData;


/**
 * Unit test for {@link SessionExpirationScheduler}.
 *
 * @author Paul Ferraro
 */
public class SessionExpirationSchedulerTestCase {
    @Test
    public void test() throws InterruptedException {
        Batcher<TransactionBatch> batcher = Mockito.mock(Batcher.class);
        TransactionBatch batch = Mockito.mock(TransactionBatch.class);
        Remover<String> remover = Mockito.mock(Remover.class);
        ImmutableSessionMetaData immortalSessionMetaData = Mockito.mock(ImmutableSessionMetaData.class);
        ImmutableSessionMetaData expiringSessionMetaData = Mockito.mock(ImmutableSessionMetaData.class);
        ImmutableSessionMetaData canceledSessionMetaData = Mockito.mock(ImmutableSessionMetaData.class);
        String immortalSessionId = "immortal";
        String expiringSessionId = "expiring";
        String canceledSessionId = "canceled";
        Mockito.when(batcher.createBatch()).thenReturn(batch);
        Mockito.when(immortalSessionMetaData.getMaxInactiveInterval()).thenReturn(Duration.ZERO);
        Mockito.when(expiringSessionMetaData.getMaxInactiveInterval()).thenReturn(Duration.ofMillis(1L));
        Mockito.when(canceledSessionMetaData.getMaxInactiveInterval()).thenReturn(Duration.ofSeconds(100L));
        Instant now = Instant.now();
        Mockito.when(expiringSessionMetaData.getLastAccessedTime()).thenReturn(now);
        Mockito.when(canceledSessionMetaData.getLastAccessedTime()).thenReturn(now);
        try (Scheduler scheduler = new SessionExpirationScheduler(batcher, remover)) {
            scheduler.schedule(immortalSessionId, immortalSessionMetaData);
            scheduler.schedule(canceledSessionId, canceledSessionMetaData);
            scheduler.schedule(expiringSessionId, expiringSessionMetaData);
            TimeUnit.SECONDS.sleep(1L);
            scheduler.cancel(canceledSessionId);
            scheduler.schedule(canceledSessionId, canceledSessionMetaData);
        }
        Mockito.verify(remover, Mockito.never()).remove(immortalSessionId);
        Mockito.verify(remover).remove(expiringSessionId);
        Mockito.verify(remover, Mockito.never()).remove(canceledSessionId);
        Mockito.verify(batch).close();
    }
}

