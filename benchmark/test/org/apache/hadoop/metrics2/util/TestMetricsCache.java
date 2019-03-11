/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.metrics2.util;


import MetricsCache.Record;
import java.util.Arrays;
import java.util.Collection;
import org.apache.hadoop.metrics2.AbstractMetric;
import org.apache.hadoop.metrics2.MetricsRecord;
import org.apache.hadoop.metrics2.MetricsTag;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static MetricsCache.MAX_RECS_PER_NAME_DEFAULT;


public class TestMetricsCache {
    private static final Logger LOG = LoggerFactory.getLogger(TestMetricsCache.class);

    @SuppressWarnings("deprecation")
    @Test
    public void testUpdate() {
        MetricsCache cache = new MetricsCache();
        MetricsRecord mr = makeRecord("r", Arrays.asList(makeTag("t", "tv")), Arrays.asList(makeMetric("m", 0), makeMetric("m1", 1)));
        MetricsCache.Record cr = cache.update(mr);
        Mockito.verify(mr).name();
        Mockito.verify(mr).tags();
        Mockito.verify(mr).metrics();
        Assert.assertEquals("same record size", cr.metrics().size(), ((Collection<AbstractMetric>) (mr.metrics())).size());
        Assert.assertEquals("same metric value", 0, cr.getMetric("m"));
        MetricsRecord mr2 = makeRecord("r", Arrays.asList(makeTag("t", "tv")), Arrays.asList(makeMetric("m", 2), makeMetric("m2", 42)));
        cr = cache.update(mr2);
        Assert.assertEquals("contains 3 metric", 3, cr.metrics().size());
        checkMetricValue("updated metric value", cr, "m", 2);
        checkMetricValue("old metric value", cr, "m1", 1);
        checkMetricValue("new metric value", cr, "m2", 42);
        MetricsRecord mr3 = // different tag value
        makeRecord("r", Arrays.asList(makeTag("t", "tv3")), Arrays.asList(makeMetric("m3", 3)));
        cr = cache.update(mr3);// should get a new record

        Assert.assertEquals("contains 1 metric", 1, cr.metrics().size());
        checkMetricValue("updated metric value", cr, "m3", 3);
        // tags cache should be empty so far
        Assert.assertEquals("no tags", 0, cr.tags().size());
        // until now
        cr = cache.update(mr3, true);
        Assert.assertEquals("Got 1 tag", 1, cr.tags().size());
        Assert.assertEquals("Tag value", "tv3", cr.getTag("t"));
        checkMetricValue("Metric value", cr, "m3", 3);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGet() {
        MetricsCache cache = new MetricsCache();
        Assert.assertNull("empty", cache.get("r", Arrays.asList(makeTag("t", "t"))));
        MetricsRecord mr = makeRecord("r", Arrays.asList(makeTag("t", "t")), Arrays.asList(makeMetric("m", 1)));
        cache.update(mr);
        MetricsCache.Record cr = cache.get("r", mr.tags());
        TestMetricsCache.LOG.debug(((("tags=" + (mr.tags())) + " cr=") + cr));
        Assert.assertNotNull("Got record", cr);
        Assert.assertEquals("contains 1 metric", 1, cr.metrics().size());
        checkMetricValue("new metric value", cr, "m", 1);
    }

    /**
     * Make sure metrics tag has a sane hashCode impl
     */
    @Test
    public void testNullTag() {
        MetricsCache cache = new MetricsCache();
        MetricsRecord mr = makeRecord("r", Arrays.asList(makeTag("t", null)), Arrays.asList(makeMetric("m", 0), makeMetric("m1", 1)));
        MetricsCache.Record cr = cache.update(mr);
        Assert.assertTrue("t value should be null", (null == (cr.getTag("t"))));
    }

    @Test
    public void testOverflow() {
        MetricsCache cache = new MetricsCache();
        MetricsCache.Record cr;
        Collection<MetricsTag> t0 = Arrays.asList(makeTag("t0", "0"));
        for (int i = 0; i < ((MAX_RECS_PER_NAME_DEFAULT) + 1); ++i) {
            cr = cache.update(makeRecord("r", Arrays.asList(makeTag(("t" + i), ("" + i))), Arrays.asList(makeMetric("m", i))));
            checkMetricValue("new metric value", cr, "m", i);
            if (i < (MAX_RECS_PER_NAME_DEFAULT)) {
                Assert.assertNotNull("t0 is still there", cache.get("r", t0));
            }
        }
        Assert.assertNull("t0 is gone", cache.get("r", t0));
    }
}

