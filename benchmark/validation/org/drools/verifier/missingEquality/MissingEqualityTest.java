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
package org.drools.verifier.missingEquality;


import Severity.WARNING;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.drools.core.base.RuleNameMatchesAgendaFilter;
import org.drools.verifier.TestBaseOld;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.data.VerifierReportFactory;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;


public class MissingEqualityTest extends TestBaseOld {
    @Test
    public void testMissingEqualityInLiteralRestrictions() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("MissingEquality.drl"));
        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass().getResourceAsStream("MissingEqualityTest.drl"), result.getVerifierData());
        session.setGlobal("result", result);
        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Missing restriction in LiteralRestrictions"));
        Iterator<VerifierMessageBase> iter = result.getBySeverity(WARNING).iterator();
        Collection<String> ruleNames = new ArrayList<String>();
        while (iter.hasNext()) {
            Object o = ((Object) (iter.next()));
            if (o instanceof VerifierMessage) {
                Cause cause = getFaulty();
                String name = getRuleName();
                ruleNames.add(name);
            }
        } 
        Assert.assertTrue(ruleNames.remove("Missing equality 1"));
        Assert.assertTrue(ruleNames.remove("Missing equality 2"));
        if (!(ruleNames.isEmpty())) {
            for (String string : ruleNames) {
                Assert.fail((("Rule " + string) + " caused an error."));
            }
        }
    }

    @Test
    public void testMissingEqualityInVariableRestrictions() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("MissingEquality.drl"));
        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass().getResourceAsStream("MissingEqualityTest.drl"), result.getVerifierData());
        session.setGlobal("result", result);
        // for (Object o : testData) {
        // if (o instanceof VariableRestriction) {
        // System.out.println(o);
        // VariableRestriction variableRestriction = (VariableRestriction) o;
        // System.out.println(variableRestriction.getOperator());
        // }
        // }
        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Missing restriction in VariableRestrictions, equal operator"));
        Iterator<VerifierMessageBase> iter = result.getBySeverity(WARNING).iterator();
        Set<String> ruleNames = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = ((Object) (iter.next()));
            if (o instanceof VerifierMessage) {
                Cause cause = getFaulty();
                String name = getRuleName();
                ruleNames.add(name);
            }
        } 
        Assert.assertTrue(ruleNames.remove("Missing equality 5"));
        if (!(ruleNames.isEmpty())) {
            for (String string : ruleNames) {
                Assert.fail((("Rule " + string) + " caused an error."));
            }
        }
    }

    @Test
    public void testMissingEqualityInVariableRestrictions2() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("MissingEquality.drl"));
        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass().getResourceAsStream("MissingEqualityTest.drl"), result.getVerifierData());
        session.setGlobal("result", result);
        // for (Object o : testData) {
        // if (o instanceof VariableRestriction) {
        // System.out.println(o);
        // VariableRestriction variableRestriction = (VariableRestriction) o;
        // System.out.println(variableRestriction.getOperator());
        // }
        // }
        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Missing restriction in VariableRestrictions, unequal operator"));
        Iterator<VerifierMessageBase> iter = result.getBySeverity(WARNING).iterator();
        Set<String> ruleNames = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = ((Object) (iter.next()));
            if (o instanceof VerifierMessage) {
                Cause cause = getFaulty();
                String name = getRuleName();
                ruleNames.add(name);
            }
        } 
        Assert.assertTrue(ruleNames.remove("Missing equality 7"));
        if (!(ruleNames.isEmpty())) {
            for (String string : ruleNames) {
                Assert.fail((("Rule " + string) + " caused an error."));
            }
        }
    }

    @Test
    public void testMissingEqualityInVariableRestrictions3() throws Exception {
        KieSession session = getStatelessKieSession(this.getClass().getResourceAsStream("MissingEquality.drl"));
        VerifierReport result = VerifierReportFactory.newVerifierReport();
        Collection<? extends Object> testData = getTestData(this.getClass().getResourceAsStream("MissingEqualityTest.drl"), result.getVerifierData());
        session.setGlobal("result", result);
        // for (Object o : testData) {
        // if (o instanceof VariableRestriction) {
        // System.out.println(o);
        // VariableRestriction variableRestriction = (VariableRestriction) o;
        // System.out.println(variableRestriction.getOperator());
        // }
        // }
        for (Object o : testData) {
            session.insert(o);
        }
        session.fireAllRules(new RuleNameMatchesAgendaFilter("Missing restriction in VariableRestrictions, custom operator"));
        Iterator<VerifierMessageBase> iter = result.getBySeverity(WARNING).iterator();
        Set<String> ruleNames = new HashSet<String>();
        while (iter.hasNext()) {
            Object o = ((Object) (iter.next()));
            if (o instanceof VerifierMessage) {
                Cause cause = getFaulty();
                String name = getRuleName();
                ruleNames.add(name);
            }
        } 
        Assert.assertTrue(ruleNames.remove("Missing equality 3"));
        Assert.assertTrue(ruleNames.remove("Missing equality 4"));
        Assert.assertTrue(ruleNames.remove("Missing equality 6"));
        if (!(ruleNames.isEmpty())) {
            for (String string : ruleNames) {
                Assert.fail((("Rule " + string) + " caused an error."));
            }
        }
    }
}

