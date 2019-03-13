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


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.persistence.util.DroolsPersistenceUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class RuleFlowGroupRollbackTest {
    private static Logger logger = LoggerFactory.getLogger(RuleFlowGroupRollbackTest.class);

    private Map<String, Object> context;

    private boolean locking;

    public RuleFlowGroupRollbackTest(String locking) {
        this.locking = DroolsPersistenceUtil.PESSIMISTIC_LOCKING.equals(locking);
    }

    @Test
    public void testRuleFlowGroupRollback() throws Exception {
        CommandBasedStatefulKnowledgeSession ksession = createSession();
        List<String> list = new ArrayList<String>();
        list.add("Test");
        ksession.insert(list);
        ksession.execute(new RuleFlowGroupRollbackTest.ActivateRuleFlowCommand("ruleflow-group"));
        Assert.assertEquals(1, ksession.fireAllRules());
        try {
            ksession.execute(new RuleFlowGroupRollbackTest.ExceptionCommand());
            Assert.fail("Process must throw an exception");
        } catch (Exception e) {
            RuleFlowGroupRollbackTest.logger.info((("The above " + (RuntimeException.class.getSimpleName())) + " was expected in this test."));
        }
        ksession.insert(list);
        ksession.execute(new RuleFlowGroupRollbackTest.ActivateRuleFlowCommand("ruleflow-group"));
        Assert.assertEquals(1, ksession.fireAllRules());
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
    public class ExceptionCommand implements ExecutableCommand<Object> {
        public Void execute(Context context) {
            throw new RuntimeException("(Expected) exception thrown by test");
        }
    }
}

