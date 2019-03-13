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
package org.drools.verifier.data;


import VerifierComponentType.RULE;
import java.util.Collection;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.verifier.VerifierComponentMockFactory;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.OperatorDescrType;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.components.VerifierRule;
import org.junit.Assert;
import org.junit.Test;


public class VerifierDataMapsTest {
    @Test
    public void testSaveVerifierComponentAndGet() {
        VerifierData data = VerifierReportFactory.newVerifierData();
        VerifierRule rule = VerifierComponentMockFactory.createRule1();
        rule.setName("0");
        String rulePath = rule.getPath();
        data.add(rule);
        Collection<VerifierComponent> all = data.getAll();
        Assert.assertEquals(1, all.size());
        Assert.assertEquals(rule, all.toArray()[0]);
        Collection<VerifierRule> rules = data.getAll(RULE);
        Assert.assertEquals(1, rules.size());
        Assert.assertEquals(rule, rules.toArray()[0]);
        VerifierRule rule2 = data.getVerifierObject(RULE, rulePath);
        Assert.assertNotNull(rule2);
        Assert.assertEquals(rule, rule2);
    }

    @Test
    public void testSaveVerifierComponentAndGetForAllComponentTypes() {
        RulePackage rulePackage = VerifierComponentMockFactory.createPackage1();
        saveVerifierComponentAndGet(rulePackage);
        VerifierRule rule = VerifierComponentMockFactory.createRule1();
        saveVerifierComponentAndGet(rule);
        Pattern pattern = VerifierComponentMockFactory.createPattern1();
        saveVerifierComponentAndGet(pattern);
        saveVerifierComponentAndGet(new org.drools.verifier.components.InlineEvalDescr(pattern));
        saveVerifierComponentAndGet(new ObjectType(new PackageDescr("testPackage1")));
        saveVerifierComponentAndGet(new org.drools.verifier.components.RuleOperatorDescr(new AndDescr(), rule, OperatorDescrType.AND));
        saveVerifierComponentAndGet(new org.drools.verifier.components.PatternOperatorDescr(pattern, OperatorDescrType.AND));
        saveVerifierComponentAndGet(new org.drools.verifier.components.SubPattern(pattern, 0));
        saveVerifierComponentAndGet(new org.drools.verifier.components.ReturnValueFieldDescr(pattern));
        saveVerifierComponentAndGet(new org.drools.verifier.components.SubRule(rule, 0));
        saveVerifierComponentAndGet(new org.drools.verifier.components.TextConsequence(rule));
        saveVerifierComponentAndGet(new org.drools.verifier.components.PatternVariable(rule));
        saveVerifierComponentAndGet(new org.drools.verifier.components.VerifierAccessorDescr(rule));
        saveVerifierComponentAndGet(new org.drools.verifier.components.VerifierAccumulateDescr(pattern));
        saveVerifierComponentAndGet(new org.drools.verifier.components.VerifierCollectDescr(pattern));
        saveVerifierComponentAndGet(new org.drools.verifier.components.RuleEval(rule));
        saveVerifierComponentAndGet(new org.drools.verifier.components.VerifierFieldAccessDescr(rule));
        saveVerifierComponentAndGet(new org.drools.verifier.components.VerifierFromDescr(pattern));
        saveVerifierComponentAndGet(new org.drools.verifier.components.VerifierMethodAccessDescr(rule));
        saveVerifierComponentAndGet(new org.drools.verifier.components.PatternEval(pattern));
    }

    @Test
    public void testSaveVerifierComponentAndGetForAllFields() {
        saveVerifierComponentAndGet(new org.drools.verifier.components.EnumField(new PackageDescr("testPackage1")));
        saveVerifierComponentAndGet(new org.drools.verifier.components.Field(new PackageDescr("testPackage1")));
    }

    @Test
    public void testSaveVerifierComponentAndGetForAllRestrictions() {
        Pattern pattern = VerifierComponentMockFactory.createPattern1();
        saveVerifierComponentAndGet(LiteralRestriction.createRestriction(pattern, ""));
        saveVerifierComponentAndGet(new org.drools.verifier.components.EnumRestriction(pattern));
        saveVerifierComponentAndGet(new org.drools.verifier.components.QualifiedIdentifierRestriction(pattern));
        saveVerifierComponentAndGet(new org.drools.verifier.components.ReturnValueRestriction(pattern));
        saveVerifierComponentAndGet(new org.drools.verifier.components.ReturnValueRestriction(pattern));
        saveVerifierComponentAndGet(new org.drools.verifier.components.VariableRestriction(pattern));
    }

    @Test
    public void testSavePatternAndGet() {
        VerifierData data = VerifierReportFactory.newVerifierData();
        VerifierRule rule = VerifierComponentMockFactory.createRule1();
        Assert.assertNotNull(rule.getName());
        Assert.assertEquals("testRule1", rule.getName());
        ObjectType objectType = new ObjectType(new PackageDescr("testPackage1"));
        Pattern pattern = VerifierComponentMockFactory.createPattern1();
        Assert.assertNotNull(pattern.getRulePath());
        Assert.assertEquals(rule.getPath(), pattern.getRulePath());
        Assert.assertNotNull(pattern.getName());
        Assert.assertEquals(rule.getName(), pattern.getRuleName());
        pattern.setObjectTypePath(objectType.getPath());
        Assert.assertNotNull(pattern.getObjectTypePath());
        Assert.assertEquals(objectType.getPath(), pattern.getObjectTypePath());
        data.add(rule);
        data.add(objectType);
        data.add(pattern);
        Collection<VerifierComponent> all = data.getAll();
        Assert.assertEquals(3, all.size());
        Assert.assertTrue(all.contains(pattern));
        Assert.assertTrue(all.contains(objectType));
        Assert.assertTrue(all.contains(rule));
        Collection<VerifierComponent> components = data.getAll(pattern.getVerifierComponentType());
        Assert.assertEquals(1, components.size());
        Assert.assertEquals(pattern, components.toArray()[0]);
        VerifierComponent objectType2 = data.getVerifierObject(objectType.getVerifierComponentType(), objectType.getPath());
        Assert.assertNotNull(objectType2);
        Assert.assertEquals(objectType, objectType2);
        VerifierComponent rule2 = data.getVerifierObject(rule.getVerifierComponentType(), rule.getPath());
        Assert.assertNotNull(rule2);
        Assert.assertEquals(rule, rule2);
    }
}

