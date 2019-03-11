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


import DataTypes.STRING;
import StreamBucket.Builder;
import io.crate.Streamer;
import io.crate.data.RowN;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.TestingHelpers;
import java.util.UUID;
import org.elasticsearch.common.breaker.NoopCircuitBreaker;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.hamcrest.Matchers;
import org.junit.Test;


public class DistributedResultRequestTest extends CrateUnitTest {
    @Test
    public void testStreaming() throws Exception {
        Streamer<?>[] streamers = new Streamer[]{ STRING.streamer() };
        UUID uuid = UUID.randomUUID();
        StreamBucket.Builder builder = new StreamBucket.Builder(streamers, new io.crate.breaker.RamAccountingContext("dummy", new NoopCircuitBreaker("dummy")));
        builder.add(new RowN(new Object[]{ "ab" }));
        builder.add(new RowN(new Object[]{ null }));
        builder.add(new RowN(new Object[]{ "cd" }));
        DistributedResultRequest r1 = new DistributedResultRequest(uuid, 1, ((byte) (3)), 1, builder.build(), false);
        BytesStreamOutput out = new BytesStreamOutput();
        r1.writeTo(out);
        StreamInput in = out.bytes().streamInput();
        DistributedResultRequest r2 = new DistributedResultRequest();
        r2.readFrom(in);
        assertEquals(r1.readRows(streamers).size(), r2.readRows(streamers).size());
        assertThat(r1.isLast(), Matchers.is(r2.isLast()));
        assertThat(r1.executionPhaseInputId(), Matchers.is(r2.executionPhaseInputId()));
        assertThat(r2.readRows(streamers), Matchers.contains(TestingHelpers.isRow("ab"), TestingHelpers.isNullRow(), TestingHelpers.isRow("cd")));
    }

    @Test
    public void testStreamingOfFailure() throws Exception {
        UUID uuid = UUID.randomUUID();
        Throwable throwable = new IllegalStateException("dummy");
        DistributedResultRequest r1 = new DistributedResultRequest(uuid, 1, ((byte) (3)), 1, throwable, true);
        BytesStreamOutput out = new BytesStreamOutput();
        r1.writeTo(out);
        StreamInput in = StreamInput.wrap(BytesReference.toBytes(out.bytes()));
        DistributedResultRequest r2 = new DistributedResultRequest();
        r2.readFrom(in);
        assertThat(r2.throwable(), Matchers.instanceOf(throwable.getClass()));
        assertThat(r2.isKilled(), Matchers.is(r1.isKilled()));
    }
}

