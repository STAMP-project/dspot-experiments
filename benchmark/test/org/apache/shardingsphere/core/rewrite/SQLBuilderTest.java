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
package org.apache.shardingsphere.core.rewrite;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.shardingsphere.core.exception.ShardingException;
import org.apache.shardingsphere.core.metadata.datasource.ShardingDataSourceMetaData;
import org.apache.shardingsphere.core.rewrite.placeholder.IndexPlaceholder;
import org.apache.shardingsphere.core.rewrite.placeholder.SchemaPlaceholder;
import org.apache.shardingsphere.core.rewrite.placeholder.TablePlaceholder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class SQLBuilderTest {
    private ShardingDataSourceMetaData shardingDataSourceMetaData;

    @Test
    public void assertAppendLiteralsOnly() {
        SQLBuilder sqlBuilder = new SQLBuilder();
        sqlBuilder.appendLiterals("SELECT ");
        sqlBuilder.appendLiterals("table_x");
        sqlBuilder.appendLiterals(".id");
        sqlBuilder.appendLiterals(" FROM ");
        sqlBuilder.appendLiterals("table_x");
        Assert.assertThat(sqlBuilder.toSQL(null, Collections.<String, String>emptyMap(), null, null).getSql(), CoreMatchers.is("SELECT table_x.id FROM table_x"));
    }

    @Test
    public void assertAppendTableWithoutTableToken() {
        SQLBuilder sqlBuilder = new SQLBuilder();
        sqlBuilder.appendLiterals("SELECT ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_x", "", ""));
        sqlBuilder.appendLiterals(".id");
        sqlBuilder.appendLiterals(" FROM ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_x", "", ""));
        Assert.assertThat(sqlBuilder.toSQL(null, Collections.<String, String>emptyMap(), null, null).getSql(), CoreMatchers.is("SELECT table_x.id FROM table_x"));
    }

    @Test
    public void assertAppendTableWithTableToken() {
        SQLBuilder sqlBuilder = new SQLBuilder();
        sqlBuilder.appendLiterals("SELECT ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_x", "", ""));
        sqlBuilder.appendLiterals(".id");
        sqlBuilder.appendLiterals(" FROM ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_x", "", ""));
        Map<String, String> tableTokens = new HashMap<>(1, 1);
        tableTokens.put("table_x", "table_x_1");
        Assert.assertThat(sqlBuilder.toSQL(null, tableTokens, null, null).getSql(), CoreMatchers.is("SELECT table_x_1.id FROM table_x_1"));
    }

    @Test
    public void assertIndexPlaceholderAppendTableWithoutTableToken() {
        SQLBuilder sqlBuilder = new SQLBuilder();
        sqlBuilder.appendLiterals("CREATE INDEX ");
        sqlBuilder.appendPlaceholder(new IndexPlaceholder("index_name", "index_name"));
        sqlBuilder.appendLiterals(" ON ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_x", "", ""));
        sqlBuilder.appendLiterals(" ('column')");
        Assert.assertThat(sqlBuilder.toSQL(null, Collections.<String, String>emptyMap(), null, null).getSql(), CoreMatchers.is("CREATE INDEX index_name ON table_x ('column')"));
    }

    @Test
    public void assertIndexPlaceholderAppendTableWithTableToken() {
        SQLBuilder sqlBuilder = new SQLBuilder();
        sqlBuilder.appendLiterals("CREATE INDEX ");
        sqlBuilder.appendPlaceholder(new IndexPlaceholder("index_name", "table_x"));
        sqlBuilder.appendLiterals(" ON ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_x", "", ""));
        sqlBuilder.appendLiterals(" ('column')");
        Map<String, String> tableTokens = new HashMap<>(1, 1);
        tableTokens.put("table_x", "table_x_1");
        Assert.assertThat(sqlBuilder.toSQL(null, tableTokens, null, null).getSql(), CoreMatchers.is("CREATE INDEX index_name_table_x_1 ON table_x_1 ('column')"));
    }

    @Test(expected = ShardingException.class)
    public void assertSchemaPlaceholderAppendTableWithoutTableToken() {
        SQLBuilder sqlBuilder = new SQLBuilder();
        sqlBuilder.appendLiterals("SHOW ");
        sqlBuilder.appendLiterals("CREATE TABLE ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_x", "", ""));
        sqlBuilder.appendLiterals("ON ");
        sqlBuilder.appendPlaceholder(new SchemaPlaceholder("dx", "table_x"));
        sqlBuilder.toSQL(null, Collections.<String, String>emptyMap(), createShardingRule(), null);
    }

    @Test
    public void assertSchemaPlaceholderAppendTableWithTableToken() {
        SQLBuilder sqlBuilder = new SQLBuilder();
        sqlBuilder.appendLiterals("SHOW ");
        sqlBuilder.appendLiterals("CREATE TABLE ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_0", "", ""));
        sqlBuilder.appendLiterals(" ON ");
        sqlBuilder.appendPlaceholder(new SchemaPlaceholder("ds0", "table_0"));
        Map<String, String> tableTokens = new HashMap<>(1, 1);
        tableTokens.put("table_0", "table_1");
        // ShardingDataSourceMetaData shardingDataSourceMetaData = Mockito.mock(ShardingDataSourceMetaData.class);
        // Mockito.when(shardingDataSourceMetaData.getActualSchemaName(Mockito.anyString())).thenReturn("actual_db");
        Assert.assertThat(sqlBuilder.toSQL(null, tableTokens, createShardingRule(), shardingDataSourceMetaData).getSql(), CoreMatchers.is("SHOW CREATE TABLE table_1 ON actual_db"));
    }

    @Test
    public void assertAppendTableWithoutTableTokenWithBackQuotes() {
        SQLBuilder sqlBuilder = new SQLBuilder();
        sqlBuilder.appendLiterals("SELECT ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_x", "`", "`"));
        sqlBuilder.appendLiterals(".id");
        sqlBuilder.appendLiterals(" FROM ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_x", "`", "`"));
        Assert.assertThat(sqlBuilder.toSQL(null, Collections.<String, String>emptyMap(), null, null).getSql(), CoreMatchers.is("SELECT `table_x`.id FROM `table_x`"));
    }

    @Test
    public void assertAppendTableWithTableTokenWithBackQuotes() {
        SQLBuilder sqlBuilder = new SQLBuilder();
        sqlBuilder.appendLiterals("SELECT ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_x", "`", "`"));
        sqlBuilder.appendLiterals(".id");
        sqlBuilder.appendLiterals(" FROM ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_x", "`", "`"));
        Map<String, String> tableTokens = new HashMap<>(1, 1);
        tableTokens.put("table_x", "table_x_1");
        Assert.assertThat(sqlBuilder.toSQL(null, tableTokens, null, null).getSql(), CoreMatchers.is("SELECT `table_x_1`.id FROM `table_x_1`"));
    }

    @Test
    public void assertIndexPlaceholderAppendTableWithoutTableTokenWithBackQuotes() {
        SQLBuilder sqlBuilder = new SQLBuilder();
        sqlBuilder.appendLiterals("CREATE INDEX ");
        sqlBuilder.appendPlaceholder(new IndexPlaceholder("index_name", "index_name"));
        sqlBuilder.appendLiterals(" ON ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_x", "`", "`"));
        sqlBuilder.appendLiterals(" ('column')");
        Assert.assertThat(sqlBuilder.toSQL(null, Collections.<String, String>emptyMap(), null, null).getSql(), CoreMatchers.is("CREATE INDEX index_name ON `table_x` ('column')"));
    }

    @Test
    public void assertIndexPlaceholderAppendTableWithTableTokenWithBackQuotes() {
        SQLBuilder sqlBuilder = new SQLBuilder();
        sqlBuilder.appendLiterals("CREATE INDEX ");
        sqlBuilder.appendPlaceholder(new IndexPlaceholder("index_name", "table_x"));
        sqlBuilder.appendLiterals(" ON ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_x", "`", "`"));
        sqlBuilder.appendLiterals(" ('column')");
        Map<String, String> tableTokens = new HashMap<>(1, 1);
        tableTokens.put("table_x", "table_x_1");
        Assert.assertThat(sqlBuilder.toSQL(null, tableTokens, null, null).getSql(), CoreMatchers.is("CREATE INDEX index_name_table_x_1 ON `table_x_1` ('column')"));
    }

    @Test
    public void assertSchemaPlaceholderAppendTableWithTableTokenWithBackQuotes() {
        SQLBuilder sqlBuilder = new SQLBuilder();
        sqlBuilder.appendLiterals("SHOW ");
        sqlBuilder.appendLiterals("CREATE TABLE ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_0", "`", "`"));
        sqlBuilder.appendLiterals(" ON ");
        sqlBuilder.appendPlaceholder(new SchemaPlaceholder("ds", "table_0"));
        Map<String, String> tableTokens = new HashMap<>(1, 1);
        tableTokens.put("table_0", "table_1");
        Assert.assertThat(sqlBuilder.toSQL(null, tableTokens, createShardingRule(), shardingDataSourceMetaData).getSql(), CoreMatchers.is("SHOW CREATE TABLE `table_1` ON actual_db"));
    }

    @Test
    public void assertAppendTableWithoutTableTokenWithDoubleQuotes() {
        SQLBuilder sqlBuilder = new SQLBuilder();
        sqlBuilder.appendLiterals("SELECT ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_x", "\"", "\""));
        sqlBuilder.appendLiterals(".id");
        sqlBuilder.appendLiterals(" FROM ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_x", "\"", "\""));
        Assert.assertThat(sqlBuilder.toSQL(null, Collections.<String, String>emptyMap(), null, null).getSql(), CoreMatchers.is("SELECT \"table_x\".id FROM \"table_x\""));
    }

    @Test
    public void assertAppendTableWithTableTokenWithDoubleQuotes() {
        SQLBuilder sqlBuilder = new SQLBuilder();
        sqlBuilder.appendLiterals("SELECT ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_x", "\"", "\""));
        sqlBuilder.appendLiterals(".id");
        sqlBuilder.appendLiterals(" FROM ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_x", "\"", "\""));
        Map<String, String> tableTokens = new HashMap<>(1, 1);
        tableTokens.put("table_x", "table_x_1");
        Assert.assertThat(sqlBuilder.toSQL(null, tableTokens, null, null).getSql(), CoreMatchers.is("SELECT \"table_x_1\".id FROM \"table_x_1\""));
    }

    @Test
    public void assertIndexPlaceholderAppendTableWithoutTableTokenWithDoubleQuotes() {
        SQLBuilder sqlBuilder = new SQLBuilder();
        sqlBuilder.appendLiterals("CREATE INDEX ");
        sqlBuilder.appendPlaceholder(new IndexPlaceholder("index_name", "index_name"));
        sqlBuilder.appendLiterals(" ON ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_x", "\"", "\""));
        sqlBuilder.appendLiterals(" ('column')");
        Assert.assertThat(sqlBuilder.toSQL(null, Collections.<String, String>emptyMap(), null, null).getSql(), CoreMatchers.is("CREATE INDEX index_name ON \"table_x\" (\'column\')"));
    }

    @Test
    public void assertIndexPlaceholderAppendTableWithTableTokenWithDoubleQuotes() {
        SQLBuilder sqlBuilder = new SQLBuilder();
        sqlBuilder.appendLiterals("CREATE INDEX ");
        sqlBuilder.appendPlaceholder(new IndexPlaceholder("index_name", "table_x"));
        sqlBuilder.appendLiterals(" ON ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_x", "\"", "\""));
        sqlBuilder.appendLiterals(" ('column')");
        Map<String, String> tableTokens = new HashMap<>(1, 1);
        tableTokens.put("table_x", "table_x_1");
        Assert.assertThat(sqlBuilder.toSQL(null, tableTokens, null, null).getSql(), CoreMatchers.is("CREATE INDEX index_name_table_x_1 ON \"table_x_1\" (\'column\')"));
    }

    @Test
    public void assertSchemaPlaceholderAppendTableWithTableTokenWithDoubleQuotes() {
        SQLBuilder sqlBuilder = new SQLBuilder();
        sqlBuilder.appendLiterals("SHOW ");
        sqlBuilder.appendLiterals("CREATE TABLE ");
        sqlBuilder.appendPlaceholder(new TablePlaceholder("table_0", "\"", "\""));
        sqlBuilder.appendLiterals(" ON ");
        sqlBuilder.appendPlaceholder(new SchemaPlaceholder("ds", "table_0"));
        Map<String, String> tableTokens = new HashMap<>(1, 1);
        tableTokens.put("table_0", "table_1");
        Assert.assertThat(sqlBuilder.toSQL(null, tableTokens, createShardingRule(), shardingDataSourceMetaData).getSql(), CoreMatchers.is("SHOW CREATE TABLE \"table_1\" ON actual_db"));
    }

    @Test
    public void assertShardingPlaceholderToString() {
        Assert.assertThat(new IndexPlaceholder("index_name", "table_x").toString(), CoreMatchers.is("index_name"));
        Assert.assertThat(new SchemaPlaceholder("schema_name", "table_x").toString(), CoreMatchers.is("schema_name"));
        Assert.assertThat(new TablePlaceholder("table_name", "`", "`").toString(), CoreMatchers.is("`table_name`"));
    }
}

