/**
 * Copyright (c) 2011-2017 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.publisher;


import OperatorDisposables.DISPOSED;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.junit.Test;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.scheduler.Schedulers;
import reactor.test.util.RaceTestUtils;
import reactor.util.context.Context;


public class OperatorDisposablesTest {
    static final AtomicReferenceFieldUpdater<OperatorDisposablesTest.TestDisposable, Disposable> DISPOSABLE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(OperatorDisposablesTest.TestDisposable.class, Disposable.class, "disp");

    private static class TestDisposable implements Runnable {
        volatile Disposable disp;

        public TestDisposable() {
        }

        public TestDisposable(Disposable disp) {
            this.disp = disp;
        }

        @Override
        public void run() {
            // NO-OP by default
        }
    }

    @Test
    public void singletonIsDisposed() {
        assertThat(DISPOSED.isDisposed()).isTrue();
        DISPOSED.dispose();
        assertThat(DISPOSED.isDisposed()).isTrue();
        assertThat(DISPOSED).isNotSameAs(Disposables.disposed());
    }

    @Test
    public void validationNull() {
        Hooks.onErrorDropped(( e) -> assertThat(e).isInstanceOf(.class).hasMessage("next is null"));
        try {
            assertThat(OperatorDisposables.validate(null, null, ( e) -> Operators.onErrorDropped(e, Context.empty()))).isFalse();
        } finally {
            Hooks.resetOnErrorDropped();
        }
    }

    @Test
    public void disposeRace() {
        for (int i = 0; i < 500; i++) {
            OperatorDisposablesTest.TestDisposable r = new OperatorDisposablesTest.TestDisposable() {
                @Override
                public void run() {
                    OperatorDisposables.dispose(OperatorDisposablesTest.DISPOSABLE_UPDATER, this);
                }
            };
            RaceTestUtils.race(r, r, Schedulers.elastic());
        }
    }

    @Test
    public void setReplace() {
        for (int i = 0; i < 500; i++) {
            OperatorDisposablesTest.TestDisposable r = new OperatorDisposablesTest.TestDisposable() {
                @Override
                public void run() {
                    OperatorDisposables.replace(OperatorDisposablesTest.DISPOSABLE_UPDATER, this, Disposables.single());
                }
            };
            RaceTestUtils.race(r, r, Schedulers.elastic());
        }
    }

    @Test
    public void setRace() {
        for (int i = 0; i < 500; i++) {
            OperatorDisposablesTest.TestDisposable r = new OperatorDisposablesTest.TestDisposable() {
                @Override
                public void run() {
                    OperatorDisposables.set(OperatorDisposablesTest.DISPOSABLE_UPDATER, this, Disposables.single());
                }
            };
            RaceTestUtils.race(r, r, Schedulers.elastic());
        }
    }

    @Test
    public void setReplaceNull() {
        OperatorDisposablesTest.TestDisposable r = new OperatorDisposablesTest.TestDisposable();
        OperatorDisposables.dispose(OperatorDisposablesTest.DISPOSABLE_UPDATER, r);
        assertThat(OperatorDisposables.set(OperatorDisposablesTest.DISPOSABLE_UPDATER, r, null)).isFalse();
        assertThat(OperatorDisposables.replace(OperatorDisposablesTest.DISPOSABLE_UPDATER, r, null)).isFalse();
    }

    @Test
    public void dispose() {
        Disposable u = Disposables.single();
        OperatorDisposablesTest.TestDisposable r = new OperatorDisposablesTest.TestDisposable(u);
        OperatorDisposables.dispose(OperatorDisposablesTest.DISPOSABLE_UPDATER, r);
        assertThat(u.isDisposed()).isTrue();
    }

    @Test
    public void trySet() {
        OperatorDisposablesTest.TestDisposable r = new OperatorDisposablesTest.TestDisposable();
        Disposable d1 = Disposables.single();
        assertThat(OperatorDisposables.trySet(OperatorDisposablesTest.DISPOSABLE_UPDATER, r, d1)).isTrue();
        Disposable d2 = Disposables.single();
        assertThat(OperatorDisposables.trySet(OperatorDisposablesTest.DISPOSABLE_UPDATER, r, d2)).isFalse();
        assertThat(d1.isDisposed()).isFalse();
        assertThat(d2.isDisposed()).isFalse();
        OperatorDisposables.dispose(OperatorDisposablesTest.DISPOSABLE_UPDATER, r);
        Disposable d3 = Disposables.single();
        assertThat(OperatorDisposables.trySet(OperatorDisposablesTest.DISPOSABLE_UPDATER, r, d3)).isFalse();
        assertThat(d3.isDisposed()).isTrue();
    }
}

