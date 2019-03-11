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
package org.apache.shardingsphere.core.metadata.table;


import com.google.common.base.Joiner;
import java.util.Arrays;
import org.apache.shardingsphere.core.parsing.antlr.sql.segment.definition.column.ColumnDefinitionSegment;
import org.apache.shardingsphere.core.parsing.antlr.sql.segment.definition.column.position.ColumnAfterPositionSegment;
import org.apache.shardingsphere.core.parsing.antlr.sql.segment.definition.column.position.ColumnFirstPositionSegment;
import org.apache.shardingsphere.core.parsing.antlr.sql.statement.ddl.AlterTableStatement;
import org.apache.shardingsphere.core.parsing.antlr.sql.statement.ddl.CreateTableStatement;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class TableMetaDataFactoryTest {
    @Test
    public void assertNewInstanceWithCreateTable() {
        CreateTableStatement createTableStatement = new CreateTableStatement();
        createTableStatement.getColumnDefinitions().add(new ColumnDefinitionSegment("id", "bigint", true));
        createTableStatement.getColumnDefinitions().add(new ColumnDefinitionSegment("status", "varchar", false));
        TableMetaData actual = TableMetaDataFactory.newInstance(createTableStatement);
        Assert.assertThat(Joiner.on(", ").join(actual.getColumns().keySet()), CoreMatchers.is("id, status"));
        Assert.assertThat(actual.getColumns().get("id"), CoreMatchers.is(new ColumnMetaData("id", "bigint", true)));
        Assert.assertThat(actual.getColumns().get("status"), CoreMatchers.is(new ColumnMetaData("status", "varchar", false)));
    }

    @Test
    public void assertNewInstanceWithAlterTable() {
        AlterTableStatement alterTableStatement = new AlterTableStatement();
        alterTableStatement.getAddedColumnDefinitions().add(new ColumnDefinitionSegment("new_column_1", "bigint", true));
        alterTableStatement.getAddedColumnDefinitions().add(new ColumnDefinitionSegment("new_column_2", "varchar", false));
        alterTableStatement.getModifiedColumnDefinitions().put("id", new ColumnDefinitionSegment("user_id", "bigint", true));
        alterTableStatement.getModifiedColumnDefinitions().put("status", new ColumnDefinitionSegment("status", "char", false));
        alterTableStatement.getDroppedColumnNames().add("drop_column_1");
        alterTableStatement.getDroppedColumnNames().add("drop_column_2");
        TableMetaData oldTableMetaData = new TableMetaData(Arrays.asList(new ColumnMetaData("id", "bigint", true), new ColumnMetaData("status", "varchar", false), new ColumnMetaData("drop_column_1", "varchar", false), new ColumnMetaData("drop_column_2", "varchar", false)));
        TableMetaData actual = TableMetaDataFactory.newInstance(alterTableStatement, oldTableMetaData);
        Assert.assertThat(Joiner.on(", ").join(actual.getColumns().keySet()), CoreMatchers.is("user_id, status, new_column_1, new_column_2"));
        Assert.assertThat(actual.getColumns().get("user_id"), CoreMatchers.is(new ColumnMetaData("user_id", "bigint", true)));
        Assert.assertThat(actual.getColumns().get("status"), CoreMatchers.is(new ColumnMetaData("status", "char", false)));
        Assert.assertThat(actual.getColumns().get("new_column_1"), CoreMatchers.is(new ColumnMetaData("new_column_1", "bigint", true)));
        Assert.assertThat(actual.getColumns().get("new_column_2"), CoreMatchers.is(new ColumnMetaData("new_column_2", "varchar", false)));
    }

    @Test
    public void assertNewInstanceWithAlterTableWhenChangedPosition() {
        AlterTableStatement alterTableStatement = new AlterTableStatement();
        alterTableStatement.getAddedColumnDefinitions().add(new ColumnDefinitionSegment("new_column_1", "bigint", true));
        alterTableStatement.getAddedColumnDefinitions().add(new ColumnDefinitionSegment("new_column_2", "varchar", false));
        alterTableStatement.getModifiedColumnDefinitions().put("id", new ColumnDefinitionSegment("user_id", "bigint", true));
        alterTableStatement.getModifiedColumnDefinitions().put("status", new ColumnDefinitionSegment("status", "char", false));
        alterTableStatement.getChangedPositionColumns().add(new ColumnFirstPositionSegment("new_column_1", 1));
        alterTableStatement.getChangedPositionColumns().add(new ColumnFirstPositionSegment("user_id", 2));
        alterTableStatement.getChangedPositionColumns().add(new ColumnAfterPositionSegment("status", 3, "new_column_2"));
        TableMetaData oldTableMetaData = new TableMetaData(Arrays.asList(new ColumnMetaData("status", "varchar", false), new ColumnMetaData("id", "bigint", true)));
        TableMetaData actual = TableMetaDataFactory.newInstance(alterTableStatement, oldTableMetaData);
        Assert.assertThat(Joiner.on(", ").join(actual.getColumns().keySet()), CoreMatchers.is("user_id, new_column_1, new_column_2, status"));
        Assert.assertThat(actual.getColumns().get("user_id"), CoreMatchers.is(new ColumnMetaData("user_id", "bigint", true)));
        Assert.assertThat(actual.getColumns().get("new_column_1"), CoreMatchers.is(new ColumnMetaData("new_column_1", "bigint", true)));
        Assert.assertThat(actual.getColumns().get("new_column_2"), CoreMatchers.is(new ColumnMetaData("new_column_2", "varchar", false)));
        Assert.assertThat(actual.getColumns().get("status"), CoreMatchers.is(new ColumnMetaData("status", "char", false)));
    }

    @Test
    public void assertNewInstanceWithAlterTableWhenDropPrimaryKey() {
        AlterTableStatement alterTableStatement = new AlterTableStatement();
        alterTableStatement.setDropPrimaryKey(true);
        alterTableStatement.getAddedColumnDefinitions().add(new ColumnDefinitionSegment("new_column", "bigint", true));
        TableMetaData oldTableMetaData = new TableMetaData(Arrays.asList(new ColumnMetaData("id", "bigint", true), new ColumnMetaData("status", "varchar", false)));
        TableMetaData actual = TableMetaDataFactory.newInstance(alterTableStatement, oldTableMetaData);
        Assert.assertThat(Joiner.on(", ").join(actual.getColumns().keySet()), CoreMatchers.is("id, status, new_column"));
        Assert.assertThat(actual.getColumns().get("id"), CoreMatchers.is(new ColumnMetaData("id", "bigint", false)));
        Assert.assertThat(actual.getColumns().get("new_column"), CoreMatchers.is(new ColumnMetaData("new_column", "bigint", false)));
        Assert.assertThat(actual.getColumns().get("status"), CoreMatchers.is(new ColumnMetaData("status", "varchar", false)));
    }
}

