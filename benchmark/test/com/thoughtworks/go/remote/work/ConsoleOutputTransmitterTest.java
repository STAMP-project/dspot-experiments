/**
 * Copyright 2016 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.remote.work;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ConsoleOutputTransmitterTest {
    @Mock
    private ConsoleAppender consoleAppender;

    private ArgumentCaptor<String> requestArgumentCaptor;

    private ConsoleOutputTransmitter transmitter;

    @Test
    public void shouldFlushContentsInBufferToServerInOneGo() throws Exception {
        transmitter.consumeLine("first line");
        transmitter.consumeLine("second line");
        transmitter.flushToServer();
        Mockito.verify(consoleAppender).append(ArgumentMatchers.any(String.class));
        Assert.assertThat(requestArgumentCaptor.getValue(), Matchers.containsString("first line\n"));
        Assert.assertThat(requestArgumentCaptor.getValue(), Matchers.containsString("second line\n"));
    }

    @Test
    public void shouldNotFlushToServerWhenBufferIsEmpty() throws Exception {
        transmitter.flushToServer();
        Mockito.verify(consoleAppender, Mockito.never()).append(ArgumentMatchers.any(String.class));
    }
}

