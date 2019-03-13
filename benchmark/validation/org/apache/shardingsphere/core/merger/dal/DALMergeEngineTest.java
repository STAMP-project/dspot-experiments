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
package org.apache.shardingsphere.core.merger.dal;


import java.sql.SQLException;
import java.util.List;
import org.apache.shardingsphere.core.merger.QueryResult;
import org.apache.shardingsphere.core.merger.dal.show.ShowCreateTableMergedResult;
import org.apache.shardingsphere.core.merger.dal.show.ShowDatabasesMergedResult;
import org.apache.shardingsphere.core.merger.dal.show.ShowOtherMergedResult;
import org.apache.shardingsphere.core.merger.dal.show.ShowTablesMergedResult;
import org.apache.shardingsphere.core.parsing.parser.dialect.mysql.statement.ShowCreateTableStatement;
import org.apache.shardingsphere.core.parsing.parser.dialect.mysql.statement.ShowDatabasesStatement;
import org.apache.shardingsphere.core.parsing.parser.dialect.mysql.statement.ShowOtherStatement;
import org.apache.shardingsphere.core.parsing.parser.dialect.mysql.statement.ShowTablesStatement;
import org.apache.shardingsphere.core.parsing.parser.sql.dal.DALStatement;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class DALMergeEngineTest {
    private List<QueryResult> queryResults;

    @Test
    public void assertMergeForShowDatabasesStatement() throws SQLException {
        DALStatement dalStatement = new ShowDatabasesStatement();
        DALMergeEngine dalMergeEngine = new DALMergeEngine(null, queryResults, dalStatement, null);
        Assert.assertThat(dalMergeEngine.merge(), CoreMatchers.instanceOf(ShowDatabasesMergedResult.class));
    }

    @Test
    public void assertMergeForShowShowTablesStatement() throws SQLException {
        DALStatement dalStatement = new ShowTablesStatement();
        DALMergeEngine dalMergeEngine = new DALMergeEngine(null, queryResults, dalStatement, null);
        Assert.assertThat(dalMergeEngine.merge(), CoreMatchers.instanceOf(ShowTablesMergedResult.class));
    }

    @Test
    public void assertMergeForShowCreateTableStatement() throws SQLException {
        DALStatement dalStatement = new ShowCreateTableStatement();
        DALMergeEngine dalMergeEngine = new DALMergeEngine(null, queryResults, dalStatement, null);
        Assert.assertThat(dalMergeEngine.merge(), CoreMatchers.instanceOf(ShowCreateTableMergedResult.class));
    }

    @Test
    public void assertMergeForShowOtherStatement() throws SQLException {
        DALStatement dalStatement = new ShowOtherStatement();
        DALMergeEngine dalMergeEngine = new DALMergeEngine(null, queryResults, dalStatement, null);
        Assert.assertThat(dalMergeEngine.merge(), CoreMatchers.instanceOf(ShowOtherMergedResult.class));
    }
}

