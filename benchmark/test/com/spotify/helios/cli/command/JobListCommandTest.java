/**
 * -
 * -\-\-
 * Helios Tools
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
package com.spotify.helios.cli.command;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.spotify.helios.cli.TestUtils;
import com.spotify.helios.client.HeliosClient;
import com.spotify.helios.common.descriptors.Job;
import com.spotify.helios.common.descriptors.JobId;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class JobListCommandTest {
    private final Namespace options = Mockito.mock(Namespace.class);

    private final HeliosClient client = Mockito.mock(HeliosClient.class);

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    private final PrintStream out = new PrintStream(baos);

    private JobListCommand command;

    private final List<String> expectedOrder = ImmutableList.of("job:1-aaa", "job:2-bbb", "job:3-ccc");

    private final Map<JobId, Job> jobs = ImmutableMap.of(new JobId("job", "1-aaa"), Job.newBuilder().build(), new JobId("job", "3-ccc"), Job.newBuilder().build(), new JobId("job", "2-bbb"), Job.newBuilder().build());

    @Test
    public void testQuietOutputIsSorted() throws Exception {
        Mockito.when(options.getBoolean("q")).thenReturn(true);
        final int ret = command.run(options, client, out, false, null);
        Assert.assertEquals(0, ret);
        Assert.assertEquals(expectedOrder, TestUtils.readFirstColumnFromOutput(baos.toString(), false));
    }

    @Test
    public void testNonQuietOutputIsSorted() throws Exception {
        Mockito.when(options.getBoolean("q")).thenReturn(false);
        final int ret = command.run(options, client, out, false, null);
        Assert.assertEquals(0, ret);
        Assert.assertEquals(expectedOrder, TestUtils.readFirstColumnFromOutput(baos.toString(), true));
    }
}

