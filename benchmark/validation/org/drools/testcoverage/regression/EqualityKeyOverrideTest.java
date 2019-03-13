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


import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.KieSessionTest;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;


public class EqualityKeyOverrideTest extends KieSessionTest {
    private static final int ANY_NUMBER = 42;

    private static final String DRL = "package org.drools.testcoverage.regression;\n" + ((((((((((((((("declare Superclass\n" + "end\n") + "declare Subclass extends Superclass\n") + "end\n") + "rule insertSubclass\n") + "    when\n") + "        String()\n") + "    then\n") + "        insertLogical(new Subclass());\n") + "end\n") + "rule insertSuperclass\n") + "    when\n") + "        Integer()\n") + "    then\n") + "        insertLogical(new Superclass());\n") + "end\n");

    public EqualityKeyOverrideTest(final KieBaseTestConfiguration kieBaseTestConfiguration, final KieSessionTestConfiguration kieSessionTestConfiguration) {
        super(kieBaseTestConfiguration, kieSessionTestConfiguration);
    }

    @Test
    public void testEqualityOverride() {
        KieSession ksession = session.getStateful();
        FactHandle string = ksession.insert("testString");
        ksession.fireAllRules();
        Assertions.assertThat(ksession.getObjects().size()).isEqualTo(2);
        ksession.insert(EqualityKeyOverrideTest.ANY_NUMBER);
        ksession.fireAllRules();
        Assertions.assertThat(ksession.getObjects().size()).isEqualTo(4);
        ksession.delete(string);
        ksession.fireAllRules();
        Assertions.assertThat(ksession.getObjects().size()).isEqualTo(2);
    }
}

