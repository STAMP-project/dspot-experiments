/**
 * Copyright 2015 Netflix, Inc.
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
package io.reactivex.netty.util;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import rx.Observable;
import rx.exceptions.MissingBackpressureException;
import rx.observers.TestSubscriber;


public class UnicastBufferingSubjectTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final UnicastBufferingSubjectTest.SubjectRule rule = new UnicastBufferingSubjectTest.SubjectRule();

    @Test(timeout = 60000)
    public void testSequentialSubscriptions() throws Exception {
        String[] msgs = new String[]{ "Hello1", "Hello2" };
        rule.subject.onNext(msgs[0]);
        rule.subscribeAndAssertValues(rule.subject.take(1), msgs[0]);
        rule.subject.onNext(msgs[1]);
        rule.subscribeAndAssertValues(rule.subject.take(1), msgs[1]);
    }

    @Test(timeout = 60000)
    public void testConcurrentSubscriptions() throws Exception {
        TestSubscriber<String> sub1 = new TestSubscriber();
        rule.subject.subscribe(sub1);
        sub1.assertNoTerminalEvent();
        TestSubscriber<String> sub2 = new TestSubscriber();
        rule.subject.subscribe(sub2);
        sub2.assertError(IllegalStateException.class);
    }

    @Test(timeout = 60000)
    public void testBufferCompletion() throws Exception {
        rule.subject.onCompleted();
        rule.subscribeAndAssertValues(rule.subject);
    }

    @Test(timeout = 60000)
    public void testBufferError() throws Exception {
        rule.subject.onError(new IllegalStateException());
        TestSubscriber<String> sub = new TestSubscriber();
        rule.subject.subscribe(sub);
        sub.assertTerminalEvent();
        sub.assertError(IllegalStateException.class);
    }

    @Test(timeout = 60000)
    public void testUnsubscribeBeforeDemandComplete() throws Exception {
        String[] msgs = new String[]{ "Hello1", "Hello2" };
        rule.subject.onNext(msgs[0]);
        rule.subject.onNext(msgs[1]);
        rule.subject.onCompleted();
        rule.subscribeAndAssertValues(rule.subject.take(1), msgs[0]);
        TestSubscriber<String> sub = new TestSubscriber(0);
        rule.subject.subscribe(sub);
        sub.assertNoTerminalEvent();
        sub.assertNoValues();
        sub.requestMore(1);
        sub.assertTerminalEvent();
        sub.assertNoErrors();
        sub.assertValue(msgs[1]);
    }

    @Test(timeout = 60000)
    public void testBufferOverflowWithOffer() throws Exception {
        String[] msgs = new String[]{ "Hello1", "Hello2" };
        UnicastBufferingSubject<String> subject = UnicastBufferingSubject.create(1);
        subject.onNext(msgs[0]);
        boolean offered = subject.offerNext(msgs[1]);
        Assert.assertThat("Offered passed when over capacity", offered, is(false));
    }

    @Test(timeout = 60000)
    public void testBufferOverflowWithOnNext() throws Exception {
        expectedException.expectCause(isA(MissingBackpressureException.class));
        String[] msgs = new String[]{ "Hello1", "Hello2" };
        UnicastBufferingSubject<String> subject = UnicastBufferingSubject.create(1);
        subject.onNext(msgs[0]);
        subject.onNext(msgs[1]);
    }

    @Test(timeout = 60000)
    public void testOverflowSubscribeAndThenAccept() throws Exception {
        String[] msgs = new String[]{ "Hello1", "Hello2" };
        UnicastBufferingSubject<String> subject = UnicastBufferingSubject.create(1);
        subject.onNext(msgs[0]);
        boolean offered = subject.offerNext(msgs[1]);
        Assert.assertThat("Offered passed when over capacity", offered, is(false));
        TestSubscriber<String> subscriber = new TestSubscriber();
        subject.subscribe(subscriber);
        subscriber.assertNoTerminalEvent();
        subscriber.assertValue(msgs[0]);
        subject.onNext(msgs[1]);
        subject.onCompleted();
        subscriber.assertTerminalEvent();
        subscriber.assertNoErrors();
        subscriber.assertValues(msgs);
    }

    @Test(timeout = 60000)
    public void testErrorPostSubscribe() throws Exception {
        String[] msgs = new String[]{ "Hello1", "Hello2" };
        rule.subject.onNext(msgs[0]);
        TestSubscriber<String> subscriber = new TestSubscriber();
        rule.subject.subscribe(subscriber);
        subscriber.assertNoTerminalEvent();
        subscriber.assertValue(msgs[0]);
        rule.subject.onError(new IllegalStateException());
        subscriber.assertTerminalEvent();
        subscriber.assertError(IllegalStateException.class);
    }

    private static class SubjectRule extends ExternalResource {
        private UnicastBufferingSubject<String> subject;

        @Override
        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    subject = UnicastBufferingSubject.create(Long.MAX_VALUE);
                    base.evaluate();
                }
            };
        }

        public void subscribeAndAssertValues(Observable<String> source, String... values) {
            TestSubscriber<String> sub1 = new TestSubscriber();
            source.subscribe(sub1);
            sub1.assertTerminalEvent();
            sub1.assertNoErrors();
            sub1.assertValues(values);
        }
    }
}

