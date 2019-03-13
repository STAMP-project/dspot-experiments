/**
 * -
 * -\-\-
 * Helios Client
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
package com.spotify.helios.client;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.spotify.helios.common.descriptors.Job;
import com.spotify.helios.common.descriptors.JobId;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class HeliosClientTest {
    private final RequestDispatcher dispatcher = Mockito.mock(RequestDispatcher.class);

    private final HeliosClient client = new HeliosClient("test", dispatcher);

    @Test(expected = IllegalStateException.class)
    public void testBuildWithNoEndpoints() {
        HeliosClient.newBuilder().setEndpoints(Collections.<URI>emptyList()).build();
    }

    @Test
    public void listHosts() throws Exception {
        final List<String> hosts = ImmutableList.of("foo1", "foo2", "foo3");
        mockResponse("GET", HeliosClientTest.hasPath("/hosts/"), HeliosClientTest.response("GET", 200, hosts));
        Assert.assertThat(client.listHosts().get(), Matchers.equalTo(hosts));
    }

    @Test
    public void listHostsFilterByNamePattern() throws Exception {
        final List<String> hosts = ImmutableList.of("foo1", "foo2", "foo3");
        mockResponse("GET", Matchers.allOf(HeliosClientTest.hasPath("/hosts/"), HeliosClientTest.containsQuery("namePattern=foo")), HeliosClientTest.response("GET", 200, hosts));
        Assert.assertThat(client.listHosts("foo").get(), Matchers.equalTo(hosts));
    }

    @Test
    public void listHostsFilterBySelectors() throws Exception {
        final List<String> hosts = ImmutableList.of("foo1", "foo2", "foo3");
        mockResponse("GET", Matchers.allOf(HeliosClientTest.hasPath("/hosts/"), HeliosClientTest.containsQuery("selector=foo%3Dbar"), HeliosClientTest.containsQuery("selector=site%3Dabc")), HeliosClientTest.response("GET", 200, hosts));
        final Set<String> selectors = ImmutableSet.of("foo=bar", "site=abc");
        Assert.assertThat(client.listHosts(selectors).get(), Matchers.equalTo(hosts));
    }

    @Test
    public void listJobs() throws Exception {
        final Map<JobId, Job> jobs = HeliosClientTest.fakeJobs(JobId.parse("foobar:v1"));
        mockResponse("GET", HeliosClientTest.hasPath("/jobs"), HeliosClientTest.response("GET", 200, jobs));
        Assert.assertThat(client.jobs().get(), Matchers.is(jobs));
    }

    @Test
    public void listJobsWithJobFilter() throws Exception {
        final Map<JobId, Job> jobs = HeliosClientTest.fakeJobs(JobId.parse("foobar:v1"));
        mockResponse("GET", Matchers.allOf(HeliosClientTest.hasPath("/jobs"), HeliosClientTest.containsQuery("q=foo")), HeliosClientTest.response("GET", 200, jobs));
        Assert.assertThat(client.jobs("foo").get(), Matchers.is(jobs));
    }

    @Test
    public void listJobsWithJobAndHostFilter() throws Exception {
        final Map<JobId, Job> jobs = HeliosClientTest.fakeJobs(JobId.parse("foobar:v1"));
        mockResponse("GET", Matchers.allOf(HeliosClientTest.hasPath("/jobs"), HeliosClientTest.containsQuery("q=foo"), HeliosClientTest.containsQuery("hostPattern=bar")), HeliosClientTest.response("GET", 200, jobs));
        Assert.assertThat(client.jobs("foo", "bar").get(), Matchers.is(jobs));
    }
}

