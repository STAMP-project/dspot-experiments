/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer;


import ResourceState.DOWNLOADING;
import ResourceState.LOCALIZED;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.event.DrainDispatcher;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerEventType;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.LocalizerEvent;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.LocalizerEventType;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.LocalizerResourceRequestEvent;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestLocalizedResource {
    // mocked generic
    @Test
    @SuppressWarnings("unchecked")
    public void testNotification() throws Exception {
        DrainDispatcher dispatcher = new DrainDispatcher();
        dispatcher.init(new Configuration());
        try {
            dispatcher.start();
            EventHandler<ContainerEvent> containerBus = Mockito.mock(EventHandler.class);
            EventHandler<LocalizerEvent> localizerBus = Mockito.mock(EventHandler.class);
            dispatcher.register(ContainerEventType.class, containerBus);
            dispatcher.register(LocalizerEventType.class, localizerBus);
            // mock resource
            LocalResource apiRsrc = TestLocalizedResource.createMockResource();
            final ContainerId container0 = TestLocalizedResource.getMockContainer(0L);
            final Credentials creds0 = new Credentials();
            final LocalResourceVisibility vis0 = LocalResourceVisibility.PRIVATE;
            final LocalizerContext ctxt0 = new LocalizerContext("yak", container0, creds0);
            LocalResourceRequest rsrcA = new LocalResourceRequest(apiRsrc);
            LocalizedResource local = new LocalizedResource(rsrcA, dispatcher);
            local.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ResourceRequestEvent(rsrcA, vis0, ctxt0));
            dispatcher.await();
            // Register C0, verify request event
            TestLocalizedResource.LocalizerEventMatcher matchesL0Req = new TestLocalizedResource.LocalizerEventMatcher(container0, creds0, vis0, LocalizerEventType.REQUEST_RESOURCE_LOCALIZATION);
            Mockito.verify(localizerBus).handle(ArgumentMatchers.argThat(matchesL0Req));
            Assert.assertEquals(DOWNLOADING, local.getState());
            // Register C1, verify request event
            final Credentials creds1 = new Credentials();
            final ContainerId container1 = TestLocalizedResource.getMockContainer(1L);
            final LocalizerContext ctxt1 = new LocalizerContext("yak", container1, creds1);
            final LocalResourceVisibility vis1 = LocalResourceVisibility.PUBLIC;
            local.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ResourceRequestEvent(rsrcA, vis1, ctxt1));
            dispatcher.await();
            TestLocalizedResource.LocalizerEventMatcher matchesL1Req = new TestLocalizedResource.LocalizerEventMatcher(container1, creds1, vis1, LocalizerEventType.REQUEST_RESOURCE_LOCALIZATION);
            Mockito.verify(localizerBus).handle(ArgumentMatchers.argThat(matchesL1Req));
            // Release C0 container localization, verify no notification
            local.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ResourceReleaseEvent(rsrcA, container0));
            dispatcher.await();
            Mockito.verify(containerBus, Mockito.never()).handle(ArgumentMatchers.isA(ContainerEvent.class));
            Assert.assertEquals(DOWNLOADING, local.getState());
            // Release C1 container localization, verify no notification
            local.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ResourceReleaseEvent(rsrcA, container1));
            dispatcher.await();
            Mockito.verify(containerBus, Mockito.never()).handle(ArgumentMatchers.isA(ContainerEvent.class));
            Assert.assertEquals(DOWNLOADING, local.getState());
            // Register C2, C3
            final ContainerId container2 = TestLocalizedResource.getMockContainer(2L);
            final LocalResourceVisibility vis2 = LocalResourceVisibility.PRIVATE;
            final Credentials creds2 = new Credentials();
            final LocalizerContext ctxt2 = new LocalizerContext("yak", container2, creds2);
            final ContainerId container3 = TestLocalizedResource.getMockContainer(3L);
            final LocalResourceVisibility vis3 = LocalResourceVisibility.PRIVATE;
            final Credentials creds3 = new Credentials();
            final LocalizerContext ctxt3 = new LocalizerContext("yak", container3, creds3);
            local.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ResourceRequestEvent(rsrcA, vis2, ctxt2));
            local.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ResourceRequestEvent(rsrcA, vis3, ctxt3));
            dispatcher.await();
            TestLocalizedResource.LocalizerEventMatcher matchesL2Req = new TestLocalizedResource.LocalizerEventMatcher(container2, creds2, vis2, LocalizerEventType.REQUEST_RESOURCE_LOCALIZATION);
            Mockito.verify(localizerBus).handle(ArgumentMatchers.argThat(matchesL2Req));
            TestLocalizedResource.LocalizerEventMatcher matchesL3Req = new TestLocalizedResource.LocalizerEventMatcher(container3, creds3, vis3, LocalizerEventType.REQUEST_RESOURCE_LOCALIZATION);
            Mockito.verify(localizerBus).handle(ArgumentMatchers.argThat(matchesL3Req));
            // Successful localization. verify notification C2, C3
            Path locA = new Path("file:///cache/rsrcA");
            local.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ResourceLocalizedEvent(rsrcA, locA, 10));
            dispatcher.await();
            TestLocalizedResource.ContainerEventMatcher matchesC2Localized = new TestLocalizedResource.ContainerEventMatcher(container2, ContainerEventType.RESOURCE_LOCALIZED);
            TestLocalizedResource.ContainerEventMatcher matchesC3Localized = new TestLocalizedResource.ContainerEventMatcher(container3, ContainerEventType.RESOURCE_LOCALIZED);
            Mockito.verify(containerBus).handle(ArgumentMatchers.argThat(matchesC2Localized));
            Mockito.verify(containerBus).handle(ArgumentMatchers.argThat(matchesC3Localized));
            Assert.assertEquals(LOCALIZED, local.getState());
            // Register C4, verify notification
            final ContainerId container4 = TestLocalizedResource.getMockContainer(4L);
            final Credentials creds4 = new Credentials();
            final LocalizerContext ctxt4 = new LocalizerContext("yak", container4, creds4);
            final LocalResourceVisibility vis4 = LocalResourceVisibility.PRIVATE;
            local.handle(new org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.ResourceRequestEvent(rsrcA, vis4, ctxt4));
            dispatcher.await();
            TestLocalizedResource.ContainerEventMatcher matchesC4Localized = new TestLocalizedResource.ContainerEventMatcher(container4, ContainerEventType.RESOURCE_LOCALIZED);
            Mockito.verify(containerBus).handle(ArgumentMatchers.argThat(matchesC4Localized));
            Assert.assertEquals(LOCALIZED, local.getState());
        } finally {
            dispatcher.stop();
        }
    }

    static class LocalizerEventMatcher implements ArgumentMatcher<LocalizerEvent> {
        Credentials creds;

        LocalResourceVisibility vis;

        private final ContainerId idRef;

        private final LocalizerEventType type;

        public LocalizerEventMatcher(ContainerId idRef, Credentials creds, LocalResourceVisibility vis, LocalizerEventType type) {
            this.vis = vis;
            this.type = type;
            this.creds = creds;
            this.idRef = idRef;
        }

        @Override
        public boolean matches(LocalizerEvent le) {
            if (!(le instanceof LocalizerResourceRequestEvent)) {
                return false;
            }
            LocalizerResourceRequestEvent evt = ((LocalizerResourceRequestEvent) (le));
            return ((((idRef) == (evt.getContext().getContainerId())) && ((type) == (evt.getType()))) && ((vis) == (evt.getVisibility()))) && ((creds) == (evt.getContext().getCredentials()));
        }
    }

    static class ContainerEventMatcher implements ArgumentMatcher<ContainerEvent> {
        private final ContainerId idRef;

        private final ContainerEventType type;

        public ContainerEventMatcher(ContainerId idRef, ContainerEventType type) {
            this.idRef = idRef;
            this.type = type;
        }

        @Override
        public boolean matches(ContainerEvent evt) {
            return ((idRef) == (evt.getContainerID())) && ((type) == (evt.getType()));
        }
    }
}

