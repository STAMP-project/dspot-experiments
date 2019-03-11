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


import com.google.common.collect.ImmutableSet;
import io.crate.data.RowConsumer;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import org.hamcrest.Matchers;
import org.junit.Test;


public class DistributingConsumerFactoryTest extends CrateDummyClusterServiceUnitTest {
    private DistributingConsumerFactory rowDownstreamFactory;

    @Test
    public void testCreateDownstreamOneNode() throws Exception {
        RowConsumer downstream = createDownstream(ImmutableSet.of("downstream_node"));
        assertThat(downstream, Matchers.instanceOf(DistributingConsumer.class));
        assertThat(((DistributingConsumer) (downstream)).multiBucketBuilder, Matchers.instanceOf(BroadcastingBucketBuilder.class));
    }

    @Test
    public void testCreateDownstreamMultipleNode() throws Exception {
        RowConsumer downstream = createDownstream(ImmutableSet.of("downstream_node1", "downstream_node2"));
        assertThat(((DistributingConsumer) (downstream)).multiBucketBuilder, Matchers.instanceOf(ModuloBucketBuilder.class));
    }
}

