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


import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.wildfly.clustering.ee.Remover;
import org.wildfly.clustering.web.LocalContextFactory;
import org.wildfly.clustering.web.session.Session;


/**
 * Unit test for {@link InfinispanSession}.
 *
 * @author paul
 */
public class InfinispanSessionTestCase {
    private final String id = "session";

    private final InvalidatableSessionMetaData metaData = Mockito.mock(InvalidatableSessionMetaData.class);

    private final SessionAttributes attributes = Mockito.mock(SessionAttributes.class);

    private final Remover<String> remover = Mockito.mock(Remover.class);

    private final LocalContextFactory<Object> localContextFactory = Mockito.mock(LocalContextFactory.class);

    private final AtomicReference<Object> localContextRef = new AtomicReference<>();

    private final Session<Object> session = new InfinispanSession(this.id, this.metaData, this.attributes, this.localContextRef, this.localContextFactory, this.remover);

    @Test
    public void getId() {
        Assert.assertSame(this.id, this.session.getId());
    }

    @Test
    public void getAttributes() {
        Assert.assertSame(this.attributes, this.session.getAttributes());
    }

    @Test
    public void getMetaData() {
        Assert.assertSame(this.metaData, this.session.getMetaData());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void invalidate() {
        Mockito.when(this.metaData.invalidate()).thenReturn(true);
        this.session.invalidate();
        Mockito.verify(this.remover).remove(this.id);
        Mockito.reset(this.remover);
        Mockito.when(this.metaData.invalidate()).thenReturn(false);
        this.session.invalidate();
        Mockito.verify(this.remover, Mockito.never()).remove(this.id);
    }

    @Test
    public void isValid() {
        Mockito.when(this.metaData.isValid()).thenReturn(true);
        Assert.assertTrue(this.session.isValid());
        Mockito.when(this.metaData.isValid()).thenReturn(false);
        Assert.assertFalse(this.session.isValid());
    }

    @Test
    public void close() {
        Mockito.when(this.metaData.isValid()).thenReturn(true);
        this.session.close();
        Mockito.verify(this.attributes).close();
        Mockito.verify(this.metaData).setLastAccessedTime(ArgumentMatchers.any(Instant.class));
        Mockito.reset(this.metaData, this.attributes);
        // Verify that session is not mutated if invalid
        Mockito.when(this.metaData.isValid()).thenReturn(false);
        this.session.close();
        this.session.close();
        Mockito.verify(this.attributes, Mockito.never()).close();
        Mockito.verify(this.metaData, Mockito.never()).setLastAccessedTime(ArgumentMatchers.any(Instant.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getLocalContext() {
        Object expected = new Object();
        Mockito.when(this.localContextFactory.createLocalContext()).thenReturn(expected);
        Object result = this.session.getLocalContext();
        Assert.assertSame(expected, result);
        Mockito.reset(this.localContextFactory);
        result = this.session.getLocalContext();
        Mockito.verifyZeroInteractions(this.localContextFactory);
        Assert.assertSame(expected, result);
    }
}

