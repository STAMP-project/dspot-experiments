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


import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;


@Ignore
public class WorkingMemoryActionsSerializationTest {
    private static final List<String> RULES = Arrays.asList("enableRecording", "saveRecord", "processEvent", "ignoreEvent");// rules expected to be executed


    private KieSession ksession;

    private KieBase kbase;

    private final Map<String, Integer> ruleCalls = new HashMap<String, Integer>();

    // private final String accessor = "getEventName()";
    private final String accessor = "eventName";

    private final String drl = ((((((((((((((((((((((((((((((((((((((((((("package apackage\n" + ((" \n" + "import org.drools.core.spi.KnowledgeHelper\n") + "import ")) + (WorkingMemoryActionsSerializationTest.AnEvent.class.getCanonicalName())) + "\n") + " \n") + "declare DoRecord end\n") + "\n") + "rule \"enableRecording\"\n") + "  salience 100\n") + "when\n") + "  AnEvent() from entry-point \"game stream\"\n") + "then\n") + "  drools.getEntryPoint(\"internal stream\").insert(new DoRecord());\n") + "end\n") + "\n") + "rule \"saveRecord\"\n") + "  salience -100\n") + "when\n") + "  $event : DoRecord() from entry-point \"internal stream\"\n") + "then\n") + "  retract($event);\n") + "  //save record\n") + "end\n") + " \n") + "rule \"ignoreEvent\"\n") + "  salience 40\n") + "when\n") + "  $discardCardEvent2 : AnEvent(") + (accessor)) + " == \"discardCardIrr\") from entry-point \"game stream\"\n") + "then\n") + "  retract($discardCardEvent2);\n") + "  //This rule is intended to remove the event and ignore it\n") + "  //ignore this message\n") + "end\n") + "\n") + "rule \"processEvent\"\n") + "when\n") + "  $discardCardEvent : AnEvent(") + (accessor)) + " == \"discardCard\") from entry-point \"game stream\"\n") + "then\n") + "  retract($discardCardEvent);\n") + "  //side effects go here\n") + "end";

    @Test
    public void testMultipleFires() {
        playAnEvent("discardCard");
        checkExecutions(WorkingMemoryActionsSerializationTest.RULES, Arrays.asList(1, 1, 1, 0));
        System.out.println("first played");
        playAnEvent("discardCard");
        checkExecutions(WorkingMemoryActionsSerializationTest.RULES, Arrays.asList(2, 2, 2, 0));
        System.out.println("second played");
        playAnEvent("discardCardIrr");
        checkExecutions(WorkingMemoryActionsSerializationTest.RULES, Arrays.asList(3, 3, 2, 1));
        System.out.println("third played");
        playAnEvent("discardCardIrr");
        checkExecutions(WorkingMemoryActionsSerializationTest.RULES, Arrays.asList(4, 4, 2, 2));
        System.out.println("fourth played");
    }

    public static class AnEvent implements Serializable {
        private String eventName;

        public AnEvent() {
        }

        public AnEvent(String eventName) {
            this.eventName = eventName;
        }

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }
    }
}

