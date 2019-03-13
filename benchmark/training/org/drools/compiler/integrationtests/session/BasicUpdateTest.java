/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests.session;


import java.util.ArrayList;
import java.util.List;
import org.drools.core.test.model.Cheese;
import org.drools.core.test.model.Person;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;


public class BasicUpdateTest {
    private static final String UPDATE_TEST_DRL = "org/drools/compiler/integrationtests/session/update_test.drl";

    private KieSession ksession;

    @Test
    public void updateTheOnlyFactTest() {
        final Person person = new Person("George", 18);
        final FactHandle factPerson = ksession.insert(person);
        assertThat(ksession.getObjects()).hasSize(1);
        assertThat(ksession.getObjects().iterator().next()).isInstanceOf(Person.class);
        Person personToBeVerified = ((Person) (ksession.getObjects().iterator().next()));
        verifyPerson(person, personToBeVerified, 18, "George", true);
        ksession.update(factPerson, new Person("Henry", 21));
        verifyFactsPresentInSession(1, Person.class);
        personToBeVerified = ((Person) (ksession.getObjects().iterator().next()));
        verifyPerson(person, personToBeVerified, 21, "Henry", false);
    }

    @Test(expected = NullPointerException.class)
    public void updateWithNullTest() {
        final Person person = new Person("George", 18);
        final FactHandle factPerson = ksession.insert(person);
        verifyFactsPresentInSession(1, Person.class);
        ksession.update(factPerson, null);
    }

    @Test
    public void updateWithDifferentClassGetQueryResultsTest() {
        final Person person = new Person("George", 18);
        final FactHandle fact = ksession.insert(person);
        verifyFactsWithQuery(Person.class, "persons", person);
        final Cheese cheese = new Cheese("Camembert", 2);
        ksession.update(fact, cheese);
        verifyWithQueryNoPersonsPresentInFacts();
        verifyFactsPresentInSession(1, Cheese.class);
        Cheese cheeseToBeVerified = ((Cheese) (ksession.getObjects().iterator().next()));
        verifyCheese(cheeseToBeVerified, 2, "Camembert");
        cheeseToBeVerified = verifyFactPresentInSession(fact, Cheese.class);
        verifyCheese(cheeseToBeVerified, 2, "Camembert");
    }

    @Test
    public void updateWithDifferentClassGetObjectsTest() {
        final Person person = new Person("George", 18);
        final FactHandle factPerson = ksession.insert(person);
        final Person personToBeVerified = verifyFactsPresentInSession(1, Person.class).get(0);
        assertThat(personToBeVerified).isEqualTo(person);
        final Cheese cheese = new Cheese("Camembert", 50);
        ksession.update(factPerson, cheese);
        verifyFactsPresentInSession(1, Cheese.class);
        final Cheese cheeseToBeVerified = ((Cheese) (ksession.getObjects().iterator().next()));
        verifyCheese(cheeseToBeVerified, 50, "Camembert");
    }

    @Test
    public void updateFireRulesTest() {
        final Person george = new Person("George", 17);
        final Person henry = new Person("Henry", 25);
        final FactHandle georgeFact = ksession.insert(george);
        ksession.insert(henry);
        verifyFactsWithQuery(Person.class, "persons", george, henry);
        final List<Person> drivers = new ArrayList<>();
        ksession.setGlobal("drivers", drivers);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        verifyList(drivers, george, henry);
        george.setAge(18);
        ksession.update(georgeFact, george);
        verifyFactsWithQuery(Person.class, "persons", george, henry);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        verifyList(drivers, null, george, henry);
    }

    @Test
    public void updateFactOnRuleFireTest() {
        final Cheese camembert = new Cheese("Camembert", 19);
        final Cheese cheddar = new Cheese("Cheddar", 45);
        ksession.insert(camembert);
        ksession.insert(cheddar);
        verifyFactsWithQuery(Cheese.class, "cheeseTypes", camembert, cheddar);
        final List<Cheese> expensiveCheese = new ArrayList<>();
        ksession.setGlobal("expensiveCheese", expensiveCheese);
        final int firedRules = ksession.fireAllRules();
        assertThat(firedRules).isEqualTo(2);
        verifyList(expensiveCheese, camembert, cheddar);
        verifyFactsWithQuery(Cheese.class, "cheeseTypes", camembert, cheddar);
        assertThat(camembert.getPrice()).isEqualTo(21);
        assertThat(cheddar.getPrice()).isEqualTo(45);
    }
}

