/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.rest.server.resources.streaming;


import OutputNode.LimitHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.engine.KsqlEngine;
import io.confluent.ksql.planner.plan.OutputNode;
import io.confluent.ksql.util.KsqlException;
import io.confluent.ksql.util.QueuedQueryMetadata;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.streams.KeyValue;
import org.easymock.Capture;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.MockType;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author andy
created 19/04/2018
 */
@SuppressWarnings({ "unchecked", "ConstantConditions" })
@RunWith(EasyMockRunner.class)
public class QueryStreamWriterTest {
    @Rule
    public final Timeout timeout = Timeout.builder().withTimeout(30, TimeUnit.SECONDS).withLookingForStuckThread(true).build();

    @Mock(MockType.NICE)
    private KsqlEngine ksqlEngine;

    @Mock(MockType.NICE)
    private QueuedQueryMetadata queryMetadata;

    @Mock(MockType.NICE)
    private BlockingQueue<KeyValue<String, GenericRow>> rowQueue;

    private Capture<Thread.UncaughtExceptionHandler> ehCapture;

    private Capture<Collection<KeyValue<String, GenericRow>>> drainCapture;

    private Capture<OutputNode.LimitHandler> limitHandlerCapture;

    private QueryStreamWriter writer;

    private ByteArrayOutputStream out;

    private LimitHandler limitHandler;

    private ObjectMapper objectMapper;

    @Test
    public void shouldWriteAnyPendingRowsBeforeReportingException() throws Exception {
        // Given:
        expect(queryMetadata.isRunning()).andReturn(true).anyTimes();
        expect(rowQueue.drainTo(capture(drainCapture))).andAnswer(rows("Row1", "Row2", "Row3"));
        createWriter();
        givenUncaughtException(new KsqlException("Server went Boom"));
        // When:
        writer.write(out);
        // Then:
        final List<String> lines = QueryStreamWriterTest.getOutput(out);
        MatcherAssert.assertThat(lines, CoreMatchers.hasItems(CoreMatchers.containsString("Row1"), CoreMatchers.containsString("Row2"), CoreMatchers.containsString("Row3")));
    }

    @Test
    public void shouldExitAndDrainIfQueryStopsRunning() throws Exception {
        // Given:
        expect(queryMetadata.isRunning()).andReturn(true).andReturn(false);
        expect(rowQueue.drainTo(capture(drainCapture))).andAnswer(rows("Row1", "Row2", "Row3"));
        createWriter();
        // When:
        writer.write(out);
        // Then:
        final List<String> lines = QueryStreamWriterTest.getOutput(out);
        MatcherAssert.assertThat(lines, CoreMatchers.hasItems(CoreMatchers.containsString("Row1"), CoreMatchers.containsString("Row2"), CoreMatchers.containsString("Row3")));
    }

    @Test
    public void shouldExitAndDrainIfLimitReached() throws Exception {
        // Given:
        expect(queryMetadata.isRunning()).andReturn(true).anyTimes();
        expect(rowQueue.drainTo(capture(drainCapture))).andAnswer(rows("Row1", "Row2", "Row3"));
        createWriter();
        limitHandler.limitReached();
        // When:
        writer.write(out);
        // Then:
        final List<String> lines = QueryStreamWriterTest.getOutput(out);
        MatcherAssert.assertThat(lines, CoreMatchers.hasItems(CoreMatchers.containsString("Row1"), CoreMatchers.containsString("Row2"), CoreMatchers.containsString("Row3")));
    }
}

