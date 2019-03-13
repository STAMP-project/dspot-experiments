/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.compiler.integrationtests;


import java.util.HashSet;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;


// DROOLS-1010
@RunWith(Parameterized.class)
public class AlphaNodeSharingWithDiffPackageNameTest {
    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AlphaNodeSharingWithDiffPackageNameTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    public static class TypeA {
        private final int parentId = 2;

        private final int id = 3;

        public int getParentId() {
            return parentId;
        }

        public int getId() {
            return id;
        }

        private String alphaNode;

        private HashSet<String> firings = new HashSet<>();

        public HashSet<String> getFirings() {
            if ((firings) == null) {
                firings = new HashSet<>();
            }
            return firings;
        }

        public void setFirings(final HashSet<String> x) {
            firings = x;
        }

        private final String data = "AlphaNodeHashingThreshold Data";

        public String getData() {
            return data;
        }

        public String getAlphaNode() {
            return alphaNode;
        }

        public void setAlphaNode(final String alphaNode) {
            this.alphaNode = alphaNode;
        }
    }

    public static class TypeB {
        private final int parentId = 1;

        private final int id = 2;

        public int getParentId() {
            return parentId;
        }

        public int getId() {
            return id;
        }
    }

    public static class TypeC {
        private final int parentId = 0;

        private final int id = 1;

        public int getParentId() {
            return parentId;
        }

        public int getId() {
            return id;
        }
    }

    public static class TypeD {}

    public static class TypeE {}

    private static final String rule1 = ((((((((((((((((((("package com.test.rule1;\r\n" + ("\r\n" + "import ")) + (AlphaNodeSharingWithDiffPackageNameTest.TypeA.class.getCanonicalName())) + ";\r\n") + "import ") + (AlphaNodeSharingWithDiffPackageNameTest.TypeB.class.getCanonicalName())) + ";\r\n") + "import ") + (AlphaNodeSharingWithDiffPackageNameTest.TypeC.class.getCanonicalName())) + ";\r\n") + "           \r\n") + "rule R1\r\n") + "when\r\n") + "   $c : TypeC()\r\n") + "   $b : TypeB(parentId == $c.Id)\r\n") + "   $a : TypeA( parentId == $b.Id, firings not contains \"R1 Fired\")\r\n") + "then\r\n") + "   $a.setAlphaNode(\"value contains TypeD TypeE data type\");\r\n") + "   $a.getFirings().add(\"R1 Fired\");\r\n") + "   update($a);\r\n") + "end";

    private static final String rule2 = (((((((((((((((((((("package com.test.rule2;\r\n" + ("\r\n" + "import ")) + (AlphaNodeSharingWithDiffPackageNameTest.TypeA.class.getCanonicalName())) + ";\r\n") + "import ") + (AlphaNodeSharingWithDiffPackageNameTest.TypeB.class.getCanonicalName())) + ";\r\n") + "import ") + (AlphaNodeSharingWithDiffPackageNameTest.TypeC.class.getCanonicalName())) + ";\r\n") + "\r\n") + "rule R2 \r\n") + "when\r\n") + "   $c : TypeC()\r\n") + "   $b : TypeB(parentId == $c.Id)\r\n") + "   $a : TypeA(parentId == $b.Id, \r\n") + "               alphaNode==\"value contains TypeD TypeE data type\", \r\n") + "               firings not contains \"R2 Fired\")\r\n") + "then\r\n") + "       $a.getFirings().add(\"R2 Fired\");\r\n") + "       update($a);\r\n") + "end";

    private static final String rule3 = (((((((((((((((((((((((("package com.test.rule3;\r\n" + ("\r\n" + "import ")) + (AlphaNodeSharingWithDiffPackageNameTest.TypeA.class.getCanonicalName())) + ";\r\n") + "import ") + (AlphaNodeSharingWithDiffPackageNameTest.TypeB.class.getCanonicalName())) + ";\r\n") + "import ") + (AlphaNodeSharingWithDiffPackageNameTest.TypeC.class.getCanonicalName())) + ";\r\n") + "import ") + (AlphaNodeSharingWithDiffPackageNameTest.TypeD.class.getCanonicalName())) + ";\r\n") + "\r\n") + "rule R3 \r\n") + "when\r\n") + "   $d : TypeD()\r\n") + "   $c : TypeC()\r\n") + "   $b : TypeB(parentId == $c.Id)\r\n") + "   $a : TypeA( parentId == $b.Id,\r\n") + "               alphaNode==\"value contains TypeD TypeE data type\", \r\n") + "               firings not contains \"R3 Fired\")\r\n") + "then\r\n") + "   $a.getFirings().add(\"R3 Fired\");\r\n") + "   update($a);\r\n") + "end;";

    private static final String rule4 = (((((((((((((((((((((((("package com.test.rule4;\r\n" + ("\r\n" + "import ")) + (AlphaNodeSharingWithDiffPackageNameTest.TypeA.class.getCanonicalName())) + ";\r\n") + "import ") + (AlphaNodeSharingWithDiffPackageNameTest.TypeB.class.getCanonicalName())) + ";\r\n") + "import ") + (AlphaNodeSharingWithDiffPackageNameTest.TypeC.class.getCanonicalName())) + ";\r\n") + "import ") + (AlphaNodeSharingWithDiffPackageNameTest.TypeE.class.getCanonicalName())) + ";\r\n") + "\r\n") + "rule R4 \r\n") + "when\r\n") + "   $e : TypeE()\r\n") + "   $c : TypeC()\r\n") + "   $b : TypeB(parentId == $c.Id)\r\n") + "   $a : TypeA( parentId == $b.Id,\r\n") + "               alphaNode==\"value contains TypeD TypeE data type\", \r\n") + "               firings not contains \"R4 Fired\")\r\n") + "then\r\n") + "   $a.getFirings().add(\"R4 Fired\");\r\n") + "   update($a);\r\n") + "end;";

    @Test
    public void testAlphaNode() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("alpha-node-sharing-test", kieBaseTestConfiguration, AlphaNodeSharingWithDiffPackageNameTest.rule1, AlphaNodeSharingWithDiffPackageNameTest.rule2, AlphaNodeSharingWithDiffPackageNameTest.rule3, AlphaNodeSharingWithDiffPackageNameTest.rule4);
        final KieSession ksession = kbase.newKieSession();
        try {
            final AlphaNodeSharingWithDiffPackageNameTest.TypeC c = new AlphaNodeSharingWithDiffPackageNameTest.TypeC();
            final AlphaNodeSharingWithDiffPackageNameTest.TypeB b = new AlphaNodeSharingWithDiffPackageNameTest.TypeB();
            final AlphaNodeSharingWithDiffPackageNameTest.TypeA a = new AlphaNodeSharingWithDiffPackageNameTest.TypeA();
            final AlphaNodeSharingWithDiffPackageNameTest.TypeD d = new AlphaNodeSharingWithDiffPackageNameTest.TypeD();
            final AlphaNodeSharingWithDiffPackageNameTest.TypeE e = new AlphaNodeSharingWithDiffPackageNameTest.TypeE();
            ksession.insert(a);
            ksession.insert(b);
            ksession.insert(c);
            ksession.insert(d);
            ksession.insert(e);
            ksession.fireAllRules();
            Assert.assertTrue(a.getFirings().contains("R1 Fired"));
            Assert.assertTrue(a.getFirings().contains("R2 Fired"));
            Assert.assertTrue(a.getFirings().contains("R3 Fired"));
            Assert.assertTrue(a.getFirings().contains("R4 Fired"));
        } finally {
            ksession.dispose();
        }
    }
}

