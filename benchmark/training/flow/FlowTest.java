/**
 * Copyright 2013 Square Inc.
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
package flow;


import Direction.BACKWARD;
import Direction.FORWARD;
import Direction.REPLACE;
import History.Builder;
import android.content.Context;
import android.support.annotation.NonNull;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class FlowTest {
    static class Uno {}

    static class Dos {}

    static class Tres {}

    @NotPersistent
    static class NoPersist extends TestKey {
        NoPersist() {
            super("NoPersist");
        }
    }

    final TestKey able = new TestKey("Able");

    final TestKey baker = new TestKey("Baker");

    final TestKey charlie = new TestKey("Charlie");

    final TestKey delta = new TestKey("Delta");

    final TestKey noPersist = new FlowTest.NoPersist();

    @Mock
    KeyManager keyManager;

    History lastStack;

    Direction lastDirection;

    class FlowDispatcher implements Dispatcher {
        @Override
        public void dispatch(@NonNull
        Traversal traversal, @NonNull
        TraversalCallback callback) {
            lastStack = traversal.destination;
            lastDirection = traversal.direction;
            callback.onTraversalCompleted();
        }
    }

    class AsyncDispatcher implements Dispatcher {
        Traversal traversal;

        TraversalCallback callback;

        @Override
        public void dispatch(@NonNull
        Traversal traversal, @NonNull
        TraversalCallback callback) {
            this.traversal = traversal;
            this.callback = callback;
        }

        void fire() {
            TraversalCallback oldCallback = callback;
            callback = null;
            traversal = null;
            oldCallback.onTraversalCompleted();
        }

        void assertIdle() {
            assertThat(callback).isNull();
            assertThat(traversal).isNull();
        }

        void assertDispatching(Object newTop) {
            assertThat(callback).isNotNull();
            assertThat(traversal.destination.top()).isEqualTo(newTop);
        }
    }

    @Test
    public void oneTwoThree() {
        History history = History.single(new FlowTest.Uno());
        Flow flow = new Flow(keyManager, history);
        flow.setDispatcher(new FlowTest.FlowDispatcher());
        flow.set(new FlowTest.Dos());
        assertThat(lastStack.top()).isInstanceOf(FlowTest.Dos.class);
        assertThat(lastDirection).isSameAs(FORWARD);
        flow.set(new FlowTest.Tres());
        assertThat(lastStack.top()).isInstanceOf(FlowTest.Tres.class);
        assertThat(lastDirection).isSameAs(FORWARD);
        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isInstanceOf(FlowTest.Dos.class);
        assertThat(lastDirection).isSameAs(BACKWARD);
        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isInstanceOf(FlowTest.Uno.class);
        assertThat(lastDirection).isSameAs(BACKWARD);
        assertThat(flow.goBack()).isFalse();
    }

    @Test
    public void historyChangesAfterListenerCall() {
        final History firstHistory = History.single(new FlowTest.Uno());
        class Ourrobouros implements Dispatcher {
            Flow flow = new Flow(keyManager, firstHistory);

            {
                flow.setDispatcher(this);
            }

            @Override
            public void dispatch(@NonNull
            Traversal traversal, @NonNull
            TraversalCallback onComplete) {
                assertThat(firstHistory).hasSameSizeAs(flow.getHistory());
                Iterator<Object> original = firstHistory.framesFromTop().iterator();
                for (Object o : flow.getHistory().framesFromTop()) {
                    assertThat(o).isEqualTo(original.next());
                }
                onComplete.onTraversalCompleted();
            }
        }
        Ourrobouros listener = new Ourrobouros();
        listener.flow.set(new FlowTest.Dos());
    }

    @Test
    public void historyPushAllIsPushy() {
        History history = History.emptyBuilder().pushAll(Arrays.<Object>asList(able, baker, charlie)).build();
        assertThat(history.size()).isEqualTo(3);
        Flow flow = new Flow(keyManager, history);
        flow.setDispatcher(new FlowTest.FlowDispatcher());
        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isEqualTo(baker);
        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isEqualTo(able);
        assertThat(flow.goBack()).isFalse();
    }

    @Test
    public void setHistoryWorks() {
        History history = History.emptyBuilder().pushAll(Arrays.<Object>asList(able, baker)).build();
        Flow flow = new Flow(keyManager, history);
        FlowTest.FlowDispatcher dispatcher = new FlowTest.FlowDispatcher();
        flow.setDispatcher(dispatcher);
        History newHistory = History.emptyBuilder().pushAll(Arrays.<Object>asList(charlie, delta)).build();
        flow.setHistory(newHistory, FORWARD);
        assertThat(lastDirection).isSameAs(FORWARD);
        assertThat(lastStack.top()).isSameAs(delta);
        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isSameAs(charlie);
        assertThat(flow.goBack()).isFalse();
    }

    @Test
    public void setObjectGoesBack() {
        History history = History.emptyBuilder().pushAll(Arrays.<Object>asList(able, baker, charlie, delta)).build();
        Flow flow = new Flow(keyManager, history);
        flow.setDispatcher(new FlowTest.FlowDispatcher());
        assertThat(history.size()).isEqualTo(4);
        flow.set(charlie);
        assertThat(lastStack.top()).isEqualTo(charlie);
        assertThat(lastStack.size()).isEqualTo(3);
        assertThat(lastDirection).isEqualTo(BACKWARD);
        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isEqualTo(baker);
        assertThat(lastDirection).isEqualTo(BACKWARD);
        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isEqualTo(able);
        assertThat(lastDirection).isEqualTo(BACKWARD);
        assertThat(flow.goBack()).isFalse();
    }

    @Test
    public void setObjectToMissingObjectPushes() {
        History history = History.emptyBuilder().pushAll(Arrays.<Object>asList(able, baker)).build();
        Flow flow = new Flow(keyManager, history);
        flow.setDispatcher(new FlowTest.FlowDispatcher());
        assertThat(history.size()).isEqualTo(2);
        flow.set(charlie);
        assertThat(lastStack.top()).isEqualTo(charlie);
        assertThat(lastStack.size()).isEqualTo(3);
        assertThat(lastDirection).isEqualTo(FORWARD);
        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isEqualTo(baker);
        assertThat(lastDirection).isEqualTo(BACKWARD);
        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isEqualTo(able);
        assertThat(lastDirection).isEqualTo(BACKWARD);
        assertThat(flow.goBack()).isFalse();
    }

    @Test
    public void setObjectKeepsOriginal() {
        History history = History.emptyBuilder().pushAll(Arrays.<Object>asList(able, baker)).build();
        Flow flow = new Flow(keyManager, history);
        flow.setDispatcher(new FlowTest.FlowDispatcher());
        assertThat(history.size()).isEqualTo(2);
        flow.set(new TestKey("Able"));
        assertThat(lastStack.top()).isEqualTo(new TestKey("Able"));
        assertThat(((lastStack.top()) == (able))).isTrue();
        assertThat(lastStack.top()).isSameAs(able);
        assertThat(lastStack.size()).isEqualTo(1);
        assertThat(lastDirection).isEqualTo(BACKWARD);
    }

    @Test
    public void replaceHistoryResultsInLengthOneHistory() {
        History history = History.emptyBuilder().pushAll(Arrays.<Object>asList(able, baker, charlie)).build();
        Flow flow = new Flow(keyManager, history);
        flow.setDispatcher(new FlowTest.FlowDispatcher());
        assertThat(history.size()).isEqualTo(3);
        flow.replaceHistory(delta, REPLACE);
        assertThat(lastStack.top()).isEqualTo(new TestKey("Delta"));
        assertThat(((lastStack.top()) == (delta))).isTrue();
        assertThat(lastStack.top()).isSameAs(delta);
        assertThat(lastStack.size()).isEqualTo(1);
        assertThat(lastDirection).isEqualTo(REPLACE);
    }

    @Test
    public void replaceTopDoesNotAlterHistoryLength() {
        History history = History.emptyBuilder().pushAll(Arrays.<Object>asList(able, baker, charlie)).build();
        Flow flow = new Flow(keyManager, history);
        flow.setDispatcher(new FlowTest.FlowDispatcher());
        assertThat(history.size()).isEqualTo(3);
        flow.replaceTop(delta, REPLACE);
        assertThat(lastStack.top()).isEqualTo(new TestKey("Delta"));
        assertThat(((lastStack.top()) == (delta))).isTrue();
        assertThat(lastStack.top()).isSameAs(delta);
        assertThat(lastStack.size()).isEqualTo(3);
        assertThat(lastDirection).isEqualTo(REPLACE);
    }

    @Test
    public void secondDispatcherIsBootstrapped() {
        FlowTest.AsyncDispatcher firstDispatcher = new FlowTest.AsyncDispatcher();
        History history = History.single(able);
        Flow flow = new Flow(keyManager, history);
        flow.setDispatcher(firstDispatcher);
        // Quick check that we bootstrapped (and test the test dispatcher).
        firstDispatcher.assertDispatching(able);
        firstDispatcher.fire();
        firstDispatcher.assertIdle();
        // No activity, dispatchers change. Maybe pause / resume. Maybe config change.
        flow.removeDispatcher(firstDispatcher);
        FlowTest.AsyncDispatcher secondDispatcher = new FlowTest.AsyncDispatcher();
        flow.setDispatcher(secondDispatcher);
        // New dispatcher is bootstrapped
        secondDispatcher.assertDispatching(able);
        secondDispatcher.fire();
        secondDispatcher.assertIdle();
    }

    @Test
    public void hangingTraversalsSurviveDispatcherChange() {
        FlowTest.AsyncDispatcher firstDispatcher = new FlowTest.AsyncDispatcher();
        History history = History.single(able);
        Flow flow = new Flow(keyManager, history);
        flow.setDispatcher(firstDispatcher);
        firstDispatcher.fire();
        // Start traversal to second screen.
        flow.set(baker);
        firstDispatcher.assertDispatching(baker);
        // Dispatcher is removed before finishing baker--maybe it caused a configuration change.
        flow.removeDispatcher(firstDispatcher);
        // New dispatcher shows up, maybe from new activity after config change.
        FlowTest.AsyncDispatcher secondDispatcher = new FlowTest.AsyncDispatcher();
        flow.setDispatcher(secondDispatcher);
        // New dispatcher is ignored until the in-progress baker traversal is done.
        secondDispatcher.assertIdle();
        // New dispatcher is bootstrapped with baker.
        firstDispatcher.fire();
        secondDispatcher.assertDispatching(baker);
        // Confirm no redundant extra bootstrap traversals enqueued.
        secondDispatcher.fire();
        secondDispatcher.assertIdle();
    }

    @Test
    public void enqueuedTraversalsSurviveDispatcherChange() {
        FlowTest.AsyncDispatcher firstDispatcher = new FlowTest.AsyncDispatcher();
        History history = History.single(able);
        Flow flow = new Flow(keyManager, history);
        flow.setDispatcher(firstDispatcher);
        firstDispatcher.fire();
        // Dispatcher is removed. Maybe we paused.
        flow.removeDispatcher(firstDispatcher);
        // A few traversals are enqueued because software.
        flow.set(baker);
        flow.set(charlie);
        // New dispatcher shows up, we resumed.
        FlowTest.AsyncDispatcher secondDispatcher = new FlowTest.AsyncDispatcher();
        flow.setDispatcher(secondDispatcher);
        // New dispatcher receives baker and charlie traversals and nothing else.
        secondDispatcher.assertDispatching(baker);
        secondDispatcher.fire();
        secondDispatcher.assertDispatching(charlie);
        secondDispatcher.fire();
        secondDispatcher.assertIdle();
    }

    @SuppressWarnings({ "deprecation", "CheckResult" })
    @Test
    public void setHistoryKeepsOriginals() {
        TestKey able = new TestKey("Able");
        TestKey baker = new TestKey("Baker");
        TestKey charlie = new TestKey("Charlie");
        TestKey delta = new TestKey("Delta");
        History history = History.emptyBuilder().pushAll(Arrays.<Object>asList(able, baker, charlie, delta)).build();
        Flow flow = new Flow(keyManager, history);
        flow.setDispatcher(new FlowTest.FlowDispatcher());
        assertThat(history.size()).isEqualTo(4);
        TestKey echo = new TestKey("Echo");
        TestKey foxtrot = new TestKey("Foxtrot");
        History newHistory = History.emptyBuilder().pushAll(Arrays.<Object>asList(able, baker, echo, foxtrot)).build();
        flow.setHistory(newHistory, REPLACE);
        assertThat(lastStack.size()).isEqualTo(4);
        assertThat(lastStack.top()).isEqualTo(foxtrot);
        flow.goBack();
        assertThat(lastStack.size()).isEqualTo(3);
        assertThat(lastStack.top()).isEqualTo(echo);
        flow.goBack();
        assertThat(lastStack.size()).isEqualTo(2);
        assertThat(lastStack.top()).isSameAs(baker);
        flow.goBack();
        assertThat(lastStack.size()).isEqualTo(1);
        assertThat(lastStack.top()).isSameAs(able);
    }

    static class Picky {
        final String value;

        Picky(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            FlowTest.Picky picky = ((FlowTest.Picky) (o));
            return value.equals(picky.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

    @Test
    public void setCallsEquals() {
        History history = History.emptyBuilder().pushAll(Arrays.<Object>asList(new FlowTest.Picky("Able"), new FlowTest.Picky("Baker"), new FlowTest.Picky("Charlie"), new FlowTest.Picky("Delta"))).build();
        Flow flow = new Flow(keyManager, history);
        flow.setDispatcher(new FlowTest.FlowDispatcher());
        assertThat(history.size()).isEqualTo(4);
        flow.set(new FlowTest.Picky("Charlie"));
        assertThat(lastStack.top()).isEqualTo(new FlowTest.Picky("Charlie"));
        assertThat(lastStack.size()).isEqualTo(3);
        assertThat(lastDirection).isEqualTo(BACKWARD);
        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isEqualTo(new FlowTest.Picky("Baker"));
        assertThat(lastDirection).isEqualTo(BACKWARD);
        assertThat(flow.goBack()).isTrue();
        assertThat(lastStack.top()).isEqualTo(new FlowTest.Picky("Able"));
        assertThat(lastDirection).isEqualTo(BACKWARD);
        assertThat(flow.goBack()).isFalse();
    }

    @Test
    public void incorrectFlowGetUsage() {
        Context mockContext = Mockito.mock(Context.class);
        // noinspection WrongConstant
        Mockito.when(mockContext.getSystemService(Mockito.anyString())).thenReturn(null);
        try {
            Flow.get(mockContext);
            fail("Flow was supposed to throw an exception on wrong usage");
        } catch (IllegalStateException ignored) {
            // That's good!
        }
    }

    @Test
    public void defaultHistoryFilter() {
        History history = History.emptyBuilder().pushAll(Arrays.<Object>asList(able, noPersist, charlie)).build();
        Flow flow = new Flow(keyManager, history);
        flow.setDispatcher(new FlowTest.FlowDispatcher());
        List<Object> expected = History.emptyBuilder().pushAll(Arrays.asList(able, charlie)).build().asList();
        assertThat(flow.getFilteredHistory().asList()).isEqualTo(expected);
    }

    @Test
    public void customHistoryFilter() {
        History history = History.emptyBuilder().pushAll(Arrays.<Object>asList(able, noPersist, charlie)).build();
        Flow flow = new Flow(keyManager, history);
        flow.setDispatcher(new FlowTest.FlowDispatcher());
        flow.setHistoryFilter(new HistoryFilter() {
            @NonNull
            @Override
            public History scrubHistory(@NonNull
            History history) {
                History.Builder builder = History.emptyBuilder();
                for (Object key : history.framesFromBottom()) {
                    if (!(key.equals(able))) {
                        builder.push(key);
                    }
                }
                return builder.build();
            }
        });
        List<Object> expected = History.emptyBuilder().pushAll(Arrays.asList(noPersist, charlie)).build().asList();
        assertThat(flow.getFilteredHistory().asList()).isEqualTo(expected);
    }
}

