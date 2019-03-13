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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.drools.model.FlowDSL;
import org.drools.model.Model;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.drools.modelcompiler.domain.Person;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;


public class RuleUnitTest {
    public static class AdultUnit implements RuleUnit {
        private List<String> results = new ArrayList<String>();

        private int adultAge = 18;

        private DataSource<Person> persons;

        public AdultUnit() {
        }

        public AdultUnit(DataSource<Person> persons) {
            this.persons = persons;
        }

        public DataSource<Person> getPersons() {
            return persons;
        }

        public int getAdultAge() {
            return adultAge;
        }

        public List<String> getResults() {
            return results;
        }
    }

    @Test
    public void testRuleUnit() {
        List<String> result = new ArrayList<>();
        Variable<Person> adult = FlowDSL.declarationOf(Person.class, FlowDSL.unitData("persons"));
        Rule rule = FlowDSL.rule("org.drools.retebuilder", "Adult").unit(RuleUnitTest.AdultUnit.class).build(FlowDSL.expr("$expr$1$", adult, ( p) -> (p.getAge()) > 18), FlowDSL.on(adult).execute(( p) -> {
            System.out.println(p.getName());
            result.add(p.getName());
        }));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kieBase);
        executor.newDataSource("persons", new Person("Mario", 43), new Person("Marilena", 44), new Person("Sofia", 5));
        executor.run(RuleUnitTest.AdultUnit.class);
        Assert.assertTrue(result.containsAll(Arrays.asList("Mario", "Marilena")));
    }

    @Test
    public void testRuleUnitWithVarBinding() {
        Variable<RuleUnitTest.AdultUnit> unit = FlowDSL.declarationOf(RuleUnitTest.AdultUnit.class);
        Variable<Person> adult = FlowDSL.declarationOf(Person.class, FlowDSL.unitData("persons"));
        Rule rule = FlowDSL.rule("org.drools.retebuilder", "Adult").unit(RuleUnitTest.AdultUnit.class).build(FlowDSL.expr("$expr$1$", adult, unit, ( p, u) -> (p.getAge()) > (u.getAdultAge())), FlowDSL.on(adult, FlowDSL.unitData(List.class, "results")).execute(( p, r) -> {
            System.out.println(p.getName());
            r.add(p.getName());
        }));
        Model model = new ModelImpl().addRule(rule);
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kieBase);
        executor.newDataSource("persons", new Person("Mario", 43), new Person("Marilena", 44), new Person("Sofia", 5));
        RuleUnitTest.AdultUnit ruleUnit = new RuleUnitTest.AdultUnit();
        executor.run(ruleUnit);
        Assert.assertTrue(ruleUnit.getResults().containsAll(Arrays.asList("Mario", "Marilena")));
    }
}

