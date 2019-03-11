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
package org.apache.shardingsphere.core.executor.sql.prepare;


import ConnectionMode.CONNECTION_STRICTLY;
import ConnectionMode.MEMORY_STRICTLY;
import java.sql.SQLException;
import java.util.Collection;
import org.apache.shardingsphere.core.executor.ShardingExecuteGroup;
import org.apache.shardingsphere.core.executor.StatementExecuteUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SQLExecutePrepareTemplateTest {
    private SQLExecutePrepareTemplate sqlExecutePrepareTemplate;

    @Mock
    private SQLExecutePrepareCallback callback;

    @Test
    public void assertGetExecuteUnitGroupForOneShardMemoryStrictly() throws SQLException {
        mockConnections(callback, MEMORY_STRICTLY, 1);
        sqlExecutePrepareTemplate = new SQLExecutePrepareTemplate(2);
        Collection<ShardingExecuteGroup<StatementExecuteUnit>> actual = sqlExecutePrepareTemplate.getExecuteUnitGroups(mockShardRouteUnit(1, 1), callback);
        Assert.assertThat(actual.size(), CoreMatchers.is(1));
        for (ShardingExecuteGroup<StatementExecuteUnit> each : actual) {
            Assert.assertThat(each.getInputs().size(), CoreMatchers.is(1));
        }
    }

    @Test
    public void assertGetExecuteUnitGroupForMultiShardConnectionStrictly() throws SQLException {
        mockConnections(callback, CONNECTION_STRICTLY, 1);
        sqlExecutePrepareTemplate = new SQLExecutePrepareTemplate(1);
        Collection<ShardingExecuteGroup<StatementExecuteUnit>> actual = sqlExecutePrepareTemplate.getExecuteUnitGroups(mockShardRouteUnit(10, 2), callback);
        Assert.assertThat(actual.size(), CoreMatchers.is(10));
        for (ShardingExecuteGroup<StatementExecuteUnit> each : actual) {
            Assert.assertThat(each.getInputs().size(), CoreMatchers.is(2));
        }
    }
}

