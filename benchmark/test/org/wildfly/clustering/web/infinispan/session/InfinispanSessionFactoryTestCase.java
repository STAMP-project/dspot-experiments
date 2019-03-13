/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
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


import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.wildfly.clustering.web.LocalContextFactory;
import org.wildfly.clustering.web.session.ImmutableSession;
import org.wildfly.clustering.web.session.ImmutableSessionAttributes;
import org.wildfly.clustering.web.session.ImmutableSessionMetaData;
import org.wildfly.clustering.web.session.Session;


/**
 * Unit test for {@link InfinispanSessionFactory}.
 *
 * @author Paul Ferraro
 */
public class InfinispanSessionFactoryTestCase {
    private final SessionMetaDataFactory<InfinispanSessionMetaData<Object>, Object> metaDataFactory = Mockito.mock(SessionMetaDataFactory.class);

    private final SessionAttributesFactory<Object> attributesFactory = Mockito.mock(SessionAttributesFactory.class);

    private final LocalContextFactory<Object> localContextFactory = Mockito.mock(LocalContextFactory.class);

    private final SessionFactory<InfinispanSessionMetaData<Object>, Object, Object> factory = new InfinispanSessionFactory(this.metaDataFactory, this.attributesFactory, this.localContextFactory);

    @Test
    public void createValue() {
        SessionCreationMetaData creationMetaData = Mockito.mock(SessionCreationMetaData.class);
        SessionAccessMetaData accessMetaData = Mockito.mock(SessionAccessMetaData.class);
        AtomicReference<Object> localContext = new AtomicReference<>();
        InfinispanSessionMetaData<Object> metaData = new InfinispanSessionMetaData(creationMetaData, accessMetaData, localContext);
        Object attributes = new Object();
        String id = "id";
        Mockito.when(this.metaDataFactory.createValue(id, null)).thenReturn(metaData);
        Mockito.when(this.attributesFactory.createValue(id, null)).thenReturn(attributes);
        Map.Entry<InfinispanSessionMetaData<Object>, Object> result = this.factory.createValue(id, null);
        Assert.assertNotNull(result);
        Assert.assertSame(metaData, result.getKey());
        Assert.assertSame(attributes, result.getValue());
    }

    @Test
    public void findValue() {
        String missingMetaDataSessionId = "no-meta-data";
        String missingAttributesSessionId = "no-attributes";
        String existingSessionId = "existing";
        SessionCreationMetaData creationMetaData = Mockito.mock(SessionCreationMetaData.class);
        SessionAccessMetaData accessMetaData = Mockito.mock(SessionAccessMetaData.class);
        AtomicReference<Object> localContext = new AtomicReference<>();
        InfinispanSessionMetaData<Object> metaData = new InfinispanSessionMetaData(creationMetaData, accessMetaData, localContext);
        Object attributes = new Object();
        Mockito.when(this.metaDataFactory.findValue(missingMetaDataSessionId)).thenReturn(null);
        Mockito.when(this.metaDataFactory.findValue(missingAttributesSessionId)).thenReturn(metaData);
        Mockito.when(this.metaDataFactory.findValue(existingSessionId)).thenReturn(metaData);
        Mockito.when(this.attributesFactory.findValue(missingAttributesSessionId)).thenReturn(null);
        Mockito.when(this.attributesFactory.findValue(existingSessionId)).thenReturn(attributes);
        Map.Entry<InfinispanSessionMetaData<Object>, Object> missingMetaDataResult = this.factory.findValue(missingMetaDataSessionId);
        Map.Entry<InfinispanSessionMetaData<Object>, Object> missingAttributesResult = this.factory.findValue(missingAttributesSessionId);
        Map.Entry<InfinispanSessionMetaData<Object>, Object> existingSessionResult = this.factory.findValue(existingSessionId);
        Assert.assertNull(missingMetaDataResult);
        Assert.assertNull(missingAttributesResult);
        Assert.assertNotNull(existingSessionResult);
        Assert.assertSame(metaData, existingSessionResult.getKey());
        Assert.assertSame(attributes, existingSessionResult.getValue());
    }

    @Test
    public void remove() {
        String id = "id";
        Mockito.when(this.metaDataFactory.remove(id)).thenReturn(false);
        this.factory.remove(id);
        Mockito.verify(this.attributesFactory, Mockito.never()).remove(id);
        Mockito.when(this.metaDataFactory.remove(id)).thenReturn(true);
        this.factory.remove(id);
        Mockito.verify(this.attributesFactory).remove(id);
    }

    @Test
    public void getMetaDataFactory() {
        Assert.assertSame(this.metaDataFactory, this.factory.getMetaDataFactory());
    }

    @Test
    public void createSession() {
        Map.Entry<InfinispanSessionMetaData<Object>, Object> entry = Mockito.mock(Map.Entry.class);
        SessionCreationMetaData creationMetaData = Mockito.mock(SessionCreationMetaData.class);
        SessionAccessMetaData accessMetaData = Mockito.mock(SessionAccessMetaData.class);
        Object localContext = new Object();
        InfinispanSessionMetaData<Object> metaDataValue = new InfinispanSessionMetaData(creationMetaData, accessMetaData, new AtomicReference(localContext));
        Object attributesValue = new Object();
        InvalidatableSessionMetaData metaData = Mockito.mock(InvalidatableSessionMetaData.class);
        SessionAttributes attributes = Mockito.mock(SessionAttributes.class);
        String id = "id";
        Mockito.when(entry.getKey()).thenReturn(metaDataValue);
        Mockito.when(entry.getValue()).thenReturn(attributesValue);
        Mockito.when(this.metaDataFactory.createSessionMetaData(id, metaDataValue)).thenReturn(metaData);
        Mockito.when(this.attributesFactory.createSessionAttributes(id, attributesValue)).thenReturn(attributes);
        Session<Object> result = this.factory.createSession(id, entry);
        Assert.assertSame(id, result.getId());
        Assert.assertSame(metaData, result.getMetaData());
        Assert.assertSame(attributes, result.getAttributes());
        Assert.assertSame(localContext, result.getLocalContext());
    }

    @Test
    public void createImmutableSession() {
        Map.Entry<InfinispanSessionMetaData<Object>, Object> entry = Mockito.mock(Map.Entry.class);
        SessionCreationMetaData creationMetaData = Mockito.mock(SessionCreationMetaData.class);
        SessionAccessMetaData accessMetaData = Mockito.mock(SessionAccessMetaData.class);
        InfinispanSessionMetaData<Object> metaDataValue = new InfinispanSessionMetaData(creationMetaData, accessMetaData, null);
        Object attributesValue = new Object();
        ImmutableSessionMetaData metaData = Mockito.mock(ImmutableSessionMetaData.class);
        ImmutableSessionAttributes attributes = Mockito.mock(ImmutableSessionAttributes.class);
        String id = "id";
        Mockito.when(entry.getKey()).thenReturn(metaDataValue);
        Mockito.when(entry.getValue()).thenReturn(attributesValue);
        Mockito.when(this.metaDataFactory.createImmutableSessionMetaData(id, metaDataValue)).thenReturn(metaData);
        Mockito.when(this.attributesFactory.createImmutableSessionAttributes(id, attributesValue)).thenReturn(attributes);
        ImmutableSession result = this.factory.createImmutableSession(id, entry);
        Assert.assertSame(id, result.getId());
        Assert.assertSame(metaData, result.getMetaData());
        Assert.assertSame(attributes, result.getAttributes());
    }
}

