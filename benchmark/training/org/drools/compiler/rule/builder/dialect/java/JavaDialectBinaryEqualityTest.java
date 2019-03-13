/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.rule.builder.dialect.java;


import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.EvalCondition;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.PredicateConstraint;
import org.drools.core.spi.Constraint;
import org.drools.core.spi.EvalExpression;
import org.drools.core.spi.PredicateExpression;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.definition.KiePackage;


public class JavaDialectBinaryEqualityTest {
    @Test
    public void test1() {
        KiePackage pkg1 = getKnowledgePackage1();
        KiePackage pkg2 = getKnowledgePackage1();
        KiePackage pkg3 = getKnowledgePackage2();
        RuleImpl rule1 = getRule("rule1");
        RuleImpl rule2 = getRule("rule1");
        RuleImpl rule3 = getRule("rule1");
        // test return value
        Pattern p1 = ((Pattern) (rule1.getLhs().getChildren().get(0)));
        Constraint rvc1 = p1.getConstraints().get(0);
        Pattern p2 = ((Pattern) (rule2.getLhs().getChildren().get(0)));
        Constraint rvc2 = p2.getConstraints().get(0);
        Assert.assertNotSame(rvc1, rvc2);
        Assert.assertEquals(rvc1, rvc2);
        Pattern p3 = ((Pattern) (rule3.getLhs().getChildren().get(0)));
        Constraint rvc3 = p3.getConstraints().get(0);
        Assert.assertNotSame(rvc1, rvc3);
        Assert.assertThat(rvc1, CoreMatchers.not(CoreMatchers.equalTo(rvc3)));
        // test inline eval
        PredicateConstraint pc1 = getPredicateConstraint(p1);
        PredicateExpression pe1 = ((PredicateExpression) (pc1.getPredicateExpression()));
        PredicateConstraint pc2 = getPredicateConstraint(p2);
        PredicateExpression pe2 = ((PredicateExpression) (pc2.getPredicateExpression()));
        Assert.assertNotSame(pe1, pe2);
        Assert.assertEquals(pe1, pe2);
        PredicateConstraint pc3 = getPredicateConstraint(p3);
        PredicateExpression pe3 = ((PredicateExpression) (pc3.getPredicateExpression()));
        Assert.assertNotSame(pe1, pe3);
        Assert.assertThat(pe1, CoreMatchers.not(CoreMatchers.equalTo(pe3)));
        // test eval
        EvalCondition ec1 = ((EvalCondition) (rule1.getLhs().getChildren().get(1)));
        EvalExpression ee1 = ((EvalExpression) (ec1.getEvalExpression()));
        EvalCondition ec2 = ((EvalCondition) (rule2.getLhs().getChildren().get(1)));
        EvalExpression ee2 = ((EvalExpression) (ec2.getEvalExpression()));
        Assert.assertNotSame(ee1, ee2);
        Assert.assertEquals(ee1, ee2);
        EvalCondition ec3 = ((EvalCondition) (rule3.getLhs().getChildren().get(1)));
        EvalExpression ee3 = ((EvalExpression) (ec3.getEvalExpression()));
        Assert.assertNotSame(ee1, ee3);
        Assert.assertThat(ee1, CoreMatchers.not(CoreMatchers.equalTo(ee3)));
        // test consequence
        Assert.assertNotSame(rule1.getConsequence(), rule2.getConsequence());
        Assert.assertEquals(rule1.getConsequence(), rule2.getConsequence());
        Assert.assertNotSame(rule1.getConsequence(), rule3.getConsequence());
        Assert.assertThat(rule1.getConsequence(), CoreMatchers.not(CoreMatchers.equalTo(rule3.getConsequence())));
        // check LHS equals
        Assert.assertNotSame(rule1.getLhs(), rule2.getLhs());
        Assert.assertEquals(rule1.getLhs(), rule2.getLhs());
        Assert.assertNotSame(rule1.getLhs(), rule3.getLhs());
        Assert.assertThat(rule1.getLhs(), CoreMatchers.not(CoreMatchers.equalTo(rule3.getLhs())));
    }
}

