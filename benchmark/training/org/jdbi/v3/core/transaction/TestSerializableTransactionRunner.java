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
package org.jdbi.v3.core.transaction;


import TransactionIsolationLevel.SERIALIZABLE;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;


public class TestSerializableTransactionRunner {
    private static final int MAX_RETRIES = 5;

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Mock
    private Consumer<List<Exception>> onFailure;

    @Mock
    private Consumer<List<Exception>> onSuccess;

    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule();

    @Test
    public void testEventuallyFails() {
        final AtomicInteger attempts = new AtomicInteger(0);
        Handle handle = dbRule.getJdbi().open();
        assertThatExceptionOfType(SQLException.class).isThrownBy(() -> handle.inTransaction(TransactionIsolationLevel.SERIALIZABLE, ( conn) -> {
            attempts.incrementAndGet();
            throw new SQLException("serialization", "40001", attempts.get());
        })).satisfies(( e) -> assertThat(e.getSQLState()).isEqualTo("40001")).satisfies(( e) -> assertThat(e.getSuppressed()).hasSize(MAX_RETRIES).describedAs("suppressed are ordered reverse chronologically, like a stack").isSortedAccordingTo(Comparator.comparing(( ex) -> ((SQLException) (ex)).getErrorCode()).reversed())).describedAs("thrown exception is chronologically last").satisfies(( e) -> assertThat(e.getErrorCode()).isEqualTo(((((SQLException) (e.getSuppressed()[0])).getErrorCode()) + 1)));
        assertThat(attempts.get()).isEqualTo((1 + (TestSerializableTransactionRunner.MAX_RETRIES)));
    }

    @Test
    public void testEventuallySucceeds() throws Exception {
        final AtomicInteger remaining = new AtomicInteger(((TestSerializableTransactionRunner.MAX_RETRIES) / 2));
        Handle handle = dbRule.getJdbi().open();
        handle.inTransaction(SERIALIZABLE, ( conn) -> {
            if ((remaining.decrementAndGet()) == 0) {
                return null;
            }
            throw new SQLException("serialization", "40001");
        });
        assertThat(remaining.get()).isZero();
    }

    @Test
    public void testNonsenseRetryCount() {
        assertThatThrownBy(() -> dbRule.getJdbi().configure(.class, ( config) -> config.setMaxRetries((-1)))).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Set a number >= 0");
    }

    @Test
    public void testFailureAndSuccessCallback() throws SQLException {
        AtomicInteger remainingAttempts = new AtomicInteger(TestSerializableTransactionRunner.MAX_RETRIES);
        AtomicInteger expectedExceptions = new AtomicInteger(1);
        Mockito.doAnswer(( invocation) -> {
            assertThat(((List<Exception>) (invocation.getArgument(0)))).hasSize(expectedExceptions.getAndIncrement()).describedAs("ordered chronologically").isSortedAccordingTo(Comparator.comparing(( e) -> ((SQLException) (e)).getErrorCode()));
            return null;
        }).when(onFailure).accept(ArgumentMatchers.anyList());
        Mockito.doAnswer(( invocation) -> {
            assertThat(((List<Exception>) (invocation.getArgument(0)))).hasSize(((TestSerializableTransactionRunner.MAX_RETRIES) - 1)).describedAs("ordered chronologically").isSortedAccordingTo(Comparator.comparing(( e) -> ((SQLException) (e)).getErrorCode()));
            return null;
        }).when(onSuccess).accept(ArgumentMatchers.anyList());
        dbRule.getJdbi().open().inTransaction(SERIALIZABLE, ( conn) -> {
            if ((remainingAttempts.decrementAndGet()) == 0) {
                return null;
            }
            // use vendor error code as order number
            throw new SQLException("serialization", "40001", expectedExceptions.get());
        });
        assertThat(remainingAttempts.get()).isZero();
        Mockito.verify(onFailure, Mockito.times(((TestSerializableTransactionRunner.MAX_RETRIES) - 1))).accept(ArgumentMatchers.anyList());
        Mockito.verifyNoMoreInteractions(onFailure);
        Mockito.verify(onSuccess, Mockito.times(1)).accept(ArgumentMatchers.anyList());
        Mockito.verifyNoMoreInteractions(onSuccess);
        assertThat(expectedExceptions.get()).isEqualTo(TestSerializableTransactionRunner.MAX_RETRIES);
    }
}

