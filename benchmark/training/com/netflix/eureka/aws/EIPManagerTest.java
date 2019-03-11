/**
 * Copyright 2018 Netflix, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.netflix.eureka.aws;


import com.google.common.collect.Lists;
import com.netflix.discovery.EurekaClientConfig;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Joseph Witthuhn
 */
public class EIPManagerTest {
    private EurekaClientConfig config = Mockito.mock(EurekaClientConfig.class);

    private EIPManager eipManager;

    @Test
    public void shouldFilterNonElasticNames() {
        Mockito.when(config.getRegion()).thenReturn("us-east-1");
        List<String> hosts = Lists.newArrayList("example.com", "ec2-1-2-3-4.compute.amazonaws.com", "5.6.7.8", "ec2-101-202-33-44.compute.amazonaws.com");
        Mockito.when(config.getEurekaServerServiceUrls(ArgumentMatchers.any(String.class))).thenReturn(hosts);
        Collection<String> returnValue = eipManager.getCandidateEIPs("i-123", "us-east-1d");
        Assert.assertEquals(2, returnValue.size());
        Assert.assertTrue(returnValue.contains("1.2.3.4"));
        Assert.assertTrue(returnValue.contains("101.202.33.44"));
    }

    @Test
    public void shouldFilterNonElasticNamesInOtherRegion() {
        Mockito.when(config.getRegion()).thenReturn("eu-west-1");
        List<String> hosts = Lists.newArrayList("example.com", "ec2-1-2-3-4.eu-west-1.compute.amazonaws.com", "5.6.7.8", "ec2-101-202-33-44.eu-west-1.compute.amazonaws.com");
        Mockito.when(config.getEurekaServerServiceUrls(ArgumentMatchers.any(String.class))).thenReturn(hosts);
        Collection<String> returnValue = eipManager.getCandidateEIPs("i-123", "eu-west-1a");
        Assert.assertEquals(2, returnValue.size());
        Assert.assertTrue(returnValue.contains("1.2.3.4"));
        Assert.assertTrue(returnValue.contains("101.202.33.44"));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionWhenNoElasticNames() {
        Mockito.when(config.getRegion()).thenReturn("eu-west-1");
        List<String> hosts = Lists.newArrayList("example.com", "5.6.7.8");
        Mockito.when(config.getEurekaServerServiceUrls(ArgumentMatchers.any(String.class))).thenReturn(hosts);
        eipManager.getCandidateEIPs("i-123", "eu-west-1a");
    }
}

