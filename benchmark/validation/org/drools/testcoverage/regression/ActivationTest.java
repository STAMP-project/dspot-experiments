/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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


import java.util.ArrayList;
import java.util.List;
import org.drools.testcoverage.common.KieSessionTest;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ActivationTest extends KieSessionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivationTest.class);

    private static final String DRL = "package org.drools;\n" + ((((((((((((((((("import org.drools.testcoverage.common.model.Cheese;\n" + "import org.drools.testcoverage.common.model.Person;\n") + "global org.slf4j.Logger LOGGER;\n") + " rule R1\n") + "    salience 10\n") + "    when\n") + "        $c : Cheese( price == 10 )\n") + "        $p : Person( )\n") + "    then\n") + "        modify($c) { setPrice( 5 ) }\n") + "        modify($p) { setAge( 20 ) }\n") + "end\n") + "rule R2\n") + "    when\n") + "        $p : Person( )\n") + "    then \n") + "        LOGGER.debug(\"noop\");\n") + "end\n");

    public ActivationTest(final KieBaseTestConfiguration kieBaseTestConfiguration, final KieSessionTestConfiguration kieSessionTestConfiguration) {
        super(kieBaseTestConfiguration, kieSessionTestConfiguration);
    }

    /**
     * Tests improper deactivation of already activated rule on the agenda. See
     * BZ 862325.
     */
    @Test
    public void noDormantCheckOnModifies() throws Exception {
        AgendaEventListener ael = Mockito.mock(AgendaEventListener.class);
        session.addEventListener(ael);
        session.setGlobal("LOGGER", ActivationTest.LOGGER);
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(KieUtil.getCommands().newInsert(new Person("Bob", 19)));
        commands.add(KieUtil.getCommands().newInsert(new Cheese("brie", 10)));
        commands.add(KieUtil.getCommands().newFireAllRules());
        session.execute(KieUtil.getCommands().newBatchExecution(commands, null));
        // both rules should fire exactly once
        Mockito.verify(ael, Mockito.times(2)).afterMatchFired(ArgumentMatchers.any(AfterMatchFiredEvent.class));
        // no cancellations should have happened
        Mockito.verify(ael, Mockito.never()).matchCancelled(ArgumentMatchers.any(MatchCancelledEvent.class));
    }
}

