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
package com.spotify.apollo.meta;


import com.spotify.apollo.Request;
import com.spotify.apollo.environment.IncomingRequestAwareClient;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class OutgoingCallsGatheringClientTest {
    @Mock
    OutgoingCallsGatherer callsGatherer;

    @Mock
    IncomingRequestAwareClient delegate;

    IncomingRequestAwareClient client;

    @Test
    public void shouldGatherCallsWithServiceFromAuthority() throws Exception {
        Request request = Request.forUri("http://bowman/path/to/file");
        client.send(request, Optional.empty());
        Mockito.verify(delegate).send(ArgumentMatchers.eq(request), ArgumentMatchers.eq(Optional.empty()));
        Mockito.verify(callsGatherer).gatherOutgoingCall(ArgumentMatchers.eq("bowman"), ArgumentMatchers.eq(request));
    }
}

