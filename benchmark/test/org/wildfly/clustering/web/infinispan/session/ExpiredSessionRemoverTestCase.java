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


import java.util.UUID;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.wildfly.clustering.Registration;
import org.wildfly.clustering.web.session.ImmutableSession;
import org.wildfly.clustering.web.session.ImmutableSessionAttributes;
import org.wildfly.clustering.web.session.ImmutableSessionMetaData;
import org.wildfly.clustering.web.session.SessionExpirationListener;


/**
 * Unit test for {@link ExpiredSessionRemover}.
 *
 * @author Paul Ferraro
 */
public class ExpiredSessionRemoverTestCase {
    @Test
    public void test() {
        SessionFactory<UUID, UUID, Object> factory = Mockito.mock(SessionFactory.class);
        SessionMetaDataFactory<UUID, Object> metaDataFactory = Mockito.mock(SessionMetaDataFactory.class);
        SessionAttributesFactory<UUID> attributesFactory = Mockito.mock(SessionAttributesFactory.class);
        SessionExpirationListener listener = Mockito.mock(SessionExpirationListener.class);
        ImmutableSessionAttributes expiredAttributes = Mockito.mock(ImmutableSessionAttributes.class);
        ImmutableSessionMetaData validMetaData = Mockito.mock(ImmutableSessionMetaData.class);
        ImmutableSessionMetaData expiredMetaData = Mockito.mock(ImmutableSessionMetaData.class);
        ImmutableSession expiredSession = Mockito.mock(ImmutableSession.class);
        String missingSessionId = "missing";
        String expiredSessionId = "expired";
        String validSessionId = "valid";
        UUID expiredMetaDataValue = UUID.randomUUID();
        UUID expiredAttributesValue = UUID.randomUUID();
        UUID validMetaDataValue = UUID.randomUUID();
        ExpiredSessionRemover<UUID, UUID, Object> subject = new ExpiredSessionRemover(factory);
        try (Registration regisration = subject.register(listener)) {
            Mockito.when(factory.getMetaDataFactory()).thenReturn(metaDataFactory);
            Mockito.when(factory.getAttributesFactory()).thenReturn(attributesFactory);
            Mockito.when(metaDataFactory.tryValue(missingSessionId)).thenReturn(null);
            Mockito.when(metaDataFactory.tryValue(expiredSessionId)).thenReturn(expiredMetaDataValue);
            Mockito.when(metaDataFactory.tryValue(validSessionId)).thenReturn(validMetaDataValue);
            Mockito.when(metaDataFactory.createImmutableSessionMetaData(expiredSessionId, expiredMetaDataValue)).thenReturn(expiredMetaData);
            Mockito.when(metaDataFactory.createImmutableSessionMetaData(validSessionId, validMetaDataValue)).thenReturn(validMetaData);
            Mockito.when(expiredMetaData.isExpired()).thenReturn(true);
            Mockito.when(validMetaData.isExpired()).thenReturn(false);
            Mockito.when(attributesFactory.findValue(expiredSessionId)).thenReturn(expiredAttributesValue);
            Mockito.when(attributesFactory.createImmutableSessionAttributes(expiredSessionId, expiredAttributesValue)).thenReturn(expiredAttributes);
            Mockito.when(factory.createImmutableSession(ArgumentMatchers.same(expiredSessionId), ArgumentMatchers.same(expiredMetaData), ArgumentMatchers.same(expiredAttributes))).thenReturn(expiredSession);
            subject.remove(missingSessionId);
            subject.remove(expiredSessionId);
            subject.remove(validSessionId);
            Mockito.verify(factory).remove(expiredSessionId);
            Mockito.verify(factory, Mockito.never()).remove(missingSessionId);
            Mockito.verify(factory, Mockito.never()).remove(validSessionId);
            Mockito.verify(listener).sessionExpired(expiredSession);
        }
    }
}

