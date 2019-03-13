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
package org.apache.shardingsphere.core.parsing.antlr.rule.registry;


import DatabaseType.H2;
import DatabaseType.MySQL;
import org.apache.shardingsphere.core.parsing.antlr.sql.segment.SQLSegment;
import org.apache.shardingsphere.core.parsing.antlr.sql.segment.table.TableSegment;
import org.junit.Assert;
import org.junit.Test;


public final class ParsingRuleRegistryTest {
    @Test
    public void assertFindSQLStatementRule() {
        Assert.assertTrue(ShardingParsingRuleRegistry.getInstance().findSQLStatementRule(MySQL, "CreateTableContext").isPresent());
    }

    @Test
    public void assertNotFindSQLStatementRule() {
        Assert.assertFalse(ShardingParsingRuleRegistry.getInstance().findSQLStatementRule(MySQL, "Invalid").isPresent());
    }

    @Test
    public void assertFindSQLStatementRuleWithH2() {
        Assert.assertTrue(ShardingParsingRuleRegistry.getInstance().findSQLStatementRule(H2, "CreateTableContext").isPresent());
    }

    @Test
    public void assertFindSQLStatementFiller() {
        Assert.assertTrue(ShardingParsingRuleRegistry.getInstance().findSQLSegmentFiller(MySQL, TableSegment.class).isPresent());
    }

    @Test
    public void assertNotFindSQLStatementFiller() {
        Assert.assertFalse(ShardingParsingRuleRegistry.getInstance().findSQLSegmentFiller(MySQL, SQLSegment.class).isPresent());
    }
}

