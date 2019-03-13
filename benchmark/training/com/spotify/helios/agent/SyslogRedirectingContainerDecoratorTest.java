/**
 * -
 * -\-\-
 * Helios Services
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
package com.spotify.helios.agent;


import HostConfig.Builder;
import com.google.common.base.Optional;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.ImageInfo;
import com.spotify.docker.client.messages.LogConfig;
import com.spotify.helios.common.descriptors.Job;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SyslogRedirectingContainerDecoratorTest {
    private static final String SYSLOG_HOST_PORT = "fun:123";

    private static final Job JOB = Job.newBuilder().setName("myjob").setImage("abc").setVersion("x").build();

    private ImageInfo imageInfo;

    @Test
    public void testWithDockerVersionPre1_9() {
        final Optional<String> dockerVersion = Optional.of("1.6.0");
        final SyslogRedirectingContainerDecorator decorator = new SyslogRedirectingContainerDecorator(SyslogRedirectingContainerDecoratorTest.SYSLOG_HOST_PORT);
        final HostConfig.Builder hostBuilder = HostConfig.builder();
        decorator.decorateHostConfig(SyslogRedirectingContainerDecoratorTest.JOB, dockerVersion, hostBuilder);
        final ContainerConfig.Builder containerBuilder = ContainerConfig.builder();
        decorator.decorateContainerConfig(SyslogRedirectingContainerDecoratorTest.JOB, imageInfo, dockerVersion, containerBuilder);
        final ContainerConfig containerConfig = containerBuilder.build();
        Assert.assertThat(containerConfig.entrypoint(), Matchers.hasItem("/helios/syslog-redirector"));
        final HostConfig hostConfig = hostBuilder.build();
        Assert.assertNull(hostConfig.logConfig());
        Assert.assertFalse(hostConfig.binds().isEmpty());
    }

    @Test
    public void testWithDockerVersionPost1_9() {
        final Optional<String> dockerVersion = Optional.of("1.12.1");
        final SyslogRedirectingContainerDecorator decorator = new SyslogRedirectingContainerDecorator(SyslogRedirectingContainerDecoratorTest.SYSLOG_HOST_PORT);
        final HostConfig.Builder hostBuilder = HostConfig.builder();
        decorator.decorateHostConfig(SyslogRedirectingContainerDecoratorTest.JOB, dockerVersion, hostBuilder);
        final ContainerConfig.Builder containerBuilder = ContainerConfig.builder();
        decorator.decorateContainerConfig(SyslogRedirectingContainerDecoratorTest.JOB, imageInfo, dockerVersion, containerBuilder);
        final ContainerConfig containerConfig = containerBuilder.build();
        Assert.assertThat(containerConfig.entrypoint(), Matchers.not(Matchers.hasItem("/helios/syslog-redirector")));
        final HostConfig hostConfig = hostBuilder.build();
        final LogConfig logConfig = hostConfig.logConfig();
        Assert.assertEquals("syslog", logConfig.logType());
        Assert.assertEquals(SyslogRedirectingContainerDecoratorTest.JOB.getId().toString(), logConfig.logOptions().get("tag"));
        Assert.assertEquals(("udp://" + (SyslogRedirectingContainerDecoratorTest.SYSLOG_HOST_PORT)), logConfig.logOptions().get("syslog-address"));
    }
}

