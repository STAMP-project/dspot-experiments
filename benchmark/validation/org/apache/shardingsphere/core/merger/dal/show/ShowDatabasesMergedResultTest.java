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
package org.apache.shardingsphere.core.merger.dal.show;


import ShardingConstant.LOGIC_SCHEMA_NAME;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Calendar;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ShowDatabasesMergedResultTest {
    private ShowDatabasesMergedResult showDatabasesMergedResult;

    @Test
    public void assertNext() {
        Assert.assertTrue(showDatabasesMergedResult.next());
        Assert.assertFalse(showDatabasesMergedResult.next());
    }

    @Test
    public void assertGetValueWithColumnIndex() throws SQLException {
        Assert.assertTrue(showDatabasesMergedResult.next());
        Assert.assertThat(showDatabasesMergedResult.getValue(1, Object.class).toString(), CoreMatchers.is(LOGIC_SCHEMA_NAME));
    }

    @Test
    public void assertGetValueWithColumnLabel() throws SQLException {
        Assert.assertTrue(showDatabasesMergedResult.next());
        Assert.assertThat(showDatabasesMergedResult.getValue("label", Object.class).toString(), CoreMatchers.is(LOGIC_SCHEMA_NAME));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertGetCalendarValueWithColumnIndex() throws SQLException {
        showDatabasesMergedResult.getCalendarValue(1, Object.class, Calendar.getInstance());
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertGetCalendarValueWithColumnLabel() throws SQLException {
        showDatabasesMergedResult.getCalendarValue("label", Object.class, Calendar.getInstance());
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertGetInputStreamWithColumnIndex() throws SQLException {
        showDatabasesMergedResult.getInputStream(1, "Ascii");
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertGetInputStreamWithColumnLabel() throws SQLException {
        showDatabasesMergedResult.getInputStream("label", "Ascii");
    }

    @Test
    public void assertWasNull() {
        Assert.assertFalse(showDatabasesMergedResult.wasNull());
    }
}

