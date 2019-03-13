/**
 * -\-\-
 * Spotify Apollo Jetty HTTP Server Module
 * --
 * Copyright (C) 2013 - 2016 Spotify AB
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
package com.spotify.apollo.http.server;


import Status.INTERNAL_SERVER_ERROR;
import com.spotify.apollo.Response;
import com.spotify.apollo.request.OngoingRequest;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class TimeoutListenerTest {
    private TimeoutListener listener;

    @Mock
    private OngoingRequest ongoingRequest;

    @Test
    public void shouldSendErrorResponseOnTimeout() throws Exception {
        listener.onTimeout(null);
        Mockito.verify(ongoingRequest).reply(Response.forStatus(INTERNAL_SERVER_ERROR.withReasonPhrase("Timeout")));
    }
}

