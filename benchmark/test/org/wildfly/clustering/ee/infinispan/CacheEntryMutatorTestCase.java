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
package org.wildfly.clustering.ee.infinispan;


import Flag.FAIL_SILENTLY;
import Flag.IGNORE_RETURN_VALUES;
import TransactionMode.NON_TRANSACTIONAL;
import TransactionMode.TRANSACTIONAL;
import org.infinispan.AdvancedCache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.wildfly.clustering.ee.Mutator;


/**
 * Unit test for {@link CacheEntryMutator}.
 *
 * @author Paul Ferraro
 */
public class CacheEntryMutatorTestCase {
    @Test
    public void mutateTransactional() {
        AdvancedCache<Object, Object> cache = Mockito.mock(AdvancedCache.class);
        Object id = new Object();
        Object value = new Object();
        Configuration config = new ConfigurationBuilder().transaction().transactionMode(TRANSACTIONAL).build();
        Mockito.when(cache.getCacheConfiguration()).thenReturn(config);
        Mutator mutator = new CacheEntryMutator(cache, id, value);
        Mockito.when(cache.getAdvancedCache()).thenReturn(cache);
        Mockito.when(cache.withFlags(IGNORE_RETURN_VALUES, FAIL_SILENTLY)).thenReturn(cache);
        mutator.mutate();
        Mockito.verify(cache).put(ArgumentMatchers.same(id), ArgumentMatchers.same(value));
        mutator.mutate();
        Mockito.verify(cache, Mockito.times(1)).put(ArgumentMatchers.same(id), ArgumentMatchers.same(value));
        mutator.mutate();
        Mockito.verify(cache, Mockito.times(1)).put(ArgumentMatchers.same(id), ArgumentMatchers.same(value));
    }

    @Test
    public void mutateNonTransactional() {
        AdvancedCache<Object, Object> cache = Mockito.mock(AdvancedCache.class);
        Object id = new Object();
        Object value = new Object();
        Configuration config = new ConfigurationBuilder().transaction().transactionMode(NON_TRANSACTIONAL).build();
        Mockito.when(cache.getCacheConfiguration()).thenReturn(config);
        Mutator mutator = new CacheEntryMutator(cache, id, value);
        Mockito.when(cache.getAdvancedCache()).thenReturn(cache);
        Mockito.when(cache.withFlags(IGNORE_RETURN_VALUES, FAIL_SILENTLY)).thenReturn(cache);
        mutator.mutate();
        Mockito.verify(cache).put(ArgumentMatchers.same(id), ArgumentMatchers.same(value));
        mutator.mutate();
        Mockito.verify(cache, Mockito.times(2)).put(ArgumentMatchers.same(id), ArgumentMatchers.same(value));
        mutator.mutate();
        Mockito.verify(cache, Mockito.times(3)).put(ArgumentMatchers.same(id), ArgumentMatchers.same(value));
    }
}

