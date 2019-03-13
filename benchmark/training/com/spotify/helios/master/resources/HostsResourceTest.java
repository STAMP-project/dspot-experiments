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
package com.spotify.helios.master.resources;


import Response.Status.BAD_REQUEST;
import com.google.common.collect.ImmutableList;
import com.spotify.helios.master.MasterModel;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class HostsResourceTest {
    private static final List<String> NO_SELECTOR_ARG = Collections.emptyList();

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private final MasterModel model = Mockito.mock(MasterModel.class);

    private final HostsResource resource = new HostsResource(model);

    private final ImmutableList<String> hosts = ImmutableList.of("host1.foo.example.com", "host2.foo.example.com", "host3.foo.example.com", "host4.foo.example.com");

    @Test
    public void listHosts() {
        Assert.assertThat(resource.list(null, HostsResourceTest.NO_SELECTOR_ARG), Matchers.equalTo(hosts));
    }

    @Test
    public void listHostsNameFilter() {
        Mockito.when(model.listHosts("foo.example")).thenReturn(hosts);
        Assert.assertThat(resource.list("foo.example", HostsResourceTest.NO_SELECTOR_ARG), Matchers.equalTo(hosts));
        Mockito.when(model.listHosts("host1")).thenReturn(ImmutableList.of("host1.foo.example.com"));
        Assert.assertThat(resource.list("host1", HostsResourceTest.NO_SELECTOR_ARG), Matchers.contains("host1.foo.example.com"));
        Mockito.when(model.listHosts("host5")).thenReturn(ImmutableList.of());
        Assert.assertThat(resource.list("host5", HostsResourceTest.NO_SELECTOR_ARG), Matchers.empty());
    }

    @Test
    public void listHostsSelectorFilter() {
        Assert.assertThat(resource.list(null, ImmutableList.of("site=foo")), Matchers.equalTo(hosts));
        Assert.assertThat(resource.list(null, ImmutableList.of("site=bar")), Matchers.empty());
        Assert.assertThat(resource.list(null, ImmutableList.of("site!=foo")), Matchers.empty());
        Assert.assertThat(resource.list(null, ImmutableList.of("index in (1,2)")), Matchers.contains("host1.foo.example.com", "host2.foo.example.com"));
        Assert.assertThat(resource.list(null, ImmutableList.of("site=foo", "index in (1,2)")), Matchers.contains("host1.foo.example.com", "host2.foo.example.com"));
    }

    @Test
    public void listHostsSelectorFilterMissingStatus() {
        Mockito.when(model.getHostLabels(hosts.get(0))).thenReturn(Collections.emptyMap());
        Assert.assertThat(resource.list(null, ImmutableList.of("site=foo")), Matchers.equalTo(hosts.subList(1, hosts.size())));
        Assert.assertThat(resource.list(null, ImmutableList.of("site=bar")), Matchers.empty());
        Assert.assertThat(resource.list(null, ImmutableList.of("site!=foo")), Matchers.empty());
        Assert.assertThat(resource.list(null, ImmutableList.of("index in (1,2)")), Matchers.contains("host2.foo.example.com"));
        Assert.assertThat(resource.list(null, ImmutableList.of("site=foo", "index in (1,2)")), Matchers.contains("host2.foo.example.com"));
    }

    // Test behavior when both a name pattern and selector list is specified.
    @Test
    public void listHostsNameAndSelectorFilter() {
        Mockito.when(model.listHosts("foo.example.com")).thenReturn(hosts);
        Assert.assertThat(resource.list("foo.example.com", ImmutableList.of("site=foo")), Matchers.equalTo(hosts));
        Mockito.when(model.listHosts("host3")).thenReturn(ImmutableList.of("host3.foo.example.com"));
        Assert.assertThat(resource.list("host3", ImmutableList.of("index =2")), Matchers.empty());
        Assert.assertThat(resource.list("host3", ImmutableList.of("index!=2")), Matchers.contains("host3.foo.example.com"));
    }

    @Test
    public void listHostsInvalidHostSelectorSyntax() {
        exception.expect(WebApplicationException.class);
        exception.expect(HostsResourceTest.hasStatus(BAD_REQUEST));
        resource.list(null, ImmutableList.of("foo <@> bar"));
    }
}

