/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.ruleunit;


import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.rule.EntryPointId;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class RuleUnitDescriptionTest {
    private RuleUnitDescription ruleUnitDescr;

    @Test
    public void getRuleUnitClass() {
        assertThat(ruleUnitDescr.getRuleUnitClass()).isEqualTo(TestRuleUnit.class);
    }

    @Test
    public void getRuleUnitName() {
        assertThat(ruleUnitDescr.getRuleUnitName()).isEqualTo(TestRuleUnit.class.getName());
    }

    @Test
    public void getEntryPointId() {
        final Optional<EntryPointId> entryPointId = ruleUnitDescr.getEntryPointId("nonexisting");
        assertThat(entryPointId).isNotPresent();
        assertEntryPointIdExists("numbersArray");
        assertEntryPointIdExists("number");
        assertEntryPointIdExists("stringList");
        assertEntryPointIdExists("simpleFactList");
    }

    @Test
    public void getDatasourceType() {
        final Optional<Class<?>> dataSourceType = ruleUnitDescr.getDatasourceType("nonexisting");
        assertThat(dataSourceType).isNotPresent();
        assertDataSourceType("number", BigDecimal.class);
        assertDataSourceType("numbersArray", Integer.class);
        assertDataSourceType("stringList", String.class);
        assertDataSourceType("simpleFactList", SimpleFact.class);
    }

    @Test
    public void getVarType() {
        final Optional<Class<?>> varType = ruleUnitDescr.getVarType("nonexisting");
        assertThat(varType).isNotPresent();
        assertVarType("number", BigDecimal.class);
        assertVarType("numbersArray", Integer[].class);
        assertVarType("stringList", List.class);
        assertVarType("simpleFactList", List.class);
    }

    @Test
    public void hasVar() {
        assertThat(ruleUnitDescr.hasVar("nonexisting")).isFalse();
        assertThat(ruleUnitDescr.hasVar("numbers")).isFalse();
        assertThat(ruleUnitDescr.hasVar("number")).isTrue();
        assertThat(ruleUnitDescr.hasVar("numbersArray")).isTrue();
        assertThat(ruleUnitDescr.hasVar("stringList")).isTrue();
        assertThat(ruleUnitDescr.hasVar("simpleFactList")).isTrue();
    }

    @Test
    public void getUnitVars() {
        final Collection<String> unitVars = ruleUnitDescr.getUnitVars();
        assertThat(unitVars).isNotEmpty();
        assertThat(unitVars).hasSize(5);
        assertThat(unitVars).containsExactlyInAnyOrder("bound", "number", "numbersArray", "stringList", "simpleFactList");
    }

    @Test
    public void getUnitVarAccessors() {
        final Map<String, Method> unitVarAccessors = ruleUnitDescr.getUnitVarAccessors();
        assertThat(unitVarAccessors).isNotEmpty();
        assertThat(unitVarAccessors).hasSize(5);
        assertThat(unitVarAccessors).containsKeys("bound", "number", "numbersArray", "stringList", "simpleFactList");
        assertThat(unitVarAccessors.values()).extracting("name", String.class).containsExactlyInAnyOrder("getBound", "getNumber", "getNumbersArray", "getStringList", "getSimpleFactList");
    }

    @Test
    public void hasDataSource() {
        assertThat(ruleUnitDescr.hasDataSource("nonexisting")).isFalse();
        assertThat(ruleUnitDescr.hasDataSource("numbers")).isFalse();
        assertThat(ruleUnitDescr.hasDataSource("number")).isTrue();
        assertThat(ruleUnitDescr.hasDataSource("numbersArray")).isTrue();
        assertThat(ruleUnitDescr.hasDataSource("stringList")).isTrue();
        assertThat(ruleUnitDescr.hasDataSource("simpleFactList")).isTrue();
    }

    @Test
    public void bindDataSourcesNonexistingEntryPoints() {
        final StatefulKnowledgeSessionImpl sessionImpl = Mockito.mock(StatefulKnowledgeSessionImpl.class);
        Mockito.when(sessionImpl.getEntryPoint("number")).thenReturn(null);
        Mockito.when(sessionImpl.getEntryPoint("numbersArray")).thenReturn(null);
        Mockito.when(sessionImpl.getEntryPoint("stringList")).thenReturn(null);
        Mockito.when(sessionImpl.getEntryPoint("simpleFactList")).thenReturn(null);
        final TestRuleUnit testRuleUnit = new TestRuleUnit(new Integer[]{ 1, 2, 5 }, BigDecimal.TEN);
        final SimpleFact simpleFact = new SimpleFact("testValue");
        testRuleUnit.addSimpleFact(simpleFact);
        ruleUnitDescr.bindDataSources(sessionImpl, testRuleUnit);
        Mockito.verify(sessionImpl, Mockito.never()).insert(ArgumentMatchers.anyObject());
        Mockito.verify(sessionImpl, Mockito.never()).insert(ArgumentMatchers.anyObject(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject());
    }

    @Test
    public void bindDataSources() {
        final WorkingMemoryEntryPoint numberEntryPoint = Mockito.mock(WorkingMemoryEntryPoint.class);
        final WorkingMemoryEntryPoint numbersArrayEntryPoint = Mockito.mock(WorkingMemoryEntryPoint.class);
        final WorkingMemoryEntryPoint stringListEntryPoint = Mockito.mock(WorkingMemoryEntryPoint.class);
        final WorkingMemoryEntryPoint simpleFactListEntryPoint = Mockito.mock(WorkingMemoryEntryPoint.class);
        final StatefulKnowledgeSessionImpl sessionImpl = Mockito.mock(StatefulKnowledgeSessionImpl.class);
        Mockito.when(sessionImpl.getEntryPoint(((TestRuleUnit.class.getCanonicalName()) + ".number"))).thenReturn(numberEntryPoint);
        Mockito.when(sessionImpl.getEntryPoint(((TestRuleUnit.class.getCanonicalName()) + ".numbersArray"))).thenReturn(numbersArrayEntryPoint);
        Mockito.when(sessionImpl.getEntryPoint(((TestRuleUnit.class.getCanonicalName()) + ".stringList"))).thenReturn(stringListEntryPoint);
        Mockito.when(sessionImpl.getEntryPoint(((TestRuleUnit.class.getCanonicalName()) + ".simpleFactList"))).thenReturn(simpleFactListEntryPoint);
        final TestRuleUnit testRuleUnit = new TestRuleUnit(new Integer[]{ 1, 2, 5 }, BigDecimal.TEN);
        final SimpleFact simpleFact = new SimpleFact("testValue");
        testRuleUnit.addSimpleFact(simpleFact);
        ruleUnitDescr.bindDataSources(sessionImpl, testRuleUnit);
        Mockito.verify(numberEntryPoint).insert(BigDecimal.TEN);
        Mockito.verifyNoMoreInteractions(numberEntryPoint);
        Mockito.verify(numbersArrayEntryPoint).insert(1);
        Mockito.verify(numbersArrayEntryPoint).insert(2);
        Mockito.verify(numbersArrayEntryPoint).insert(5);
        Mockito.verifyNoMoreInteractions(numbersArrayEntryPoint);
        Mockito.verifyZeroInteractions(stringListEntryPoint);
        Mockito.verify(simpleFactListEntryPoint).insert(simpleFact);
        Mockito.verifyNoMoreInteractions(simpleFactListEntryPoint);
    }

    @Test
    public void unbindDataSources() {
        final WorkingMemoryEntryPoint boundPropEntryPoint = Mockito.mock(WorkingMemoryEntryPoint.class);
        final StatefulKnowledgeSessionImpl sessionImpl = Mockito.mock(StatefulKnowledgeSessionImpl.class);
        // This is a little hack, see description in TestRuleUnit class.
        Mockito.when(sessionImpl.getEntryPoint(((TestRuleUnit.class.getCanonicalName()) + ".bound"))).thenReturn(boundPropEntryPoint);
        final TestRuleUnit testRuleUnit = new TestRuleUnit(new Integer[]{ 1, 2, 5 }, BigDecimal.TEN);
        final SimpleFact simpleFact = new SimpleFact("testValue");
        testRuleUnit.addSimpleFact(simpleFact);
        assertThat(testRuleUnit.bound).isFalse();
        // Bind calls getBound() which switches the bound flag to true
        ruleUnitDescr.bindDataSources(sessionImpl, testRuleUnit);
        Mockito.verify(boundPropEntryPoint).insert(true);
        // Unbind calls getBound() which switches the bound flag to false
        ruleUnitDescr.unbindDataSources(sessionImpl, testRuleUnit);
        // We have to observe directly. By calling getBound, it will get switched again.
        assertThat(testRuleUnit.bound).isFalse();
    }

    @Test
    public void getValue() {
        final TestRuleUnit testRuleUnit = new TestRuleUnit(new Integer[]{ 1, 2, 5 }, BigDecimal.TEN);
        final SimpleFact simpleFact = new SimpleFact("testValue");
        testRuleUnit.addSimpleFact(simpleFact);
        Object value = ruleUnitDescr.getValue(testRuleUnit, "nonexisting");
        assertThat(value).isNull();
        value = ruleUnitDescr.getValue(testRuleUnit, "number");
        assertThat(value).isInstanceOf(BigDecimal.class);
        assertThat(value).isEqualTo(BigDecimal.TEN);
        value = ruleUnitDescr.getValue(testRuleUnit, "numbersArray");
        assertThat(value).isInstanceOf(Integer[].class);
        assertThat(value).isEqualTo(new Integer[]{ 1, 2, 5 });
        value = ruleUnitDescr.getValue(testRuleUnit, "simpleFactList");
        assertThat(value).isInstanceOfSatisfying(List.class, ( list) -> assertThat(list).containsExactly(simpleFact));
    }
}

