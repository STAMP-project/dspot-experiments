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


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.impl.InternalKnowledgeBase;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.CommandFactory;


public class DynamicRulesChangesTest extends CommonTestMethodBase {
    private static final int PARALLEL_THREADS = 1;

    private static InternalKnowledgeBase kbase;

    private ExecutorService executor;

    @Test(timeout = 10000)
    public void testConcurrentRuleAdditions() throws Exception {
        parallelExecute(DynamicRulesChangesTest.RulesExecutor.getSolvers());
    }

    @Test(timeout = 10000)
    public void testBatchRuleAdditions() throws Exception {
        parallelExecute(DynamicRulesChangesTest.BatchRulesExecutor.getSolvers());
    }

    public static class RulesExecutor implements Callable<List<String>> {
        public List<String> call() throws Exception {
            final List<String> events = new ArrayList<String>();
            try {
                KieSession ksession = DynamicRulesChangesTest.kbase.newKieSession();
                ksession.setGlobal("events", events);
                // phase 1
                DynamicRulesChangesTest.Room room1 = new DynamicRulesChangesTest.Room("Room 1");
                ksession.insert(room1);
                FactHandle fireFact1 = ksession.insert(new DynamicRulesChangesTest.Fire(room1));
                ksession.fireAllRules();
                Assert.assertEquals(1, events.size());
                // phase 2
                DynamicRulesChangesTest.Sprinkler sprinkler1 = new DynamicRulesChangesTest.Sprinkler(room1);
                ksession.insert(sprinkler1);
                ksession.fireAllRules();
                Assert.assertEquals(2, events.size());
                // phase 3
                ksession.retract(fireFact1);
                ksession.fireAllRules();
            } catch (Exception e) {
                System.err.println(((("Exception in thread " + (Thread.currentThread().getName())) + ": ") + (e.getLocalizedMessage())));
                throw e;
            }
            return events;
        }

        public static Collection<Callable<List<String>>> getSolvers() {
            Collection<Callable<List<String>>> solvers = new ArrayList<Callable<List<String>>>();
            for (int i = 0; i < (DynamicRulesChangesTest.PARALLEL_THREADS); ++i) {
                solvers.add(new DynamicRulesChangesTest.RulesExecutor());
            }
            return solvers;
        }
    }

    public static class BatchRulesExecutor extends CommonTestMethodBase implements Callable<List<String>> {
        public List<String> call() throws Exception {
            final List<String> events = new ArrayList<String>();
            try {
                KieSession ksession = createKnowledgeSession(DynamicRulesChangesTest.kbase);
                ksession.setGlobal("events", events);
                DynamicRulesChangesTest.Room room1 = new DynamicRulesChangesTest.Room("Room 1");
                DynamicRulesChangesTest.Fire fire1 = new DynamicRulesChangesTest.Fire(room1);
                // phase 1
                List<Command> cmds = new ArrayList<Command>();
                cmds.add(CommandFactory.newInsert(room1, "room1"));
                cmds.add(CommandFactory.newInsert(fire1, "fire1"));
                cmds.add(CommandFactory.newFireAllRules());
                ksession.execute(CommandFactory.newBatchExecution(cmds));
                Assert.assertEquals(1, events.size());
                // phase 2
                cmds = new ArrayList<Command>();
                cmds.add(CommandFactory.newInsert(new DynamicRulesChangesTest.Sprinkler(room1), "sprinkler1"));
                cmds.add(CommandFactory.newFireAllRules());
                ksession.execute(CommandFactory.newBatchExecution(cmds));
                Assert.assertEquals(2, events.size());
                // phase 3
                cmds = new ArrayList<Command>();
                cmds.add(CommandFactory.newDelete(ksession.getFactHandle(fire1)));
                cmds.add(CommandFactory.newFireAllRules());
                ksession.execute(CommandFactory.newBatchExecution(cmds));
            } catch (Exception e) {
                System.err.println(((("Exception in thread " + (Thread.currentThread().getName())) + ": ") + (e.getLocalizedMessage())));
                throw e;
            }
            return events;
        }

        public static Collection<Callable<List<String>>> getSolvers() {
            Collection<Callable<List<String>>> solvers = new ArrayList<Callable<List<String>>>();
            for (int i = 0; i < (DynamicRulesChangesTest.PARALLEL_THREADS); ++i) {
                solvers.add(new DynamicRulesChangesTest.BatchRulesExecutor());
            }
            return solvers;
        }
    }

    // Rules
    private static Map<String, String> rules = new HashMap<String, String>() {
        {
            put("raiseAlarm", ((((((((((("import " + (DynamicRulesChangesTest.class.getCanonicalName())) + "\n ") + "global java.util.List events\n") + "rule \"Raise the alarm when we have one or more fires\"\n") + "when\n") + "    exists DynamicRulesChangesTest.Fire()\n") + "then\n") + "    insert( new DynamicRulesChangesTest.Alarm() );\n") + "    events.add( \"Raise the alarm\" );\n") + "    DynamicRulesChangesTest.addRule(\"onFire\", drools.getRule());\n") + "end"));
            put("onFire", (((((((((((("import " + (DynamicRulesChangesTest.class.getCanonicalName())) + "\n ") + "global java.util.List events\n") + "rule \"When there is a fire turn on the sprinkler\"\n") + "when\n") + "    $fire: DynamicRulesChangesTest.Fire($room : room)\n") + "    $sprinkler : DynamicRulesChangesTest.Sprinkler( room == $room, on == false )\n") + "then\n") + "    modify( $sprinkler ) { setOn( true ) };\n") + "    events.add( \"Turn on the sprinkler for room \" + $room.getName() );\n") + "    DynamicRulesChangesTest.addRule(\"fireGone\", drools.getRule());\n") + "end"));
            put("fireGone", ((((((((((((("import " + (DynamicRulesChangesTest.class.getCanonicalName())) + "\n ") + "global java.util.List events\n") + "rule \"When the fire is gone turn off the sprinkler\"\n") + "when\n") + "    $room : DynamicRulesChangesTest.Room( )\n") + "    $sprinkler : DynamicRulesChangesTest.Sprinkler( room == $room, on == true )\n") + "    not DynamicRulesChangesTest.Fire( room == $room )\n") + "then\n") + "    modify( $sprinkler ) { setOn( false ) };\n") + "    events.add( \"Turn off the sprinkler for room \" + $room.getName() );\n") + "    DynamicRulesChangesTest.addRule(\"cancelAlarm\", drools.getRule());\n") + "end"));
            put("cancelAlarm", (((((((((((("import " + (DynamicRulesChangesTest.class.getCanonicalName())) + "\n ") + "global java.util.List events\n") + "rule \"Cancel the alarm when all the fires have gone\"\n") + "when\n") + "    not DynamicRulesChangesTest.Fire()\n") + "    $alarm : DynamicRulesChangesTest.ParentAlarm()\n") + "then\n") + "    retract( $alarm );\n") + "    events.add( \"Cancel the alarm\" );\n") + "    DynamicRulesChangesTest.addRule(\"status\", drools.getRule());\n") + "end"));
            put("status", (((((((((("import " + (DynamicRulesChangesTest.class.getCanonicalName())) + "\n ") + "global java.util.List events\n") + "rule \"Status output when things are ok\"\n") + "when\n") + "    not DynamicRulesChangesTest.Alarm()\n") + "    not DynamicRulesChangesTest.Sprinkler( on == true )\n") + "then\n") + "    events.add( \"Everything is ok\" );\n") + "end"));
        }
    };

    // Model
    public static class ParentAlarm {}

    public static class Alarm extends DynamicRulesChangesTest.ParentAlarm {}

    public static class Fire {
        private DynamicRulesChangesTest.Room room;

        public Fire() {
        }

        public Fire(DynamicRulesChangesTest.Room room) {
            this.room = room;
        }

        public DynamicRulesChangesTest.Room getRoom() {
            return room;
        }

        public void setRoom(DynamicRulesChangesTest.Room room) {
            this.room = room;
        }
    }

    public static class Room {
        private String name;

        public Room() {
        }

        public Room(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DynamicRulesChangesTest.Room))
                return false;

            return name.equals(((DynamicRulesChangesTest.Room) (obj)).getName());
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class Sprinkler {
        private DynamicRulesChangesTest.Room room;

        private boolean on = false;

        public Sprinkler() {
        }

        public Sprinkler(DynamicRulesChangesTest.Room room) {
            this.room = room;
        }

        public DynamicRulesChangesTest.Room getRoom() {
            return room;
        }

        public void setRoom(DynamicRulesChangesTest.Room room) {
            this.room = room;
        }

        public boolean isOn() {
            return on;
        }

        public void setOn(boolean on) {
            this.on = on;
        }

        @Override
        public int hashCode() {
            return room.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DynamicRulesChangesTest.Sprinkler))
                return false;

            return room.equals(((DynamicRulesChangesTest.Sprinkler) (obj)).getRoom());
        }

        @Override
        public String toString() {
            return "Sprinkler for " + (room);
        }
    }
}

