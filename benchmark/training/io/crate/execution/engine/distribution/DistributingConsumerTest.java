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


import DataTypes.INTEGER;
import io.crate.Streamer;
import io.crate.data.CollectionBucket;
import io.crate.data.Row;
import io.crate.execution.jobs.DistResultRXTask;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.BatchSimulatingIterator;
import io.crate.testing.FailingBatchIterator;
import io.crate.testing.TestingBatchIterators;
import io.crate.testing.TestingHelpers;
import io.crate.testing.TestingRowConsumer;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DistributingConsumerTest extends CrateUnitTest {
    private Logger logger = LogManager.getLogger(DistributingConsumer.class);

    private ExecutorService executorService;

    @Test
    public void testSendUsingDistributingConsumerAndReceiveWithDistResultRXTask() throws Exception {
        try {
            Streamer<?>[] streamers = new Streamer<?>[]{ INTEGER.streamer() };
            TestingRowConsumer collectingConsumer = new TestingRowConsumer();
            DistResultRXTask distResultRXTask = createPageDownstreamContext(streamers, collectingConsumer);
            TransportDistributedResultAction distributedResultAction = createFakeTransport(streamers, distResultRXTask);
            DistributingConsumer distributingConsumer = createDistributingConsumer(streamers, distributedResultAction);
            BatchSimulatingIterator<Row> batchSimulatingIterator = new BatchSimulatingIterator(TestingBatchIterators.range(0, 5), 2, 3, executorService);
            distributingConsumer.accept(batchSimulatingIterator, null);
            List<Object[]> result = collectingConsumer.getResult();
            assertThat(TestingHelpers.printedTable(new CollectionBucket(result)), Matchers.is(("0\n" + ((("1\n" + "2\n") + "3\n") + "4\n"))));
            // pageSize=2 and 5 rows causes 3x pushResult
            Mockito.verify(distributedResultAction, Mockito.times(3)).pushResult(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any());
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testDistributingConsumerForwardsFailure() throws Exception {
        Streamer<?>[] streamers = new Streamer<?>[]{ INTEGER.streamer() };
        TestingRowConsumer collectingConsumer = new TestingRowConsumer();
        DistResultRXTask distResultRXTask = createPageDownstreamContext(streamers, collectingConsumer);
        TransportDistributedResultAction distributedResultAction = createFakeTransport(streamers, distResultRXTask);
        DistributingConsumer distributingConsumer = createDistributingConsumer(streamers, distributedResultAction);
        distributingConsumer.accept(null, new CompletionException(new IllegalArgumentException("foobar")));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("foobar");
        collectingConsumer.getResult();
    }

    @Test
    public void testFailureOnAllLoadedIsForwarded() throws Exception {
        Streamer<?>[] streamers = new Streamer<?>[]{ INTEGER.streamer() };
        TestingRowConsumer collectingConsumer = new TestingRowConsumer();
        DistResultRXTask distResultRXTask = createPageDownstreamContext(streamers, collectingConsumer);
        TransportDistributedResultAction distributedResultAction = createFakeTransport(streamers, distResultRXTask);
        DistributingConsumer distributingConsumer = createDistributingConsumer(streamers, distributedResultAction);
        distributingConsumer.accept(FailingBatchIterator.failOnAllLoaded(), null);
        expectedException.expect(InterruptedException.class);
        collectingConsumer.getResult();
    }
}

