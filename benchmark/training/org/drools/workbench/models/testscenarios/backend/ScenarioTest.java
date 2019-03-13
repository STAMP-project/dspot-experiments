/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.models.testscenarios.backend;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.RetractFact;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.models.testscenarios.shared.VerifyFact;
import org.drools.workbench.models.testscenarios.shared.VerifyField;
import org.drools.workbench.models.testscenarios.shared.VerifyRuleFired;
import org.junit.Assert;
import org.junit.Test;


public class ScenarioTest {
    @Test
    public void testInsertBetween() {
        Scenario sc = new Scenario();
        VerifyRuleFired vf = new VerifyRuleFired();
        sc.insertBetween(null, vf);
        Assert.assertEquals(1, sc.getFixtures().size());
        Assert.assertEquals(vf, sc.getFixtures().get(0));
        VerifyRuleFired vf2 = new VerifyRuleFired();
        sc.getFixtures().add(vf2);
        VerifyRuleFired vf3 = new VerifyRuleFired();
        sc.insertBetween(vf, vf3);
        Assert.assertEquals(3, sc.getFixtures().size());
        Assert.assertEquals(vf, sc.getFixtures().get(0));
        Assert.assertEquals(vf2, sc.getFixtures().get(1));
        Assert.assertEquals(vf3, sc.getFixtures().get(2));
        VerifyRuleFired vf4 = new VerifyRuleFired();
        sc.insertBetween(vf2, vf4);
        Assert.assertEquals(4, sc.getFixtures().size());
        Assert.assertEquals(3, sc.getFixtures().indexOf(vf4));
        Assert.assertEquals(2, sc.getFixtures().indexOf(vf3));
        // assertEquals(vf4, sc.fixtures.get(3));
        Assert.assertEquals(1, sc.getFixtures().indexOf(vf2));
        // assertEquals(vf2, sc.fixtures.get(2));
        Assert.assertEquals(0, sc.getFixtures().indexOf(vf));
        VerifyRuleFired vf5 = new VerifyRuleFired();
        sc.insertBetween(null, vf5);
        Assert.assertEquals(5, sc.getFixtures().size());
        Assert.assertEquals(4, sc.getFixtures().indexOf(vf5));
        sc = new Scenario();
        sc.getFixtures().add(vf);
        ExecutionTrace ex = new ExecutionTrace();
        sc.getFixtures().add(ex);
        sc.insertBetween(null, vf2);
        Assert.assertEquals(0, sc.getFixtures().indexOf(vf));
        Assert.assertEquals(1, sc.getFixtures().indexOf(vf2));
        Assert.assertEquals(2, sc.getFixtures().indexOf(ex));
        Assert.assertEquals(3, sc.getFixtures().size());
        sc.insertBetween(ex, vf3);
        Assert.assertEquals(4, sc.getFixtures().size());
        Assert.assertEquals(0, sc.getFixtures().indexOf(vf));
        Assert.assertEquals(1, sc.getFixtures().indexOf(vf2));
        Assert.assertEquals(2, sc.getFixtures().indexOf(ex));
        Assert.assertEquals(3, sc.getFixtures().indexOf(vf3));
        ExecutionTrace ex2 = new ExecutionTrace();
        sc.getFixtures().add(ex2);
        sc.insertBetween(ex, vf4);
        Assert.assertEquals(6, sc.getFixtures().size());
        Assert.assertEquals(0, sc.getFixtures().indexOf(vf));
        Assert.assertEquals(1, sc.getFixtures().indexOf(vf2));
        Assert.assertEquals(2, sc.getFixtures().indexOf(ex));
        Assert.assertEquals(3, sc.getFixtures().indexOf(vf3));
        Assert.assertEquals(4, sc.getFixtures().indexOf(vf4));
        Assert.assertEquals(5, sc.getFixtures().indexOf(ex2));
        sc.insertBetween(ex2, vf5);
        Assert.assertEquals(7, sc.getFixtures().size());
        Assert.assertEquals(0, sc.getFixtures().indexOf(vf));
        Assert.assertEquals(1, sc.getFixtures().indexOf(vf2));
        Assert.assertEquals(2, sc.getFixtures().indexOf(ex));
        Assert.assertEquals(3, sc.getFixtures().indexOf(vf3));
        Assert.assertEquals(4, sc.getFixtures().indexOf(vf4));
        Assert.assertEquals(5, sc.getFixtures().indexOf(ex2));
        Assert.assertEquals(6, sc.getFixtures().indexOf(vf5));
        sc = new Scenario();
        sc.getFixtures().add(ex);
        sc.insertBetween(null, vf);
        Assert.assertEquals(2, sc.getFixtures().size());
        Assert.assertEquals(0, sc.getFixtures().indexOf(vf));
        Assert.assertEquals(1, sc.getFixtures().indexOf(ex));
    }

    @Test
    public void testExecutionTrace() {
        Scenario sc = new Scenario();
        sc.getGlobals().add(new FactData("A", "A", new ArrayList(), false));
        sc.getFixtures().add(new FactData("B", "B", new ArrayList(), true));
        sc.getFixtures().add(new FactData("C", "C", new ArrayList(), true));
        ExecutionTrace ex1 = new ExecutionTrace();
        sc.getFixtures().add(ex1);
        sc.getFixtures().add(new VerifyFact());
        sc.getFixtures().add(new RetractFact());
        sc.getFixtures().add(new FactData("D", "D", new ArrayList(), false));
        sc.getFixtures().add(new FactData("E", "E", new ArrayList(), false));
        ExecutionTrace ex2 = new ExecutionTrace();
        sc.getFixtures().add(ex2);
        sc.getFixtures().add(new VerifyFact());
        sc.getFixtures().add(new FactData("F", "F", new ArrayList(), false));
        ExecutionTrace ex3 = new ExecutionTrace();
        sc.getFixtures().add(ex3);
        Assert.assertEquals(11, sc.getFixtures().size());
        sc.removeExecutionTrace(ex2);
        Assert.assertEquals(6, sc.getFixtures().size());
        Assert.assertTrue(sc.isFactNameReserved("A"));
        Assert.assertTrue(sc.isFactNameReserved("B"));
        Assert.assertTrue(sc.isFactNameReserved("C"));
        Assert.assertFalse(sc.isFactNameReserved("D"));
        Assert.assertFalse(sc.isFactNameReserved("E"));
        Assert.assertTrue(sc.isFactNameReserved("F"));
    }

    @Test
    public void testRemoveFixture() {
        Scenario sc = new Scenario();
        VerifyRuleFired vf1 = new VerifyRuleFired();
        VerifyRuleFired vf2 = new VerifyRuleFired();
        VerifyRuleFired vf3 = new VerifyRuleFired();
        FactData fd = new FactData();
        sc.getFixtures().add(vf1);
        sc.getFixtures().add(vf2);
        sc.getFixtures().add(vf3);
        sc.getGlobals().add(fd);
        sc.removeFixture(vf2);
        Assert.assertEquals(2, sc.getFixtures().size());
        Assert.assertEquals(vf1, sc.getFixtures().get(0));
        Assert.assertEquals(vf3, sc.getFixtures().get(1));
        Assert.assertEquals(1, sc.getGlobals().size());
        sc.removeFixture(fd);
        Assert.assertEquals(0, sc.getGlobals().size());
        Assert.assertEquals(2, sc.getFixtures().size());
    }

    @Test
    public void testMapFactTypes() {
        Scenario sc = new Scenario();
        sc.getFixtures().add(new FactData("X", "q", null, false));
        sc.getGlobals().add(new FactData("Q", "x", null, false));
        Map r = sc.getVariableTypes();
        Assert.assertEquals(2, r.size());
        Assert.assertEquals("X", r.get("q"));
        Assert.assertEquals("Q", r.get("x"));
    }

    @Test
    public void testVariablesInScope() {
        Scenario sc = new Scenario();
        sc.getGlobals().add(new FactData("X", "x", new ArrayList(), false));
        sc.getFixtures().add(new FactData("Q", "q", new ArrayList(), true));
        sc.getFixtures().add(new FactData("Z", "z", new ArrayList(), false));
        ExecutionTrace ex1 = new ExecutionTrace();
        sc.getFixtures().add(ex1);
        sc.getFixtures().add(new RetractFact("z"));
        sc.getFixtures().add(new FactData("Y", "y", new ArrayList(), false));
        ExecutionTrace ex2 = new ExecutionTrace();
        sc.getFixtures().add(ex2);
        List l = sc.getFactNamesInScope(ex1, true);
        Assert.assertEquals(3, l.size());
        Assert.assertEquals("q", l.get(0));
        Assert.assertEquals("z", l.get(1));
        Assert.assertEquals("x", l.get(2));
        l = sc.getFactNamesInScope(ex1, false);
        Assert.assertEquals(2, l.size());
        Assert.assertFalse(l.contains(sc.getGlobals().get(0)));
        l = sc.getFactNamesInScope(ex2, true);
        Assert.assertEquals(3, l.size());
        Assert.assertEquals("q", l.get(0));
        Assert.assertEquals("y", l.get(1));
        Assert.assertEquals("x", l.get(2));
        l = sc.getFactNamesInScope(null, true);
        Assert.assertEquals(0, l.size());
    }

    @Test
    public void testAllowRemoveFact() {
        Scenario sc = new Scenario();
        FactData fd1 = new FactData("X", "x", new ArrayList(), false);
        sc.getFixtures().add(fd1);
        FactData fd2 = new FactData("Q", "q", new ArrayList(), false);
        sc.getFixtures().add(fd2);
        FactData fd3 = new FactData("Z", "z", new ArrayList(), false);
        sc.getFixtures().add(fd3);
        ExecutionTrace ex1 = new ExecutionTrace();
        FactData fd4 = new FactData("I", "i", new ArrayList(), false);
        sc.getGlobals().add(fd4);
        sc.getFixtures().add(ex1);
        sc.getFixtures().add(new RetractFact("z"));
        sc.getFixtures().add(new FactData("Z", "z", new ArrayList(), true));
        sc.getFixtures().add(new VerifyFact("q", new ArrayList()));
        Assert.assertFalse(sc.isFactDataReferenced(fd1));
        Assert.assertTrue(sc.isFactDataReferenced(fd2));
        Assert.assertTrue(sc.isFactDataReferenced(fd3));
        Assert.assertFalse(sc.isFactDataReferenced(fd4));
    }

    @Test
    public void testIsFactNameUsed() {
        Scenario sc = new Scenario();
        sc.getGlobals().add(new FactData("X", "x", null, false));
        sc.getFixtures().add(new FactData("Q", "q", null, false));
        sc.getFixtures().add(new ExecutionTrace());
        Assert.assertTrue(sc.isFactNameReserved("x"));
        Assert.assertTrue(sc.isFactNameReserved("q"));
        Assert.assertFalse(sc.isFactNameReserved("w"));
        sc = new Scenario();
        Assert.assertFalse(sc.isFactNameReserved("w"));
    }

    @Test
    public void testCountSuccessFailures() {
        Scenario sc = new Scenario();
        sc.getFixtures().add(new FactData());
        sc.getFixtures().add(new ExecutionTrace());
        VerifyRuleFired vr = new VerifyRuleFired();
        vr.setSuccessResult(false);
        sc.getFixtures().add(vr);
        VerifyField vf = new VerifyField();
        vf.setSuccessResult(true);
        VerifyField vf2 = new VerifyField();
        vf2.setSuccessResult(false);
        VerifyFact vfact = new VerifyFact();
        vfact.getFieldValues().add(vf);
        vfact.getFieldValues().add(vf2);
        sc.getFixtures().add(vfact);
        int[] totals = sc.countFailuresTotal();
        Assert.assertEquals(2, totals[0]);
        Assert.assertEquals(3, totals[1]);
    }
}

