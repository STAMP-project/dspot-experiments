/**
 * Copyright 2011 Red Hat Inc.
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
package org.drools.persistence.session;


import Message.Level.ERROR;
import Message.Level.WARNING;
import ResourceType.DRL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.spi.AgendaGroup;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.builder.Results;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;


@RunWith(Parameterized.class)
public class AgendaRuleFlowGroupsTest {
    private Map<String, Object> context;

    private boolean locking;

    public AgendaRuleFlowGroupsTest(boolean locking) {
        this.locking = true;
    }

    @Test
    public void testRuleFlowGroupOnly() throws Exception {
        CommandBasedStatefulKnowledgeSession ksession = createSession((-1), "ruleflow-groups.drl");
        org.drools.core[] groups = getAgendaGroups();
        // only main is available
        Assert.assertEquals(1, groups.length);
        Assert.assertEquals("MAIN", groups[0].getName());
        long id = ksession.getIdentifier();
        List<String> list = new ArrayList<String>();
        list.add("Test");
        ksession.insert(list);
        ksession.execute(new AgendaRuleFlowGroupsTest.ActivateRuleFlowCommand("ruleflow-group"));
        ksession.dispose();
        ksession = createSession(id, "ruleflow-groups.drl");
        groups = ((org.drools.core.common.InternalAgenda) (stripSession(ksession).getAgenda())).getAgendaGroups();
        // main and rule flow is now on the agenda
        Assert.assertEquals(2, groups.length);
        Assert.assertEquals("MAIN", groups[0].getName());
        Assert.assertEquals("ruleflow-group", groups[1].getName());
    }

    @Test
    public void testAgendaGroupOnly() throws Exception {
        CommandBasedStatefulKnowledgeSession ksession = createSession((-1), "agenda-groups.drl");
        org.drools.core[] groups = getAgendaGroups();
        // only main is available
        Assert.assertEquals(1, groups.length);
        Assert.assertEquals("MAIN", groups[0].getName());
        long id = ksession.getIdentifier();
        List<String> list = new ArrayList<String>();
        list.add("Test");
        ksession.insert(list);
        ksession.execute(new AgendaRuleFlowGroupsTest.ActivateAgendaGroupCommand("agenda-group"));
        ksession.dispose();
        ksession = createSession(id, "agenda-groups.drl");
        groups = ((org.drools.core.common.InternalAgenda) (stripSession(ksession).getAgenda())).getAgendaGroups();
        // main and agenda group is now on the agenda
        Assert.assertEquals(2, groups.length);
        Assert.assertEquals("MAIN", groups[0].getName());
        Assert.assertEquals("agenda-group", groups[1].getName());
    }

    @Test
    public void testAgendaGroupAndRuleFlowGroup() throws Exception {
        CommandBasedStatefulKnowledgeSession ksession = createSession((-1), "agenda-groups.drl", "ruleflow-groups.drl");
        org.drools.core[] groups = getAgendaGroups();
        // only main is available
        Assert.assertEquals(1, groups.length);
        Assert.assertEquals("MAIN", groups[0].getName());
        long id = ksession.getIdentifier();
        List<String> list = new ArrayList<String>();
        list.add("Test");
        ksession.insert(list);
        ksession.execute(new AgendaRuleFlowGroupsTest.ActivateAgendaGroupCommand("agenda-group"));
        ksession.execute(new AgendaRuleFlowGroupsTest.ActivateRuleFlowCommand("ruleflow-group"));
        ksession.dispose();
        ksession = createSession(id, "agenda-groups.drl", "ruleflow-groups.drl");
        groups = ((org.drools.core.common.InternalAgenda) (stripSession(ksession).getAgenda())).getAgendaGroups();
        // main and agenda group is now on the agenda
        Assert.assertEquals(3, groups.length);
        Assert.assertEquals("MAIN", groups[0].getName());
        Assert.assertEquals("ruleflow-group", groups[1].getName());
        Assert.assertEquals("agenda-group", groups[2].getName());
    }

    @SuppressWarnings("serial")
    public class ActivateRuleFlowCommand implements ExecutableCommand<Object> {
        private String ruleFlowGroupName;

        public ActivateRuleFlowCommand(String ruleFlowGroupName) {
            this.ruleFlowGroupName = ruleFlowGroupName;
        }

        public Void execute(Context context) {
            KieSession ksession = lookup(KieSession.class);
            activateRuleFlowGroup(ruleFlowGroupName);
            return null;
        }
    }

    @SuppressWarnings("serial")
    public class ActivateAgendaGroupCommand implements ExecutableCommand<Object> {
        private String agendaGroupName;

        public ActivateAgendaGroupCommand(String agendaGroupName) {
            this.agendaGroupName = agendaGroupName;
        }

        public Void execute(Context context) {
            KieSession ksession = lookup(KieSession.class);
            getAgendaGroup(agendaGroupName).setFocus();
            return null;
        }
    }

    @SuppressWarnings("serial")
    public class ExceptionCommand implements ExecutableCommand<Object> {
        public Void execute(Context context) {
            throw new RuntimeException();
        }
    }

    @Test
    public void testConflictingAgendaAndRuleflowGroups() throws Exception {
        String drl = "package org.drools.test; " + (((((("" + "rule Test ") + "  agenda-group 'ag' ") + "  ruleflow-group 'rf' ") + "when ") + "then ") + "end ");
        KieHelper helper = new KieHelper();
        helper.addContent(drl, DRL);
        Results res = helper.verify();
        System.err.println(res.getMessages());
        Assert.assertEquals(1, res.getMessages(WARNING).size());
        Assert.assertEquals(0, res.getMessages(ERROR).size());
    }
}

