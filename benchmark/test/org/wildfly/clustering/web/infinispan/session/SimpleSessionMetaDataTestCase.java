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
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.wildfly.clustering.web.session.SessionMetaData;


public class SimpleSessionMetaDataTestCase {
    private final SessionCreationMetaData creationMetaData = Mockito.mock(SessionCreationMetaData.class);

    private final SessionAccessMetaData accessMetaData = Mockito.mock(SessionAccessMetaData.class);

    private final SessionMetaData metaData = new SimpleSessionMetaData(this.creationMetaData, this.accessMetaData);

    @Test
    public void isNew() {
        Mockito.when(this.accessMetaData.getLastAccessedDuration()).thenReturn(Duration.ZERO);
        Assert.assertTrue(this.metaData.isNew());
        Mockito.when(this.accessMetaData.getLastAccessedDuration()).thenReturn(Duration.ofMillis(1L));
        Assert.assertFalse(this.metaData.isNew());
    }

    @Test
    public void isExpired() {
        Mockito.when(this.creationMetaData.getCreationTime()).thenReturn(Instant.now().minus(Duration.ofMinutes(10L)));
        Mockito.when(this.creationMetaData.getMaxInactiveInterval()).thenReturn(Duration.ofMinutes(10L));
        Mockito.when(this.accessMetaData.getLastAccessedDuration()).thenReturn(Duration.ofMinutes(5L));
        Assert.assertFalse(this.metaData.isExpired());
        Mockito.when(this.creationMetaData.getMaxInactiveInterval()).thenReturn(Duration.ofMinutes(5L));
        Mockito.when(this.accessMetaData.getLastAccessedDuration()).thenReturn(Duration.ofMinutes(0L));
        Assert.assertTrue(this.metaData.isExpired());
        // Max inactive interval of 0 means never expire
        Mockito.when(this.creationMetaData.getMaxInactiveInterval()).thenReturn(Duration.ZERO);
        Assert.assertFalse(this.metaData.isExpired());
    }

    @Test
    public void getCreationTime() {
        Instant expected = Instant.now();
        Mockito.when(this.creationMetaData.getCreationTime()).thenReturn(expected);
        Instant result = this.metaData.getCreationTime();
        Assert.assertSame(expected, result);
    }

    @Test
    public void getLastAccessedTime() {
        Instant now = Instant.now();
        Duration lastAccessed = Duration.ofSeconds(10L);
        Mockito.when(this.creationMetaData.getCreationTime()).thenReturn(now.minus(lastAccessed));
        Mockito.when(this.accessMetaData.getLastAccessedDuration()).thenReturn(lastAccessed);
        Instant result = this.metaData.getLastAccessedTime();
        Assert.assertEquals(now, result);
    }

    @Test
    public void getMaxInactiveInterval() {
        Duration expected = Duration.ofMinutes(30L);
        Mockito.when(this.creationMetaData.getMaxInactiveInterval()).thenReturn(expected);
        Duration result = this.metaData.getMaxInactiveInterval();
        Assert.assertSame(expected, result);
    }

    @Test
    public void setLastAccessedTime() {
        Instant now = Instant.now();
        Duration sinceCreated = Duration.ofSeconds(10L);
        Mockito.when(this.creationMetaData.getCreationTime()).thenReturn(now.minus(sinceCreated));
        this.metaData.setLastAccessedTime(now);
        Mockito.verify(this.accessMetaData).setLastAccessedDuration(sinceCreated);
    }

    @Test
    public void setMaxInactiveInterval() {
        Duration duration = Duration.ZERO;
        this.metaData.setMaxInactiveInterval(duration);
        Mockito.verify(this.creationMetaData).setMaxInactiveInterval(duration);
    }
}

