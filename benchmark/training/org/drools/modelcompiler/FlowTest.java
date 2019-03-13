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


import ClockType.PSEUDO_CLOCK;
import ConstraintType.EQUAL;
import ConstraintType.GREATER_THAN;
import ConstraintType.NOT_EQUAL;
import EventProcessingOption.STREAM;
import Rule.Attribute.NO_LOOP;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import org.assertj.core.api.Assertions;
import org.drools.core.base.accumulators.AverageAccumulateFunction;
import org.drools.core.base.accumulators.CollectListAccumulateFunction;
import org.drools.core.base.accumulators.IntegerSumAccumulateFunction;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.model.BitMask;
import org.drools.model.DSL;
import org.drools.model.FlowDSL;
import org.drools.model.Global;
import org.drools.model.Index.ConstraintType.LESS_THAN;
import org.drools.model.Model;
import org.drools.model.Query;
import org.drools.model.Query1Def;
import org.drools.model.Query2Def;
import org.drools.model.QueryDef;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.WindowDefinition.Type.TIME;
import org.drools.model.WindowReference;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.drools.modelcompiler.domain.Address;
import org.drools.modelcompiler.domain.Adult;
import org.drools.modelcompiler.domain.Child;
import org.drools.modelcompiler.domain.Customer;
import org.drools.modelcompiler.domain.Employee;
import org.drools.modelcompiler.domain.Man;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Relationship;
import org.drools.modelcompiler.domain.Result;
import org.drools.modelcompiler.domain.StockTick;
import org.drools.modelcompiler.domain.TargetPolicy;
import org.drools.modelcompiler.domain.Toy;
import org.drools.modelcompiler.domain.Woman;
import org.drools.modelcompiler.oopathdtables.InternationalAddress;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.time.SessionPseudoClock;


public class FlowTest {
    @Test
    public void testBindWithEval() {
        Variable<Person> var_$p1 = FlowDSL.declarationOf(Person.class, "$p1");
        Variable<Integer> var_$a1 = FlowDSL.declarationOf(Integer.class, "$a1");
        Rule rule = FlowDSL.rule("R").build(FlowDSL.bind(var_$a1).as(var_$p1, ( _this) -> _this.getAge()).reactOn("age"), FlowDSL.expr("$expr$2$", var_$a1, ( _this) -> _this > 39), FlowDSL.on(var_$p1).execute(( drools, $p1) -> {
            drools.insert(new Result($p1.getName()));
        }));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Mario", 40));
        ksession.insert(new Person("Mark", 38));
        ksession.insert(new Person("Edson", 35));
        ksession.fireAllRules();
        Collection<Result> results = BaseModelTest.getObjectsIntoList(ksession, Result.class);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("Mario", results.iterator().next().getValue());
    }

    @Test
    public void testBeta() {
        Result result = new Result();
        Variable<Person> markV = FlowDSL.declarationOf(Person.class);
        Variable<Person> olderV = FlowDSL.declarationOf(Person.class);
        Rule rule = // also react on age, see RuleDescr.lookAheadFieldsOfIdentifier
        FlowDSL.rule("beta").build(FlowDSL.expr("exprA", markV, ( p) -> p.getName().equals("Mark")).indexedBy(String.class, EQUAL, 1, Person::getName, "Mark").reactOn("name", "age"), FlowDSL.expr("exprB", olderV, ( p) -> !(p.getName().equals("Mark"))).indexedBy(String.class, NOT_EQUAL, 1, Person::getName, "Mark").reactOn("name"), FlowDSL.expr("exprC", olderV, markV, ( p1, p2) -> (p1.getAge()) > (p2.getAge())).indexedBy(int.class, GREATER_THAN, 0, Person::getAge, Person::getAge).reactOn("age"), FlowDSL.on(olderV, markV).execute(( p1, p2) -> result.setValue((((p1.getName()) + " is older than ") + (p2.getName())))));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);
        FactHandle markFH = ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);
        ksession.fireAllRules();
        Assert.assertEquals("Mario is older than Mark", result.getValue());
        result.setValue(null);
        ksession.delete(marioFH);
        ksession.fireAllRules();
        Assert.assertNull(result.getValue());
        mark.setAge(34);
        ksession.update(markFH, mark, "age");
        ksession.fireAllRules();
        Assert.assertEquals("Edson is older than Mark", result.getValue());
    }

    @Test
    public void testBetaWithDeclaration() {
        Variable<Person> markV = FlowDSL.declarationOf(Person.class);
        Variable<Integer> markAge = FlowDSL.declarationOf(Integer.class);
        Variable<Person> olderV = FlowDSL.declarationOf(Person.class);
        Rule rule = // also react on age, see RuleDescr.lookAheadFieldsOfIdentifier
        FlowDSL.rule("beta").build(FlowDSL.expr("exprA", markV, ( p) -> p.getName().equals("Mark")).indexedBy(String.class, EQUAL, 1, Person::getName, "Mark").reactOn("name"), FlowDSL.bind(markAge).as(markV, Person::getAge).reactOn("age"), FlowDSL.expr("exprB", olderV, ( p) -> !(p.getName().equals("Mark"))).indexedBy(String.class, NOT_EQUAL, 1, Person::getName, "Mark").reactOn("name"), FlowDSL.expr("exprC", olderV, markAge, ( p1, age) -> (p1.getAge()) > age).indexedBy(int.class, GREATER_THAN, 0, Person::getAge, int.class::cast).reactOn("age"), FlowDSL.on(olderV, markV).execute(( drools, p1, p2) -> drools.insert((((p1.getName()) + " is older than ") + (p2.getName())))));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);
        FactHandle markFH = ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);
        ksession.fireAllRules();
        Collection<String> results = BaseModelTest.getObjectsIntoList(ksession, String.class);
        Assertions.assertThat(results).containsExactlyInAnyOrder("Mario is older than Mark");
        ksession.delete(marioFH);
        ksession.fireAllRules();
        mark.setAge(34);
        ksession.update(markFH, mark, "age");
        ksession.fireAllRules();
        results = BaseModelTest.getObjectsIntoList(ksession, String.class);
        Assertions.assertThat(results).containsExactlyInAnyOrder("Mario is older than Mark", "Edson is older than Mark");
    }

    @Test
    public void testBetaWithDeclarationBeforePattern() {
        Variable<Person> markV = FlowDSL.declarationOf(Person.class);
        Variable<Integer> markAge = FlowDSL.declarationOf(Integer.class);
        Variable<Person> olderV = FlowDSL.declarationOf(Person.class);
        Rule rule = // also react on age, see RuleDescr.lookAheadFieldsOfIdentifier
        FlowDSL.rule("beta").build(FlowDSL.bind(markAge).as(markV, Person::getAge).reactOn("age"), FlowDSL.expr("exprA", markV, ( p) -> p.getName().equals("Mark")).indexedBy(String.class, EQUAL, 1, Person::getName, "Mark").reactOn("name"), FlowDSL.expr("exprB", olderV, ( p) -> !(p.getName().equals("Mark"))).indexedBy(String.class, NOT_EQUAL, 1, Person::getName, "Mark").reactOn("name"), FlowDSL.expr("exprC", olderV, markAge, ( p1, age) -> (p1.getAge()) > age).indexedBy(int.class, GREATER_THAN, 0, Person::getAge, int.class::cast).reactOn("age"), FlowDSL.on(olderV, markV).execute(( drools, p1, p2) -> drools.insert((((p1.getName()) + " is older than ") + (p2.getName())))));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);
        FactHandle markFH = ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);
        ksession.fireAllRules();
        Collection<String> results = BaseModelTest.getObjectsIntoList(ksession, String.class);
        Assertions.assertThat(results).containsExactlyInAnyOrder("Mario is older than Mark");
        ksession.delete(marioFH);
        ksession.fireAllRules();
        mark.setAge(34);
        ksession.update(markFH, mark, "age");
        ksession.fireAllRules();
        results = BaseModelTest.getObjectsIntoList(ksession, String.class);
        Assertions.assertThat(results).containsExactlyInAnyOrder("Mario is older than Mark", "Edson is older than Mark");
    }

    @Test
    public void testBetaWithResult() {
        Variable<Person> markV = FlowDSL.declarationOf(Person.class);
        Variable<Person> olderV = FlowDSL.declarationOf(Person.class);
        Variable<Result> resultV = FlowDSL.declarationOf(Result.class);
        Rule rule = // also react on age, see RuleDescr.lookAheadFieldsOfIdentifier
        FlowDSL.rule("beta").build(FlowDSL.expr("exprA", markV, ( p) -> p.getName().equals("Mark")).indexedBy(String.class, EQUAL, 1, Person::getName, "Mark").reactOn("name", "age"), FlowDSL.expr("exprB", olderV, ( p) -> !(p.getName().equals("Mark"))).indexedBy(String.class, NOT_EQUAL, 1, Person::getName, "Mark").reactOn("name"), FlowDSL.expr("exprC", olderV, markV, ( p1, p2) -> (p1.getAge()) > (p2.getAge())).indexedBy(int.class, GREATER_THAN, 0, Person::getAge, Person::getAge).reactOn("age"), FlowDSL.on(olderV, markV, resultV).execute(( p1, p2, r) -> r.setValue((((p1.getName()) + " is older than ") + (p2.getName())))));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        Result result = new Result();
        ksession.insert(result);
        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);
        FactHandle markFH = ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);
        ksession.fireAllRules();
        Assert.assertEquals("Mario is older than Mark", result.getValue());
    }

    @Test
    public void test3Patterns() {
        Result result = new Result();
        Variable<Person> personV = FlowDSL.declarationOf(Person.class);
        Variable<Person> markV = FlowDSL.declarationOf(Person.class);
        Variable<String> nameV = FlowDSL.declarationOf(String.class);
        Rule rule = FlowDSL.rule("myrule").build(FlowDSL.expr("exprA", markV, ( p) -> p.getName().equals("Mark")), FlowDSL.expr("exprB", personV, markV, ( p1, p2) -> (p1.getAge()) > (p2.getAge())), FlowDSL.expr("exprC", nameV, personV, ( s, p) -> s.equals(p.getName())), FlowDSL.on(nameV).execute(result::setValue));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        ksession.insert("Mario");
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();
        Assert.assertEquals("Mario", result.getValue());
    }

    @Test
    public void testOr() {
        Result result = new Result();
        Variable<Person> personV = FlowDSL.declarationOf(Person.class);
        Variable<Person> markV = FlowDSL.declarationOf(Person.class);
        Variable<String> nameV = FlowDSL.declarationOf(String.class);
        Rule rule = FlowDSL.rule("or").build(FlowDSL.or(FlowDSL.expr("exprA", personV, ( p) -> p.getName().equals("Mark")), FlowDSL.and(FlowDSL.expr("exprA", markV, ( p) -> p.getName().equals("Mark")), FlowDSL.expr("exprB", personV, markV, ( p1, p2) -> (p1.getAge()) > (p2.getAge())))), FlowDSL.expr("exprC", nameV, personV, ( s, p) -> s.equals(p.getName())), FlowDSL.on(nameV).execute(result::setValue));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        ksession.insert("Mario");
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();
        Assert.assertEquals("Mario", result.getValue());
    }

    @Test
    public void testNot() {
        Result result = new Result();
        Variable<Person> oldestV = FlowDSL.declarationOf(Person.class);
        Variable<Person> otherV = FlowDSL.declarationOf(Person.class);
        Rule rule = FlowDSL.rule("not").build(FlowDSL.not(otherV, oldestV, ( p1, p2) -> (p1.getAge()) > (p2.getAge())), FlowDSL.on(oldestV).execute(( p) -> result.setValue(("Oldest person is " + (p.getName())))));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();
        Assert.assertEquals("Oldest person is Mario", result.getValue());
    }

    @Test
    public void testForall() {
        Variable<Person> p1V = FlowDSL.declarationOf(Person.class);
        Variable<Person> p2V = FlowDSL.declarationOf(Person.class);
        Rule rule = FlowDSL.rule("not").build(FlowDSL.forall(FlowDSL.expr("exprA", p1V, ( p) -> (p.getName().length()) == 5), FlowDSL.expr("exprB", p2V, p1V, ( p2, p1) -> p2 == p1), FlowDSL.expr("exprC", p2V, ( p) -> (p.getAge()) > 40)), FlowDSL.execute(( drools) -> drools.insert(new Result("ok"))));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Mario", 41));
        ksession.insert(new Person("Mark", 39));
        ksession.insert(new Person("Edson", 42));
        ksession.fireAllRules();
        Collection<Result> results = BaseModelTest.getObjectsIntoList(ksession, Result.class);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("ok", results.iterator().next().getValue());
    }

    @Test
    public void testAccumulate1() {
        Result result = new Result();
        Variable<Person> person = FlowDSL.declarationOf(Person.class);
        Variable<Integer> resultSum = FlowDSL.declarationOf(Integer.class);
        Variable<Integer> age = FlowDSL.declarationOf(Integer.class);
        Rule rule = FlowDSL.rule("accumulate").build(FlowDSL.bind(age).as(person, Person::getAge), FlowDSL.accumulate(FlowDSL.expr(person, ( p) -> p.getName().startsWith("M")), FlowDSL.accFunction(IntegerSumAccumulateFunction.class, age).as(resultSum)), FlowDSL.on(resultSum).execute(( sum) -> result.setValue(("total = " + sum))));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();
        Assert.assertEquals("total = 77", result.getValue());
    }

    @Test
    public void testAccumulate2() {
        Result result = new Result();
        Variable<Person> person = FlowDSL.declarationOf(Person.class);
        Variable<Integer> resultSum = FlowDSL.declarationOf(Integer.class);
        Variable<Double> resultAvg = FlowDSL.declarationOf(Double.class);
        Variable<Integer> age = FlowDSL.declarationOf(Integer.class);
        Rule rule = FlowDSL.rule("accumulate").build(FlowDSL.bind(age).as(person, Person::getAge), FlowDSL.accumulate(FlowDSL.expr(person, ( p) -> p.getName().startsWith("M")), FlowDSL.accFunction(IntegerSumAccumulateFunction.class, age).as(resultSum), FlowDSL.accFunction(AverageAccumulateFunction.class, age).as(resultAvg)), FlowDSL.on(resultSum, resultAvg).execute(( sum, avg) -> result.setValue(((("total = " + sum) + "; average = ") + avg))));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();
        Assert.assertEquals("total = 77; average = 38.5", result.getValue());
    }

    @Test
    public void testGlobalInConsequence() {
        Variable<Person> markV = FlowDSL.declarationOf(Person.class);
        Global<Result> resultG = FlowDSL.globalOf(Result.class, "org.mypkg");
        Rule rule = FlowDSL.rule("org.mypkg", "global").build(FlowDSL.expr("exprA", markV, ( p) -> p.getName().equals("Mark")).indexedBy(String.class, EQUAL, 0, Person::getName, "Mark").reactOn("name"), FlowDSL.on(markV, resultG).execute(( p, r) -> r.setValue((((p.getName()) + " is ") + (p.getAge())))));
        Model model = new ModelImpl().addRule(rule).addGlobal(resultG);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        Result result = new Result();
        ksession.setGlobal(resultG.getName(), result);
        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);
        ksession.insert(mark);
        ksession.insert(edson);
        ksession.insert(mario);
        ksession.fireAllRules();
        Assert.assertEquals("Mark is 37", result.getValue());
    }

    @Test
    public void testGlobalInConstraint() {
        Variable<Person> markV = FlowDSL.declarationOf(Person.class);
        Global<Result> resultG = FlowDSL.globalOf(Result.class, "org.mypkg");
        Global<String> nameG = FlowDSL.globalOf(String.class, "org.mypkg");
        Rule rule = FlowDSL.rule("org.mypkg", "global").build(FlowDSL.expr("exprA", markV, nameG, ( p, n) -> p.getName().equals(n)).reactOn("name"), FlowDSL.on(markV, resultG).execute(( p, r) -> r.setValue((((p.getName()) + " is ") + (p.getAge())))));
        Model model = new ModelImpl().addRule(rule).addGlobal(nameG).addGlobal(resultG);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        ksession.setGlobal(nameG.getName(), "Mark");
        Result result = new Result();
        ksession.setGlobal(resultG.getName(), result);
        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);
        ksession.insert(mark);
        ksession.insert(edson);
        ksession.insert(mario);
        ksession.fireAllRules();
        Assert.assertEquals("Mark is 37", result.getValue());
    }

    @Test
    public void testNotEmptyPredicate() {
        Rule rule = FlowDSL.rule("R").build(FlowDSL.not(FlowDSL.input(FlowDSL.declarationOf(Person.class))), FlowDSL.execute(( drools) -> drools.insert(new Result("ok"))));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        Person mario = new Person("Mario", 40);
        ksession.insert(mario);
        ksession.fireAllRules();
        Assert.assertTrue(ksession.getObjects(new ClassObjectFilter(Result.class)).isEmpty());
    }

    @Test
    public void testQuery() {
        Variable<Person> personV = FlowDSL.declarationOf(Person.class, "$p");
        Query1Def<Integer> qdef = FlowDSL.query("olderThan", Integer.class);
        Query query = qdef.build(FlowDSL.expr("exprA", personV, qdef.getArg1(), ( p, a) -> (p.getAge()) > a));
        Model model = new ModelImpl().addQuery(query);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Mark", 39));
        ksession.insert(new Person("Mario", 41));
        QueryResults results = ksession.getQueryResults("olderThan", 40);
        Assert.assertEquals(1, results.size());
        Person p = ((Person) (results.iterator().next().get("$p")));
        Assert.assertEquals("Mario", p.getName());
    }

    @Test
    public void testQueryWithNamedArg() {
        Variable<Person> personV = FlowDSL.declarationOf(Person.class, "$p");
        Query1Def<Integer> qdef = FlowDSL.query("olderThan", Integer.class, "ageArg");
        Query query = qdef.build(FlowDSL.expr("exprA", personV, qdef.getArg("ageArg", Integer.class), ( p, a) -> (p.getAge()) > a));
        Model model = new ModelImpl().addQuery(query);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Mark", 39));
        ksession.insert(new Person("Mario", 41));
        QueryResults results = ksession.getQueryResults("olderThan", 40);
        Assert.assertEquals(1, results.size());
        Person p = ((Person) (results.iterator().next().get("$p")));
        Assert.assertEquals("Mario", p.getName());
    }

    @Test
    public void testQueryInRule() {
        Variable<Person> personV = FlowDSL.declarationOf(Person.class);
        Query2Def<Person, Integer> qdef = FlowDSL.query("olderThan", Person.class, Integer.class);
        Query query = qdef.build(FlowDSL.expr("exprA", qdef.getArg1(), qdef.getArg2(), ( p, a) -> (p.getAge()) > a));
        Variable<Person> personVRule = FlowDSL.declarationOf(Person.class);
        Rule rule = FlowDSL.rule("R").build(qdef.call(personVRule, FlowDSL.valueOf(40)), FlowDSL.on(personVRule).execute(( drools, p) -> drools.insert(new Result(p.getName()))));
        Model model = new ModelImpl().addQuery(query).addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Mark", 39));
        ksession.insert(new Person("Mario", 41));
        ksession.fireAllRules();
        Collection<Result> results = ((Collection<Result>) (ksession.getObjects(new ClassObjectFilter(Result.class))));
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("Mario", results.iterator().next().getValue());
    }

    @Test
    public void testQueryInvokingQuery() {
        Variable<Relationship> relV = FlowDSL.declarationOf(Relationship.class);
        Query2Def<String, String> query1Def = FlowDSL.query("isRelatedTo1", String.class, String.class);
        Query2Def<String, String> query2Def = FlowDSL.query("isRelatedTo2", String.class, String.class);
        Query query2 = query2Def.build(FlowDSL.expr("exprA", relV, query2Def.getArg1(), ( r, s) -> r.getStart().equals(s)), FlowDSL.expr("exprB", relV, query2Def.getArg2(), ( r, e) -> r.getEnd().equals(e)));
        Query query1 = query1Def.build(query2Def.call(query1Def.getArg1(), query1Def.getArg2()));
        Model model = new ModelImpl().addQuery(query2).addQuery(query1);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Relationship("A", "B"));
        ksession.insert(new Relationship("B", "C"));
        QueryResults results = ksession.getQueryResults("isRelatedTo1", "A", "B");
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("B", results.iterator().next().get(query1Def.getArg2().getName()));
    }

    @Test
    public void testPositionalRecursiveQueryWithUnification() {
        Variable<Relationship> var_$pattern_Relationship$1$ = FlowDSL.declarationOf(Relationship.class);
        Variable<Relationship> var_$pattern_Relationship$2$ = FlowDSL.declarationOf(Relationship.class);
        Variable<String> var_$unificationExpr$1$ = FlowDSL.declarationOf(String.class);
        Query2Def<String, String> queryDef_isRelatedTo = FlowDSL.query("isRelatedTo", String.class, String.class);
        Query query = queryDef_isRelatedTo.build(FlowDSL.or(FlowDSL.and(FlowDSL.expr("exprA", var_$pattern_Relationship$1$, queryDef_isRelatedTo.getArg1(), ( r, s) -> r.getStart().equals(s)), FlowDSL.expr("exprB", var_$pattern_Relationship$1$, queryDef_isRelatedTo.getArg2(), ( r, e) -> r.getEnd().equals(e))), FlowDSL.and(FlowDSL.and(FlowDSL.bind(var_$unificationExpr$1$).as(var_$pattern_Relationship$2$, ( relationship) -> relationship.getStart()), FlowDSL.expr("exprD", var_$pattern_Relationship$2$, queryDef_isRelatedTo.getArg2(), ( r, e) -> r.getEnd().equals(e))), queryDef_isRelatedTo.call(queryDef_isRelatedTo.getArg1(), var_$unificationExpr$1$))));
        Model model = new ModelImpl().addQuery(query);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Relationship("A", "B"));
        ksession.insert(new Relationship("B", "C"));
        QueryResults results = ksession.getQueryResults("isRelatedTo", "A", "C");
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("B", results.iterator().next().get(var_$unificationExpr$1$.getName()));
    }

    @Test
    public void testQueryInRuleWithDeclaration() {
        Variable<Person> personV = FlowDSL.declarationOf(Person.class);
        Query2Def<Person, Integer> qdef = FlowDSL.query("olderThan", Person.class, Integer.class);
        Query query = qdef.build(FlowDSL.expr("exprA", qdef.getArg1(), qdef.getArg2(), ( p, a) -> (p.getAge()) > a));
        Rule rule = FlowDSL.rule("R").build(FlowDSL.expr("exprB", personV, ( p) -> p.getName().startsWith("M")), qdef.call(personV, FlowDSL.valueOf(40)), FlowDSL.on(personV).execute(( drools, p) -> drools.insert(new Result(p.getName()))));
        Model model = new ModelImpl().addQuery(query).addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Mark", 39));
        ksession.insert(new Person("Mario", 41));
        ksession.insert(new Person("Edson", 41));
        ksession.fireAllRules();
        Collection<Result> results = ((Collection<Result>) (ksession.getObjects(new ClassObjectFilter(Result.class))));
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("Mario", results.iterator().next().getValue());
    }

    @Test
    public void testQueryInvokedWithGlobal() {
        Global<Integer> ageG = FlowDSL.globalOf(Integer.class, "defaultpkg", "ageG");
        Variable<Person> personV = FlowDSL.declarationOf(Person.class);
        Query2Def<Person, Integer> qdef = FlowDSL.query("olderThan", Person.class, Integer.class);
        Query query = qdef.build(FlowDSL.expr("exprA", qdef.getArg1(), qdef.getArg2(), ( _this, $age) -> (_this.getAge()) > $age));
        Rule rule = FlowDSL.rule("R").build(FlowDSL.expr("exprB", personV, ( p) -> p.getName().startsWith("M")), qdef.call(personV, ageG), FlowDSL.on(personV).execute(( drools, p) -> drools.insert(new Result(p.getName()))));
        Model model = new ModelImpl().addGlobal(ageG).addQuery(query).addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        ksession.setGlobal("ageG", 40);
        ksession.insert(new Person("Mark", 39));
        ksession.insert(new Person("Mario", 41));
        ksession.insert(new Person("Edson", 41));
        ksession.fireAllRules();
        Collection<Result> results = BaseModelTest.getObjectsIntoList(ksession, Result.class);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("Mario", results.iterator().next().getValue());
    }

    @Test
    public void testNamedConsequence() {
        Variable<Result> resultV = FlowDSL.declarationOf(Result.class);
        Variable<Person> markV = FlowDSL.declarationOf(Person.class);
        Variable<Person> olderV = FlowDSL.declarationOf(Person.class);
        Rule rule = // also react on age, see RuleDescr.lookAheadFieldsOfIdentifier
        FlowDSL.rule("beta").build(FlowDSL.expr("exprA", markV, ( p) -> p.getName().equals("Mark")).indexedBy(String.class, EQUAL, 1, Person::getName, "Mark").reactOn("name", "age"), FlowDSL.on(markV, resultV).execute(( p, r) -> r.addValue(("Found " + (p.getName())))), FlowDSL.expr("exprB", olderV, ( p) -> !(p.getName().equals("Mark"))).indexedBy(String.class, NOT_EQUAL, 1, Person::getName, "Mark").reactOn("name"), FlowDSL.expr("exprC", olderV, markV, ( p1, p2) -> (p1.getAge()) > (p2.getAge())).indexedBy(int.class, GREATER_THAN, 0, Person::getAge, Person::getAge).reactOn("age"), FlowDSL.on(olderV, markV, resultV).execute(( p1, p2, r) -> r.addValue((((p1.getName()) + " is older than ") + (p2.getName())))));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        Result result = new Result();
        ksession.insert(result);
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();
        Collection<String> results = ((Collection<String>) (result.getValue()));
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.containsAll(Arrays.asList("Found Mark", "Mario is older than Mark")));
    }

    @Test
    public void testBreakingNamedConsequence() {
        Variable<Result> resultV = FlowDSL.declarationOf(Result.class);
        Variable<Person> markV = FlowDSL.declarationOf(Person.class);
        Variable<Person> olderV = FlowDSL.declarationOf(Person.class);
        Rule rule = FlowDSL.rule("beta").build(FlowDSL.expr("exprA", markV, ( p) -> p.getName().equals("Mark")).indexedBy(String.class, EQUAL, 1, Person::getName, "Mark").reactOn("name", "age"), FlowDSL.when("cond1", markV, ( p) -> (p.getAge()) < 30).then(FlowDSL.on(markV, resultV).breaking().execute(( p, r) -> r.addValue(("Found young " + (p.getName()))))).elseWhen("cond2", markV, ( p) -> (p.getAge()) > 50).then(FlowDSL.on(markV, resultV).breaking().execute(( p, r) -> r.addValue(("Found old " + (p.getName()))))).elseWhen().then(FlowDSL.on(markV, resultV).breaking().execute(( p, r) -> r.addValue(("Found " + (p.getName()))))), FlowDSL.expr("exprB", olderV, ( p) -> !(p.getName().equals("Mark"))).indexedBy(String.class, NOT_EQUAL, 1, Person::getName, "Mark").reactOn("name"), FlowDSL.expr("exprC", olderV, markV, ( p1, p2) -> (p1.getAge()) > (p2.getAge())).indexedBy(int.class, GREATER_THAN, 0, Person::getAge, Person::getAge).reactOn("age"), FlowDSL.on(olderV, markV, resultV).execute(( p1, p2, r) -> r.addValue((((p1.getName()) + " is older than ") + (p2.getName())))));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        Result result = new Result();
        ksession.insert(result);
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();
        Collection<String> results = ((Collection<String>) (result.getValue()));
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("Found Mark", results.iterator().next());
    }

    @Test
    public void testFrom() throws Exception {
        Variable<Result> resultV = FlowDSL.declarationOf(Result.class);
        Variable<Adult> dadV = FlowDSL.declarationOf(Adult.class);
        Variable<Child> childV = FlowDSL.declarationOf(Child.class, FlowDSL.from(dadV, ( adult) -> adult.getChildren()));
        Rule rule = FlowDSL.rule("from").build(FlowDSL.expr("exprA", childV, ( c) -> (c.getAge()) > 8), FlowDSL.on(childV, resultV).execute(( c, r) -> r.setValue(c.getName())));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        Result result = new Result();
        ksession.insert(result);
        Adult dad = new Adult("dad", 40);
        dad.addChild(new Child("Alan", 10));
        dad.addChild(new Child("Betty", 7));
        ksession.insert(dad);
        ksession.fireAllRules();
        Assert.assertEquals("Alan", result.getValue());
    }

    @Test
    public void testFromGlobal() throws Exception {
        // global java.util.List list
        // rule R when
        // $o : String(length > 3) from list
        // then
        // insert($o);
        // end
        final Global<List> var_list = FlowDSL.globalOf(List.class, "defaultpkg", "list");
        final Variable<String> var_$o = FlowDSL.declarationOf(String.class, "$o", FlowDSL.from(var_list, ( x) -> x));// cannot use Function.identity() here because target type is ?.

        Rule rule = FlowDSL.rule("R").build(FlowDSL.expr("$expr$1$", var_$o, ( _this) -> (_this.length()) > 3).indexedBy(int.class, org.drools.model.Index.ConstraintType.GREATER_THAN, 0, ( _this) -> _this.length(), 3).reactOn("length"), FlowDSL.on(var_$o).execute(( drools, $o) -> {
            drools.insert($o);
        }));
        Model model = new ModelImpl().addGlobal(var_list).addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        List<String> messages = Arrays.asList("a", "Hello World!", "b");
        ksession.setGlobal("list", messages);
        ksession.fireAllRules();
        List<String> results = BaseModelTest.getObjectsIntoList(ksession, String.class);
        Assert.assertFalse(results.contains("a"));
        Assert.assertTrue(results.contains("Hello World!"));
        Assert.assertFalse(results.contains("b"));
    }

    @Test
    public void testConcatenatedFrom() {
        Global<List> listG = FlowDSL.globalOf(List.class, "defaultpkg", "list");
        Variable<Man> manV = FlowDSL.declarationOf(Man.class);
        Variable<Woman> wifeV = FlowDSL.declarationOf(Woman.class, FlowDSL.from(manV, Man::getWife));
        Variable<Child> childV = FlowDSL.declarationOf(Child.class, FlowDSL.from(wifeV, Woman::getChildren));
        Variable<Toy> toyV = FlowDSL.declarationOf(Toy.class, FlowDSL.from(childV, Child::getToys));
        Rule rule = FlowDSL.rule("froms").build(FlowDSL.expr("exprA", childV, ( c) -> (c.getAge()) > 10), FlowDSL.on(toyV, listG).execute(( t, l) -> l.add(t.getName())));
        Model model = new ModelImpl().addGlobal(listG).addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        final Woman alice = new Woman("Alice", 38);
        final Man bob = new Man("Bob", 40);
        bob.setWife(alice);
        final Child charlie = new Child("Charles", 12);
        final Child debbie = new Child("Debbie", 10);
        alice.addChild(charlie);
        alice.addChild(debbie);
        charlie.addToy(new Toy("car"));
        charlie.addToy(new Toy("ball"));
        debbie.addToy(new Toy("doll"));
        ksession.insert(bob);
        ksession.fireAllRules();
        Assertions.assertThat(list).containsExactlyInAnyOrder("car", "ball");
    }

    @Test(timeout = 5000)
    public void testNoLoopWithModel() {
        Variable<Person> meV = FlowDSL.declarationOf(Person.class);
        Rule rule = FlowDSL.rule("noloop").attribute(NO_LOOP, true).build(FlowDSL.expr("exprA", meV, ( p) -> (p.getAge()) > 18).reactOn("age"), FlowDSL.on(meV).execute(( drools, p) -> {
            p.setAge(((p.getAge()) + 1));
            drools.update(p, "age");
        }));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        Person me = new Person("Mario", 40);
        ksession.insert(me);
        ksession.fireAllRules();
        Assert.assertEquals(41, me.getAge());
    }

    @Test
    public void testMetadataBasics() {
        final String PACKAGE_NAME = "org.asd";
        final String RULE_NAME = "hello world";
        final String RULE_KEY = "output";
        final String RULE_VALUE = "Hello world!";
        Rule rule = // equivalent of DRL form:  @output("\"Hello world!\"")
        FlowDSL.rule(PACKAGE_NAME, RULE_NAME).metadata(RULE_KEY, (("\"" + RULE_VALUE) + "\"")).build(FlowDSL.execute(() -> {
            System.out.println("Hello world!");
        }));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        final Map<String, Object> metadata = ksession.getKieBase().getRule(PACKAGE_NAME, RULE_NAME).getMetaData();
        Assertions.assertThat(metadata.containsKey(RULE_KEY)).isTrue();
        Assertions.assertThat(metadata.get(RULE_KEY)).isEqualTo((("\"" + RULE_VALUE) + "\""));// testing of the DRL form:  @output("\"Hello world!\"")

    }

    @Test
    public void testDeclaredSlidingWindow() {
        WindowReference var_DeclaredWindow = FlowDSL.window(TIME, 5, TimeUnit.SECONDS, StockTick.class, ( _this) -> _this.getCompany().equals("DROO"));
        final Variable<StockTick> var_$a = FlowDSL.declarationOf(StockTick.class, "$a", var_DeclaredWindow);
        Rule rule = FlowDSL.rule("R").build(FlowDSL.on(var_$a).execute(( $a) -> {
            System.out.println($a.getCompany());
        }));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model, STREAM);
        KieSessionConfiguration sessionConfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        sessionConfig.setOption(ClockTypeOption.get(PSEUDO_CLOCK.getId()));
        KieSession ksession = kieBase.newKieSession(sessionConfig, null);
        SessionPseudoClock clock = ksession.getSessionClock();
        clock.advanceTime(2, TimeUnit.SECONDS);
        ksession.insert(new StockTick("DROO"));
        clock.advanceTime(2, TimeUnit.SECONDS);
        ksession.insert(new StockTick("DROO"));
        clock.advanceTime(2, TimeUnit.SECONDS);
        ksession.insert(new StockTick("ACME"));
        clock.advanceTime(2, TimeUnit.SECONDS);
        ksession.insert(new StockTick("DROO"));
        Assert.assertEquals(2, ksession.fireAllRules());
    }

    @Test
    public void testMVELinsert() {
        final Variable<Integer> var_$pattern_Integer$1$ = FlowDSL.declarationOf(Integer.class, "$pattern_Integer$1$");
        Rule rule = FlowDSL.rule("R").build(FlowDSL.input(var_$pattern_Integer$1$), FlowDSL.executeScript("mvel", null, ("System.out.println(\"Hello World\");\n" + "drools.insert(\"Hello World\");")));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        FactHandle fh_47 = ksession.insert(47);
        ksession.fireAllRules();
        Collection<String> results = BaseModelTest.getObjectsIntoList(ksession, String.class);
        Assert.assertTrue(results.contains("Hello World"));
    }

    @Test
    public void testMVELmodify() {
        final Variable<Person> var_$p = FlowDSL.declarationOf(Person.class, "$p");
        final BitMask mask_$p = org.drools.model.BitMask.getPatternMask(Person.class, "age");
        Rule rule = FlowDSL.rule("R").build(FlowDSL.input(var_$p), FlowDSL.on(var_$p).executeScript("mvel", null, "System.out.println($p); modify($p) { setAge(1); } System.out.println($p);"));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Matteo", 47));
        ksession.fireAllRules();
        List<Person> results = BaseModelTest.getObjectsIntoList(ksession, Person.class);
        Assert.assertEquals(1, results.get(0).getAge());
    }

    @Test
    public void testAccumuluateWithAnd2() {
        Variable<Object> var_$pattern_Object$1$ = FlowDSL.declarationOf(Object.class, "$pattern_Object$1$");
        Variable<Child> var_$c = FlowDSL.declarationOf(Child.class, "$c");
        Variable<Adult> var_$a = FlowDSL.declarationOf(Adult.class, "$a");
        Variable<Integer> var_$parentAge = FlowDSL.declarationOf(Integer.class, "$parentAge");
        Variable<Integer> var_$expr$5$ = FlowDSL.declarationOf(Integer.class, "$expr$5$");
        Rule rule = FlowDSL.rule("R").build(FlowDSL.bind(var_$expr$5$).as(var_$a, var_$c, ( $a, $c) -> ($a.getAge()) + ($c.getAge())), FlowDSL.accumulate(FlowDSL.and(FlowDSL.expr("$expr$1$", var_$c, ( _this) -> (_this.getAge()) < 10).indexedBy(int.class, LESS_THAN, 0, ( _this) -> _this.getAge(), 10).reactOn("age"), FlowDSL.expr("$expr$2$", var_$a, var_$c, ( _this, $c) -> _this.getName().equals($c.getParent())).reactOn("name")), FlowDSL.accFunction(IntegerSumAccumulateFunction.class, var_$expr$5$).as(var_$parentAge)), FlowDSL.on(var_$parentAge).execute(( drools, $parentAge) -> {
            drools.insert(new Result($parentAge));
        }));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        Adult a = new Adult("Mario", 43);
        Child c = new Child("Sofia", 6, "Mario");
        ksession.insert(a);
        ksession.insert(c);
        ksession.fireAllRules();
        Collection<Result> results = BaseModelTest.getObjectsIntoList(ksession, Result.class);
        Assert.assertThat(results, CoreMatchers.hasItem(new Result(49)));
    }

    @Test
    public void testQueryOOPathAccumulateTransformed() {
        final QueryDef queryDef_listSafeCities = FlowDSL.query("listSafeCities");
        final Variable<org.drools.modelcompiler.oopathdtables.Person> var_$p = FlowDSL.declarationOf(org.drools.modelcompiler.oopathdtables.Person.class, "$p");
        final Variable<InternationalAddress> var_$a = FlowDSL.declarationOf(InternationalAddress.class, "$a", FlowDSL.from(var_$p, ( _this) -> _this.getAddress()));
        final Variable<List> var_$cities = FlowDSL.declarationOf(List.class, "$cities");
        final Variable<String> var_$city = FlowDSL.declarationOf(String.class, "$city", FlowDSL.from(var_$a, ( _this) -> _this.getCity()));
        Query listSafeCities_build = queryDef_listSafeCities.build(FlowDSL.input(var_$p), FlowDSL.expr("$expr$2$", var_$a, ( _this) -> _this.getState().equals("Safecountry")).indexedBy(String.class, org.drools.model.Index.ConstraintType.EQUAL, 0, ( _this) -> _this.getState(), "Safecountry").reactOn("state"), FlowDSL.accumulate(FlowDSL.expr(var_$city, ( s) -> true), FlowDSL.accFunction(CollectListAccumulateFunction.class, var_$city).as(var_$cities)));
        Model model = new ModelImpl().addQuery(listSafeCities_build);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        org.drools.modelcompiler.oopathdtables.Person person = new org.drools.modelcompiler.oopathdtables.Person();
        person.setAddress(new InternationalAddress("", 1, "Milan", "Safecountry"));
        ksession.insert(person);
        org.drools.modelcompiler.oopathdtables.Person person2 = new org.drools.modelcompiler.oopathdtables.Person();
        person2.setAddress(new InternationalAddress("", 1, "Rome", "Unsafecountry"));
        ksession.insert(person2);
        QueryResults results = ksession.getQueryResults("listSafeCities");
        Assert.assertEquals("Milan", ((List) (results.iterator().next().get("$cities"))).iterator().next());
    }

    @Test
    public void testFromAccumulate() {
        final Variable<Number> var_$sum = FlowDSL.declarationOf(Number.class, "$sum");
        final Variable<Person> var_$p = FlowDSL.declarationOf(Person.class, "$p");
        final Variable<Integer> var_$expr$3$ = FlowDSL.declarationOf(Integer.class, "$expr$3$");
        Rule rule = FlowDSL.rule("X").build(FlowDSL.bind(var_$expr$3$).as(var_$p, ( _this) -> _this.getAge()), FlowDSL.expr("$expr$1$", var_$sum, ( _this) -> (_this.intValue()) > 0).reactOn("intValue"), FlowDSL.accumulate(FlowDSL.expr("$expr$2$", var_$p, ( _this) -> _this.getName().startsWith("M")), FlowDSL.accFunction(IntegerSumAccumulateFunction.class, var_$expr$3$).as(var_$sum)), FlowDSL.on(var_$sum).execute(( drools, $sum) -> {
            drools.insert(new Result($sum));
        }));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();
        Collection<Result> results = BaseModelTest.getObjectsIntoList(ksession, Result.class);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(77, results.iterator().next().getValue());
    }

    @Test
    public void testAccumulateAnd() {
        Variable<Number> var_$sum = FlowDSL.declarationOf(Number.class, "$sum");
        Variable<Person> var_$p = FlowDSL.declarationOf(Person.class, "$p");
        Variable<Integer> var_$expr$3$ = FlowDSL.declarationOf(Integer.class, "$expr$3$");
        Rule rule = FlowDSL.rule("X").build(FlowDSL.bind(var_$expr$3$).as(var_$p, ( _this) -> _this.getAge()), FlowDSL.accumulate(FlowDSL.and(FlowDSL.expr("$expr$1$", var_$p, ( _this) -> (_this.getAge()) > 10).indexedBy(int.class, org.drools.model.Index.ConstraintType.GREATER_THAN, 0, ( _this) -> _this.getAge(), 10).reactOn("age"), FlowDSL.expr("$expr$2$", var_$p, ( _this) -> _this.getName().startsWith("M"))), FlowDSL.accFunction(IntegerSumAccumulateFunction.class, var_$expr$3$).as(var_$sum)), FlowDSL.on(var_$sum).execute(( drools, $sum) -> {
            drools.insert(new Result($sum));
        }));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();
        Collection<Result> results = BaseModelTest.getObjectsIntoList(ksession, Result.class);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(77, results.iterator().next().getValue());
    }

    public static class Data {
        private List values;

        private int bias;

        public Data(List values, int bias) {
            this.values = values;
            this.bias = bias;
        }

        public List getValues() {
            return values;
        }

        public int getBias() {
            return bias;
        }
    }

    @Test
    public void testIn() {
        final Variable<String> var_$pattern_String$1$ = FlowDSL.declarationOf(String.class, "$pattern_String$1$");
        Rule rule = FlowDSL.rule("R").build(FlowDSL.expr("$expr$1$", var_$pattern_String$1$, ( _this) -> eval(InOperator.INSTANCE, _this, "a", "b")), FlowDSL.execute(() -> {
        }));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        ksession.insert("b");
        Assert.assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testCustomAccumulate() {
        final Variable<Customer> var_$customer = FlowDSL.declarationOf(Customer.class, "$customer");
        final Variable<String> var_$code = FlowDSL.declarationOf(String.class, "$code");
        final Variable<TargetPolicy> var_$target = FlowDSL.declarationOf(TargetPolicy.class, "$target");
        final Variable<List> var_$pattern_List$1$ = FlowDSL.declarationOf(List.class, "$pattern_List$1$");
        final Variable<TargetPolicy> var_$tp = FlowDSL.declarationOf(TargetPolicy.class, "$tp");
        final BitMask mask_$target = org.drools.model.BitMask.getPatternMask(TargetPolicy.class, "coefficient");
        Rule rule = FlowDSL.rule("Customer can only have one Target Policy for Product p1 with coefficient 1").build(FlowDSL.bind(var_$code).as(var_$customer, ( _this) -> _this.getCode()).reactOn("code"), FlowDSL.expr("$expr$2$", var_$target, var_$code, ( _this, $code) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getCustomerCode(), $code)).indexedBy(String.class, org.drools.model.Index.ConstraintType.EQUAL, 0, ( _this) -> _this.getCustomerCode(), ( $code) -> $code).reactOn("customerCode"), FlowDSL.expr("$expr$3$", var_$target, ( _this) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getProductCode(), "p1")).indexedBy(String.class, org.drools.model.Index.ConstraintType.EQUAL, 1, ( _this) -> _this.getProductCode(), "p1").reactOn("productCode"), FlowDSL.expr("$expr$4$", var_$target, ( _this) -> (_this.getCoefficient()) == 1).indexedBy(int.class, org.drools.model.Index.ConstraintType.EQUAL, 2, ( _this) -> _this.getCoefficient(), 1).reactOn("coefficient"), FlowDSL.expr("$expr$5$", var_$pattern_List$1$, ( _this) -> (_this.size()) > 1).indexedBy(int.class, org.drools.model.Index.ConstraintType.GREATER_THAN, 0, ( _this) -> _this.size(), 1).reactOn("size"), FlowDSL.accumulate(FlowDSL.and(FlowDSL.expr("$expr$2$", var_$tp, var_$code, ( _this, $code) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getCustomerCode(), $code)).indexedBy(String.class, org.drools.model.Index.ConstraintType.EQUAL, 0, ( _this) -> _this.getCustomerCode(), ( $code) -> $code).reactOn("customerCode"), FlowDSL.expr("$expr$3$", var_$tp, ( _this) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getProductCode(), "p1")).indexedBy(String.class, org.drools.model.Index.ConstraintType.EQUAL, 1, ( _this) -> _this.getProductCode(), "p1").reactOn("productCode"), FlowDSL.expr("$expr$4$", var_$tp, ( _this) -> (_this.getCoefficient()) == 1).indexedBy(int.class, org.drools.model.Index.ConstraintType.EQUAL, 2, ( _this) -> _this.getCoefficient(), 1).reactOn("coefficient")), FlowDSL.accFunction(FlowTest.MyAccumulateFunction.class, var_$tp).as(var_$pattern_List$1$)), FlowDSL.on(var_$target).execute(( drools, $target) -> {
            $target.setCoefficient(0);
            drools.update($target, mask_$target);
        }));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession ksession = kieBase.newKieSession();
        Customer customer = new Customer();
        customer.setCode("code1");
        TargetPolicy target1 = new TargetPolicy();
        target1.setCustomerCode("code1");
        target1.setProductCode("p1");
        target1.setCoefficient(1);
        TargetPolicy target2 = new TargetPolicy();
        target2.setCustomerCode("code1");
        target2.setProductCode("p1");
        target2.setCoefficient(1);
        TargetPolicy target3 = new TargetPolicy();
        target3.setCustomerCode("code1");
        target3.setProductCode("p1");
        target3.setCoefficient(1);
        ksession.insert(customer);
        ksession.insert(target1);
        ksession.insert(target2);
        ksession.insert(target3);
        ksession.fireAllRules();
        List<TargetPolicy> targetPolicyList = Arrays.asList(target1, target2, target3);
        long filtered = targetPolicyList.stream().filter(( c) -> (c.getCoefficient()) == 1).count();
        Assert.assertEquals(1, filtered);
    }

    public static class MyAccumulateFunction implements AccumulateFunction<FlowTest.MyAccumulateFunction.MyData> {
        public static class MyData implements Serializable {
            public ArrayList myList;
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            // functions are stateless, so nothing to serialize
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            // functions are stateless, so nothing to serialize
        }

        public FlowTest.MyAccumulateFunction.MyData createContext() {
            return new FlowTest.MyAccumulateFunction.MyData();
        }

        public void init(FlowTest.MyAccumulateFunction.MyData data) {
            data.myList = new ArrayList<Object>();
        }

        public void accumulate(FlowTest.MyAccumulateFunction.MyData data, Object $tp) {
            data.myList.add($tp);
        }

        public void reverse(FlowTest.MyAccumulateFunction.MyData data, Object $tp) {
            data.myList.remove($tp);
        }

        public Object getResult(FlowTest.MyAccumulateFunction.MyData data) {
            return data.myList;
        }

        public boolean supportsReverse() {
            return true;
        }

        public Class<?> getResultType() {
            return ArrayList.class;
        }
    }

    @Test
    public void testOrConditional() {
        final Global<List> var_list = FlowDSL.globalOf(List.class, "defaultpkg", "list");
        final Variable<Employee> var_$pattern_Employee$1$ = FlowDSL.declarationOf(Employee.class, "$pattern_Employee$1$");
        final Variable<Address> var_$address = FlowDSL.declarationOf(Address.class, "$address", FlowDSL.reactiveFrom(var_$pattern_Employee$1$, ( _this) -> _this.getAddress()));
        Rule rule = FlowDSL.rule("R").build(FlowDSL.or(FlowDSL.and(FlowDSL.input(var_$pattern_Employee$1$), FlowDSL.expr("$expr$2$", var_$address, ( _this) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getCity(), "Big City")).indexedBy(String.class, org.drools.model.Index.ConstraintType.EQUAL, 0, ( _this) -> _this.getCity(), "Big City").reactOn("city")), FlowDSL.and(FlowDSL.input(var_$pattern_Employee$1$), FlowDSL.expr("$expr$4$", var_$address, ( _this) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getCity(), "Small City")).indexedBy(String.class, org.drools.model.Index.ConstraintType.EQUAL, 0, ( _this) -> _this.getCity(), "Small City").reactOn("city"))), FlowDSL.on(var_$address, var_list).execute(( $address, list) -> {
            list.add($address.getCity());
        }));
        Model model = new ModelImpl().addRule(rule).addGlobal(var_list);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        KieSession kieSession = kieBase.newKieSession();
        List<String> results = new ArrayList<>();
        kieSession.setGlobal("list", results);
        final Employee bruno = Employee.createEmployee("Bruno", new Address("Elm", 10, "Small City"));
        kieSession.insert(bruno);
        final Employee alice = Employee.createEmployee("Alice", new Address("Elm", 10, "Big City"));
        kieSession.insert(alice);
        kieSession.fireAllRules();
        Assertions.assertThat(results).containsExactlyInAnyOrder("Big City", "Small City");
    }

    @Test
    public void testAfterWithAnd() throws Exception {
        Variable<StockTick> var_$a = FlowDSL.declarationOf(StockTick.class, "$a");
        Variable<StockTick> var_$b = FlowDSL.declarationOf(StockTick.class, "$b");
        Rule rule = FlowDSL.rule("R").build(FlowDSL.expr("$expr$1$", var_$a, ( _this) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getCompany(), "DROO")).indexedBy(String.class, org.drools.model.Index.ConstraintType.EQUAL, 0, ( _this) -> _this.getCompany(), "DROO").reactOn("company"), FlowDSL.and(FlowDSL.expr("$expr$2$", var_$b, ( _this) -> org.drools.modelcompiler.util.EvaluationUtil.areNullSafeEquals(_this.getCompany(), "ACME")).indexedBy(String.class, org.drools.model.Index.ConstraintType.EQUAL, 0, ( _this) -> _this.getCompany(), "ACME").reactOn("company"), FlowDSL.expr("$expr$3$", var_$b, var_$a, DSL.after(5L, TimeUnit.SECONDS, 8L, TimeUnit.SECONDS))), FlowDSL.execute(() -> {
            System.out.println("fired");
        }));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model, STREAM);
        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        conf.setOption(ClockTypeOption.get(PSEUDO_CLOCK.getId()));
        KieSession ksession = kieBase.newKieSession(conf, null);
        SessionPseudoClock clock = ksession.getSessionClock();
        ksession.insert(new StockTick("DROO"));
        clock.advanceTime(6, TimeUnit.SECONDS);
        ksession.insert(new StockTick("ACME"));
        Assert.assertEquals(1, ksession.fireAllRules());
        clock.advanceTime(4, TimeUnit.SECONDS);
        ksession.insert(new StockTick("ACME"));
        Assert.assertEquals(0, ksession.fireAllRules());
    }
}

