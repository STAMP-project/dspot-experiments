/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.jdbc;


import java.sql.SQLException;
import java.sql.Types;
import org.apache.drill.categories.JdbcTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test compatibility with older versions of the server
 */
@Category(JdbcTest.class)
public class LegacyDatabaseMetaDataGetColumnsTest extends DatabaseMetaDataGetColumnsTest {
    // Override because of DRILL-1959
    @Override
    @Test
    public void test_SOURCE_DATA_TYPE_hasRightTypeString() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnTypeName(22), CoreMatchers.equalTo("INTEGER"));
    }

    @Override
    @Test
    public void test_SOURCE_DATA_TYPE_hasRightTypeCode() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnType(22), CoreMatchers.equalTo(Types.INTEGER));
    }

    @Override
    @Test
    public void test_SOURCE_DATA_TYPE_hasRightClass() throws SQLException {
        Assert.assertThat(DatabaseMetaDataGetColumnsTest.rowsMetadata.getColumnClassName(22), CoreMatchers.equalTo(Integer.class.getName()));
    }
}

