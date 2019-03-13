/**
 * Copyright 2016 Red Hat Inc.
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
package org.drools.persistence.kie.persistence.session;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.drools.compiler.Person;
import org.drools.persistence.util.DroolsPersistenceUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;


@RunWith(Parameterized.class)
public class PersistentSessionForallTest {
    private KieSession kieSession;

    private Map<String, Object> context;

    private Environment env;

    private boolean locking;

    public PersistentSessionForallTest(String locking) {
        this.locking = DroolsPersistenceUtil.PESSIMISTIC_LOCKING.equals(locking);
    }

    /**
     * Tests marshalling of persistent KieSession with forall.
     */
    @Test
    public void testNotMatchedCombination() {
        PersistentSessionForallTest.TrackingAgendaEventListener listener = new PersistentSessionForallTest.TrackingAgendaEventListener();
        kieSession.addEventListener(listener);
        Person owner = new Person("cat lady");
        PersistentSessionForallTest.Pet dog = new PersistentSessionForallTest.Pet(PersistentSessionForallTest.Pet.PetType.dog, owner);
        kieSession.insert(dog);
        kieSession.fireAllRules();
        Assert.assertTrue(listener.isRuleFired("Forall2"));
    }

    public static class Pet implements Serializable {
        private static final long serialVersionUID = -3519777750853629395L;

        public enum PetType {

            dog,
            cat;}

        private PersistentSessionForallTest.Pet.PetType type;

        private int age;

        private Person owner;

        public Pet(PersistentSessionForallTest.Pet.PetType type) {
            this.type = type;
            age = 0;
        }

        public Pet(PersistentSessionForallTest.Pet.PetType type, Person owner) {
            this(type);
            this.owner = owner;
        }

        public PersistentSessionForallTest.Pet.PetType getType() {
            return type;
        }

        public void setType(PersistentSessionForallTest.Pet.PetType type) {
            this.type = type;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Person getOwner() {
            return owner;
        }

        public void setOwner(Person owner) {
            this.owner = owner;
        }
    }

    /**
     * Listener tracking number of rules fired.
     */
    public static class TrackingAgendaEventListener extends DefaultAgendaEventListener {
        private Map<String, Integer> rulesFired = new HashMap<String, Integer>();

        @Override
        public void afterMatchFired(AfterMatchFiredEvent event) {
            String rule = event.getMatch().getRule().getName();
            if (isRuleFired(rule)) {
                rulesFired.put(rule, ((rulesFired.get(rule)) + 1));
            } else {
                rulesFired.put(rule, 1);
            }
        }

        /**
         * Return true if the rule was fired at least once
         *
         * @param rule
         * 		- name of the rule
         * @return true if the rule was fired
         */
        public boolean isRuleFired(String rule) {
            return rulesFired.containsKey(rule);
        }

        /**
         * Returns number saying how many times the rule was fired
         *
         * @param rule
         * 		- name of the rule
         * @return number how many times rule was fired, 0 if rule wasn't fired
         */
        public int ruleFiredCount(String rule) {
            if (isRuleFired(rule)) {
                return rulesFired.get(rule);
            } else {
                return 0;
            }
        }

        public void clear() {
            rulesFired.clear();
        }
    }
}

