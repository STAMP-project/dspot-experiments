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


import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import org.drools.testcoverage.common.KieSessionTest;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.junit.Test;
import org.kie.api.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SerializableInstantiationTest extends KieSessionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SerializableInstantiationTest.class);

    private static final String DRL = "package org.drools.testcoverage.regression;\n" + ((((((("import org.drools.testcoverage.regression.SerializableInstantiationTest.SerializableWrapper;\n" + "global org.slf4j.Logger LOGGER;\n") + "rule serializable\n") + "    when\n") + "        $holder : SerializableWrapper( original == \"hello\" )\n") + "    then\n") + "//        LOGGER.info(\"Works like a charm!\");\n") + "end\n");

    public SerializableInstantiationTest(final KieBaseTestConfiguration kieBaseTestConfiguration, final KieSessionTestConfiguration kieSessionTestConfiguration) {
        super(kieBaseTestConfiguration, kieSessionTestConfiguration);
    }

    @Test
    public void testSerializableInstantiation() {
        session.setGlobal("LOGGER", SerializableInstantiationTest.LOGGER);
        List<Command<?>> commands = new LinkedList<Command<?>>();
        commands.add(KieUtil.getCommands().newInsert(new SerializableInstantiationTest.SerializableWrapper("hello")));
        commands.add(KieUtil.getCommands().newFireAllRules());
        for (int i = 0; i < 500; i++) {
            session.execute(KieUtil.getCommands().newBatchExecution(commands));
        }
    }

    @SuppressWarnings("serial")
    public static class SerializableWrapper implements Serializable {
        private final Serializable original;

        public SerializableWrapper(Serializable original) {
            this.original = original;
        }

        public Serializable getOriginal() {
            return original;
        }
    }
}

