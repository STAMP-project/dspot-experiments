/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.profiler.memory;


import com.google.devtools.build.lib.packages.RuleClass;
import com.google.devtools.build.lib.packages.RuleFunction;
import com.google.devtools.build.lib.profiler.memory.AllocationTracker.RuleBytes;
import com.google.devtools.build.lib.syntax.ASTNode;
import com.google.devtools.build.lib.syntax.BaseFunction;
import com.google.devtools.build.lib.syntax.Callstack;
import com.google.devtools.build.lib.syntax.SyntaxTreeVisitor;
import com.google.perftools.profiles.ProfileProto.Profile;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link AllocationTracker}.
 */
@RunWith(JUnit4.class)
public class AllocationTrackerTest {
    private AllocationTracker allocationTracker;

    static class TestNode extends ASTNode {
        TestNode(String file, int line) {
            setLocation(AllocationTrackerTest.location(file, line));
        }

        @Override
        public void prettyPrint(Appendable buffer, int indentLevel) throws IOException {
        }

        @Override
        public void accept(SyntaxTreeVisitor visitor) {
        }
    }

    static class TestFunction extends BaseFunction {
        TestFunction(String file, String name, int line) {
            super(name);
            this.location = AllocationTrackerTest.location(file, line);
        }
    }

    static class TestRuleFunction extends AllocationTrackerTest.TestFunction implements RuleFunction {
        private final RuleClass ruleClass;

        TestRuleFunction(String file, String name, int line) {
            super(file, name, line);
            this.ruleClass = Mockito.mock(RuleClass.class);
            Mockito.when(ruleClass.getName()).thenReturn(name);
            Mockito.when(ruleClass.getKey()).thenReturn(name);
        }

        @Override
        public RuleClass getRuleClass() {
            return ruleClass;
        }
    }

    @Test
    public void testSimpleMemoryProfile() {
        Object allocation = new Object();
        Callstack.push(new AllocationTrackerTest.TestFunction("fileA", "fn", 120));
        Callstack.push(new AllocationTrackerTest.TestNode("fileA", 10));
        allocationTracker.sampleAllocation(1, "", allocation, 12);
        Callstack.pop();
        Callstack.pop();
        Map<String, RuleBytes> rules = new HashMap<>();
        Map<String, RuleBytes> aspects = new HashMap<>();
        allocationTracker.getRuleMemoryConsumption(rules, aspects);
        assertThat(rules).isEmpty();
        assertThat(aspects).isEmpty();
        Profile profile = allocationTracker.buildMemoryProfile();
        assertThat(profile.getSampleList()).hasSize(1);
        assertThat(sampleToCallstack(profile, profile.getSample(0))).containsExactly("fileA:fn:10");
    }

    @Test
    public void testLongerCallstack() {
        Object allocation = new Object();
        Callstack.push(new AllocationTrackerTest.TestFunction("fileB", "fnB", 120));
        Callstack.push(new AllocationTrackerTest.TestNode("fileB", 10));
        Callstack.push(new AllocationTrackerTest.TestNode("fileB", 12));
        Callstack.push(new AllocationTrackerTest.TestNode("fileB", 14));
        Callstack.push(new AllocationTrackerTest.TestNode("fileB", 18));
        Callstack.push(new AllocationTrackerTest.TestFunction("fileA", "fnA", 120));
        Callstack.push(new AllocationTrackerTest.TestNode("fileA", 10));
        allocationTracker.sampleAllocation(1, "", allocation, 12);
        for (int i = 0; i < 7; ++i) {
            Callstack.pop();
        }
        Profile profile = allocationTracker.buildMemoryProfile();
        assertThat(profile.getSampleList()).hasSize(1);
        assertThat(sampleToCallstack(profile, profile.getSample(0))).containsExactly("fileB:fnB:18", "fileA:fnA:10");
    }

    @Test
    public void testConfiguredTargetsMemoryAllocation() {
        RuleClass ruleClass = Mockito.mock(RuleClass.class);
        Mockito.when(ruleClass.getName()).thenReturn("rule");
        Mockito.when(ruleClass.getKey()).thenReturn("rule");
        CurrentRuleTracker.beginConfiguredTarget(ruleClass);
        Object ruleAllocation0 = new Object();
        Object ruleAllocation1 = new Object();
        allocationTracker.sampleAllocation(1, "", ruleAllocation0, 10);
        allocationTracker.sampleAllocation(1, "", ruleAllocation1, 20);
        CurrentRuleTracker.endConfiguredTarget();
        CurrentRuleTracker.beginConfiguredAspect(() -> "aspect");
        Object aspectAllocation = new Object();
        allocationTracker.sampleAllocation(1, "", aspectAllocation, 12);
        CurrentRuleTracker.endConfiguredAspect();
        Map<String, RuleBytes> rules = new HashMap<>();
        Map<String, RuleBytes> aspects = new HashMap<>();
        allocationTracker.getRuleMemoryConsumption(rules, aspects);
        assertThat(rules).containsExactly("rule", new RuleBytes("rule").addBytes(30L));
        assertThat(aspects).containsExactly("aspect", new RuleBytes("aspect").addBytes(12L));
        Profile profile = allocationTracker.buildMemoryProfile();
        assertThat(profile.getSampleList()).isEmpty();// No callstacks

    }

    @Test
    public void testLoadingPhaseRuleAllocations() {
        Object allocation = new Object();
        Callstack.push(new AllocationTrackerTest.TestFunction("fileB", "fnB", 120));
        Callstack.push(new AllocationTrackerTest.TestNode("fileB", 18));
        Callstack.push(new AllocationTrackerTest.TestFunction("fileA", "fnA", 120));
        Callstack.push(new AllocationTrackerTest.TestNode("fileA", 10));
        Callstack.push(new AllocationTrackerTest.TestRuleFunction("<native>", "proto_library", (-1)));
        allocationTracker.sampleAllocation(1, "", allocation, 128);
        for (int i = 0; i < 5; ++i) {
            Callstack.pop();
        }
        Map<String, RuleBytes> rules = new HashMap<>();
        Map<String, RuleBytes> aspects = new HashMap<>();
        allocationTracker.getRuleMemoryConsumption(rules, aspects);
        assertThat(rules).containsExactly("proto_library", new RuleBytes("proto_library").addBytes(128L));
    }
}

