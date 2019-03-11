/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hive.storage.jdbc.dao;


import JdbcStorageConfig.QUERY;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hive.storage.jdbc.exception.HiveJdbcDatabaseAccessException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestGenericJdbcDatabaseAccessor {
    @Test
    public void testGetColumnNames_starQuery() throws HiveJdbcDatabaseAccessException {
        Configuration conf = buildConfiguration();
        DatabaseAccessor accessor = DatabaseAccessorFactory.getAccessor(conf);
        List<String> columnNames = accessor.getColumnNames(conf);
        Assert.assertThat(columnNames, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(columnNames.size(), Matchers.is(Matchers.equalTo(7)));
        Assert.assertThat(columnNames.get(0), Matchers.is(Matchers.equalToIgnoringCase("strategy_id")));
    }

    @Test
    public void testGetColumnNames_fieldListQuery() throws HiveJdbcDatabaseAccessException {
        Configuration conf = buildConfiguration();
        conf.set(QUERY.getPropertyName(), "select name,referrer from test_strategy");
        DatabaseAccessor accessor = DatabaseAccessorFactory.getAccessor(conf);
        List<String> columnNames = accessor.getColumnNames(conf);
        Assert.assertThat(columnNames, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(columnNames.size(), Matchers.is(Matchers.equalTo(2)));
        Assert.assertThat(columnNames.get(0), Matchers.is(Matchers.equalToIgnoringCase("name")));
    }

    @Test(expected = HiveJdbcDatabaseAccessException.class)
    public void testGetColumnNames_invalidQuery() throws HiveJdbcDatabaseAccessException {
        Configuration conf = buildConfiguration();
        conf.set(QUERY.getPropertyName(), "select * from invalid_strategy");
        DatabaseAccessor accessor = DatabaseAccessorFactory.getAccessor(conf);
        @SuppressWarnings("unused")
        List<String> columnNames = accessor.getColumnNames(conf);
    }

    @Test
    public void testGetTotalNumberOfRecords() throws HiveJdbcDatabaseAccessException {
        Configuration conf = buildConfiguration();
        DatabaseAccessor accessor = DatabaseAccessorFactory.getAccessor(conf);
        int numRecords = accessor.getTotalNumberOfRecords(conf);
        Assert.assertThat(numRecords, Matchers.is(Matchers.equalTo(5)));
    }

    @Test
    public void testGetTotalNumberOfRecords_whereClause() throws HiveJdbcDatabaseAccessException {
        Configuration conf = buildConfiguration();
        conf.set(QUERY.getPropertyName(), "select * from test_strategy where strategy_id = '5'");
        DatabaseAccessor accessor = DatabaseAccessorFactory.getAccessor(conf);
        int numRecords = accessor.getTotalNumberOfRecords(conf);
        Assert.assertThat(numRecords, Matchers.is(Matchers.equalTo(1)));
    }

    @Test
    public void testGetTotalNumberOfRecords_noRecords() throws HiveJdbcDatabaseAccessException {
        Configuration conf = buildConfiguration();
        conf.set(QUERY.getPropertyName(), "select * from test_strategy where strategy_id = '25'");
        DatabaseAccessor accessor = DatabaseAccessorFactory.getAccessor(conf);
        int numRecords = accessor.getTotalNumberOfRecords(conf);
        Assert.assertThat(numRecords, Matchers.is(Matchers.equalTo(0)));
    }

    @Test(expected = HiveJdbcDatabaseAccessException.class)
    public void testGetTotalNumberOfRecords_invalidQuery() throws HiveJdbcDatabaseAccessException {
        Configuration conf = buildConfiguration();
        conf.set(QUERY.getPropertyName(), "select * from strategyx where strategy_id = '5'");
        DatabaseAccessor accessor = DatabaseAccessorFactory.getAccessor(conf);
        @SuppressWarnings("unused")
        int numRecords = accessor.getTotalNumberOfRecords(conf);
    }

    @Test
    public void testGetRecordIterator() throws HiveJdbcDatabaseAccessException {
        Configuration conf = buildConfiguration();
        DatabaseAccessor accessor = DatabaseAccessorFactory.getAccessor(conf);
        JdbcRecordIterator iterator = accessor.getRecordIterator(conf, null, null, null, 2, 0);
        Assert.assertThat(iterator, Matchers.is(Matchers.notNullValue()));
        int count = 0;
        while (iterator.hasNext()) {
            Map<String, Object> record = iterator.next();
            count++;
            Assert.assertThat(record, Matchers.is(Matchers.notNullValue()));
            Assert.assertThat(record.size(), Matchers.is(Matchers.equalTo(7)));
            Assert.assertThat(record.get("strategy_id"), Matchers.is(Matchers.equalTo(count)));
        } 
        Assert.assertThat(count, Matchers.is(Matchers.equalTo(2)));
        iterator.close();
    }

    @Test
    public void testGetRecordIterator_offsets() throws HiveJdbcDatabaseAccessException {
        Configuration conf = buildConfiguration();
        DatabaseAccessor accessor = DatabaseAccessorFactory.getAccessor(conf);
        JdbcRecordIterator iterator = accessor.getRecordIterator(conf, null, null, null, 2, 2);
        Assert.assertThat(iterator, Matchers.is(Matchers.notNullValue()));
        int count = 0;
        while (iterator.hasNext()) {
            Map<String, Object> record = iterator.next();
            count++;
            Assert.assertThat(record, Matchers.is(Matchers.notNullValue()));
            Assert.assertThat(record.size(), Matchers.is(Matchers.equalTo(7)));
            Assert.assertThat(record.get("strategy_id"), Matchers.is(Matchers.equalTo((count + 2))));
        } 
        Assert.assertThat(count, Matchers.is(Matchers.equalTo(2)));
        iterator.close();
    }

    @Test
    public void testGetRecordIterator_emptyResultSet() throws HiveJdbcDatabaseAccessException {
        Configuration conf = buildConfiguration();
        conf.set(QUERY.getPropertyName(), "select * from test_strategy where strategy_id = '25'");
        DatabaseAccessor accessor = DatabaseAccessorFactory.getAccessor(conf);
        JdbcRecordIterator iterator = accessor.getRecordIterator(conf, null, null, null, 0, 2);
        Assert.assertThat(iterator, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(iterator.hasNext(), Matchers.is(false));
        iterator.close();
    }

    @Test
    public void testGetRecordIterator_largeOffset() throws HiveJdbcDatabaseAccessException {
        Configuration conf = buildConfiguration();
        DatabaseAccessor accessor = DatabaseAccessorFactory.getAccessor(conf);
        JdbcRecordIterator iterator = accessor.getRecordIterator(conf, null, null, null, 10, 25);
        Assert.assertThat(iterator, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(iterator.hasNext(), Matchers.is(false));
        iterator.close();
    }

    @Test(expected = HiveJdbcDatabaseAccessException.class)
    public void testGetRecordIterator_invalidQuery() throws HiveJdbcDatabaseAccessException {
        Configuration conf = buildConfiguration();
        conf.set(QUERY.getPropertyName(), "select * from strategyx");
        DatabaseAccessor accessor = DatabaseAccessorFactory.getAccessor(conf);
        @SuppressWarnings("unused")
        JdbcRecordIterator iterator = accessor.getRecordIterator(conf, null, null, null, 0, 2);
    }
}

