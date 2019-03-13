/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.server.operations.pojo;


import SyncResponseStatus.DELTA;
import SyncResponseStatus.NO_DELTA;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.server.sync.ConfigurationServerSync;
import org.kaaproject.kaa.server.sync.EndpointAttachResponse;
import org.kaaproject.kaa.server.sync.EndpointDetachResponse;
import org.kaaproject.kaa.server.sync.Event;
import org.kaaproject.kaa.server.sync.EventListenersResponse;
import org.kaaproject.kaa.server.sync.EventServerSync;
import org.kaaproject.kaa.server.sync.LogServerSync;
import org.kaaproject.kaa.server.sync.NotificationServerSync;
import org.kaaproject.kaa.server.sync.ProfileServerSync;
import org.kaaproject.kaa.server.sync.ServerSync;
import org.kaaproject.kaa.server.sync.UserAttachResponse;
import org.kaaproject.kaa.server.sync.UserServerSync;


public class SyncResponseHolderTest {
    @Test
    public void requireReplyTestForProfile() {
        ServerSync response = new ServerSync();
        response.setProfileSync(new ProfileServerSync());
        response.getProfileSync().setResponseStatus(NO_DELTA);
        Assert.assertFalse(requireImmediateReply());
        response.getProfileSync().setResponseStatus(DELTA);
        Assert.assertTrue(requireImmediateReply());
    }

    @Test
    public void requireReplyTestForConfig() {
        ServerSync response = new ServerSync();
        response.setConfigurationSync(new ConfigurationServerSync());
        response.getConfigurationSync().setResponseStatus(NO_DELTA);
        Assert.assertFalse(requireImmediateReply());
        response.getConfigurationSync().setResponseStatus(DELTA);
        Assert.assertTrue(requireImmediateReply());
    }

    @Test
    public void requireReplyTestForNotification() {
        ServerSync response = new ServerSync();
        response.setNotificationSync(new NotificationServerSync());
        response.getNotificationSync().setResponseStatus(NO_DELTA);
        Assert.assertFalse(requireImmediateReply());
        response.getNotificationSync().setResponseStatus(DELTA);
        Assert.assertTrue(requireImmediateReply());
    }

    @Test
    public void requireReplyTestForEvents() {
        ServerSync response = new ServerSync();
        response.setEventSync(new EventServerSync());
        Assert.assertFalse(requireImmediateReply());
        response.getEventSync().setEvents(new ArrayList<Event>());
        Assert.assertFalse(requireImmediateReply());
        response.getEventSync().getEvents().add(new Event());
        Assert.assertTrue(requireImmediateReply());
    }

    @Test
    public void requireReplyTestForEventListeners() {
        ServerSync response = new ServerSync();
        response.setEventSync(new EventServerSync());
        Assert.assertFalse(requireImmediateReply());
        response.getEventSync().setEventListenersResponses(new ArrayList<EventListenersResponse>());
        Assert.assertFalse(requireImmediateReply());
        response.getEventSync().getEventListenersResponses().add(new EventListenersResponse());
        Assert.assertTrue(requireImmediateReply());
    }

    @Test
    public void requireReplyTestForUserAttach() {
        ServerSync response = new ServerSync();
        response.setUserSync(new UserServerSync());
        Assert.assertFalse(requireImmediateReply());
        response.getUserSync().setUserAttachResponse(new UserAttachResponse());
        Assert.assertTrue(requireImmediateReply());
    }

    @Test
    public void requireReplyTestForEndpointAttach() {
        ServerSync response = new ServerSync();
        response.setUserSync(new UserServerSync());
        Assert.assertFalse(requireImmediateReply());
        response.getUserSync().setEndpointAttachResponses(new ArrayList<EndpointAttachResponse>());
        Assert.assertFalse(requireImmediateReply());
        response.getUserSync().getEndpointAttachResponses().add(new EndpointAttachResponse());
        Assert.assertTrue(requireImmediateReply());
    }

    @Test
    public void requireReplyTestForEndpointDetach() {
        ServerSync response = new ServerSync();
        response.setUserSync(new UserServerSync());
        Assert.assertFalse(requireImmediateReply());
        response.getUserSync().setEndpointDetachResponses(new ArrayList<EndpointDetachResponse>());
        Assert.assertFalse(requireImmediateReply());
        response.getUserSync().getEndpointDetachResponses().add(new EndpointDetachResponse());
        Assert.assertTrue(requireImmediateReply());
    }

    @Test
    public void requireReplyTestForLogs() {
        ServerSync response = new ServerSync();
        response.setLogSync(new LogServerSync());
        Assert.assertTrue(requireImmediateReply());
    }
}

