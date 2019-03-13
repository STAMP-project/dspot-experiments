/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.cache.spi;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import junit.framework.Assert;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.internal.NaturalIdCacheKey;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertArrayEquals;


public class NaturalIdCacheKeyTest {
    @Test
    public void testSerializationRoundTrip() throws Exception {
        final EntityPersister entityPersister = Mockito.mock(EntityPersister.class);
        final SessionImplementor sessionImplementor = Mockito.mock(SessionImplementor.class);
        final SessionFactoryImplementor sessionFactoryImplementor = Mockito.mock(SessionFactoryImplementor.class);
        final Type mockType = Mockito.mock(Type.class);
        Mockito.when(entityPersister.getRootEntityName()).thenReturn("EntityName");
        Mockito.when(sessionImplementor.getFactory()).thenReturn(sessionFactoryImplementor);
        Mockito.when(entityPersister.getNaturalIdentifierProperties()).thenReturn(new int[]{ 0, 1, 2 });
        Mockito.when(entityPersister.getPropertyTypes()).thenReturn(new Type[]{ mockType, mockType, mockType });
        Mockito.when(mockType.getHashCode(ArgumentMatchers.anyObject(), ArgumentMatchers.eq(sessionFactoryImplementor))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0].hashCode();
            }
        });
        Mockito.when(mockType.disassemble(ArgumentMatchers.anyObject(), ArgumentMatchers.eq(sessionImplementor), ArgumentMatchers.eq(null))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0];
            }
        });
        final NaturalIdCacheKey key = ((NaturalIdCacheKey) (DefaultCacheKeysFactory.staticCreateNaturalIdKey(new Object[]{ "a", "b", "c" }, entityPersister, sessionImplementor)));
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(key);
        final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        final NaturalIdCacheKey keyClone = ((NaturalIdCacheKey) (ois.readObject()));
        Assert.assertEquals(key, keyClone);
        Assert.assertEquals(key.hashCode(), keyClone.hashCode());
        Assert.assertEquals(key.toString(), keyClone.toString());
        Assert.assertEquals(key.getEntityName(), keyClone.getEntityName());
        assertArrayEquals(key.getNaturalIdValues(), keyClone.getNaturalIdValues());
        Assert.assertEquals(key.getTenantId(), keyClone.getTenantId());
    }
}

