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


import ResourceType.DRL;
import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;


/**
 * This is a sample class to launch a rule.
 */
public class DateComparisonTest extends CommonTestMethodBase {
    @Test
    public void testDateComparisonThan() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";
        str += "global java.util.List results;\n";
        str += "rule \"test date greater than\"\n";
        str += "     when\n";
        str += "         $c : Cheese(type == \"Yesterday\")\n";
        str += "         Cheese(type == \"Tomorrow\",  usedBy > ($c.usedBy))\n";
        str += "     then\n";
        str += "         results.add( \"test date greater than\" );\n";
        str += "end\n";
        str += "rule \"test date less than\"\n";
        str += "    when\n";
        str += "        $c : Cheese(type == \"Tomorrow\")\n";
        str += "        Cheese(type == \"Yesterday\", usedBy < ($c.usedBy));\n";
        str += "    then\n";
        str += "        results.add( \"test date less than\" );\n";
        str += "end\n";
        KieSession ksession = new KieHelper().addContent(str, DRL).build().newKieSession();
        List<String> results = new ArrayList<String>();
        ksession.setGlobal("results", results);
        // go !
        Cheese yesterday = new Cheese("Yesterday");
        yesterday.setUsedBy(yesterday());
        Cheese tomorrow = new Cheese("Tomorrow");
        tomorrow.setUsedBy(tomorrow());
        ksession.insert(yesterday);
        ksession.insert(tomorrow);
        ksession.fireAllRules();
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.contains("test date greater than"));
        Assert.assertTrue(results.contains("test date less than"));
    }

    @Test
    public void testDateComparisonAfter() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";
        str += "global java.util.List results;\n";
        str += "rule \"test date greater than\"\n";
        str += "     when\n";
        str += "         $c : Cheese(type == \"Yesterday\")\n";
        str += "         Cheese(type == \"Tomorrow\", $c.usedBy before usedBy)\n";
        str += "     then\n";
        str += "         results.add( \"test date greater than\" );\n";
        str += "end\n";
        str += "rule \"test date less than\"\n";
        str += "    when\n";
        str += "        $c : Cheese(type == \"Tomorrow\")\n";
        str += "        Cheese(type == \"Yesterday\", $c.usedBy after usedBy);\n";
        str += "    then\n";
        str += "        results.add( \"test date less than\" );\n";
        str += "end\n";
        KieSession ksession = new KieHelper().addContent(str, DRL).build().newKieSession();
        List<String> results = new ArrayList<String>();
        ksession.setGlobal("results", results);
        // go !
        Cheese yesterday = new Cheese("Yesterday");
        yesterday.setUsedBy(yesterday());
        Cheese tomorrow = new Cheese("Tomorrow");
        tomorrow.setUsedBy(tomorrow());
        ksession.insert(yesterday);
        ksession.insert(tomorrow);
        ksession.fireAllRules();
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.contains("test date greater than"));
        Assert.assertTrue(results.contains("test date less than"));
    }

    @Test
    public void testDateComparisonAfterWithThisBinding() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";
        str += "global java.util.List results;\n";
        str += "rule \"test date greater than\"\n";
        str += "     when\n";
        str += "         Cheese(type == \"Yesterday\", $c: this)\n";
        str += "         Cheese(type == \"Tomorrow\", $c.usedBy before usedBy)\n";
        str += "     then\n";
        str += "         results.add( \"test date greater than\" );\n";
        str += "end\n";
        str += "rule \"test date less than\"\n";
        str += "    when\n";
        str += "        Cheese(type == \"Tomorrow\", $c: this)\n";
        str += "        Cheese(type == \"Yesterday\", $c.usedBy after usedBy);\n";
        str += "    then\n";
        str += "        results.add( \"test date less than\" );\n";
        str += "end\n";
        KieSession ksession = new KieHelper().addContent(str, DRL).build().newKieSession();
        List<String> results = new ArrayList<String>();
        ksession.setGlobal("results", results);
        // go !
        Cheese yesterday = new Cheese("Yesterday");
        yesterday.setUsedBy(yesterday());
        Cheese tomorrow = new Cheese("Tomorrow");
        tomorrow.setUsedBy(tomorrow());
        ksession.insert(yesterday);
        ksession.insert(tomorrow);
        ksession.fireAllRules();
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.contains("test date greater than"));
        Assert.assertTrue(results.contains("test date less than"));
    }
}

