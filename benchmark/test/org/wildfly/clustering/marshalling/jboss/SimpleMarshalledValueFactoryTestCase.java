/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.wildfly.clustering.marshalling.jboss;


import java.util.UUID;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for SimpleMarshalledValue.
 *
 * @author Brian Stansberry
 */
public class SimpleMarshalledValueFactoryTestCase {
    private final MarshallingContext context;

    private final SimpleMarshalledValueFactory factory;

    public SimpleMarshalledValueFactoryTestCase() {
        MarshallingConfigurationRepository repository = new MarshallingConfigurationRepository() {
            @Override
            public int getCurrentMarshallingVersion() {
                return 0;
            }

            @Override
            public MarshallingConfiguration getMarshallingConfiguration(int version) {
                Assert.assertEquals(0, version);
                return new MarshallingConfiguration();
            }
        };
        this.context = new SimpleMarshallingContext(Marshalling.getMarshallerFactory("river", Marshalling.class.getClassLoader()), repository, Thread.currentThread().getContextClassLoader());
        this.factory = this.createFactory(this.context);
    }

    /**
     * Test method for {@link org.jboss.ha.framework.server.SimpleMarshalledValue#get()}.
     */
    @Test
    public void get() throws Exception {
        UUID uuid = UUID.randomUUID();
        SimpleMarshalledValue<UUID> mv = this.factory.createMarshalledValue(uuid);
        Assert.assertNotNull(mv.peek());
        Assert.assertSame(uuid, mv.peek());
        Assert.assertSame(uuid, mv.get(this.context));
        SimpleMarshalledValue<UUID> copy = replicate(mv);
        Assert.assertNull(copy.peek());
        UUID uuid2 = copy.get(this.context);
        Assert.assertNotSame(uuid, uuid2);
        Assert.assertEquals(uuid, uuid2);
        copy = replicate(copy);
        uuid2 = copy.get(this.context);
        Assert.assertEquals(uuid, uuid2);
        mv = this.factory.createMarshalledValue(null);
        Assert.assertNull(mv.peek());
        Assert.assertNull(mv.getBytes());
        Assert.assertNull(mv.get(this.context));
    }

    /**
     * Test method for {@link org.jboss.ha.framework.server.SimpleMarshalledValue#equals(java.lang.Object)}.
     */
    @Test
    public void equals() throws Exception {
        UUID uuid = UUID.randomUUID();
        SimpleMarshalledValue<UUID> mv = this.factory.createMarshalledValue(uuid);
        Assert.assertTrue(mv.equals(mv));
        Assert.assertFalse(mv.equals(null));
        SimpleMarshalledValue<UUID> dup = this.factory.createMarshalledValue(uuid);
        Assert.assertTrue(mv.equals(dup));
        Assert.assertTrue(dup.equals(mv));
        SimpleMarshalledValue<UUID> replica = replicate(mv);
        Assert.assertTrue(mv.equals(replica));
        Assert.assertTrue(replica.equals(mv));
        SimpleMarshalledValue<UUID> nulled = this.factory.createMarshalledValue(null);
        Assert.assertFalse(mv.equals(nulled));
        Assert.assertFalse(nulled.equals(mv));
        Assert.assertFalse(replica.equals(nulled));
        Assert.assertFalse(nulled.equals(replica));
        Assert.assertTrue(nulled.equals(nulled));
        Assert.assertFalse(nulled.equals(null));
        Assert.assertTrue(nulled.equals(this.factory.createMarshalledValue(null)));
    }

    /**
     * Test method for {@link org.jboss.ha.framework.server.SimpleMarshalledValue#hashCode()}.
     */
    @Test
    public void testHashCode() throws Exception {
        UUID uuid = UUID.randomUUID();
        SimpleMarshalledValue<UUID> mv = this.factory.createMarshalledValue(uuid);
        Assert.assertEquals(uuid.hashCode(), mv.hashCode());
        SimpleMarshalledValue<UUID> copy = replicate(mv);
        this.validateHashCode(uuid, copy);
        mv = this.factory.createMarshalledValue(null);
        Assert.assertEquals(0, mv.hashCode());
    }
}

