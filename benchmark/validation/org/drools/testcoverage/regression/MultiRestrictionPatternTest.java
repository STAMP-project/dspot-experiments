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
import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.KieSessionTest;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.junit.Test;
import org.kie.api.command.Command;


/**
 * Test to verify BRMS-364 (multi-restriction pattern throws UnsupportedOpEx) is
 * fixed
 */
public class MultiRestrictionPatternTest extends KieSessionTest {
    private static final String DRL_FILE = "BRMS-364.drl";

    public MultiRestrictionPatternTest(final KieBaseTestConfiguration kieBaseTestConfiguration, final KieSessionTestConfiguration kieSessionTestConfiguration) {
        super(kieBaseTestConfiguration, kieSessionTestConfiguration);
    }

    @Test
    public void multiRestriction1() throws Exception {
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(KieUtil.getCommands().newInsert(new Person("multi")));
        commands.add(KieUtil.getCommands().newFireAllRules());
        session.execute(KieUtil.getCommands().newBatchExecution(commands, null));
        Assertions.assertThat(firedRules.isRuleFired("or1")).isTrue();
    }

    @Test
    public void multiRestriction2() throws Exception {
        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(KieUtil.getCommands().newInsert(new Person("MULTIRESTRICTION")));
        commands.add(KieUtil.getCommands().newFireAllRules());
        session.execute(KieUtil.getCommands().newBatchExecution(commands, null));
        Assertions.assertThat(firedRules.isRuleFired("or2")).isTrue();
    }

    @Test
    public void multiRestriction3() throws Exception {
        List<Command<?>> commands = new ArrayList<Command<?>>();
        Person p = new Person();
        p.setId(3);
        commands.add(KieUtil.getCommands().newInsert(p));
        commands.add(KieUtil.getCommands().newFireAllRules());
        session.execute(KieUtil.getCommands().newBatchExecution(commands, null));
        Assertions.assertThat(firedRules.isRuleFired("and")).isTrue();
    }
}

