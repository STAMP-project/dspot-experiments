/**
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.spanner.it;


import ErrorCode.ALREADY_EXISTS;
import ErrorCode.INVALID_ARGUMENT;
import com.google.cloud.spanner.Database;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.IntegrationTest;
import com.google.cloud.spanner.IntegrationTestEnv;
import com.google.cloud.spanner.SpannerBatchUpdateException;
import com.google.cloud.spanner.SpannerException;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.TransactionContext;
import com.google.cloud.spanner.TransactionRunner;
import com.google.cloud.spanner.TransactionRunner.TransactionCallable;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Integration tests for DML.
 */
@Category(IntegrationTest.class)
@RunWith(JUnit4.class)
public final class ITBatchDmlTest {
    private static Database db;

    @ClassRule
    public static IntegrationTestEnv env = new IntegrationTestEnv();

    private static final String INSERT_DML = "INSERT INTO T (k, v) VALUES ('boo1', 1), ('boo2', 2), ('boo3', 3), ('boo4', 4);";

    private static final String UPDATE_DML = "UPDATE T SET T.V = 100 WHERE T.K LIKE 'boo%';";

    private static final String DELETE_DML = "DELETE FROM T WHERE T.K like 'boo%';";

    private static DatabaseClient client;

    @Test
    public void noStatementsInRequest() {
        final TransactionCallable<long[]> callable = new TransactionCallable<long[]>() {
            @Override
            public long[] run(TransactionContext transaction) {
                List<Statement> stmts = new ArrayList<>();
                long[] rowCounts;
                try {
                    rowCounts = transaction.batchUpdate(stmts);
                    Assert.fail("Expecting an exception.");
                } catch (SpannerException e) {
                    assertThat((e instanceof SpannerBatchUpdateException)).isFalse();
                    assertThat(e.getErrorCode()).isEqualTo(INVALID_ARGUMENT);
                    assertThat(e.getMessage()).contains("No statements in batch DML request.");
                    rowCounts = new long[0];
                }
                return rowCounts;
            }
        };
        TransactionRunner runner = ITBatchDmlTest.client.readWriteTransaction();
        long[] rowCounts = runner.run(callable);
        assertThat(rowCounts.length).isEqualTo(0);
    }

    @Test
    public void batchDml() {
        final TransactionCallable<long[]> callable = new TransactionCallable<long[]>() {
            @Override
            public long[] run(TransactionContext transaction) throws Exception {
                List<Statement> stmts = new ArrayList<>();
                stmts.add(Statement.of(ITBatchDmlTest.INSERT_DML));
                stmts.add(Statement.of(ITBatchDmlTest.UPDATE_DML));
                stmts.add(Statement.of(ITBatchDmlTest.DELETE_DML));
                return transaction.batchUpdate(stmts);
            }
        };
        TransactionRunner runner = ITBatchDmlTest.client.readWriteTransaction();
        long[] rowCounts = runner.run(callable);
        assertThat(rowCounts.length).isEqualTo(3);
        for (long rc : rowCounts) {
            assertThat(rc).isEqualTo(4);
        }
    }

    @Test
    public void mixedBatchDmlAndDml() {
        final TransactionCallable<long[]> callable = new TransactionCallable<long[]>() {
            @Override
            public long[] run(TransactionContext transaction) throws Exception {
                long rowCount = transaction.executeUpdate(Statement.of(ITBatchDmlTest.INSERT_DML));
                List<Statement> stmts = new ArrayList<>();
                stmts.add(Statement.of(ITBatchDmlTest.UPDATE_DML));
                stmts.add(Statement.of(ITBatchDmlTest.DELETE_DML));
                long[] batchRowCounts = transaction.batchUpdate(stmts);
                long[] rowCounts = new long[(batchRowCounts.length) + 1];
                System.arraycopy(batchRowCounts, 0, rowCounts, 0, batchRowCounts.length);
                rowCounts[batchRowCounts.length] = rowCount;
                return rowCounts;
            }
        };
        TransactionRunner runner = ITBatchDmlTest.client.readWriteTransaction();
        long[] rowCounts = runner.run(callable);
        assertThat(rowCounts.length).isEqualTo(3);
        for (long rc : rowCounts) {
            assertThat(rc).isEqualTo(4);
        }
    }

    @Test
    public void errorBatchDmlIllegalStatement() {
        final TransactionCallable<long[]> callable = new TransactionCallable<long[]>() {
            @Override
            public long[] run(TransactionContext transaction) {
                List<Statement> stmts = new ArrayList<>();
                stmts.add(Statement.of(ITBatchDmlTest.INSERT_DML));
                stmts.add(Statement.of("some illegal statement"));
                stmts.add(Statement.of(ITBatchDmlTest.UPDATE_DML));
                return transaction.batchUpdate(stmts);
            }
        };
        TransactionRunner runner = ITBatchDmlTest.client.readWriteTransaction();
        try {
            runner.run(callable);
            Assert.fail("Expecting an exception.");
        } catch (SpannerBatchUpdateException e) {
            assertThat(e.getErrorCode()).isEqualTo(INVALID_ARGUMENT);
            assertThat(e.getMessage()).contains("is not valid DML.");
            long[] rowCounts = e.getUpdateCounts();
            assertThat(rowCounts.length).isEqualTo(1);
            for (long rc : rowCounts) {
                assertThat(rc).isEqualTo(4);
            }
        }
    }

    @Test
    public void errorBatchDmlAlreadyExist() {
        final TransactionCallable<long[]> callable = new TransactionCallable<long[]>() {
            @Override
            public long[] run(TransactionContext transaction) {
                List<Statement> stmts = new ArrayList<>();
                stmts.add(Statement.of(ITBatchDmlTest.INSERT_DML));
                stmts.add(Statement.of(ITBatchDmlTest.INSERT_DML));// should fail

                stmts.add(Statement.of(ITBatchDmlTest.UPDATE_DML));
                return transaction.batchUpdate(stmts);
            }
        };
        TransactionRunner runner = ITBatchDmlTest.client.readWriteTransaction();
        try {
            runner.run(callable);
            Assert.fail("Expecting an exception.");
        } catch (SpannerBatchUpdateException e) {
            assertThat(e.getErrorCode()).isEqualTo(ALREADY_EXISTS);
            assertThat(e.getMessage()).contains("already exists");
            long[] rowCounts = e.getUpdateCounts();
            assertThat(rowCounts.length).isEqualTo(1);
            for (long rc : rowCounts) {
                assertThat(rc).isEqualTo(4);
            }
        }
    }
}

