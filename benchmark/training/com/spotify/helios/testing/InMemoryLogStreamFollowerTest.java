/**
 * -
 * -\-\-
 * Helios Testing Library
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
package com.spotify.helios.testing;


import LogMessage.Stream.STDERR;
import LogMessage.Stream.STDOUT;
import com.google.common.collect.Iterators;
import com.spotify.docker.client.LogMessage;
import com.spotify.helios.common.descriptors.JobId;
import java.util.Iterator;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class InMemoryLogStreamFollowerTest {
    private final InMemoryLogStreamFollower follower = InMemoryLogStreamFollower.create();

    private final JobId jobId = JobId.fromString("foobar:1");

    @Test
    public void testFollow() throws Exception {
        final Iterator<LogMessage> messages = Iterators.forArray(InMemoryLogStreamFollowerTest.asMessage(STDOUT, "hello "), InMemoryLogStreamFollowerTest.asMessage(STDERR, "error 1"), InMemoryLogStreamFollowerTest.asMessage(STDOUT, "world"));
        follower.followLog(jobId, "1234abcd", messages);
        Assert.assertThat(new String(follower.getStdout(jobId)), Matchers.is("hello world"));
        Assert.assertThat(new String(follower.getStderr(jobId)), Matchers.is("error 1"));
    }
}

