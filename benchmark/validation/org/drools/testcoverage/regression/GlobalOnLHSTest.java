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


import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.KieSessionTest;
import org.drools.testcoverage.common.model.Message;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GlobalOnLHSTest extends KieSessionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalOnLHSTest.class);

    private static final String DRL_FILE = "bz1019473.drl";

    public GlobalOnLHSTest(final KieBaseTestConfiguration kieBaseTestConfiguration, final KieSessionTestConfiguration kieSessionTestConfiguration) {
        super(kieBaseTestConfiguration, kieSessionTestConfiguration);
    }

    @Test
    public void testNPEOnMutableGlobal() throws Exception {
        KieSession ksession = session.getStateful();
        List<String> context = new ArrayList<String>();
        ksession.setGlobal("context", context);
        ksession.setGlobal("LOGGER", GlobalOnLHSTest.LOGGER);
        FactHandle b = ksession.insert(new Message("b"));
        ksession.delete(b);
        int fired = ksession.fireAllRules(1);
        Assertions.assertThat(fired).isEqualTo(0);
        ksession.dispose();
    }
}

