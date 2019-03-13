/**
 * Copyright 2015 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.web.calltree.span;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author jaehong.kim
 */
public class CallTreeIteratorTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final boolean SYNC = false;

    private static final boolean ASYNC = true;

    private static final long START_TIME = 1430983914531L;

    private static final int ELAPSED = 10;

    private CallTreeFactory factory = new CallTreeFactory();

    @Test
    public void internal00() {
        // current is internal, prev is internal
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 1);
        callStack.add("####", 3, 1, 1);
        callStack.add("#####", 4, 1, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void internal01() {
        // current is internal, prev is internal
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 1);
        callStack.add("####", 3, 1, 1);
        callStack.add("####", 3, 0, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void internal02() {
        // current is internal, prev is internal
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 1);
        callStack.add("####", 3, 1, 1);
        callStack.add("###", 2, 0, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void internal03() {
        // current is internal, prev is internal
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 1);
        callStack.add("####", 3, 1, 1);
        callStack.add("##", 1, 0, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void internal10() {
        // current is internal, prev is remote
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 3);
        callStack.add("RRRR", 3, 1, 1);
        callStack.add("#####", 4, 1, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void internal11() {
        // current is internal, prev is remote
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 2);
        callStack.add("RRRR", 3, 1, 1);
        callStack.add("####", 3, 0, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void internal12() {
        // current is internal, prev is remote
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 2);
        callStack.add("RRRR", 3, 1, 1);
        callStack.add("###", 2, 0, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void internal13() {
        // current is internal, prev is remote
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 2);
        callStack.add("RRRR", 3, 1, 1);
        callStack.add("##", 1, 0, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void internal20() {
        // current is internal, prev is async
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 4);
        callStack.add("AAAA", 3, 2, 1);
        callStack.add("#####", 4, 1, 1);
        callStack.add("####", 3, 4, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void internal21() {
        // current is internal, prev is async
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 4);
        callStack.add("AAAA", 3, 2, 1);
        callStack.add("#####", 4, 1, 1);
        callStack.add("###", 2, 0, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void internal22() {
        // current is internal, prev is async
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 4);
        callStack.add("AAAA", 3, 2, 1);
        callStack.add("#####", 4, 1, 1);
        callStack.add("##", 1, 0, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void remote00() {
        // current is remote, prev is internal
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 6);
        callStack.add("RRRR", 3, 1, 1);// check

        callStack.add("#####", 4, 1, 4);
        callStack.add("RRRRRR", 5, 1, 1);// check

        callStack.add("#######", 6, 1, 2);
        callStack.add("RRRRRRRR", 7, 1, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void remote01() {
        // current is remote, prev is internal
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 2);
        callStack.add("####", 3, 1, 1);
        callStack.add("RRRR", 3, 0, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void remote02() {
        // current is remote, prev is internal
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 2);
        callStack.add("###", 2, 1, 1);
        callStack.add("####", 3, 1, 1);
        callStack.add("RRR", 2, 0, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void remote03() {
        // current is remote, prev is internal
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 2);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 1);
        callStack.add("####", 3, 1, 1);
        callStack.add("RR", 1, 0, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void remote10() {
        // current is remote, prev is async
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 5);
        callStack.add("AAAA", 3, 2, 1);
        callStack.add("#####", 4, 1, 2);
        callStack.add("RRRRRR", 5, 1, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void remote11() {
        // current is remote, prev is async
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 5);
        callStack.add("AAAA", 3, 2, 2);
        callStack.add("#####", 4, 1, 1);
        callStack.add("RRRRR", 4, 0, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void remote12() {
        // current is remote, prev is async
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 5);
        callStack.add("AAAA", 3, 2, 1);
        callStack.add("#####", 4, 1, 1);
        callStack.add("RRRR", 3, 4, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void remote13() {
        // current is remote, prev is async
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 2);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 4);
        callStack.add("AAAA", 3, 2, 1);
        callStack.add("#####", 4, 1, 1);
        callStack.add("RR", 1, 0, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void async00() {
        // current is async, prev is internal
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 1);
        callStack.add("####", 3, 1, 3);
        callStack.add("AAAAA", 4, 2, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void async01() {
        // current is async, prev is internal
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 3);
        callStack.add("####", 3, 1, 1);
        callStack.add("AAAA", 3, 3, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void async02() {
        // current is async, prev is internal
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 3);
        callStack.add("###", 2, 1, 1);
        callStack.add("####", 3, 1, 1);
        callStack.add("AAA", 2, 4, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void async10() {
        // current is async, prev is remote
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 5);
        callStack.add("RRRR", 3, 1, 1);
        callStack.add("#####", 4, 1, 3);
        callStack.add("AAAAAA", 5, 2, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void async11() {
        // current is async, prev is remote
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 6);
        callStack.add("RRRR", 3, 1, 1);
        callStack.add("#####", 4, 1, 3);
        callStack.add("######", 5, 1, 1);
        callStack.add("AAAAAA", 5, 3, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void async12() {
        // current is async, prev is remote
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 4);
        callStack.add("RRRR", 3, 1, 1);
        callStack.add("AAAA", 3, 3, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void async13() {
        // current is async, prev is remote
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 3);
        callStack.add("###", 2, 1, 2);
        callStack.add("RRRR", 3, 1, 1);
        callStack.add("AAA", 2, 4, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void async20() {
        // current is async, prev is async
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 5);
        callStack.add("AAAA", 3, 2, 3);
        callStack.add("AAAAA", 4, 2, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void async21() {
        // current is async, prev is async
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 1);
        callStack.add("###", 2, 1, 3);
        callStack.add("AAAA", 3, 2, 1);
        callStack.add("###", 2, 0, 3);
        callStack.add("AAAA", 3, 2, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    @Test
    public void async22() {
        // current is async, prev is async
        CallTreeIteratorTest.CallStackDummy callStack = new CallTreeIteratorTest.CallStackDummy();
        callStack.add("R", 0, 0, 1);
        callStack.add("##", 1, 1, 3);
        callStack.add("###", 2, 1, 3);
        callStack.add("AAAA", 3, 2, 1);
        callStack.add("AAA", 2, 5, 1);// check

        CallTree callTree = factory.get(callStack.getEvents());
        assertCallTree(callTree, callStack.getStackEvents(), true);
    }

    class CallStackDummy {
        final List<CallTreeIteratorTest.StackEvent> stackEvents = new ArrayList<>();

        public void add(String event, int depth, int gap, int exec) {
            final CallTreeIteratorTest.StackEvent stackEvent = new CallTreeIteratorTest.StackEvent(event, depth, gap, exec);
            this.stackEvents.add(stackEvent);
        }

        List<String> getEvents() {
            List<String> eventList = new ArrayList<>(stackEvents.size());
            for (CallTreeIteratorTest.StackEvent stackEvent : stackEvents) {
                eventList.add(stackEvent.getEvent());
            }
            return eventList;
        }

        List<CallTreeIteratorTest.StackEvent> getStackEvents() {
            return stackEvents;
        }
    }

    static class StackEvent {
        private final String event;

        private final int depth;

        private final int gap;

        private final int exec;

        public StackEvent(String event, int depth, int gap, int exec) {
            this.event = Objects.requireNonNull(event, "event must not be null");
            this.depth = depth;
            this.gap = gap;
            this.exec = exec;
        }

        public String getEvent() {
            return event;
        }

        public int getDepth() {
            return depth;
        }

        public int getGap() {
            return gap;
        }

        public int getExec() {
            return exec;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof CallTreeIteratorTest.StackEvent))
                return false;

            CallTreeIteratorTest.StackEvent that = ((CallTreeIteratorTest.StackEvent) (o));
            if ((depth) != (that.depth))
                return false;

            if ((gap) != (that.gap))
                return false;

            if ((exec) != (that.exec))
                return false;

            return (event) != null ? event.equals(that.event) : (that.event) == null;
        }

        @Override
        public int hashCode() {
            int result = ((event) != null) ? event.hashCode() : 0;
            result = (31 * result) + (depth);
            result = (31 * result) + (gap);
            result = (31 * result) + (exec);
            return result;
        }

        @Override
        public String toString() {
            return ((((((((("StackEvent{" + "event='") + (event)) + '\'') + ", depth=") + (depth)) + ", gap=") + (gap)) + ", exec=") + (exec)) + '}';
        }
    }
}

