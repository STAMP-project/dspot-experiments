/**
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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
package org.drools.compiler.integrationtests.waltz;


import org.drools.compiler.CommonTestMethodBase;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;


/**
 * This is a sample file to launch a rule package from a rule source file.
 */
public abstract class Waltz extends CommonTestMethodBase {
    @Test(timeout = 20000)
    public void testWaltz() {
        try {
            // load up the rulebase
            final KieBase kBase = readKnowledegBase();
            for (int i = 0; i < 50; i++) {
                KieSession kSession = kBase.newKieSession();
                // workingMemory.setGlobal( "sysout",
                // System.out );
                // DebugWorkingMemoryEventListener wmListener = new DebugWorkingMemoryEventListener();
                // DebugAgendaEventListener agendaListener = new DebugAgendaEventListener();
                // workingMemory.addEventListener( wmListener );
                // workingMemory.addEventListener( agendaListener );
                // go !
                this.loadLines(kSession, "waltz50.dat");
                // final Stage stage = new Stage( Stage.START );
                // workingMemory.assertObject( stage );
                final long start = System.currentTimeMillis();
                final Stage stage = new Stage(Stage.DUPLICATE);
                kSession.insert(stage);
                kSession.fireAllRules();
                kSession.dispose();
                final long end = System.currentTimeMillis();
                System.out.println((end - start));
            }
        } catch (final Throwable t) {
            t.printStackTrace();
            Assert.fail(t.getMessage());
        }
    }
}

