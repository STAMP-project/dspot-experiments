/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.testcoverage.regression;


import KieServices.Factory;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Bugfix regression test for:
 * https://bugzilla.redhat.com/show_bug.cgi?id=746887
 * https://issues.jboss.org/browse/JBRULES-3256
 */
@RunWith(Parameterized.class)
public class PropertyListenerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyListenerTest.class);

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public PropertyListenerTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Test
    public void runTest() {
        final KieServices kieServices = Factory.get();
        final Resource drlResource = kieServices.getResources().newClassPathResource("propertyListenerTest.drl", getClass());
        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromResources(TestConstants.PACKAGE_REGRESSION, kieBaseTestConfiguration, drlResource);
        final KieSession session = kieBase.newKieSession();
        final ArrayList<PropertyListenerTest.Person> people = new ArrayList<PropertyListenerTest.Person>();
        people.add(new PropertyListenerTest.Person("Test 1"));
        people.add(new PropertyListenerTest.Person("Test 2"));
        PropertyListenerTest.LOGGER.info("== Listeners attached before rules ==");
        for (PropertyListenerTest.Person person : people) {
            for (PropertyChangeListener listener : person.getBoundSupport().getPropertyChangeListeners()) {
                PropertyListenerTest.LOGGER.info(("Listener attached of type: " + listener));
            }
        }
        // call rules
        final List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(kieServices.getCommands().newInsert(people));
        commands.add(kieServices.getCommands().newFireAllRules());
        session.execute(kieServices.getCommands().newBatchExecution(commands, null));
        session.dispose();
        PropertyListenerTest.LOGGER.info("Session disposed");
        PropertyListenerTest.LOGGER.info("== Listeners attached after rules (should be none) ==");
        printAndAssertListenersFromPeople(people);
        // update name to trigger update
        people.get(0).setName("Test 3");
        PropertyListenerTest.LOGGER.info("== Listeners attached after updating name (should be none) ==");
        printAndAssertListenersFromPeople(people);
    }

    public static class Person {
        private String name;

        private PropertyChangeSupport boundSupport;

        public Person() {
            this(null);
        }

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String value) {
            String oldValue = this.name;
            this.name = value;
            PropertyChangeSupport support = this.getBoundSupport();
            support.firePropertyChange("name", oldValue, this.name);
        }

        public PropertyChangeSupport getBoundSupport() {
            if (null == (this.boundSupport)) {
                this.boundSupport = new PropertyChangeSupport(this);
            }
            return this.boundSupport;
        }

        public void removePropertyChangeListener(String param0, PropertyChangeListener param1) {
            this.getBoundSupport().removePropertyChangeListener(param0, param1);
        }

        public void addPropertyChangeListener(String param0, PropertyChangeListener param1) {
            this.getBoundSupport().addPropertyChangeListener(param0, param1);
        }

        public void removePropertyChangeListener(PropertyChangeListener param0) {
            this.getBoundSupport().removePropertyChangeListener(param0);
        }

        public void addPropertyChangeListener(PropertyChangeListener param0) {
            this.getBoundSupport().addPropertyChangeListener(param0);
        }
    }
}

