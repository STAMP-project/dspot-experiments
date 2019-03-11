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
package io.crate.execution.engine.fetch;


import com.carrotsearch.hppc.IntObjectMap;
import com.google.common.collect.Iterables;
import io.crate.Streamer;
import io.crate.breaker.RamAccountingContext;
import io.crate.execution.engine.distribution.StreamBucket;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.TestingHelpers;
import org.apache.logging.log4j.LogManager;
import org.elasticsearch.common.breaker.CircuitBreakingException;
import org.elasticsearch.common.breaker.NoopCircuitBreaker;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.junit.Test;


public class NodeFetchResponseTest extends CrateUnitTest {
    private IntObjectMap<Streamer[]> streamers;

    private IntObjectMap<StreamBucket> fetched;

    private long originalFlushBufferSize = RamAccountingContext.FLUSH_BUFFER_SIZE;

    private RamAccountingContext ramAccountingContext = new RamAccountingContext("dummy", new NoopCircuitBreaker("dummy"));

    @Test
    public void testStreaming() throws Exception {
        NodeFetchResponse orig = NodeFetchResponse.forSending(fetched);
        BytesStreamOutput out = new BytesStreamOutput();
        orig.writeTo(out);
        StreamInput in = out.bytes().streamInput();
        // receiving side is required to set the streamers
        NodeFetchResponse streamed = NodeFetchResponse.forReceiveing(streamers, ramAccountingContext);
        streamed.readFrom(in);
        assertThat(((io.crate.data.Row) (Iterables.getOnlyElement(streamed.fetched().get(1)))), TestingHelpers.isRow(true));
    }

    @Test
    public void testResponseCircuitBreaker() throws Exception {
        NodeFetchResponse orig = NodeFetchResponse.forSending(fetched);
        BytesStreamOutput out = new BytesStreamOutput();
        orig.writeTo(out);
        StreamInput in = out.bytes().streamInput();
        NodeFetchResponse nodeFetchResponse = NodeFetchResponse.forReceiveing(streamers, new RamAccountingContext("test", new org.elasticsearch.common.breaker.MemoryCircuitBreaker(new org.elasticsearch.common.unit.ByteSizeValue(2, ByteSizeUnit.BYTES), 1.0, LogManager.getLogger(NodeFetchResponseTest.class))));
        expectedException.expect(CircuitBreakingException.class);
        nodeFetchResponse.readFrom(in);
    }
}

