/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.security.zynamics.reil.algorithms.mono2.registertracking;


import com.google.common.collect.Sets;
import com.google.security.zynamics.reil.ReilFunction;
import com.google.security.zynamics.reil.ReilGraph;
import com.google.security.zynamics.reil.algorithms.mono2.common.MonoReilSolverResult;
import com.google.security.zynamics.reil.algorithms.mono2.common.enums.AnalysisDirection;
import com.google.security.zynamics.reil.translators.StandardEnvironment;
import com.google.security.zynamics.zylib.disassembly.IAddress;
import com.google.security.zynamics.zylib.disassembly.MockInstruction;
import java.util.HashSet;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class RegisterTrackerTest {
    private final StandardEnvironment environment = new StandardEnvironment();

    private ReilGraph m_graph;

    private ReilFunction m_function;

    private MockInstruction m_movInstruction;

    private MockInstruction m_shldInstruction;

    private RegisterTrackingOptions m_options;

    @Test
    public void trackBackwardIncoming() {
        m_options = new RegisterTrackingOptions(true, new HashSet<String>(), true, AnalysisDirection.UP);
        final String trackedRegister = "ecx";
        final MonoReilSolverResult<RegisterSetLatticeElement> result = RegisterTracker.track(m_function, m_shldInstruction, trackedRegister, m_options);
        final Map<IAddress, RegisterSetLatticeElement> resultMap = result.generateAddressToStateMapping(m_shldInstruction, m_options.trackIncoming());
        for (final Map.Entry<IAddress, RegisterSetLatticeElement> resultEntry : resultMap.entrySet()) {
            if (((resultEntry.getKey().toLong()) >> 8) == 5) {
                final RegisterSetLatticeElement movInstructionResult = resultEntry.getValue();
                Assert.assertTrue(movInstructionResult.getNewlyTaintedRegisters().isEmpty());
                Assert.assertTrue(movInstructionResult.getReadRegisters().contains("ecx"));
                Assert.assertTrue(movInstructionResult.getTaintedRegisters().contains("edx"));
                Assert.assertTrue(movInstructionResult.getUntaintedRegisters().contains("ecx"));
                Assert.assertTrue(movInstructionResult.getUpdatedRegisters().isEmpty());
            }
            if (((resultEntry.getKey().toLong()) >> 8) == 8) {
                final RegisterSetLatticeElement shldInstructionResult = resultEntry.getValue();
                Assert.assertTrue(shldInstructionResult.getNewlyTaintedRegisters().contains("edx"));
                Assert.assertTrue(shldInstructionResult.getReadRegisters().contains("ecx"));
                Assert.assertTrue(shldInstructionResult.getTaintedRegisters().contains("ecx"));
                Assert.assertTrue(shldInstructionResult.getTaintedRegisters().contains("edx"));
                Assert.assertTrue(shldInstructionResult.getUntaintedRegisters().contains("ecx"));
                Assert.assertTrue(shldInstructionResult.getUpdatedRegisters().contains("ecx"));
            }
        }
    }

    @Test
    public void trackBackwardOutgoing() {
        m_options = new RegisterTrackingOptions(true, new HashSet<String>(), false, AnalysisDirection.UP);
        final String trackedRegister = "ecx";
        final MonoReilSolverResult<RegisterSetLatticeElement> result = RegisterTracker.track(m_function, m_shldInstruction, trackedRegister, m_options);
        final Map<IAddress, RegisterSetLatticeElement> resultMap = result.generateAddressToStateMapping(m_shldInstruction, m_options.trackIncoming());
        for (final Map.Entry<IAddress, RegisterSetLatticeElement> resultEntry : resultMap.entrySet()) {
            if (((resultEntry.getKey().toLong()) >> 8) == 5) {
                final RegisterSetLatticeElement movInstructionResult = resultEntry.getValue();
                Assert.assertTrue(movInstructionResult.getNewlyTaintedRegisters().isEmpty());
                Assert.assertTrue(movInstructionResult.getReadRegisters().contains("ecx"));
                Assert.assertTrue(movInstructionResult.getTaintedRegisters().contains("edx"));
                Assert.assertTrue(movInstructionResult.getUntaintedRegisters().contains("ecx"));
                Assert.assertTrue(movInstructionResult.getUpdatedRegisters().isEmpty());
            }
            if (((resultEntry.getKey().toLong()) >> 8) == 8) {
                final RegisterSetLatticeElement shldInstructionResult = resultEntry.getValue();
                Assert.assertTrue(shldInstructionResult.getNewlyTaintedRegisters().contains("edx"));
                Assert.assertTrue(shldInstructionResult.getReadRegisters().contains("ecx"));
                Assert.assertTrue(shldInstructionResult.getTaintedRegisters().contains("ecx"));
                Assert.assertTrue(shldInstructionResult.getTaintedRegisters().contains("edx"));
                Assert.assertTrue(shldInstructionResult.getUntaintedRegisters().contains("ecx"));
                Assert.assertTrue(shldInstructionResult.getUpdatedRegisters().contains("ecx"));
            }
        }
    }

    @Test
    public void trackForwardIncoming() {
        m_options = new RegisterTrackingOptions(true, new HashSet<String>(), true, AnalysisDirection.DOWN);
        final String trackedRegister = "ebp";
        final MonoReilSolverResult<RegisterSetLatticeElement> result = RegisterTracker.track(m_function, m_movInstruction, trackedRegister, m_options);
        final Map<IAddress, RegisterSetLatticeElement> resultMap = result.generateAddressToStateMapping(m_movInstruction, m_options.trackIncoming());
        for (final Map.Entry<IAddress, RegisterSetLatticeElement> resultEntry : resultMap.entrySet()) {
            if (((resultEntry.getKey().toLong()) >> 8) == 5) {
                final RegisterSetLatticeElement movInstructionResult = resultEntry.getValue();
                Assert.assertTrue(movInstructionResult.getNewlyTaintedRegisters().isEmpty());
                Assert.assertTrue(movInstructionResult.getReadRegisters().contains("ebp"));
                Assert.assertTrue(movInstructionResult.getTaintedRegisters().contains("ebp"));
                Assert.assertTrue(movInstructionResult.getUntaintedRegisters().isEmpty());
                Assert.assertTrue(movInstructionResult.getUpdatedRegisters().isEmpty());
            }
        }
    }

    @Test
    public void trackForwardOutgoing() {
        m_options = new RegisterTrackingOptions(true, new HashSet<String>(), false, AnalysisDirection.DOWN);
        final String trackedRegister = "ecx";
        final MonoReilSolverResult<RegisterSetLatticeElement> result = RegisterTracker.track(m_function, m_movInstruction, trackedRegister, m_options);
        final Map<IAddress, RegisterSetLatticeElement> resultMap = result.generateAddressToStateMapping(m_movInstruction, m_options.trackIncoming());
        for (final Map.Entry<IAddress, RegisterSetLatticeElement> resultEntry : resultMap.entrySet()) {
            if (((resultEntry.getKey().toLong()) >> 8) == 5) {
                final RegisterSetLatticeElement movInstructionResult = resultEntry.getValue();
                Assert.assertTrue(movInstructionResult.getNewlyTaintedRegisters().contains("ecx"));
                Assert.assertTrue(movInstructionResult.getReadRegisters().isEmpty());
                Assert.assertTrue(movInstructionResult.getTaintedRegisters().contains("ecx"));
                Assert.assertTrue(movInstructionResult.getUntaintedRegisters().isEmpty());
                Assert.assertTrue(movInstructionResult.getUpdatedRegisters().isEmpty());
            }
            if (((resultEntry.getKey().toLong()) >> 8) == 8) {
                final RegisterSetLatticeElement shldInstructionResult = resultEntry.getValue();
                final HashSet<String> newSet = Sets.newHashSet("ZF", "SF", "CF", "ecx", "OF");
                final HashSet<String> flagSet = Sets.newHashSet("ZF", "SF", "CF", "OF");
                Assert.assertTrue(shldInstructionResult.getNewlyTaintedRegisters().containsAll(flagSet));
                Assert.assertTrue(shldInstructionResult.getReadRegisters().contains("ecx"));
                Assert.assertTrue(shldInstructionResult.getTaintedRegisters().containsAll(newSet));
                Assert.assertTrue(shldInstructionResult.getUntaintedRegisters().contains("ecx"));
                Assert.assertTrue(shldInstructionResult.getUpdatedRegisters().contains("ecx"));
            }
        }
    }
}

