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
package com.spotify.helios.master;


import com.google.common.collect.ImmutableMap;
import com.spotify.helios.common.descriptors.DeploymentGroup;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class HostMatcherTest {
    private final Map<String, Map<String, String>> hosts = ImmutableMap.of("foo-a1", ImmutableMap.of("role", "foo"), "foo-a2", ImmutableMap.of("role", "foo", "special", "yes"), "bar-a1", ImmutableMap.of("role", "bar", "pool", "a"), "bar-b1", ImmutableMap.of("role", "bar", "pool", "b"), "bar-c1", ImmutableMap.of("role", "bar", "pool", "c"));

    private final HostMatcher matcher = new HostMatcher(hosts);

    @Test
    public void testHostMatcher() {
        Assert.assertThat(matcher.getMatchingHosts(HostMatcherTest.group("role=foo")), Matchers.contains("foo-a1", "foo-a2"));
        Assert.assertThat(matcher.getMatchingHosts(HostMatcherTest.group("role=foo", "special=yes")), Matchers.contains("foo-a2"));
        // does not match foo-a1 because it has no 'special' label
        Assert.assertThat(matcher.getMatchingHosts(HostMatcherTest.group("role=foo", "special!=yes")), Matchers.empty());
        Assert.assertThat(matcher.getMatchingHosts(HostMatcherTest.group("pool=a")), Matchers.contains("bar-a1"));
        Assert.assertThat(matcher.getMatchingHosts(HostMatcherTest.group("pool=a", "role=bar")), Matchers.contains("bar-a1"));
        Assert.assertThat(matcher.getMatchingHosts(HostMatcherTest.group("pool=b")), Matchers.contains("bar-b1"));
        Assert.assertThat(matcher.getMatchingHosts(HostMatcherTest.group("pool=b", "role=bar")), Matchers.contains("bar-b1"));
        Assert.assertThat(matcher.getMatchingHosts(HostMatcherTest.group("pool=c")), Matchers.contains("bar-c1"));
        Assert.assertThat(matcher.getMatchingHosts(HostMatcherTest.group("pool=c", "role=bar")), Matchers.contains("bar-c1"));
        // groups where some selectors match hosts but others do not, should return no matches
        Assert.assertThat(matcher.getMatchingHosts(HostMatcherTest.group("pool=c", "role=awesome")), Matchers.empty());
        Assert.assertThat(matcher.getMatchingHosts(HostMatcherTest.group("special=yes", "role=awesome")), Matchers.empty());
    }

    @Test
    public void testDeploymentGroupWithNoSelectors() {
        final DeploymentGroup deploymentGroup = HostMatcherTest.group();
        Assert.assertThat(matcher.getMatchingHosts(deploymentGroup), Matchers.empty());
    }
}

