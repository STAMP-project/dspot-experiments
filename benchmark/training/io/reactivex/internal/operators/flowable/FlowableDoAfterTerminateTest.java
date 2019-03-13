/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivex.internal.operators.flowable;


import io.reactivex.TestHelper;
import io.reactivex.functions.Action;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subscribers.TestSubscriber;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


public class FlowableDoAfterTerminateTest {
    private Action aAction0;

    private Subscriber<String> subscriber;

    @Test
    public void testDoAfterTerminateCalledOnComplete() {
        checkActionCalled(Flowable.fromArray("1", "2", "3"));
    }

    @Test
    public void testDoAfterTerminateCalledOnError() {
        checkActionCalled(Flowable.<String>error(new RuntimeException("expected")));
    }

    @Test
    public void nullActionShouldBeCheckedInConstructor() {
        try {
            Flowable.empty().doAfterTerminate(null);
            Assert.fail("Should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals("onAfterTerminate is null", expected.getMessage());
        }
    }

    @Test
    public void nullFinallyActionShouldBeCheckedASAP() {
        try {
            Flowable.just("value").doAfterTerminate(null);
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void ifFinallyActionThrowsExceptionShouldNotBeSwallowedAndActionShouldBeCalledOnce() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Action finallyAction = Mockito.mock(Action.class);
            Mockito.doThrow(new IllegalStateException()).when(finallyAction).run();
            TestSubscriber<String> testSubscriber = new TestSubscriber<String>();
            Flowable.just("value").doAfterTerminate(finallyAction).subscribe(testSubscriber);
            testSubscriber.assertValue("value");
            Mockito.verify(finallyAction).run();
            TestHelper.assertError(errors, 0, IllegalStateException.class);
            // Actual result:
            // Not only IllegalStateException was swallowed
            // But finallyAction was called twice!
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

