/**
 * Copyright 2005 JBoss Inc
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
package org.drools.modelcompiler;


import Index.ConstraintType.EQUAL;
import Index.ConstraintType.GREATER_THAN;
import Index.ConstraintType.NOT_EQUAL;
import java.util.Collection;
import org.drools.core.facttemplates.Fact;
import org.drools.model.DSL;
import org.drools.model.Model;
import org.drools.model.PatternDSL;
import org.drools.model.Prototype;
import org.drools.model.PrototypeVariable;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;


public class FactTemplateTest {
    @Test
    public void testAlpha() {
        Prototype personFact = DSL.prototype("org.drools", "Person", DSL.field("name", Integer.class), DSL.field("age", String.class));
        PrototypeVariable markV = PatternDSL.declarationOf(personFact);
        Rule rule = PatternDSL.rule("alpha").build(PatternDSL.pattern(markV).expr("exprA", ( p) -> p.get("name").equals("Mark"), PatternDSL.alphaIndexedBy(String.class, EQUAL, 1, ( p) -> ((String) (p.get("name"))), "Mark"), PatternDSL.reactOn("name", "age")), PatternDSL.on(markV).execute(( drools, p) -> drools.insert(new Result((("Found a " + (p.get("age"))) + " years old Mark")))));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        Assert.assertTrue(hasFactTemplateObjectType(ksession, "Person"));
        Fact mark = createMapBasedFact(personFact);
        mark.setFieldValue("name", "Mark");
        mark.setFieldValue("age", 40);
        ksession.insert(mark);
        ksession.fireAllRules();
        Collection<Result> results = BaseModelTest.getObjectsIntoList(ksession, Result.class);
        Assert.assertThat(results, CoreMatchers.hasItem(new Result("Found a 40 years old Mark")));
    }

    @Test
    public void testBeta() {
        Result result = new Result();
        Prototype personFact = DSL.prototype("org.drools", "Person", DSL.field("name", Integer.class), DSL.field("age", String.class));
        PrototypeVariable markV = PatternDSL.declarationOf(personFact);
        PrototypeVariable olderV = PatternDSL.declarationOf(personFact);
        Rule rule = PatternDSL.rule("beta").build(PatternDSL.pattern(markV).expr("exprA", ( p) -> p.get("name").equals("Mark"), PatternDSL.alphaIndexedBy(String.class, EQUAL, 1, ( p) -> ((String) (p.get("name"))), "Mark"), PatternDSL.reactOn("name", "age")), PatternDSL.pattern(olderV).expr("exprB", ( p) -> !(p.get("name").equals("Mark")), PatternDSL.alphaIndexedBy(String.class, NOT_EQUAL, 1, ( p) -> ((String) (p.get("name"))), "Mark"), PatternDSL.reactOn("name")).expr("exprC", markV, ( p1, p2) -> ((int) (p1.get("age"))) > ((int) (p2.get("age"))), PatternDSL.betaIndexedBy(int.class, GREATER_THAN, 0, ( p) -> ((int) (p.get("age"))), ( p) -> ((int) (p.get("age")))), PatternDSL.reactOn("age")), PatternDSL.on(olderV, markV).execute(( p1, p2) -> result.setValue((((p1.get("name")) + " is older than ") + (p2.get("name"))))));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        Assert.assertTrue(hasFactTemplateObjectType(ksession, "Person"));
        Fact mark = createMapBasedFact(personFact);
        mark.setFieldValue("name", "Mark");
        mark.setFieldValue("age", 37);
        Fact edson = createMapBasedFact(personFact);
        edson.setFieldValue("name", "Edson");
        edson.setFieldValue("age", 35);
        Fact mario = createMapBasedFact(personFact);
        mario.setFieldValue("name", "Mario");
        mario.setFieldValue("age", 40);
        FactHandle markFH = ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);
        ksession.fireAllRules();
        Assert.assertEquals("Mario is older than Mark", result.getValue());
        result.setValue(null);
        ksession.delete(marioFH);
        ksession.fireAllRules();
        Assert.assertNull(result.getValue());
        mark.setFieldValue("age", 34);
        ksession.update(markFH, mark);
        ksession.fireAllRules();
        Assert.assertEquals("Edson is older than Mark", result.getValue());
    }

    @Test
    public void testBetaMixingClassAndFact() {
        Prototype personFact = DSL.prototype("org.drools", "FactPerson", DSL.field("name", Integer.class), DSL.field("age", String.class));
        PrototypeVariable markV = PatternDSL.declarationOf(personFact);
        Variable<Person> olderV = PatternDSL.declarationOf(Person.class);
        Rule rule = PatternDSL.rule("beta").build(PatternDSL.pattern(markV).expr("exprA", ( p) -> p.get("name").equals("Mark"), PatternDSL.alphaIndexedBy(String.class, EQUAL, 1, ( p) -> ((String) (p.get("name"))), "Mark"), PatternDSL.reactOn("name", "age")), PatternDSL.pattern(olderV).expr("exprB", ( p) -> !(p.getName().equals("Mark")), PatternDSL.alphaIndexedBy(String.class, NOT_EQUAL, 1, ( p) -> p.getName(), "Mark"), PatternDSL.reactOn("name")).expr("exprC", markV, ( p1, p2) -> (p1.getAge()) > ((int) (p2.get("age"))), PatternDSL.betaIndexedBy(int.class, GREATER_THAN, 0, ( p) -> p.getAge(), ( p) -> ((int) (p.get("age")))), PatternDSL.reactOn("age")), PatternDSL.on(olderV, markV).execute(( drools, p1, p2) -> drools.insert(new Result((((p1.getName()) + " is older than ") + (p2.get("name")))))));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        Assert.assertTrue(hasFactTemplateObjectType(ksession, "FactPerson"));
        Fact mark = createMapBasedFact(personFact);
        mark.setFieldValue("name", "Mark");
        mark.setFieldValue("age", 37);
        ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(new Person("Edson", 35));
        FactHandle marioFH = ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();
        Collection<Result> results = BaseModelTest.getObjectsIntoList(ksession, Result.class);
        Assert.assertThat(results, CoreMatchers.hasItem(new Result("Mario is older than Mark")));
    }
}

