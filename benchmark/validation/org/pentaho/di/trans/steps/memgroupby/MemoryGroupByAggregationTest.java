/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.memgroupby;


import Const.KETTLE_AGGREGATION_ALL_NULLS_ARE_ZERO;
import Const.KETTLE_AGGREGATION_MIN_NULL_IS_VALUED;
import Const.KETTLE_COMPATIBILITY_MEMORY_GROUP_BY_SUM_AVERAGE_RETURN_NUMBER_TYPE;
import MemoryGroupByMeta.TYPE_GROUP_AVERAGE;
import MemoryGroupByMeta.TYPE_GROUP_COUNT_ALL;
import MemoryGroupByMeta.TYPE_GROUP_COUNT_ANY;
import MemoryGroupByMeta.TYPE_GROUP_COUNT_DISTINCT;
import MemoryGroupByMeta.TYPE_GROUP_MAX;
import MemoryGroupByMeta.TYPE_GROUP_MIN;
import MemoryGroupByMeta.TYPE_GROUP_SUM;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.TreeBasedTable;
import java.util.Date;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.value.ValueMetaDate;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaNumber;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


/**
 *
 *
 * @author nhudak
 */
public class MemoryGroupByAggregationTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private Variables variables;

    private Map<String, Integer> aggregates;

    public static final String STEP_NAME = "testStep";

    private static final ImmutableMap<String, Integer> default_aggregates;

    static {
        default_aggregates = ImmutableMap.<String, Integer>builder().put("min", TYPE_GROUP_MIN).put("max", TYPE_GROUP_MAX).put("sum", TYPE_GROUP_SUM).put("ave", TYPE_GROUP_AVERAGE).put("count", TYPE_GROUP_COUNT_ALL).put("count_any", TYPE_GROUP_COUNT_ANY).put("count_distinct", TYPE_GROUP_COUNT_DISTINCT).build();
    }

    private RowMeta rowMeta;

    private TreeBasedTable<Integer, Integer, Optional<Object>> data;

    @Test
    public void testDefault() throws Exception {
        addColumn(new ValueMetaInteger("intg"), 0L, 1L, 1L, 10L);
        addColumn(new ValueMetaInteger("nul"));
        addColumn(new ValueMetaInteger("mix1"), (-1L), 2L);
        addColumn(new ValueMetaInteger("mix2"), null, 7L);
        addColumn(new ValueMetaNumber("mix3"), (-1.0), 2.5);
        addColumn(new ValueMetaDate("date1"), new Date(1L), new Date(2L));
        RowMetaAndData output = runStep();
        Assert.assertThat(output.getInteger("intg_min"), CoreMatchers.is(0L));
        Assert.assertThat(output.getInteger("intg_max"), CoreMatchers.is(10L));
        Assert.assertThat(output.getInteger("intg_sum"), CoreMatchers.is(12L));
        Assert.assertThat(output.getInteger("intg_ave"), CoreMatchers.is(3L));
        Assert.assertThat(output.getInteger("intg_count"), CoreMatchers.is(4L));
        Assert.assertThat(output.getInteger("intg_count_any"), CoreMatchers.is(4L));
        Assert.assertThat(output.getInteger("intg_count_distinct"), CoreMatchers.is(3L));
        Assert.assertThat(output.getInteger("nul_min"), CoreMatchers.nullValue());
        Assert.assertThat(output.getInteger("nul_max"), CoreMatchers.nullValue());
        Assert.assertThat(output.getInteger("nul_sum"), CoreMatchers.nullValue());
        Assert.assertThat(output.getInteger("nul_ave"), CoreMatchers.nullValue());
        Assert.assertThat(output.getInteger("nul_count"), CoreMatchers.is(0L));
        Assert.assertThat(output.getInteger("nul_count_any"), CoreMatchers.is(4L));
        Assert.assertThat(output.getInteger("nul_count_distinct"), CoreMatchers.is(0L));
        Assert.assertThat(output.getInteger("mix1_max"), CoreMatchers.is(2L));
        Assert.assertThat(output.getInteger("mix1_min"), CoreMatchers.is((-1L)));
        Assert.assertThat(output.getInteger("mix1_sum"), CoreMatchers.is(1L));
        Assert.assertThat(output.getInteger("mix1_ave"), CoreMatchers.is(0L));
        Assert.assertThat(output.getInteger("mix1_count"), CoreMatchers.is(2L));
        Assert.assertThat(output.getInteger("mix1_count_any"), CoreMatchers.is(4L));
        Assert.assertThat(output.getInteger("mix1_count_distinct"), CoreMatchers.is(2L));
        Assert.assertThat(output.getInteger("mix2_max"), CoreMatchers.is(7L));
        Assert.assertThat(output.getInteger("mix2_min"), CoreMatchers.is(7L));
        Assert.assertThat(output.getInteger("mix2_sum"), CoreMatchers.is(7L));
        Assert.assertThat(output.getNumber("mix2_ave", Double.NaN), CoreMatchers.is(7.0));
        Assert.assertThat(output.getInteger("mix2_count"), CoreMatchers.is(1L));
        Assert.assertThat(output.getInteger("mix2_count_any"), CoreMatchers.is(4L));
        Assert.assertThat(output.getInteger("mix2_count_distinct"), CoreMatchers.is(1L));
        Assert.assertThat(output.getNumber("mix3_max", Double.NaN), CoreMatchers.is(2.5));
        Assert.assertThat(output.getNumber("mix3_min", Double.NaN), CoreMatchers.is((-1.0)));
        Assert.assertThat(output.getNumber("mix3_sum", Double.NaN), CoreMatchers.is(1.5));
        Assert.assertThat(output.getNumber("mix3_ave", Double.NaN), CoreMatchers.is(0.75));
        Assert.assertThat(output.getInteger("mix3_count"), CoreMatchers.is(2L));
        Assert.assertThat(output.getInteger("mix3_count_any"), CoreMatchers.is(4L));
        Assert.assertThat(output.getInteger("mix3_count_distinct"), CoreMatchers.is(2L));
        Assert.assertThat(output.getNumber("date1_min", Double.NaN), CoreMatchers.is(1.0));
        Assert.assertThat(output.getNumber("date1_max", Double.NaN), CoreMatchers.is(2.0));
        Assert.assertThat(output.getNumber("date1_sum", Double.NaN), CoreMatchers.is(3.0));
        Assert.assertThat(output.getNumber("date1_ave", Double.NaN), CoreMatchers.is(1.5));
        Assert.assertThat(output.getInteger("date1_count"), CoreMatchers.is(2L));
        Assert.assertThat(output.getInteger("date1_count_any"), CoreMatchers.is(4L));
        Assert.assertThat(output.getInteger("date1_count_distinct"), CoreMatchers.is(2L));
    }

    @Test
    public void testCompatibility() throws KettleException {
        variables.setVariable(KETTLE_COMPATIBILITY_MEMORY_GROUP_BY_SUM_AVERAGE_RETURN_NUMBER_TYPE, "Y");
        addColumn(new ValueMetaInteger("intg"), 0L, 1L, 1L, 10L);
        addColumn(new ValueMetaInteger("nul"));
        addColumn(new ValueMetaInteger("mix1"), (-1L), 2L);
        addColumn(new ValueMetaInteger("mix2"), null, 7L);
        addColumn(new ValueMetaNumber("mix3"), (-1.0), 2.5);
        RowMetaAndData output = runStep();
        Assert.assertThat(output.getInteger("intg_min"), CoreMatchers.is(0L));
        Assert.assertThat(output.getInteger("intg_max"), CoreMatchers.is(10L));
        Assert.assertThat(output.getInteger("intg_sum"), CoreMatchers.is(12L));
        Assert.assertThat(output.getInteger("intg_ave"), CoreMatchers.is(3L));
        Assert.assertThat(output.getInteger("intg_count"), CoreMatchers.is(4L));
        Assert.assertThat(output.getInteger("intg_count_any"), CoreMatchers.is(4L));
        Assert.assertThat(output.getInteger("intg_count_distinct"), CoreMatchers.is(3L));
        Assert.assertThat(output.getInteger("nul_min"), CoreMatchers.nullValue());
        Assert.assertThat(output.getInteger("nul_max"), CoreMatchers.nullValue());
        Assert.assertThat(output.getInteger("nul_sum"), CoreMatchers.nullValue());
        Assert.assertThat(output.getInteger("nul_ave"), CoreMatchers.nullValue());
        Assert.assertThat(output.getInteger("nul_count"), CoreMatchers.is(0L));
        Assert.assertThat(output.getInteger("nul_count_any"), CoreMatchers.is(4L));
        Assert.assertThat(output.getInteger("nul_count_distinct"), CoreMatchers.is(0L));
        Assert.assertThat(output.getInteger("mix1_max"), CoreMatchers.is(2L));
        Assert.assertThat(output.getInteger("mix1_min"), CoreMatchers.is((-1L)));
        Assert.assertThat(output.getInteger("mix1_sum"), CoreMatchers.is(1L));
        Assert.assertThat(output.getNumber("mix1_ave", Double.NaN), CoreMatchers.is(0.5));
        Assert.assertThat(output.getInteger("mix1_count"), CoreMatchers.is(2L));
        Assert.assertThat(output.getInteger("mix1_count_any"), CoreMatchers.is(4L));
        Assert.assertThat(output.getInteger("mix1_count_distinct"), CoreMatchers.is(2L));
        Assert.assertThat(output.getInteger("mix2_max"), CoreMatchers.is(7L));
        Assert.assertThat(output.getInteger("mix2_min"), CoreMatchers.is(7L));
        Assert.assertThat(output.getInteger("mix2_sum"), CoreMatchers.is(7L));
        Assert.assertThat(output.getNumber("mix2_ave", Double.NaN), CoreMatchers.is(7.0));
        Assert.assertThat(output.getInteger("mix2_count"), CoreMatchers.is(1L));
        Assert.assertThat(output.getInteger("mix2_count_any"), CoreMatchers.is(4L));
        Assert.assertThat(output.getInteger("mix2_count_distinct"), CoreMatchers.is(1L));
        Assert.assertThat(output.getNumber("mix3_max", Double.NaN), CoreMatchers.is(2.5));
        Assert.assertThat(output.getNumber("mix3_min", Double.NaN), CoreMatchers.is((-1.0)));
        Assert.assertThat(output.getNumber("mix3_sum", Double.NaN), CoreMatchers.is(1.5));
        Assert.assertThat(output.getNumber("mix3_ave", Double.NaN), CoreMatchers.is(0.75));
        Assert.assertThat(output.getInteger("mix3_count"), CoreMatchers.is(2L));
        Assert.assertThat(output.getInteger("mix3_count_any"), CoreMatchers.is(4L));
        Assert.assertThat(output.getInteger("mix3_count_distinct"), CoreMatchers.is(2L));
    }

    @Test
    public void testNullMin() throws Exception {
        variables.setVariable(KETTLE_AGGREGATION_MIN_NULL_IS_VALUED, "Y");
        addColumn(new ValueMetaInteger("intg"), null, 0L, 1L, (-1L));
        addColumn(new ValueMetaString("str"), "A", null, "B", null);
        aggregates = Maps.toMap(ImmutableList.of("min", "max"), Functions.forMap(MemoryGroupByAggregationTest.default_aggregates));
        RowMetaAndData output = runStep();
        Assert.assertThat(output.getInteger("intg_min"), CoreMatchers.nullValue());
        Assert.assertThat(output.getInteger("intg_max"), CoreMatchers.is(1L));
        Assert.assertThat(output.getString("str_min", null), CoreMatchers.nullValue());
        Assert.assertThat(output.getString("str_max", "invalid"), CoreMatchers.is("B"));
    }

    @Test
    public void testNullsAreZeroCompatible() throws Exception {
        variables.setVariable(KETTLE_AGGREGATION_ALL_NULLS_ARE_ZERO, "Y");
        variables.setVariable(KETTLE_COMPATIBILITY_MEMORY_GROUP_BY_SUM_AVERAGE_RETURN_NUMBER_TYPE, "Y");
        addColumn(new ValueMetaInteger("nul"));
        addColumn(new ValueMetaInteger("both"), (-2L), 0L, null, 10L);
        RowMetaAndData output = runStep();
        Assert.assertThat(output.getInteger("nul_min"), CoreMatchers.is(0L));
        Assert.assertThat(output.getInteger("nul_max"), CoreMatchers.is(0L));
        Assert.assertThat(output.getInteger("nul_sum"), CoreMatchers.is(0L));
        Assert.assertThat(output.getInteger("nul_ave"), CoreMatchers.is(0L));
        Assert.assertThat(output.getInteger("nul_count"), CoreMatchers.is(0L));
        Assert.assertThat(output.getInteger("nul_count_any"), CoreMatchers.is(4L));
        Assert.assertThat(output.getInteger("nul_count_distinct"), CoreMatchers.is(0L));
        Assert.assertThat(output.getInteger("both_max"), CoreMatchers.is(10L));
        Assert.assertThat(output.getInteger("both_min"), CoreMatchers.is((-2L)));
        Assert.assertThat(output.getInteger("both_sum"), CoreMatchers.is(8L));
        Assert.assertThat(output.getInteger("both_ave"), CoreMatchers.is(3L));
        Assert.assertThat(output.getInteger("both_count"), CoreMatchers.is(3L));
        Assert.assertThat(output.getInteger("both_count_any"), CoreMatchers.is(4L));
        Assert.assertThat(output.getInteger("both_count_distinct"), CoreMatchers.is(3L));
    }

    @Test
    public void testNullsAreZeroDefault() throws Exception {
        variables.setVariable(KETTLE_AGGREGATION_ALL_NULLS_ARE_ZERO, "Y");
        addColumn(new ValueMetaInteger("nul"));
        addColumn(new ValueMetaInteger("both"), (-2L), 0L, null, 10L);
        addColumn(new ValueMetaNumber("both_num"), (-2.0), 0.0, null, 10.0);
        RowMetaAndData output = runStep();
        Assert.assertThat(output.getInteger("nul_min"), CoreMatchers.is(0L));
        Assert.assertThat(output.getInteger("nul_max"), CoreMatchers.is(0L));
        Assert.assertThat(output.getInteger("nul_sum"), CoreMatchers.is(0L));
        Assert.assertThat(output.getInteger("nul_ave"), CoreMatchers.is(0L));
        Assert.assertThat(output.getInteger("nul_count"), CoreMatchers.is(0L));
        Assert.assertThat(output.getInteger("nul_count_any"), CoreMatchers.is(4L));
        Assert.assertThat(output.getInteger("nul_count_distinct"), CoreMatchers.is(0L));
        Assert.assertThat(output.getInteger("both_max"), CoreMatchers.is(10L));
        Assert.assertThat(output.getInteger("both_min"), CoreMatchers.is((-2L)));
        Assert.assertThat(output.getInteger("both_sum"), CoreMatchers.is(8L));
        Assert.assertThat(output.getInteger("both_ave"), CoreMatchers.is(2L));
        Assert.assertThat(output.getInteger("both_count"), CoreMatchers.is(3L));
        Assert.assertThat(output.getInteger("both_count_any"), CoreMatchers.is(4L));
        Assert.assertThat(output.getInteger("both_count_distinct"), CoreMatchers.is(3L));
        Assert.assertThat(output.getNumber("both_num_max", Double.NaN), CoreMatchers.is(10.0));
        Assert.assertThat(output.getNumber("both_num_min", Double.NaN), CoreMatchers.is((-2.0)));
        Assert.assertThat(output.getNumber("both_num_sum", Double.NaN), CoreMatchers.is(8.0));
        /* delta */
        Assert.assertEquals(2.666666, output.getNumber("both_num_ave", Double.NaN), 1.0E-6);
        Assert.assertThat(output.getInteger("both_num_count"), CoreMatchers.is(3L));
        Assert.assertThat(output.getInteger("both_num_count_any"), CoreMatchers.is(4L));
        Assert.assertThat(output.getInteger("both_num_count_distinct"), CoreMatchers.is(3L));
    }

    @Test
    public void testSQLCompatible() throws Exception {
        addColumn(new ValueMetaInteger("value"), null, (-2L), null, 0L, null, 10L, null, null, 0L, null);
        RowMetaAndData output = runStep();
        Assert.assertThat(output.getInteger("value_max"), CoreMatchers.is(10L));
        Assert.assertThat(output.getInteger("value_min"), CoreMatchers.is((-2L)));
        Assert.assertThat(output.getInteger("value_sum"), CoreMatchers.is(8L));
        Assert.assertThat(output.getInteger("value_ave"), CoreMatchers.is(2L));
        Assert.assertThat(output.getInteger("value_count"), CoreMatchers.is(4L));
        Assert.assertThat(output.getInteger("value_count_any"), CoreMatchers.is(10L));
        Assert.assertThat(output.getInteger("value_count_distinct"), CoreMatchers.is(3L));
    }
}

