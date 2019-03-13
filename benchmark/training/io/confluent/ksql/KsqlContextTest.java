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
package io.confluent.ksql;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.KsqlExecutionContext.ExecuteResult;
import io.confluent.ksql.engine.KsqlEngine;
import io.confluent.ksql.parser.KsqlParser.ParsedStatement;
import io.confluent.ksql.parser.KsqlParser.PreparedStatement;
import io.confluent.ksql.parser.SqlBaseParser.SingleStatementContext;
import io.confluent.ksql.parser.tree.Statement;
import io.confluent.ksql.schema.inference.SchemaInjector;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.KsqlException;
import io.confluent.ksql.util.PersistentQueryMetadata;
import io.confluent.ksql.util.QueuedQueryMetadata;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class KsqlContextTest {
    private static final KsqlConfig SOME_CONFIG = new KsqlConfig(Collections.emptyMap());

    private static final ImmutableMap<String, Object> SOME_PROPERTIES = ImmutableMap.of("overridden", "props");

    private static final ParsedStatement PARSED_STMT_0 = ParsedStatement.of("sql 0", Mockito.mock(SingleStatementContext.class));

    private static final ParsedStatement PARSED_STMT_1 = ParsedStatement.of("sql 1", Mockito.mock(SingleStatementContext.class));

    private static final PreparedStatement<?> PREPARED_STMT_0 = PreparedStatement.of("sql 0", Mockito.mock(Statement.class));

    private static final PreparedStatement<?> PREPARED_STMT_1 = PreparedStatement.of("sql 1", Mockito.mock(Statement.class));

    private static final PreparedStatement<?> STMT_0_WITH_SCHEMA = PreparedStatement.of("sql 0", Mockito.mock(Statement.class));

    private static final PreparedStatement<?> STMT_1_WITH_SCHEMA = PreparedStatement.of("sql 1", Mockito.mock(Statement.class));

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private ServiceContext serviceContext;

    @Mock
    private KsqlEngine ksqlEngine;

    @Mock
    private KsqlExecutionContext sandbox;

    @Mock
    private PersistentQueryMetadata persistentQuery;

    @Mock
    private QueuedQueryMetadata transientQuery;

    @Mock
    private SchemaInjector schemaInjector;

    private KsqlContext ksqlContext;

    @Test
    public void shouldParseStatements() {
        // When:
        ksqlContext.sql("Some SQL", KsqlContextTest.SOME_PROPERTIES);
        // Then:
        Mockito.verify(ksqlEngine).parse("Some SQL");
    }

    @Test
    public void shouldOnlyPrepareNextStatementOncePreviousStatementHasBeenExecuted() {
        // Given:
        Mockito.when(ksqlEngine.parse(ArgumentMatchers.any())).thenReturn(ImmutableList.of(KsqlContextTest.PARSED_STMT_0, KsqlContextTest.PARSED_STMT_1));
        // When:
        ksqlContext.sql("Some SQL", KsqlContextTest.SOME_PROPERTIES);
        // Then:
        final InOrder inOrder = Mockito.inOrder(ksqlEngine);
        inOrder.verify(ksqlEngine).prepare(KsqlContextTest.PARSED_STMT_0);
        inOrder.verify(ksqlEngine).execute(ArgumentMatchers.eq(KsqlContextTest.STMT_0_WITH_SCHEMA), ArgumentMatchers.any(), ArgumentMatchers.any());
        inOrder.verify(ksqlEngine).prepare(KsqlContextTest.PARSED_STMT_1);
        inOrder.verify(ksqlEngine).execute(ArgumentMatchers.eq(KsqlContextTest.STMT_1_WITH_SCHEMA), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void shouldTryExecuteStatementsReturnedByParserBeforeExecute() {
        // Given:
        Mockito.when(ksqlEngine.parse(ArgumentMatchers.any())).thenReturn(ImmutableList.of(KsqlContextTest.PARSED_STMT_0, KsqlContextTest.PARSED_STMT_1));
        // When:
        ksqlContext.sql("Some SQL", KsqlContextTest.SOME_PROPERTIES);
        // Then:
        final InOrder inOrder = Mockito.inOrder(ksqlEngine, sandbox);
        inOrder.verify(sandbox).execute(ArgumentMatchers.eq(KsqlContextTest.STMT_0_WITH_SCHEMA), ArgumentMatchers.any(), ArgumentMatchers.any());
        inOrder.verify(sandbox).execute(ArgumentMatchers.eq(KsqlContextTest.STMT_1_WITH_SCHEMA), ArgumentMatchers.any(), ArgumentMatchers.any());
        inOrder.verify(ksqlEngine).execute(ArgumentMatchers.eq(KsqlContextTest.STMT_0_WITH_SCHEMA), ArgumentMatchers.any(), ArgumentMatchers.any());
        inOrder.verify(ksqlEngine).execute(ArgumentMatchers.eq(KsqlContextTest.STMT_1_WITH_SCHEMA), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void shouldThrowIfParseFails() {
        // Given:
        Mockito.when(ksqlEngine.parse(ArgumentMatchers.any())).thenThrow(new KsqlException("Bad tings happen"));
        // Expect
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Bad tings happen");
        // When:
        ksqlContext.sql("Some SQL", KsqlContextTest.SOME_PROPERTIES);
    }

    @Test
    public void shouldThrowIfSandboxExecuteThrows() {
        // Given:
        Mockito.when(sandbox.execute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new KsqlException("Bad tings happen"));
        // Expect
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Bad tings happen");
        // When:
        ksqlContext.sql("Some SQL", KsqlContextTest.SOME_PROPERTIES);
    }

    @Test
    public void shouldThrowIfExecuteThrows() {
        // Given:
        Mockito.when(ksqlEngine.execute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new KsqlException("Bad tings happen"));
        // Expect
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Bad tings happen");
        // When:
        ksqlContext.sql("Some SQL", KsqlContextTest.SOME_PROPERTIES);
    }

    @Test
    public void shouldNotExecuteAnyStatementsIfTryExecuteThrows() {
        // Given:
        Mockito.when(sandbox.execute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new KsqlException("Bad tings happen"));
        // When:
        try {
            ksqlContext.sql("Some SQL", KsqlContextTest.SOME_PROPERTIES);
        } catch (final KsqlException e) {
            // expected
        }
        // Then:
        Mockito.verify(ksqlEngine, Mockito.never()).execute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void shouldStartPersistentQueries() {
        // Given:
        Mockito.when(ksqlEngine.execute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(ExecuteResult.of(persistentQuery));
        // When:
        ksqlContext.sql("Some SQL", KsqlContextTest.SOME_PROPERTIES);
        // Then:
        Mockito.verify(persistentQuery).start();
    }

    @Test
    public void shouldNotBlowUpOnSqlThatDoesNotResultInPersistentQueries() {
        // Given:
        Mockito.when(ksqlEngine.execute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(ExecuteResult.of(transientQuery));
        // When:
        ksqlContext.sql("Some SQL", KsqlContextTest.SOME_PROPERTIES);
        // Then:
        // Did not blow up.
    }

    @Test
    public void shouldCloseEngineBeforeServiceContextOnClose() {
        // When:
        ksqlContext.close();
        // Then:
        final InOrder inOrder = Mockito.inOrder(ksqlEngine, serviceContext);
        inOrder.verify(ksqlEngine).close();
        inOrder.verify(serviceContext).close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldInferSchema() {
        // Given:
        Mockito.when(schemaInjector.forStatement(ArgumentMatchers.any())).thenReturn(((PreparedStatement) (KsqlContextTest.STMT_0_WITH_SCHEMA)));
        // When:
        ksqlContext.sql("Some SQL", KsqlContextTest.SOME_PROPERTIES);
        // Then:
        Mockito.verify(ksqlEngine).execute(ArgumentMatchers.eq(KsqlContextTest.STMT_0_WITH_SCHEMA), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void shouldThrowIfFailedToInferSchema() {
        // Given:
        Mockito.when(schemaInjector.forStatement(ArgumentMatchers.any())).thenThrow(new RuntimeException("Boom"));
        // Then:
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Boom");
        // When:
        ksqlContext.sql("Some SQL", KsqlContextTest.SOME_PROPERTIES);
    }
}

