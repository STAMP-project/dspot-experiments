/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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
package org.wildfly.clustering.web.infinispan;


import org.infinispan.Cache;
import org.infinispan.affinity.KeyAffinityService;
import org.infinispan.affinity.KeyGenerator;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.transport.Address;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.wildfly.clustering.infinispan.spi.affinity.KeyAffinityServiceFactory;
import org.wildfly.clustering.infinispan.spi.distribution.Key;
import org.wildfly.clustering.web.IdentifierFactory;


/**
 * Unit test for {@link AffinityIdentifierFactory}
 *
 * @author Paul Ferraro
 */
public class AffinityIdentityFactoryTestCase {
    private final IdentifierFactory<String> factory = Mockito.mock(IdentifierFactory.class);

    private final KeyAffinityServiceFactory affinityFactory = Mockito.mock(KeyAffinityServiceFactory.class);

    private final KeyAffinityService<Key<String>> affinity = Mockito.mock(KeyAffinityService.class);

    private final Cache<Key<String>, ?> cache = Mockito.mock(Cache.class);

    private final EmbeddedCacheManager manager = Mockito.mock(EmbeddedCacheManager.class);

    private IdentifierFactory<String> subject;

    @Captor
    private ArgumentCaptor<KeyGenerator<Key<String>>> capturedGenerator;

    @Test
    public void createIdentifier() {
        String expected = "id";
        Address address = Mockito.mock(Address.class);
        Mockito.when(this.manager.getAddress()).thenReturn(address);
        Mockito.when(this.affinity.getKeyForAddress(address)).thenReturn(new Key(expected));
        String result = this.subject.createIdentifier();
        Assert.assertSame(expected, result);
    }
}

