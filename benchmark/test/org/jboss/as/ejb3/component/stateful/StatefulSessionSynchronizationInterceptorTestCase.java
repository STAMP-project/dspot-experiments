/**
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.as.ejb3.component.stateful;


import Status.STATUS_COMMITTED;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.transaction.Synchronization;
import javax.transaction.TransactionSynchronizationRegistry;
import org.jboss.as.ee.component.Component;
import org.jboss.as.ee.component.ComponentInstance;
import org.jboss.as.ejb3.cache.Cache;
import org.jboss.ejb.client.SessionID;
import org.jboss.invocation.Interceptor;
import org.jboss.invocation.InterceptorContext;
import org.jboss.invocation.Interceptors;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 *
 *
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class StatefulSessionSynchronizationInterceptorTestCase {
    /**
     * After the bean is accessed within a tx and the tx has committed, the
     * association should be gone (and thus it is ready for another tx).
     */
    @Test
    public void testDifferentTx() throws Exception {
        final Interceptor interceptor = new StatefulSessionSynchronizationInterceptor(true);
        final InterceptorContext context = new InterceptorContext();
        context.setInterceptors(Arrays.asList(StatefulSessionSynchronizationInterceptorTestCase.noop()));
        final StatefulSessionComponent component = Mockito.mock(StatefulSessionComponent.class);
        context.putPrivateData(Component.class, component);
        Mockito.when(component.getAccessTimeout(null)).thenReturn(StatefulSessionSynchronizationInterceptorTestCase.defaultAccessTimeout());
        Cache<SessionID, StatefulSessionComponentInstance> cache = Mockito.mock(Cache.class);
        Mockito.when(component.getCache()).thenReturn(cache);
        final TransactionSynchronizationRegistry transactionSynchronizationRegistry = Mockito.mock(TransactionSynchronizationRegistry.class);
        Mockito.when(component.getTransactionSynchronizationRegistry()).thenReturn(transactionSynchronizationRegistry);
        Mockito.when(transactionSynchronizationRegistry.getTransactionKey()).thenReturn("TX1");
        final List<Synchronization> synchronizations = new LinkedList<Synchronization>();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Synchronization synchronization = ((Synchronization) (invocation.getArguments()[0]));
                synchronizations.add(synchronization);
                return null;
            }
        }).when(transactionSynchronizationRegistry).registerInterposedSynchronization(((Synchronization) (ArgumentMatchers.any())));
        final StatefulSessionComponentInstance instance = new StatefulSessionComponentInstance(component, Interceptors.getTerminalInterceptor(), Collections.EMPTY_MAP, Collections.emptyMap());
        context.putPrivateData(ComponentInstance.class, instance);
        interceptor.processInvocation(context);
        // commit
        for (Synchronization synchronization : synchronizations) {
            synchronization.beforeCompletion();
        }
        for (Synchronization synchronization : synchronizations) {
            synchronization.afterCompletion(STATUS_COMMITTED);
        }
        synchronizations.clear();
        Mockito.when(transactionSynchronizationRegistry.getTransactionKey()).thenReturn("TX2");
        interceptor.processInvocation(context);
    }
}

