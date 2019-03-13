/**
 * Copyright 2016 Square Inc.
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


import History.Builder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// Necessary for functional SparseArray
// 
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class HistoryTest {
    private static final TestKey ABLE = new TestKey("able");

    private static final TestKey BAKER = new TestKey("baker");

    private static final TestKey CHARLIE = new TestKey("charlie");

    @Test
    public void builderCanPushPeekAndPopObjects() {
        History.Builder builder = History.emptyBuilder();
        List<TestKey> objects = Arrays.asList(HistoryTest.ABLE, HistoryTest.BAKER, HistoryTest.CHARLIE);
        for (Object object : objects) {
            builder.push(object);
        }
        for (int i = (objects.size()) - 1; i >= 0; i--) {
            Object object = objects.get(i);
            assertThat(builder.peek()).isSameAs(object);
            assertThat(builder.pop()).isSameAs(object);
        }
    }

    @Test
    public void builderCanPopTo() {
        History.Builder builder = History.emptyBuilder();
        builder.push(HistoryTest.ABLE);
        builder.push(HistoryTest.BAKER);
        builder.push(HistoryTest.CHARLIE);
        builder.popTo(HistoryTest.ABLE);
        assertThat(builder.peek()).isSameAs(HistoryTest.ABLE);
    }

    @Test
    public void builderPopToExplodesOnMissingState() {
        History.Builder builder = History.emptyBuilder();
        builder.push(HistoryTest.ABLE);
        builder.push(HistoryTest.BAKER);
        builder.push(HistoryTest.CHARLIE);
        try {
            builder.popTo(new Object());
            fail("Missing state object, should have thrown");
        } catch (IllegalArgumentException ignored) {
            // Correct!
        }
    }

    @Test
    public void builderCanPopCount() {
        History.Builder builder = History.emptyBuilder();
        builder.push(HistoryTest.ABLE);
        builder.push(HistoryTest.BAKER);
        builder.push(HistoryTest.CHARLIE);
        builder.pop(1);
        assertThat(builder.peek()).isSameAs(HistoryTest.BAKER);
        builder.pop(2);
        assertThat(builder.isEmpty());
    }

    @Test
    public void builderPopExplodesIfCountIsTooLarge() {
        History.Builder builder = History.emptyBuilder();
        builder.push(HistoryTest.ABLE);
        builder.push(HistoryTest.BAKER);
        builder.push(HistoryTest.CHARLIE);
        try {
            builder.pop(4);
            fail("Count is too large, should have thrown");
        } catch (IllegalArgumentException ignored) {
            // Success!
        }
    }

    @Test
    public void forwardIterator() {
        List<Object> paths = new ArrayList<>(Arrays.<Object>asList(HistoryTest.ABLE, HistoryTest.BAKER, HistoryTest.CHARLIE));
        History history = History.emptyBuilder().pushAll(paths).build();
        for (Object o : history) {
            assertThat(o).isSameAs(paths.remove(((paths.size()) - 1)));
        }
    }

    @Test
    public void framesFromBottom() {
        List<Object> paths = new ArrayList<>(Arrays.<Object>asList(HistoryTest.ABLE, HistoryTest.BAKER, HistoryTest.CHARLIE));
        History history = History.emptyBuilder().pushAll(paths).build();
        Iterator<Object> iterator = history.framesFromBottom().iterator();
        assertThat(iterator.next()).isSameAs(HistoryTest.ABLE);
        assertThat(iterator.next()).isSameAs(HistoryTest.BAKER);
        assertThat(iterator.next()).isSameAs(HistoryTest.CHARLIE);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    public void reverseIterator() {
        List<Object> paths = new ArrayList<>(Arrays.<Object>asList(HistoryTest.ABLE, HistoryTest.BAKER, HistoryTest.CHARLIE));
        History history = History.emptyBuilder().pushAll(paths).build();
        Iterator<Object> i = history.reverseIterator();
        while (i.hasNext()) {
            assertThat(i.next()).isSameAs(paths.remove(0));
        } 
    }

    @Test
    public void framesFromTop() {
        List<Object> paths = new ArrayList<>(Arrays.<Object>asList(HistoryTest.ABLE, HistoryTest.BAKER, HistoryTest.CHARLIE));
        History history = History.emptyBuilder().pushAll(paths).build();
        Iterator<Object> iterator = history.framesFromTop().iterator();
        assertThat(iterator.next()).isSameAs(HistoryTest.CHARLIE);
        assertThat(iterator.next()).isSameAs(HistoryTest.BAKER);
        assertThat(iterator.next()).isSameAs(HistoryTest.ABLE);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    public void emptyBuilderPeekIsNullable() {
        assertThat(History.emptyBuilder().peek()).isNull();
    }

    @Test
    public void emptyBuilderPopThrows() {
        try {
            History.emptyBuilder().pop();
            fail("Should throw");
        } catch (IllegalStateException e) {
            // pass
        }
    }

    @Test
    public void isEmpty() {
        final History.Builder builder = History.emptyBuilder();
        assertThat(builder.isEmpty()).isTrue();
        builder.push("foo");
        assertThat(builder.isEmpty()).isFalse();
        builder.pop();
        assertThat(builder.isEmpty()).isTrue();
    }

    @Test
    public void historyIndexAccess() {
        History history = History.emptyBuilder().pushAll(Arrays.asList(HistoryTest.ABLE, HistoryTest.BAKER, HistoryTest.CHARLIE)).build();
        assertThat(history.peek(0)).isEqualTo(HistoryTest.CHARLIE);
        assertThat(history.peek(1)).isEqualTo(HistoryTest.BAKER);
        assertThat(history.peek(2)).isEqualTo(HistoryTest.ABLE);
    }

    @Test
    public void historyIsIsolatedFromItsBuilder() {
        History.Builder builder = History.emptyBuilder().pushAll(Arrays.asList(HistoryTest.ABLE, HistoryTest.BAKER, HistoryTest.CHARLIE));
        History history = builder.build();
        builder.pop();
        assertThat(history.peek(0)).isEqualTo(HistoryTest.CHARLIE);
    }
}

