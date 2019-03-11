/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.core.executor;


import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import lombok.RequiredArgsConstructor;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ShardingExecuteEngineTest {
    private ShardingExecuteEngine shardingExecuteEngine = new ShardingExecuteEngine(10);

    private Collection<ShardingExecuteGroup<StatementExecuteUnit>> inputGroups;

    private ShardingExecuteEngineTest.MockGroupExecuteCallback firstCallback;

    private ShardingExecuteEngineTest.MockGroupExecuteCallback callback;

    private CountDownLatch latch;

    @Test
    public void assertParallelExecuteWithoutFirstCallback() throws InterruptedException, SQLException {
        List<String> actual = shardingExecuteEngine.groupExecute(inputGroups, callback);
        latch.await();
        Assert.assertThat(actual.size(), CoreMatchers.is(4));
    }

    @Test
    public void assertParallelExecuteWithFirstCallback() throws InterruptedException, SQLException {
        List<String> actual = shardingExecuteEngine.groupExecute(inputGroups, firstCallback, callback, false);
        latch.await();
        Assert.assertThat(actual.size(), CoreMatchers.is(4));
    }

    @Test
    public void assertSerialExecute() throws InterruptedException, SQLException {
        List<String> actual = shardingExecuteEngine.groupExecute(inputGroups, firstCallback, callback, true);
        latch.await();
        Assert.assertThat(actual.size(), CoreMatchers.is(4));
    }

    @Test
    public void assertInputGroupIsEmpty() throws SQLException {
        CountDownLatch latch = new CountDownLatch(1);
        List<String> actual = shardingExecuteEngine.groupExecute(new LinkedList<ShardingExecuteGroup<StatementExecuteUnit>>(), new ShardingExecuteEngineTest.MockGroupExecuteCallback(latch));
        latch.countDown();
        Assert.assertThat(actual.size(), CoreMatchers.is(0));
    }

    @RequiredArgsConstructor
    private final class MockGroupExecuteCallback implements ShardingExecuteCallback<StatementExecuteUnit, String> , ShardingGroupExecuteCallback<StatementExecuteUnit, String> {
        private final CountDownLatch latch;

        @Override
        public String execute(final StatementExecuteUnit input, final boolean isTrunkThread, final Map<String, Object> shardingExecuteDataMap) {
            latch.countDown();
            return "succeed";
        }

        @Override
        public Collection<String> execute(final Collection<StatementExecuteUnit> inputs, final boolean isTrunkThread, final Map<String, Object> shardingExecuteDataMap) {
            List<String> result = new LinkedList<>();
            for (StatementExecuteUnit each : inputs) {
                latch.countDown();
                result.add("succeed");
            }
            return result;
        }
    }
}

