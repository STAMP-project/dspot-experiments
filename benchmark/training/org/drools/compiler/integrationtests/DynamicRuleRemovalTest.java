/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests;


import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;


public class DynamicRuleRemovalTest extends CommonTestMethodBase {
    @Test
    public void testDynamicRuleRemoval() throws Exception {
        InternalKnowledgeBase kbase = ((InternalKnowledgeBase) (KnowledgeBaseFactory.newKnowledgeBase()));
        addRule(kbase, "rule1");
        addRule(kbase, "rule2");
        addRule(kbase, "rule3");
        final KieSession ksession = kbase.newKieSession();
        List<String> rulesList = new ArrayList<String>();
        ksession.setGlobal("list", rulesList);
        ksession.insert("2");
        ksession.fireAllRules();
        Assert.assertEquals(3, rulesList.size());
        Assert.assertTrue(rulesList.contains("rule1"));
        Assert.assertTrue(rulesList.contains("rule2"));
        Assert.assertTrue(rulesList.contains("rule3"));
        removeRule(kbase, "rule1");
        rulesList.clear();
        ksession.insert("3");
        ksession.fireAllRules();
        Assert.assertEquals(2, rulesList.size());
        Assert.assertFalse(rulesList.contains("rule1"));
        Assert.assertTrue(rulesList.contains("rule2"));
        Assert.assertTrue(rulesList.contains("rule3"));
    }
}

