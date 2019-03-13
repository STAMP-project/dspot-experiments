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


import Job.Builder;
import TemporaryJobReports.ReportWriter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.spotify.docker.client.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.helios.common.descriptors.Job;
import java.util.Collections;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TemporaryJobBuilderTest {
    private static final Logger log = LoggerFactory.getLogger(TemporaryJobBuilderTest.class);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private final Deployer deployer = Mockito.mock(Deployer.class);

    private final Prober prober = Mockito.mock(Prober.class);

    private final Map<String, String> env = Collections.emptyMap();

    private final ReportWriter reportWriter = Mockito.mock(ReportWriter.class);

    private TemporaryJobBuilder builder;

    private Builder jobBuilder;

    @Test
    public void testBuildFromJob() {
        final ImmutableList<String> hosts = ImmutableList.of("host1");
        builder.deploy(hosts);
        final ImmutableSet<String> expectedWaitPorts = ImmutableSet.of("http");
        Mockito.verify(deployer).deploy(ArgumentMatchers.any(Job.class), ArgumentMatchers.eq(hosts), ArgumentMatchers.eq(expectedWaitPorts), ArgumentMatchers.eq(prober), ArgumentMatchers.eq(reportWriter));
    }

    @Test
    public void testImageFromBuild_NoImageFiles() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Could not find image_info.json");
        builder.imageFromBuild();
    }

    @Test
    public void testImageFromJson() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        final String fileContents = objectMapper.createObjectNode().put("image", "foobar:1.2.3").toString();
        writeToFile(fileContents, "target/image_info.json");
        builder.imageFromBuild();
        Assert.assertThat(jobBuilder.getImage(), Matchers.is("foobar:1.2.3"));
    }

    @Test
    public void testImageFromDockerfileMavenPlugin() throws Exception {
        writeToFile("foobar:from.dockerfile\n", "target/docker/image-name");
        builder.imageFromBuild();
        Assert.assertThat(jobBuilder.getImage(), Matchers.is("foobar:from.dockerfile"));
    }
}

