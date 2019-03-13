/**
 * Copyright 2015 Netflix, Inc.
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
package com.netflix.discovery.shared.resolver.aws;


import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tomasz Bak
 */
public class ZoneAffinityClusterResolverTest {
    @Test
    public void testApplicationZoneIsFirstOnTheList() throws Exception {
        List<AwsEndpoint> endpoints = SampleCluster.merge(SampleCluster.UsEast1a, SampleCluster.UsEast1b, SampleCluster.UsEast1c);
        ZoneAffinityClusterResolver resolver = new ZoneAffinityClusterResolver(new com.netflix.discovery.shared.resolver.StaticClusterResolver("regionA", endpoints), "us-east-1b", true);
        List<AwsEndpoint> result = resolver.getClusterEndpoints();
        Assert.assertThat(result.size(), CoreMatchers.is(CoreMatchers.equalTo(endpoints.size())));
        Assert.assertThat(result.get(0).getZone(), CoreMatchers.is(CoreMatchers.equalTo("us-east-1b")));
    }

    @Test
    public void testAntiAffinity() throws Exception {
        List<AwsEndpoint> endpoints = SampleCluster.merge(SampleCluster.UsEast1a, SampleCluster.UsEast1b);
        ZoneAffinityClusterResolver resolver = new ZoneAffinityClusterResolver(new com.netflix.discovery.shared.resolver.StaticClusterResolver("regionA", endpoints), "us-east-1b", false);
        List<AwsEndpoint> result = resolver.getClusterEndpoints();
        Assert.assertThat(result.size(), CoreMatchers.is(CoreMatchers.equalTo(endpoints.size())));
        Assert.assertThat(result.get(0).getZone(), CoreMatchers.is(CoreMatchers.equalTo("us-east-1a")));
    }

    @Test
    public void testUnrecognizedZoneIsIgnored() throws Exception {
        List<AwsEndpoint> endpoints = SampleCluster.merge(SampleCluster.UsEast1a, SampleCluster.UsEast1b);
        ZoneAffinityClusterResolver resolver = new ZoneAffinityClusterResolver(new com.netflix.discovery.shared.resolver.StaticClusterResolver("regionA", endpoints), "us-east-1c", true);
        List<AwsEndpoint> result = resolver.getClusterEndpoints();
        Assert.assertThat(result.size(), CoreMatchers.is(CoreMatchers.equalTo(endpoints.size())));
    }
}

