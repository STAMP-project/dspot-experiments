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
package org.datavec.api.transform.serde;


import DateTimeZone.UTC;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.datavec.api.transform.condition.BooleanCondition;
import org.datavec.api.transform.condition.Condition;
import org.datavec.api.transform.condition.ConditionOp;
import org.datavec.api.transform.condition.string.StringRegexColumnCondition;
import org.datavec.api.transform.filter.ConditionFilter;
import org.datavec.api.transform.filter.Filter;
import org.datavec.api.transform.filter.FilterInvalidValues;
import org.datavec.api.transform.rank.CalculateSortedRank;
import org.datavec.api.transform.reduce.IAssociativeReducer;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.sequence.ConvertFromSequence;
import org.datavec.api.transform.sequence.SequenceComparator;
import org.datavec.api.transform.sequence.SequenceSplit;
import org.datavec.api.transform.sequence.comparator.NumericalColumnComparator;
import org.datavec.api.transform.sequence.comparator.StringComparator;
import org.datavec.api.transform.sequence.split.SequenceSplitTimeSeparation;
import org.datavec.api.transform.sequence.split.SplitMaxLengthSequence;
import org.datavec.api.transform.transform.categorical.CategoricalToIntegerTransform;
import org.datavec.api.transform.transform.categorical.CategoricalToOneHotTransform;
import org.datavec.api.transform.transform.categorical.IntegerToCategoricalTransform;
import org.datavec.api.transform.transform.categorical.StringToCategoricalTransform;
import org.datavec.api.transform.transform.column.DuplicateColumnsTransform;
import org.datavec.api.transform.transform.column.RemoveColumnsTransform;
import org.datavec.api.transform.transform.column.RenameColumnsTransform;
import org.datavec.api.transform.transform.column.ReorderColumnsTransform;
import org.datavec.api.transform.transform.integer.ReplaceEmptyIntegerWithValueTransform;
import org.datavec.api.transform.transform.integer.ReplaceInvalidWithIntegerTransform;
import org.datavec.api.transform.transform.time.DeriveColumnsFromTimeTransform;
import org.datavec.api.writable.comparator.DoubleWritableComparator;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;

import static MathOp.Add;
import static MathOp.Multiply;
import static MathOp.ScalarMax;
import static MathOp.Subtract;


/**
 * Created by Alex on 20/07/2016.
 */
public class TestYamlJsonSerde {
    public static YamlSerializer y = new YamlSerializer();

    public static JsonSerializer j = new JsonSerializer();

    @Test
    public void testTransforms() {
        Map<String, String> map = new HashMap<>();
        map.put("A", "A1");
        map.put("B", "B1");
        Transform[] transforms = new Transform[]{ new CategoricalToIntegerTransform("ColName"), new CategoricalToOneHotTransform("ColName"), new IntegerToCategoricalTransform("ColName", Arrays.asList("State0", "State1")), new StringToCategoricalTransform("ColName", Arrays.asList("State0", "State1")), new DuplicateColumnsTransform(Arrays.asList("Dup1", "Dup2"), Arrays.asList("NewName1", "NewName2")), new RemoveColumnsTransform("R1", "R2"), new RenameColumnsTransform(Arrays.asList("N1", "N2"), Arrays.asList("NewN1", "NewN2")), new ReorderColumnsTransform("A", "B"), new DoubleColumnsMathOpTransform("NewName", Subtract, "A", "B"), new DoubleMathOpTransform("ColName", Multiply, 2.0), new Log2Normalizer("ColName", 1.0, 0.0, 2.0), new MinMaxNormalizer("ColName", 0, 100, (-1), 1), new StandardizeNormalizer("ColName", 20, 5), new SubtractMeanNormalizer("ColName", 10), new org.datavec.api.transform.transform.integer.IntegerColumnsMathOpTransform("NewName", Multiply, "A", "B"), new org.datavec.api.transform.transform.integer.IntegerMathOpTransform("ColName", Add, 10), new ReplaceEmptyIntegerWithValueTransform("Col", 3), new ReplaceInvalidWithIntegerTransform("Col", 3), new org.datavec.api.transform.transform.longtransform.LongColumnsMathOpTransform("NewName", Multiply, "A", "B"), new org.datavec.api.transform.transform.longtransform.LongMathOpTransform("Col", ScalarMax, 5L), new MapAllStringsExceptListTransform("Col", "NewVal", Arrays.asList("E1", "E2")), new RemoveWhiteSpaceTransform("Col"), new ReplaceEmptyStringTransform("Col", "WasEmpty"), new ReplaceStringTransform("Col_A", map), new StringListToCategoricalSetTransform("Col", Arrays.asList("A", "B"), Arrays.asList("A", "B"), ","), new StringMapTransform("Col", map), new DeriveColumnsFromTimeTransform.Builder("TimeColName").addIntegerDerivedColumn("Hour", DateTimeFieldType.hourOfDay()).addStringDerivedColumn("DateTime", "YYYY-MM-dd hh:mm:ss", UTC).build(), new org.datavec.api.transform.transform.time.StringToTimeTransform("TimeCol", "YYYY-MM-dd hh:mm:ss", DateTimeZone.UTC), new org.datavec.api.transform.transform.time.TimeMathOpTransform("TimeCol", Add, 1, TimeUnit.HOURS) };
        for (Transform t : transforms) {
            String yaml = TestYamlJsonSerde.y.serialize(t);
            String json = TestYamlJsonSerde.j.serialize(t);
            // System.out.println(yaml);
            // System.out.println(json);
            // System.out.println();
            // Transform t2 = y.deserializeTransform(yaml);
            Transform t3 = TestYamlJsonSerde.j.deserializeTransform(json);
            // assertEquals(t, t2);
            Assert.assertEquals(t, t3);
        }
        String tArrAsYaml = TestYamlJsonSerde.y.serialize(transforms);
        String tArrAsJson = TestYamlJsonSerde.j.serialize(transforms);
        String tListAsYaml = TestYamlJsonSerde.y.serializeTransformList(Arrays.asList(transforms));
        String tListAsJson = TestYamlJsonSerde.j.serializeTransformList(Arrays.asList(transforms));
        // System.out.println("\n\n\n\n");
        // System.out.println(tListAsYaml);
        List<Transform> lFromYaml = TestYamlJsonSerde.y.deserializeTransformList(tListAsYaml);
        List<Transform> lFromJson = TestYamlJsonSerde.j.deserializeTransformList(tListAsJson);
        Assert.assertEquals(Arrays.asList(transforms), TestYamlJsonSerde.y.deserializeTransformList(tArrAsYaml));
        Assert.assertEquals(Arrays.asList(transforms), TestYamlJsonSerde.j.deserializeTransformList(tArrAsJson));
        Assert.assertEquals(Arrays.asList(transforms), lFromYaml);
        Assert.assertEquals(Arrays.asList(transforms), lFromJson);
    }

    @Test
    public void testTransformsRegexBrackets() {
        Schema schema = new Schema.Builder().addColumnString("someCol").addColumnString("otherCol").build();
        Transform[] transforms = new Transform[]{ new org.datavec.api.transform.transform.condition.ConditionalCopyValueTransform("someCol", "otherCol", new StringRegexColumnCondition("someCol", "\\d")), new org.datavec.api.transform.transform.condition.ConditionalCopyValueTransform("someCol", "otherCol", new StringRegexColumnCondition("someCol", "\\D+")), new org.datavec.api.transform.transform.condition.ConditionalCopyValueTransform("someCol", "otherCol", new StringRegexColumnCondition("someCol", "\".*\"")), new org.datavec.api.transform.transform.condition.ConditionalCopyValueTransform("someCol", "otherCol", new StringRegexColumnCondition("someCol", "[]{}()][}{)(")) };
        for (Transform t : transforms) {
            String json = TestYamlJsonSerde.j.serialize(t);
            Transform t3 = TestYamlJsonSerde.j.deserializeTransform(json);
            Assert.assertEquals(t, t3);
            TransformProcess tp = new TransformProcess.Builder(schema).transform(t).build();
            String tpJson = TestYamlJsonSerde.j.serialize(tp);
            TransformProcess fromJson = TransformProcess.fromJson(tpJson);
            Assert.assertEquals(tp, fromJson);
        }
        String tArrAsYaml = TestYamlJsonSerde.y.serialize(transforms);
        String tArrAsJson = TestYamlJsonSerde.j.serialize(transforms);
        String tListAsYaml = TestYamlJsonSerde.y.serializeTransformList(Arrays.asList(transforms));
        String tListAsJson = TestYamlJsonSerde.j.serializeTransformList(Arrays.asList(transforms));
        List<Transform> lFromYaml = TestYamlJsonSerde.y.deserializeTransformList(tListAsYaml);
        List<Transform> lFromJson = TestYamlJsonSerde.j.deserializeTransformList(tListAsJson);
        Assert.assertEquals(Arrays.asList(transforms), TestYamlJsonSerde.y.deserializeTransformList(tArrAsYaml));
        Assert.assertEquals(Arrays.asList(transforms), TestYamlJsonSerde.j.deserializeTransformList(tArrAsJson));
        Assert.assertEquals(Arrays.asList(transforms), lFromYaml);
        Assert.assertEquals(Arrays.asList(transforms), lFromJson);
    }

    @Test
    public void testFilters() {
        Filter[] filters = new Filter[]{ new FilterInvalidValues("A", "B"), new ConditionFilter(new DoubleColumnCondition("Col", ConditionOp.GreaterOrEqual, 10.0)) };
        for (Filter f : filters) {
            String yaml = TestYamlJsonSerde.y.serialize(f);
            String json = TestYamlJsonSerde.j.serialize(f);
            // System.out.println(yaml);
            // System.out.println(json);
            // System.out.println();
            Filter t2 = TestYamlJsonSerde.y.deserializeFilter(yaml);
            Filter t3 = TestYamlJsonSerde.j.deserializeFilter(json);
            Assert.assertEquals(f, t2);
            Assert.assertEquals(f, t3);
        }
        String arrAsYaml = TestYamlJsonSerde.y.serialize(filters);
        String arrAsJson = TestYamlJsonSerde.j.serialize(filters);
        String listAsYaml = TestYamlJsonSerde.y.serializeFilterList(Arrays.asList(filters));
        String listAsJson = TestYamlJsonSerde.j.serializeFilterList(Arrays.asList(filters));
        // System.out.println("\n\n\n\n");
        // System.out.println(listAsYaml);
        List<Filter> lFromYaml = TestYamlJsonSerde.y.deserializeFilterList(listAsYaml);
        List<Filter> lFromJson = TestYamlJsonSerde.j.deserializeFilterList(listAsJson);
        Assert.assertEquals(Arrays.asList(filters), TestYamlJsonSerde.y.deserializeFilterList(arrAsYaml));
        Assert.assertEquals(Arrays.asList(filters), TestYamlJsonSerde.j.deserializeFilterList(arrAsJson));
        Assert.assertEquals(Arrays.asList(filters), lFromYaml);
        Assert.assertEquals(Arrays.asList(filters), lFromJson);
    }

    @Test
    public void testConditions() {
        Set<String> setStr = new HashSet<>();
        setStr.add("A");
        setStr.add("B");
        Set<Double> setD = new HashSet<>();
        setD.add(1.0);
        setD.add(2.0);
        Set<Integer> setI = new HashSet<>();
        setI.add(1);
        setI.add(2);
        Set<Long> setL = new HashSet<>();
        setL.add(1L);
        setL.add(2L);
        Condition[] conditions = new Condition[]{ new CategoricalColumnCondition("Col", ConditionOp.Equal, "A"), new CategoricalColumnCondition("Col", ConditionOp.NotInSet, setStr), new DoubleColumnCondition("Col", ConditionOp.Equal, 1.0), new DoubleColumnCondition("Col", ConditionOp.InSet, setD), new IntegerColumnCondition("Col", ConditionOp.Equal, 1), new IntegerColumnCondition("Col", ConditionOp.InSet, setI), new LongColumnCondition("Col", ConditionOp.Equal, 1), new LongColumnCondition("Col", ConditionOp.InSet, setL), new NullWritableColumnCondition("Col"), new StringColumnCondition("Col", ConditionOp.NotEqual, "A"), new StringColumnCondition("Col", ConditionOp.InSet, setStr), new TimeColumnCondition("Col", ConditionOp.Equal, 1L), new TimeColumnCondition("Col", ConditionOp.InSet, setL), new StringRegexColumnCondition("Col", "Regex"), BooleanCondition.OR(BooleanCondition.AND(new CategoricalColumnCondition("Col", ConditionOp.Equal, "A"), new LongColumnCondition("Col2", ConditionOp.Equal, 1)), BooleanCondition.NOT(new TimeColumnCondition("Col3", ConditionOp.Equal, 1L))) };
        for (Condition c : conditions) {
            String yaml = TestYamlJsonSerde.y.serialize(c);
            String json = TestYamlJsonSerde.j.serialize(c);
            // System.out.println(yaml);
            // System.out.println(json);
            // System.out.println();
            Condition t2 = TestYamlJsonSerde.y.deserializeCondition(yaml);
            Condition t3 = TestYamlJsonSerde.j.deserializeCondition(json);
            Assert.assertEquals(c, t2);
            Assert.assertEquals(c, t3);
        }
        String arrAsYaml = TestYamlJsonSerde.y.serialize(conditions);
        String arrAsJson = TestYamlJsonSerde.j.serialize(conditions);
        String listAsYaml = TestYamlJsonSerde.y.serializeConditionList(Arrays.asList(conditions));
        String listAsJson = TestYamlJsonSerde.j.serializeConditionList(Arrays.asList(conditions));
        // System.out.println("\n\n\n\n");
        // System.out.println(listAsYaml);
        List<Condition> lFromYaml = TestYamlJsonSerde.y.deserializeConditionList(listAsYaml);
        List<Condition> lFromJson = TestYamlJsonSerde.j.deserializeConditionList(listAsJson);
        Assert.assertEquals(Arrays.asList(conditions), TestYamlJsonSerde.y.deserializeConditionList(arrAsYaml));
        Assert.assertEquals(Arrays.asList(conditions), TestYamlJsonSerde.j.deserializeConditionList(arrAsJson));
        Assert.assertEquals(Arrays.asList(conditions), lFromYaml);
        Assert.assertEquals(Arrays.asList(conditions), lFromJson);
    }

    @Test
    public void testReducer() {
        IAssociativeReducer[] reducers = new IAssociativeReducer[]{ keyColumns("KeyCol").stdevColumns("Stdev").minColumns("min").countUniqueColumns("B").build() };
        for (IAssociativeReducer r : reducers) {
            String yaml = TestYamlJsonSerde.y.serialize(r);
            String json = TestYamlJsonSerde.j.serialize(r);
            // System.out.println(yaml);
            // System.out.println(json);
            // System.out.println();
            IAssociativeReducer t2 = TestYamlJsonSerde.y.deserializeReducer(yaml);
            IAssociativeReducer t3 = TestYamlJsonSerde.j.deserializeReducer(json);
            Assert.assertEquals(r, t2);
            Assert.assertEquals(r, t3);
        }
        String arrAsYaml = TestYamlJsonSerde.y.serialize(reducers);
        String arrAsJson = TestYamlJsonSerde.j.serialize(reducers);
        String listAsYaml = TestYamlJsonSerde.y.serializeReducerList(Arrays.asList(reducers));
        String listAsJson = TestYamlJsonSerde.j.serializeReducerList(Arrays.asList(reducers));
        // System.out.println("\n\n\n\n");
        // System.out.println(listAsYaml);
        List<IAssociativeReducer> lFromYaml = TestYamlJsonSerde.y.deserializeReducerList(listAsYaml);
        List<IAssociativeReducer> lFromJson = TestYamlJsonSerde.j.deserializeReducerList(listAsJson);
        Assert.assertEquals(Arrays.asList(reducers), TestYamlJsonSerde.y.deserializeReducerList(arrAsYaml));
        Assert.assertEquals(Arrays.asList(reducers), TestYamlJsonSerde.j.deserializeReducerList(arrAsJson));
        Assert.assertEquals(Arrays.asList(reducers), lFromYaml);
        Assert.assertEquals(Arrays.asList(reducers), lFromJson);
    }

    @Test
    public void testSequenceComparator() {
        SequenceComparator[] comparators = new SequenceComparator[]{ new NumericalColumnComparator("Col", true), new StringComparator("Col") };
        for (SequenceComparator f : comparators) {
            String yaml = TestYamlJsonSerde.y.serialize(f);
            String json = TestYamlJsonSerde.j.serialize(f);
            // System.out.println(yaml);
            // System.out.println(json);
            // System.out.println();
            SequenceComparator t2 = TestYamlJsonSerde.y.deserializeSequenceComparator(yaml);
            SequenceComparator t3 = TestYamlJsonSerde.j.deserializeSequenceComparator(json);
            Assert.assertEquals(f, t2);
            Assert.assertEquals(f, t3);
        }
        String arrAsYaml = TestYamlJsonSerde.y.serialize(comparators);
        String arrAsJson = TestYamlJsonSerde.j.serialize(comparators);
        String listAsYaml = TestYamlJsonSerde.y.serializeSequenceComparatorList(Arrays.asList(comparators));
        String listAsJson = TestYamlJsonSerde.j.serializeSequenceComparatorList(Arrays.asList(comparators));
        // System.out.println("\n\n\n\n");
        // System.out.println(listAsYaml);
        List<SequenceComparator> lFromYaml = TestYamlJsonSerde.y.deserializeSequenceComparatorList(listAsYaml);
        List<SequenceComparator> lFromJson = TestYamlJsonSerde.j.deserializeSequenceComparatorList(listAsJson);
        Assert.assertEquals(Arrays.asList(comparators), TestYamlJsonSerde.y.deserializeSequenceComparatorList(arrAsYaml));
        Assert.assertEquals(Arrays.asList(comparators), TestYamlJsonSerde.j.deserializeSequenceComparatorList(arrAsJson));
        Assert.assertEquals(Arrays.asList(comparators), lFromYaml);
        Assert.assertEquals(Arrays.asList(comparators), lFromJson);
    }

    @Test
    public void testCalculateSortedRank() {
        CalculateSortedRank rank = new CalculateSortedRank("RankCol", "SortOnCol", new DoubleWritableComparator());
        String asYaml = TestYamlJsonSerde.y.serialize(rank);
        String asJson = TestYamlJsonSerde.j.serialize(rank);
        CalculateSortedRank yRank = TestYamlJsonSerde.y.deserializeSortedRank(asYaml);
        CalculateSortedRank jRank = TestYamlJsonSerde.j.deserializeSortedRank(asJson);
        Assert.assertEquals(rank, yRank);
        Assert.assertEquals(rank, jRank);
    }

    @Test
    public void testSequenceSplit() {
        SequenceSplit[] splits = new SequenceSplit[]{ new SequenceSplitTimeSeparation("col", 1, TimeUnit.HOURS), new SplitMaxLengthSequence(100, false) };
        for (SequenceSplit f : splits) {
            String yaml = TestYamlJsonSerde.y.serialize(f);
            String json = TestYamlJsonSerde.j.serialize(f);
            // System.out.println(yaml);
            // System.out.println(json);
            // System.out.println();
            SequenceSplit t2 = TestYamlJsonSerde.y.deserializeSequenceSplit(yaml);
            SequenceSplit t3 = TestYamlJsonSerde.j.deserializeSequenceSplit(json);
            Assert.assertEquals(f, t2);
            Assert.assertEquals(f, t3);
        }
    }

    @Test
    public void testDataAction() {
        DataAction[] dataActions = new DataAction[]{ new DataAction(new CategoricalToIntegerTransform("Col")), new DataAction(new ConditionFilter(new DoubleColumnCondition("Col", ConditionOp.Equal, 1))), new DataAction(new org.datavec.api.transform.sequence.ConvertToSequence("KeyCol", new NumericalColumnComparator("Col", true))), new DataAction(new ConvertFromSequence()), new DataAction(new SequenceSplitTimeSeparation("TimeCol", 1, TimeUnit.HOURS)), new DataAction(build()), new DataAction(new CalculateSortedRank("NewCol", "SortCol", new DoubleWritableComparator())) };
        for (DataAction f : dataActions) {
            String yaml = TestYamlJsonSerde.y.serialize(f);
            String json = TestYamlJsonSerde.j.serialize(f);
            // System.out.println(yaml);
            // System.out.println(json);
            // System.out.println();
            DataAction t2 = TestYamlJsonSerde.y.deserializeDataAction(yaml);
            DataAction t3 = TestYamlJsonSerde.j.deserializeDataAction(json);
            Assert.assertEquals(f, t2);
            Assert.assertEquals(f, t3);
        }
        String arrAsYaml = TestYamlJsonSerde.y.serialize(dataActions);
        String arrAsJson = TestYamlJsonSerde.j.serialize(dataActions);
        String listAsYaml = TestYamlJsonSerde.y.serializeDataActionList(Arrays.asList(dataActions));
        String listAsJson = TestYamlJsonSerde.j.serializeDataActionList(Arrays.asList(dataActions));
        // System.out.println("\n\n\n\n");
        // System.out.println(listAsYaml);
        List<DataAction> lFromYaml = TestYamlJsonSerde.y.deserializeDataActionList(listAsYaml);
        List<DataAction> lFromJson = TestYamlJsonSerde.j.deserializeDataActionList(listAsJson);
        Assert.assertEquals(Arrays.asList(dataActions), TestYamlJsonSerde.y.deserializeDataActionList(arrAsYaml));
        Assert.assertEquals(Arrays.asList(dataActions), TestYamlJsonSerde.j.deserializeDataActionList(arrAsJson));
        Assert.assertEquals(Arrays.asList(dataActions), lFromYaml);
        Assert.assertEquals(Arrays.asList(dataActions), lFromJson);
    }
}

