/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights
 * Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is
 * distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either
 * express or implied. See the License for the specific language
 * governing
 * permissions and limitations under the License.
 */
package com.amazonaws.metrics;


import Field.ClientExecuteTime;
import Field.Exception;
import RequestMetricCollector.NONE;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

import static RequestMetricCollector.NONE;


public class AwsSdkMetricsTest {
    /**
     * By default the AWS SDK metric collection is disabled. Enabling it should
     * fail unless the necessary CloudWatch related jars are on the classpath.
     * Therefore, this test is expected to fail in enabling the default metric
     * collection, but have absolutely no impact otherwise.
     */
    @Test
    public void enableDefaultMetrics() {
        Assert.assertFalse(AwsSdkMetrics.enableDefaultMetrics());
    }

    @Test
    public void test() {
        // by default, it's disabled
        Assert.assertFalse(AwsSdkMetrics.isDefaultMetricsEnabled());
        // won't be anble to enable unless the default impl library is on the classpath
        Assert.assertFalse(AwsSdkMetrics.enableDefaultMetrics());
        Assert.assertFalse(AwsSdkMetrics.isDefaultMetricsEnabled());
        Assert.assertSame(NONE, AwsSdkMetrics.getRequestMetricCollector());
        Assert.assertFalse(AwsSdkMetrics.isDefaultMetricsEnabled());
        // effectively no effect
        AwsSdkMetrics.disableMetrics();
        Assert.assertFalse(AwsSdkMetrics.isDefaultMetricsEnabled());
    }

    @Test
    public void defaultMetricTypes() {
        // Default set of predefined metric types is not empty
        Set<MetricType> set = AwsSdkMetrics.getPredefinedMetrics();
        Assert.assertNotNull(set);
        Assert.assertTrue(((set.size()) > 0));
        // Clear out the default set of predefined metric types
        AwsSdkMetrics.set(Collections.<MetricType>emptyList());
        Set<MetricType> empty = AwsSdkMetrics.getPredefinedMetrics();
        Assert.assertNotNull(empty);
        Assert.assertTrue(((empty.size()) == 0));
        // Reconfigure the default set of predefined metric types back to the original
        AwsSdkMetrics.set(set);
        Set<MetricType> set2 = AwsSdkMetrics.getPredefinedMetrics();
        Assert.assertNotNull(set2);
        Assert.assertTrue(((set2.size()) > 0));
        // Not the same due to ensuring thread-safety
        Assert.assertNotSame(set, set2);
    }

    @Test
    public void setNullOrEmpty() {
        Set<MetricType> orig = AwsSdkMetrics.getPredefinedMetrics();
        Assert.assertTrue(((orig.size()) > 0));
        AwsSdkMetrics.set(null);
        Set<MetricType> empty = AwsSdkMetrics.getPredefinedMetrics();
        Assert.assertTrue(((empty.size()) == 0));
        AwsSdkMetrics.set(null);
        Set<MetricType> stillEmpty = AwsSdkMetrics.getPredefinedMetrics();
        Assert.assertSame(empty, stillEmpty);
        AwsSdkMetrics.set(Collections.<MetricType>emptySet());
        Set<MetricType> empty3 = AwsSdkMetrics.getPredefinedMetrics();
        Assert.assertSame(empty, empty3);
        AwsSdkMetrics.set(orig);
    }

    @Test
    public void addNull() {
        Assert.assertFalse(AwsSdkMetrics.add(null));
    }

    @Test
    public void addAllNull() {
        Assert.assertFalse(AwsSdkMetrics.addAll(null));
        Assert.assertFalse(AwsSdkMetrics.addAll(Collections.<MetricType>emptyList()));
    }

    @Test
    public void removeNull() {
        Assert.assertFalse(AwsSdkMetrics.remove(null));
    }

    @Test
    public void addAndRemove() {
        Set<MetricType> orig = AwsSdkMetrics.getPredefinedMetrics();
        AwsSdkMetrics.set(null);
        // Test add and remove
        Assert.assertTrue(AwsSdkMetrics.getPredefinedMetrics().isEmpty());
        AwsSdkMetrics.add(ClientExecuteTime);
        Assert.assertFalse(AwsSdkMetrics.getPredefinedMetrics().isEmpty());
        AwsSdkMetrics.remove(ClientExecuteTime);
        Assert.assertTrue(AwsSdkMetrics.getPredefinedMetrics().isEmpty());
        // Test add more than one entry
        AwsSdkMetrics.add(ClientExecuteTime);
        AwsSdkMetrics.add(Exception);
        Assert.assertTrue(((AwsSdkMetrics.getPredefinedMetrics().size()) == 2));
        AwsSdkMetrics.set(null);
        Assert.assertTrue(AwsSdkMetrics.getPredefinedMetrics().isEmpty());
        // Test addAll
        AwsSdkMetrics.addAll(Arrays.asList(Exception, ClientExecuteTime));
        Assert.assertTrue(((AwsSdkMetrics.getPredefinedMetrics().size()) == 2));
        AwsSdkMetrics.set(orig);
        Assert.assertTrue(((AwsSdkMetrics.getPredefinedMetrics().size()) == (orig.size())));
    }

    @Test
    public void setJvmMetricsExcluded() {
        final boolean b = AwsSdkMetrics.isMachineMetricExcluded();
        AwsSdkMetrics.setMachineMetricsExcluded(b);
        Assert.assertTrue((b == (AwsSdkMetrics.isMachineMetricExcluded())));
        AwsSdkMetrics.setMachineMetricsExcluded((!b));
        Assert.assertFalse((b == (AwsSdkMetrics.isMachineMetricExcluded())));
        AwsSdkMetrics.setMachineMetricsExcluded(b);
        Assert.assertTrue((b == (AwsSdkMetrics.isMachineMetricExcluded())));
    }

    @Test
    public void setPerHostMetricsIncluded() {
        final boolean b = AwsSdkMetrics.isPerHostMetricIncluded();
        AwsSdkMetrics.setPerHostMetricsIncluded(b);
        Assert.assertTrue((b == (AwsSdkMetrics.isPerHostMetricIncluded())));
        AwsSdkMetrics.setPerHostMetricsIncluded((!b));
        Assert.assertFalse((b == (AwsSdkMetrics.isPerHostMetricIncluded())));
        AwsSdkMetrics.setPerHostMetricsIncluded(b);
        Assert.assertTrue((b == (AwsSdkMetrics.isPerHostMetricIncluded())));
    }

    @Test
    public void testEnableHttpSocketReadMetric() {
        Assert.assertFalse(AwsSdkMetrics.isHttpSocketReadMetricEnabled());
        AwsSdkMetrics.enableHttpSocketReadMetric();
        Assert.assertTrue(AwsSdkMetrics.isHttpSocketReadMetricEnabled());
    }

    @Test
    public void isMetricEnabled() {
        // originally disabled
        Assert.assertFalse(AwsSdkMetrics.isMetricsEnabled());
        // set to NONE, so still disabled
        AwsSdkMetrics.setMetricCollector(MetricCollector.NONE);
        Assert.assertFalse(AwsSdkMetrics.isMetricsEnabled());
        // set to a custom collector, so now considered enabled
        AwsSdkMetrics.setMetricCollector(new MetricCollector() {
            @Override
            public boolean start() {
                return true;
            }

            @Override
            public boolean stop() {
                return false;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public RequestMetricCollector getRequestMetricCollector() {
                return NONE;
            }

            @Override
            public ServiceMetricCollector getServiceMetricCollector() {
                return ServiceMetricCollector.NONE;
            }
        });
        Assert.assertTrue(AwsSdkMetrics.isMetricsEnabled());
    }

    @Test
    public void setRegion_WithoutRegionsEnum() {
        AwsSdkMetrics.setRegion("us-east-1");
        Assert.assertTrue(AwsSdkMetrics.getRegionName().equals("us-east-1"));
    }

    @Test
    public void setRegion_WithNonStandardRegion() {
        AwsSdkMetrics.setRegion("us-east-9");
        Assert.assertTrue(AwsSdkMetrics.getRegionName().equals("us-east-9"));
    }

    @Test
    public void setRegions_WhenRegionDoesNotExist_DefaultsToAwsPartition() {
        AwsSdkMetrics.setRegion("non-existent-region");
        Assert.assertEquals("non-existent-region", AwsSdkMetrics.getRegionName());
    }
}

