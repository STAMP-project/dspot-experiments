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


import java.util.List;
import org.drools.compiler.integrationtests.facts.AnEnum;
import org.drools.compiler.integrationtests.facts.ChildFact4WithFirings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class JoinsConcurrentSessionsTest extends AbstractConcurrentTest {
    private static final Integer NUMBER_OF_THREADS = 10;

    private static final Integer NUMBER_OF_REPETITIONS = 1;

    public JoinsConcurrentSessionsTest(final boolean enforcedJitting, final boolean serializeKieBase, final boolean sharedKieBase) {
        super(enforcedJitting, serializeKieBase, sharedKieBase, false);
    }

    @Test(timeout = 20000)
    public void test5() throws InterruptedException {
        final String drlTemplate = " import org.drools.compiler.integrationtests.facts.*;\n" + ((((((((((((((((((" rule \"${ruleName}\"\n" + " dialect \"java\"\n") + " when\n") + "     $rootFact : RootFact( )\n") + "     $childFact1 : ChildFact1( parentId == $rootFact.id )\n") + "     $childFact2 : ChildFact2( parentId == $childFact1.id )\n") + "     $childFact3 : ChildFact3WithEnum( \n") + "         parentId == $childFact2.id, \n") + "         enumValue == ${enumValue}, \n") + "         $enumValue : enumValue )\n") + "     $childFact4 : ChildFact4WithFirings( \n") + "         parentId == $childFact1.id, \n") + "         $evaluationName : evaluationName, \n") + "         firings not contains \"${ruleName}\" )\n") + " then\n") + "     $childFact4.setEvaluationName(String.valueOf($enumValue));\n") + "     $childFact4.getFirings().add(\"${ruleName}\");\n") + "     update($childFact4);\n") + " end\n");
        final String drl1 = drlTemplate.replace("${ruleName}", "R1").replace("${enumValue}", "AnEnum.FIRST");
        final String drl2 = drlTemplate.replace("${ruleName}", "R2").replace("${enumValue}", "AnEnum.SECOND");
        parallelTest(JoinsConcurrentSessionsTest.NUMBER_OF_REPETITIONS, JoinsConcurrentSessionsTest.NUMBER_OF_THREADS, ( kieSession, counter) -> {
            final List<Object> facts = getFacts();
            for (final Object fact : facts) {
                kieSession.insert(fact);
            }
            kieSession.fireAllRules();
            for (final Object fact : facts) {
                if (fact instanceof ChildFact4WithFirings) {
                    final ChildFact4WithFirings childFact4 = ((ChildFact4WithFirings) (fact));
                    if ((childFact4.getFirings().size()) != 1) {
                        return false;
                    } else
                        if ((childFact4.getFirings().get(0).equals("R1")) && (!(childFact4.getEvaluationName().equals(String.valueOf(AnEnum.FIRST))))) {
                            return false;
                        } else
                            if ((childFact4.getFirings().get(0).equals("R2")) && (!(childFact4.getEvaluationName().equals(String.valueOf(AnEnum.SECOND))))) {
                                return false;
                            }


                }
            }
            return true;
        }, null, null, drl1, drl2);
    }
}

