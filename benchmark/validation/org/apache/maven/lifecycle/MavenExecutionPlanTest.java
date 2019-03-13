/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.maven.lifecycle;


import java.util.Set;
import junit.framework.TestCase;
import org.apache.maven.lifecycle.internal.ExecutionPlanItem;
import org.apache.maven.lifecycle.internal.stub.LifecycleExecutionPlanCalculatorStub;
import org.apache.maven.model.Plugin;


/**
 *
 *
 * @author Kristian Rosenvold
 */
public class MavenExecutionPlanTest extends TestCase {
    public void testFindLastInPhase() throws Exception {
        MavenExecutionPlan plan = LifecycleExecutionPlanCalculatorStub.getProjectAExceutionPlan();
        ExecutionPlanItem expected = plan.findLastInPhase("package");
        ExecutionPlanItem beerPhase = plan.findLastInPhase("BEER");// Beer comes straight after package in stub

        TestCase.assertEquals(expected, beerPhase);
        TestCase.assertNotNull(expected);
    }

    public void testThreadSafeMojos() throws Exception {
        MavenExecutionPlan plan = LifecycleExecutionPlanCalculatorStub.getProjectAExceutionPlan();
        final Set<Plugin> unSafePlugins = plan.getNonThreadSafePlugins();
        // There is only a single threadsafe plugin here...
        TestCase.assertEquals(((plan.size()) - 1), unSafePlugins.size());
    }

    public void testFindLastWhenFirst() throws Exception {
        MavenExecutionPlan plan = LifecycleExecutionPlanCalculatorStub.getProjectAExceutionPlan();
        ExecutionPlanItem beerPhase = plan.findLastInPhase(LifecycleExecutionPlanCalculatorStub.VALIDATE.getPhase());// Beer comes straight after package in stub

        TestCase.assertNull(beerPhase);
    }

    public void testFindLastInPhaseMisc() throws Exception {
        MavenExecutionPlan plan = LifecycleExecutionPlanCalculatorStub.getProjectAExceutionPlan();
        TestCase.assertNull(plan.findLastInPhase("pacXkage"));
        // Beer comes straight after package in stub, much like real life.
        TestCase.assertNotNull(plan.findLastInPhase(LifecycleExecutionPlanCalculatorStub.INITIALIZE.getPhase()));
    }
}

