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
import org.drools.compiler.StockTick;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.runtime.KieSession;


/**
 * Verifies that serialization and de-serialization of a composite trigger succeeds (BZ 1142914).
 */
@RunWith(Parameterized.class)
public class DeserializationWithCompositeTriggerTest {
    private static final String DRL = "package org.drools.test;\n" + (((((((((((((("import org.drools.compiler.StockTick;\n" + "global java.util.List list;\n") + "\n") + "declare StockTick\n") + " @role( event )\n") + " @expires( 1s )\n") + "end\n") + "\n") + "rule \"One\"\n") + "when\n") + " $event : StockTick( )\n") + " not StockTick( company == \"BBB\", this after[0,96h] $event )\n") + " not StockTick( company == \"CCC\", this after[0,96h] $event )\n") + "then\n") + "end\n");

    private KieSession ksession;

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public DeserializationWithCompositeTriggerTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    /**
     * Verifies that serialization of a rule with composite trigger does not fail on
     * org.drools.core.time.impl.CompositeMaxDurationTrigger class serialization.
     */
    @Test
    public void testSerializationAndDeserialization() throws Exception {
        this.ksession.insert(new StockTick(2, "AAA", 1.0, 0));
        this.ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true, false);
        Assertions.assertThat(this.ksession).isNotNull();
    }
}

