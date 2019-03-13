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
package org.drools.compiler.integrationtests.concurrency;


import org.drools.compiler.integrationtests.facts.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class SubnetworkConcurrentSessionsTest extends AbstractConcurrentTest {
    private static final Integer NUMBER_OF_THREADS = 10;

    private static final Integer NUMBER_OF_REPETITIONS = 1;

    public SubnetworkConcurrentSessionsTest(final boolean enforcedJitting, final boolean serializeKieBase, final boolean sharedKieBase) {
        super(enforcedJitting, serializeKieBase, sharedKieBase, false);
    }

    @Test(timeout = 40000)
    public void test1() throws InterruptedException {
        final String drl = "rule R when String() then end";
        parallelTest(SubnetworkConcurrentSessionsTest.NUMBER_OF_REPETITIONS, SubnetworkConcurrentSessionsTest.NUMBER_OF_THREADS, ( kieSession, counter) -> {
            kieSession.insert("test");
            return (kieSession.fireAllRules()) == 1;
        }, null, null, drl);
    }

    @Test(timeout = 40000)
    public void test2NoSubnetwork() throws InterruptedException {
        test2(getRule("R1", "this == \"odd\"", false, false, "Number( intValue > 0 )"), getRule("R2", "this == \"pair\"", false, false, "Number( intValue < 10000 )"));
    }

    @Test(timeout = 40000)
    public void test2WithSubnetwork() throws InterruptedException {
        test2(getRule("R1", "this == \"odd\"", false, true, "Number( intValue > 0 )"), getRule("R2", "this == \"pair\"", false, true, "Number( intValue < 10000 )"));
    }

    @Test(timeout = 40000)
    public void test3NoSubnetwork() throws InterruptedException {
        test3(getRule("R1", "this == \"odd\"", false, false, "Number( intValue > 0 )"), getRule("R2", "this == \"pair\"", false, false, "Number( intValue < 10000 )"));
    }

    @Test(timeout = 40000)
    public void test3WithSubnetwork() throws InterruptedException {
        test3(getRule("R1", "this == \"odd\"", false, true, "Number( intValue > 0 )"), getRule("R2", "this == \"pair\"", false, true, "Number( intValue < 10000 )"));
    }

    @Test(timeout = 40000)
    public void test3WithSharedSubnetwork() throws InterruptedException {
        final String ruleTemplate = (((((((((((((("import " + (Product.class.getCanonicalName())) + ";\n") + "rule ${ruleName} when\n") + "  $s : String()\n") + "  $p : Product( category == $s )\n") + "  $n : Number(intValue > 0) from accumulate (\n") + "    $s_1 : String( this == $s ) and\n") + "    $p_1 : Product( category == $s_1 )\n") + "    ;count($p_1))\n") + "  Product(this == $p, category == \"${category}\", firings not contains \"${ruleName}\")\n") + "then\n") + "  $p.getFirings().add(\"${ruleName}\");\n") + "  $p.appendDescription($s);\n") + "  update($p);\n") + "end\n";
        final String drl1 = ruleTemplate.replace("${ruleName}", "R1").replace("${category}", "odd");
        final String drl2 = ruleTemplate.replace("${ruleName}", "R2").replace("${category}", "pair");
        test3(drl1, drl2);
    }

    @Test(timeout = 10000)
    public void test4NoSharing() throws InterruptedException {
        test4(getRule("R1", "", false, true, "Number( intValue > 5 )"), getRule("R2", "", false, true, "Number( intValue < 5 )"));
    }

    @Test(timeout = 10000)
    public void test4WithSharing() throws InterruptedException {
        test4(getRule("R1", "", true, true, "Number( intValue > 5 )"), getRule("R2", "", true, true, "Number( intValue < 5 )"));
    }
}

