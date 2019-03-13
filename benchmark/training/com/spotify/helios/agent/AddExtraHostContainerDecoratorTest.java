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
import com.google.common.collect.ImmutableList;
import com.spotify.docker.client.messages.HostConfig;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class AddExtraHostContainerDecoratorTest {
    @Test
    public void simpleTest() {
        final List<String> hosts = ImmutableList.of("one:one", "two:two");
        final AddExtraHostContainerDecorator decorator = new AddExtraHostContainerDecorator(hosts);
        final HostConfig.Builder hostBuilder = HostConfig.builder();
        decorator.decorateHostConfig(null, Optional.absent(), hostBuilder);
        final HostConfig config = hostBuilder.build();
        Assert.assertThat(config.extraHosts(), Matchers.equalTo(hosts));
    }

    @Test
    public void invalidArgument() {
        Assert.assertThat(AddExtraHostContainerDecorator.isValidArg("abcdefg"), Matchers.is(false));
    }

    @Test
    public void invalidArguments() {
        Assert.assertThat(AddExtraHostContainerDecorator.isValidArg("abc:def:gh"), Matchers.is(false));
    }

    @Test
    public void testValidArgument() {
        Assert.assertThat(AddExtraHostContainerDecorator.isValidArg("foo:169.254.169.254"), Matchers.is(true));
    }
}

