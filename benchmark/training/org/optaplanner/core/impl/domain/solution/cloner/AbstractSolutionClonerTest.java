/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.optaplanner.core.impl.domain.solution.cloner;


import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;
import org.optaplanner.core.impl.testdata.domain.collection.TestdataArrayBasedEntity;
import org.optaplanner.core.impl.testdata.domain.collection.TestdataArrayBasedSolution;
import org.optaplanner.core.impl.testdata.domain.collection.TestdataEntityCollectionPropertyEntity;
import org.optaplanner.core.impl.testdata.domain.collection.TestdataEntityCollectionPropertySolution;
import org.optaplanner.core.impl.testdata.domain.collection.TestdataSetBasedEntity;
import org.optaplanner.core.impl.testdata.domain.collection.TestdataSetBasedSolution;
import org.optaplanner.core.impl.testdata.domain.deepcloning.TestdataDeepCloningEntity;
import org.optaplanner.core.impl.testdata.domain.deepcloning.TestdataDeepCloningSolution;
import org.optaplanner.core.impl.testdata.domain.deepcloning.field.TestdataFieldAnnotatedDeepCloningEntity;
import org.optaplanner.core.impl.testdata.domain.deepcloning.field.TestdataFieldAnnotatedDeepCloningSolution;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataUnannotatedExtendedEntity;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataUnannotatedExtendedSolution;
import org.optaplanner.core.impl.testdata.domain.extended.thirdparty.TestdataExtendedThirdPartyEntity;
import org.optaplanner.core.impl.testdata.domain.extended.thirdparty.TestdataExtendedThirdPartySolution;
import org.optaplanner.core.impl.testdata.domain.extended.thirdparty.TestdataThirdPartyEntityPojo;
import org.optaplanner.core.impl.testdata.domain.reflect.accessmodifier.TestdataAccessModifierSolution;
import org.optaplanner.core.impl.testdata.domain.reflect.field.TestdataFieldAnnotatedEntity;
import org.optaplanner.core.impl.testdata.domain.reflect.field.TestdataFieldAnnotatedSolution;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;


public abstract class AbstractSolutionClonerTest {
    @Test
    public void cloneSolution() {
        SolutionDescriptor solutionDescriptor = TestdataSolution.buildSolutionDescriptor();
        SolutionCloner<TestdataSolution> cloner = createSolutionCloner(solutionDescriptor);
        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataEntity a = new TestdataEntity("a", val1);
        TestdataEntity b = new TestdataEntity("b", val1);
        TestdataEntity c = new TestdataEntity("c", val3);
        TestdataEntity d = new TestdataEntity("d", val3);
        TestdataSolution original = new TestdataSolution("solution");
        List<TestdataValue> valueList = Arrays.asList(val1, val2, val3);
        original.setValueList(valueList);
        List<TestdataEntity> originalEntityList = Arrays.asList(a, b, c, d);
        original.setEntityList(originalEntityList);
        TestdataSolution clone = cloner.cloneSolution(original);
        Assert.assertNotSame(original, clone);
        PlannerAssert.assertCode("solution", clone);
        Assert.assertSame(valueList, clone.getValueList());
        Assert.assertEquals(original.getScore(), clone.getScore());
        List<TestdataEntity> cloneEntityList = clone.getEntityList();
        Assert.assertNotSame(originalEntityList, cloneEntityList);
        Assert.assertEquals(4, cloneEntityList.size());
        TestdataEntity cloneA = cloneEntityList.get(0);
        TestdataEntity cloneB = cloneEntityList.get(1);
        TestdataEntity cloneC = cloneEntityList.get(2);
        TestdataEntity cloneD = cloneEntityList.get(3);
        assertEntityClone(a, cloneA, "a", "1");
        assertEntityClone(b, cloneB, "b", "1");
        assertEntityClone(c, cloneC, "c", "3");
        assertEntityClone(d, cloneD, "d", "3");
        Assert.assertNotSame(b, cloneB);
        b.setValue(val2);
        PlannerAssert.assertCode("2", b.getValue());
        // Clone remains unchanged
        PlannerAssert.assertCode("1", cloneB.getValue());
    }

    @Test
    public void cloneFieldAnnotatedSolution() {
        SolutionDescriptor solutionDescriptor = TestdataFieldAnnotatedSolution.buildSolutionDescriptor();
        SolutionCloner<TestdataFieldAnnotatedSolution> cloner = createSolutionCloner(solutionDescriptor);
        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataFieldAnnotatedEntity a = new TestdataFieldAnnotatedEntity("a", val1);
        TestdataFieldAnnotatedEntity b = new TestdataFieldAnnotatedEntity("b", val1);
        TestdataFieldAnnotatedEntity c = new TestdataFieldAnnotatedEntity("c", val3);
        TestdataFieldAnnotatedEntity d = new TestdataFieldAnnotatedEntity("d", val3);
        List<TestdataValue> valueList = Arrays.asList(val1, val2, val3);
        List<TestdataFieldAnnotatedEntity> originalEntityList = Arrays.asList(a, b, c, d);
        TestdataFieldAnnotatedSolution original = new TestdataFieldAnnotatedSolution("solution", valueList, originalEntityList);
        TestdataFieldAnnotatedSolution clone = cloner.cloneSolution(original);
        Assert.assertNotSame(original, clone);
        PlannerAssert.assertCode("solution", clone);
        Assert.assertSame(valueList, clone.getValueList());
        Assert.assertEquals(original.getScore(), clone.getScore());
        List<TestdataFieldAnnotatedEntity> cloneEntityList = clone.getEntityList();
        Assert.assertNotSame(originalEntityList, cloneEntityList);
        Assert.assertEquals(4, cloneEntityList.size());
        TestdataFieldAnnotatedEntity cloneA = cloneEntityList.get(0);
        TestdataFieldAnnotatedEntity cloneB = cloneEntityList.get(1);
        TestdataFieldAnnotatedEntity cloneC = cloneEntityList.get(2);
        TestdataFieldAnnotatedEntity cloneD = cloneEntityList.get(3);
        assertEntityClone(a, cloneA, "a", "1");
        assertEntityClone(b, cloneB, "b", "1");
        assertEntityClone(c, cloneC, "c", "3");
        assertEntityClone(d, cloneD, "d", "3");
        Assert.assertNotSame(b, cloneB);
    }

    @Test
    public void cloneAccessModifierSolution() {
        Object staticObject = new Object();
        TestdataAccessModifierSolution.setStaticField(staticObject);
        SolutionDescriptor solutionDescriptor = TestdataAccessModifierSolution.buildSolutionDescriptor();
        SolutionCloner<TestdataAccessModifierSolution> cloner = createSolutionCloner(solutionDescriptor);
        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataEntity a = new TestdataEntity("a", val1);
        TestdataEntity b = new TestdataEntity("b", val1);
        TestdataEntity c = new TestdataEntity("c", val3);
        TestdataEntity d = new TestdataEntity("d", val3);
        TestdataAccessModifierSolution original = new TestdataAccessModifierSolution("solution");
        original.setWriteOnlyField("writeHello");
        List<TestdataValue> valueList = Arrays.asList(val1, val2, val3);
        original.setValueList(valueList);
        List<TestdataEntity> originalEntityList = Arrays.asList(a, b, c, d);
        original.setEntityList(originalEntityList);
        TestdataAccessModifierSolution clone = cloner.cloneSolution(original);
        Assert.assertSame("staticFinalFieldValue", TestdataAccessModifierSolution.getStaticFinalField());
        Assert.assertSame(staticObject, TestdataAccessModifierSolution.getStaticField());
        Assert.assertNotSame(original, clone);
        PlannerAssert.assertCode("solution", clone);
        Assert.assertEquals(original.getFinalField(), clone.getFinalField());
        Assert.assertEquals("readHello", clone.getReadOnlyField());
        Assert.assertSame(valueList, clone.getValueList());
        Assert.assertEquals(original.getScore(), clone.getScore());
        List<TestdataEntity> cloneEntityList = clone.getEntityList();
        Assert.assertNotSame(originalEntityList, cloneEntityList);
        Assert.assertEquals(4, cloneEntityList.size());
        TestdataEntity cloneA = cloneEntityList.get(0);
        TestdataEntity cloneB = cloneEntityList.get(1);
        TestdataEntity cloneC = cloneEntityList.get(2);
        TestdataEntity cloneD = cloneEntityList.get(3);
        assertEntityClone(a, cloneA, "a", "1");
        assertEntityClone(b, cloneB, "b", "1");
        assertEntityClone(c, cloneC, "c", "3");
        assertEntityClone(d, cloneD, "d", "3");
        Assert.assertNotSame(b, cloneB);
        b.setValue(val2);
        PlannerAssert.assertCode("2", b.getValue());
        // Clone remains unchanged
        PlannerAssert.assertCode("1", cloneB.getValue());
    }

    @Test
    public void cloneExtendedSolution() {
        SolutionDescriptor solutionDescriptor = TestdataUnannotatedExtendedSolution.buildSolutionDescriptor();
        SolutionCloner<TestdataUnannotatedExtendedSolution> cloner = createSolutionCloner(solutionDescriptor);
        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataUnannotatedExtendedEntity a = new TestdataUnannotatedExtendedEntity("a", val1, null);
        TestdataUnannotatedExtendedEntity b = new TestdataUnannotatedExtendedEntity("b", val1, "extraObjectOnEntity");
        TestdataUnannotatedExtendedEntity c = new TestdataUnannotatedExtendedEntity("c", val3);
        TestdataUnannotatedExtendedEntity d = new TestdataUnannotatedExtendedEntity("d", val3, c);
        c.setExtraObject(d);
        TestdataUnannotatedExtendedSolution original = new TestdataUnannotatedExtendedSolution("solution", "extraObjectOnSolution");
        List<TestdataValue> valueList = Arrays.asList(val1, val2, val3);
        original.setValueList(valueList);
        List<TestdataEntity> originalEntityList = Arrays.<TestdataEntity>asList(a, b, c, d);
        original.setEntityList(originalEntityList);
        TestdataUnannotatedExtendedSolution clone = ((TestdataUnannotatedExtendedSolution) (cloner.cloneSolution(original)));
        Assert.assertNotSame(original, clone);
        PlannerAssert.assertCode("solution", clone);
        Assert.assertEquals("extraObjectOnSolution", clone.getExtraObject());
        Assert.assertSame(valueList, clone.getValueList());
        Assert.assertEquals(original.getScore(), clone.getScore());
        List<TestdataEntity> cloneEntityList = clone.getEntityList();
        Assert.assertNotSame(originalEntityList, cloneEntityList);
        Assert.assertEquals(4, cloneEntityList.size());
        TestdataUnannotatedExtendedEntity cloneA = ((TestdataUnannotatedExtendedEntity) (cloneEntityList.get(0)));
        TestdataUnannotatedExtendedEntity cloneB = ((TestdataUnannotatedExtendedEntity) (cloneEntityList.get(1)));
        TestdataUnannotatedExtendedEntity cloneC = ((TestdataUnannotatedExtendedEntity) (cloneEntityList.get(2)));
        TestdataUnannotatedExtendedEntity cloneD = ((TestdataUnannotatedExtendedEntity) (cloneEntityList.get(3)));
        assertEntityClone(a, cloneA, "a", "1");
        Assert.assertEquals(null, cloneA.getExtraObject());
        assertEntityClone(b, cloneB, "b", "1");
        Assert.assertEquals("extraObjectOnEntity", cloneB.getExtraObject());
        assertEntityClone(c, cloneC, "c", "3");
        Assert.assertEquals(cloneD, cloneC.getExtraObject());
        assertEntityClone(d, cloneD, "d", "3");
        Assert.assertEquals(cloneC, cloneD.getExtraObject());
        Assert.assertNotSame(b, cloneB);
        b.setValue(val2);
        PlannerAssert.assertCode("2", b.getValue());
        // Clone remains unchanged
        PlannerAssert.assertCode("1", cloneB.getValue());
    }

    @Test
    public void cloneExtendedThirdPartySolution() {
        SolutionDescriptor solutionDescriptor = TestdataExtendedThirdPartySolution.buildSolutionDescriptor();
        SolutionCloner<TestdataExtendedThirdPartySolution> cloner = createSolutionCloner(solutionDescriptor);
        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataExtendedThirdPartyEntity a = new TestdataExtendedThirdPartyEntity("a", val1, null);
        TestdataExtendedThirdPartyEntity b = new TestdataExtendedThirdPartyEntity("b", val1, "extraObjectOnEntity");
        TestdataExtendedThirdPartyEntity c = new TestdataExtendedThirdPartyEntity("c", val3);
        TestdataExtendedThirdPartyEntity d = new TestdataExtendedThirdPartyEntity("d", val3, c);
        c.setExtraObject(d);
        TestdataExtendedThirdPartySolution original = new TestdataExtendedThirdPartySolution("solution", "extraObjectOnSolution");
        List<TestdataValue> valueList = Arrays.asList(val1, val2, val3);
        original.setValueList(valueList);
        List<TestdataThirdPartyEntityPojo> originalEntityList = Arrays.<TestdataThirdPartyEntityPojo>asList(a, b, c, d);
        original.setEntityList(originalEntityList);
        TestdataExtendedThirdPartySolution clone = ((TestdataExtendedThirdPartySolution) (cloner.cloneSolution(original)));
        Assert.assertNotSame(original, clone);
        PlannerAssert.assertCode("solution", clone);
        Assert.assertEquals("extraObjectOnSolution", clone.getExtraObject());
        Assert.assertSame(valueList, clone.getValueList());
        Assert.assertEquals(original.getScore(), clone.getScore());
        List<TestdataThirdPartyEntityPojo> cloneEntityList = clone.getEntityList();
        Assert.assertNotSame(originalEntityList, cloneEntityList);
        Assert.assertEquals(4, cloneEntityList.size());
        TestdataExtendedThirdPartyEntity cloneA = ((TestdataExtendedThirdPartyEntity) (cloneEntityList.get(0)));
        TestdataExtendedThirdPartyEntity cloneB = ((TestdataExtendedThirdPartyEntity) (cloneEntityList.get(1)));
        TestdataExtendedThirdPartyEntity cloneC = ((TestdataExtendedThirdPartyEntity) (cloneEntityList.get(2)));
        TestdataExtendedThirdPartyEntity cloneD = ((TestdataExtendedThirdPartyEntity) (cloneEntityList.get(3)));
        assertEntityClone(a, cloneA, "a", "1");
        Assert.assertEquals(null, cloneA.getExtraObject());
        assertEntityClone(b, cloneB, "b", "1");
        Assert.assertEquals("extraObjectOnEntity", cloneB.getExtraObject());
        assertEntityClone(c, cloneC, "c", "3");
        Assert.assertEquals(cloneD, cloneC.getExtraObject());
        assertEntityClone(d, cloneD, "d", "3");
        Assert.assertEquals(cloneC, cloneD.getExtraObject());
        Assert.assertNotSame(b, cloneB);
        b.setValue(val2);
        PlannerAssert.assertCode("2", b.getValue());
        // Clone remains unchanged
        PlannerAssert.assertCode("1", cloneB.getValue());
    }

    @Test
    public void cloneChainedSolution() {
        SolutionDescriptor solutionDescriptor = TestdataChainedSolution.buildSolutionDescriptor();
        SolutionCloner<TestdataChainedSolution> cloner = createSolutionCloner(solutionDescriptor);
        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);
        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);
        TestdataChainedSolution original = new TestdataChainedSolution("solution");
        List<TestdataChainedAnchor> anchorList = Arrays.asList(a0, b0);
        original.setChainedAnchorList(anchorList);
        List<TestdataChainedEntity> originalEntityList = Arrays.asList(a1, a2, a3, b1);
        original.setChainedEntityList(originalEntityList);
        TestdataChainedSolution clone = cloner.cloneSolution(original);
        Assert.assertNotSame(original, clone);
        PlannerAssert.assertCode("solution", clone);
        Assert.assertSame(anchorList, clone.getChainedAnchorList());
        Assert.assertEquals(original.getScore(), clone.getScore());
        List<TestdataChainedEntity> cloneEntityList = clone.getChainedEntityList();
        Assert.assertNotSame(originalEntityList, cloneEntityList);
        Assert.assertEquals(4, cloneEntityList.size());
        TestdataChainedEntity cloneA1 = cloneEntityList.get(0);
        TestdataChainedEntity cloneA2 = cloneEntityList.get(1);
        TestdataChainedEntity cloneA3 = cloneEntityList.get(2);
        TestdataChainedEntity cloneB1 = cloneEntityList.get(3);
        assertChainedEntityClone(a1, cloneA1, "a1", a0);
        assertChainedEntityClone(a2, cloneA2, "a2", cloneA1);
        assertChainedEntityClone(a3, cloneA3, "a3", cloneA2);
        assertChainedEntityClone(b1, cloneB1, "b1", b0);
        a3.setChainedObject(b1);
        PlannerAssert.assertCode("b1", a3.getChainedObject());
        // Clone remains unchanged
        PlannerAssert.assertCode("a2", cloneA3.getChainedObject());
    }

    @Test
    public void cloneSetBasedSolution() {
        SolutionDescriptor solutionDescriptor = TestdataSetBasedSolution.buildSolutionDescriptor();
        SolutionCloner<TestdataSetBasedSolution> cloner = createSolutionCloner(solutionDescriptor);
        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataSetBasedEntity a = new TestdataSetBasedEntity("a", val1);
        TestdataSetBasedEntity b = new TestdataSetBasedEntity("b", val1);
        TestdataSetBasedEntity c = new TestdataSetBasedEntity("c", val3);
        TestdataSetBasedEntity d = new TestdataSetBasedEntity("d", val3);
        TestdataSetBasedSolution original = new TestdataSetBasedSolution("solution");
        Set<TestdataValue> valueSet = new TreeSet<>(( a1, b1) -> {
            return b1.getCode().compareTo(a1.getCode());// Reverse alphabetic

        });
        valueSet.addAll(Arrays.asList(val1, val2, val3));
        original.setValueSet(valueSet);
        Comparator<TestdataSetBasedEntity> entityComparator = ( a1, b1) -> {
            return b1.getCode().compareTo(a1.getCode());// Reverse alphabetic

        };
        Set<TestdataSetBasedEntity> originalEntitySet = new TreeSet<>(entityComparator);
        originalEntitySet.addAll(Arrays.asList(a, b, c, d));
        original.setEntitySet(originalEntitySet);
        TestdataSetBasedSolution clone = cloner.cloneSolution(original);
        Assert.assertNotSame(original, clone);
        Assert.assertSame(valueSet, clone.getValueSet());
        Assert.assertEquals(original.getScore(), clone.getScore());
        Set<TestdataSetBasedEntity> cloneEntitySet = clone.getEntitySet();
        Assert.assertNotSame(originalEntitySet, cloneEntitySet);
        Assert.assertTrue((cloneEntitySet instanceof SortedSet));
        Assert.assertSame(entityComparator, ((SortedSet) (cloneEntitySet)).comparator());
        PlannerAssert.assertCode("solution", clone);
        Assert.assertEquals(4, cloneEntitySet.size());
        Iterator<TestdataSetBasedEntity> it = cloneEntitySet.iterator();
        // Reverse order because they got sorted
        TestdataSetBasedEntity cloneD = it.next();
        TestdataSetBasedEntity cloneC = it.next();
        TestdataSetBasedEntity cloneB = it.next();
        TestdataSetBasedEntity cloneA = it.next();
        assertSetBasedEntityClone(a, cloneA, "a", "1");
        assertSetBasedEntityClone(b, cloneB, "b", "1");
        assertSetBasedEntityClone(c, cloneC, "c", "3");
        assertSetBasedEntityClone(d, cloneD, "d", "3");
        b.setValue(val2);
        PlannerAssert.assertCode("2", b.getValue());
        // Clone remains unchanged
        PlannerAssert.assertCode("1", cloneB.getValue());
    }

    @Test
    public void cloneEntityCollectionPropertySolution() {
        SolutionDescriptor solutionDescriptor = TestdataEntityCollectionPropertySolution.buildSolutionDescriptor();
        SolutionCloner<TestdataEntityCollectionPropertySolution> cloner = createSolutionCloner(solutionDescriptor);
        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataEntityCollectionPropertyEntity a = new TestdataEntityCollectionPropertyEntity("a", val1);
        TestdataEntityCollectionPropertyEntity b = new TestdataEntityCollectionPropertyEntity("b", val1);
        TestdataEntityCollectionPropertyEntity c = new TestdataEntityCollectionPropertyEntity("c", val3);
        a.setEntityList(Arrays.asList(b, c));
        a.setEntitySet(new HashSet<>(Arrays.asList(b, c)));
        a.setStringToEntityMap(new HashMap<>());
        a.getStringToEntityMap().put("b", b);
        a.getStringToEntityMap().put("c", c);
        a.setEntityToStringMap(new HashMap<>());
        a.getEntityToStringMap().put(b, "b");
        a.getEntityToStringMap().put(c, "c");
        a.setStringToEntityListMap(new HashMap<>());
        a.getStringToEntityListMap().put("bc", Arrays.asList(b, c));
        b.setEntityList(Collections.emptyList());
        b.setEntitySet(new HashSet<>());
        b.setStringToEntityMap(new HashMap<>());
        b.setEntityToStringMap(null);
        b.setStringToEntityListMap(null);
        c.setEntityList(Arrays.asList(a, c));
        c.setEntitySet(new HashSet<>(Arrays.asList(a, c)));
        c.setStringToEntityMap(new HashMap<>());
        c.getStringToEntityMap().put("a", a);
        c.getStringToEntityMap().put("c", c);
        c.setEntityToStringMap(null);
        c.setStringToEntityListMap(null);
        TestdataEntityCollectionPropertySolution original = new TestdataEntityCollectionPropertySolution("solution");
        List<TestdataValue> valueList = Arrays.asList(val1, val2, val3);
        original.setValueList(valueList);
        List<TestdataEntityCollectionPropertyEntity> originalEntityList = Arrays.asList(a, b, c);
        original.setEntityList(originalEntityList);
        TestdataEntityCollectionPropertySolution clone = cloner.cloneSolution(original);
        Assert.assertNotSame(original, clone);
        PlannerAssert.assertCode("solution", clone);
        Assert.assertSame(valueList, clone.getValueList());
        Assert.assertEquals(original.getScore(), clone.getScore());
        List<TestdataEntityCollectionPropertyEntity> cloneEntityList = clone.getEntityList();
        Assert.assertNotSame(originalEntityList, cloneEntityList);
        Assert.assertEquals(3, cloneEntityList.size());
        TestdataEntityCollectionPropertyEntity cloneA = cloneEntityList.get(0);
        TestdataEntityCollectionPropertyEntity cloneB = cloneEntityList.get(1);
        TestdataEntityCollectionPropertyEntity cloneC = cloneEntityList.get(2);
        assertEntityCollectionPropertyEntityClone(a, cloneA, "a", "1");
        Assert.assertNotSame(a.getEntityList(), cloneA.getEntityList());
        Assert.assertEquals(2, cloneA.getEntityList().size());
        Assert.assertSame(cloneB, cloneA.getEntityList().get(0));
        Assert.assertSame(cloneC, cloneA.getEntityList().get(1));
        Assert.assertNotSame(a.getEntitySet(), cloneA.getEntitySet());
        Assert.assertEquals(2, cloneA.getEntitySet().size());
        Assert.assertNotSame(a.getStringToEntityMap(), cloneA.getStringToEntityMap());
        Assert.assertEquals(2, cloneA.getStringToEntityMap().size());
        Assert.assertSame(cloneB, cloneA.getStringToEntityMap().get("b"));
        Assert.assertSame(cloneC, cloneA.getStringToEntityMap().get("c"));
        Assert.assertNotSame(a.getEntityToStringMap(), cloneA.getEntityToStringMap());
        Assert.assertEquals(2, cloneA.getEntityToStringMap().size());
        Assert.assertEquals("b", cloneA.getEntityToStringMap().get(cloneB));
        Assert.assertEquals("c", cloneA.getEntityToStringMap().get(cloneC));
        Assert.assertNotSame(a.getStringToEntityListMap(), cloneA.getStringToEntityListMap());
        Assert.assertEquals(1, cloneA.getStringToEntityListMap().size());
        List<TestdataEntityCollectionPropertyEntity> entityListOfMap = cloneA.getStringToEntityListMap().get("bc");
        Assert.assertEquals(2, entityListOfMap.size());
        Assert.assertSame(cloneB, entityListOfMap.get(0));
        Assert.assertSame(cloneC, entityListOfMap.get(1));
        assertEntityCollectionPropertyEntityClone(b, cloneB, "b", "1");
        Assert.assertEquals(0, cloneB.getEntityList().size());
        Assert.assertEquals(0, cloneB.getEntitySet().size());
        Assert.assertEquals(0, cloneB.getStringToEntityMap().size());
        Assert.assertNull(cloneB.getEntityToStringMap());
        Assert.assertNull(cloneB.getStringToEntityListMap());
        assertEntityCollectionPropertyEntityClone(c, cloneC, "c", "3");
        Assert.assertEquals(2, cloneC.getEntityList().size());
        Assert.assertSame(cloneA, cloneC.getEntityList().get(0));
        Assert.assertSame(cloneC, cloneC.getEntityList().get(1));
        Assert.assertEquals(2, cloneC.getEntitySet().size());
        Assert.assertEquals(2, cloneC.getStringToEntityMap().size());
        Assert.assertSame(cloneA, cloneC.getStringToEntityMap().get("a"));
        Assert.assertSame(cloneC, cloneC.getStringToEntityMap().get("c"));
        Assert.assertNull(cloneC.getEntityToStringMap());
        Assert.assertNull(cloneC.getStringToEntityListMap());
    }

    @Test
    public void cloneEntityArrayPropertySolution() {
        SolutionDescriptor solutionDescriptor = TestdataArrayBasedSolution.buildSolutionDescriptor();
        SolutionCloner<TestdataArrayBasedSolution> cloner = createSolutionCloner(solutionDescriptor);
        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataArrayBasedEntity a = new TestdataArrayBasedEntity("a", val1);
        TestdataArrayBasedEntity b = new TestdataArrayBasedEntity("b", val1);
        TestdataArrayBasedEntity c = new TestdataArrayBasedEntity("c", val3);
        a.setEntities(new TestdataArrayBasedEntity[]{ b, c });
        b.setEntities(new TestdataArrayBasedEntity[]{  });
        c.setEntities(new TestdataArrayBasedEntity[]{ a, c });
        TestdataArrayBasedSolution original = new TestdataArrayBasedSolution("solution");
        TestdataValue[] values = new TestdataValue[]{ val1, val2, val3 };
        original.setValues(values);
        TestdataArrayBasedEntity[] originalEntities = new TestdataArrayBasedEntity[]{ a, b, c };
        original.setEntities(originalEntities);
        TestdataArrayBasedSolution clone = cloner.cloneSolution(original);
        Assert.assertNotSame(original, clone);
        PlannerAssert.assertCode("solution", clone);
        Assert.assertSame(values, clone.getValues());
        Assert.assertEquals(original.getScore(), clone.getScore());
        TestdataArrayBasedEntity[] cloneEntities = clone.getEntities();
        Assert.assertNotSame(originalEntities, cloneEntities);
        Assert.assertEquals(3, cloneEntities.length);
        TestdataArrayBasedEntity cloneA = cloneEntities[0];
        TestdataArrayBasedEntity cloneB = cloneEntities[1];
        TestdataArrayBasedEntity cloneC = cloneEntities[2];
        assertEntityArrayPropertyEntityClone(a, cloneA, "a", "1");
        Assert.assertNotSame(a.getEntities(), cloneA.getEntities());
        Assert.assertEquals(2, cloneA.getEntities().length);
        Assert.assertSame(cloneB, cloneA.getEntities()[0]);
        Assert.assertSame(cloneC, cloneA.getEntities()[1]);
        assertEntityArrayPropertyEntityClone(b, cloneB, "b", "1");
        Assert.assertEquals(0, cloneB.getEntities().length);
        assertEntityArrayPropertyEntityClone(c, cloneC, "c", "3");
        Assert.assertEquals(2, cloneC.getEntities().length);
        Assert.assertSame(cloneA, cloneC.getEntities()[0]);
        Assert.assertSame(cloneC, cloneC.getEntities()[1]);
    }

    @Test
    public void deepPlanningClone() {
        SolutionDescriptor solutionDescriptor = TestdataDeepCloningSolution.buildSolutionDescriptor();
        SolutionCloner<TestdataDeepCloningSolution> cloner = createSolutionCloner(solutionDescriptor);
        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataDeepCloningEntity a = new TestdataDeepCloningEntity("a", val1);
        List<String> aShadowVariableList = Arrays.asList("shadow a1", "shadow a2");
        a.setShadowVariableList(aShadowVariableList);
        TestdataDeepCloningEntity b = new TestdataDeepCloningEntity("b", val1);
        Map<String, String> bShadowVariableMap = new HashMap<>();
        bShadowVariableMap.put("shadow key b1", "shadow value b1");
        bShadowVariableMap.put("shadow key b2", "shadow value b2");
        b.setShadowVariableMap(bShadowVariableMap);
        TestdataDeepCloningEntity c = new TestdataDeepCloningEntity("c", val3);
        List<String> cShadowVariableList = Arrays.asList("shadow c1", "shadow c2");
        c.setShadowVariableList(cShadowVariableList);
        TestdataDeepCloningEntity d = new TestdataDeepCloningEntity("d", val3);
        TestdataDeepCloningSolution original = new TestdataDeepCloningSolution("solution");
        List<TestdataValue> valueList = Arrays.asList(val1, val2, val3);
        original.setValueList(valueList);
        List<TestdataDeepCloningEntity> originalEntityList = Arrays.asList(a, b, c, d);
        original.setEntityList(originalEntityList);
        List<String> generalShadowVariableList = Arrays.asList("shadow g1", "shadow g2");
        original.setGeneralShadowVariableList(generalShadowVariableList);
        TestdataDeepCloningSolution clone = cloner.cloneSolution(original);
        Assert.assertNotSame(original, clone);
        PlannerAssert.assertCode("solution", clone);
        Assert.assertSame(valueList, clone.getValueList());
        Assert.assertEquals(original.getScore(), clone.getScore());
        List<TestdataDeepCloningEntity> cloneEntityList = clone.getEntityList();
        Assert.assertNotSame(originalEntityList, cloneEntityList);
        Assert.assertEquals(4, cloneEntityList.size());
        TestdataDeepCloningEntity cloneA = cloneEntityList.get(0);
        assertDeepCloningEntityClone(a, cloneA, "a");
        TestdataDeepCloningEntity cloneB = cloneEntityList.get(1);
        assertDeepCloningEntityClone(b, cloneB, "b");
        TestdataDeepCloningEntity cloneC = cloneEntityList.get(2);
        assertDeepCloningEntityClone(c, cloneC, "c");
        TestdataDeepCloningEntity cloneD = cloneEntityList.get(3);
        assertDeepCloningEntityClone(d, cloneD, "d");
        List<String> cloneGeneralShadowVariableList = clone.getGeneralShadowVariableList();
        Assert.assertNotSame(generalShadowVariableList, cloneGeneralShadowVariableList);
        Assert.assertEquals(2, cloneGeneralShadowVariableList.size());
        Assert.assertSame(generalShadowVariableList.get(0), cloneGeneralShadowVariableList.get(0));
        Assert.assertEquals(generalShadowVariableList.get(1), cloneGeneralShadowVariableList.get(1));
        b.setValue(val2);
        PlannerAssert.assertCode("2", b.getValue());
        // Clone remains unchanged
        PlannerAssert.assertCode("1", cloneB.getValue());
        b.getShadowVariableMap().put("shadow key b1", "other shadow value b1");
        Assert.assertEquals("other shadow value b1", b.getShadowVariableMap().get("shadow key b1"));
        // Clone remains unchanged
        Assert.assertEquals("shadow value b1", cloneB.getShadowVariableMap().get("shadow key b1"));
    }

    @Test
    public void fieldAnnotatedDeepPlanningClone() {
        SolutionDescriptor solutionDescriptor = TestdataFieldAnnotatedDeepCloningSolution.buildSolutionDescriptor();
        SolutionCloner<TestdataFieldAnnotatedDeepCloningSolution> cloner = createSolutionCloner(solutionDescriptor);
        TestdataValue val1 = new TestdataValue("1");
        TestdataValue val2 = new TestdataValue("2");
        TestdataValue val3 = new TestdataValue("3");
        TestdataFieldAnnotatedDeepCloningEntity a = new TestdataFieldAnnotatedDeepCloningEntity("a", val1);
        List<String> aShadowVariableList = Arrays.asList("shadow a1", "shadow a2");
        a.setShadowVariableList(aShadowVariableList);
        TestdataFieldAnnotatedDeepCloningEntity b = new TestdataFieldAnnotatedDeepCloningEntity("b", val1);
        Map<String, String> bShadowVariableMap = new HashMap<>();
        bShadowVariableMap.put("shadow key b1", "shadow value b1");
        bShadowVariableMap.put("shadow key b2", "shadow value b2");
        b.setShadowVariableMap(bShadowVariableMap);
        TestdataFieldAnnotatedDeepCloningEntity c = new TestdataFieldAnnotatedDeepCloningEntity("c", val3);
        List<String> cShadowVariableList = Arrays.asList("shadow c1", "shadow c2");
        c.setShadowVariableList(cShadowVariableList);
        TestdataFieldAnnotatedDeepCloningEntity d = new TestdataFieldAnnotatedDeepCloningEntity("d", val3);
        TestdataFieldAnnotatedDeepCloningSolution original = new TestdataFieldAnnotatedDeepCloningSolution("solution");
        List<TestdataValue> valueList = Arrays.asList(val1, val2, val3);
        original.setValueList(valueList);
        List<TestdataFieldAnnotatedDeepCloningEntity> originalEntityList = Arrays.asList(a, b, c, d);
        original.setEntityList(originalEntityList);
        List<String> generalShadowVariableList = Arrays.asList("shadow g1", "shadow g2");
        original.setGeneralShadowVariableList(generalShadowVariableList);
        TestdataFieldAnnotatedDeepCloningSolution clone = cloner.cloneSolution(original);
        Assert.assertNotSame(original, clone);
        PlannerAssert.assertCode("solution", clone);
        Assert.assertSame(valueList, clone.getValueList());
        Assert.assertEquals(original.getScore(), clone.getScore());
        List<TestdataFieldAnnotatedDeepCloningEntity> cloneEntityList = clone.getEntityList();
        Assert.assertNotSame(originalEntityList, cloneEntityList);
        Assert.assertEquals(4, cloneEntityList.size());
        TestdataFieldAnnotatedDeepCloningEntity cloneA = cloneEntityList.get(0);
        assertDeepCloningEntityClone(a, cloneA, "a");
        TestdataFieldAnnotatedDeepCloningEntity cloneB = cloneEntityList.get(1);
        assertDeepCloningEntityClone(b, cloneB, "b");
        TestdataFieldAnnotatedDeepCloningEntity cloneC = cloneEntityList.get(2);
        assertDeepCloningEntityClone(c, cloneC, "c");
        TestdataFieldAnnotatedDeepCloningEntity cloneD = cloneEntityList.get(3);
        assertDeepCloningEntityClone(d, cloneD, "d");
        List<String> cloneGeneralShadowVariableList = clone.getGeneralShadowVariableList();
        Assert.assertNotSame(generalShadowVariableList, cloneGeneralShadowVariableList);
        Assert.assertEquals(2, cloneGeneralShadowVariableList.size());
        Assert.assertSame(generalShadowVariableList.get(0), cloneGeneralShadowVariableList.get(0));
        Assert.assertEquals(generalShadowVariableList.get(1), cloneGeneralShadowVariableList.get(1));
        b.setValue(val2);
        PlannerAssert.assertCode("2", b.getValue());
        // Clone remains unchanged
        PlannerAssert.assertCode("1", cloneB.getValue());
        b.getShadowVariableMap().put("shadow key b1", "other shadow value b1");
        Assert.assertEquals("other shadow value b1", b.getShadowVariableMap().get("shadow key b1"));
        // Clone remains unchanged
        Assert.assertEquals("shadow value b1", cloneB.getShadowVariableMap().get("shadow key b1"));
    }
}

