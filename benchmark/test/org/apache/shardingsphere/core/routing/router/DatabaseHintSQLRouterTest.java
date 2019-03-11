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
package org.apache.shardingsphere.core.routing.router;


import java.util.Collections;
import org.apache.shardingsphere.api.hint.HintManager;
import org.apache.shardingsphere.core.parsing.parser.sql.dql.DQLStatement;
import org.apache.shardingsphere.core.parsing.parser.sql.dql.select.SelectStatement;
import org.apache.shardingsphere.core.routing.router.sharding.DatabaseHintSQLRouter;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class DatabaseHintSQLRouterTest {
    private final HintManager hintManager = HintManager.getInstance();

    private DatabaseHintSQLRouter databaseHintSQLRouter;

    @Test
    public void assertParse() {
        Assert.assertThat(databaseHintSQLRouter.parse("select t from table t", false), CoreMatchers.instanceOf(SelectStatement.class));
    }

    @Test
    public void assertRoute() {
        hintManager.addDatabaseShardingValue("", 1);
        Assert.assertNotNull(databaseHintSQLRouter.route("select t from table t", Collections.emptyList(), new DQLStatement()));
    }
}

