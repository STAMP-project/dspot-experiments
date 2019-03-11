/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.execution.engine.distribution;


import io.crate.data.Row1;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class MultiBucketBuilderTest {
    private List<MultiBucketBuilder> builders = new ArrayList<>();

    @Test
    public void testBucketIsEmptyAfterSecondBuildBucket() throws Exception {
        StreamBucket[] buckets = new StreamBucket[1];
        for (MultiBucketBuilder builder : builders) {
            builder.add(new Row1(42));
            builder.build(buckets);
            Assert.assertThat(buckets[0].size(), Is.is(1));
            builder.build(buckets);
            Assert.assertThat(buckets[0].size(), Is.is(0));
        }
    }

    @Test
    public void testSizeIsResetOnBuildBuckets() throws Exception {
        StreamBucket[] buckets = new StreamBucket[1];
        for (MultiBucketBuilder builder : builders) {
            builder.add(new Row1(42));
            builder.add(new Row1(42));
            Assert.assertThat(builder.size(), Is.is(2));
            builder.build(buckets);
            Assert.assertThat(buckets[0].size(), Is.is(2));
            Assert.assertThat(builder.size(), Is.is(0));
        }
    }
}

