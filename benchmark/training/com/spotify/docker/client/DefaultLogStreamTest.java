/**
 * -
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 Spotify AB
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
package com.spotify.docker.client;


import LogMessage.Stream.STDERR;
import LogMessage.Stream.STDOUT;
import java.io.ByteArrayOutputStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DefaultLogStreamTest {
    private final LogReader reader = Mockito.mock(LogReader.class);

    private final DefaultLogStream logStream = new DefaultLogStream(reader);

    @Test
    public void testAttach() throws Exception {
        // need to return null to signal end of stream
        Mockito.when(reader.nextMessage()).thenReturn(DefaultLogStreamTest.logMessage(STDOUT, "hello\n"), DefaultLogStreamTest.logMessage(STDERR, "oops\n"), DefaultLogStreamTest.logMessage(STDOUT, "world!\n"), null);
        final ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        final ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        logStream.attach(stdout, stderr);
        Assert.assertThat(stdout.toString(), Matchers.is("hello\nworld!\n"));
        Assert.assertThat(stderr.toString(), Matchers.is("oops\n"));
    }
}

