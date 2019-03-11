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
package org.apache.hive.storage.jdbc;


import com.google.common.collect.Lists;
import java.io.IOException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hive.storage.jdbc.dao.DatabaseAccessor;
import org.apache.hive.storage.jdbc.dao.DatabaseAccessorFactory;
import org.apache.hive.storage.jdbc.exception.HiveJdbcDatabaseAccessException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import serdeConstants.LIST_COLUMN_TYPES;


@RunWith(PowerMockRunner.class)
@PrepareForTest(DatabaseAccessorFactory.class)
public class TestJdbcInputFormat {
    @Mock
    private DatabaseAccessor mockDatabaseAccessor;

    @Test
    public void testLimitSplit_noSpillOver() throws IOException, HiveJdbcDatabaseAccessException {
        PowerMockito.mockStatic(DatabaseAccessorFactory.class);
        BDDMockito.given(DatabaseAccessorFactory.getAccessor(ArgumentMatchers.any(Configuration.class))).willReturn(mockDatabaseAccessor);
        JdbcInputFormat f = new JdbcInputFormat();
        Mockito.when(mockDatabaseAccessor.getTotalNumberOfRecords(ArgumentMatchers.any(Configuration.class))).thenReturn(15);
        JobConf conf = new JobConf();
        conf.set("mapred.input.dir", "/temp");
        conf.set("hive.sql.numPartitions", "3");
        InputSplit[] splits = f.getSplits(conf, (-1));
        Assert.assertThat(splits, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(splits.length, Matchers.is(3));
        Assert.assertThat(splits[0].getLength(), Matchers.is(5L));
    }

    @Test
    public void testLimitSplit_withSpillOver() throws IOException, HiveJdbcDatabaseAccessException {
        PowerMockito.mockStatic(DatabaseAccessorFactory.class);
        BDDMockito.given(DatabaseAccessorFactory.getAccessor(ArgumentMatchers.any(Configuration.class))).willReturn(mockDatabaseAccessor);
        JdbcInputFormat f = new JdbcInputFormat();
        Mockito.when(mockDatabaseAccessor.getTotalNumberOfRecords(ArgumentMatchers.any(Configuration.class))).thenReturn(15);
        JobConf conf = new JobConf();
        conf.set("mapred.input.dir", "/temp");
        conf.set("hive.sql.numPartitions", "6");
        InputSplit[] splits = f.getSplits(conf, (-1));
        Assert.assertThat(splits, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(splits.length, Matchers.is(6));
        for (int i = 0; i < 3; i++) {
            Assert.assertThat(splits[i].getLength(), Matchers.is(3L));
        }
        for (int i = 3; i < 6; i++) {
            Assert.assertThat(splits[i].getLength(), Matchers.is(2L));
        }
    }

    @Test
    public void testIntervalSplit_Long() throws IOException, HiveJdbcDatabaseAccessException {
        PowerMockito.mockStatic(DatabaseAccessorFactory.class);
        BDDMockito.given(DatabaseAccessorFactory.getAccessor(ArgumentMatchers.any(Configuration.class))).willReturn(mockDatabaseAccessor);
        JdbcInputFormat f = new JdbcInputFormat();
        Mockito.when(mockDatabaseAccessor.getColumnNames(ArgumentMatchers.any(Configuration.class))).thenReturn(Lists.newArrayList("a"));
        JobConf conf = new JobConf();
        conf.set("mapred.input.dir", "/temp");
        conf.set(LIST_COLUMN_TYPES, "int");
        conf.set("hive.sql.partitionColumn", "a");
        conf.set("hive.sql.numPartitions", "3");
        conf.set("hive.sql.lowerBound", "1");
        conf.set("hive.sql.upperBound", "10");
        InputSplit[] splits = f.getSplits(conf, (-1));
        Assert.assertThat(splits, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(splits.length, Matchers.is(3));
        Assert.assertNull(getLowerBound());
        Assert.assertEquals(getUpperBound(), "4");
        Assert.assertEquals(getLowerBound(), "4");
        Assert.assertEquals(getUpperBound(), "7");
        Assert.assertEquals(getLowerBound(), "7");
        Assert.assertNull(getUpperBound());
    }

    @Test
    public void testIntervalSplit_Double() throws IOException, HiveJdbcDatabaseAccessException {
        PowerMockito.mockStatic(DatabaseAccessorFactory.class);
        BDDMockito.given(DatabaseAccessorFactory.getAccessor(ArgumentMatchers.any(Configuration.class))).willReturn(mockDatabaseAccessor);
        JdbcInputFormat f = new JdbcInputFormat();
        Mockito.when(mockDatabaseAccessor.getColumnNames(ArgumentMatchers.any(Configuration.class))).thenReturn(Lists.newArrayList("a"));
        JobConf conf = new JobConf();
        conf.set("mapred.input.dir", "/temp");
        conf.set(LIST_COLUMN_TYPES, "double");
        conf.set("hive.sql.partitionColumn", "a");
        conf.set("hive.sql.numPartitions", "3");
        conf.set("hive.sql.lowerBound", "0");
        conf.set("hive.sql.upperBound", "10");
        InputSplit[] splits = f.getSplits(conf, (-1));
        Assert.assertThat(splits, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(splits.length, Matchers.is(3));
        Assert.assertNull(getLowerBound());
        Assert.assertTrue((((Double.parseDouble(getUpperBound())) > 3.3) && ((Double.parseDouble(getUpperBound())) < 3.4)));
        Assert.assertTrue((((Double.parseDouble(getLowerBound())) > 3.3) && ((Double.parseDouble(getLowerBound())) < 3.4)));
        Assert.assertTrue((((Double.parseDouble(getUpperBound())) > 6.6) && ((Double.parseDouble(getUpperBound())) < 6.7)));
        Assert.assertTrue((((Double.parseDouble(getLowerBound())) > 6.6) && ((Double.parseDouble(getLowerBound())) < 6.7)));
        Assert.assertNull(getUpperBound());
    }

    @Test
    public void testIntervalSplit_Decimal() throws IOException, HiveJdbcDatabaseAccessException {
        PowerMockito.mockStatic(DatabaseAccessorFactory.class);
        BDDMockito.given(DatabaseAccessorFactory.getAccessor(ArgumentMatchers.any(Configuration.class))).willReturn(mockDatabaseAccessor);
        JdbcInputFormat f = new JdbcInputFormat();
        Mockito.when(mockDatabaseAccessor.getColumnNames(ArgumentMatchers.any(Configuration.class))).thenReturn(Lists.newArrayList("a"));
        JobConf conf = new JobConf();
        conf.set("mapred.input.dir", "/temp");
        conf.set(LIST_COLUMN_TYPES, "decimal(10,5)");
        conf.set("hive.sql.partitionColumn", "a");
        conf.set("hive.sql.numPartitions", "4");
        conf.set("hive.sql.lowerBound", "5");
        conf.set("hive.sql.upperBound", "1000");
        InputSplit[] splits = f.getSplits(conf, (-1));
        Assert.assertThat(splits, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(splits.length, Matchers.is(4));
        Assert.assertNull(getLowerBound());
        Assert.assertEquals(getUpperBound(), "253.75000");
        Assert.assertEquals(getLowerBound(), "253.75000");
        Assert.assertEquals(getUpperBound(), "502.50000");
        Assert.assertEquals(getLowerBound(), "502.50000");
        Assert.assertEquals(getUpperBound(), "751.25000");
        Assert.assertEquals(getLowerBound(), "751.25000");
        Assert.assertNull(getUpperBound());
    }

    @Test
    public void testIntervalSplit_Timestamp() throws IOException, HiveJdbcDatabaseAccessException {
        PowerMockito.mockStatic(DatabaseAccessorFactory.class);
        BDDMockito.given(DatabaseAccessorFactory.getAccessor(ArgumentMatchers.any(Configuration.class))).willReturn(mockDatabaseAccessor);
        JdbcInputFormat f = new JdbcInputFormat();
        Mockito.when(mockDatabaseAccessor.getColumnNames(ArgumentMatchers.any(Configuration.class))).thenReturn(Lists.newArrayList("a"));
        Mockito.when(mockDatabaseAccessor.getBounds(ArgumentMatchers.any(Configuration.class), ArgumentMatchers.any(String.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(new ImmutablePair<String, String>("2010-01-01 00:00:00.000000000", ("2018-01-01 " + "12:00:00.000000000")));
        JobConf conf = new JobConf();
        conf.set("mapred.input.dir", "/temp");
        conf.set(LIST_COLUMN_TYPES, "timestamp");
        conf.set("hive.sql.partitionColumn", "a");
        conf.set("hive.sql.numPartitions", "2");
        InputSplit[] splits = f.getSplits(conf, (-1));
        Assert.assertThat(splits, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(splits.length, Matchers.is(2));
        Assert.assertNull(getLowerBound());
        Assert.assertEquals(getUpperBound(), "2014-01-01 06:00:00.0");
        Assert.assertEquals(getLowerBound(), "2014-01-01 06:00:00.0");
        Assert.assertNull(getUpperBound());
    }

    @Test
    public void testIntervalSplit_Date() throws IOException, HiveJdbcDatabaseAccessException {
        PowerMockito.mockStatic(DatabaseAccessorFactory.class);
        BDDMockito.given(DatabaseAccessorFactory.getAccessor(ArgumentMatchers.any(Configuration.class))).willReturn(mockDatabaseAccessor);
        JdbcInputFormat f = new JdbcInputFormat();
        Mockito.when(mockDatabaseAccessor.getColumnNames(ArgumentMatchers.any(Configuration.class))).thenReturn(Lists.newArrayList("a"));
        Mockito.when(mockDatabaseAccessor.getBounds(ArgumentMatchers.any(Configuration.class), ArgumentMatchers.any(String.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean())).thenReturn(new ImmutablePair<String, String>("2010-01-01", "2018-01-01"));
        JobConf conf = new JobConf();
        conf.set("mapred.input.dir", "/temp");
        conf.set(LIST_COLUMN_TYPES, "date");
        conf.set("hive.sql.partitionColumn", "a");
        conf.set("hive.sql.numPartitions", "3");
        InputSplit[] splits = f.getSplits(conf, (-1));
        Assert.assertThat(splits, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(splits.length, Matchers.is(3));
        Assert.assertNull(getLowerBound());
        Assert.assertEquals(getUpperBound(), "2012-09-01");
        Assert.assertEquals(getLowerBound(), "2012-09-01");
        Assert.assertEquals(getUpperBound(), "2015-05-03");
        Assert.assertEquals(getLowerBound(), "2015-05-03");
        Assert.assertNull(getUpperBound());
    }

    @Test
    public void testIntervalSplit_AutoShrink() throws IOException, HiveJdbcDatabaseAccessException {
        PowerMockito.mockStatic(DatabaseAccessorFactory.class);
        BDDMockito.given(DatabaseAccessorFactory.getAccessor(ArgumentMatchers.any(Configuration.class))).willReturn(mockDatabaseAccessor);
        JdbcInputFormat f = new JdbcInputFormat();
        Mockito.when(mockDatabaseAccessor.getColumnNames(ArgumentMatchers.any(Configuration.class))).thenReturn(Lists.newArrayList("a"));
        JobConf conf = new JobConf();
        conf.set("mapred.input.dir", "/temp");
        conf.set(LIST_COLUMN_TYPES, "int");
        conf.set("hive.sql.partitionColumn", "a");
        conf.set("hive.sql.numPartitions", "5");
        conf.set("hive.sql.lowerBound", "2");
        conf.set("hive.sql.upperBound", "4");
        InputSplit[] splits = f.getSplits(conf, (-1));
        Assert.assertThat(splits, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(splits.length, Matchers.is(2));
        Assert.assertNull(getLowerBound());
        Assert.assertEquals(getUpperBound(), "3");
        Assert.assertEquals(getLowerBound(), "3");
        Assert.assertNull(getUpperBound());
    }

    @Test
    public void testIntervalSplit_NoSplit() throws IOException, HiveJdbcDatabaseAccessException {
        PowerMockito.mockStatic(DatabaseAccessorFactory.class);
        BDDMockito.given(DatabaseAccessorFactory.getAccessor(ArgumentMatchers.any(Configuration.class))).willReturn(mockDatabaseAccessor);
        JdbcInputFormat f = new JdbcInputFormat();
        Mockito.when(mockDatabaseAccessor.getColumnNames(ArgumentMatchers.any(Configuration.class))).thenReturn(Lists.newArrayList("a"));
        JobConf conf = new JobConf();
        conf.set("mapred.input.dir", "/temp");
        conf.set(LIST_COLUMN_TYPES, "int");
        conf.set("hive.sql.partitionColumn", "a");
        conf.set("hive.sql.numPartitions", "5");
        conf.set("hive.sql.lowerBound", "1");
        conf.set("hive.sql.upperBound", "2");
        InputSplit[] splits = f.getSplits(conf, (-1));
        Assert.assertThat(splits, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(splits.length, Matchers.is(1));
        Assert.assertNull(getPartitionColumn());
    }
}

