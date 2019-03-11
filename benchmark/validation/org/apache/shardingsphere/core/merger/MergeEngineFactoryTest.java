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
package org.apache.shardingsphere.core.merger;


import DatabaseType.MySQL;
import java.sql.SQLException;
import java.util.List;
import org.apache.shardingsphere.core.merger.dal.DALMergeEngine;
import org.apache.shardingsphere.core.merger.dql.DQLMergeEngine;
import org.apache.shardingsphere.core.parsing.parser.sql.SQLStatement;
import org.apache.shardingsphere.core.parsing.parser.sql.dal.DALStatement;
import org.apache.shardingsphere.core.parsing.parser.sql.dml.insert.InsertStatement;
import org.apache.shardingsphere.core.parsing.parser.sql.dql.select.SelectStatement;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class MergeEngineFactoryTest {
    private List<QueryResult> queryResults;

    @Test
    public void assertNewInstanceWithSelectStatement() throws SQLException {
        SQLStatement selectStatement = new SelectStatement();
        Assert.assertThat(MergeEngineFactory.newInstance(MySQL, null, selectStatement, null, queryResults), CoreMatchers.instanceOf(DQLMergeEngine.class));
    }

    @Test
    public void assertNewInstanceWithDALStatement() throws SQLException {
        SQLStatement dalStatement = new DALStatement();
        Assert.assertThat(MergeEngineFactory.newInstance(MySQL, null, dalStatement, null, queryResults), CoreMatchers.instanceOf(DALMergeEngine.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void assertNewInstanceWithOtherStatement() throws SQLException {
        SQLStatement insertStatement = new InsertStatement();
        MergeEngineFactory.newInstance(MySQL, null, insertStatement, null, queryResults);
    }
}

