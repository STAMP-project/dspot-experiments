/**
 * Copyright (c) 2011-2017 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core;


import java.util.Collections;
import org.junit.Test;
import reactor.util.annotation.Nullable;


/**
 *
 *
 * @author Stephane Maldini
 */
public class CoreTest {
    @Test
    public void defaultDisposable() {
        Disposable d = () -> {
        };
        assertThat(d.isDisposed()).isFalse();
    }

    @Test
    public void defaultFuseableQueueSubscription() {
        CoreTest.TestQueueSubscription tqs = new CoreTest.TestQueueSubscription();
        testUnsupported(() -> peek());
        testUnsupported(() -> add(0));
        testUnsupported(() -> addAll(Collections.emptyList()));
        testUnsupported(() -> offer(0));
        testUnsupported(() -> retainAll(Collections.emptyList()));
        testUnsupported(() -> tqs.remove());
        testUnsupported(() -> remove(0));
        testUnsupported(() -> removeAll(Collections.emptyList()));
        testUnsupported(() -> element());
        testUnsupported(() -> contains(0));
        testUnsupported(() -> containsAll(Collections.emptyList()));
        testUnsupported(() -> iterator());
        testUnsupported(() -> tqs.toArray(((Integer[]) (null))));
        testUnsupported(() -> toArray());
        testUnsupported(() -> peek());
    }

    static final class TestQueueSubscription implements Fuseable.QueueSubscription<Integer> {
        @Override
        public int requestFusion(int requestedMode) {
            return 0;
        }

        @Override
        @Nullable
        public Integer poll() {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void clear() {
        }

        @Override
        public void request(long n) {
        }

        @Override
        public void cancel() {
        }
    }
}

