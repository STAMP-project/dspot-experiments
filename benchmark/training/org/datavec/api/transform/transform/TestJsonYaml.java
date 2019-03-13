/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.datavec.api.transform.transform;


import ColumnType.Integer;
import DateTimeZone.UTC;
import MathOp.Add;
import MathOp.Multiply;
import MathOp.ScalarMax;
import MathOp.Subtract;
import ReduceOp.Mean;
import SequenceOffsetTransform.OperationType.InPlace;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.datavec.api.transform.analysis.DataAnalysis;
import org.datavec.api.transform.analysis.columns.CategoricalAnalysis;
import org.datavec.api.transform.analysis.columns.DoubleAnalysis;
import org.datavec.api.transform.analysis.columns.StringAnalysis;
import org.datavec.api.transform.condition.ConditionOp;
import org.datavec.api.transform.condition.column.NullWritableColumnCondition;
import org.datavec.api.transform.filter.FilterInvalidValues;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.sequence.comparator.NumericalColumnComparator;
import org.datavec.api.transform.sequence.comparator.StringComparator;
import org.datavec.api.transform.sequence.split.SequenceSplitTimeSeparation;
import org.datavec.api.transform.sequence.window.OverlappingTimeWindowFunction;
import org.datavec.api.transform.transform.integer.ReplaceEmptyIntegerWithValueTransform;
import org.datavec.api.transform.transform.integer.ReplaceInvalidWithIntegerTransform;
import org.datavec.api.transform.transform.string.MapAllStringsExceptListTransform;
import org.datavec.api.transform.transform.string.ReplaceEmptyStringTransform;
import org.datavec.api.transform.transform.string.StringListToCategoricalSetTransform;
import org.datavec.api.transform.transform.time.DeriveColumnsFromTimeTransform;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.Text;
import org.datavec.api.writable.comparator.LongWritableComparator;
import org.joda.time.DateTimeFieldType;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Alex on 18/07/2016.
 */
public class TestJsonYaml {
    @Test
    public void testToFromJsonYaml() {
        Schema schema = new Schema.Builder().addColumnCategorical("Cat", "State1", "State2").addColumnCategorical("Cat2", "State1", "State2").addColumnDouble("Dbl").addColumnDouble("Dbl2", null, 100.0, true, false).addColumnInteger("Int").addColumnInteger("Int2", 0, 10).addColumnLong("Long").addColumnLong("Long2", (-100L), null).addColumnString("Str").addColumnString("Str2", "someregexhere", 1, null).addColumnString("Str3").addColumnTime("TimeCol", UTC).addColumnTime("TimeCol2", UTC, null, 1000L).build();
        Map<String, String> map = new HashMap<>();
        map.put("from", "to");
        map.put("anotherFrom", "anotherTo");
        TransformProcess tp = // Calculate sorted rank
        // Reducers and reduce by window:
        // Sequence split
        // Convert to/from sequence
        // Filters:
        categoricalToInteger("Cat").categoricalToOneHot("Cat2").appendStringColumnTransform("Str3", "ToAppend").integerToCategorical("Cat", Arrays.asList("State1", "State2")).stringToCategorical("Str", Arrays.asList("State1", "State2")).duplicateColumn("Str", "Str2a").removeColumns("Str2a").renameColumn("Str2", "Str2a").reorderColumns("Cat", "Dbl").conditionalCopyValueTransform("Dbl", "Dbl2", new org.datavec.api.transform.condition.column.DoubleColumnCondition("Dbl", ConditionOp.Equal, 0.0)).conditionalReplaceValueTransform("Dbl", new DoubleWritable(1.0), new org.datavec.api.transform.condition.column.DoubleColumnCondition("Dbl", ConditionOp.Equal, 1.0)).doubleColumnsMathOp("NewDouble", Add, "Dbl", "Dbl2").doubleMathOp("Dbl", Add, 1.0).integerColumnsMathOp("NewInt", Subtract, "Int", "Int2").integerMathOp("Int", Multiply, 2).transform(new ReplaceEmptyIntegerWithValueTransform("Int", 1)).transform(new ReplaceInvalidWithIntegerTransform("Int", 1)).longColumnsMathOp("Long", Multiply, "Long", "Long2").longMathOp("Long", ScalarMax, 0).transform(new MapAllStringsExceptListTransform("Str", "Other", Arrays.asList("Ok", "SomeVal"))).stringRemoveWhitespaceTransform("Str").transform(new ReplaceEmptyStringTransform("Str", "WasEmpty")).replaceStringTransform("Str", map).transform(new StringListToCategoricalSetTransform("Str", Arrays.asList("StrA", "StrB"), Arrays.asList("StrA", "StrB"), ",")).stringMapTransform("Str2a", map).transform(new DeriveColumnsFromTimeTransform.Builder("TimeCol").addIntegerDerivedColumn("Hour", DateTimeFieldType.hourOfDay()).addStringDerivedColumn("Date", "YYYY-MM-dd", UTC).build()).stringToTimeTransform("Str2a", "YYYY-MM-dd hh:mm:ss", UTC).timeMathOp("TimeCol2", Add, 1, TimeUnit.HOURS).filter(new FilterInvalidValues("Cat", "Str2a")).filter(new org.datavec.api.transform.filter.ConditionFilter(new NullWritableColumnCondition("Long"))).convertToSequence("Int", new NumericalColumnComparator("TimeCol2")).convertFromSequence().convertToSequence("Int", new StringComparator("Str2a")).splitSequence(new SequenceSplitTimeSeparation("TimeCol2", 1, TimeUnit.HOURS)).reduce(countColumns("Cat").sumColumns("Dbl").build()).reduceSequenceByWindow(countColumns("Cat2").stdevColumns("Dbl2").build(), new OverlappingTimeWindowFunction.Builder().timeColumn("TimeCol2").addWindowStartTimeColumn(true).addWindowEndTimeColumn(true).windowSize(1, TimeUnit.HOURS).offset(5, TimeUnit.MINUTES).windowSeparation(15, TimeUnit.MINUTES).excludeEmptyWindows(true).build()).convertFromSequence().calculateSortedRank("rankColName", "TimeCol2", new LongWritableComparator()).sequenceMovingWindowReduce("rankColName", 20, Mean).addConstantColumn("someIntColumn", Integer, new IntWritable(0)).integerToOneHot("someIntColumn", 0, 3).filter(new org.datavec.api.transform.condition.sequence.SequenceLengthCondition(ConditionOp.LessThan, 1)).addConstantColumn("testColSeq", Integer, new DoubleWritable(0)).offsetSequence(Collections.singletonList("testColSeq"), 1, InPlace).addConstantColumn("someTextCol", ColumnType.String, new Text("some values")).build();
        String asJson = tp.toJson();
        String asYaml = tp.toYaml();
        // System.out.println(asJson);
        // System.out.println("\n\n\n");
        // System.out.println(asYaml);
        TransformProcess tpFromJson = TransformProcess.fromJson(asJson);
        TransformProcess tpFromYaml = TransformProcess.fromYaml(asYaml);
        List<DataAction> daList = tp.getActionList();
        List<DataAction> daListJson = tpFromJson.getActionList();
        List<DataAction> daListYaml = tpFromYaml.getActionList();
        for (int i = 0; i < (daList.size()); i++) {
            DataAction da1 = daList.get(i);
            DataAction da2 = daListJson.get(i);
            DataAction da3 = daListYaml.get(i);
            // System.out.println(i + "\t" + da1);
            Assert.assertEquals(da1, da2);
            Assert.assertEquals(da1, da3);
        }
        Assert.assertEquals(tp, tpFromJson);
        Assert.assertEquals(tp, tpFromYaml);
    }

    @Test
    public void testJsonYamlAnalysis() throws Exception {
        Schema s = new Schema.Builder().addColumnsDouble("first", "second").addColumnString("third").addColumnCategorical("fourth", "cat0", "cat1").build();
        DoubleAnalysis d1 = new DoubleAnalysis.Builder().max((-1)).max(1).countPositive(10).mean(3.0).build();
        DoubleAnalysis d2 = new DoubleAnalysis.Builder().max((-5)).max(5).countPositive(4).mean(2.0).build();
        StringAnalysis sa = new StringAnalysis.Builder().minLength(0).maxLength(10).build();
        Map<String, Long> countMap = new HashMap<>();
        countMap.put("cat0", 100L);
        countMap.put("cat1", 200L);
        CategoricalAnalysis ca = new CategoricalAnalysis(countMap);
        DataAnalysis da = new DataAnalysis(s, Arrays.asList(d1, d2, sa, ca));
        String strJson = da.toJson();
        String strYaml = da.toYaml();
        // System.out.println(str);
        DataAnalysis daFromJson = DataAnalysis.fromJson(strJson);
        DataAnalysis daFromYaml = DataAnalysis.fromYaml(strYaml);
        // System.out.println(da2);
        Assert.assertEquals(da.getColumnAnalysis(), daFromJson.getColumnAnalysis());
        Assert.assertEquals(da.getColumnAnalysis(), daFromYaml.getColumnAnalysis());
    }
}

