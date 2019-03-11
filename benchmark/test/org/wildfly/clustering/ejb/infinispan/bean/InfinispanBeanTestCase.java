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
package org.wildfly.clustering.ejb.infinispan.bean;


import java.time.Duration;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.wildfly.clustering.ee.Mutator;
import org.wildfly.clustering.ejb.Bean;
import org.wildfly.clustering.ejb.PassivationListener;
import org.wildfly.clustering.ejb.RemoveListener;
import org.wildfly.clustering.ejb.infinispan.BeanEntry;
import org.wildfly.clustering.ejb.infinispan.BeanGroup;
import org.wildfly.clustering.ejb.infinispan.BeanRemover;


public class InfinispanBeanTestCase {
    private final String id = "id";

    private final BeanEntry<String> entry = Mockito.mock(BeanEntry.class);

    private final BeanGroup<String, Object> group = Mockito.mock(BeanGroup.class);

    private final Mutator mutator = Mockito.mock(Mutator.class);

    private final BeanRemover<String, Object> remover = Mockito.mock(BeanRemover.class);

    private final Duration timeout = Duration.ofMinutes(1L);

    private final PassivationListener<Object> listener = Mockito.mock(PassivationListener.class);

    private final Bean<String, Object> bean = new InfinispanBean(this.id, this.entry, this.group, this.mutator, this.remover, this.timeout, this.listener);

    @Test
    public void getId() {
        Assert.assertSame(this.id, this.bean.getId());
    }

    @Test
    public void getGroupId() {
        String groupId = "group";
        Mockito.when(this.entry.getGroupId()).thenReturn(groupId);
        String result = this.bean.getGroupId();
        Assert.assertSame(groupId, result);
    }

    @Test
    public void acquire() {
        Object value = new Object();
        Mockito.when(this.group.getBean(this.id, this.listener)).thenReturn(value);
        Object result = this.bean.acquire();
        Assert.assertSame(value, result);
    }

    @Test
    public void release() {
        this.bean.release();
        Mockito.verify(this.group).releaseBean(this.id, this.listener);
    }

    @Test
    public void isExpired() {
        Mockito.when(this.entry.getLastAccessedTime()).thenReturn(null);
        Assert.assertFalse(this.bean.isExpired());
        long now = System.currentTimeMillis();
        Mockito.when(this.entry.getLastAccessedTime()).thenReturn(new Date(now));
        Assert.assertFalse(this.bean.isExpired());
        Mockito.when(this.entry.getLastAccessedTime()).thenReturn(new Date(((now - (this.timeout.toMillis())) - 1)));
        Assert.assertTrue(this.bean.isExpired());
    }

    @Test
    public void remove() {
        RemoveListener<Object> listener = Mockito.mock(RemoveListener.class);
        Mockito.when(this.group.isCloseable()).thenReturn(false);
        this.bean.remove(listener);
        Mockito.verify(this.remover).remove(this.id, listener);
        this.bean.remove(listener);
        Mockito.verifyNoMoreInteractions(this.remover);
    }

    @Test
    public void close() {
        Mockito.when(this.entry.getLastAccessedTime()).thenReturn(null);
        Mockito.when(this.group.isCloseable()).thenReturn(false);
        this.bean.close();
        Mockito.verify(this.entry).setLastAccessedTime(ArgumentMatchers.<Date>any());
        Mockito.verify(this.mutator, Mockito.never()).mutate();
        Mockito.verify(this.group, Mockito.never()).close();
        Mockito.reset(this.entry, this.mutator, this.group);
        Mockito.when(this.entry.getLastAccessedTime()).thenReturn(new Date());
        Mockito.when(this.group.isCloseable()).thenReturn(true);
        this.bean.close();
        Mockito.verify(this.entry).setLastAccessedTime(ArgumentMatchers.<Date>any());
        Mockito.verify(this.mutator).mutate();
        Mockito.verify(this.group).close();
    }
}

