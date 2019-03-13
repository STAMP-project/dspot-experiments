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
package org.drools.compiler.integrationtests.drl;


import java.util.ArrayList;
import java.util.List;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;


@RunWith(Parameterized.class)
public class RHSTest {
    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public RHSTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Test
    public void testGenericsInRHS() {
        final String drl = "package org.drools.compiler.integrationtests.drl;\n" + (((((("import java.util.Map;\n" + "import java.util.HashMap;\n") + "rule \"Test Rule\"\n") + "  when\n") + "  then\n") + "    Map<String,String> map = new HashMap<String,String>();\n") + "end");
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rhs-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        ksession.dispose();
    }

    @Test
    public void testIncrementOperator() {
        final String drl = "package org.drools.compiler.integrationtest.drl \n" + ((((((((("global java.util.List list \n" + "rule rule1 \n") + "    dialect \"java\" \n") + "when \n") + "    $I : Integer() \n") + "then \n") + "    int i = $I.intValue(); \n") + "    i += 5; \n") + "    list.add( i ); \n") + "end \n");
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rhs-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);
            ksession.insert(5);
            ksession.fireAllRules();
            Assert.assertEquals(1, list.size());
            Assert.assertEquals(10, list.get(0));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testKnowledgeHelperFixerInStrings() {
        final String drl = "package org.drools.compiler.integrationtests.drl; \n" + ((((((((("global java.util.List list \n" + "rule xxx \n") + "  no-loop true ") + "when \n") + "  $fact : String() \n") + "then \n") + "  list.add(\"This is an update()\"); \n") + "  list.add(\"This is an update($fact)\"); \n") + "  update($fact); \n") + "end  \n");
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rhs-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);
            ksession.insert("hello");
            ksession.fireAllRules();
            Assert.assertEquals(2, list.size());
            Assert.assertEquals("This is an update()", list.get(0));
            Assert.assertEquals("This is an update($fact)", list.get(1));
        } finally {
            ksession.dispose();
        }
    }
}

