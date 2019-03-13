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
package com.netflix.discovery.shared.resolver;


import com.netflix.discovery.shared.resolver.aws.AwsEndpoint;
import com.netflix.discovery.shared.resolver.aws.SampleCluster;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tomasz Bak
 */
public class ResolverUtilsTest {
    @Test
    public void testSplitByZone() throws Exception {
        List<AwsEndpoint> endpoints = SampleCluster.merge(SampleCluster.UsEast1a, SampleCluster.UsEast1b, SampleCluster.UsEast1c);
        List<AwsEndpoint>[] parts = ResolverUtils.splitByZone(endpoints, "us-east-1b");
        List<AwsEndpoint> myZoneServers = parts[0];
        List<AwsEndpoint> remainingServers = parts[1];
        Assert.assertThat(myZoneServers, CoreMatchers.is(CoreMatchers.equalTo(SampleCluster.UsEast1b.build())));
        Assert.assertThat(remainingServers, CoreMatchers.is(CoreMatchers.equalTo(SampleCluster.merge(SampleCluster.UsEast1a, SampleCluster.UsEast1c))));
    }

    @Test
    public void testExtractZoneFromHostName() throws Exception {
        Assert.assertThat(ResolverUtils.extractZoneFromHostName("us-east-1c.myservice.net"), CoreMatchers.is(CoreMatchers.equalTo("us-east-1c")));
        Assert.assertThat(ResolverUtils.extractZoneFromHostName("txt.us-east-1c.myservice.net"), CoreMatchers.is(CoreMatchers.equalTo("us-east-1c")));
    }

    @Test
    public void testIdentical() throws Exception {
        List<AwsEndpoint> firstList = SampleCluster.UsEast1a.builder().withServerPool(10).build();
        List<AwsEndpoint> secondList = ResolverUtils.randomize(firstList);
        Assert.assertThat(ResolverUtils.identical(firstList, secondList), CoreMatchers.is(true));
        secondList.set(0, SampleCluster.UsEast1b.build().get(0));
        Assert.assertThat(ResolverUtils.identical(firstList, secondList), CoreMatchers.is(false));
    }
}

