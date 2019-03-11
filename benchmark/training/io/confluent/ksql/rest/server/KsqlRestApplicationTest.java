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
package io.confluent.ksql.rest.server;


import ProcessingLogConfig.STREAM_AUTO_CREATE;
import RestConfig.LISTENERS_CONFIG;
import io.confluent.ksql.KsqlExecutionContext;
import io.confluent.ksql.engine.KsqlEngine;
import io.confluent.ksql.logging.processing.ProcessingLogConfig;
import io.confluent.ksql.parser.KsqlParser.PreparedStatement;
import io.confluent.ksql.rest.server.computation.CommandQueue;
import io.confluent.ksql.rest.server.computation.CommandRunner;
import io.confluent.ksql.rest.server.computation.QueuedCommandStatus;
import io.confluent.ksql.rest.server.resources.KsqlResource;
import io.confluent.ksql.rest.server.resources.RootDocument;
import io.confluent.ksql.rest.server.resources.StatusResource;
import io.confluent.ksql.rest.server.resources.streaming.StreamedQueryResource;
import io.confluent.ksql.rest.util.ProcessingLogServerUtils;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.KsqlException;
import io.confluent.ksql.version.metrics.VersionCheckerAgent;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class KsqlRestApplicationTest {
    private static final String LOG_STREAM_NAME = "log_stream";

    private static final String LOG_TOPIC_NAME = "log_topic";

    private final KsqlRestConfig restConfig = new KsqlRestConfig(Collections.singletonMap(LISTENERS_CONFIG, "http://localhost:8088"));

    @Mock
    private ServiceContext serviceContext;

    @Mock
    private KsqlEngine ksqlEngine;

    @Mock
    private KsqlExecutionContext sandBox;

    @Mock
    private KsqlConfig ksqlConfig;

    @Mock
    private ProcessingLogConfig processingLogConfig;

    @Mock
    private CommandRunner commandRunner;

    @Mock
    private RootDocument rootDocument;

    @Mock
    private StatusResource statusResource;

    @Mock
    private StreamedQueryResource streamedQueryResource;

    @Mock
    private KsqlResource ksqlResource;

    @Mock
    private VersionCheckerAgent versionCheckerAgent;

    @Mock
    private CommandQueue commandQueue;

    @Mock
    private QueuedCommandStatus queuedCommandStatus;

    private KsqlRestApplication app;

    @Test
    public void shouldCloseServiceContextOnClose() {
        // When:
        app.stop();
        // Then:
        Mockito.verify(serviceContext).close();
    }

    @Test
    public void shouldCreateLogStream() {
        // When:
        KsqlRestApplication.maybeCreateProcessingLogStream(processingLogConfig, ksqlConfig, ksqlEngine, commandQueue);
        // Then:
        Mockito.verify(commandQueue).isEmpty();
        final PreparedStatement<?> statement = ProcessingLogServerUtils.processingLogStreamCreateStatement(processingLogConfig, ksqlConfig);
        Mockito.verify(sandBox).execute(statement, ksqlConfig, Collections.emptyMap());
        Mockito.verify(commandQueue).enqueueCommand(statement, ksqlConfig, Collections.emptyMap());
    }

    @Test
    public void shouldNotCreateLogStreamIfAutoCreateNotConfigured() {
        // Given:
        Mockito.when(processingLogConfig.getBoolean(STREAM_AUTO_CREATE)).thenReturn(false);
        // When:
        KsqlRestApplication.maybeCreateProcessingLogStream(processingLogConfig, ksqlConfig, ksqlEngine, commandQueue);
        // Then:
        Mockito.verifyNoMoreInteractions(ksqlEngine, commandQueue);
    }

    @Test
    public void shouldOnlyCreateLogStreamIfCommandTopicEmpty() {
        // Given:
        Mockito.when(commandQueue.isEmpty()).thenReturn(false);
        // When:
        KsqlRestApplication.maybeCreateProcessingLogStream(processingLogConfig, ksqlConfig, ksqlEngine, commandQueue);
        // Then:
        Mockito.verify(commandQueue, Mockito.never()).enqueueCommand(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void shouldNotCreateLogStreamIfValidationFails() {
        // Given:
        Mockito.when(sandBox.execute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new KsqlException("error"));
        // When:
        KsqlRestApplication.maybeCreateProcessingLogStream(processingLogConfig, ksqlConfig, ksqlEngine, commandQueue);
        // Then:
        Mockito.verify(commandQueue, Mockito.never()).enqueueCommand(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}

