/**
 * -\-\-
 * Spotify Apollo API Implementations
 * --
 * Copyright (C) 2013 - 2015 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.apollo.request;


import Status.SERVICE_UNAVAILABLE;
import com.spotify.apollo.Request;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class RequestTrackerTest {
    private RequestTracker tracker;

    @Test
    public void droppedAfterExpiration() {
        Request requestMessage = Request.forUri("http://service/path");
        OngoingRequest ongoingRequest = Mockito.mock(OngoingRequest.class);
        Mockito.when(ongoingRequest.request()).thenReturn(requestMessage);
        Mockito.when(ongoingRequest.isExpired()).thenReturn(true);
        new TrackedOngoingRequestImpl(ongoingRequest, tracker);
        tracker.reap();
        Mockito.verify(ongoingRequest).drop();
    }

    @Test
    public void shouldFailRequestsWhenClosed() throws Exception {
        Request requestMessage = Request.forUri("http://service/path");
        TrackedOngoingRequest ongoingRequest = Mockito.mock(TrackedOngoingRequest.class);
        Mockito.when(ongoingRequest.request()).thenReturn(requestMessage);
        Mockito.when(ongoingRequest.isExpired()).thenReturn(false);
        tracker.register(ongoingRequest);
        tracker.close();
        Mockito.verify(ongoingRequest).reply(ArgumentMatchers.argThat(hasStatus(SERVICE_UNAVAILABLE)));
    }
}

