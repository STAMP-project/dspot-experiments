/**
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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
package org.drools.core.base;


import ValueType.OBJECT_TYPE;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.drools.core.base.evaluators.AfterEvaluatorDefinition;
import org.drools.core.base.evaluators.BeforeEvaluatorDefinition;
import org.drools.core.base.evaluators.CoincidesEvaluatorDefinition;
import org.drools.core.base.evaluators.DuringEvaluatorDefinition;
import org.drools.core.base.evaluators.EvaluatorRegistry;
import org.drools.core.common.DisconnectedWorkingMemoryEntryPoint;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.spi.InternalReadAccessor;
import org.junit.Test;


/**
 * Test coverage for the temporal evaluators.
 */
public class TemporalEvaluatorFactoryTest {
    private EvaluatorRegistry registry = new EvaluatorRegistry();

    @Test
    public void testAfter() {
        registry.addEvaluatorDefinition(AfterEvaluatorDefinition.class.getName());
        EventFactHandle foo = new EventFactHandle(1, "foo", 1, 1, 2, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle bar = new EventFactHandle(2, "bar", 1, 4, 3, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle drool = new EventFactHandle(1, "drool", 1, 5, 2, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        final Object[][] data = new Object[][]{ new Object[]{ drool, "after", foo, Boolean.TRUE }, new Object[]{ drool, "after", bar, Boolean.FALSE }, new Object[]{ bar, "after", foo, Boolean.TRUE }, new Object[]{ bar, "after", drool, Boolean.FALSE }, new Object[]{ foo, "after", drool, Boolean.FALSE }, new Object[]{ foo, "after", bar, Boolean.FALSE }, new Object[]{ foo, "not after", bar, Boolean.TRUE }, new Object[]{ foo, "not after", drool, Boolean.TRUE }, new Object[]{ bar, "not after", drool, Boolean.TRUE }, new Object[]{ bar, "not after", foo, Boolean.FALSE }, new Object[]{ drool, "not after", foo, Boolean.FALSE }, new Object[]{ drool, "not after", bar, Boolean.TRUE }, new Object[]{ bar, "after[1]", foo, Boolean.TRUE }, new Object[]{ bar, "after[0]", foo, Boolean.TRUE }, new Object[]{ bar, "after[-3]", drool, Boolean.TRUE }, new Object[]{ bar, "after[-4]", drool, Boolean.TRUE }, new Object[]{ drool, "after[2]", foo, Boolean.TRUE }, new Object[]{ drool, "after[1]", foo, Boolean.TRUE }, new Object[]{ drool, "after[-2]", bar, Boolean.TRUE }, new Object[]{ drool, "after[-3]", bar, Boolean.TRUE }, new Object[]{ foo, "after[-6]", drool, Boolean.TRUE }, new Object[]{ foo, "after[-7]", drool, Boolean.TRUE }, new Object[]{ foo, "after[-6]", bar, Boolean.TRUE }, new Object[]{ foo, "after[-7]", bar, Boolean.TRUE }, new Object[]{ bar, "not after[1]", foo, Boolean.FALSE }, new Object[]{ bar, "not after[0]", foo, Boolean.FALSE }, new Object[]{ bar, "not after[-3]", drool, Boolean.FALSE }, new Object[]{ bar, "not after[-4]", drool, Boolean.FALSE }, new Object[]{ drool, "not after[2]", foo, Boolean.FALSE }, new Object[]{ drool, "not after[1]", foo, Boolean.FALSE }, new Object[]{ drool, "not after[-2]", bar, Boolean.FALSE }, new Object[]{ drool, "not after[-3]", bar, Boolean.FALSE }, new Object[]{ foo, "not after[-6]", drool, Boolean.FALSE }, new Object[]{ foo, "not after[-7]", drool, Boolean.FALSE }, new Object[]{ foo, "not after[-6]", bar, Boolean.FALSE }, new Object[]{ foo, "not after[-7]", bar, Boolean.FALSE }, new Object[]{ drool, "after[1,4]", foo, Boolean.TRUE }, new Object[]{ drool, "after[3,6]", foo, Boolean.FALSE }, new Object[]{ drool, "after[-3,1]", bar, Boolean.TRUE }, new Object[]{ drool, "after[-1,3]", bar, Boolean.FALSE }, new Object[]{ bar, "after[1,5]", foo, Boolean.TRUE }, new Object[]{ bar, "after[2,5]", foo, Boolean.FALSE }, new Object[]{ bar, "after[-3,0]", drool, Boolean.TRUE }, new Object[]{ bar, "after[-2,1]", drool, Boolean.FALSE }, new Object[]{ foo, "after[-7,-3]", bar, Boolean.TRUE }, new Object[]{ foo, "after[-5,-1]", bar, Boolean.FALSE }, new Object[]{ foo, "after[-6,-5]", drool, Boolean.TRUE }, new Object[]{ foo, "after[-5,-4]", drool, Boolean.FALSE }, new Object[]{ drool, "not after[1,4]", foo, Boolean.FALSE }, new Object[]{ drool, "not after[3,6]", foo, Boolean.TRUE }, new Object[]{ drool, "not after[-3,1]", bar, Boolean.FALSE }, new Object[]{ drool, "not after[-1,3]", bar, Boolean.TRUE }, new Object[]{ bar, "not after[1,5]", foo, Boolean.FALSE }, new Object[]{ bar, "not after[2,5]", foo, Boolean.TRUE }, new Object[]{ bar, "not after[-3,0]", drool, Boolean.FALSE }, new Object[]{ bar, "not after[-2,1]", drool, Boolean.TRUE }, new Object[]{ foo, "not after[-7,-3]", bar, Boolean.FALSE }, new Object[]{ foo, "not after[-5,-1]", bar, Boolean.TRUE }, new Object[]{ foo, "not after[-6,-5]", drool, Boolean.FALSE }, new Object[]{ foo, "not after[-5,-4]", drool, Boolean.TRUE } };
        runEvaluatorTest(data, OBJECT_TYPE);
    }

    @Test
    public void testBefore() {
        registry.addEvaluatorDefinition(BeforeEvaluatorDefinition.class.getName());
        EventFactHandle foo = new EventFactHandle(1, "foo", 1, 1, 2, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle bar = new EventFactHandle(2, "bar", 1, 2, 2, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle drool = new EventFactHandle(1, "drool", 1, 5, 3, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        final Object[][] data = new Object[][]{ new Object[]{ foo, "before", drool, Boolean.TRUE }, new Object[]{ foo, "before", bar, Boolean.FALSE }, new Object[]{ drool, "before", foo, Boolean.FALSE }, new Object[]{ drool, "before", bar, Boolean.FALSE }, new Object[]{ bar, "before", drool, Boolean.TRUE }, new Object[]{ bar, "before", foo, Boolean.FALSE }, new Object[]{ foo, "not before", drool, Boolean.FALSE }, new Object[]{ foo, "not before", bar, Boolean.TRUE }, new Object[]{ drool, "not before", foo, Boolean.TRUE }, new Object[]{ drool, "not before", bar, Boolean.TRUE }, new Object[]{ bar, "not before", drool, Boolean.FALSE }, new Object[]{ bar, "not before", foo, Boolean.TRUE }, new Object[]{ foo, "before[2]", drool, Boolean.TRUE }, new Object[]{ foo, "before[3]", drool, Boolean.FALSE }, new Object[]{ foo, "before[-1]", bar, Boolean.TRUE }, new Object[]{ foo, "before[-2]", bar, Boolean.TRUE }, new Object[]{ bar, "before[1]", drool, Boolean.TRUE }, new Object[]{ bar, "before[2]", drool, Boolean.FALSE }, new Object[]{ bar, "before[-3]", foo, Boolean.TRUE }, new Object[]{ bar, "before[-2]", foo, Boolean.FALSE }, new Object[]{ drool, "before[-6]", bar, Boolean.TRUE }, new Object[]{ drool, "before[-5]", bar, Boolean.FALSE }, new Object[]{ drool, "before[-7]", foo, Boolean.TRUE }, new Object[]{ drool, "before[-8]", foo, Boolean.TRUE }, new Object[]{ foo, "not before[2]", drool, Boolean.FALSE }, new Object[]{ foo, "not before[3]", drool, Boolean.TRUE }, new Object[]{ foo, "not before[-1]", bar, Boolean.FALSE }, new Object[]{ foo, "not before[-2]", bar, Boolean.FALSE }, new Object[]{ bar, "not before[1]", drool, Boolean.FALSE }, new Object[]{ bar, "not before[2]", drool, Boolean.TRUE }, new Object[]{ bar, "not before[-3]", foo, Boolean.FALSE }, new Object[]{ bar, "not before[-2]", foo, Boolean.TRUE }, new Object[]{ drool, "not before[-6]", bar, Boolean.FALSE }, new Object[]{ drool, "not before[-5]", bar, Boolean.TRUE }, new Object[]{ drool, "not before[-7]", foo, Boolean.FALSE }, new Object[]{ drool, "not before[-8]", foo, Boolean.FALSE }, new Object[]{ foo, "before[2,4]", drool, Boolean.TRUE }, new Object[]{ foo, "before[3,4]", drool, Boolean.FALSE }, new Object[]{ foo, "before[-1,1]", bar, Boolean.TRUE }, new Object[]{ foo, "before[0,-2]", bar, Boolean.TRUE }, new Object[]{ bar, "before[0,4]", drool, Boolean.TRUE }, new Object[]{ bar, "before[2,4]", drool, Boolean.FALSE }, new Object[]{ bar, "before[-4,0]", foo, Boolean.TRUE }, new Object[]{ bar, "before[-2,0]", foo, Boolean.FALSE }, new Object[]{ drool, "before[-6,-3]", bar, Boolean.TRUE }, new Object[]{ drool, "before[-5,-3]", bar, Boolean.FALSE }, new Object[]{ drool, "before[-7,-4]", foo, Boolean.TRUE }, new Object[]{ drool, "before[-6,-4]", foo, Boolean.FALSE }, new Object[]{ foo, "not before[2,4]", drool, Boolean.FALSE }, new Object[]{ foo, "not before[3,4]", drool, Boolean.TRUE }, new Object[]{ foo, "not before[-1,1]", bar, Boolean.FALSE }, new Object[]{ foo, "not before[0,-2]", bar, Boolean.FALSE }, new Object[]{ bar, "not before[0,4]", drool, Boolean.FALSE }, new Object[]{ bar, "not before[2,4]", drool, Boolean.TRUE }, new Object[]{ bar, "not before[-4,0]", foo, Boolean.FALSE }, new Object[]{ bar, "not before[-2,0]", foo, Boolean.TRUE }, new Object[]{ drool, "not before[-6,-3]", bar, Boolean.FALSE }, new Object[]{ drool, "not before[-5,-3]", bar, Boolean.TRUE }, new Object[]{ drool, "not before[-7,-4]", foo, Boolean.FALSE }, new Object[]{ drool, "not before[-6,-4]", foo, Boolean.TRUE } };
        runEvaluatorTest(data, OBJECT_TYPE);
    }

    @Test
    public void testCoincides() {
        registry.addEvaluatorDefinition(CoincidesEvaluatorDefinition.class.getName());
        EventFactHandle foo = new EventFactHandle(1, "foo", 1, 2, 3, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle bar = new EventFactHandle(2, "bar", 1, 2, 3, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle drool = new EventFactHandle(1, "drool", 1, 2, 2, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle mole = new EventFactHandle(1, "mole", 1, 1, 2, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        final Object[][] data = new Object[][]{ new Object[]{ foo, "coincides", bar, Boolean.TRUE }, new Object[]{ foo, "coincides", drool, Boolean.FALSE }, new Object[]{ foo, "coincides", mole, Boolean.FALSE }, new Object[]{ drool, "coincides", mole, Boolean.FALSE }, new Object[]{ foo, "not coincides", bar, Boolean.FALSE }, new Object[]{ foo, "not coincides", drool, Boolean.TRUE }, new Object[]{ foo, "not coincides", mole, Boolean.TRUE }, new Object[]{ drool, "not coincides", mole, Boolean.TRUE }, new Object[]{ foo, "coincides[1]", bar, Boolean.TRUE }, new Object[]{ foo, "coincides[1]", drool, Boolean.TRUE }, new Object[]{ foo, "coincides[2]", mole, Boolean.TRUE }, new Object[]{ foo, "coincides[1]", mole, Boolean.FALSE }, new Object[]{ drool, "coincides[1]", mole, Boolean.TRUE }, new Object[]{ foo, "not coincides[1]", bar, Boolean.FALSE }, new Object[]{ foo, "not coincides[1]", drool, Boolean.FALSE }, new Object[]{ foo, "not coincides[2]", mole, Boolean.FALSE }, new Object[]{ foo, "not coincides[1]", mole, Boolean.TRUE }, new Object[]{ drool, "not coincides[1]", mole, Boolean.FALSE }, new Object[]{ foo, "coincides[1,2]", bar, Boolean.TRUE }, new Object[]{ foo, "coincides[0,1]", drool, Boolean.TRUE }, new Object[]{ foo, "coincides[1,0]", drool, Boolean.FALSE }, new Object[]{ foo, "coincides[1,2]", mole, Boolean.TRUE }, new Object[]{ foo, "coincides[1,1]", mole, Boolean.FALSE }, new Object[]{ drool, "coincides[1,1]", mole, Boolean.TRUE }, new Object[]{ drool, "coincides[0,1]", mole, Boolean.FALSE }, new Object[]{ foo, "not coincides[1,2]", bar, Boolean.FALSE }, new Object[]{ foo, "not coincides[0,1]", drool, Boolean.FALSE }, new Object[]{ foo, "not coincides[1,0]", drool, Boolean.TRUE }, new Object[]{ foo, "not coincides[1,2]", mole, Boolean.FALSE }, new Object[]{ foo, "not coincides[1,1]", mole, Boolean.TRUE }, new Object[]{ drool, "not coincides[1,1]", mole, Boolean.FALSE }, new Object[]{ drool, "not coincides[0,1]", mole, Boolean.TRUE } };
        runEvaluatorTest(data, OBJECT_TYPE);
    }

    @Test
    public void testDuring() {
        registry.addEvaluatorDefinition(DuringEvaluatorDefinition.class.getName());
        EventFactHandle foo = new EventFactHandle(1, "foo", 1, 2, 10, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle bar = new EventFactHandle(2, "bar", 1, 4, 7, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle drool = new EventFactHandle(1, "drool", 1, 1, 5, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle mole = new EventFactHandle(1, "mole", 1, 7, 6, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        final Object[][] data = new Object[][]{ new Object[]{ foo, "during", bar, Boolean.FALSE }, new Object[]{ foo, "during", drool, Boolean.FALSE }, new Object[]{ foo, "during", mole, Boolean.FALSE }, new Object[]{ bar, "during", foo, Boolean.TRUE }, new Object[]{ bar, "during", drool, Boolean.FALSE }, new Object[]{ bar, "during", mole, Boolean.FALSE }, new Object[]{ foo, "not during", bar, Boolean.TRUE }, new Object[]{ foo, "not during", drool, Boolean.TRUE }, new Object[]{ foo, "not during", mole, Boolean.TRUE }, new Object[]{ bar, "not during", foo, Boolean.FALSE }, new Object[]{ bar, "not during", drool, Boolean.TRUE }, new Object[]{ bar, "not during", mole, Boolean.TRUE }, new Object[]{ bar, "during[2]", foo, Boolean.TRUE }, new Object[]{ bar, "during[3]", foo, Boolean.TRUE }, new Object[]{ bar, "during[1]", foo, Boolean.FALSE }, new Object[]{ bar, "not during[2]", foo, Boolean.FALSE }, new Object[]{ bar, "not during[3]", foo, Boolean.FALSE }, new Object[]{ bar, "not during[1]", foo, Boolean.TRUE }, new Object[]{ bar, "during[1, 2]", foo, Boolean.TRUE }, new Object[]{ bar, "during[2, 3]", foo, Boolean.FALSE }, new Object[]{ bar, "during[3, 3]", foo, Boolean.FALSE }, new Object[]{ bar, "not during[1, 2]", foo, Boolean.FALSE }, new Object[]{ bar, "not during[2, 3]", foo, Boolean.TRUE }, new Object[]{ bar, "not during[3, 3]", foo, Boolean.TRUE }, new Object[]{ bar, "during[2, 2, 1, 1]", foo, Boolean.TRUE }, new Object[]{ bar, "during[1, 5, 1, 3]", foo, Boolean.TRUE }, new Object[]{ bar, "during[0, 1, 0, 3]", foo, Boolean.FALSE }, new Object[]{ bar, "not during[2, 2, 1, 1]", foo, Boolean.FALSE }, new Object[]{ bar, "not during[1, 5, 1, 3]", foo, Boolean.FALSE }, new Object[]{ bar, "not during[0, 1, 0, 3]", foo, Boolean.TRUE } };
        runEvaluatorTest(data, OBJECT_TYPE);
    }

    @Test
    public void testIncludes() {
        registry.addEvaluatorDefinition(DuringEvaluatorDefinition.class.getName());
        EventFactHandle foo = new EventFactHandle(1, "foo", 1, 2, 10, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle bar = new EventFactHandle(2, "bar", 1, 4, 7, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle drool = new EventFactHandle(1, "drool", 1, 1, 5, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle mole = new EventFactHandle(1, "mole", 1, 7, 6, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        final Object[][] data = new Object[][]{ new Object[]{ bar, "includes", foo, Boolean.FALSE }, new Object[]{ drool, "includes", foo, Boolean.FALSE }, new Object[]{ mole, "includes", foo, Boolean.FALSE }, new Object[]{ foo, "includes", bar, Boolean.TRUE }, new Object[]{ drool, "includes", bar, Boolean.FALSE }, new Object[]{ mole, "includes", bar, Boolean.FALSE }, new Object[]{ bar, "not includes", foo, Boolean.TRUE }, new Object[]{ drool, "not includes", foo, Boolean.TRUE }, new Object[]{ mole, "not includes", foo, Boolean.TRUE }, new Object[]{ foo, "not includes", bar, Boolean.FALSE }, new Object[]{ drool, "not includes", bar, Boolean.TRUE }, new Object[]{ mole, "not includes", bar, Boolean.TRUE }, new Object[]{ foo, "includes[2]", bar, Boolean.TRUE }, new Object[]{ foo, "includes[3]", bar, Boolean.TRUE }, new Object[]{ foo, "includes[1]", bar, Boolean.FALSE }, new Object[]{ foo, "not includes[2]", bar, Boolean.FALSE }, new Object[]{ foo, "not includes[3]", bar, Boolean.FALSE }, new Object[]{ foo, "not includes[1]", bar, Boolean.TRUE }, new Object[]{ foo, "includes[1, 2]", bar, Boolean.TRUE }, new Object[]{ foo, "includes[2, 3]", bar, Boolean.FALSE }, new Object[]{ foo, "includes[3, 3]", bar, Boolean.FALSE }, new Object[]{ foo, "not includes[1, 2]", bar, Boolean.FALSE }, new Object[]{ foo, "not includes[2, 3]", bar, Boolean.TRUE }, new Object[]{ foo, "not includes[3, 3]", bar, Boolean.TRUE }, new Object[]{ foo, "includes[2, 2, 1, 1]", bar, Boolean.TRUE }, new Object[]{ foo, "includes[1, 5, 1, 3]", bar, Boolean.TRUE }, new Object[]{ foo, "includes[0, 1, 0, 3]", bar, Boolean.FALSE }, new Object[]{ foo, "not includes[2, 2, 1, 1]", bar, Boolean.FALSE }, new Object[]{ foo, "not includes[1, 5, 1, 3]", bar, Boolean.FALSE }, new Object[]{ foo, "not includes[0, 1, 0, 3]", bar, Boolean.TRUE } };
        runEvaluatorTest(data, OBJECT_TYPE);
    }

    @Test
    public void testFinishes() {
        registry.addEvaluatorDefinition(DuringEvaluatorDefinition.class.getName());
        EventFactHandle foo = new EventFactHandle(1, "foo", 1, 2, 10, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle bar = new EventFactHandle(2, "bar", 1, 5, 7, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle drool = new EventFactHandle(1, "drool", 1, 2, 10, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle mole = new EventFactHandle(1, "mole", 1, 7, 6, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        final Object[][] data = new Object[][]{ new Object[]{ bar, "finishes", foo, Boolean.TRUE }, new Object[]{ drool, "finishes", foo, Boolean.FALSE }, new Object[]{ mole, "finishes", foo, Boolean.FALSE }, new Object[]{ foo, "finishes", bar, Boolean.FALSE }, new Object[]{ bar, "not finishes", foo, Boolean.FALSE }, new Object[]{ drool, "not finishes", foo, Boolean.TRUE }, new Object[]{ mole, "not finishes", foo, Boolean.TRUE }, new Object[]{ foo, "not finishes", bar, Boolean.TRUE }, new Object[]{ bar, "finishes[1]", foo, Boolean.TRUE }, new Object[]{ drool, "finishes[1]", foo, Boolean.FALSE }, new Object[]{ mole, "finishes[1]", foo, Boolean.TRUE }, new Object[]{ foo, "finishes[1]", bar, Boolean.FALSE }, new Object[]{ bar, "not finishes[1]", foo, Boolean.FALSE }, new Object[]{ drool, "not finishes[1]", foo, Boolean.TRUE }, new Object[]{ mole, "not finishes[1]", foo, Boolean.FALSE }, new Object[]{ foo, "not finishes[1]", bar, Boolean.TRUE }, new Object[]{ mole, "finishes[3]", foo, Boolean.TRUE } };
        runEvaluatorTest(data, OBJECT_TYPE);
    }

    @Test
    public void testFinishedBy() {
        registry.addEvaluatorDefinition(DuringEvaluatorDefinition.class.getName());
        EventFactHandle foo = new EventFactHandle(1, "foo", 1, 2, 10, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle bar = new EventFactHandle(2, "bar", 1, 5, 7, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle drool = new EventFactHandle(1, "drool", 1, 2, 10, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle mole = new EventFactHandle(1, "mole", 1, 7, 6, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        final Object[][] data = new Object[][]{ new Object[]{ foo, "finishedby", bar, Boolean.TRUE }, new Object[]{ foo, "finishedby", drool, Boolean.FALSE }, new Object[]{ foo, "finishedby", mole, Boolean.FALSE }, new Object[]{ bar, "finishedby", foo, Boolean.FALSE }, new Object[]{ foo, "not finishedby", bar, Boolean.FALSE }, new Object[]{ foo, "not finishedby", drool, Boolean.TRUE }, new Object[]{ foo, "not finishedby", mole, Boolean.TRUE }, new Object[]{ bar, "not finishedby", foo, Boolean.TRUE }, new Object[]{ foo, "finishedby[1]", bar, Boolean.TRUE }, new Object[]{ foo, "finishedby[1]", drool, Boolean.FALSE }, new Object[]{ foo, "finishedby[1]", mole, Boolean.TRUE }, new Object[]{ bar, "finishedby[1]", foo, Boolean.FALSE }, new Object[]{ foo, "not finishedby[1]", bar, Boolean.FALSE }, new Object[]{ foo, "not finishedby[1]", drool, Boolean.TRUE }, new Object[]{ foo, "not finishedby[1]", mole, Boolean.FALSE }, new Object[]{ bar, "not finishedby[1]", foo, Boolean.TRUE }, new Object[]{ foo, "finishedby[3]", mole, Boolean.TRUE } };
        runEvaluatorTest(data, OBJECT_TYPE);
    }

    @Test
    public void testStarts() {
        registry.addEvaluatorDefinition(DuringEvaluatorDefinition.class.getName());
        EventFactHandle foo = new EventFactHandle(1, "foo", 1, 2, 10, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle bar = new EventFactHandle(2, "bar", 1, 2, 7, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle drool = new EventFactHandle(1, "drool", 1, 2, 10, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle mole = new EventFactHandle(1, "mole", 1, 1, 4, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        final Object[][] data = new Object[][]{ new Object[]{ bar, "starts", foo, Boolean.TRUE }, new Object[]{ drool, "starts", foo, Boolean.FALSE }, new Object[]{ mole, "starts", foo, Boolean.FALSE }, new Object[]{ foo, "starts", bar, Boolean.FALSE }, new Object[]{ bar, "not starts", foo, Boolean.FALSE }, new Object[]{ drool, "not starts", foo, Boolean.TRUE }, new Object[]{ mole, "not starts", foo, Boolean.TRUE }, new Object[]{ foo, "not starts", bar, Boolean.TRUE }, new Object[]{ bar, "starts[1]", foo, Boolean.TRUE }, new Object[]{ drool, "starts[1]", foo, Boolean.FALSE }, new Object[]{ mole, "starts[1]", foo, Boolean.TRUE }, new Object[]{ foo, "starts[1]", bar, Boolean.FALSE }, new Object[]{ bar, "not starts[1]", foo, Boolean.FALSE }, new Object[]{ drool, "not starts[1]", foo, Boolean.TRUE }, new Object[]{ mole, "not starts[1]", foo, Boolean.FALSE }, new Object[]{ foo, "not starts[1]", bar, Boolean.TRUE }, new Object[]{ mole, "starts[3]", foo, Boolean.TRUE } };
        runEvaluatorTest(data, OBJECT_TYPE);
    }

    @Test
    public void testStartedBy() {
        registry.addEvaluatorDefinition(DuringEvaluatorDefinition.class.getName());
        EventFactHandle foo = new EventFactHandle(1, "foo", 1, 2, 10, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle bar = new EventFactHandle(2, "bar", 1, 2, 7, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle drool = new EventFactHandle(1, "drool", 1, 2, 10, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle mole = new EventFactHandle(1, "mole", 1, 1, 6, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        final Object[][] data = new Object[][]{ new Object[]{ foo, "startedby", bar, Boolean.TRUE }, new Object[]{ foo, "startedby", drool, Boolean.FALSE }, new Object[]{ foo, "startedby", mole, Boolean.FALSE }, new Object[]{ bar, "startedby", foo, Boolean.FALSE }, new Object[]{ foo, "not startedby", bar, Boolean.FALSE }, new Object[]{ foo, "not startedby", drool, Boolean.TRUE }, new Object[]{ foo, "not startedby", mole, Boolean.TRUE }, new Object[]{ bar, "not startedby", foo, Boolean.TRUE }, new Object[]{ foo, "startedby[1]", bar, Boolean.TRUE }, new Object[]{ foo, "startedby[1]", drool, Boolean.FALSE }, new Object[]{ foo, "startedby[1]", mole, Boolean.TRUE }, new Object[]{ bar, "startedby[1]", foo, Boolean.FALSE }, new Object[]{ foo, "not startedby[1]", bar, Boolean.FALSE }, new Object[]{ foo, "not startedby[1]", drool, Boolean.TRUE }, new Object[]{ foo, "not startedby[1]", mole, Boolean.FALSE }, new Object[]{ bar, "not startedby[1]", foo, Boolean.TRUE }, new Object[]{ foo, "startedby[3]", mole, Boolean.TRUE } };
        runEvaluatorTest(data, OBJECT_TYPE);
    }

    @Test
    public void testMeets() {
        registry.addEvaluatorDefinition(DuringEvaluatorDefinition.class.getName());
        EventFactHandle foo = new EventFactHandle(1, "foo", 1, 2, 8, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle bar = new EventFactHandle(2, "bar", 1, 10, 7, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle drool = new EventFactHandle(1, "drool", 1, 8, 5, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle mole = new EventFactHandle(1, "mole", 1, 11, 4, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        final Object[][] data = new Object[][]{ new Object[]{ foo, "meets", bar, Boolean.TRUE }, new Object[]{ foo, "meets", drool, Boolean.FALSE }, new Object[]{ foo, "meets", mole, Boolean.FALSE }, new Object[]{ foo, "not meets", bar, Boolean.FALSE }, new Object[]{ foo, "not meets", drool, Boolean.TRUE }, new Object[]{ foo, "not meets", mole, Boolean.TRUE }, new Object[]{ foo, "meets[1]", bar, Boolean.TRUE }, new Object[]{ foo, "meets[1]", drool, Boolean.FALSE }, new Object[]{ foo, "meets[1]", mole, Boolean.TRUE }, new Object[]{ foo, "meets[2]", drool, Boolean.TRUE }, new Object[]{ foo, "not meets[1]", bar, Boolean.FALSE }, new Object[]{ foo, "not meets[1]", drool, Boolean.TRUE }, new Object[]{ foo, "not meets[1]", mole, Boolean.FALSE }, new Object[]{ foo, "not meets[2]", drool, Boolean.FALSE } };
        runEvaluatorTest(data, OBJECT_TYPE);
    }

    @Test
    public void testMetBy() {
        registry.addEvaluatorDefinition(DuringEvaluatorDefinition.class.getName());
        EventFactHandle foo = new EventFactHandle(1, "foo", 1, 10, 8, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle bar = new EventFactHandle(2, "bar", 1, 2, 8, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle drool = new EventFactHandle(1, "drool", 1, 5, 3, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle mole = new EventFactHandle(1, "mole", 1, 4, 7, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        final Object[][] data = new Object[][]{ new Object[]{ foo, "metby", bar, Boolean.TRUE }, new Object[]{ foo, "metby", drool, Boolean.FALSE }, new Object[]{ foo, "metby", mole, Boolean.FALSE }, new Object[]{ foo, "not metby", bar, Boolean.FALSE }, new Object[]{ foo, "not metby", drool, Boolean.TRUE }, new Object[]{ foo, "not metby", mole, Boolean.TRUE }, new Object[]{ foo, "metby[1]", bar, Boolean.TRUE }, new Object[]{ foo, "metby[1]", drool, Boolean.FALSE }, new Object[]{ foo, "metby[1]", mole, Boolean.TRUE }, new Object[]{ foo, "metby[2]", drool, Boolean.TRUE }, new Object[]{ foo, "not metby[1]", bar, Boolean.FALSE }, new Object[]{ foo, "not metby[1]", drool, Boolean.TRUE }, new Object[]{ foo, "not metby[1]", mole, Boolean.FALSE }, new Object[]{ foo, "not metby[2]", drool, Boolean.FALSE } };
        runEvaluatorTest(data, OBJECT_TYPE);
    }

    @Test
    public void testOverlaps() {
        registry.addEvaluatorDefinition(DuringEvaluatorDefinition.class.getName());
        EventFactHandle foo = new EventFactHandle(1, "foo", 1, 2, 8, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle bar = new EventFactHandle(2, "bar", 1, 7, 7, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle drool = new EventFactHandle(1, "drool", 1, 11, 5, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle mole = new EventFactHandle(1, "mole", 1, 5, 5, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        final Object[][] data = new Object[][]{ new Object[]{ foo, "overlaps", bar, Boolean.TRUE }, new Object[]{ foo, "overlaps", drool, Boolean.FALSE }, new Object[]{ foo, "overlaps", mole, Boolean.FALSE }, new Object[]{ foo, "not overlaps", bar, Boolean.FALSE }, new Object[]{ foo, "not overlaps", drool, Boolean.TRUE }, new Object[]{ foo, "not overlaps", mole, Boolean.TRUE }, new Object[]{ foo, "overlaps[3]", bar, Boolean.TRUE }, new Object[]{ foo, "overlaps[3]", drool, Boolean.FALSE }, new Object[]{ foo, "overlaps[3]", mole, Boolean.FALSE }, new Object[]{ foo, "overlaps[2]", bar, Boolean.FALSE }, new Object[]{ foo, "overlaps[6]", mole, Boolean.FALSE }, new Object[]{ foo, "not overlaps[3]", bar, Boolean.FALSE }, new Object[]{ foo, "not overlaps[3]", drool, Boolean.TRUE }, new Object[]{ foo, "not overlaps[3]", mole, Boolean.TRUE }, new Object[]{ foo, "not overlaps[2]", bar, Boolean.TRUE }, new Object[]{ foo, "not overlaps[6]", mole, Boolean.TRUE }, new Object[]{ foo, "overlaps[1,3]", bar, Boolean.TRUE }, new Object[]{ foo, "overlaps[1,3]", drool, Boolean.FALSE }, new Object[]{ foo, "overlaps[1,3]", mole, Boolean.FALSE }, new Object[]{ foo, "overlaps[4,6]", bar, Boolean.FALSE }, new Object[]{ foo, "overlaps[1,8]", mole, Boolean.FALSE }, new Object[]{ foo, "not overlaps[1,3]", bar, Boolean.FALSE }, new Object[]{ foo, "not overlaps[1,3]", drool, Boolean.TRUE }, new Object[]{ foo, "not overlaps[1,3]", mole, Boolean.TRUE }, new Object[]{ foo, "not overlaps[4,6]", bar, Boolean.TRUE }, new Object[]{ foo, "not overlaps[1,8]", mole, Boolean.TRUE } };
        runEvaluatorTest(data, OBJECT_TYPE);
    }

    @Test
    public void testOverlapedBy() {
        registry.addEvaluatorDefinition(DuringEvaluatorDefinition.class.getName());
        EventFactHandle foo = new EventFactHandle(1, "foo", 1, 7, 8, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle bar = new EventFactHandle(2, "bar", 1, 2, 8, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle drool = new EventFactHandle(1, "drool", 1, 11, 5, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        EventFactHandle mole = new EventFactHandle(1, "mole", 1, 7, 3, new DisconnectedWorkingMemoryEntryPoint("DEFAULT"));
        final Object[][] data = new Object[][]{ new Object[]{ foo, "overlappedby", bar, Boolean.TRUE }, new Object[]{ foo, "overlappedby", drool, Boolean.FALSE }, new Object[]{ foo, "overlappedby", mole, Boolean.FALSE }, new Object[]{ foo, "not overlappedby", bar, Boolean.FALSE }, new Object[]{ foo, "not overlappedby", drool, Boolean.TRUE }, new Object[]{ foo, "not overlappedby", mole, Boolean.TRUE }, new Object[]{ foo, "overlappedby[3]", bar, Boolean.TRUE }, new Object[]{ foo, "overlappedby[3]", drool, Boolean.FALSE }, new Object[]{ foo, "overlappedby[3]", mole, Boolean.FALSE }, new Object[]{ foo, "overlappedby[2]", bar, Boolean.FALSE }, new Object[]{ foo, "overlappedby[6]", mole, Boolean.FALSE }, new Object[]{ foo, "not overlappedby[3]", bar, Boolean.FALSE }, new Object[]{ foo, "not overlappedby[3]", drool, Boolean.TRUE }, new Object[]{ foo, "not overlappedby[3]", mole, Boolean.TRUE }, new Object[]{ foo, "not overlappedby[2]", bar, Boolean.TRUE }, new Object[]{ foo, "not overlappedby[6]", mole, Boolean.TRUE }, new Object[]{ foo, "overlappedby[1,3]", bar, Boolean.TRUE }, new Object[]{ foo, "overlappedby[1,3]", drool, Boolean.FALSE }, new Object[]{ foo, "overlappedby[1,3]", mole, Boolean.FALSE }, new Object[]{ foo, "overlappedby[4,6]", bar, Boolean.FALSE }, new Object[]{ foo, "overlappedby[1,8]", mole, Boolean.FALSE }, new Object[]{ foo, "not overlappedby[1,3]", bar, Boolean.FALSE }, new Object[]{ foo, "not overlappedby[1,3]", drool, Boolean.TRUE }, new Object[]{ foo, "not overlappedby[1,3]", mole, Boolean.TRUE }, new Object[]{ foo, "not overlappedby[4,6]", bar, Boolean.TRUE }, new Object[]{ foo, "not overlappedby[1,8]", mole, Boolean.TRUE } };
        runEvaluatorTest(data, OBJECT_TYPE);
    }

    public static class MockExtractor implements InternalReadAccessor {
        private static final long serialVersionUID = 510L;

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }

        public boolean isSelfReference() {
            return true;
        }

        public boolean getBooleanValue(InternalWorkingMemory workingMemory, final Object object) {
            return object != null ? ((Boolean) (object)).booleanValue() : false;
        }

        public byte getByteValue(InternalWorkingMemory workingMemory, final Object object) {
            return object != null ? ((Number) (object)).byteValue() : ((byte) (0));
        }

        public char getCharValue(InternalWorkingMemory workingMemory, final Object object) {
            return object != null ? ((Character) (object)).charValue() : '\u0000';
        }

        public double getDoubleValue(InternalWorkingMemory workingMemory, final Object object) {
            return object != null ? ((Number) (object)).doubleValue() : 0.0;
        }

        public Class<?> getExtractToClass() {
            return null;
        }

        public String getExtractToClassName() {
            return null;
        }

        public float getFloatValue(InternalWorkingMemory workingMemory, final Object object) {
            return object != null ? ((Number) (object)).floatValue() : ((float) (0.0));
        }

        public int getHashCode(InternalWorkingMemory workingMemory, final Object object) {
            return 0;
        }

        public int getIntValue(InternalWorkingMemory workingMemory, final Object object) {
            return object != null ? ((Number) (object)).intValue() : 0;
        }

        public long getLongValue(InternalWorkingMemory workingMemory, final Object object) {
            return object != null ? ((Number) (object)).longValue() : 0;
        }

        public Method getNativeReadMethod() {
            return null;
        }

        public String getNativeReadMethodName() {
            return null;
        }

        public short getShortValue(InternalWorkingMemory workingMemory, final Object object) {
            return object != null ? ((Number) (object)).shortValue() : ((short) (0));
        }

        public Object getValue(InternalWorkingMemory workingMemory, final Object object) {
            return object;
        }

        public boolean isNullValue(InternalWorkingMemory workingMemory, final Object object) {
            return object == null;
        }

        public ValueType getValueType() {
            // TODO Auto-generated method stub
            return null;
        }

        public int getIndex() {
            return 0;
        }

        public boolean isGlobal() {
            return false;
        }

        public boolean getBooleanValue(Object object) {
            // TODO Auto-generated method stub
            return false;
        }

        public byte getByteValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public char getCharValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public double getDoubleValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public float getFloatValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public int getHashCode(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public int getIntValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public long getLongValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public short getShortValue(Object object) {
            // TODO Auto-generated method stub
            return 0;
        }

        public Object getValue(Object object) {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean isNullValue(Object object) {
            // TODO Auto-generated method stub
            return false;
        }

        public BigDecimal getBigDecimalValue(InternalWorkingMemory workingMemory, Object object) {
            // TODO Auto-generated method stub
            return null;
        }

        public BigInteger getBigIntegerValue(InternalWorkingMemory workingMemory, Object object) {
            // TODO Auto-generated method stub
            return null;
        }

        public BigDecimal getBigDecimalValue(Object object) {
            // TODO Auto-generated method stub
            return null;
        }

        public BigInteger getBigIntegerValue(Object object) {
            // TODO Auto-generated method stub
            return null;
        }
    }
}

