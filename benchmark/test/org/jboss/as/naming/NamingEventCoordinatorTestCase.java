/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.as.naming;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.naming.CompositeName;
import javax.naming.event.EventContext;
import javax.naming.event.NamespaceChangeListener;
import javax.naming.event.NamingEvent;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.ObjectChangeListener;
import org.junit.Assert;
import org.junit.Test;


/**
 * Basic naming event coordinator tests.
 *
 * @author John E. Bailey
 */
public class NamingEventCoordinatorTestCase {
    private NamingContext context;

    @Test
    public void testFireObjectEvent() throws Exception {
        final NamingEventCoordinator coordinator = new NamingEventCoordinator();
        final NamingEventCoordinatorTestCase.CollectingListener objectListener = new NamingEventCoordinatorTestCase.CollectingListener(1);
        coordinator.addListener("test/path", EventContext.OBJECT_SCOPE, objectListener);
        final NamingEventCoordinatorTestCase.CollectingListener subtreeListener = new NamingEventCoordinatorTestCase.CollectingListener(0);
        coordinator.addListener("test", EventContext.SUBTREE_SCOPE, subtreeListener);
        final NamingEventCoordinatorTestCase.CollectingListener oneLevelListener = new NamingEventCoordinatorTestCase.CollectingListener(0);
        coordinator.addListener("test", EventContext.ONELEVEL_SCOPE, oneLevelListener);
        coordinator.fireEvent(context, new CompositeName("test/path"), null, null, NamingEvent.OBJECT_ADDED, "bind", EventContext.OBJECT_SCOPE);
        objectListener.latch.await(1, TimeUnit.SECONDS);
        Assert.assertEquals(1, objectListener.capturedEvents.size());
        Assert.assertTrue(oneLevelListener.capturedEvents.isEmpty());
        Assert.assertTrue(subtreeListener.capturedEvents.isEmpty());
    }

    @Test
    public void testFireSubTreeEvent() throws Exception {
        final NamingEventCoordinator coordinator = new NamingEventCoordinator();
        final NamingEventCoordinatorTestCase.CollectingListener objectListener = new NamingEventCoordinatorTestCase.CollectingListener(0);
        coordinator.addListener("test/path", EventContext.OBJECT_SCOPE, objectListener);
        final NamingEventCoordinatorTestCase.CollectingListener subtreeListener = new NamingEventCoordinatorTestCase.CollectingListener(1);
        coordinator.addListener("test", EventContext.SUBTREE_SCOPE, subtreeListener);
        final NamingEventCoordinatorTestCase.CollectingListener oneLevelListener = new NamingEventCoordinatorTestCase.CollectingListener(0);
        coordinator.addListener("test", EventContext.ONELEVEL_SCOPE, oneLevelListener);
        coordinator.fireEvent(context, new CompositeName("test/path"), null, null, NamingEvent.OBJECT_ADDED, "bind", EventContext.SUBTREE_SCOPE);
        subtreeListener.latch.await(1, TimeUnit.SECONDS);
        Assert.assertTrue(objectListener.capturedEvents.isEmpty());
        Assert.assertTrue(oneLevelListener.capturedEvents.isEmpty());
        Assert.assertEquals(1, subtreeListener.capturedEvents.size());
    }

    @Test
    public void testFireOneLevelEvent() throws Exception {
        final NamingEventCoordinator coordinator = new NamingEventCoordinator();
        final NamingEventCoordinatorTestCase.CollectingListener objectListener = new NamingEventCoordinatorTestCase.CollectingListener(0);
        coordinator.addListener("test/path", EventContext.OBJECT_SCOPE, objectListener);
        final NamingEventCoordinatorTestCase.CollectingListener subtreeListener = new NamingEventCoordinatorTestCase.CollectingListener(0);
        coordinator.addListener("test", EventContext.SUBTREE_SCOPE, subtreeListener);
        final NamingEventCoordinatorTestCase.CollectingListener oneLevelListener = new NamingEventCoordinatorTestCase.CollectingListener(1);
        coordinator.addListener("test", EventContext.ONELEVEL_SCOPE, oneLevelListener);
        coordinator.fireEvent(context, new CompositeName("test/path"), null, null, NamingEvent.OBJECT_ADDED, "bind", EventContext.ONELEVEL_SCOPE);
        oneLevelListener.latch.await(1, TimeUnit.SECONDS);
        Assert.assertTrue(objectListener.capturedEvents.isEmpty());
        Assert.assertTrue(subtreeListener.capturedEvents.isEmpty());
        Assert.assertEquals(1, oneLevelListener.capturedEvents.size());
    }

    @Test
    public void testFireAllEvent() throws Exception {
        final NamingEventCoordinator coordinator = new NamingEventCoordinator();
        final NamingEventCoordinatorTestCase.CollectingListener objectListener = new NamingEventCoordinatorTestCase.CollectingListener(1);
        coordinator.addListener("test/path", EventContext.OBJECT_SCOPE, objectListener);
        final NamingEventCoordinatorTestCase.CollectingListener subtreeListener = new NamingEventCoordinatorTestCase.CollectingListener(1);
        coordinator.addListener("test", EventContext.SUBTREE_SCOPE, subtreeListener);
        final NamingEventCoordinatorTestCase.CollectingListener oneLevelListener = new NamingEventCoordinatorTestCase.CollectingListener(1);
        coordinator.addListener("test", EventContext.ONELEVEL_SCOPE, oneLevelListener);
        coordinator.fireEvent(context, new CompositeName("test/path"), null, null, NamingEvent.OBJECT_ADDED, "bind", EventContext.OBJECT_SCOPE, EventContext.ONELEVEL_SCOPE, EventContext.SUBTREE_SCOPE);
        objectListener.latch.await(1, TimeUnit.SECONDS);
        oneLevelListener.latch.await(1, TimeUnit.SECONDS);
        subtreeListener.latch.await(1, TimeUnit.SECONDS);
        Assert.assertEquals(1, objectListener.capturedEvents.size());
        Assert.assertEquals(1, subtreeListener.capturedEvents.size());
        Assert.assertEquals(1, oneLevelListener.capturedEvents.size());
    }

    @Test
    public void testFireMultiLevelEvent() throws Exception {
        final NamingEventCoordinator coordinator = new NamingEventCoordinator();
        final NamingEventCoordinatorTestCase.CollectingListener subtreeListener = new NamingEventCoordinatorTestCase.CollectingListener(1);
        coordinator.addListener("foo", EventContext.SUBTREE_SCOPE, subtreeListener);
        final NamingEventCoordinatorTestCase.CollectingListener subtreeListenerTwo = new NamingEventCoordinatorTestCase.CollectingListener(1);
        coordinator.addListener("foo/bar", EventContext.SUBTREE_SCOPE, subtreeListenerTwo);
        final NamingEventCoordinatorTestCase.CollectingListener subtreeListenerThree = new NamingEventCoordinatorTestCase.CollectingListener(1);
        coordinator.addListener("foo/bar/baz", EventContext.SUBTREE_SCOPE, subtreeListenerThree);
        coordinator.fireEvent(context, new CompositeName("foo/bar/baz/boo"), null, null, NamingEvent.OBJECT_ADDED, "bind", EventContext.OBJECT_SCOPE, EventContext.ONELEVEL_SCOPE, EventContext.SUBTREE_SCOPE);
        subtreeListener.latch.await(1, TimeUnit.SECONDS);
        subtreeListenerTwo.latch.await(1, TimeUnit.SECONDS);
        subtreeListenerThree.latch.await(1, TimeUnit.SECONDS);
        Assert.assertEquals(1, subtreeListener.capturedEvents.size());
        Assert.assertEquals(1, subtreeListenerTwo.capturedEvents.size());
        Assert.assertEquals(1, subtreeListenerThree.capturedEvents.size());
    }

    private class CollectingListener implements NamespaceChangeListener , ObjectChangeListener {
        private final List<NamingEvent> capturedEvents = new ArrayList<NamingEvent>();

        private final CountDownLatch latch;

        CollectingListener(int expectedEvents) {
            latch = new CountDownLatch(expectedEvents);
        }

        @Override
        public void objectChanged(NamingEvent evt) {
            captured(evt);
        }

        @Override
        public void objectAdded(NamingEvent evt) {
            captured(evt);
        }

        @Override
        public void objectRemoved(NamingEvent evt) {
            captured(evt);
        }

        @Override
        public void objectRenamed(NamingEvent evt) {
            captured(evt);
        }

        private void captured(final NamingEvent event) {
            capturedEvents.add(event);
            latch.countDown();
        }

        @Override
        public void namingExceptionThrown(NamingExceptionEvent evt) {
        }
    }
}

