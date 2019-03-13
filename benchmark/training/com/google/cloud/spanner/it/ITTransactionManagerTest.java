/**
 * Copyright 2017 Google LLC
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


import TransactionState.ABORTED;
import TransactionState.COMMITTED;
import TransactionState.COMMIT_FAILED;
import TransactionState.ROLLED_BACK;
import TransactionState.STARTED;
import com.google.cloud.spanner.AbortedException;
import com.google.cloud.spanner.Database;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.IntegrationTest;
import com.google.cloud.spanner.IntegrationTestEnv;
import com.google.cloud.spanner.Key;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.SpannerException;
import com.google.cloud.spanner.Struct;
import com.google.cloud.spanner.TransactionContext;
import com.google.cloud.spanner.TransactionManager;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@Category(IntegrationTest.class)
@RunWith(JUnit4.class)
public class ITTransactionManagerTest {
    @ClassRule
    public static IntegrationTestEnv env = new IntegrationTestEnv();

    private static Database db;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static DatabaseClient client;

    @Test
    public void simpleInsert() {
        TransactionManager manager = ITTransactionManagerTest.client.transactionManager();
        TransactionContext txn = manager.begin();
        assertThat(manager.getState()).isEqualTo(STARTED);
        txn.buffer(Mutation.newInsertBuilder("T").set("K").to("Key1").set("BoolValue").to(true).build());
        manager.commit();
        assertThat(manager.getState()).isEqualTo(COMMITTED);
        Struct row = ITTransactionManagerTest.client.singleUse().readRow("T", Key.of("Key1"), Arrays.asList("K", "BoolValue"));
        assertThat(row.getString(0)).isEqualTo("Key1");
        assertThat(row.getBoolean(1)).isTrue();
    }

    @Test
    public void invalidInsert() {
        TransactionManager manager = ITTransactionManagerTest.client.transactionManager();
        TransactionContext txn = manager.begin();
        txn.buffer(Mutation.newInsertBuilder("InvalidTable").set("K").to("Key1").set("BoolValue").to(true).build());
        try {
            manager.commit();
            Assert.fail("Expected exception");
        } catch (SpannerException e) {
            // expected
        }
        assertThat(manager.getState()).isEqualTo(COMMIT_FAILED);
        // We cannot retry for non aborted errors.
        expectedException.expect(IllegalStateException.class);
        manager.resetForRetry();
    }

    @Test
    public void rollback() {
        TransactionManager manager = ITTransactionManagerTest.client.transactionManager();
        TransactionContext txn = manager.begin();
        txn.buffer(Mutation.newInsertBuilder("T").set("K").to("Key2").set("BoolValue").to(true).build());
        manager.rollback();
        assertThat(manager.getState()).isEqualTo(ROLLED_BACK);
        // Row should not have been inserted.
        assertThat(ITTransactionManagerTest.client.singleUse().readRow("T", Key.of("Key2"), Arrays.asList("K", "BoolValue"))).isNull();
    }

    @Test
    public void abortAndRetry() {
        ITTransactionManagerTest.client.write(Arrays.asList(Mutation.newInsertBuilder("T").set("K").to("Key3").set("BoolValue").to(true).build()));
        TransactionManager manager1 = ITTransactionManagerTest.client.transactionManager();
        TransactionContext txn1 = manager1.begin();
        txn1.readRow("T", Key.of("Key3"), Arrays.asList("K", "BoolValue"));
        TransactionManager manager2 = ITTransactionManagerTest.client.transactionManager();
        TransactionContext txn2 = manager2.begin();
        txn2.readRow("T", Key.of("Key3"), Arrays.asList("K", "BoolValue"));
        txn1.buffer(Mutation.newUpdateBuilder("T").set("K").to("Key3").set("BoolValue").to(false).build());
        manager1.commit();
        // txn2 should have been aborted.
        try {
            manager2.commit();
            Assert.fail("Expected to abort");
        } catch (AbortedException e) {
            assertThat(manager2.getState()).isEqualTo(ABORTED);
            txn2 = manager2.resetForRetry();
        }
        txn2.buffer(Mutation.newUpdateBuilder("T").set("K").to("Key3").set("BoolValue").to(true).build());
        manager2.commit();
        Struct row = ITTransactionManagerTest.client.singleUse().readRow("T", Key.of("Key3"), Arrays.asList("K", "BoolValue"));
        assertThat(row.getString(0)).isEqualTo("Key3");
        assertThat(row.getBoolean(1)).isTrue();
    }
}

