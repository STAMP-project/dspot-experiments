/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.core.statement;


import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.junit.Rule;
import org.junit.Test;


public class TestSqlLoggerCallPoints {
    private static final String CREATE = "create table foo(bar int primary key not null)";

    private static final String INSERT = "insert into foo(bar) values(1)";

    private static final String INSERT_NULL = "insert into foo(bar) values(null)";

    private static final String INSERT_PREPARED = "insert into foo(bar) values(?)";

    // TODO can apparently be changed to > 0 with jdk9
    private static final Predicate<Long> IS_POSITIVE = ( x) -> x >= 0;

    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule();

    private Handle h;

    private TestSqlLoggerCallPoints.TalkativeSqlLogger logger;

    @Test
    public void testStatement() {
        h.execute(TestSqlLoggerCallPoints.CREATE);
        h.createUpdate(TestSqlLoggerCallPoints.INSERT).execute();
        assertThat(logger.getRawSql()).containsExactly(TestSqlLoggerCallPoints.CREATE, TestSqlLoggerCallPoints.CREATE, TestSqlLoggerCallPoints.INSERT, TestSqlLoggerCallPoints.INSERT);
        assertThat(logger.getTimings()).hasSize(2).allMatch(TestSqlLoggerCallPoints.IS_POSITIVE);
        assertThat(logger.getExceptions()).isEmpty();
    }

    @Test
    public void testStatementException() {
        h.execute(TestSqlLoggerCallPoints.CREATE);
        Throwable e = catchThrowable(h.createUpdate(TestSqlLoggerCallPoints.INSERT_NULL)::execute);
        assertThat(e).isInstanceOf(RuntimeException.class).hasCauseInstanceOf(SQLException.class);
        assertThat(logger.getRawSql()).containsExactly(TestSqlLoggerCallPoints.CREATE, TestSqlLoggerCallPoints.CREATE, TestSqlLoggerCallPoints.INSERT_NULL, TestSqlLoggerCallPoints.INSERT_NULL);
        assertThat(logger.getTimings()).hasSize(2).allMatch(TestSqlLoggerCallPoints.IS_POSITIVE);
        assertThat(logger.getExceptions()).containsExactly(((SQLException) (e.getCause())));
    }

    @Test
    public void testBatch() {
        h.execute(TestSqlLoggerCallPoints.CREATE);
        h.createBatch().add(TestSqlLoggerCallPoints.INSERT).execute();
        // unfortunately...
        assertThat(logger.getRawSql()).containsExactly(TestSqlLoggerCallPoints.CREATE, TestSqlLoggerCallPoints.CREATE, null, null);
        assertThat(logger.getTimings()).hasSize(2).allMatch(TestSqlLoggerCallPoints.IS_POSITIVE);
        assertThat(logger.getExceptions()).isEmpty();
    }

    @Test
    public void testBatchException() {
        h.execute(TestSqlLoggerCallPoints.CREATE);
        Throwable e = catchThrowable(h.createBatch().add(TestSqlLoggerCallPoints.INSERT_NULL)::execute);
        // unfortunately...
        assertThat(logger.getRawSql()).containsExactly(TestSqlLoggerCallPoints.CREATE, TestSqlLoggerCallPoints.CREATE, null, null);
        assertThat(logger.getTimings()).hasSize(2).allMatch(TestSqlLoggerCallPoints.IS_POSITIVE);
        assertThat(logger.getExceptions()).containsExactly(((SQLException) (e.getCause())));
    }

    @Test
    public void testPreparedBatch() {
        h.execute(TestSqlLoggerCallPoints.CREATE);
        h.prepareBatch(TestSqlLoggerCallPoints.INSERT_PREPARED).bind(0, 1).execute();
        assertThat(logger.getRawSql()).containsExactly(TestSqlLoggerCallPoints.CREATE, TestSqlLoggerCallPoints.CREATE, TestSqlLoggerCallPoints.INSERT_PREPARED, TestSqlLoggerCallPoints.INSERT_PREPARED);
        assertThat(logger.getTimings()).hasSize(2).allMatch(TestSqlLoggerCallPoints.IS_POSITIVE);
        assertThat(logger.getExceptions()).isEmpty();
    }

    @Test
    public void testPreparedBatchException() {
        h.execute(TestSqlLoggerCallPoints.CREATE);
        Throwable e = catchThrowable(h.prepareBatch(TestSqlLoggerCallPoints.INSERT_PREPARED).bindByType(0, null, Integer.class)::execute);
        assertThat(logger.getRawSql()).containsExactly(TestSqlLoggerCallPoints.CREATE, TestSqlLoggerCallPoints.CREATE, TestSqlLoggerCallPoints.INSERT_PREPARED, TestSqlLoggerCallPoints.INSERT_PREPARED);
        assertThat(logger.getTimings()).hasSize(2).allMatch(TestSqlLoggerCallPoints.IS_POSITIVE);
        assertThat(logger.getExceptions()).containsExactly(((SQLException) (e.getCause())));
    }

    @Test
    public void testNotSql() {
        String query = "herp derp";
        assertThatThrownBy(() -> h.execute(query)).isNotNull();
        assertThat(logger.getRawSql()).isEmpty();
        assertThat(logger.getTimings()).isEmpty();
        assertThat(logger.getExceptions()).isEmpty();
    }

    private static class TalkativeSqlLogger implements SqlLogger {
        private final List<String> rawSql = new ArrayList<>();

        private final List<Long> timings = new ArrayList<>();

        private final List<SQLException> exceptions = new ArrayList<>();

        @Override
        public void logBeforeExecution(StatementContext context) {
            rawSql.add(context.getRawSql());
        }

        @Override
        public void logAfterExecution(StatementContext context) {
            rawSql.add(context.getRawSql());
            timings.add(context.getElapsedTime(ChronoUnit.NANOS));
        }

        @Override
        public void logException(StatementContext context, SQLException ex) {
            rawSql.add(context.getRawSql());
            exceptions.add(ex);
            timings.add(context.getElapsedTime(ChronoUnit.NANOS));
        }

        public List<String> getRawSql() {
            return rawSql;
        }

        public List<Long> getTimings() {
            return timings;
        }

        public List<SQLException> getExceptions() {
            return exceptions;
        }
    }
}

